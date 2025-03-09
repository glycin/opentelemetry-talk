package com.glycin.persistenceservice.logrythm.services

import com.glycin.persistenceservice.logrythm.model.JamSession
import com.glycin.persistenceservice.logrythm.model.PowerChord
import com.glycin.persistenceservice.logrythm.model.Rocker
import com.glycin.persistenceservice.logrythm.repository.JamSessionRepository
import jakarta.annotation.PostConstruct
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@Service
class JamSessionService(
    private val repository: JamSessionRepository,
) {
    private val logger: Logger = LoggerFactory.getLogger(JamSessionService::class.java)

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

    fun processRockerStrum(rocker: Rocker, chord: PowerChord) {
        val session = getActiveSession() ?: return
        //TODO: Do i need to do anything here?
    }
}