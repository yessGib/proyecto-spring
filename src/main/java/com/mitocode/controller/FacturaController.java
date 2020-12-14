package com.mitocode.controller;

import java.net.URI;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.mitocode.dto.FiltroDTO;
import com.mitocode.model.Factura;
import com.mitocode.service.IFacturaService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;
import static reactor.function.TupleUtils.function;

//import 	org.springframework.web.reactive.function.client.WebClient;
@RestController
@RequestMapping("/facturas")
public class FacturaController {

	@Autowired
	private IFacturaService service;

	@GetMapping
	public Mono<ResponseEntity<Flux<Factura>>> listar() {
		Flux<Factura> fxFacturas = service.listar();
		return Mono.just(ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(fxFacturas));
	}

	@GetMapping("/{id}")
	public Mono<ResponseEntity<Factura>> listarPorId(@PathVariable("id") String id) {
		return service.listarPorId(id) // regresa Mono<Factura>
				.map(p -> ResponseEntity.ok() // del flujo anterior (service.listarPorId(id)), será p(aunque regresa un
												// Mono<Factura>, cuando pasaal map solo manda Factura por lo tanto p es
												// Factura) y se le aplica un proceso, se le dice que va a generar un
												// ResponseEntity con un ok. Lo que se pasa al siguiente como un
												// Mono<ResponseEntity>
						.contentType(MediaType.APPLICATION_JSON) // trabaja sobre Mono<ResponseEntity>
						.body(p) // p es Factura
				).defaultIfEmpty(ResponseEntity.notFound().build()); // defaultIfEmpty por si no encuentra un id y
																		// regresa el objeto vacío se mande el status
																		// correcto
	}

	@PostMapping
	public Mono<ResponseEntity<Factura>> registrar(@Valid @RequestBody Factura Factura, final ServerHttpRequest req) {// @RequestBody
																														// ransforma
																														// de
																														// json
																														// a
																														// java
		return service.registrar(Factura)
				.map(p -> ResponseEntity.created(URI.create(req.getURI().toString().concat("/").concat(p.getId()))) // .created()
																													// permite
																													// enviar
																													// el
																													// código
																													// correcto
																													// para
																													// indicar
																													// que
																													// el
																													// objeto
																													// se
																													// ha
																													// creado.
																													// Pide
																													// que
																													// se
																													// envíe
																													// un
																													// "located"
																													// lo
																													// cual
																													// es
																													// la
																													// url
																													// donde
																													// puedo
																													// localizar
																													// el
																													// recurso
																													// que
																													// acabo
																													// de
																													// crear
																													// p.e.:
																													// //localhost:8080/Facturas/11
						.contentType(MediaType.APPLICATION_JSON).body(p));
	}

	@PutMapping("/{id}") // con @Valid le dices que se respete si o si el @NotNull de Factura
	public Mono<ResponseEntity<Factura>> modificar(@Valid @RequestBody Factura Factura, @PathVariable("id") String id) {
		Mono<Factura> monoFactura = Mono.just(Factura); // datos a modificar
		Mono<Factura> monodb = service.listarPorId(id); // datos si existe en la bd

		return monodb.zipWith(monoFactura, (bd, p) -> { // lee los dos objetos y va setteando la información
			bd.setId(id);
			bd.setCliente(p.getCliente());
			bd.setDescripcion(p.getDescripcion());
			bd.setObservacion(p.getObservacion());
			bd.setItems(p.getItems());
			return bd; // en zipWith en el return debe regresar en primer elemento que se indico, en
						// este caso bd
						// cuando bd regresa, ya no regresa solo como bd si no como Mono<bd> por lo que
						// se le pueden seguir aplicando métodos de flujos
		}).flatMap(service::modificar) // ahora Mono<bd> (Mono<Factura>) se pasa al flatMap y este lo recibe como
										// FacturaNuevo de tipo Factura
										// se reomienda que cuando se use un método que venga de base de datos se use
										// flatMap
										// con procesos simples de transformacion se use map
										// .flatMap(FacturaNuevo -> service.modificar(FacturaNuevo)) <- esta es la
										// manera poco elegante de hacerlo
				.map(p -> ResponseEntity.ok() // .created() permite enviar el código correcto para indicar que el objeto
												// se ha creado. Pide que se envíe un "located" lo cual es la url donde
												// puedo localizar el recurso que acabo de crear p.e.:
												// //localhost:8080/Facturas/11
						.contentType(MediaType.APPLICATION_JSON).body(p))
				.defaultIfEmpty(new ResponseEntity<Factura>(HttpStatus.NOT_FOUND)); // es lo mismo que poner
																					// .defaultIfEmpty(ResponseEntity.notFound().build())
	}

	@DeleteMapping("/{id}")
	public Mono<ResponseEntity<Void>> eliminar(@PathVariable("id") String id) { // Se usa Mono ya qye esta en un
																				// contexto de weflux

		return service.listarPorId(id).flatMap(p -> {
			return service.eliminar(p.getId()).then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT))); // se
																													// usa
																													// para
																													// Mono,
																													// se
																													// usa
																													// cuando
																													// se
																													// quiere
																													// terminar
																													// todo
																													// (similar
																													// a
																													// break)
																													// y
																													// ntonces
																													// regresará
																													// algo
																													// del
																													// tipo
																													// que
																													// indique
																													// el
																													// método
																													// en
																													// este
																													// cso,
																													// tendría
																													// que
																													// regresar
																													// un
																													// Mono<ResponseEntity<Void>>
		}).defaultIfEmpty(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
	}

	private Factura FacturaHateoas;

	@GetMapping("/hateoas/{id}")
	public Mono<EntityModel<Factura>> listarHateoasPorId(@PathVariable("id") String id) {
		Mono<Link> link1 = linkTo(methodOn(FacturaController.class).listarPorId(id)).withSelfRel().toMono(); // quiero
																												// recuperar
																												// el
																												// endpoint
																												// de
																												// FacturaController(/Facturas)
																												// con
																												// la
																												// parte
																												// dinámica
																												// del
																												// método
																												// listarPorId
																												// {id}
																												// /Facturas/{id}
		Mono<Link> link2 = linkTo(methodOn(FacturaController.class).listarPorId(id)).withSelfRel().toMono(); // quiero
																												// recuperar
																												// el
																												// endpoint
																												// de
																												// FacturaController(/Facturas)
																												// con
																												// la
																												// parte
																												// dinámica
																												// del
																												// método
																												// listarPorId
																												// {id}
																												// /Facturas/{id}
		// formas de agregar el link al body
		// 1. practica no recomendada mal uso de la variable global, ya que si la
		// ponemos dentro del return no se puede usar.
		/*
		 * return service.listarPorId(id) .flatMap(p -> { this.FacturaHateoas = p;
		 * return link1; //retorna un link para trabajar con el en el siguiente paso })
		 * .map(lk -> { return EntityModel.of(this.FacturaHateoas,lk); });
		 */
		// 2.pracica intermedia ya que anidar maps no es muy efectiva tal vez dos si
		// pero no mas no es muy amigable
		/*
		 * return service.listarPorId(id) .flatMap(p -> { return link1.map(lk ->
		 * EntityModel.of(p,lk)); });
		 */
		// 3. practica ideal
		/*
		 * return service.listarPorId(id) .zipWith(link1,(p,lk) ->
		 * EntityModel.of(p,lk));
		 */
		// caso: mas de un link
		return link1.zipWith(link2) // se "unen" en un solo flujo
				.map(function((left, right) -> Links.of(left, right))) // genera un tercer link de la union de link1 y
																		// link2
				.zipWith(service.listarPorId(id), (links, p) -> EntityModel.of(p, links));
	}

	@PostMapping("/buscar")
	public Mono<ResponseEntity<Flux<Factura>>> buscar(@RequestBody FiltroDTO filtro){
		Flux<Factura> fxPlatos = service.obtenerFacturasPorFiltro(filtro);
		
		return Mono.just(ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(fxPlatos)
				);
		
	}
	
	@GetMapping("/generarReporte/{id}")
	public Mono<ResponseEntity<byte[]>> generarReporte(@PathVariable("id") String id){
		
		Mono<byte[]> monoReporte = service.generarReporte(id);
		return monoReporte
				.map(bytes -> ResponseEntity.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.body(bytes)
				).defaultIfEmpty(new ResponseEntity<byte[]>(HttpStatus.NO_CONTENT));
	}
}
