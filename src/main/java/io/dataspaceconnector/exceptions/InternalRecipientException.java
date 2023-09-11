package io.dataspaceconnector.exceptions;

import de.fraunhofer.iais.eis.Message;

public class InternalRecipientException extends RuntimeException {

	private static final long serialVersionUID = -3764087406348269338L;
	private Message header;

	public InternalRecipientException(String message) {
		super(message);
	}

	public InternalRecipientException(String message, Throwable cause) {
		super(message, cause);
	}

	public Message getHeader() {
		return header;
	}

	public void setHeader(Message header) {
		this.header = header;
	}
}
