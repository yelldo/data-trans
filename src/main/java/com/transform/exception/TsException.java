package com.transform.exception;

public class TsException extends RuntimeException {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;
	private String code;
	public TsException() {
		super();

	}

	public TsException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
		super(message, cause, enableSuppression, writableStackTrace);

	}

	public TsException(String message, Throwable cause) {
		super(message, cause);

	}

	public TsException(String message) {
		super(message);

	}

	public TsException(Throwable cause) {
		super(cause);

	}

	public TsException(String code, String msg, Throwable e) {
		super(msg, e);
		this.code = code;
	}

	public TsException(String code, String message) {
		this(code, message, null);
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

}
