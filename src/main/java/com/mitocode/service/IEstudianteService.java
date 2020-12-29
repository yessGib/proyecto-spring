package com.mitocode.service;

import com.mitocode.model.Estudiante;

import reactor.core.publisher.Flux;

public interface IEstudianteService extends ICRUD<Estudiante, String> {

	Flux<Estudiante> listarOrdenado();
}
