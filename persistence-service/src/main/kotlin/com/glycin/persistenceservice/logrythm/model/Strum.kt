package com.glycin.persistenceservice.logrythm.model

data class Strum(
    val timeStamp : Long,
    val chord: PowerChord = PowerChord.C,
)

enum class PowerChord {
    A_MINOR, //553
    G, //331
    E_MINOR, // XXX
    F, // 664
    D_MINOR,
    C,
}