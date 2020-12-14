package com.mitocode.service.impl;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;

import com.mitocode.pagination.PageSupport;
import com.mitocode.repo.IGenericRepo;
import com.mitocode.repo.IPlatoRepo;
import com.mitocode.service.ICRUD;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public abstract class CRUDImpl<T,ID> implements ICRUD<T, ID> {


	protected abstract IGenericRepo<T,ID> getRepo();
	@Override
	public Mono<T> registrar(T t) {
		return getRepo().save(t);
	}

	@Override
	public Mono<T> modificar(T t) {
		return getRepo().save(t);
	}

	@Override
	public Flux<T> listar() {
		return getRepo().findAll();
	}

	@Override
	public Mono<T> listarPorId(ID id) {
		return getRepo().findById(id);
	}

	@Override
	public Mono<Void> eliminar(ID id) {
		return getRepo().deleteById(id);
	}
	
	public Mono<PageSupport<T>> listarPage(Pageable page) {
		// mongo es commo si se ejecutara la siguiente: db.getCollection('platos').find().skip(1).limit(2)  // skip es de donde comienza y limit cuantos quiero ver 
		return getRepo().findAll()  //devuelve flux
				.collectList()		//devuelve los elementos de findAll como un mono
				.map(list -> new PageSupport<>(
						list												//content
						.stream()
						.skip(page.getPageNumber() * page.getPageSize())
						.limit(page.getPageSize())
						.collect(Collectors.toList())
						,
						page.getPageNumber()								//pageNumber
						,
						page.getPageSize()									//pageSize
						,
						list.size()											 //totalElements
						));
		
	}
}
