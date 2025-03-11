package com.glycin.persistenceservice.logrhythm.services

import com.glycin.persistenceservice.logrhythm.model.JamSession
import com.glycin.persistenceservice.logrhythm.model.Rocker
import com.glycin.persistenceservice.logrhythm.repository.JamSessionRepository
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Service
class JamSessionService(
    private val repository: JamSessionRepository,
) {
    @PostConstruct
    fun init() {
        createSession()
    }

    fun createSession(): JamSession {
        val sesh = JamSession(
            rockers = Collections.newSetFromMap(ConcurrentHashMap())
        )
        repository.save(sesh)
        return sesh
    }

    fun getActiveSession(): JamSession? = repository.getLatestSession()

    fun addRockerToJam(rocker: Rocker, sessionId: UUID) {
        repository.addRocker(rocker, sessionId)
    }
}