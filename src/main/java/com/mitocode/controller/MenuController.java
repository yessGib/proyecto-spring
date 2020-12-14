package com.mitocode.controller;

import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.ReactiveSecurityContextHolder;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mitocode.model.Menu;
import com.mitocode.service.IMenuService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/menus")
public class MenuController {

	@Autowired	
	private IMenuService service;
	
	@GetMapping
	public Mono<ResponseEntity<Flux<Menu>>> listar(){			// para lostar las opciones de menpu del usuario que se logueo
		return ReactiveSecurityContextHolder.getContext()		//
				.map(SecurityContext::getAuthentication)		//obtengo al usuario logueado
				.map(Authentication::getAuthorities)			//obtener roles
				.map(roles -> {
					String rolesString = roles.stream().map(Object::toString).collect(Collectors.joining(",")); //se tiene que hacer un stream de roles no una lista. A vada elemento del arreglo le concatena una ,  lo convierte a stream ej ADMIN,USER,DBA
					String[] strings = rolesString.split(",");
					return service.obtenerMenus(strings);
				})
				.flatMap(fx -> {
					return Mono.just(ResponseEntity.ok()
							.contentType(MediaType.APPLICATION_JSON)
							.body(fx));
				});	
	}
	
}
