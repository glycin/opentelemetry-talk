package com.glycin.kotlinbackend.connector

import com.glycin.kotlinbackend.controller.PLAYER_ID_HEADER
import com.glycin.kotlinbackend.controller.ROCKER_ID_HEADER
import com.glycin.kotlinbackend.model.*
import com.glycin.kotlinbackend.model.rest.AddActionResponse
import com.glycin.kotlinbackend.model.rest.StrumResponse
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import java.util.*

@FeignClient("persistence-service")
interface PersistenceServiceConnector {

    @RequestMapping(method = [RequestMethod.GET], value = ["/session/getLatestState"])
    fun getLatestState(
        @RequestHeader(PLAYER_ID_HEADER) playerId: UUID,
    ): Session

    @RequestMapping(method = [RequestMethod.GET], value = ["/player/create"])
    fun createPlayer(
        @RequestParam id: UUID,
        @RequestParam name: String,
    ): Session?

    @RequestMapping(method = [RequestMethod.POST], value = ["/player/action"])
    fun postAction(
        @RequestHeader(PLAYER_ID_HEADER) playerId: UUID,
        @RequestParam actionTime: Long,
        @RequestParam actionType: ActionType,
    ): AddActionResponse

    @RequestMapping(method = [RequestMethod.GET], value = ["/logrythm/jam/getLatestState"])
    fun getLatestJamState(
        @RequestHeader(ROCKER_ID_HEADER) playerId: UUID,
    ): RockerSession

    @RequestMapping(method = [RequestMethod.GET], value = ["/logrythm/rocker/create"])
    fun createRocker(
        @RequestParam id: UUID,
        @RequestParam name: String,
    ): RockerSession?

    @RequestMapping(method = [RequestMethod.POST], value = ["/logrythm/rocker/strum"])
    fun postStrum(
        @RequestHeader(ROCKER_ID_HEADER) rockerId: UUID,
        @RequestParam strumTime: Long,
        @RequestParam chord: PowerChord,
    ): StrumResponse
}
