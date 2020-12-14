package com.mitocode.repo;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import org.springframework.data.repository.NoRepositoryBean;

import com.mitocode.model.Cliente;
import com.mitocode.model.Factura;
import com.mitocode.model.Plato;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;


public interface IFacturaRepo extends IGenericRepo<Factura, String>{
	
	@Query("{'cliente' : {_id: ?0  }}")
	Flux<Factura> obtenerFacturaPorCliente(String cliente);
	
	@Query("{'creadoEn' : {$gte: ?0, $lt: ?1} }") //Between 02-11-2020 and 04-11-2020 //?0 para declarar la variable 0 , gte(>=) = greater than equals,  gt = grater than (>), lt = lower than (<)
	Flux<Factura> obtenerFacturasPorFecha(LocalDate fechaInicio, LocalDate fechaFin);//localDate por que no me interesa la hora 
}
