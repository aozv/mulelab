package com.pragmagenia.servicios;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.pragmagenia.exception.AppEntityException;
import com.pragmagenia.model.Comando;
import com.pragmagenia.model.Servidor;
import com.pragmagenia.model.Usuario;


public interface AppServicios  extends  UserDetailsService{
	
	public Page<Usuario> findUsuarios(int pageNumber, int pageSize);
	public Page<Servidor> findServidores(int pageNumber, int pageSize);
	public Servidor findByIp(String ip) throws AppEntityException;
	public Usuario findByLogin(String login);
	public void deleteUsuario(Usuario u);
	public void deleteServidor(Servidor s);
	public Usuario saveUsuario(Usuario u);
	public Servidor saveServidor(Servidor s);
	public boolean login(String login, String contrasena) throws AppEntityException;
	public List<Usuario> findAllUsers();
	public List<Servidor> findAllServers();
	public void deleteUserById(String id) throws AppEntityException;
	public void deleteServerById(String id) throws AppEntityException;
	public List<Usuario.Rol> getRoles();
	public Usuario updateUsuario(Usuario usuario);
	public Servidor updateServidor(Servidor servidor);
	public List<Comando> findComandos();
	public void deleteComando(String id);
	public Comando saveComando(Comando comando);
	public Comando findComandoById(String id);

}
