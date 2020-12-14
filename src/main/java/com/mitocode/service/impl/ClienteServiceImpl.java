package com.mitocode.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.mitocode.model.Cliente;
import com.mitocode.repo.IClienteRepo;
import com.mitocode.repo.IGenericRepo;
import com.mitocode.repo.IPlatoRepo;
import com.mitocode.service.IClienteService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service //estereotipo usado en el caso del repositorio (DAO) no se agrega @Repository ya que hereda de una clase que ya lo es
public class ClienteServiceImpl  extends CRUDImpl<Cliente, String> implements IClienteService{

	@Autowired
	private IClienteRepo repo;
	
	@Override
	protected IGenericRepo<Cliente, String> getRepo() {

		return repo;
	}
	

}
