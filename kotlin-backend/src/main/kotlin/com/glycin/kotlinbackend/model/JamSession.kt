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
    A_MINOR, //553
    G, //331
    E_MINOR, // XXX
    F, // 664
    D_MINOR,
    C,
}

data class RockerSession(
    val rockerId: UUID,
    val rockerName: String,
    val sessionId: UUID,
)