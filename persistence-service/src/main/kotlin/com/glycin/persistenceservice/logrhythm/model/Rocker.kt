package com.glycin.persistenceservice.logrhythm.model

import java.util.*

data class Rocker(
    val id: UUID,
    val name: String,
    val chordsPlayed: MutableList<Strum>,
)