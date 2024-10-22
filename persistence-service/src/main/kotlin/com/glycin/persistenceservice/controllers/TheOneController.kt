package com.glycin.persistenceservice.controllers

import com.glycin.persistenceservice.model.Player
import com.glycin.persistenceservice.model.Session
import com.glycin.persistenceservice.service.PlayerService
import com.glycin.persistenceservice.service.SessionService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController
import java.util.UUID

@RestController
class TheOneController(
    private val sessionService: SessionService,
    private val playerService: PlayerService,
){

    @GetMapping("/session/init")
    fun initNewSession(): ResponseEntity<Session> {
        return ResponseEntity.ok(sessionService.createSession())
    }

    @GetMapping("/player/create")
    fun createPlayer(
        @RequestParam playerId: UUID,
        @RequestParam name: String,
    ): ResponseEntity<Session> {
        val player = playerService.createPlayer(playerId, name)
        return sessionService.getActiveSession()?.let {
            sessionService.addPlayerToSession(player, it.id)
            ResponseEntity.ok(it)
        } ?: ResponseEntity.notFound().build()
    }

    @GetMapping("/session/getLatestState")
    fun getLatestState(): ResponseEntity<Session> {
        sessionService.getActiveSession()?.let {
            return ResponseEntity.ok(it)
        } ?: return ResponseEntity.notFound().build()
    }
}