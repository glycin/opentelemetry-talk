package com.glycin.kotlinbackend.connector

import com.glycin.kotlinbackend.model.Session
import org.springframework.cloud.openfeign.FeignClient
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestMethod
import org.springframework.web.bind.annotation.RequestParam
import java.util.*

@FeignClient("persistence-service")
interface PersistenceServiceConnector {

    @RequestMapping(method = [RequestMethod.GET], value = ["/session/getLatestState"])
    fun getLatestState(): Session

    @RequestMapping(method = [RequestMethod.GET], value = ["/player/create"])
    fun createPlayer(
        @RequestParam playerId: UUID,
        @RequestParam name: String,
    ): Session

    @RequestMapping(method = [RequestMethod.POST], value = ["/player/action"])
    fun postAction(
        @RequestParam playerId: UUID,
        @RequestParam actionTime: Long,
    )
}