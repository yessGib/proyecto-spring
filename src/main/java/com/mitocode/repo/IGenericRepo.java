package com.mitocode.repo;

import java.util.List;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.mitocode.model.Cliente;
import com.mitocode.model.Plato;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@NoRepositoryBean // se le dice que solo es una interfaz de apoyo
public interface IGenericRepo<T, ID> extends ReactiveMongoRepository<T, ID>{
	
	
}
