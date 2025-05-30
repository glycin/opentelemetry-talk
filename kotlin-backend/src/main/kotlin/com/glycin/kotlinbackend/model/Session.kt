package com.glycin.kotlinbackend.model

import java.util.*

data class Session(
    val playerId: UUID,
    val playerName: String,
    val sessionId: UUID,
    val obstacles: List<Obstacle>,
)
