package com.bootcamp.capacidad.infrastructure.output.feign.adapter;

import com.bootcamp.capacidad.infrastructure.output.feign.dto.TechnologyExternalResponse;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.test.StepVerifier;

import java.io.IOException;

class TechnologyWebClientAdapterTest {

    private MockWebServer mockWebServer;
    private TechnologyWebClientAdapter adapter;

    @BeforeEach
    void setUp() throws IOException {
        mockWebServer = new MockWebServer();
        mockWebServer.start();

        String baseUrl = mockWebServer.url("/").toString();
        adapter = new TechnologyWebClientAdapter(baseUrl, WebClient.builder());
    }

    @AfterEach
    void tearDown() throws IOException {
        mockWebServer.shutdown();
    }

    @Test
    @DisplayName("findById - debe retornar tecnología cuando el servicio responde 200")
    void findById_WhenServiceReturns200_ShouldReturnTechnology() {
        mockWebServer.enqueue(new MockResponse()
                .setBody("{\"id\":1,\"name\":\"Java\",\"description\":\"Lenguaje backend\"}")
                .addHeader("Content-Type", "application/json"));

        StepVerifier.create(adapter.findById(1L))
                .expectNextMatches(t -> t.getId().equals(1L) && t.getName().equals("Java"))
                .verifyComplete();
    }

    @Test
    @DisplayName("findById - debe lanzar error cuando el servicio responde 404")
    void findById_WhenServiceReturns404_ShouldThrow() {
        mockWebServer.enqueue(new MockResponse().setResponseCode(404));

        StepVerifier.create(adapter.findById(99L))
                .expectError(RuntimeException.class)
                .verify();
    }
}
