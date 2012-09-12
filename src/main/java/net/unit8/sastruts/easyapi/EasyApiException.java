package net.unit8.sastruts.easyapi;

import java.util.Iterator;

@SuppressWarnings("serial")
public class EasyApiException extends Exception {
	protected String messageCode;
	protected String message;

	protected EasyApiException next;
	protected EasyApiException prev;

	public EasyApiException(String messageCode) {
		this.messageCode = messageCode;
	}

	public EasyApiException append(EasyApiException e) {
		EasyApiException lastException = this;
		// TODO Need a cyclic reference stopper.
		while(lastException.next != null) {
			lastException = lastException.next;
		}
		lastException.next = e;
		e.prev = lastException;

		return e;
	}

	public EasyApiException first() {
		EasyApiException first = this;
		while(first.prev != null) {
			first = first.prev;
		}
		return first;
	}

	public Iterator<EasyApiException> iterator() {
		return new EasyApiExceptionIterator(this);
	}

	public String getMessageCode() {
		return messageCode;
	}

	@Override
	public String getMessage() {
		return message;
	}
}
