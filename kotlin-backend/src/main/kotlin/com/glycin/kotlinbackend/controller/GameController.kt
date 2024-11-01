package com.glycin.kotlinbackend.controller

import com.glycin.kotlinbackend.connector.PersistenceServiceConnector
import com.glycin.kotlinbackend.model.Session
import com.glycin.kotlinbackend.model.rest.RestActionBody
import io.opentelemetry.api.trace.Span
import io.opentelemetry.instrumentation.annotations.WithSpan
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

const val PLAYER_ID_HEADER = "X-Player-ID"
const val PLAYER_ID_SPAN_ATTRIBUTE = "player.id"
const val PLAYER_NAME_SPAN_ATTRIBUTE = "player.name"

@RestController
class GameController(
    private val persistenceConnector: PersistenceServiceConnector,
) {

    @WithSpan
    @GetMapping("/latest")
    fun getLatestSession(
        @RequestHeader(PLAYER_ID_HEADER) playerId: UUID,
    ): ResponseEntity<Session> {
        return ResponseEntity.ok(persistenceConnector.getLatestState(playerId).alsoAddPlayerToSpan())
    }

    @WithSpan
    @PostMapping("/create/player")
    fun createPlayer(
        @RequestParam name: String,
    ): ResponseEntity<Any> {
        return persistenceConnector.createPlayer(name)
            ?.let { ResponseEntity.ok(it.alsoAddPlayerToSpan()) }
            ?: ResponseEntity.status(HttpStatus.CONFLICT).build()
    }

    @WithSpan
    @PostMapping("/player/action")
    fun submitAction(
        @RequestHeader(PLAYER_ID_HEADER) playerId: UUID,
        @RequestBody action: RestActionBody,
    ): ResponseEntity<Unit> {
        persistenceConnector.postAction(playerId, action.timestamp).also { response ->
            addToSpan(playerId = response.playerId, playerName = response.playerName)
        }
        return ResponseEntity.noContent().build()
    }

    private fun Session.alsoAddPlayerToSpan() = also {
        addToSpan(playerId = playerId, playerName = playerName)
    }

    private fun addToSpan(playerId: UUID, playerName: String) {
        val currentSpan = Span.current()
        currentSpan.setAttribute(PLAYER_ID_SPAN_ATTRIBUTE, playerId.toString())
        currentSpan.setAttribute(PLAYER_NAME_SPAN_ATTRIBUTE, playerName)
    }
}
