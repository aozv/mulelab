package com.pragmagenia.component;

import java.io.IOException;

import com.pragmagenia.model.Comando;
import com.pragmagenia.model.Execution;
import com.pragmagenia.model.Servidor;

public interface SSH {
	
	public String runCommand(Servidor servidor, Comando comando) throws IOException;
	public String runCommand(String ip, String user, String pass, String command) throws IOException ;

}
