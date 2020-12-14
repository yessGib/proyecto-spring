package com.mitocode.model;

import java.util.List;

import javax.validation.constraints.NotNull;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Document(collection = "usuarios")
public class Usuario {
	
	@Id
	private String id;
	
	@NotNull
	@Field(name = "usuario")
	private String usuario;
	
	@NotNull
	@Field(name = "clave")
	private String clave;
	
	@NotNull
	@Field(name = "estado")
	private Boolean estado;
	
	private List<Rol> roles; //cantes con DBRef se obtenia toda la informacion del Rol pero ahora solo se obtiene el id y se tiene que hacer una neva consulta para obtener la informacion

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

	public String getClave() {
		return clave;
	}

	public void setClave(String clave) {
		this.clave = clave;
	}

	public Boolean getEstado() {
		return estado;
	}

	public void setEstado(Boolean estado) {
		this.estado = estado;
	}

	public List<Rol> getRoles() {
		return roles;
	}

	public void setRoles(List<Rol> roles) {
		this.roles = roles;
	}
	
	
	
}
