package br.com.cea.monitor.exception;

public class UfNotFoundException extends RuntimeException {
	
	private static final long serialVersionUID = 1L;

	public UfNotFoundException(String msg) {
		super(msg);
	}
}
