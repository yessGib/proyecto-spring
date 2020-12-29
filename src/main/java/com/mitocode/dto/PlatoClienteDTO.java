package com.mitocode.dto;

import com.mitocode.model.Estudiante;
import com.mitocode.model.Curso;

public class PlatoClienteDTO {
	
	private Estudiante estudiante;
	private Curso curso;
	
	public PlatoClienteDTO(Estudiante estudiante, Curso curso) {
		super();
		this.estudiante = estudiante;
		this.curso = curso;
	}
	public Estudiante getCliente() {
		return estudiante;
	}
	public void setCliente(Estudiante estudiante) {
		this.estudiante = estudiante;
	}
	public Curso getPlato() {
		return curso;
	}
	public void setPlato(Curso curso) {
		this.curso = curso;
	}
	
	
	
}
