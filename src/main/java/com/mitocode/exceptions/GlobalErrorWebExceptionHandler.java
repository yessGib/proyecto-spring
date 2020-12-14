package com.mitocode.exceptions;

import java.util.HashMap;
import java.util.Map;

import org.springframework.boot.autoconfigure.web.ResourceProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.RequestPredicate;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;

import reactor.core.publisher.Mono;

@Component  //para clases de apoyo utilitarias
//@Order(-1)  // en caso de que aún siga intentando entrar en accion el validator de spring y quiera enviar su mensjae de error, le diremos que priorice lo que indica esta clase
public class GlobalErrorWebExceptionHandler extends AbstractErrorWebExceptionHandler{

	public GlobalErrorWebExceptionHandler(ErrorAttributes errorAttributes, ResourceProperties resourceProperties,
			ApplicationContext applicationContext, ServerCodecConfigurer configurer) {
		super(errorAttributes, resourceProperties, applicationContext);
		this.setMessageWriters(configurer.getWriters());
	}
	
	@Override
	protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
		// TODO Auto-generated method stub
		return RouterFunctions.route(RequestPredicates.all(), this::renderErrorResponse);
	}
	
	private Mono<ServerResponse> renderErrorResponse(ServerRequest request){
		Map<String, Object> errorGeneral = getErrorAttributes(request, false);
		Map<String, Object> mapException = new HashMap<>();
		var httpStatus = HttpStatus.INTERNAL_SERVER_ERROR; // con var se ke dice que infiera el tipo de variable
		String statusCode = String.valueOf(errorGeneral.get("status"));
		switch (statusCode) {
		case "500":
			mapException.put("error", "500");
			mapException.put("excepcion", "error del backend");
			httpStatus = HttpStatus.INTERNAL_SERVER_ERROR;
			break;
		case "400":
			mapException.put("error", "400");
			mapException.put("excepcion", "Petición incorrecta");
			httpStatus = HttpStatus.BAD_REQUEST;
			break;
		default:
			mapException.put("error", "900");
			mapException.put("excepcion", "Otro error");
			httpStatus = HttpStatus.CONFLICT;
			break;
		}
		mapException.put("excepcion", errorGeneral.toString());
		return ServerResponse.status(HttpStatus.INTERNAL_SERVER_ERROR)
				.contentType(MediaType.APPLICATION_JSON)
				.body(BodyInserters.fromValue(mapException));
		
	}
}
