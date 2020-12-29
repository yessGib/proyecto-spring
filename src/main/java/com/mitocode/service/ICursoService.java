package com.mitocode.service;

import com.mitocode.model.Curso;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ICursoService {
// como primera parte se ponen aqui las interfaces de los métodos, cuando se crea la clase IEstudianteService se agregará una clase ICRUD de la cual extenderán ICursoService e ICleinte service ya que usna los mismo métodos
// debería extender de ICRUD y quitar estos mpetodos de aquí per por practica lo dejaré
	
	Mono<Curso> registrar (Curso p);
	Mono<Curso> modificar (Curso p);
	Flux<Curso> listar();
	Mono<Curso> listarPorId(String id);
	Mono<Void> eliminar(String id);
}
