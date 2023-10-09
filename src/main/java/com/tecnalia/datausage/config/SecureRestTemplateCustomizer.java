package com.tecnalia.datausage.config;

import java.util.Arrays;

import org.apache.http.client.HttpClient;
import org.apache.http.conn.socket.LayeredConnectionSocketFactory;
import org.apache.http.conn.ssl.DefaultHostnameVerifier;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.ssl.SSLContextBuilder;
import org.apache.http.ssl.SSLContexts;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateCustomizer;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

/**
 * Used for Customized TLS security, to avoid PKIX error.
 * When using RestTemplate call restTemplateBuilder.build().
 */
@Component
public class SecureRestTemplateCustomizer implements RestTemplateCustomizer {

	private static final Logger logger = LoggerFactory.getLogger(SecureRestTemplateCustomizer.class);

	@Value("${server.ssl.key-store}")
	private String trustStore;
	@Value("${server.ssl.key-store-password}")
	String trustStorePassword;

	@Override
	public void customize(RestTemplate restTemplate) {
		SSLContextBuilder sslcontextBuilder = SSLContexts.custom();
		HttpClient httpClient;
		try {
			sslcontextBuilder.loadTrustMaterial(null, (cert, auth) -> true);

			SSLConnectionSocketFactory sslConnectionSocketFactory = new SSLConnectionSocketFactory(
					sslcontextBuilder.build(),
					new DefaultHostnameVerifier()
			);
			httpClient = HttpClients.custom()
					.setSSLSocketFactory((LayeredConnectionSocketFactory) sslConnectionSocketFactory).build();
			final ClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory(httpClient);
			logger.info("Registered SSL truststore {} for client requests", trustStore);
			restTemplate.setRequestFactory(requestFactory);
		} catch (Exception e) {
			throw new IllegalStateException("Failed to setup client SSL context", e);
		} finally {
			// it's good security practice to zero out passwords,
			// which is why they're char[]
			Arrays.fill(trustStorePassword.toCharArray(), (char) 0);
		}
	}
}
