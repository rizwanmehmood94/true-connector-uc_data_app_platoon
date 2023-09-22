package io.dataspaceconnector.exceptions;

import de.fraunhofer.iais.eis.Message;

public class TemporarilyNotAvailableException extends RuntimeException {

	private static final long serialVersionUID = -2712820209827697302L;

	private Message header;

	public TemporarilyNotAvailableException(String message) {
		super(message);
	}

	public Message getHeader() {
		return header;
	}

	public void setHeader(Message header) {
		this.header = header;
	}
}
