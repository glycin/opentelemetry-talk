package com.glycin.kotlinbackend.model.rest

import java.util.*

data class RestActionBody(
    val playerId: UUID,
    val timestamp: Long,
)