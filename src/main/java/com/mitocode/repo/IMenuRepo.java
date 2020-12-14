package com.mitocode.repo;

import org.springframework.data.mongodb.repository.Query;

import com.mitocode.model.Menu;

import reactor.core.publisher.Flux;

public interface IMenuRepo extends IGenericRepo<Menu, String> {
	
	@Query("{'roles' : { $in: ?0 }}") // busoc en roles  le paso toda la osta de roles para que busque en esa lista si tiene el permiso
	Flux<Menu> obtenerMenus(String[] roles);

}
