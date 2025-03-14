package com.glycin.persistenceservice.logrhythm.controller

import com.glycin.persistenceservice.logrhythm.model.*
import com.glycin.persistenceservice.logrhythm.services.RockerService
import com.glycin.persistenceservice.logrhythm.services.JamSessionService
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
        logger.info("Creating new jam session!!!")
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

        val rocker = rockerService.createRocker(id = id, name = name)?.alsoAddToSpan()
            ?: return ResponseEntity.status(HttpStatus.CONFLICT).build()

        return sessionService.getActiveSession()?.let {
            sessionService.addRockerToJam(rocker, it.id)
            logger.info("A new rocker appeared! '$name' joined the jam")
            ResponseEntity.ok(it.toRockerSessionDto(rocker))
        } ?: ResponseEntity.notFound().build()
    }

    @GetMapping("/jam/latest")
    fun getLatestSession(): ResponseEntity<JamSession> {
        return sessionService.getActiveSession()
            ?.let { update ->
                logger.info("Return jam session with ${update.rockers.size} rockers and ${update.rockers.flatMap { it.chordsPlayed }.size} chords")
                ResponseEntity.ok(update)
            }
            ?: ResponseEntity.notFound().build()
    }

    @WithSpan
    @GetMapping("/jam/getLatestState")
    fun getLatestState(
        @RequestHeader(ROCKER_ID_HEADER) playerId: UUID,
    ): ResponseEntity<RockerSession> {
        val rocker = rockerService.getRocker(playerId).alsoAddToSpan()
        return sessionService.getActiveSession()
            ?.let { ResponseEntity.ok(it.toRockerSessionDto(rocker)) }
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

        if(chord == PowerChord.NOTE) {
            logger.warn("Snap! Rocker ${rocker.name} strummed a wrong chord maybe...?")
        } else if (chord == PowerChord.EXPLOSION) {
            logger.error("Oh no!!! Rocker ${rocker.name} messed up the jam with a wrong chord!!!")
        }

        rockerService.addChordToRocker(rocker, chord, strumTime)
        logger.info("Rocker '${rocker.name}' successfully rocked their screen, strumming ${chord.toEmoji()}. Rock on!")

        return ResponseEntity.accepted().body(
            StrumResponse(
                rockerId = rocker.id,
                rockerName = rocker.name,
                strumTime = strumTime,
                chord = chord,
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

fun PowerChord.toEmoji(): String {
    return when (this) {
        PowerChord.GUITAR -> "\uD83C\uDFB8" //🎸
        PowerChord.HORNS -> "\uD83E\uDD18" //🤘
        PowerChord.EXPLOSION -> "\uD83D\uDCA5" //💥
        PowerChord.SKULL -> "\uD83D\uDC80" //💀
        PowerChord.TIGER -> "\uD83D\uDC2F" //🐯
        PowerChord.NOTE -> "\uD83C\uDFB5" // 🎵
    }
}