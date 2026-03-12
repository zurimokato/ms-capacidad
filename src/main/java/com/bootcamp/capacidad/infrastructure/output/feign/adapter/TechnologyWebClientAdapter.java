package com.bootcamp.capacidad.infrastructure.output.feign.adapter;

import com.bootcamp.capacidad.domain.model.Technology;
import com.bootcamp.capacidad.domain.spi.ITechnologyServicePort;
import com.bootcamp.capacidad.infrastructure.output.feign.dto.TechnologyExternalResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Service
public class TechnologyWebClientAdapter implements ITechnologyServicePort {

    private final WebClient webClient;

    public TechnologyWebClientAdapter(@Value("${external.technology-service.url}") String baseUrl,
                                      WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(baseUrl).build();
    }

    @Override
    public Mono<Technology> findById(Long id) {
        return webClient.get()
                .uri("/api/technologies/{id}", id)
                .retrieve()
                .onStatus(status -> status.is4xxClientError(),
                        response -> Mono.error(new RuntimeException("Tecnología con id " + id + " no encontrada en el servicio externo")))
                .bodyToMono(TechnologyExternalResponse.class)
                .map(ext -> new Technology(ext.getId(), ext.getName()));
    }
}
