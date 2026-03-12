package com.example.demofabrikacoda.data

import java.time.Instant

data class PingModel(
    val timestamp: Instant?,
    val latency: Long?,
    val status: String?,
)
