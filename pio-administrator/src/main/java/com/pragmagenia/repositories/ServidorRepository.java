package com.pragmagenia.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.pragmagenia.model.Servidor;

public interface ServidorRepository extends MongoRepository<Servidor, String>{
	
	public Servidor findByIp(String ip);

}
