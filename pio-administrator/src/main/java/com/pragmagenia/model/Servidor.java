package com.pragmagenia.model;

import java.io.Serializable;
import java.util.UUID;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import org.springframework.data.annotation.Id;


@XmlRootElement
public class Servidor implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6466946617710850164L;
	@Id
	private String ip;
	private String nombre;
	private String descripcion;
	private String user;
	private String password;
	private String command;
	private String basepath;
	private Boolean status;
	
	public Servidor(){}
	
	public Servidor(String ip,String user, String password, String command, Boolean status){
		this.ip = ip;
		this.user = user;
		this.password = password;
		this.command = command;
		this.status = status;
	}
	
	
	
	
	@XmlElement
	public String getIp() {
		return ip;
	}

	public void setIp(String ip) {
		this.ip = ip;
	}
	
	@XmlElement
	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}
	
	@XmlElement
	public String getCommand() {
		return command;
	}

	public void setCommand(String command) {
		this.command = command;
	}
	
	@XmlElement
	public String getUser() {
		return user;
	}

	public void setUser(String user) {
		this.user = user;
	}
	
	@XmlElement(nillable=true)
	public String getBasepath() {
		return basepath;
	}

	public void setBasepath(String basepath) {
		this.basepath = basepath;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getDescripcion() {
		return descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Boolean isStatus() {
		return status;
	}

	public void setStatus(Boolean status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "Servidor [ip=" + ip + ", nombre=" + nombre + ", descripcion="
				+ descripcion + ", user=" + user + ", password=" + password
				+ "]";
	}
	
	
	


}
