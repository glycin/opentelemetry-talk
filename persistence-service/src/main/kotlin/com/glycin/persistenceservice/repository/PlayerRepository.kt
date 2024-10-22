package com.glycin.persistenceservice.repository

import com.glycin.persistenceservice.model.Action
import com.glycin.persistenceservice.model.Player
import org.springframework.stereotype.Repository
import java.util.*
import kotlin.collections.HashMap

@Repository
class PlayerRepository {

    private val players: MutableMap<UUID, Player> = HashMap()

    fun save(player: Player) {
        players[player.id] = player
    }

    fun updatePlayerAction(player: Player, action: Action) {
        players[player.id]!!.actions.add(action)
    }

    fun getPlayer(id: UUID): Player = players[id]!!
}