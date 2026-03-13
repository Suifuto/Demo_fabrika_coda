package com.example.demofabrikacoda.data

import java.time.Instant

data class PingModel(
    val timestamp: Instant? = null,
    val latency: Long? = null
)
