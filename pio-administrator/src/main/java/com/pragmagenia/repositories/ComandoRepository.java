package com.pragmagenia.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;

import com.pragmagenia.model.Comando;
import com.pragmagenia.model.Servidor;

public interface ComandoRepository extends MongoRepository<Comando, String>{
	
}
