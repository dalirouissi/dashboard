package com.mar.application;

import javax.annotation.Resource;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.elasticsearch.client.Client;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.common.transport.TransportAddress;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;

@SpringBootApplication
@ComponentScan("com")
@EnableAutoConfiguration
@EnableElasticsearchRepositories("com.mar.elasticsearch.repository")
public class Application {
	
	@Resource
	private Environment environment;
	
	Log logger = LogFactory.getLog(Application.class);
	
    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }
    
    @Bean
    public Client client(){
    	logger.info(" ----------------------    "+environment.getProperty("elasticsearch.host"));
    	TransportClient client = new TransportClient();
    	TransportAddress address = new InetSocketTransportAddress(
    			environment.getProperty("elasticsearch.host"), 
    			Integer.parseInt(environment.getProperty("elasticsearch.port")));
    	client.addTransportAddress(address);
    	
    	return client;
    }
    
}