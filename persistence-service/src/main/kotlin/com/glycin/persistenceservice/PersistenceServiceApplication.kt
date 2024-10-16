package com.glycin.persistenceservice

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class PersistenceServiceApplication

fun main(args: Array<String>) {
    runApplication<PersistenceServiceApplication>(*args)
}
