package com.mitocode.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mitocode.model.Plato;
import com.mitocode.repo.IPlatoRepo;
import com.mitocode.service.IPlatoService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service //estereotipo usado en el caso del repositorio (DAO) no se agrega @Repository ya que hereda de una clase que ya lo es
public class PlatoServiceImpl implements IPlatoService{
	
	@Autowired
	private IPlatoRepo repo;
	// el service es donde va la lógica de negocio, service es una palabra reservada 
	
	//las clases save(),findAll, FindById(), deleteById(), etc vienen de la clase ReactiveMongoRepository
	@Override
	public Mono<Plato> registrar(Plato p) {
		// metodo save sirva para el insert o para el update, si del objeto plato que se le envia 
		//si encuentra un id lo toma como modificación y si no encuentra un id lo toma como inserción
		return repo.save(p);
	}

	@Override
	public Mono<Plato> modificar(Plato p) {
		return repo.save(p);
	}

	@Override
	public Flux<Plato> listar() {
		// TODO Auto-generated method stub
		return repo.findAll();
	}

	@Override
	public Mono<Plato> listarPorId(String id) {
		// TODO Auto-generated method stub
		return repo.findById(id);
	}

	@Override
	public Mono<Void> eliminar(String id) {
		// TODO Auto-generated method stub
		return repo.deleteById(id);
	}

}
