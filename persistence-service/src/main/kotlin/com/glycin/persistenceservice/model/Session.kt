package com.glycin.persistenceservice.model

import java.util.*
import java.util.concurrent.atomic.AtomicInteger

data class Session(
    val id: UUID = UUID.randomUUID(),
    val obstacles: List<Obstacle>,
    val players: MutableSet<Player>,
    val highScore: AtomicInteger,
    val totalDeaths: AtomicInteger,
)
