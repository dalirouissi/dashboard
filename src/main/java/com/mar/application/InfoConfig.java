package com.mar.application;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.actuate.endpoint.InfoEndpoint;
import org.springframework.boot.bind.PropertiesConfigurationFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MutablePropertySources;

@Configuration
public class InfoConfig {
	
	@Autowired
	private ConfigurableEnvironment environment;
	
	//@Value("${classpath:git.properties}")
	//private Resource gitProperties;
	
	/*
	 * how to override an endpoint (example).
	 * create new bean that will replace spring boot default bean (information).
	 * which means that if spring boot does not find a bean of such class in his factory
	 * it will create its own.
	 * 
	 * 
	 */
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Bean
	public InfoEndpoint infoEndPoint() throws Exception {
		MutablePropertySources propertySource = environment.getPropertySources();
		PropertiesConfigurationFactory factory = new PropertiesConfigurationFactory(new HashMap<String, Object>());
		factory.setTargetName("info");
		factory.setPropertySources(propertySource);
		Map info = new HashMap<String, Object>();
		info.put("info", factory.getObject());
		Properties props = new Properties();
		/*		PropertiesLoaderUtils.fillProperties(props, gitProperties);
		
		PropertiesConfigurationFactory gitFactory = new PropertiesConfigurationFactory(new HashMap<String, Object>());
		gitFactory.setTargetName("git");
		gitFactory.setProperties(props);
		info.put("git", gitFactory.getObject());
	*/	return new InfoEndpoint(info);
	}	
	
}
