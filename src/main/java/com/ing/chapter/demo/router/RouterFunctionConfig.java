package com.ing.chapter.demo.router;

import com.ing.chapter.demo.handler.SampleHandlerFunction;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;

@Configuration
public class RouterFunctionConfig {


    @Bean
    public RouterFunction<ServerResponse> route(SampleHandlerFunction handlerFunction){

        return RouterFunctions
                .route(GET("/functional/flux").and(accept(MediaType.APPLICATION_JSON)),
                        handlerFunction::flux)
               /* (request) ->  ServerResponse.ok()
                                .contentType(MediaType.APPLICATION_JSON)
                                .body(
                                        Flux.just(1, 2, 3, 8)
                                                .log(), Integer.class
                                ))*/


                .andRoute(GET("/functional/mono").and(accept(MediaType.APPLICATION_JSON)),
                        handlerFunction::mono);

    }


}
