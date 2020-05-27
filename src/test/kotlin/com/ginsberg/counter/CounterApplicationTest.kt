package com.ginsberg.counter

import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.asCoroutineDispatcher
import kotlinx.coroutines.async
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.take
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.reactive.asFlow
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.http.MediaType
import org.springframework.test.web.reactive.server.WebTestClient
import org.springframework.test.web.reactive.server.returnResult
import java.util.concurrent.Executors

@ExperimentalCoroutinesApi
@SpringBootTest
@Import(TestRedisConfiguration::class)
@AutoConfigureWebTestClient
internal class CounterApplicationTest(
    @Autowired private val webTestClient: WebTestClient
) {
    private val testDispatcher = Executors.newFixedThreadPool(3).asCoroutineDispatcher()

    @Test
    fun `PUT up increments the counter`() = runBlocking<Unit> {
        // Arrange
        val before = webTestClient.get().uri("/")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .returnResult<CounterState>()
            .responseBody
            .awaitFirst()

        // Act, Assert
        webTestClient.put().uri("/up")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("\$.value").isEqualTo(before.value.inc())
    }

    @Test
    fun `PUT down decrements the counter`() = runBlocking<Unit> {
        // Arrange
        val before = webTestClient.get().uri("/")
            .accept(MediaType.APPLICATION_JSON)
            .exchange()
            .returnResult<CounterState>()
            .responseBody
            .awaitFirst()

        // Act,Assert
        webTestClient.put().uri("/down")
            .exchange()
            .expectStatus().isOk
            .expectBody()
            .jsonPath("\$.value").isEqualTo(before.value.dec())
    }

    @Test
    fun `If subscribed, the counter events are delivered to a PubSub stream`() = runBlocking<Unit>(testDispatcher) {

        // Arrange
        val stream = async {
            webTestClient.get().uri("/")
                .accept(MediaType.TEXT_EVENT_STREAM)
                .exchange()
                .returnResult<CounterEvent>()
                .responseBody
                .asFlow()
        }

        // This is not ideal, but if the stream starts listening _after_ the values are produced, it won't receive anything.
        // And I can't find a nice way to figure out when webTestClient has started exchanging
        delay(1000)

        // Act
        val down = webTestClient.put().uri("/down").exchange().returnResult<CounterState>().responseBody.awaitFirst()
        val up = webTestClient.put().uri("/up").exchange().returnResult<CounterState>().responseBody.awaitFirst()
        val events = stream.await().take(2).toList()

        // Assert
        assertThat(events.first())
            .hasFieldOrPropertyWithValue("value", down.value)
            .hasFieldOrPropertyWithValue("action", CounterAction.DOWN)
            .hasFieldOrPropertyWithValue("at", down.at)
        assertThat(events.last())
            .hasFieldOrPropertyWithValue("value", up.value)
            .hasFieldOrPropertyWithValue("action", CounterAction.UP)
            .hasFieldOrPropertyWithValue("at", up.at)
    }

}