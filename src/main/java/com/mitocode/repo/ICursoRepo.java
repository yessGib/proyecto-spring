package com.mitocode.repo;

import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

import com.mitocode.model.Curso;

public interface ICursoRepo extends ReactiveMongoRepository<Curso, String>{
	
	
}
