package com.mitocode.controller;

import java.net.URI;

import javax.validation.Valid;

import org.jfree.util.Log;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.reactive.function.client.WebClient;

import com.mitocode.dto.PlatoClienteDTO;
import com.mitocode.model.Cliente;
import com.mitocode.model.Plato;
import com.mitocode.pagination.PageSupport;
import com.mitocode.service.IClienteService;
import com.mitocode.service.IPlatoService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;

import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.linkTo;
import static org.springframework.hateoas.server.reactive.WebFluxLinkBuilder.methodOn;
import static reactor.function.TupleUtils.function;
//import 	org.springframework.web.reactive.function.client.WebClient;
@RestController
@RequestMapping("/platos")
public class PlatoController {
	//ver https://martinfowler.com/articles/richardsonMaturityModel.html
	//ver https://www.adictosaltrabajo.com/2013/12/02/spring-hateoas/
	//servicio rest reactivo de enfoque de anotaciones semejante al mvc
	@Autowired
	private IPlatoService service;
	
	@Autowired
	private IClienteService clienteService;
	
	@GetMapping
	public Mono<ResponseEntity<Flux<Plato>>> listar(){
		Flux<Plato> fxPlatos = service.listar();
		
		//service.listar().subscribe(i -> Log.info(i.toString()));
		//service.listar().parallel().runOn(Schedulers.parallel()).subscribe(i -> Log.info(i.toString())); 	//el orden es dependiendo como esté disponible, como s haya obtenido // aprovecha los cores que tengas en el CPU
		//service.listar().subscribeOn(Schedulers.parallel()).subscribe(i -> Log.info(i.toString()));			//regresa los valore sen orden secuancial, tal como vienen
		
		
		return Mono.just(ResponseEntity.ok()
				.contentType(MediaType.APPLICATION_JSON)
				.body(fxPlatos)
				);
	}
	@GetMapping("/{id}")
	public Mono<ResponseEntity<Plato>> listarPorId(@PathVariable("id") String id){
		return service.listarPorId(id)			// regresa Mono<Plato>
				.map(p -> ResponseEntity.ok()  // del flujo anterior (service.listarPorId(id)), será p(aunque regresa un Mono<Plato>, cuando pasaal map solo manda Plato por lo tanto p  es Plato) y se le aplica un proceso, se le dice que va a generar un ResponseEntity con un ok. Lo que se pasa al siguiente como un Mono<ResponseEntity>
				.contentType(MediaType.APPLICATION_JSON) // trabaja sobre Mono<ResponseEntity> 
				.body(p)						// p es Plato
					)
				.defaultIfEmpty(ResponseEntity.notFound().build()); // defaultIfEmpty por si no encuentra un id y regresa el objeto vacío se  mande el status correcto 
	}
	
	@PostMapping
	public Mono<ResponseEntity<Plato>> registrar(@Valid @RequestBody Plato plato, final ServerHttpRequest req){//@RequestBody ransforma de json a java
		return service.registrar(plato)
				.map(p -> ResponseEntity.created(URI.create(req.getURI().toString().concat("/").concat(p.getId())))  //.created() permite enviar el código correcto para indicar que el objeto se ha creado. Pide que se envíe un "located" lo cual es la url donde puedo localizar el recurso que acabo de crear p.e.: //localhost:8080/platos/11
						.contentType(MediaType.APPLICATION_JSON)
						.body(p)
					);
	}
	
	@PutMapping("/{id}")                   // con @Valid le dices que se respete si o si el @NotNull de Plato
	public Mono<ResponseEntity<Plato>> modificar(@Valid @RequestBody Plato plato, @PathVariable("id") String id){
		Mono<Plato> monoPlato = Mono.just(plato); // datos a modificar
		Mono<Plato> monodb = service.listarPorId(id); // datos si existe en la bd
		
		return monodb
				.zipWith(monoPlato, (bd, p) -> { // lee los dos objetos y va setteando la información
					bd.setId(id);
					bd.setNombre(p.getNombre());
					bd.setPrecio(p.getPrecio());
					bd.setEstado(p.getEstado());
					return bd;				// en zipWith en el return debe regresar en primer elemento que se indico, en este caso bd
											// cuando bd regresa, ya no regresa solo como bd si no como Mono<bd> por lo que se le pueden seguir aplicando métodos de flujos
				})
				.flatMap(service::modificar)  // ahora Mono<bd> (Mono<Plato>) se pasa al flatMap y este lo recibe como platoNuevo de tipo Plato
												// se reomienda que cuando se use un método que venga de base de datos se use flatMap
												// con procesos simples de transformacion se use map
											  //.flatMap(platoNuevo -> service.modificar(platoNuevo)) <- esta es la manera poco elegante de hacerlo
				.map(p -> ResponseEntity.ok()  //.created() permite enviar el código correcto para indicar que el objeto se ha creado. Pide que se envíe un "located" lo cual es la url donde puedo localizar el recurso que acabo de crear p.e.: //localhost:8080/platos/11
						.contentType(MediaType.APPLICATION_JSON)
						.body(p)
				).defaultIfEmpty(new ResponseEntity<Plato>(HttpStatus.NOT_FOUND)); // es lo mismo que poner .defaultIfEmpty(ResponseEntity.notFound().build())
	}
	
	@DeleteMapping("/{id}")
	public Mono<ResponseEntity<Void>> eliminar(@PathVariable("id") String id){ //Se usa Mono ya qye esta en un contexto de weflux
		
		return service.listarPorId(id)
				.flatMap(p ->{
					return service.eliminar(p.getId())
							.then(Mono.just(new ResponseEntity<Void>(HttpStatus.NO_CONTENT)));	// se usa para Mono, se usa cuando se quiere terminar todo (similar a break) y ntonces regresará algo del tipo que indique el método en este cso, tendría que regresar un  Mono<ResponseEntity<Void>>
				})
				.defaultIfEmpty(new ResponseEntity<Void>(HttpStatus.NOT_FOUND));
	}
	
	private Plato platoHateoas;
	@GetMapping("/hateoas/{id}")
	public Mono<EntityModel<Plato>> listarHateoasPorId(@PathVariable("id") String id){
		Mono<Link> link1 = linkTo(methodOn(PlatoController.class).listarPorId(id)).withSelfRel().toMono(); // quiero recuperar el endpoint de PlatoController(/platos) con la parte dinámica del método listarPorId {id}  /platos/{id}
		Mono<Link> link2 = linkTo(methodOn(PlatoController.class).listarPorId(id)).withSelfRel().toMono(); // quiero recuperar el endpoint de PlatoController(/platos) con la parte dinámica del método listarPorId {id}  /platos/{id}
		//formas de agregar el link al body
		//1. practica no recomendada mal uso de la variable global, ya que si la ponemos dentro del return no se puede usar.
		/*return service.listarPorId(id)
				.flatMap(p -> {
					this.platoHateoas = p;
					return link1;    //retorna un link para trabajar con el en el siguiente paso
				})
				.map(lk -> {
					return EntityModel.of(this.platoHateoas,lk);
				});
		*/
		//2.pracica intermedia  ya que anidar maps no es muy efectiva tal vez dos si pero no mas no es muy amigable
		/*return service.listarPorId(id)
				.flatMap(p -> {
					return link1.map(lk -> EntityModel.of(p,lk));
				});
		*/
		//3. practica ideal  
		/*return service.listarPorId(id)
				.zipWith(link1,(p,lk) -> EntityModel.of(p,lk));
		*/
		//caso: mas de un link 
		return link1.zipWith(link2) 			//se "unen" en un solo flujo 
				.map(function((left,right) -> Links.of(left,right)))	//genera un tercer link de la union de link1 y link2
				.zipWith(service.listarPorId(id), (links,p) -> EntityModel.of(p,links));
	}
	
	@GetMapping("/client1")
	public Flux<Plato> listarClient1(){
		Flux<Plato> fx = WebClient.create("http://localhost:8080/platos")   // para obtener los datos de un endpoint que no es nuestro, es de otro ws
										.get()
										.retrieve()
										.bodyToFlux(Plato.class);
	return fx;
	}
	
	@GetMapping("/client2")
	public Mono<ResponseEntity<PlatoClienteDTO>> listarClient2(){			//para combinar la petición de una cliente y un plato que no necesariamente tengan algo en común 
		Mono<Plato> plato = service.listarPorId("5fd19d7ef77d56662a5917bc").defaultIfEmpty(new Plato());		//ya que si alguno de ellos es vació ya no puede hacer la nstancia del otro
		Mono<Cliente> cliente = clienteService.listarPorId("5fadf189ade82d399f841fb2").defaultIfEmpty(new Cliente());
		
		return Mono.zip(cliente, plato, PlatoClienteDTO::new)   // zip junta dos instancias en una en este caso PlatoCliente
				.map(pc -> ResponseEntity.ok()
						.contentType(MediaType.APPLICATION_JSON)
						.body(pc)
						).defaultIfEmpty(ResponseEntity.notFound().build());
	}
}	
	

