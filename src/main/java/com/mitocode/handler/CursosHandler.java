package com.mitocode.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;


import com.mitocode.model.Estudiante;
import com.mitocode.service.IEstudianteService;
import com.mitocode.validators.RequestValidator;


import reactor.core.publisher.Mono;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;

import java.net.URI;


@Component 
public class CursosHandler {

	@Autowired
	private IEstudianteService service;
	

	
	@Autowired
	private RequestValidator validatorGeneral;
	
	public Mono<ServerResponse> listar(ServerRequest req){
		return ServerResponse
				.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(service.listar(), Estudiante.class);
	}
	
	public Mono<ServerResponse> listarPorId(ServerRequest req){
		String id = req.pathVariable("id");
		return service.listarPorId(id) 		
				.flatMap(p -> ServerResponse
					.ok()
					.contentType(MediaType.APPLICATION_JSON)
					.body(fromValue(p))		
				)
				.switchIfEmpty(ServerResponse.notFound().build());
	}
	
	
	public Mono<ServerResponse> registrar(ServerRequest req){
		Mono<Estudiante> monoCliente = req.bodyToMono(Estudiante.class);	
		
		return monoCliente
				.flatMap(validatorGeneral::validate)
				.flatMap(service::registrar)
				.flatMap(p -> ServerResponse.created(URI.create(req.uri().toString().concat("/").concat(p.getId())))
						.contentType(MediaType.APPLICATION_JSON)
						.body(fromValue(p))
						);
		
	}
	public Mono<ServerResponse> modificar(ServerRequest req){
		Mono<Estudiante> monoCliente = req.bodyToMono(Estudiante.class);	
		Mono<Estudiante> monoBD = service.listarPorId(req.pathVariable("id"));
		
		return monoBD
				.zipWith(monoCliente, (bd, p) -> {
					bd.setId(p.getId());
					bd.setNombres(p.getNombres());
					bd.setApellidos(p.getApellidos());
					bd.setDni(p.getDni());
					bd.setEdad(p.getEdad());
					return bd;
				})
				
				.flatMap(service::modificar)
				.flatMap(p -> ServerResponse.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.body(fromValue(p))
						)
				.switchIfEmpty(ServerResponse.notFound().build());
	}
	
	public Mono<ServerResponse> eliminar(ServerRequest req){
		String id = req.pathVariable("id");
		return service.listarPorId(id) 		
				.flatMap(p -> service.eliminar(p.getId())
						.then(ServerResponse.noContent().build())
				)
				.switchIfEmpty(ServerResponse.notFound().build()); 
	}
}
