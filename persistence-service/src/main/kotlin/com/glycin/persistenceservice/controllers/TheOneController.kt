package com.glycin.persistenceservice.controllers

import com.glycin.persistenceservice.model.AddActionResponse
import com.glycin.persistenceservice.model.Player
import com.glycin.persistenceservice.model.PlayerSession
import com.glycin.persistenceservice.model.Session
import com.glycin.persistenceservice.service.PlayerService
import com.glycin.persistenceservice.service.SessionService
import io.opentelemetry.api.trace.Span
import io.opentelemetry.instrumentation.annotations.WithSpan
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

const val PLAYER_ID_HEADER = "X-Player-ID"
const val PLAYER_ID_SPAN_ATTRIBUTE = "player.id"
const val PLAYER_NAME_SPAN_ATTRIBUTE = "player.name"

private val PLAYER_NAME_REGEX = Regex("""^\w[\w\s\-_]{1,8}\w$""")

@RestController
class TheOneController(
    private val sessionService: SessionService,
    private val playerService: PlayerService,
) {

    @PostMapping("/session/init") // TODO admin endpoint
    fun initNewSession(): ResponseEntity<Session> {
        return ResponseEntity.ok(sessionService.createSession())
    }

    @WithSpan
    @GetMapping("/player/create")
    fun createPlayer(
        @RequestParam name: String,
    ): ResponseEntity<PlayerSession> {
        if (!PLAYER_NAME_REGEX.matches(name)) {
            return ResponseEntity.badRequest().build()
        }

        val player = playerService.createPlayer(name = name)?.alsoAddToSpan()
            ?: return ResponseEntity.status(HttpStatus.CONFLICT).build()

        return sessionService.getActiveSession()?.let {
            sessionService.addPlayerToSession(player, it.id)
            ResponseEntity.ok(it.toPlayerSessionDTO(player))
        } ?: ResponseEntity.notFound().build()
    }

    @WithSpan
    @GetMapping("/session/getLatestState")
    fun getLatestState(
        @RequestHeader(PLAYER_ID_HEADER) playerId: UUID,
    ): ResponseEntity<PlayerSession> {
        val player = playerService.getPlayer(playerId).alsoAddToSpan()
        return sessionService.getActiveSession()
            ?.let { ResponseEntity.ok(it.toPlayerSessionDTO(player)) }
            ?: ResponseEntity.notFound().build()
    }

    @WithSpan
    @GetMapping("/player/getLatestState")
    fun getLatestPlayersState(
        @RequestHeader(PLAYER_ID_HEADER) playerId: UUID,
    ): ResponseEntity<Player> {
        return sessionService.getActiveSession()
            ?.let { session -> session.players.find { it.id == playerId }?.alsoAddToSpan() }
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
    }

    @WithSpan
    @PostMapping("/player/action")
    fun addAction(
        @RequestHeader(PLAYER_ID_HEADER) playerId: UUID,
        @RequestParam actionTime: Long,
    ): ResponseEntity<AddActionResponse> {
        val player = playerService.getPlayer(playerId).alsoAddToSpan()
        playerService.addActionToPlayer(player, actionTime)
        return ResponseEntity.accepted().body(AddActionResponse(
            playerId = player.id,
            playerName = player.name,
            actionTime = actionTime,
        ))
    }

    private fun Session.toPlayerSessionDTO(player: Player) = PlayerSession(
        playerId = player.id,
        playerName = player.name,
        sessionId = id,
        obstacles = obstacles,
    )

    private fun Player.alsoAddToSpan() = also {
        val currentSpan = Span.current()
        currentSpan.setAttribute(PLAYER_ID_SPAN_ATTRIBUTE, id.toString())
        currentSpan.setAttribute(PLAYER_NAME_SPAN_ATTRIBUTE, name)
    }
}
