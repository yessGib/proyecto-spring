package com.mitocode.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonInclude;

//@Entity y @Table se usan para bases de datos relacionales
//@Document // lo que @Document hará será buscar el plural de esa clase en la base de datos
//para indicarle la relación en la base de datos:
@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = "platos")
public class Plato {
 //esta es la representación de un documento
	@Id
	private String id;
	// para bases de dtos relacionales @Column(name = "nomre", ... etc)
	@NotEmpty
	@Field(name = "nombre") // es opcional, si no se pone asumirá el nombre del aributo en la base 
	private String nombre;
	
	
	@Field(name = "precio")
	private Double precio;
	
	@NotNull  //es un requerido
	@Field(name = "estado")
	private Boolean estado;
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public Boolean getEstado() {
		return estado;
	}
	public void setEstado(Boolean estado) {
		this.estado = estado;
	}
	public String getNombre() {
		return nombre;
	}
	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	public Double getPrecio() {
		return precio;
	}
	public void setPrecio(Double precio) {
		this.precio = precio;
	}
	@Override
	public String toString() {
		return "Plato [id=" + id + "]";
	}
	
	
	
}
