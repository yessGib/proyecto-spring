package com.mitocode.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.validation.BeanPropertyBindingResult;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import com.mitocode.dto.ValidacionDTO;
import com.mitocode.model.Cliente;
import com.mitocode.service.IClienteService;
import com.mitocode.validators.RequestValidator;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import static org.springframework.web.reactive.function.BodyInserters.fromValue;

import java.net.URI;


@Component // ya que no lo usaré ni como controller ni como service ni nada asi
public class ClienteHandler {
	//servicios funcionales es un cambio de sintaxis
	@Autowired
	private IClienteService service;
	
	@Autowired
	private Validator validator;
	
	@Autowired
	private RequestValidator validatorGeneral;
	
	public Mono<ServerResponse> listar(ServerRequest req){
		return ServerResponse
				.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(service.listar(), Cliente.class);
	}
	
	public Mono<ServerResponse> listarPorId(ServerRequest req){
		String id = req.pathVariable("id");
		return service.listarPorId(id) 			// se pone así y no como listar ya que no puede ver id dentro del procesamiento solo si fuera una variable global
				.flatMap(p -> ServerResponse
					.ok()
					.contentType(MediaType.APPLICATION_JSON)
					.body(fromValue(p))			// se tiene que eniar con froValue
				)
				.switchIfEmpty(ServerResponse.notFound().build()); // en servicios funcionales no fucniona en defaultifempty
	}
	/*
	public Mono<ServerResponse> registrar(ServerRequest req){
		Mono<Cliente> monoCliente = req.bodyToMono(Cliente.class);	
		
		return monoCliente
				.flatMap(service::registrar) 
				.flatMap(p -> ServerResponse.created(URI.create(req.uri().toString().concat("/").concat(p.getId())))
						.contentType(MediaType.APPLICATION_JSON)
						.body(fromValue(p))	
					);
	}
	*/
	
	public Mono<ServerResponse> registrar(ServerRequest req){
		Mono<Cliente> monoCliente = req.bodyToMono(Cliente.class);	
		/*//modo laro de validar
		return monoCliente
				.flatMap(p -> {
					Errors errores = new BeanPropertyBindingResult(p, Cliente.class.getName());
					validator.validate(p, errores);
					if (errores.hasErrors()) {
						return Flux.fromIterable(errores.getFieldErrors())                  // return errores.getFieldErrors().forEach(Mono flux);
								.map(error -> new ValidacionDTO(error.getField(), error.getDefaultMessage()))
								.collectList()
								.flatMap(listaErrores -> {
									return ServerResponse.badRequest()
											.contentType(MediaType.APPLICATION_JSON)
											.body(fromValue(listaErrores));
										}
								);
					}else {
						return service.registrar(p)
								.flatMap(pdb -> ServerResponse
								.created(URI.create(req.uri().toString().concat("/").concat(p.getId())))
								.contentType(MediaType.APPLICATION_JSON)
								.body(fromValue(pdb))
								);
						}
					});
		*/
		return monoCliente
				.flatMap(validatorGeneral::validate)
				.flatMap(service::registrar)
				.flatMap(p -> ServerResponse.created(URI.create(req.uri().toString().concat("/").concat(p.getId())))
						.contentType(MediaType.APPLICATION_JSON)
						.body(fromValue(p))
						);
		
	}
	public Mono<ServerResponse> modificar(ServerRequest req){
		Mono<Cliente> monoCliente = req.bodyToMono(Cliente.class);	
		Mono<Cliente> monoBD = service.listarPorId(req.pathVariable("id"));
		
		return monoBD
				.zipWith(monoCliente, (bd, p) -> {
					bd.setId(p.getId());
					bd.setNombres(p.getNombres());
					bd.setApellidos(p.getApellidos());
					bd.setFechaNac(p.getFechaNac());
					bd.setUrlFoto(p.getUrlFoto());
					return bd;
				})
				//.flatMap(validadorGeneral::validate)
				.flatMap(service::modificar)
				.flatMap(p -> ServerResponse.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.body(fromValue(p))
						)
				.switchIfEmpty(ServerResponse.notFound().build());
	}
	
	public Mono<ServerResponse> eliminar(ServerRequest req){
		String id = req.pathVariable("id");
		return service.listarPorId(id) 			// se pone así y no como listar ya que no puede ver id dentro del procesamiento solo si fuera una variable global
				.flatMap(p -> service.eliminar(p.getId())
						.then(ServerResponse.noContent().build())
				)
				.switchIfEmpty(ServerResponse.notFound().build()); 
	}
}
