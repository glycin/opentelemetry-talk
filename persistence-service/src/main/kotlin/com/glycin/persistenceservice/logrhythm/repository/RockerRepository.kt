package com.glycin.persistenceservice.logrhythm.repository

import com.glycin.persistenceservice.logrhythm.controller.ROCKER_ID_SPAN_ATTRIBUTE
import com.glycin.persistenceservice.logrhythm.model.Rocker
import com.glycin.persistenceservice.logrhythm.model.Strum
import io.opentelemetry.api.trace.Span
import io.opentelemetry.instrumentation.annotations.WithSpan
import org.springframework.stereotype.Repository
import java.util.*
import java.util.concurrent.ConcurrentHashMap
import java.util.concurrent.ConcurrentMap
import java.util.concurrent.locks.ReentrantLock

@Repository
class RockerRepository {

    private val rockers: ConcurrentMap<UUID, Rocker> = ConcurrentHashMap()
    private val rockerNames: MutableSet<String> = hashSetOf()

    private val rockerCreationLock = ReentrantLock()

    @WithSpan
    fun create(rocker: Rocker): Rocker? {
        Span.current().setAttribute(ROCKER_ID_SPAN_ATTRIBUTE, rocker.id.toString())
        rockerCreationLock.lock()
        return try {
            if (rockerNames.add(rocker.name)) {
                rockers[rocker.id] = rocker
                rocker
            } else {
                rockers[rocker.id]?.takeIf { it.name == rocker.name } // Allow reregistration of the same player if the ID matches
            }
        } finally {
            rockerCreationLock.unlock()
        }
    }

    @WithSpan
    fun updateRockerChord(rocker: Rocker, chord: Strum) {
        Span.current().setAttribute(ROCKER_ID_SPAN_ATTRIBUTE, rocker.id.toString())
        rocker.chordsPlayed.add(chord)
    }

    fun getRocker(id: UUID): Rocker = rockers.getValue(id)

}