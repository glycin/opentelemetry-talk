package com.glycin.persistenceservice.service

import com.glycin.persistenceservice.model.ActionType
import com.glycin.persistenceservice.repository.SessionRepository
import io.micrometer.core.instrument.Metrics
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service

@Service
class GameMetricsService(
    private val sessionRepository: SessionRepository,
) {
    @PostConstruct
    fun init() {
        val meterRegistry = Metrics.globalRegistry

        meterRegistry.gauge("traceybird.score.highest", this) {
            it.sessionRepository.getLatestSession()?.highScore?.get()?.toDouble() ?: 0.0
        }
        meterRegistry.gauge("traceybird.deaths.total", this) {
            it.sessionRepository.getLatestSession()?.totalDeaths?.get()?.toDouble() ?: 0.0
        }
        meterRegistry.gauge("traceybird.score.death.ratio", this) {
            var scores = 0
            var deaths = 0
            it.sessionRepository.getLatestSession()?.players?.forEach { player ->
                player.actions.forEach { action ->
                    when (action.type) {
                        ActionType.TAP -> {}
                        ActionType.SCORE -> scores++
                        ActionType.DEATH -> deaths++
                    }
                }
            }
            when {
                scores == 0 -> 0.0
                deaths == 0 -> 1.0
                else -> 1.0 * scores / deaths
            }
        }
    }
}
