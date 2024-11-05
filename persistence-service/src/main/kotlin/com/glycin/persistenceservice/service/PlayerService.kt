package com.glycin.persistenceservice.service

import com.glycin.persistenceservice.model.Action
import com.glycin.persistenceservice.model.ActionType
import com.glycin.persistenceservice.model.Player
import com.glycin.persistenceservice.repository.PlayerRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class PlayerService(
    private val playerRepository: PlayerRepository,
) {

    fun createPlayer(id: UUID, name: String): Player? {
        val player = Player(id, name, mutableListOf())
        return playerRepository.create(player)
    }

    fun getPlayer(id: UUID): Player = playerRepository.getPlayer(id)

    fun addActionToPlayer(player: Player, actionType: ActionType, actionTime: Long) {
        playerRepository.updatePlayerAction(player, Action(actionTime, actionType))
    }
}
