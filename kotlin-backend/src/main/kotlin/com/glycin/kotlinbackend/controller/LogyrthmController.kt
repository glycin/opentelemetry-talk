package com.glycin.kotlinbackend.controller

import com.glycin.kotlinbackend.connector.PersistenceServiceConnector
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
import java.util.*

const val ROCKER_ID_HEADER = "X-Rocker-ID"
const val ROCKER_ID_SPAN_ATTRIBUTE = "rocker.id"
const val ROCKER_NAME_SPAN_ATTRIBUTE = "rocker.name"

@RestController()
class LogyrthmController(
    private val persistenceConnector: PersistenceServiceConnector,
) {
    private val logger: Logger = LoggerFactory.getLogger(LogyrthmController::class.java)

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
            logger.info("New rocker joined the jam: '$name'")
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