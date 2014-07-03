package com.pragmagenia.ws.rest;

import java.util.List;

import javax.websocket.server.PathParam;

import org.mockito.InjectMocks;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.pragmagenia.component.SSHClient;
import com.pragmagenia.exception.AppEntityException;
import com.pragmagenia.model.App;
import com.pragmagenia.model.Servidor;
import com.pragmagenia.model.Usuario;
import com.pragmagenia.servicios.AppServicios;

@RestController
@RequestMapping("rest")
public class WSRestController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(WSRestController.class);
	
	@Autowired
	private SSHClient ssh;
	@Autowired
	private AppServicios appServicios;
	
	@RequestMapping(value="info",produces={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<?> getVersion(){
		if(LOGGER.isDebugEnabled()){
			LOGGER.debug("Getting app Version!");
		}
		return new ResponseEntity<App>(App.getInstance(),HttpStatus.OK);
	}
	
	@RequestMapping(value="run", produces=MediaType.APPLICATION_JSON_VALUE, consumes={MediaType.APPLICATION_JSON_VALUE, MediaType.APPLICATION_XML_VALUE})
	public ResponseEntity<String> runCommand(@RequestBody(required=true) Servidor params) throws Exception{
		return new ResponseEntity<String>(ssh.runCommand(params.getIp(), params.getUser(), params.getPassword(), params.getCommand()), HttpStatus.OK);
		
	}
	
	@RequestMapping(value="users", produces={MediaType.APPLICATION_JSON_VALUE}, consumes={MediaType.APPLICATION_JSON_VALUE}, method=RequestMethod.GET)
	public ResponseEntity<List<Usuario>> getUsers(){
		return new ResponseEntity<List<Usuario>>(appServicios.findAllUsers(), HttpStatus.OK);
	}
	
	@RequestMapping(value="saveuser", produces=MediaType.APPLICATION_JSON_VALUE, consumes=MediaType.APPLICATION_JSON_VALUE, method=RequestMethod.POST)
	public ResponseEntity<Usuario> saveUser(@RequestBody(required=true)Usuario u){
		
		return new ResponseEntity<Usuario>(appServicios.saveUsuario(u), HttpStatus.OK);		
	}
	
	@RequestMapping(value="saveserver", produces=MediaType.APPLICATION_JSON_VALUE, consumes=MediaType.APPLICATION_JSON_VALUE, method=RequestMethod.POST)
	public ResponseEntity<Servidor> saveServidor(@RequestBody(required=true)Servidor s){
		return new ResponseEntity<Servidor>(appServicios.saveServidor(s), HttpStatus.OK);
	}
	
	@RequestMapping(value="servers", produces=MediaType.APPLICATION_JSON_VALUE, consumes=MediaType.APPLICATION_JSON_VALUE, method=RequestMethod.GET)
	public ResponseEntity<List<Servidor>> findServidores(){
		return new ResponseEntity<List<Servidor>>(appServicios.findAllServers(), HttpStatus.OK);
	}
	
	/**
	 * EL ultimo Slash "/" es necesario ya que se los parametros con . quedan truncados. ejemplo si no le coloco el ultimo slash y hago peticion
	 * http://localhost:8080/rest/server/192.168.1.11
	 * 
	 * el parametro que pasara sera 192.168.1.1 el cual es totalmente distinto.
	 * 
	 * @param serverId
	 * @return
	 * @throws AppEntityException
	 */
	@RequestMapping(value="server/{serverId}/", method=RequestMethod.DELETE)
	public ResponseEntity<?> deleteServer(@PathVariable String serverId) throws AppEntityException{
		appServicios.deleteServerById(serverId);
		return new ResponseEntity<String>(HttpStatus.ACCEPTED);		
	}
	
	/**
	 *  * EL ultimo Slash "/" es necesario ya que se los parametros con . quedan truncados. ejemplo si no le coloco el ultimo slash y hago peticion
	 * http://localhost:8080/rest/server/192.168.1.11
	 * 
	 * el parametro que pasara sera 192.168.1.1 el cual es totalmente distinto.
	 * 
	 * @param serverId
	 * @return
	 * @throws AppEntityException
	 */
	@RequestMapping(value="server/{serverId}/", method=RequestMethod.GET, produces=MediaType.APPLICATION_JSON_VALUE)
	public ResponseEntity<Servidor> findServer(@PathVariable String serverId) throws AppEntityException{
		return new ResponseEntity<Servidor>(appServicios.findByIp(serverId),HttpStatus.ACCEPTED);		
	}
	
	@RequestMapping(value="user/{userId}", method=RequestMethod.DELETE)
	public ResponseEntity<?> deleteUser(@PathVariable String userId) throws AppEntityException{
		appServicios.deleteUserById(userId);
		return new ResponseEntity<String>(HttpStatus.ACCEPTED);		
	}
	
	@RequestMapping(value="user/{userId}/{password}", method=RequestMethod.GET)
	public ResponseEntity<Boolean> loginUser(@PathVariable String userId, @PathVariable String password) throws AppEntityException{
		return new ResponseEntity<Boolean>(appServicios.login(userId, password),HttpStatus.OK);
	}
	
	@ExceptionHandler
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleUserAlreadyExistsException(Exception e) {
        return e.getMessage();
    }
	
	@ExceptionHandler
	@ResponseStatus(HttpStatus.CONFLICT)
	public String handleAppEntityExceptions(AppEntityException ee){
		return ee.getMessage();
	}

}
