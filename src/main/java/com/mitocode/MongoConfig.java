package com.mitocode;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfigureOrder;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.mongodb.core.convert.DefaultMongoTypeMapper;
import org.springframework.data.mongodb.core.convert.MappingMongoConverter;

@Configuration
public class MongoConfig implements InitializingBean{

	@Lazy //para indicarle que solo instancie cuando sea necesario, si no se pone se instancia al inicalizar el sistema
	@Autowired
	private MappingMongoConverter mappingMongoConverter;
	
	@Override
	public void afterPropertiesSet() throws Exception {
		// con esto se omite que agregue   "_class" : "com.mitocode.model.Factura" en mongo al hacer insercion 	
		mappingMongoConverter.setTypeMapper(new DefaultMongoTypeMapper(null));
		
	}

}
