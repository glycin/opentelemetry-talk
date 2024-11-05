package com.glycin.persistenceservice.controllers

import com.glycin.persistenceservice.model.*
import com.glycin.persistenceservice.service.PlayerService
import com.glycin.persistenceservice.service.SessionService
import io.opentelemetry.api.trace.Span
import io.opentelemetry.instrumentation.annotations.WithSpan
import org.slf4j.Logger
import org.slf4j.LoggerFactory
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
    private val logger: Logger = LoggerFactory.getLogger(TheOneController::class.java)

    @PostMapping("/session/init")
    fun initNewSession(): ResponseEntity<Session> {
        return ResponseEntity.ok(sessionService.createSession())
    }

    @WithSpan
    @GetMapping("/player/create")
    fun createPlayer(
        @RequestParam id: UUID,
        @RequestParam name: String,
    ): ResponseEntity<PlayerSession> {
        if (!PLAYER_NAME_REGEX.matches(name)) {
            return ResponseEntity.badRequest().build()
        }

        val player = playerService.createPlayer(id = id, name = name)?.alsoAddToSpan()
            ?: return ResponseEntity.status(HttpStatus.CONFLICT).build()

        logger.info("New player spawned: '$name'")
        return sessionService.getActiveSession()?.let {
            sessionService.addPlayerToSession(player, it.id)
            ResponseEntity.ok(it.toPlayerSessionDTO(player))
        } ?: ResponseEntity.notFound().build()
    }

    @GetMapping("/session/latest")
    fun getLatestSession(): ResponseEntity<Session> {
        return sessionService.getActiveSession()
            ?.let { ResponseEntity.ok(it) }
            ?: ResponseEntity.notFound().build()
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
        @RequestParam actionType: ActionType,
    ): ResponseEntity<AddActionResponse> {
        val player = playerService.getPlayer(playerId).alsoAddToSpan()

        when (actionType) {
            ActionType.TAP ->  logger.info("Player '${player.name}' successfully tapped their screen, well done!")
            ActionType.SCORE -> logger.info("Player '${player.name}' was actually able to dodge a flaming server, point scored!")
            ActionType.DEATH -> logger.info("Player '${player.name}' perished in the flaming servers!!")
        }

        playerService.addActionToPlayer(player, actionTime)
        return ResponseEntity.accepted().body(AddActionResponse(
            playerId = player.id,
            playerName = player.name,
            actionTime = actionTime,
            actionType = actionType.name,
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
