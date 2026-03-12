package com.bootcamp.capacidad.domain.usecase;

import com.bootcamp.capacidad.domain.exception.CapabilityAlreadyExistsException;
import com.bootcamp.capacidad.domain.model.Capability;
import com.bootcamp.capacidad.domain.model.Technology;
import com.bootcamp.capacidad.domain.spi.ICapabilityPersistencePort;
import com.bootcamp.capacidad.domain.spi.ITechnologyServicePort;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CapabilityUseCaseTest {

    @Mock
    private ICapabilityPersistencePort persistencePort;

    @Mock
    private ITechnologyServicePort technologyServicePort;

    @InjectMocks
    private CapabilityUseCase useCase;

    private Capability validCapability;
    private List<Technology> validTechnologies;

    @BeforeEach
    void setUp() {
        validTechnologies = Arrays.asList(
                new Technology(1L, "Java"),
                new Technology(2L, "Spring Boot"),
                new Technology(3L, "Mockito")
        );
        validCapability = new Capability(null, "Backend Developer", "Desarrollo de servicios backend", validTechnologies);
    }

    @Nested
    @DisplayName("save() tests")
    class SaveTests {

        @Test
        @DisplayName("Debe guardar capacidad cuando los datos son válidos y el nombre no existe")
        void save_WhenValid_ShouldSave() {
            Capability saved = new Capability(1L, "Backend Developer", "Desarrollo de servicios backend", null);

            when(persistencePort.existsByName("Backend Developer")).thenReturn(Mono.just(false));
            when(technologyServicePort.findById(anyLong())).thenAnswer(inv -> {
                Long id = inv.getArgument(0);
                return Mono.just(new Technology(id, "Tech" + id));
            });
            when(persistencePort.save(any())).thenReturn(Mono.just(saved));
            when(persistencePort.saveCapabilityTechnologies(eq(1L), anyList())).thenReturn(Mono.empty());

            StepVerifier.create(useCase.save(validCapability))
                    .expectNextMatches(c -> c.getId().equals(1L))
                    .verifyComplete();

            verify(persistencePort).existsByName("Backend Developer");
            verify(persistencePort).save(any());
        }

        @Test
        @DisplayName("Debe lanzar error cuando el nombre ya existe")
        void save_WhenNameExists_ShouldThrow() {
            when(persistencePort.existsByName("Backend Developer")).thenReturn(Mono.just(true));

            StepVerifier.create(useCase.save(validCapability))
                    .expectError(CapabilityAlreadyExistsException.class)
                    .verify();

            verify(persistencePort, never()).save(any());
        }

        @Test
        @DisplayName("Debe lanzar error cuando el nombre es nulo")
        void save_WhenNameNull_ShouldThrow() {
            validCapability.setName(null);

            StepVerifier.create(useCase.save(validCapability))
                    .expectErrorMatches(e -> e instanceof IllegalArgumentException
                            && e.getMessage().contains("nombre es obligatorio"))
                    .verify();
        }

        @Test
        @DisplayName("Debe lanzar error cuando el nombre está vacío")
        void save_WhenNameBlank_ShouldThrow() {
            validCapability.setName("  ");

            StepVerifier.create(useCase.save(validCapability))
                    .expectError(IllegalArgumentException.class)
                    .verify();
        }

        @Test
        @DisplayName("Debe lanzar error cuando la descripción es nula")
        void save_WhenDescriptionNull_ShouldThrow() {
            validCapability.setDescription(null);

            StepVerifier.create(useCase.save(validCapability))
                    .expectErrorMatches(e -> e instanceof IllegalArgumentException
                            && e.getMessage().contains("descripción es obligatoria"))
                    .verify();
        }

        @Test
        @DisplayName("Debe lanzar error cuando no hay tecnologías")
        void save_WhenNoTechnologies_ShouldThrow() {
            validCapability.setTechnologies(null);

            StepVerifier.create(useCase.save(validCapability))
                    .expectErrorMatches(e -> e instanceof IllegalArgumentException
                            && e.getMessage().contains("tecnologías son obligatorias"))
                    .verify();
        }

        @Test
        @DisplayName("Debe lanzar error cuando hay menos de 3 tecnologías")
        void save_WhenLessThan3Technologies_ShouldThrow() {
            validCapability.setTechnologies(Arrays.asList(
                    new Technology(1L, "Java"),
                    new Technology(2L, "Spring")
            ));

            StepVerifier.create(useCase.save(validCapability))
                    .expectErrorMatches(e -> e instanceof IllegalArgumentException
                            && e.getMessage().contains("mínimo 3"))
                    .verify();
        }

        @Test
        @DisplayName("Debe lanzar error cuando hay más de 20 tecnologías")
        void save_WhenMoreThan20Technologies_ShouldThrow() {
            List<Technology> tooMany = LongStream.rangeClosed(1, 21)
                    .mapToObj(i -> new Technology(i, "Tech" + i))
                    .collect(Collectors.toList());
            validCapability.setTechnologies(tooMany);

            StepVerifier.create(useCase.save(validCapability))
                    .expectErrorMatches(e -> e instanceof IllegalArgumentException
                            && e.getMessage().contains("máximo 20"))
                    .verify();
        }

        @Test
        @DisplayName("Debe lanzar error cuando hay tecnologías duplicadas")
        void save_WhenDuplicateTechnologies_ShouldThrow() {
            validCapability.setTechnologies(Arrays.asList(
                    new Technology(1L, "Java"),
                    new Technology(1L, "Java"),
                    new Technology(2L, "Spring")
            ));

            StepVerifier.create(useCase.save(validCapability))
                    .expectErrorMatches(e -> e instanceof IllegalArgumentException
                            && e.getMessage().contains("repetidas"))
                    .verify();
        }

        @Test
        @DisplayName("Debe guardar con exactamente 3 tecnologías (boundary)")
        void save_WithExactly3Technologies_ShouldSave() {
            Capability saved = new Capability(1L, "Backend Developer", "Desc", null);

            when(persistencePort.existsByName(anyString())).thenReturn(Mono.just(false));
            when(technologyServicePort.findById(anyLong())).thenAnswer(inv ->
                    Mono.just(new Technology(inv.getArgument(0), "T")));
            when(persistencePort.save(any())).thenReturn(Mono.just(saved));
            when(persistencePort.saveCapabilityTechnologies(anyLong(), anyList())).thenReturn(Mono.empty());

            StepVerifier.create(useCase.save(validCapability))
                    .expectNextCount(1)
                    .verifyComplete();
        }

        @Test
        @DisplayName("Debe guardar con exactamente 20 tecnologías (boundary)")
        void save_WithExactly20Technologies_ShouldSave() {
            List<Technology> techs20 = LongStream.rangeClosed(1, 20)
                    .mapToObj(i -> new Technology(i, "Tech" + i))
                    .collect(Collectors.toList());
            validCapability.setTechnologies(techs20);

            Capability saved = new Capability(1L, "Backend Developer", "Desc", null);

            when(persistencePort.existsByName(anyString())).thenReturn(Mono.just(false));
            when(technologyServicePort.findById(anyLong())).thenAnswer(inv ->
                    Mono.just(new Technology(inv.getArgument(0), "T")));
            when(persistencePort.save(any())).thenReturn(Mono.just(saved));
            when(persistencePort.saveCapabilityTechnologies(anyLong(), anyList())).thenReturn(Mono.empty());

            StepVerifier.create(useCase.save(validCapability))
                    .expectNextCount(1)
                    .verifyComplete();
        }
    }

    @Nested
    @DisplayName("findAll() tests")
    class FindAllTests {

        @Test
        @DisplayName("Debe retornar capacidades ordenadas por nombre ascendente")
        void findAll_ByNameAsc_ShouldReturn() {
            Capability c1 = new Capability(1L, "Backend", "Desc", List.of());
            Capability c2 = new Capability(2L, "Frontend", "Desc", List.of());

            when(persistencePort.findAll(0, 10, "name", true)).thenReturn(Flux.just(c1, c2));

            StepVerifier.create(useCase.findAll(0, 10, "name", true))
                    .expectNext(c1)
                    .expectNext(c2)
                    .verifyComplete();
        }

        @Test
        @DisplayName("Debe retornar capacidades ordenadas por cantidad de tecnologías")
        void findAll_ByTechCount_ShouldReturn() {
            Capability c1 = new Capability(1L, "Backend", "Desc", List.of(new Technology(1L, "Java"), new Technology(2L, "Spring"), new Technology(3L, "Mockito")));

            when(persistencePort.findAll(0, 10, "technologyCount", false)).thenReturn(Flux.just(c1));

            StepVerifier.create(useCase.findAll(0, 10, "technologyCount", false))
                    .expectNextMatches(c -> c.getTechnologies().size() == 3)
                    .verifyComplete();
        }

        @Test
        @DisplayName("Debe retornar vacío cuando no hay capacidades")
        void findAll_WhenEmpty_ShouldReturnEmpty() {
            when(persistencePort.findAll(0, 10, "name", true)).thenReturn(Flux.empty());

            StepVerifier.create(useCase.findAll(0, 10, "name", true))
                    .verifyComplete();
        }
    }
}
