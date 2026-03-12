package com.bootcamp.capacidad.infrastructure.configuration.bean;

import com.bootcamp.capacidad.domain.api.ICapabilityUseCasePort;
import com.bootcamp.capacidad.domain.spi.ICapabilityPersistencePort;
import com.bootcamp.capacidad.domain.spi.ITechnologyServicePort;
import com.bootcamp.capacidad.domain.usecase.CapabilityUseCase;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BeanConfiguration {

    @Bean
    public ICapabilityUseCasePort capabilityUseCasePort(ICapabilityPersistencePort persistencePort,
                                                        ITechnologyServicePort technologyServicePort) {
        return new CapabilityUseCase(persistencePort, technologyServicePort);
    }
}
