package com.mitocode.repo;

import java.time.LocalDate;

import org.springframework.data.mongodb.repository.Query;

import com.mitocode.model.Matricula;

import reactor.core.publisher.Flux;


public interface IMatriculaRepo extends IGenericRepo<Matricula, String>{
	
	@Query("{'estudiante' : {_id: ?0  }}")
	Flux<Matricula> obtenerMatrículaPorEstudiante(String estudiante);
	
	@Query("{'creadoEn' : {$gte: ?0, $lt: ?1} }")
	Flux<Matricula> obtenerFacturasPorFecha(LocalDate fechaInicio, LocalDate fechaFin);//localDate por que no me interesa la hora 
}
