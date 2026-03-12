package com.bootcamp.capacidad.domain.spi;

import com.bootcamp.capacidad.domain.model.Technology;
import reactor.core.publisher.Mono;

public interface ITechnologyServicePort {

    Mono<Technology> findById(Long id);
}
