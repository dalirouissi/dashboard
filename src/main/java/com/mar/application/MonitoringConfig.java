package com.mar.application;

import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class MonitoringConfig {

	@Bean(name = "dashborad health checker")
	public HealthIndicator customHealthChecker(){
		return new HealthIndicator() {			
			@Override
			public Health health() {
				return Health.down(new IllegalArgumentException("Override the health check status down "))
						.withDetail("BOOT", "ROCKS")
						.build();
			}
		};
	}
	
}
