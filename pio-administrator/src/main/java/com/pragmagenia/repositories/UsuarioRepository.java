package com.pragmagenia.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.pragmagenia.model.Usuario;

public interface UsuarioRepository extends MongoRepository<Usuario, String>{
	
	public Usuario findByLogin(String login);

}
