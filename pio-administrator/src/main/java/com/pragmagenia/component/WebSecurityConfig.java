package com.pragmagenia.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.servlet.configuration.EnableWebMvcSecurity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.pragmagenia.servicios.AppServicios;
import com.pragmagenia.servicios.AppServiciosImpl;


@Configuration
@EnableWebMvcSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter{
	
	@Autowired
	private AppServicios appServicios;
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	
	
	@Override
	public void configure(WebSecurity web) throws Exception {
		
		web
        .ignoring()
            .antMatchers("/assets/**")
            .antMatchers("/css/**")
            .antMatchers("/img/**")
            .antMatchers("/js/**")
            .antMatchers("/fonts/**");
	}

	@Override
	protected void configure(HttpSecurity http) throws Exception {
		

		http
		.csrf().disable()
		.authorizeRequests()
			.antMatchers("/static/**").permitAll()
			.anyRequest().authenticated()
			.antMatchers("/rest/**").permitAll()
			.antMatchers("/**").access("isFullyAuthenticated()")
			.and()
		.formLogin()
			.loginPage("/login").failureUrl("/login-error").defaultSuccessUrl("/index", true).usernameParameter("username").passwordParameter("password")
		.permitAll()
		.and()
			.logout().logoutUrl("/logout").logoutSuccessUrl("/login").deleteCookies("JSESSIONID")
		.and()
		.sessionManagement()
			.invalidSessionUrl("/login")
		.and()
		.exceptionHandling().accessDeniedPage("/406")
		.and()
		.httpBasic().realmName("PragmageniaAPP");
		
		
	}

	@Override
	protected void configure(AuthenticationManagerBuilder auth)
			throws Exception {
		auth.userDetailsService(appServicios).passwordEncoder(passwordEncoder);
	}
	
	

	

	
	
	

}
