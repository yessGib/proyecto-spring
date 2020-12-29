package com.mitocode.repo;

import java.time.LocalDate;

import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Order;
import org.springframework.data.mongodb.repository.Query;

import com.mitocode.model.Estudiante;
import com.mitocode.model.Matricula;

import reactor.core.publisher.Flux;


public interface IEstudianteRepo extends IGenericRepo<Estudiante, String>{
	

}
