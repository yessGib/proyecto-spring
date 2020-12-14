package com.mitocode.controller;

import java.io.File;
import java.net.URI;
import java.nio.file.Files;
import java.util.Map;

import javax.validation.Valid;

import org.cloudinary.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.hateoas.EntityModel;
import org.springframework.hateoas.Link;
import org.springframework.hateoas.Links;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.codec.multipart.FilePart;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;
import com.mitocode.model.Cliente;
import com.mitocode.model.Plato;
import com.mitocode.pagination.PageSupport;
import com.mitocode.service.IClienteService;


import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;
import static reactor.function.TupleUtils.function;

//import 	org.springframework.web.reactive.function.client.WebClient;
@RestController
@RequestMapping("/clientes")
public class ClienteController {

	@Autowired
	private IClienteService service;

	@GetMapping
	public Mono<ResponseEntity<Flux<Cliente>>> listar() {
		Flux<Cliente> fxClientes = service.listar();
		return Mono.just(ResponseEntity.ok().contentType(MediaType.APPLICATION_JSON).body(fxClientes));
	}

	@GetMapping("/{id}")
	public Mono<ResponseEntity<Cliente>> listarPorId(@PathVariable("id") String id) {
		return service.listarPorId(id) // regresa Mono<Cliente>
				.map(p -> ResponseEntity.ok() // del flujo anterior (service.listarPorId(id)), será p(aunque regresa un
												// Mono<Cliente>, cuando pasaal map solo manda Cliente por lo tanto p es
												// Cliente) y se le aplica un proceso, se le dice que va a generar un
												// ResponseEntity con un ok. Lo que se pasa al siguiente como un
												// Mono<ResponseEntity>
						.contentType(MediaType.APPLICATION_JSON) // trabaja sobre Mono<ResponseEntity>
						.body(p) // p es Cliente
				).defaultIfEmpty(ResponseEntity.notFound().build()); // defaultIfEmpty por si no encuentra un id y
																		// regresa el objeto vacío se mande el status
																		// correcto
	}

	@PostMapping
	public Mono<ResponseEntity<Cliente>> registrar(@Valid @RequestBody Cliente Cliente, final ServerHttpRequest req) {// @RequestBody
																														// ransforma
																														// de
																														// json
																														// a
																														// java
		return service.registrar(Cliente)
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
																													// //localhost:8080/Clientes/11
						.contentType(MediaType.APPLICATION_JSON).body(p));
	}

	@PutMapping("/{id}") // con @Valid le dices que se respete si o si el @NotNull de Cliente
	public Mono<ResponseEntity<Cliente>> modificar(@Valid @RequestBody Cliente Cliente, @PathVariable("id") String id) {
		Mono<Cliente> monoCliente = Mono.just(Cliente); // datos a modificar
		Mono<Cliente> monodb = service.listarPorId(id); // datos si existe en la bd

		return monodb.zipWith(monoCliente, (bd, p) -> { // lee los dos objetos y va setteando la información
			bd.setId(id);
			bd.setNombres(p.getNombres());
			bd.setApellidos(p.getApellidos());
			bd.setFechaNac(p.getFechaNac());
			return bd; // en zipWith en el return debe regresar en primer elemento que se indico, en
						// este caso bd
						// cuando bd regresa, ya no regresa solo como bd si no como Mono<bd> por lo que
						// se le pueden seguir aplicando métodos de flujos
		}).flatMap(service::modificar) // ahora Mono<bd> (Mono<Cliente>) se pasa al flatMap y este lo recibe como
										// ClienteNuevo de tipo Cliente
										// se reomienda que cuando se use un método que venga de base de datos se use
										// flatMap
										// con procesos simples de transformacion se use map
										// .flatMap(ClienteNuevo -> service.modificar(ClienteNuevo)) <- esta es la
										// manera poco elegante de hacerlo
				.map(p -> ResponseEntity.ok() // .created() permite enviar el código correcto para indicar que el objeto
												// se ha creado. Pide que se envíe un "located" lo cual es la url donde
												// puedo localizar el recurso que acabo de crear p.e.:
												// //localhost:8080/Clientes/11
						.contentType(MediaType.APPLICATION_JSON).body(p))
				.defaultIfEmpty(new ResponseEntity<Cliente>(HttpStatus.NOT_FOUND)); // es lo mismo que poner
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

	private Cliente ClienteHateoas;

	@GetMapping("/hateoas/{id}")
	public Mono<EntityModel<Cliente>> listarHateoasPorId(@PathVariable("id") String id) {
		Mono<Link> link1 = linkTo(methodOn(ClienteController.class).listarPorId(id)).withSelfRel().toMono(); // quiero
																												// recuperar
																												// el
																												// endpoint
																												// de
																												// ClienteController(/Clientes)
																												// con
																												// la
																												// parte
																												// dinámica
																												// del
																												// método
																												// listarPorId
																												// {id}
																												// /Clientes/{id}
		Mono<Link> link2 = linkTo(methodOn(ClienteController.class).listarPorId(id)).withSelfRel().toMono(); // quiero
																												// recuperar
																												// el
																												// endpoint
																												// de
																												// ClienteController(/Clientes)
																												// con
																												// la
																												// parte
																												// dinámica
																												// del
																												// método
																												// listarPorId
																												// {id}
																												// /Clientes/{id}
		// formas de agregar el link al body
		// 1. practica no recomendada mal uso de la variable global, ya que si la
		// ponemos dentro del return no se puede usar.
		/*
		 * return service.listarPorId(id) .flatMap(p -> { this.ClienteHateoas = p;
		 * return link1; //retorna un link para trabajar con el en el siguiente paso })
		 * .map(lk -> { return EntityModel.of(this.ClienteHateoas,lk); });
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

	//cloudinary subir archivos aun repo externo similar a AWS
	@PostMapping("/subir/{id}")
	public Mono<ResponseEntity<Cliente>> subir(@PathVariable String id, @RequestPart FilePart file){
		Cloudinary cloudinary = new Cloudinary(ObjectUtils.asMap(
				"cloud_name","dxrlwhfiu",
				"api_key","998263221437134",
				"api_secret","XjwgOonJz7N6R9MKP5awlLyNx4k"
				));
		return service.listarPorId(id)
				.flatMap(c -> {
					try { // cuando una libreria como cloudinary no tienen un soprte completo para programación reactiva, se recomienda poner el tru catch
						File f = Files.createTempFile("temp", file.filename()).toFile(); //creo el archivo temporal  //para ponerlo en una ruta local se pone la ruta en lugar de temp y se omite lo que cloudinary 
						file.transferTo(f);												// y lo transfiero a un esoacio en memoria 
						Map response = cloudinary.uploader().upload(f, ObjectUtils.asMap("resource_type","auto")); // se carga a cloudinary  // hay raw(cualuqier archivo.pdf, xlsx etc), image(.png,.jgp), y auto (deteta la extensión y lo cataoga como cloudinary diga
						
						JSONObject json = new JSONObject(response);
						String url = json.getString("url");
						
						c.setUrlFoto(url);
						return service.modificar(c).then(Mono.just(ResponseEntity.ok().body(c)));
					} catch (Exception e) {
						e.printStackTrace();
					}
					
					return Mono.just(ResponseEntity.ok().body(c));
				})
				.defaultIfEmpty(ResponseEntity.notFound().build());	
	}	

	@GetMapping("/pageable")
	public Mono<ResponseEntity<PageSupport<Cliente>>> listarPageable(
			@RequestParam(name = "page", defaultValue = "0") int page,
			@RequestParam(name = "size", defaultValue = "10") int size
			){
		Pageable pageRequest = PageRequest.of(page, size);
		return service.listarPage(pageRequest)
				.map(p -> ResponseEntity.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.body(p)									//la pagina de cliente
						)
				.defaultIfEmpty(ResponseEntity.noContent().build());
	}
}
