package net.unit8.sastruts.easyapi;

import java.util.Iterator;

public class EasyApiExceptionIterator implements Iterator<EasyApiException> {
	private EasyApiException current;
	private boolean firstElement = true;
	
	public EasyApiExceptionIterator(EasyApiException ex) {
		this.current = ex.first();
	}
	public boolean hasNext() {
		
		return firstElement ? current != null : current.next != null;
	}

	public EasyApiException next() {
		if (firstElement) {
			firstElement = false;
		} else {
			current = current.next;
		}
		return current;
	}

	public void remove() {
		throw new UnsupportedOperationException("remove");
	}
}
