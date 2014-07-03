package com.pragmagenia.component;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.springframework.core.io.Resource;
import org.springframework.data.annotation.Transient;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import com.pragmagenia.model.Comando;
import com.pragmagenia.model.Execution;
import com.pragmagenia.model.Servidor;

import ch.ethz.ssh2.Connection;
import ch.ethz.ssh2.Session;
import ch.ethz.ssh2.StreamGobbler;

@Service("ssh")
public class SSHClient implements SSH{

	
	private Session getSession(Connection conn) throws IOException {
		Session session = conn.openSession();
		return session;

	}

	
	private Connection getConnection(String ip, String user, String pass)
			throws IOException {
		Connection conn = new Connection(ip);
		conn.connect();
		conn.authenticateWithPassword(user, pass);
		return conn;
	}
	
	public String runCommand(Servidor servidor, Comando comando) throws IOException{
		Connection connection  = getConnection(servidor.getIp(), servidor.getUser(), servidor.getPassword());
		Session session = getSession(connection);
		InputStream stdout = new StreamGobbler(session.getStdout());
		BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(
				stdout));

		// Run command
		String tempCommand = comando.getSentencia();

		session.execCommand(tempCommand);// + " && sleep 5");

		// Get output
		StringBuffer sb = new StringBuffer();
		while (true) {
			String line = stdoutReader.readLine();
			if (line == null)
				break;
			sb.append(line + "\n");
		}
		String output = sb.toString();

		session.close();
		connection.close();

		return output;
	}

	@SuppressWarnings("null")
	public String runCommand(String ip, String user, String pass, String command)
			throws IOException {

		// Setup ssh session with endpoint

		Assert.notNull(ip);
		Assert.notNull(user);
		Assert.notNull(pass);
		Assert.notNull(command);
		Assert.hasLength(ip);
		Assert.hasLength(user);
		Assert.hasLength(pass);
		Assert.hasLength(command);
		

		Connection connection  = getConnection(ip, user, pass);
		Session session = getSession(connection);
		InputStream stdout = new StreamGobbler(session.getStdout());
		BufferedReader stdoutReader = new BufferedReader(new InputStreamReader(
				stdout));

		// Run command
		String tempCommand = command;

		session.execCommand(tempCommand);// + " && sleep 5");

		// Get output
		StringBuffer sb = new StringBuffer();
		while (true) {
			String line = stdoutReader.readLine();
			if (line == null)
				break;
			sb.append(line + "\n");
		}
		String output = sb.toString();

		session.close();
		connection.close();

		return output;
	}

}
