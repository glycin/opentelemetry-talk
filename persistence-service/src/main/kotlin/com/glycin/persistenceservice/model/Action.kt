package com.glycin.persistenceservice.model

data class Action(
    val timeStamp : Long,
    val type: ActionType = ActionType.TAP,
)

enum class ActionType {
    TAP,
    SCORE,
    DEATH,
}