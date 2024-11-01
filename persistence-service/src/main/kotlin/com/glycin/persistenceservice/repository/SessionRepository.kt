package com.glycin.persistenceservice.repository

import com.glycin.persistenceservice.model.Player
import com.glycin.persistenceservice.model.Session
import org.springframework.stereotype.Repository
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import kotlin.collections.HashMap

@Repository
class SessionRepository {

    private val sessions: ConcurrentMap<UUID, Session> = ConcurrentHashMap()
    private var latestSession: Session? = null

    fun save(session: Session) {
        sessions[session.id] = session
        latestSession = session
    }

    fun getSession(id: UUID): Session {
        return sessions[id]!!
    }

    fun getLatestSession(): Session? {
        return latestSession
    }

    fun addPlayer(player: Player, sessionId: UUID) {
        sessions[sessionId]!!.players.add(player)
    }
}
