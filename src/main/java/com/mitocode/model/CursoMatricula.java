package com.mitocode.model;

public class CursoMatricula {
	//NO lleva @document ni anotaciones field ni id por que esta clase no será un documento si no parte de uno (Matricula)

	private Curso curso;

	public Curso getCurso() {
		return curso;
	}

	public void setCurso(Curso curso) {
		this.curso = curso;
	}	
	
}
