package com.glycin.persistenceservice.repository

import com.glycin.persistenceservice.controllers.PLAYER_ID_SPAN_ATTRIBUTE
import com.glycin.persistenceservice.model.Action
import com.glycin.persistenceservice.model.ActionType
import com.glycin.persistenceservice.model.Player
import io.opentelemetry.api.trace.Span
import io.opentelemetry.instrumentation.annotations.WithSpan
import org.springframework.stereotype.Repository
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.locks.ReentrantLock

@Repository
class PlayerRepository {

    private val players: ConcurrentMap<UUID, Player> = ConcurrentHashMap()
    private val playerNames: MutableSet<String> = hashSetOf()

    private val playerCreationLock = ReentrantLock()

    @WithSpan
    fun create(player: Player): Player? {
        Span.current().setAttribute(PLAYER_ID_SPAN_ATTRIBUTE, player.id.toString())
        playerCreationLock.lock()
        return try {
            if (playerNames.add(player.name)) {
                players[player.id] = player
                player
            } else {
                players[player.id]?.takeIf { it.name == player.name } // Allow reregistration of the same player if the ID matches
            }
        } finally {
            playerCreationLock.unlock()
        }
    }

    @WithSpan
    fun updatePlayerAction(player: Player, action: Action) {
        Span.current().setAttribute(PLAYER_ID_SPAN_ATTRIBUTE, player.id.toString())
        val p = players.getValue(player.id)
        when(action.type) {
            ActionType.TAP -> {} // Do nothing
            ActionType.SCORE -> p.score++
            ActionType.DEATH -> p.score = 0
        }
        p.actions.add(action)
    }

    fun getPlayer(id: UUID): Player = players.getValue(id)
}
