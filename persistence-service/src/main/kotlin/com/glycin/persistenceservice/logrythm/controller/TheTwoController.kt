package com.glycin.persistenceservice.logrythm.controller

import com.glycin.persistenceservice.logrythm.model.*
import com.glycin.persistenceservice.logrythm.services.RockerService
import com.glycin.persistenceservice.logrythm.services.JamSessionService
import io.opentelemetry.api.trace.Span
import io.opentelemetry.instrumentation.annotations.WithSpan
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

const val ROCKER_ID_HEADER = "X-Rocker-ID"
const val ROCKER_ID_SPAN_ATTRIBUTE = "rocker.id"
const val ROCKER_NAME_SPAN_ATTRIBUTE = "rocker.name"

private val ROCKER_NAME_REGEX = Regex("""^\w[\w\s\-_]{1,8}\w$""")

@RestController
class TheTwoController(
    private val rockerService: RockerService,
    private val sessionService: JamSessionService,
) {
    private val logger: Logger = LoggerFactory.getLogger(TheTwoController::class.java)

    @PostMapping("/jam/init")
    fun initNewSession(): ResponseEntity<JamSession> {
        return ResponseEntity.ok(sessionService.createSession())
    }

    @WithSpan
    @GetMapping("/rocker/create")
    fun createRocker(
        @RequestParam id: UUID,
        @RequestParam name: String,
    ): ResponseEntity<RockerSession> {
        if (!ROCKER_NAME_REGEX.matches(name)) {
            return ResponseEntity.badRequest().build()
        }

        val maestro = rockerService.createRocker(id = id, name = name)?.alsoAddToSpan()
            ?: return ResponseEntity.status(HttpStatus.CONFLICT).build()

        logger.info("New rocker joined the jam: '$name'")
        return sessionService.getActiveSession()?.let {
            sessionService.addRockerToJam(maestro, it.id)
            ResponseEntity.ok(it.toRockerSessionDto(maestro))
        } ?: ResponseEntity.notFound().build()
    }

    @GetMapping("/jam/latest")
    fun getLatestSession(): ResponseEntity<JamSession> {
        return sessionService.getActiveSession()
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    }

    @WithSpan
    @GetMapping("/jam/getLatestState")
    fun getLatestState(
        @RequestHeader(ROCKER_ID_HEADER) playerId: UUID,
    ): ResponseEntity<RockerSession> {
        val player = rockerService.getRocker(playerId).alsoAddToSpan()
        return sessionService.getActiveSession()
            ?.let { ResponseEntity.ok(it.toRockerSessionDto(player)) }
            ?: ResponseEntity.notFound().build()
    }

    @WithSpan
    @GetMapping("/rocker/getLatestState")
    fun getLatestPlayersState(
        @RequestHeader(ROCKER_ID_HEADER) playerId: UUID,
    ): ResponseEntity<Rocker> {
        return sessionService.getActiveSession()
            ?.let { session -> session.rockers.find { it.id == playerId }?.alsoAddToSpan() }
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    }

    @WithSpan
    @PostMapping("/rocker/strum")
    fun addAction(
        @RequestHeader(ROCKER_ID_HEADER) rockerId: UUID,
        @RequestParam strumTime: Long,
        @RequestParam chord: PowerChord,
    ): ResponseEntity<StrumResponse> {
        val rocker = rockerService.getRocker(rockerId).alsoAddToSpan()

        logger.info("Rocker '${rocker.name}' successfully tapped their screen, strumming ${chord.name}. Rock on!")
        rockerService.addChordToRocker(rocker, chord, strumTime)
        sessionService.processRockerStrum(rocker, chord)
        return ResponseEntity.accepted().body(
            StrumResponse(
                rockerId = rocker.id,
                rockerName = rocker.name,
                strumTime = strumTime,
                chord = chord.name,
            )
        )
    }

    private fun JamSession.toRockerSessionDto(rocker: Rocker) = RockerSession(
        rockerId = rocker.id,
        rockerName = rocker.name,
        sessionId = this.id
    )

    private fun Rocker.alsoAddToSpan() = also {
        val currentSpan = Span.current()
        currentSpan.setAttribute(ROCKER_ID_SPAN_ATTRIBUTE, id.toString())
        currentSpan.setAttribute(ROCKER_NAME_SPAN_ATTRIBUTE, name)
    }
}