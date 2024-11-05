package com.glycin.persistenceservice.service

import com.glycin.persistenceservice.model.ActionType
import com.glycin.persistenceservice.model.Obstacle
import com.glycin.persistenceservice.model.Player
import com.glycin.persistenceservice.model.Session
import com.glycin.persistenceservice.repository.SessionRepository
import jakarta.annotation.PostConstruct
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.atomic.AtomicInteger
import kotlin.math.round
import kotlin.random.Random

private const val OBSTACLE_COUNT = 1000

@Service
class SessionService(
    private val sessionRepository: SessionRepository,
) {
    private val logger: Logger = LoggerFactory.getLogger(SessionService::class.java)

    @PostConstruct
    fun init() {
        createSession()
    }

    fun createSession(): Session {
        val sesh = Session(
            obstacles = createObstacles(),
            players = Collections.newSetFromMap(ConcurrentHashMap()),
            highScore = AtomicInteger(),
            totalDeaths = AtomicInteger(),
        )
        sessionRepository.save(sesh)
        return sesh
    }

    fun getActiveSession(): Session? = sessionRepository.getLatestSession()

    fun addPlayerToSession(player: Player, sessionId: UUID) {
        sessionRepository.addPlayer(player, sessionId)
    }

    fun processPlayerAction(player: Player, action: ActionType) {
        val session = getActiveSession() ?: return
        when (action) {
            ActionType.TAP -> {} // Do nothing
            ActionType.SCORE -> session.highScore.updateAndGet { existing ->
                if (existing < player.score) {
                    logger.info("Player '${player.name}' set a new high score of ${player.score}!")
                    player.score
                } else existing
            }
            ActionType.DEATH -> session.totalDeaths.incrementAndGet()
        }
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
