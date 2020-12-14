package com.mitocode.security;

import java.io.Serializable;
import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

//Clase S4
@Component
public class JWTUtil implements Serializable {
	//clase de configuración para crear el token 

	@Value("${jjwt.secret}") // con esta anotación le digo que lea el atributo que esta en el properties
	private String secret;
	
	@Value("${jjwt.expiration}")
	private String expirationTime; // tiempo de expiracion de token
	
	public Claims getAllClaimsFromToken(String token) { //cuando se haya recibido el token, lo pasamos por aqui para revisar su autenticidad
		return Jwts.parserBuilder()
				.setSigningKey(Base64.getEncoder().encodeToString(secret.getBytes()))
				.build()
				.parseClaimsJws(token)
				.getBody();
	}
	
	public String getUsernameFromToken(String token) {
		return getAllClaimsFromToken(token).getSubject();
	}
	
	public Date getExpirationDateFromToken(String token) {
		return getAllClaimsFromToken(token).getExpiration();
	}
	
	private Boolean isTokenExpired(String token) {  //para validar si el token es valido
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}
	
	//Aqui se agrega al payload del token
	public String generateToken(User user) {
		Map<String, Object> claims = new HashMap<>();
		claims.put("roles", user.getRoles());
		claims.put("test", "probando....");
		return doGenerateToken(claims, user.getUsername());
	}
	
	private String doGenerateToken(Map<String, Object> claims, String username) { // aqui ya genera el token por edio de la llave que se tiene en el properties
		Long expirationTimeLong = Long.parseLong(expirationTime);
		
		final Date createdDate = new Date();
		final Date expirationDate = new Date(createdDate.getTime() + expirationTimeLong * 1000);
		//forma antigua
		/*return Jwts.builder()
				.setClaims(claims)
				.setSubject(username)
				.setIssuedAt(createdDate)
				.setExpiration(expirationDate)
				.signWith(SignatureAlgorithm.HS512, Base64.getEncoder().encodeToString(secret.getBytes()))
				.compact();
		*/
		
		SecretKey key = Keys.hmacShaKeyFor(this.secret.getBytes());
		//forma actual
		return Jwts.builder()
				   .setClaims(claims)
				   .setSubject(username)
				   .setIssuedAt(createdDate)
				   .setExpiration(expirationDate)
				   .signWith(key)
				   .compact();		
	}
	
	public Boolean validateToken(String token) {
		return !isTokenExpired(token);
	}
}
