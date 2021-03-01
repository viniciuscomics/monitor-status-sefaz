package br.com.cea.monitor.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import lombok.Data;

@Data
@Configuration
@ConfigurationProperties("monitor")
public class ApiConfiguration {

	private String urlSefaz;
	private Integer timeoutSecond;	
}
