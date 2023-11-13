package com.tecnalia.datausage.service.impl;

import java.io.IOException;
import java.util.Objects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.tecnalia.datausage.service.SelfDescriptionService;

import de.fraunhofer.iais.eis.Connector;
import de.fraunhofer.iais.eis.ids.jsonld.Serializer;
import io.dataspaceconnector.exceptions.InternalRecipientException;
import io.dataspaceconnector.exceptions.TemporarilyNotAvailableException;

@Service
public class SelfDescriptionServiceImpl implements SelfDescriptionService {

	private static final Logger logger = LoggerFactory.getLogger(SelfDescriptionServiceImpl.class);

	private static Serializer serializer;

	static {
		serializer = new Serializer();
	}

	private RestTemplate restTemplate;
	@Value("${application.ecc.selfdescription.url}")
	private String selfDescriptionURL;
	@Value("${application.ecc.selfdescription.port}")
	private int selfDescriptionPort;

	public SelfDescriptionServiceImpl(RestTemplateBuilder restTemplateBuilder) {
		this.restTemplate = restTemplateBuilder.build();

	}


	@Override
	public Connector getSelfDescription() {
		try {
			ResponseEntity<String> response = restTemplate.exchange(selfDescriptionURL + ":" + selfDescriptionPort, HttpMethod.GET, null, String.class);

			if (response == null) {
				throw new TemporarilyNotAvailableException("Could not fetch self description, ECC did not respond");
			}

			if (response.getStatusCodeValue() != HttpStatus.OK.value()) {
				String errorMessage = response.getBody();
				logger.error("Could not fetch self description, ECC responded with status {} and message \r{}", response.getStatusCodeValue(), errorMessage);
				throw new InternalRecipientException("Could not fetch self description: " + errorMessage);
			}

			String selfDescription = Objects.requireNonNull(response.getBody(), "Response body must not be null");
			logger.info("Deserializing self description.");
			logger.debug("Self description content: {}{}", System.lineSeparator(), selfDescription);

			return serializer.deserialize(selfDescription, Connector.class);
		} catch (IOException e) {
			logger.error("Could not deserialize self description to Connector instance", e);
			throw new InternalRecipientException("Could not deserialize self description to Connector instance", e);
		}
	}
}
