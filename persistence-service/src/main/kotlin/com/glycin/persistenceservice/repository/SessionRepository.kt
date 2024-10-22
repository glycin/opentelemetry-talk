package com.glycin.persistenceservice.repository

import com.glycin.persistenceservice.model.Player
import com.glycin.persistenceservice.model.Session
import org.springframework.stereotype.Repository
import java.util.*
import kotlin.collections.HashMap

@Repository
class SessionRepository {

    private val sessions: MutableMap<UUID, Session> = HashMap()
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