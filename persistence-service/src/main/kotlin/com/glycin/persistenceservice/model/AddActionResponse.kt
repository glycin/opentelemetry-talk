package com.glycin.persistenceservice.model

import java.util.*

data class AddActionResponse(
    val playerId: UUID,
    val playerName: String,
    val actionTime: Long,
)
