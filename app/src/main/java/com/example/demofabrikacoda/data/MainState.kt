package com.example.demofabrikacoda.data

import androidx.compose.runtime.Immutable
import org.peergos.HashedBlock

@Immutable
data class MainState(
    val status: String,
    val blocks: List<HashedBlock> = listOf()
)
