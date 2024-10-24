package com.glycin.kotlinbackend.model

import java.util.*

data class Session(
    val id: UUID = UUID.randomUUID(),
    val obstacles: List<Obstacle>,
    val players: MutableList<Player>,
)