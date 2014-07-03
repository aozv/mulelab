package com.pragmagenia.servicios;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.jasypt.encryption.StringEncryptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.pragmagenia.exception.AppEntityException;
import com.pragmagenia.model.Comando;
import com.pragmagenia.model.Servidor;
import com.pragmagenia.model.Usuario;
import com.pragmagenia.model.Usuario.Rol;
import com.pragmagenia.repositories.ComandoRepository;
import com.pragmagenia.repositories.ServidorRepository;
import com.pragmagenia.repositories.UsuarioRepository;


/**
 * Servicio de aplicacion CRUD de entidades y encriptar contrasenas
 * @see com.pragmagenia.repositories.ServidorRepository
 * @see com.pragmagenia.repositories.UsuarioRepository
 * 
 * @author Rafael Cadenas
 * @version 1.0.0
 */
@Service
public class AppServiciosImpl implements AppServicios, PasswordEncoder{
	
	private UsuarioRepository usuarioRepository;
	private ServidorRepository servidorRepository;
	private PasswordEncoder passwordEncoder;
	private StringEncryptor stringEncryptor;
	private MongoOperations mongoTemplate;
	private ComandoRepository comandoRepository;
	
	public AppServiciosImpl(){
		
	}
	
	@Autowired
	public AppServiciosImpl(UsuarioRepository usuarioRepository, ServidorRepository servidorRepository, PasswordEncoder passwordEncoder, StringEncryptor stringEncryptor, MongoOperations mongoTemplate, ComandoRepository comandoRepository){
		this.usuarioRepository = usuarioRepository;
		this.servidorRepository = servidorRepository;
		this.passwordEncoder = passwordEncoder;
		this.stringEncryptor = stringEncryptor;
		this.mongoTemplate = mongoTemplate;
		this.comandoRepository = comandoRepository;
	}

	@Override
	public Page<Usuario> findUsuarios(int pageNumber, int pageSize) {
		Pageable pageable = new PageRequest(pageNumber, pageSize);
		return usuarioRepository.findAll(pageable);
	}

	@Override
	public Page<Servidor> findServidores(int pageNumber, int pageSize) {
		Pageable pageable = new PageRequest(pageNumber, pageSize);
		return servidorRepository.findAll(pageable);
	}

	@Override
	public Servidor findByIp(String ip) throws AppEntityException {
		Assert.hasLength(ip);
		Servidor s = servidorRepository.findOne(ip);
		if(s == null){
			throw new AppEntityException("El servidor no existe!");
		}
		return s;
	}

	@Override
	public Usuario findByLogin(String login) {
		Assert.notNull(login);
		Assert.hasLength(login);
		return usuarioRepository.findByLogin(login);
	}

	@Override
	public void deleteUsuario(Usuario u) {
		Assert.notNull(u);
		Assert.hasLength(u.getLogin());
		usuarioRepository.delete(u);
	}

	@Override
	public void deleteServidor(Servidor s) {
		Assert.notNull(s);
		Assert.hasLength(s.getIp());
		servidorRepository.delete(s);
	}

	
	/**
	 * Guarda una entidad usuario con su contrasena encriptada
	 * 
	 * @author Rafael Cadenas
	 */
	@Override
	public Usuario saveUsuario(Usuario u) {
		Assert.notNull(u);
		Assert.hasLength(u.getLogin());
		Assert.hasLength(u.getContrasena());
		Assert.hasLength(u.getRol().name());
		String contrasena = encode(u.getContrasena());
		u.setContrasena(contrasena);
		return usuarioRepository.save(u);
	}

	/**
	 * Guardar una entidad Servidor
	 * 
	 * @author Rafael Cadenas
	 */
	@Override
	public Servidor saveServidor(Servidor s) {
		Assert.notNull(s);
		Assert.hasLength(s.getUser());
		Assert.hasLength(s.getPassword());
		Assert.hasLength(s.getIp());
		String encryptedUser = stringEncryptor.encrypt(s.getUser());
		String encryptedPass = stringEncryptor.encrypt(s.getPassword());
		
		s.setUser(encryptedUser);
		s.setPassword(encryptedPass);
		return servidorRepository.save(s);
	}
	
	
	@Override
	public String encode(CharSequence rawPassword) {		
		return passwordEncoder.encode(rawPassword);
	}
	
	

	@Override
	public boolean matches(CharSequence rawPassword, String encodedPassword) {
		return passwordEncoder.matches(rawPassword, encodedPassword);
	}

	@Override
	public boolean login(String login, String contrasena) throws AppEntityException {
		Usuario u = findByLogin(login);
		if(u != null){
			if(!matches(contrasena, u.getContrasena())){
				throw new AppEntityException("Contrasena Invalida!");
			}
		}else{
			throw new AppEntityException("No existen estas credenciales!");
		}
		
		return true;
	}

	@Override
	public List<Usuario> findAllUsers() {
		return usuarioRepository.findAll();
	}

	@Override
	public List<Servidor> findAllServers() {
		return servidorRepository.findAll();
	}

	@Override
	public void deleteUserById(String id) throws AppEntityException{
		Usuario u = findByLogin(id);
		if(u != null){
			usuarioRepository.delete(id);
		}else{
			throw new AppEntityException("No existe este usuario!");
		}
	}

	@Override
	public void deleteServerById(String id) throws AppEntityException{
		Servidor s = findByIp(id);
		if(s != null){
			servidorRepository.delete(id);
		}else{
			throw new AppEntityException("No existe este Servidor!");
		}
	}

	@Override
	public UserDetails loadUserByUsername(String username)
			throws UsernameNotFoundException {
		
		Usuario u = findByLogin(username);
		List<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();
		User user = null;
		if(u != null){
			if(u.getRol().name() != null){
				authorities.add(new SimpleGrantedAuthority(u.getRol().name()));
			}
			user = new User(u.getLogin(), u.getContrasena(), authorities);
		}else{
			throw new UsernameNotFoundException("Por favor revise sus credenciales!");
		}
		
		
		return user;
	}

	@Override
	public List<Rol> getRoles() {		
		return Arrays.asList(Rol.values());
	}

	@Override
	public Usuario updateUsuario(Usuario usuario) {
				
		Query qry = new Query();
		qry.addCriteria(where("login").is(usuario.getLogin()));
		
		Update upd = new Update();
		if(usuario.getNombrecompleto() != null && usuario.getNombrecompleto().trim().length() > 0){
			upd.set("nombrecompleto", usuario.getNombrecompleto());
		}
		if(usuario.getRol() != null){
			upd.set("rol", usuario.getRol());
		}
				
		Usuario u = mongoTemplate.findAndModify(qry, upd, Usuario.class);
			
		return u;
	}

	@Override
	public Servidor updateServidor(Servidor servidor) {
//		Query qry = new Query();
//		qry.addCriteria(where("login").is(servidor.getIp()));
//		
//		Update upd = new Update();
//		if(servidor.getNombrecompleto() != null && usuario.getContrasena().trim().length() > 0){
//			upd.set("nombrecomplet", usuario.getNombrecompleto());
//		}
//		if(usuario.getRol() != null){
//			upd.set("rol", usuario.getRol());
//		}
		
		
		return null;
	}

	@Override
	public List<Comando> findComandos() {
		
		return comandoRepository.findAll();
	}

	@Override
	public void deleteComando(String id) {
		comandoRepository.delete(id);		
	}

	@Override
	public Comando saveComando(Comando comando) {
		
		return comandoRepository.save(comando);
	}

	@Override
	public Comando findComandoById(String id) {
		
		return comandoRepository.findOne(id);
	}


}
