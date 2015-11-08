package com.mar.application;

import org.springframework.boot.test.SpringApplicationConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;

@SpringApplicationConfiguration(classes = Application.class)
public class ApplicationTests {
 

 	@Bean
 	public MappingJackson2HttpMessageConverter converter(){
 		return new MappingJackson2HttpMessageConverter();
 	}
	
}
