package com.pragmagenia.controller;



import java.io.IOException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;

import org.jasypt.encryption.StringEncryptor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.bind.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.Assert;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.pragmagenia.component.SSH;
import com.pragmagenia.exception.AppEntityException;
import com.pragmagenia.model.Comando;
import com.pragmagenia.model.Execution;
import com.pragmagenia.model.Servidor;
import com.pragmagenia.model.Usuario;
import com.pragmagenia.model.Usuario.Rol;
import com.pragmagenia.servicios.AppServicios;

@Controller
public class AppController {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(AppController.class);

    protected static final String VIEW_NAME_LOGIN_PAGE = "login";
    protected static final String VIEW_NAME_INDEX_PAGE = "index";
    protected static final String VIEW_NAME_COMMAND_PAGE = "command";
    protected static final String VIEW_NAME_USUARIOS_PAGE = "usuarios";
    protected static final String VIEW_NAME_SERVIDORES_PAGE = "servidores";
    protected static final String REDIRECT_NAME_USUARIOS_PAGE = "redirect:/usuarios";
    protected static final String REDIRECT_NAME_SERVIDORES_PAGE = "redirect:/servidores";
    protected static final String REDIRECT_NAME_COMANDOS_PAGE = "redirect:/command";
    protected static final String VIEW_NAME_EDIT_USUARIO_PAGE = "/usuario/editar";
    protected static final String VIEW_NAME_NUEVO_USUARIO_PAGE = "/usuario/nuevo";
    protected static final String VIEW_NAME_EDIT_SERVIDOR_PAGE = "/servidor/editar";
    protected static final String VIEW_NAME_NUEVO_SERVIDOR_PAGE = "/servidor/nuevo";
    protected static final String VIEW_NAME_EDIT_COMANDO_PAGE = "/comando/editar";
    protected static final String VIEW_NAME_NUEVO_COMANDO_PAGE = "/comando/nuevo";
    protected static final String VIEW_NAME_COMANDOS_PAGE = "/comandos";
    private AppServicios appServicios;
    private StringEncryptor stringEncriptor;
    private SSH ssh;
    
    
    @Autowired
    public AppController(AppServicios appServicios, StringEncryptor stringEncriptor, SSH ssh){
    	this.appServicios = appServicios;
    	this.stringEncriptor = stringEncriptor;
    	this.ssh = ssh;
    }
    

    @RequestMapping(value = {"/login", "/"}, method = RequestMethod.GET)
    public String showLoginPage() {
        System.out.println("Rendering login page.");
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth instanceof UsernamePasswordAuthenticationToken) {
        	System.out.println(auth.getClass().getName());
            /* The user is logged in :) */
            return "forward:/index";
        }
        return VIEW_NAME_LOGIN_PAGE;
    }
    
    @RequestMapping("/login-error")
    public String loginError(Model model) {
    	System.out.println("RETORNANDO ERROR LOGIN");
        model.addAttribute("loginError", true);
        return VIEW_NAME_LOGIN_PAGE;
    }
    
    @RequestMapping(value = "/index", method = RequestMethod.GET)
    public String showIndex(Model model) {
    	model.addAttribute("serverList", getServerList());
        return VIEW_NAME_INDEX_PAGE;
    }
    
    @RequestMapping(value = "/command", method= RequestMethod.GET)
    public String showCommandServerPage(Model model) throws AppEntityException{
    	
    	model.addAttribute("serverList", getServerList());
    	model.addAttribute("execution", new Execution());
    	model.addAttribute("comandos", getComandos());
    	
    	return VIEW_NAME_COMMAND_PAGE;    	
    }
    
    @RequestMapping(value="/command/execute", method=RequestMethod.POST)
    public String executeCommand(@ModelAttribute(value="execution") Execution execution, BindingResult result,Model model) throws AppEntityException{
		String response = "";
		
		model.addAttribute("serverList", getServerList());
    	model.addAttribute("execution", new Execution());
    	model.addAttribute("comandos", getComandos());
    	
    	Servidor s = getServidor(execution.getServer());
    	Comando c = getComandoById(execution.getCommand());
    	
    	
		if(execution != null){
			try {				
				response = ssh.runCommand(s,c);
			} catch (Exception e) {
				response = e.getMessage();
				model.addAttribute("error", response);
				return "response";
			}
		}
			
    	model.addAttribute("response", response);
    	
    	return "response";
    }
    
    @RequestMapping(value = "/servidores", method=RequestMethod.GET)
    public String showServidoresPage(Model model){
    	model.addAttribute("serverList", getServerList());
    	return VIEW_NAME_SERVIDORES_PAGE;
    }
    
    @RequestMapping(value = "/comandos", method=RequestMethod.GET)
    public String showComandosPage(Model model){
    	model.addAttribute("serverList", getServerList());
    	model.addAttribute("comandos", getComandos());
    	return VIEW_NAME_COMANDOS_PAGE;
    }
    
    private List<Comando> getComandos() {
		
		return appServicios.findComandos();
	}


	@RequestMapping(value = "/usuarios", method=RequestMethod.GET)
    public String showUsuariosPage(Model model){
    	model.addAttribute("usuarios", getUsuarios());
    	model.addAttribute("serverList", getServerList());
    	return VIEW_NAME_USUARIOS_PAGE;
    }
    
    @RequestMapping(value="/usuario/editar", method=RequestMethod.GET)
    public String showEditUsuario(@RequestParam(value="login",required=true) String login, Model model){
    	model.addAttribute("serverList", getServerList());
    	model.addAttribute("usuario", getUserByLogin(login));
    	model.addAttribute("roles", roles());
    	return VIEW_NAME_EDIT_USUARIO_PAGE;
    }
    
    @RequestMapping(value="/usuario/add", method=RequestMethod.GET)
    public String showAddUsuario(Model model){
    	model.addAttribute("usuarios", getUsuarios());
    	model.addAttribute("serverList", getServerList());
    	model.addAttribute("usuario", new Usuario());
    	model.addAttribute("roles", roles());
    	return VIEW_NAME_NUEVO_USUARIO_PAGE;
    }
    
    @RequestMapping(value="/usuario/save", method=RequestMethod.POST)
    public String updateUsuario(@ModelAttribute(value="usuario") Usuario usuario, BindingResult result, Model model){
    	Usuario persistent = appServicios.updateUsuario(usuario);
    	return REDIRECT_NAME_USUARIOS_PAGE;
    }
    
    @RequestMapping(value="/usuario/new", method=RequestMethod.POST)
    public String saveUsuario(@ModelAttribute(value="usuario") Usuario usuario, BindingResult result, Model model){
    	Usuario persistent = appServicios.saveUsuario(usuario);
    	return REDIRECT_NAME_USUARIOS_PAGE;
    }
    
    @RequestMapping(value="/usuario/borrar", method=RequestMethod.GET)
    public String deleteUsuario(@RequestParam String login, Model model){
    	try {
			appServicios.deleteUserById(login);
		} catch (AppEntityException e) {
			return REDIRECT_NAME_USUARIOS_PAGE;
		}
    	return REDIRECT_NAME_USUARIOS_PAGE;
    }
    
    @RequestMapping(value="/servidor/borrar", method=RequestMethod.GET)
    public String deleteServidor(@RequestParam String ip, Model model){
    	try {
			appServicios.deleteServerById(ip);
		} catch (AppEntityException e) {
			return REDIRECT_NAME_SERVIDORES_PAGE;
		}
    	return REDIRECT_NAME_SERVIDORES_PAGE;
    }
    
    
    
    @RequestMapping(value="/servidor/editar", method=RequestMethod.GET)
    public String showEditServidor(@RequestParam(value="ip",required=true) String ip, Model model){
    	try {
    		model.addAttribute("usuarios", getUsuarios());
        	model.addAttribute("serverList", getServerList());
			model.addAttribute("servidor", getServidor(ip));
		} catch (AppEntityException e) {
			model.addAttribute("error", e.getMessage());
			return REDIRECT_NAME_SERVIDORES_PAGE;
		}
    	return VIEW_NAME_EDIT_SERVIDOR_PAGE;
    }
    
    @RequestMapping(value="/comando/save", method=RequestMethod.POST)
    public String updateComando(@ModelAttribute(value="comando") Comando comando, BindingResult result, Model model){
    	Comando persistent = appServicios.saveComando(comando);
    	return REDIRECT_NAME_COMANDOS_PAGE;
    }
    
    @RequestMapping(value="/comando/add", method=RequestMethod.GET)
    public String showAddComando(Model model){    	
    	model.addAttribute("serverList", getServerList());
    	model.addAttribute("comando", new Comando());    	
    	return VIEW_NAME_NUEVO_COMANDO_PAGE;
    }
    
    @RequestMapping(value="/comando/borrar", method=RequestMethod.GET)
    public String deleteComando(@RequestParam String id, Model model){
    	appServicios.deleteComando(id);
    	return REDIRECT_NAME_COMANDOS_PAGE;
    }
    
    
    
    @RequestMapping(value="/comando/editar", method=RequestMethod.GET)
    public String showEditComando(@RequestParam(value="id",required=true) String id, Model model){
        	model.addAttribute("serverList", getServerList());
			model.addAttribute("comando", getComandoById(id));
    	return VIEW_NAME_EDIT_COMANDO_PAGE;
    }
    
    private Comando getComandoById(String id) {
		
		return appServicios.findComandoById(id);
	}


	@RequestMapping(value="/servidor/add", method=RequestMethod.GET)
    public String showAddServidor(Model model){    	
    	model.addAttribute("serverList", getServerList());
    	model.addAttribute("servidor", new Servidor());    	
    	return VIEW_NAME_NUEVO_SERVIDOR_PAGE;
    }
	
	
    
    
    @RequestMapping(value="/servidor/save", method=RequestMethod.POST)
    public String saveServidor(@ModelAttribute Servidor servidor, Model model){
    	Servidor persistent = appServicios.saveServidor(servidor);
    	return REDIRECT_NAME_SERVIDORES_PAGE;
    }
    
    public List<Usuario> getUsuarios(){
    	List<Usuario> usuarios = new ArrayList<Usuario>();
    	usuarios = appServicios.findAllUsers();
    	return usuarios;
    }
    
    public Usuario getUserByLogin(String login){
    	return appServicios.findByLogin(login);
    }
    
    
    public String getUserDetails(){
    	Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	String username = new String("");
    	if (principal instanceof UserDetails) {
    	  username = ((UserDetails)principal).getUsername();
    	} else {
    	  username = principal.toString();
    	}
    	
    	return username;
    }
    
    public List<Servidor> getServerList(){
    	List<Servidor> servidores = new ArrayList<Servidor>(0);    	
    	servidores = appServicios.findAllServers();
    	return servidores;    	
    	
    }
    
    public Servidor getServidor(String ip) throws AppEntityException{
    	Servidor s = new Servidor();    	
    	s = appServicios.findByIp(ip);
    	s.setUser(stringEncriptor.decrypt(s.getUser()));
    	s.setPassword(stringEncriptor.decrypt(s.getPassword()));
    	return s;
    }
    
    
    public List<Rol> roles(){
    	return appServicios.getRoles();
    }
    
    
    
    
	
}
