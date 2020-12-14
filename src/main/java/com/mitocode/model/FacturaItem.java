package com.mitocode.model;

public class FacturaItem {
	//NO lleva @document ni anotaciones field ni id por que esta clase no será un documento si no parte de uno (Factura)
	
	private Integer cantidad;
	private Plato plato;
	
	public Integer getCantidad() {
		return cantidad;
	}
	public void setCantidad(Integer cantidad) {
		this.cantidad = cantidad;
	}
	public Plato getPlato() {
		return plato;
	}
	public void setPlato(Plato plato) {
		this.plato = plato;
	}
}
