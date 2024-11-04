package com.glycin.kotlinbackend.model.rest

import java.util.*

data class AddActionResponse(
    val playerId: UUID,
    val playerName: String,
    val actionTime: Long,
    val actionType: String,
)