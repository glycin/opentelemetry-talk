package com.glycin.kotlinbackend.model

import com.fasterxml.jackson.annotation.JsonProperty

data class Obstacle(
    val number: Int,
    @JsonProperty("yposition")
    val yPosition: Float,
)