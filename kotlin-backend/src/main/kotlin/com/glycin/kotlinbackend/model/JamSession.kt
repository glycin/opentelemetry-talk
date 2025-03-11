package com.glycin.kotlinbackend.model

import java.util.*

data class JamSession(
    val id: UUID,
    val rockers : List<Rocker>
)

data class Rocker(
    val id: UUID,
    val name: String,
    val chordsPlayed: List<Strum>,
)

data class Strum(
    val timeStamp : Long,
    val chord: PowerChord,
)

enum class PowerChord {
    GUITAR,
    HORNS,
    EXPLOSION,
    SKULL,
    TIGER,
    NOTE,
}

data class RockerSession(
    val rockerId: UUID,
    val rockerName: String,
    val sessionId: UUID,
)