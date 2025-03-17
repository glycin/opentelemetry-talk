package com.glycin.kotlinbackend.controller

import com.glycin.kotlinbackend.connector.PersistenceServiceConnector
import com.glycin.kotlinbackend.model.PowerChord
import com.glycin.kotlinbackend.model.RockerSession
import com.glycin.kotlinbackend.model.rest.RestStrumBody
import feign.FeignException
import io.opentelemetry.api.trace.Span
import io.opentelemetry.instrumentation.annotations.WithSpan
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.time.Instant
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.util.*

const val ROCKER_ID_HEADER = "X-Rocker-ID"
const val ROCKER_ID_SPAN_ATTRIBUTE = "rocker.id"
const val ROCKER_NAME_SPAN_ATTRIBUTE = "rocker.name"

@RestController()
class LogrhythmController(
    private val persistenceConnector: PersistenceServiceConnector,
) {
    private val logger: Logger = LoggerFactory.getLogger(LogrhythmController::class.java)

    @WithSpan
    @GetMapping("/jam/latest")
    fun getLatestJamSession(
        @RequestHeader(ROCKER_ID_HEADER) playerId: UUID,
    ): ResponseEntity<RockerSession> {
        return ResponseEntity.ok(persistenceConnector.getLatestJamState(playerId).alsoAddRockerToSpan())
    }

    @WithSpan
    @PostMapping("/create/rocker")
    fun createRocker(
        @RequestHeader(ROCKER_ID_HEADER, required = false) rockerId: UUID?,
        @RequestParam name: String,
    ): ResponseEntity<Any> {
        return try {
            persistenceConnector.createRocker(rockerId ?: UUID.randomUUID(), name)
        } catch (e: FeignException.Conflict) {
            logger.warn("Attempting to create already existing rocker: '$name'")
            null
        }?.let {
            logger.info("New rocker successfully joined the jam: '$name'")
            ResponseEntity.ok(it.alsoAddRockerToSpan())
        } ?: ResponseEntity.status(HttpStatus.CONFLICT).build()
    }

    @WithSpan
    @PostMapping("/rocker/strum")
    fun submitStrum(
        @RequestHeader(ROCKER_ID_HEADER) rockerID: UUID,
        @RequestBody action: RestStrumBody,
    ): ResponseEntity<Unit> {
        persistenceConnector.postStrum(rockerID, action.timestamp, action.chord).also { response ->
            logger.info("Rocker '${response.rockerName}' is trying to strum chord : ${action.chord.toEmoji()} at ${Instant.ofEpochMilli(action.timestamp).atOffset(ZoneOffset.UTC).toLocalDateTime()}!")
            addToSpan(rockerId = response.rockerId, rockerName = response.rockerName)
        }
        return ResponseEntity.noContent().build()
    }

    private fun RockerSession.alsoAddRockerToSpan() = also {
        addToSpan(rockerId = rockerId, rockerName = rockerName)
    }

    private fun addToSpan(rockerId: UUID, rockerName: String) {
        val currentSpan = Span.current()
        currentSpan.setAttribute(ROCKER_ID_SPAN_ATTRIBUTE, rockerId.toString())
        currentSpan.setAttribute(ROCKER_NAME_SPAN_ATTRIBUTE, rockerName)
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