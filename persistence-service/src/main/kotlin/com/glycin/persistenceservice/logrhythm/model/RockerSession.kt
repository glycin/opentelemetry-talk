package com.glycin.persistenceservice.logrhythm.model

import java.util.*

data class RockerSession(
    val rockerId: UUID,
    val rockerName: String,
    val sessionId: UUID,
)