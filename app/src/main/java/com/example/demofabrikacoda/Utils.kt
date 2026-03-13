package com.example.demofabrikacoda

import kotlin.collections.toString

fun ByteArray?.toConvertAndFilter(): String {
    this ?: return ""
    return this.toString(Charsets.UTF_8).filter {
        it.isLetterOrDigit()
                || it.isWhitespace()
                ||  it in ",.!?;:-+*/=()@#$%^&[]{}`~|\\\'\""
    }.trim()
}