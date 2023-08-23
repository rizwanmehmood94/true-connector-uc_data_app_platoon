package com.tecnalia.datausage.service;

import de.fraunhofer.iais.eis.*;
import de.fraunhofer.iais.eis.ids.jsonld.Serializer;

import io.dataspaceconnector.exceptions.InternalRecipientException;
import io.dataspaceconnector.exceptions.NotFoundException;
import io.dataspaceconnector.exceptions.TemporarilyNotAvailableException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;

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


			ResponseEntity<String> response = restTemplate.exchange(selfDescriptionURL+ ":" +selfDescriptionPort, HttpMethod.GET, null, String.class);
			if (response != null) {
				if (response.getStatusCodeValue() == 200) {
					String selfDescription = response.getBody();
					logger.info("Deserializing self description.");
					logger.debug("Self description content: {}{}", System.lineSeparator(), selfDescription);

					return serializer.deserialize(selfDescription, Connector.class);
				} else {
					logger.error("Could not fetch self description, ECC responded with status {} and message \r{}",
							response.getStatusCodeValue(), response.getBody());

					throw new InternalRecipientException("Could not fetch self description");
				}
			}
			logger.error("Could not fetch self description, ECC did not respond");

			throw new TemporarilyNotAvailableException("Could not fetch self description, ECC did not respond");
		} catch (IOException e) {
			logger.error("Could not deserialize self description to Connector instance", e);

			throw new InternalRecipientException("Could not deserialize self description to Connector instance");
		}
	}
	@Override
	public Connector getSelfDescription(Message message) {
		try {
		/*	URI eccURI = new URI(eccProperties.getProtocol(), null, eccProperties.getHost(), eccProperties.getSelfdescriptionPort(),
					null, null, null);
			logger.info("Fetching self description from ECC {}", eccURI.toString());*/

			ResponseEntity<String> response = restTemplate.exchange("https://localhost:8443", HttpMethod.GET, null, String.class);
			if (response != null) {
				if (response.getStatusCodeValue() == 200) {
					String selfDescription = response.getBody();
					logger.info("Deserializing self description.");
					logger.debug("Self description content: {}{}", System.lineSeparator(), selfDescription);

					return serializer.deserialize(selfDescription, Connector.class);
				} else {
					logger.error("Could not fetch self description, ECC responded with status {} and message \r{}",
							response.getStatusCodeValue(), response.getBody());

					throw new InternalRecipientException("Could not fetch self description", message);
				}
			}
			logger.error("Could not fetch self description, ECC did not respond");

			throw new TemporarilyNotAvailableException("Could not fetch self description, ECC did not respond",
					message);
		} catch (IOException e) {
			logger.error("Could not deserialize self description to Connector instance", e);

			throw new InternalRecipientException("Could not deserialize self description to Connector instance",
					message);
		}
	}

	@Override
	public String getSelfDescriptionAsString(Message message) {
	/*	try {
			return MultipartMessageProcessor.serializeToJsonLD(getSelfDescription(message));
		} catch (IOException e) {
			logger.error("Could not serialize self description", e);

			throw new InternalRecipientException("Could not serialize self description", message);
		}*/
	return null;
	}

	@Override
	public boolean artifactRequestedElementExist(ArtifactRequestMessage message, Connector connector) {
		for (ResourceCatalog catalog : connector.getResourceCatalog()) {
			for (Resource offeredResource : catalog.getOfferedResource()) {
				for (Representation representation : offeredResource.getRepresentation()) {
					for (RepresentationInstance instance : representation.getInstance()) {
						if (message.getRequestedArtifact().equals(instance.getId())) {
							return true;
						}
					}
				}
			}

		}
		logger.error("Requested element not found.");

		throw new NotFoundException("Requested element not found", message);
	}

	@Override
	public String getRequestedElement(DescriptionRequestMessage message, Connector connector) {
		for (ResourceCatalog catalog : connector.getResourceCatalog()) {
			for (Resource offeredResource : catalog.getOfferedResource()) {
				if (message.getRequestedElement().equals(offeredResource.getId())) {
/*					try {

						//return MultipartMessageProcessor.serializeToJsonLD(offeredResource);
					} catch (IOException e) {
						logger.error("Could not serialize requested element.", e);

						throw new InternalRecipientException("Could not serialize requested element", message);
					}*/
				}
			}
		}
		logger.error("Requested element not found.");

		throw new NotFoundException("Requested element not found", message);
	}
}
