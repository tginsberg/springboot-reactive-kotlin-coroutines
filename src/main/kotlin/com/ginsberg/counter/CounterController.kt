package com.ginsberg.counter

import kotlinx.coroutines.flow.Flow
import org.springframework.http.MediaType
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RestController

@RestController
class CounterController(private val repo: CounterRepository) {

    @GetMapping("/")
    suspend fun get(): CounterState = repo.get()

    @PutMapping("/up")
    suspend fun up(): CounterState = repo.up()

    @PutMapping("/down")
    suspend fun down(): CounterState = repo.down()

    @GetMapping(value = ["/"], produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    suspend fun stream(): Flow<CounterEvent> = repo.stream()
}