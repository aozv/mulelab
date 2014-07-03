package com.pragmagenia.model;

import java.io.Serializable;
import java.util.UUID;

import org.springframework.data.annotation.Id;



public class Comando implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4023485513725237291L;
	@Id
	private String id;
	private String descripcion;
	private String sentencia;
	
	public Comando(){}
	
	public Comando(String descripcion, String sentencia){
		this.descripcion = descripcion;
		this.sentencia = sentencia;
	}
	
	

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

	public String getSentencia() {
		return sentencia;
	}

	public void setSentencia(String sentencia) {
		this.sentencia = sentencia;
	}

	@Override
	public String toString() {
		return "Comando [id=" + id + ", descripcion=" + descripcion
				+ ", sentencia=" + sentencia + "]";
	}

	
	
}
