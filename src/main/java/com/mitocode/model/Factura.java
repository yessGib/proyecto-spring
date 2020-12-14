package com.mitocode.model;

import java.time.LocalDateTime;
import java.util.List;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.format.annotation.DateTimeFormat;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL) //para indicar que no envie los parámetros nulos para aminorar el peso de la respuesta
@Document(collection = "facturas")
public class Factura {
	
	@Id
	private String id;
	
	@Size(min = 3)  //solo funciona para campos String para validar numeros usar @Max o @Min
	@Field(name = "descripcion")
	private String descripcion;
	
	@Field(name = "observacion")
	private String observacion;
	
	@NotNull
	@Field(name = "cliente")
	private Cliente cliente;

	private List<FacturaItem> items;
	
	@DateTimeFormat(pattern = "dd/MM/yyyy HH:mm:ss")
	private LocalDateTime creadoEn = LocalDateTime.now();// mongo usa formato UTC

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public String getObservacion() {
		return observacion;
	}

	public void setObservacion(String observacion) {
		this.observacion = observacion;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public List<FacturaItem> getItems() {
		return items;
	}

	public void setItems(List<FacturaItem> items) {
		this.items = items;
	}

	public LocalDateTime getCreadoEn() {
		return creadoEn;
	}

	public void setCreadoEn(LocalDateTime creadoEn) {
		this.creadoEn = creadoEn;
	}
	
	
}
