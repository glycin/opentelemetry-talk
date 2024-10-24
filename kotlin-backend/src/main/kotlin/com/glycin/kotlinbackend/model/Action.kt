package com.glycin.kotlinbackend.model

data class Action(
    val timeStamp : Long,
    val type: ActionType = ActionType.TAP,
)

enum class ActionType {
    TAP,
}