package com.glycin.persistenceservice.service

import com.glycin.persistenceservice.model.Player
import com.glycin.persistenceservice.repository.PlayerRepository
import org.springframework.stereotype.Service
import java.util.*

@Service
class PlayerService(
    private val playerRepository: PlayerRepository,
) {

    fun createPlayer(id: UUID, name: String): Player {
        val player = Player(id, name, mutableListOf())
        playerRepository.save(player)
        return player
    }

    fun getPlayer(id: UUID): Player = playerRepository.getPlayer(id)
}