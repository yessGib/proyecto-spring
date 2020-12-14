package com.mitocode;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;

import com.mitocode.model.Cliente;
import com.mitocode.model.Plato;

import reactor.core.publisher.Mono;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
class SpringReactorApplicationTests {
	// ver: https://www.youtube.com/watch?v=mIj2HDcnLBM&ab_channel=UniversitatPolit%C3%A8cnicadeVal%C3%A8ncia-UPV
	@Autowired
	private WebTestClient clienteWeb;
	
	@Test
	void listarTest() {
		clienteWeb.get()
		.uri("/platos")
		.exchange()
		.expectStatus().isOk()
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		.expectBodyList(Cliente.class)
		.hasSize(2);
	}

	@Test
	void registrarTest() {
		Plato p = new Plato();
		p.setNombre("Ceviche");
		p.setPrecio(20.00);
		p.setEstado(true);
		
		clienteWeb.post()
		.uri("/platos")
		.body(Mono.just(p),Plato.class)
		.exchange()
		.expectStatus().isCreated()
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		.expectBody()
		.jsonPath("$.nombre").isNotEmpty()
		.jsonPath("$.precio").isNumber();
	}
	
	@Test
	void modificarTest() {
		Plato p = new Plato();
		p.setId("5fd19d98d6dca41fca71ff5a");
		p.setNombre("Ceviche");
		p.setPrecio(30.00);
		p.setEstado(true);
		
		clienteWeb.put()
		.uri("/platos/"+p.getId())
		.body(Mono.just(p),Plato.class)
		.exchange()
		.expectStatus().isOk()
		.expectHeader().contentType(MediaType.APPLICATION_JSON)
		.expectBody()
		.jsonPath("$.nombre").isNotEmpty()
		.jsonPath("$.precio").isNumber();
	}
	
	@Test
	void eliminarTest() {
		Plato p = new Plato();
		p.setId("5fd19d98d6dca41fca71ff5a");
		
		clienteWeb.delete()
		.uri("/platos/"+p.getId())
		.exchange()
		.expectStatus().isNoContent();
	}
}
