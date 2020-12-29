package com.mitocode.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import com.fasterxml.jackson.annotation.JsonInclude;


@JsonInclude(JsonInclude.Include.NON_NULL)
@Document(collection = "cursos")
public class Curso {
	
	@Id
	private String id;
	
	@NotEmpty
	@Field(name = "nombre")
	private String nombre;
	
	
	@Field(name = "siglas")
	private String siglas;
	
	@NotNull
	@Field(name = "estado")
	private Boolean estado;
	
	public Curso() {};
	
	public Curso(String id, @NotEmpty String nombre, String siglas, @NotNull Boolean estado) {
		super();
		this.id = id;
		this.nombre = nombre;
		this.siglas = siglas;
		this.estado = estado;
	}
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
	
	public String getSiglas() {
		return siglas;
	}

	public void setSiglas(String siglas) {
		this.siglas = siglas;
	}

	@Override
	public String toString() {
		return "Curso [id=" + id + "]";
	}
	
	
	
}
