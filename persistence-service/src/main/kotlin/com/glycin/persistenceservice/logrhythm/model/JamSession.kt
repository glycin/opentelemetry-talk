package com.glycin.persistenceservice.logrhythm.model

import java.util.*

data class JamSession(
    val id: UUID = UUID.randomUUID(),
    val rockers : MutableSet<Rocker>
)