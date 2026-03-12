package com.bootcamp.capacidad.domain.usecase;

import com.bootcamp.capacidad.domain.api.ICapabilityUseCasePort;
import com.bootcamp.capacidad.domain.exception.CapabilityAlreadyExistsException;
import com.bootcamp.capacidad.domain.exception.DomainConstants;
import com.bootcamp.capacidad.domain.model.Capability;
import com.bootcamp.capacidad.domain.model.Technology;
import com.bootcamp.capacidad.domain.spi.ICapabilityPersistencePort;
import com.bootcamp.capacidad.domain.spi.ITechnologyServicePort;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class CapabilityUseCase implements ICapabilityUseCasePort {

    private final ICapabilityPersistencePort capabilityPersistencePort;
    private final ITechnologyServicePort technologyServicePort;

    public CapabilityUseCase(ICapabilityPersistencePort capabilityPersistencePort,
                             ITechnologyServicePort technologyServicePort) {
        this.capabilityPersistencePort = capabilityPersistencePort;
        this.technologyServicePort = technologyServicePort;
    }

    @Override
    public Mono<Capability> save(Capability capability) {
        return Mono.defer(() -> validate(capability))
                .then(Mono.defer(() -> capabilityPersistencePort.existsByName(capability.getName())))
                .flatMap(exists -> {
                    if (Boolean.TRUE.equals(exists)) {
                        return Mono.error(new CapabilityAlreadyExistsException(capability.getName()));
                    }
                    return validateTechnologiesExist(capability.getTechnologies())
                            .then(capabilityPersistencePort.save(capability))
                            .flatMap(saved -> {
                                List<Long> techIds = capability.getTechnologies().stream()
                                        .map(Technology::getId)
                                        .collect(Collectors.toList());
                                return capabilityPersistencePort.saveCapabilityTechnologies(saved.getId(), techIds)
                                        .then(Mono.just(saved))
                                        .doOnNext(c -> c.setTechnologies(capability.getTechnologies()));
                            });
                });
    }

    @Override
    public Flux<Capability> findAll(int page, int size, String sortBy, boolean ascending) {
        return capabilityPersistencePort.findAll(page, size, sortBy, ascending);
    }

    private Mono<Void> validate(Capability capability) {
        if (capability.getName() == null || capability.getName().isBlank()) {
            return Mono.error(new IllegalArgumentException(DomainConstants.NAME_REQUIRED));
        }
        if (capability.getDescription() == null || capability.getDescription().isBlank()) {
            return Mono.error(new IllegalArgumentException(DomainConstants.DESCRIPTION_REQUIRED));
        }
        if (capability.getTechnologies() == null || capability.getTechnologies().isEmpty()) {
            return Mono.error(new IllegalArgumentException(DomainConstants.TECHNOLOGIES_REQUIRED));
        }
        if (capability.getTechnologies().size() < DomainConstants.MIN_TECHNOLOGIES) {
            return Mono.error(new IllegalArgumentException(DomainConstants.MIN_TECHNOLOGIES_MSG));
        }
        if (capability.getTechnologies().size() > DomainConstants.MAX_TECHNOLOGIES) {
            return Mono.error(new IllegalArgumentException(DomainConstants.MAX_TECHNOLOGIES_MSG));
        }
        // Check for duplicate technology IDs
        Set<Long> uniqueIds = new HashSet<>();
        for (Technology tech : capability.getTechnologies()) {
            if (!uniqueIds.add(tech.getId())) {
                return Mono.error(new IllegalArgumentException(DomainConstants.DUPLICATE_TECHNOLOGIES_MSG));
            }
        }
        return Mono.empty();
    }

    private Mono<Void> validateTechnologiesExist(List<Technology> technologies) {
        return Flux.fromIterable(technologies)
                .flatMap(tech -> technologyServicePort.findById(tech.getId()))
                .collectList()
                .then();
    }
}
