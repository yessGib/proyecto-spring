package com.mitocode.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;

import reactor.core.publisher.Mono;

//Clase S7
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
public class WebSecurityConfig {
	
	@Bean
	public BCryptPasswordEncoder passwordEncoder() {  //libreria que me permite hacer un hash de textos //https://bcrypt-generator.com/
														// en el hahs no hay procedo inverso, solo se puede crear uno a partir de una palabra y comparar con otra palabra que se haga hash si coinciden, si lo hacen paan la prueba no se puede deriptar
		return new BCryptPasswordEncoder();
	}

	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private SecurityContextRepository securityContextRepository;
	
	@Bean
	public SecurityWebFilterChain securitygWebFilterChain(ServerHttpSecurity http) {
		return http
				.exceptionHandling()
				.authenticationEntryPoint((swe, e) -> {					
					return Mono.fromRunnable(() -> {
						swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);		// si la autenticacion es fallida se regresa un unauthorized				
					});
				}).accessDeniedHandler((swe, e) -> {					
					return Mono.fromRunnable(() -> {						
						swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN);		// si no tengo permisos regresa un forbidden
					});
				})
				.and()
				.csrf().disable()							// genera un token para enviarlo en cada form
				.formLogin().disable()						// antes spring hacia un form 
				.httpBasic().disable()
				.authenticationManager(authenticationManager)
				.securityContextRepository(securityContextRepository)	
				.authorizeExchange()
				.pathMatchers(HttpMethod.OPTIONS).permitAll()					
				//SWAGGER PARA SPRING SECURITY				
				.pathMatchers("/swagger-resources/**").permitAll()
				.pathMatchers("/swagger-ui.html").permitAll()
				.pathMatchers("/webjars/**").permitAll()
				//SWAGGER PARA SPRING SECURITY
				.pathMatchers("/login").permitAll()
				.pathMatchers("/v2/login").permitAll()
				.pathMatchers("/v2/**").authenticated()				// solo pide estar autencticado con cuaalquier rol
				//.pathMatchers("/v2/**").hasAnyAuthority("ADMIN")		//esto comentado es por si se requiere  hacer una validación sobre un tipo de rol específico
				/*.pathMatchers("/v2/**")
					.access((mono, context) -> mono
	                        .map(auth -> auth.getAuthorities()
	                        		.stream()
	                                .filter(e -> e.getAuthority().equals("ADMIN"))
	                                .count() > 0)
	                        .map(AuthorizationDecision::new)
	                )*/
				.pathMatchers("/platos/**").authenticated()
				.pathMatchers("/clientes/**").authenticated()
				.pathMatchers("/facturas/**").authenticated()
				.pathMatchers("/json/**").permitAll()
				.pathMatchers("/menus/**").authenticated()
				.anyExchange().authenticated()
				.and().build();
	}
}
