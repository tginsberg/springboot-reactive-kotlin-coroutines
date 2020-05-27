package com.ginsberg.counter

import java.time.ZoneId
import java.time.ZonedDateTime



class CounterEvent(
    val value: Long,
    val action: CounterAction,
    val at: ZonedDateTime = ZonedDateTime.now(ZoneId.of("UTC"))
)
enum class CounterAction { UP, DOWN }

class CounterState(
    val value: Long,
    val at: ZonedDateTime = ZonedDateTime.now(ZoneId.of("UTC"))
) {
    fun toEvent(action: CounterAction) = CounterEvent(value, action, at)
}
