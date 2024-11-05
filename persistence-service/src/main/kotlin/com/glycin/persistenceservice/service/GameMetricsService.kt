package com.glycin.persistenceservice.service

import com.glycin.persistenceservice.repository.SessionRepository
import io.micrometer.core.instrument.MeterRegistry
import jakarta.annotation.PostConstruct
import org.springframework.stereotype.Service

@Service
class GameMetricsService(
    private val sessionRepository: SessionRepository,
    private val meterRegistry: MeterRegistry,
) {

    @PostConstruct
    fun init() {
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
                scores += player.score
                deaths += player.deaths
            }
            when {
                scores == 0 -> 0.0
                deaths == 0 -> 1.0
                else -> 1.0 * scores / deaths
            }
        }
    }
}
