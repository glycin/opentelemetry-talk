package com.glycin.persistenceservice.service

import com.glycin.persistenceservice.model.Obstacle
import com.glycin.persistenceservice.model.Player
import com.glycin.persistenceservice.model.Session
import com.glycin.persistenceservice.repository.SessionRepository
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import kotlin.math.round
import kotlin.random.Random

private const val OBSTACLE_COUNT = 1000

@Service
class SessionService(
    private val sessionRepository: SessionRepository,
) {

    @PostConstruct
    fun init() {
        createSession()
    }

    fun createSession(): Session {
        val sesh = Session(
            obstacles = createObstacles(),
            players = Collections.newSetFromMap(ConcurrentHashMap()),
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
        repeat(OBSTACLE_COUNT) {
            obstacles.add(
                Obstacle(
                    number = it,
                    yPosition = (round(Random.nextDouble(-5.0, 5.0) * 100) / 100).toFloat(),
                )
            )
        }

        return obstacles
    }
}
