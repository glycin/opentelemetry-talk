package com.glycin.persistenceservice.logrhythm.model

import java.util.*

data class StrumResponse (
    val rockerId: UUID,
    val rockerName: String,
    val strumTime: Long,
    val chord: PowerChord,
)
