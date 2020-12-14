package com.mitocode.service;

import com.mitocode.model.Plato;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IPlatoService {
// como primera parte se ponen aqui las interfaces de los métodos, cuando se crea la clase IClienteService se agregará una clase ICRUD de la cual extenderán IPlatoService e ICleinte service ya que usna los mismo métodos
// debería extender de ICRUD y quitar estos mpetodos de aquí per por practica lo dejaré
	
	Mono<Plato> registrar (Plato p);
	Mono<Plato> modificar (Plato p);
	Flux<Plato> listar();
	Mono<Plato> listarPorId(String id);
	Mono<Void> eliminar(String id);
}
