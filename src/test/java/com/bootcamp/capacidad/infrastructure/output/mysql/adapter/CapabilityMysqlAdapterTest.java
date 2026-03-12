package com.bootcamp.capacidad.infrastructure.output.mysql.adapter;

import com.bootcamp.capacidad.domain.model.Capability;
import com.bootcamp.capacidad.domain.model.Technology;
import com.bootcamp.capacidad.domain.spi.ITechnologyServicePort;
import com.bootcamp.capacidad.infrastructure.output.mysql.entity.CapabilityEntity;
import com.bootcamp.capacidad.infrastructure.output.mysql.entity.CapabilityTechnologyEntity;
import com.bootcamp.capacidad.infrastructure.output.mysql.mapper.ICapabilityEntityMapper;
import com.bootcamp.capacidad.infrastructure.output.mysql.repository.ICapabilityRepository;
import com.bootcamp.capacidad.infrastructure.output.mysql.repository.ICapabilityTechnologyRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CapabilityMysqlAdapterTest {

    @Mock
    private ICapabilityRepository capabilityRepository;

    @Mock
    private ICapabilityTechnologyRepository capTechRepository;

    @Mock
    private ICapabilityEntityMapper mapper;

    @Mock
    private ITechnologyServicePort technologyServicePort;

    @InjectMocks
    private CapabilityMysqlAdapter adapter;

    @Test
    @DisplayName("save - debe persistir y retornar la capacidad")
    void save_ShouldPersist() {
        Capability domain = new Capability(null, "Backend", "Desc", List.of());
        CapabilityEntity entity = new CapabilityEntity(null, "Backend", "Desc");
        CapabilityEntity saved = new CapabilityEntity(1L, "Backend", "Desc");
        Capability result = new Capability(1L, "Backend", "Desc", null);

        when(mapper.toEntity(domain)).thenReturn(entity);
        when(capabilityRepository.save(entity)).thenReturn(Mono.just(saved));
        when(mapper.toDomain(saved)).thenReturn(result);

        StepVerifier.create(adapter.save(domain))
                .expectNextMatches(c -> c.getId().equals(1L))
                .verifyComplete();
    }

    @Test
    @DisplayName("existsByName - debe retornar true si existe")
    void existsByName_True() {
        when(capabilityRepository.existsByName("Backend")).thenReturn(Mono.just(true));

        StepVerifier.create(adapter.existsByName("Backend"))
                .expectNext(true)
                .verifyComplete();
    }

    @Test
    @DisplayName("findAll por nombre ascendente - debe enriquecer con tecnologías")
    void findAll_ByNameAsc_ShouldEnrichWithTechnologies() {
        CapabilityEntity entity = new CapabilityEntity(1L, "Backend", "Desc");
        Capability domain = new Capability(1L, "Backend", "Desc", null);
        CapabilityTechnologyEntity ct = new CapabilityTechnologyEntity(1L, 10L);
        ct.setId(1L);

        when(capabilityRepository.findAllByNameAsc(10, 0)).thenReturn(Flux.just(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);
        when(capTechRepository.findByCapabilityId(1L)).thenReturn(Flux.just(ct));
        when(technologyServicePort.findById(10L)).thenReturn(Mono.just(new Technology(10L, "Java")));

        StepVerifier.create(adapter.findAll(0, 10, "name", true))
                .expectNextMatches(c -> c.getTechnologies() != null && c.getTechnologies().size() == 1)
                .verifyComplete();
    }

    @Test
    @DisplayName("findAll por techCount descendente")
    void findAll_ByTechCountDesc() {
        CapabilityEntity entity = new CapabilityEntity(1L, "Backend", "Desc");
        Capability domain = new Capability(1L, "Backend", "Desc", null);

        when(capabilityRepository.findAllByTechCountDesc(10, 0)).thenReturn(Flux.just(entity));
        when(mapper.toDomain(entity)).thenReturn(domain);
        when(capTechRepository.findByCapabilityId(1L)).thenReturn(Flux.empty());

        StepVerifier.create(adapter.findAll(0, 10, "technologyCount", false))
                .expectNextMatches(c -> c.getTechnologies() != null && c.getTechnologies().isEmpty())
                .verifyComplete();
    }

    @Test
    @DisplayName("saveCapabilityTechnologies - debe guardar las relaciones")
    void saveCapabilityTechnologies_ShouldSave() {
        when(capTechRepository.save(any(CapabilityTechnologyEntity.class)))
                .thenAnswer(inv -> Mono.just(inv.getArgument(0)));

        StepVerifier.create(adapter.saveCapabilityTechnologies(1L, List.of(10L, 20L, 30L)))
                .verifyComplete();
    }
}
