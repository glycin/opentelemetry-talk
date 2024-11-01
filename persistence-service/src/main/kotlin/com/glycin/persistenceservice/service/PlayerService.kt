package com.glycin.persistenceservice.service

import com.glycin.persistenceservice.model.Action
import com.glycin.persistenceservice.model.Player
import com.glycin.persistenceservice.repository.PlayerRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class PlayerService(
    private val playerRepository: PlayerRepository,
) {

    fun createPlayer(name: String): Player? {
        val player = Player(UUID.randomUUID(), name, mutableListOf())
        return if (playerRepository.create(player)) player else null
    }

    fun getPlayer(id: UUID): Player = playerRepository.getPlayer(id)

    fun addActionToPlayer(player: Player, actionTime: Long) {
        playerRepository.updatePlayerAction(player, Action(actionTime))
    }
}
