package com.bootcamp.capacidad.infrastructure.output.mysql.repository;

import com.bootcamp.capacidad.infrastructure.output.mysql.entity.CapabilityEntity;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ICapabilityRepository extends ReactiveCrudRepository<CapabilityEntity, Long> {

    Mono<Boolean> existsByName(String name);

    @Query("SELECT * FROM capacidades ORDER BY nombre ASC LIMIT :limit OFFSET :offset")
    Flux<CapabilityEntity> findAllByNameAsc(int limit, int offset);

    @Query("SELECT * FROM capacidades ORDER BY nombre DESC LIMIT :limit OFFSET :offset")
    Flux<CapabilityEntity> findAllByNameDesc(int limit, int offset);

    @Query("""
            SELECT c.* FROM capacidades c
            LEFT JOIN capacidad_tecnologias ct ON c.id = ct.id_capacidad
            GROUP BY c.id, c.nombre, c.descripcion
            ORDER BY COUNT(ct.id_tecnologia) ASC
            LIMIT :limit OFFSET :offset
            """)
    Flux<CapabilityEntity> findAllByTechCountAsc(int limit, int offset);

    @Query("""
            SELECT c.* FROM capacidades c
            LEFT JOIN capacidad_tecnologias ct ON c.id = ct.id_capacidad
            GROUP BY c.id, c.nombre, c.descripcion
            ORDER BY COUNT(ct.id_tecnologia) DESC
            LIMIT :limit OFFSET :offset
            """)
    Flux<CapabilityEntity> findAllByTechCountDesc(int limit, int offset);
}
