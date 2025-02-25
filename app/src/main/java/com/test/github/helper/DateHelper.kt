package com.test.github.helper

import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter

fun formatDate(dateString: String): String {
    val instant = Instant.parse(dateString)

    val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")
        .withZone(ZoneId.systemDefault())

    return formatter.format(instant)
}