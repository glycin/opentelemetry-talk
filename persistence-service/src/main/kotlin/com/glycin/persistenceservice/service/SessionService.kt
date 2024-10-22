package com.glycin.persistenceservice.service

import com.glycin.persistenceservice.model.Obstacle
import com.glycin.persistenceservice.model.Player
import com.glycin.persistenceservice.model.Session
import com.glycin.persistenceservice.repository.SessionRepository
import org.springframework.stereotype.Service
import java.util.UUID
import kotlin.math.round
import kotlin.random.Random

private const val OBSTACLE_COUNT = 1000

@Service
class SessionService(
    private val sessionRepository: SessionRepository,
) {

    fun createSession(): Session {
        val sesh = Session(
            obstacles = createObstacles(),
            players = mutableListOf()
        )
        sessionRepository.save(sesh)
        return sesh
    }

    fun getActiveSession(): Session? = sessionRepository.getLatestSession()

    fun addPlayerToSession(player: Player, sessionId: UUID) {
        sessionRepository.addPlayer(player, sessionId)
    }

    private fun createObstacles(): List<Obstacle> {
        val obstacles = mutableListOf<Obstacle>()
        for(i in 1..OBSTACLE_COUNT) {
            obstacles.add(
                Obstacle(
                    number = i,
                    yPosition = (round(Random.nextDouble(-5.0, 5.0) * 100) / 100).toFloat(),
                )
            )
        }

        return obstacles
    }
}