package com.glycin.kotlinbackend.connector

import com.glycin.kotlinbackend.controller.PLAYER_ID_HEADER
import com.glycin.kotlinbackend.model.ActionType
import com.glycin.kotlinbackend.model.Session
import com.glycin.kotlinbackend.model.rest.AddActionResponse
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
}
