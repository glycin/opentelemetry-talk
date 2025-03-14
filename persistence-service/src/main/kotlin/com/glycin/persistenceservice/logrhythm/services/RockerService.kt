package com.glycin.persistenceservice.logrhythm.services

import com.glycin.persistenceservice.logrhythm.repository.RockerRepository
import com.glycin.persistenceservice.logrhythm.model.Rocker
import com.glycin.persistenceservice.logrhythm.model.PowerChord
import com.glycin.persistenceservice.logrhythm.model.Strum
import org.springframework.stereotype.Service
import java.util.*

@Service
class RockerService(
    private val rockerRepository: RockerRepository,
) {
    fun createRocker(id: UUID, name: String): Rocker? {
        val m = Rocker(id, name, mutableListOf())
        return rockerRepository.create(m)
    }

    fun getRocker(id: UUID): Rocker = rockerRepository.getRocker(id)

    fun addChordToRocker(player: Rocker, chord: PowerChord, actionTime: Long) {
        rockerRepository.updateRockerChord(player, Strum(actionTime, chord))
    }
}