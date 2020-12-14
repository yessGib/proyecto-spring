package com.mitocode.controller;

import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import reactor.core.publisher.Flux;

@RestController
@RequestMapping("/json")
public class JSONController {
	
	@GetMapping(value = "/buffer", produces = MediaType.APPLICATION_STREAM_JSON_VALUE)//APPLICATION_JSON_VALUE espera a que todo se procese para devolverlo como una rreglo de json dentro  //APPLICATION_STREAM_JSON_VALUE   se emite cada objeto de forma independiente, mientras procesa json envia cada elemeto
	public Flux<Integer> testContrapresion(){
		return Flux.range(0, 100)
				.log()
				//.limitRate(10);//cuando se llega al 75% de data drenada/ emitida, se piden los restantes
				//.limitRate(10,0);// dreno todo y pido 10 mas
				.limitRate(10,2); //dreno 8 y pido 2 mas, capacidad máxima 10
	}
}