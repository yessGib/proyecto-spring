package com.mitocode.repo;

import java.util.List;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.mitocode.model.Plato;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface IPlatoRepo extends ReactiveMongoRepository<Plato, String>{
	
	
}
