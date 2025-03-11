package com.glycin.kotlinbackend.model.rest

import com.glycin.kotlinbackend.model.PowerChord
import java.util.*

data class StrumResponse(
    val rockerId: UUID,
    val rockerName: String,
    val strumTime: Long,
    val chord: PowerChord,
)