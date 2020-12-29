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

import com.mitocode.model.Matricula;
import com.mitocode.service.IMatriculaService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


@RestController
@RequestMapping("/matriculas")
public class MatriculaController {

	@Autowired
	private IMatriculaService service;

	@GetMapping
	public Mono<ResponseEntity<Flux<Matricula>>> listar() {
		Flux<Matricula> fxMatriculas = service.listar();
		return Mono.just(ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(fxMatriculas));
	}

	@GetMapping("/{id}")
	public Mono<ResponseEntity<Matricula>> listarPorId(@PathVariable("id") String id) {
		return service.listarPorId(id)
				.map(m -> ResponseEntity.ok()
						.contentType(MediaType.APPLICATION_JSON) 
						.body(m)
				).defaultIfEmpty(ResponseEntity.notFound().build());
	}

	@PostMapping
	public Mono<ResponseEntity<Matricula>> registrar(@Valid @RequestBody Matricula matricula, final ServerHttpRequest req) {
		return service.registrar(matricula)
				.map(m -> ResponseEntity.created(URI.create(req.getURI().toString().concat("/").concat(m.getId()))) 
						.contentType(MediaType.APPLICATION_JSON).body(m));
	}

	@PutMapping("/{id}")
	public Mono<ResponseEntity<Matricula>> modificar(@Valid @RequestBody Matricula Matricula, @PathVariable("id") String id) {
		Mono<Matricula> monoMatricula = Mono.just(Matricula);
		Mono<Matricula> monodb = service.listarPorId(id);

		return monodb.zipWith(monoMatricula, (bd, m) -> {
			bd.setId(id);
			bd.setEstudiante(m.getEstudiante());
			bd.setEstado(m.getEstado());
			bd.setItems(m.getItems());
			return bd; 
		}).flatMap(service::modificar) 
				.map(m -> ResponseEntity.ok()
						.contentType(MediaType.APPLICATION_JSON).body(m))
				.defaultIfEmpty(new ResponseEntity<Matricula>(HttpStatus.NOT_FOUND)); 
	}

	@DeleteMapping("/{id}")
	public Mono<ResponseEntity<Void>> eliminar(@PathVariable("id") String id) {
		return service.listarPorId(id).flatMap(m -> {
			return service.eliminar(m.getId()).then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)));
		}).defaultIfEmpty(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
	}

	
	

}
