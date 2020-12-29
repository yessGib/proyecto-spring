package com.mitocode.service.impl;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import com.mitocode.dto.FiltroDTO;
import com.mitocode.model.Estudiante;
import com.mitocode.model.Matricula;
import com.mitocode.repo.IEstudianteRepo;
import com.mitocode.repo.IGenericRepo;
import com.mitocode.service.IEstudianteService;

import reactor.core.publisher.Flux;

@Service 
public class EstudianteServiceImpl  extends CRUDImpl<Estudiante, String> implements IEstudianteService{

	@Autowired
	private IEstudianteRepo repo;
	
	@Override
	protected IGenericRepo<Estudiante, String> getRepo() {

		return repo;
	}
	

	/*@Override
	public Flux<Estudiante> obtenerListaOrdenada() {
		Sort.Order order = new Sort.Order(Direction.DESC,"edad");
		List<Sort.Order> orders = new ArrayList<>();
		orders.add(order);
		

		
		return repo.obtenerListaOrdenada(order);
	}*/
	

	@Override
	public Flux<Estudiante> listarOrdenado() {
		// TODO Auto-generated method stub
		Sort.Order order = new Sort.Order(Direction.DESC,"edad");
		List<Sort.Order> orders = new ArrayList<>();
		orders.add(order);
		return repo.findAll(Sort.by(orders));

	}
}
