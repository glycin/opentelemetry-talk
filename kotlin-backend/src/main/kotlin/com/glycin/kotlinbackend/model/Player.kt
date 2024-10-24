package com.glycin.kotlinbackend.model

import java.util.*

data class Player(
    val id: UUID,
    val name: String,
    val actions: MutableList<Action>,
)