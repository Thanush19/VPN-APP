package com.safetunnel.backend

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication

@SpringBootApplication
class SafeTunnelBackendApplication

fun main(args: Array<String>) {
    runApplication<SafeTunnelBackendApplication>(*args)
}