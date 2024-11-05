package com.glycin.persistenceservice.model

import java.util.*

data class Player(
    val id: UUID,
    val name: String,
    val actions: MutableList<Action>,
    var score: Int = 0,
    var deaths: Int = 0,
)
