package com.bootcamp.capacidad.infrastructure.output.mysql.adapter;

import com.bootcamp.capacidad.domain.model.Capability;
import com.bootcamp.capacidad.domain.model.Technology;
import com.bootcamp.capacidad.domain.spi.ICapabilityPersistencePort;
import com.bootcamp.capacidad.domain.spi.ITechnologyServicePort;
import com.bootcamp.capacidad.infrastructure.output.mysql.entity.CapabilityTechnologyEntity;
import com.bootcamp.capacidad.infrastructure.output.mysql.mapper.ICapabilityEntityMapper;
import com.bootcamp.capacidad.infrastructure.output.mysql.repository.ICapabilityRepository;
import com.bootcamp.capacidad.infrastructure.output.mysql.repository.ICapabilityTechnologyRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

@Service
public class CapabilityMysqlAdapter implements ICapabilityPersistencePort {

    private final ICapabilityRepository capabilityRepository;
    private final ICapabilityTechnologyRepository capTechRepository;
    private final ICapabilityEntityMapper mapper;
    private final ITechnologyServicePort technologyServicePort;

    public CapabilityMysqlAdapter(ICapabilityRepository capabilityRepository,
                                  ICapabilityTechnologyRepository capTechRepository,
                                  ICapabilityEntityMapper mapper,
                                  ITechnologyServicePort technologyServicePort) {
        this.capabilityRepository = capabilityRepository;
        this.capTechRepository = capTechRepository;
        this.mapper = mapper;
        this.technologyServicePort = technologyServicePort;
    }

    @Override
    public Mono<Capability> save(Capability capability) {
        return capabilityRepository.save(mapper.toEntity(capability))
                .map(mapper::toDomain);
    }

    @Override
    public Mono<Boolean> existsByName(String name) {
        return capabilityRepository.existsByName(name);
    }

    @Override
    public Flux<Capability> findAll(int page, int size, String sortBy, boolean ascending) {
        int offset = page * size;

        Flux<com.bootcamp.capacidad.infrastructure.output.mysql.entity.CapabilityEntity> entities;

        if ("technologyCount".equalsIgnoreCase(sortBy)) {
            entities = ascending
                    ? capabilityRepository.findAllByTechCountAsc(size, offset)
                    : capabilityRepository.findAllByTechCountDesc(size, offset);
        } else {
            entities = ascending
                    ? capabilityRepository.findAllByNameAsc(size, offset)
                    : capabilityRepository.findAllByNameDesc(size, offset);
        }

        return entities.map(mapper::toDomain)
                .flatMap(this::enrichWithTechnologies);
    }

    @Override
    public Mono<Capability> findById(Long id) {
        return capabilityRepository.findById(id)
                .map(mapper::toDomain)
                .flatMap(this::enrichWithTechnologies);
    }

    @Override
    public Mono<Void> saveCapabilityTechnologies(Long capabilityId, List<Long> technologyIds) {
        return Flux.fromIterable(technologyIds)
                .map(techId -> new CapabilityTechnologyEntity(capabilityId, techId))
                .flatMap(capTechRepository::save)
                .then();
    }

    private Mono<Capability> enrichWithTechnologies(Capability capability) {
        return capTechRepository.findByCapabilityId(capability.getId())
                .flatMap(ct -> technologyServicePort.findById(ct.getTechnologyId()))
                .collectList()
                .map(techs -> {
                    capability.setTechnologies(techs);
                    return capability;
                });
    }
}
