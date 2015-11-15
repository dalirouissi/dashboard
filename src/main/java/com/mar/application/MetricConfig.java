package com.mar.application;

import java.net.InetSocketAddress;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.actuate.metrics.GaugeService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;

import com.codahale.metrics.JmxReporter;
import com.codahale.metrics.MetricRegistry;
import com.codahale.metrics.graphite.Graphite;
import com.codahale.metrics.graphite.GraphiteReporter;

@Configuration
public class MetricConfig {

	@Autowired
	private MetricRegistry metricRegistry;

	@Value("${graphite.host}")
	private String graphiteHost;

	@Value("${graphite.cabonPort}")
	private String carbonPort;

	@Bean
	public JmxReporter jmxReporter() {
		JmxReporter reporter = JmxReporter.forRegistry(metricRegistry).build();
		reporter.start();
		return reporter;
	}

	/*
	 * @Bean
	 * 
	 * @Profile("prod") public GraphiteReporter reporter(){ return
	 * Mock.reported(); }
	 */

	@Bean
	// @ConditionalOnMissingBean(name = "reporter")
	public GraphiteReporter graphiteReporter() {
		// TODO : fix Value injection.When injection values connection to
		// graphite is not working
		Graphite graphite = new Graphite(new InetSocketAddress("localhost", 2003));
		GraphiteReporter reporter = GraphiteReporter.forRegistry(metricRegistry).prefixedWith("s2gx").build(graphite);
		reporter.start(500, TimeUnit.MILLISECONDS);
		return reporter;
	}

	@Bean
	public CustomGaugeMetric gaugeMesures(GaugeService gaugeService) {
		return new CustomGaugeMetric(gaugeService);
	}
	
	
	public static class CustomGaugeMetric {

		private final GaugeService gaugeService;
		private Random random;

		public CustomGaugeMetric(GaugeService gaugeService) {
			this.gaugeService = gaugeService;
			random = new Random();
		}

		@Scheduled(fixedRate = 500)
		public void pushScheduledGauge() {
			System.err.println("Calling scheduled pushGauge.....");
			gaugeService.submit("s2gx.random", random.nextInt(100));
		}
	}
}
