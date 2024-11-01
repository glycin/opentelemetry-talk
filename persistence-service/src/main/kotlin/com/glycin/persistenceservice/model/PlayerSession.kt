package com.glycin.persistenceservice.model

import java.util.UUID

data class PlayerSession(
    val playerId: UUID,
    val playerName: String,
    val sessionId: UUID,
    val obstacles: List<Obstacle>,
)
