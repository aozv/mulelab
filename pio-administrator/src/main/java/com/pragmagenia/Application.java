package com.pragmagenia;

import javax.annotation.Resource;

import static org.springframework.data.mongodb.core.query.Criteria.where;

import org.jasypt.encryption.StringEncryptor;
import org.jasypt.encryption.pbe.StandardPBEStringEncryptor;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;
import org.springframework.core.Ordered;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.MongoDbFactory;
import org.springframework.data.mongodb.core.MongoOperations;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.SimpleMongoDbFactory;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.crypto.password.StandardPasswordEncoder;
import org.springframework.util.Assert;
import org.springframework.web.servlet.config.annotation.ViewControllerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurerAdapter;
import org.thymeleaf.extras.springsecurity3.dialect.SpringSecurityDialect;
import org.thymeleaf.spring4.SpringTemplateEngine;

import com.mongodb.MongoClient;
import com.pragmagenia.component.WebSecurityConfig;
import com.pragmagenia.model.Usuario;
import com.pragmagenia.model.Usuario.Rol;
import com.pragmagenia.servicios.AppServicios;

@Configuration
@ComponentScan
@EnableAutoConfiguration
//@Import(value={WebSecurityConfig.class})
//@PropertySource("classpath:application.properties")
public class Application {
	
	@Resource
	private Environment env;
	
	
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	public @Bean MongoDbFactory mongoDbFactory() throws Exception {
		return new SimpleMongoDbFactory(new MongoClient("198.211.112.196"), "test");
	}

	public @Bean MongoTemplate mongoTemplate() throws Exception {
		MongoTemplate mongoTemplate = new MongoTemplate(mongoDbFactory());
		return mongoTemplate;

	}	
	
	@Bean
	public PasswordEncoder passwordEncoder(){

		return new StandardPasswordEncoder();
		
	}
	/**
	 * Agregamos informacion por defecto, ejemplo, usuario admin
	 * 
	 * @param mongoTemplate
	 */
	@Bean
	public InitializingBean addDefaultData(final AppServicios appServicios, final SpringTemplateEngine springTemplateEngine){
		
		return new InitializingBean() {
			
			@Override
			public void afterPropertiesSet() throws Exception {
				
				Usuario usuario = new Usuario();
				
				usuario.setLogin("admin");
				usuario.setContrasena("admin");
				usuario.setNombrecompleto("Administrador");
				usuario.setRol(Rol.ROLE_ADMIN);
				
				if(appServicios.findByLogin("admin") == null){
					appServicios.saveUsuario(usuario);
				}
				
				
				
				
			}
		};
		
		
		
	}
	
	@Bean
	public StringEncryptor stringEncryptor(){
		StandardPBEStringEncryptor spse = new StandardPBEStringEncryptor(); 
		spse.setAlgorithm("PBEWithMD5AndDES");
		spse.setPassword("jasypt");
		return spse;
	}
	
	
	
	@Bean
	public SpringSecurityDialect securityDialect(){
		return new SpringSecurityDialect();
	}
	
	
	
	
//	@Bean
//    public WebMvcConfigurerAdapter mvcViewConfigurer() {
//    	
//    	
//        return new WebMvcConfigurerAdapter() {
//            @Override
//            public void addViewControllers(ViewControllerRegistry registry) {
//            	registry.addViewController("/").setViewName("login");
//                registry.addViewController("/login").setViewName("login");
//                registry.addViewController("/login-error").setViewName("login");
//                registry.addViewController("/index").setViewName("index");
//                registry.addViewController("/server/command").setViewName("command");
//                registry.setOrder(Ordered.HIGHEST_PRECEDENCE);
//                
//                
//            }
//        };
//    }


}
