package com.mitocode.controller;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mitocode.dto.FiltroDTO;
import com.mitocode.model.Estudiante;
import com.mitocode.service.IEstudianteService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


//import 	org.springframework.web.reactive.function.client.WebClient;
@RestController
@RequestMapping("/estudiantes")
public class EstudianteController {

	@Autowired
	private IEstudianteService service;

	@GetMapping
	public Mono<ResponseEntity<Flux<Estudiante>>> listar() {
		Flux<Estudiante> fxEstudiantes = service.listar();
		return Mono.just(ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(fxEstudiantes));
	}

	@GetMapping("/{id}")
	public Mono<ResponseEntity<Estudiante>> listarPorId(@PathVariable("id") String id) {
		return service.listarPorId(id) 
				.map(e -> ResponseEntity.ok() 
						.contentType(MediaType.APPLICATION_JSON) 
						.body(e) 
				).defaultIfEmpty(ResponseEntity.notFound().build());
	}

	@PostMapping
	public Mono<ResponseEntity<Estudiante>> registrar(@Valid @RequestBody Estudiante estudiante, final ServerHttpRequest req) {
		return service.registrar(estudiante)
				.map(p -> ResponseEntity.created(URI.create(req.getURI().toString().concat("/").concat(p.getId()))) 
						.contentType(MediaType.APPLICATION_JSON).body(p));
	}

	@PutMapping("/{id}")
	public Mono<ResponseEntity<Estudiante>> modificar(@Valid @RequestBody Estudiante estudiante, @PathVariable("id") String id) {
		Mono<Estudiante> monoCliente = Mono.just(estudiante); 
		Mono<Estudiante> monodb = service.listarPorId(id); 

		return monodb.zipWith(monoCliente, (bd, e) -> { 
			bd.setId(id);
			bd.setNombres(e.getNombres());
			bd.setApellidos(e.getApellidos());
			bd.setDni(e.getDni());
			bd.setEdad(e.getEdad());
			return bd; 
		}).flatMap(service::modificar) 
				.map(e -> ResponseEntity.ok() 
						.contentType(MediaType.APPLICATION_JSON).body(e))
				.defaultIfEmpty(new ResponseEntity<Estudiante>(HttpStatus.NOT_FOUND));
	}

	@DeleteMapping("/{id}")
	public Mono<ResponseEntity<Void>> eliminar(@PathVariable("id") String id) { 
		return service.listarPorId(id).flatMap(e -> {
			return service.eliminar(e.getId()).then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)));
		}).defaultIfEmpty(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
	}


	@GetMapping("/ordenado")
	public Mono<ResponseEntity<Flux<Estudiante>>> ListarDescendente(){
		Flux<Estudiante> fxEstudiante = service.listarOrdenado();
		
		return Mono.just(ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(fxEstudiante)
				);
		
	}
}
