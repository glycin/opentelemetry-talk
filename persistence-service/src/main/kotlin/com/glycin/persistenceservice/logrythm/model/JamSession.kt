package com.glycin.persistenceservice.logrythm.model

import java.util.*

data class JamSession(
    val id: UUID = UUID.randomUUID(),
    val rockers : MutableSet<Rocker>
)