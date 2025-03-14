package com.glycin.persistenceservice.logrhythm.model

data class Strum(
    val timeStamp : Long,
    val chord: PowerChord = PowerChord.NOTE,
)

enum class PowerChord {
    GUITAR,
    HORNS,
    EXPLOSION,
    SKULL,
    TIGER,
    NOTE,
}