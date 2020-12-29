package com.mitocode.repo;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean // se le dice que solo es una interfaz de apoyo
public interface IGenericRepo<T, ID> extends ReactiveMongoRepository<T, ID>{
	
	
}
