package com.bootcamp.capacidad.infrastructure.input.rest.router;

import com.bootcamp.capacidad.infrastructure.input.rest.dto.CapabilityRequest;
import com.bootcamp.capacidad.infrastructure.input.rest.dto.CapabilityResponse;
import com.bootcamp.capacidad.infrastructure.input.rest.handler.CapabilityHandler;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.enums.ParameterIn;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.parameters.RequestBody;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import org.springdoc.core.annotations.RouterOperation;
import org.springdoc.core.annotations.RouterOperations;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.*;

@Configuration
public class CapabilityRouter {

    private static final String BASE_PATH = "/api/capabilities";

    @Bean
    @RouterOperations({
            @RouterOperation(
                    path = "/api/capabilities",
                    method = RequestMethod.POST,
                    beanClass = CapabilityHandler.class,
                    beanMethod = "save",
                    operation = @Operation(
                            operationId = "createCapability",
                            summary = "Registrar una nueva capacidad con tecnologías asociadas",
                            requestBody = @RequestBody(content = @Content(schema = @Schema(implementation = CapabilityRequest.class))),
                            responses = {
                                    @ApiResponse(responseCode = "201", description = "Capacidad creada",
                                            content = @Content(schema = @Schema(implementation = CapabilityResponse.class))),
                                    @ApiResponse(responseCode = "400", description = "Datos inválidos"),
                                    @ApiResponse(responseCode = "409", description = "La capacidad ya existe")
                            }
                    )
            ),
            @RouterOperation(
                    path = "/api/capabilities",
                    method = RequestMethod.GET,
                    beanClass = CapabilityHandler.class,
                    beanMethod = "findAll",
                    operation = @Operation(
                            operationId = "listCapabilities",
                            summary = "Listar capacidades paginadas y ordenadas por nombre o cantidad de tecnologías",
                            parameters = {
                                    @Parameter(name = "page", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "0")),
                                    @Parameter(name = "size", in = ParameterIn.QUERY, schema = @Schema(type = "integer", defaultValue = "10")),
                                    @Parameter(name = "sortBy", in = ParameterIn.QUERY, schema = @Schema(type = "string", defaultValue = "name", allowableValues = {"name", "technologyCount"})),
                                    @Parameter(name = "ascending", in = ParameterIn.QUERY, schema = @Schema(type = "boolean", defaultValue = "true"))
                            },
                            responses = @ApiResponse(responseCode = "200", description = "Lista de capacidades con tecnologías")
                    )
            )
    })
    public RouterFunction<ServerResponse> capabilityRoutes(CapabilityHandler handler) {
        return RouterFunctions
                .route(POST(BASE_PATH).and(accept(MediaType.APPLICATION_JSON)), handler::save)
                .andRoute(GET(BASE_PATH), handler::findAll);
    }
}
