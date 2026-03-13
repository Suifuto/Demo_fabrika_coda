package com.example.demofabrikacoda.data

import io.libp2p.core.ConnectionClosedException

/**
 * Универсальный обработчик ошибок
 */
fun Throwable.toConvert(): String? {
    return when(this) {
        is ConnectionClosedException -> "Соединение с нодой прервано"
        // is Exception -> this.message
        else -> this.message
    }
}