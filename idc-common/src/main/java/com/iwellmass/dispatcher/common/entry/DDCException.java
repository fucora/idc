package com.iwellmass.dispatcher.common.entry;

public class DDCException extends Exception {
	
	private static final long serialVersionUID = -1438534885376813360L;

	private String message;

	public DDCException() {
		super();
	}
	
	public DDCException(String message) {
		super();
		this.message = message;
	}
	
	public DDCException(String format, Object... args) {
		super();
		this.message = String.format(format, args);
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}
}
