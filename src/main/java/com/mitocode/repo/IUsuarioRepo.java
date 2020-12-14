package com.mitocode.repo;

import com.mitocode.model.Usuario;

import reactor.core.publisher.Mono;

public interface IUsuarioRepo extends IGenericRepo<Usuario, String>{
	
	Mono<Usuario> findOneByUsuario(String usuario); //findByUsuario : busca si alguno coincide con el que busco  findOneByUsuario: solo una coincidencia
}
