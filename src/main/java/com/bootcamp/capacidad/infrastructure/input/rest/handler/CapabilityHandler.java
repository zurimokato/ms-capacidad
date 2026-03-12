package com.bootcamp.capacidad.infrastructure.input.rest.handler;

import com.bootcamp.capacidad.domain.api.ICapabilityUseCasePort;
import com.bootcamp.capacidad.infrastructure.input.rest.dto.CapabilityRequest;
import com.bootcamp.capacidad.infrastructure.input.rest.mapper.ICapabilityRequestMapper;
import jakarta.validation.Validator;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

@Component
public class CapabilityHandler {

    private final ICapabilityUseCasePort capabilityUseCasePort;
    private final ICapabilityRequestMapper mapper;
    private final Validator validator;

    public CapabilityHandler(ICapabilityUseCasePort capabilityUseCasePort,
                             ICapabilityRequestMapper mapper,
                             Validator validator) {
        this.capabilityUseCasePort = capabilityUseCasePort;
        this.mapper = mapper;
        this.validator = validator;
    }

    public Mono<ServerResponse> save(ServerRequest request) {
        return request.bodyToMono(CapabilityRequest.class)
                .flatMap(this::validateRequest)
                .map(mapper::toDomain)
                .flatMap(capabilityUseCasePort::save)
                .map(mapper::toResponse)
                .flatMap(response -> ServerResponse.status(HttpStatus.CREATED)
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(response));
    }

    public Mono<ServerResponse> findAll(ServerRequest request) {
        int page = Integer.parseInt(request.queryParam("page").orElse("0"));
        int size = Integer.parseInt(request.queryParam("size").orElse("10"));
        String sortBy = request.queryParam("sortBy").orElse("name");
        boolean ascending = Boolean.parseBoolean(request.queryParam("ascending").orElse("true"));

        return capabilityUseCasePort.findAll(page, size, sortBy, ascending)
                .map(mapper::toResponse)
                .collectList()
                .flatMap(list -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(list));
    }

    private Mono<CapabilityRequest> validateRequest(CapabilityRequest request) {
        var violations = validator.validate(request);
        if (!violations.isEmpty()) {
            String message = violations.iterator().next().getMessage();
            return Mono.error(new IllegalArgumentException(message));
        }
        return Mono.just(request);
    }
}
