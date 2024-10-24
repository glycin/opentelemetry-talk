package com.glycin.kotlinbackend.controller

import com.glycin.kotlinbackend.connector.PersistenceServiceConnector
import com.glycin.kotlinbackend.model.Action
import com.glycin.kotlinbackend.model.Session
import com.glycin.kotlinbackend.model.rest.RestActionBody
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.*

@RestController
class GameController(
    private val persistenceConnector: PersistenceServiceConnector,
) {

    @GetMapping("/latest")
    fun getLatestSession(): ResponseEntity<Session> {
        return ResponseEntity.ok(persistenceConnector.getLatestState())
    }

    @GetMapping("/create/player")
    fun createPlayer(
        @RequestParam playerId: UUID,
        @RequestParam name: String,
    ): ResponseEntity<Session> {
        val s = persistenceConnector.createPlayer(playerId, name)
        return ResponseEntity.ok(s)
    }

    @PostMapping("/player/action")
    fun submitAction(
        @RequestBody action: RestActionBody,
    ): ResponseEntity<*> {
        persistenceConnector.postAction(action.playerId, action.timestamp)
        return ResponseEntity.ok("")
    }
}