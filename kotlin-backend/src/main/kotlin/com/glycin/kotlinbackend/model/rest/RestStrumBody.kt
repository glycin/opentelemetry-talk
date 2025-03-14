package com.glycin.kotlinbackend.model.rest

import com.glycin.kotlinbackend.model.PowerChord

data class RestStrumBody(
    val timestamp: Long,
    val chord: PowerChord,
)