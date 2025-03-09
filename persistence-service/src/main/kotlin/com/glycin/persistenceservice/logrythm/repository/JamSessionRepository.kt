package com.glycin.persistenceservice.logrythm.repository

import com.glycin.persistenceservice.logrythm.model.JamSession
import com.glycin.persistenceservice.logrythm.model.Rocker
import org.springframework.stereotype.Repository
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap

@Repository
class JamSessionRepository {

    private val sessions: ConcurrentMap<UUID, JamSession> = ConcurrentHashMap()
    private var latestSession: JamSession? = null

    fun save(session: JamSession) {
        sessions[session.id] = session
        latestSession = session
    }

    fun getSession(id: UUID): JamSession {
        return sessions[id]!!
    }

    fun getLatestSession(): JamSession? {
        return latestSession
    }

    fun addRocker(rocker: Rocker, sessionId: UUID) {
        sessions[sessionId]!!.rockers.add(rocker)
    }
}