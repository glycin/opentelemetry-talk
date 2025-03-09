package com.glycin.kotlinbackend.model.rest

import java.util.*

data class StrumResponse(
    val rockerId: UUID,
    val rockerName: String,
    val strumTime: Long,
    val strumChord: String,
)