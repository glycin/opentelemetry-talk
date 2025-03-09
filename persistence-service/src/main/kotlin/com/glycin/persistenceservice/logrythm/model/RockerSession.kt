package com.glycin.persistenceservice.logrythm.model

import java.util.*

data class RockerSession(
    val rockerId: UUID,
    val rockerName: String,
    val sessionId: UUID,
)