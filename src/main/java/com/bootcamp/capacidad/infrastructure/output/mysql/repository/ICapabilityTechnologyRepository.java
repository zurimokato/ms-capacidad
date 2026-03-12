package com.bootcamp.capacidad.infrastructure.output.mysql.repository;

import com.bootcamp.capacidad.infrastructure.output.mysql.entity.CapabilityTechnologyEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

public interface ICapabilityTechnologyRepository extends ReactiveCrudRepository<CapabilityTechnologyEntity, Long> {

    Flux<CapabilityTechnologyEntity> findByCapabilityId(Long capabilityId);
}
