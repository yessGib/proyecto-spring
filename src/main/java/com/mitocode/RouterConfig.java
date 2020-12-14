package com.mitocode;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.mitocode.handler.ClienteHandler;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET; // se pone static para evitar escribir  route(RequestPredicates.GET(
import static org.springframework.web.reactive.function.server.RequestPredicates.POST;
import static org.springframework.web.reactive.function.server.RequestPredicates.PUT;
import static org.springframework.web.reactive.function.server.RequestPredicates.DELETE;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
@Configuration
public class RouterConfig {
	//Servicios funcionales
	@Bean
	public RouterFunction<ServerResponse> rutas(ClienteHandler handler){ //solo existe en webflux
	//	return route(GET("v2/clientes"), handler::listar) // es igual a este método de referencia  req -> handler.listar(req)
		return route(GET("v2/clientes"), handler::listar)
				.andRoute(GET("v2/clientes/{id}"), handler::listarPorId)
				.andRoute(POST("v2/clientes/"), handler::registrar)
				.andRoute(PUT("v2/clientes/{id}"), handler::modificar)
				.andRoute(DELETE("v2/clientes/{id}"), handler::eliminar); 
	}
}
