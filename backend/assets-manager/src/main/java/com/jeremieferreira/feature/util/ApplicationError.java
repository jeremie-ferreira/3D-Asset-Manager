package com.jeremieferreira.feature.util;

import com.fasterxml.jackson.annotation.JsonIgnore;

public class ApplicationError extends Exception {

	private static final long serialVersionUID = 1L;
	
	public enum Code {
		TEMPLATE_NOT_FOUND,
		CONVERT_PDF,
		ERROR_SAVE_FILE,
		TEMPLATE_FILLING,
		USER_ALREADY_EXISTS,
		UNAUTHORIZED_OPERATION,
		USER_NOT_FOUND,
		ERROR_GET_FILE,
		SENDING_FILE,
		FILE_ERROR,
		PASSWORDS_DO_NOT_MATCH,
		AUTHENTICATION_FAILED, SENDING_MAIL,
		HTTP_CONNECTION_ERROR
	}
	
	private Code code;
	private Object reference;
	private Throwable cause;
	
	
	public ApplicationError(Code code) {
		this(code, null, null);
	}
	
	public ApplicationError(String message) {
		super(message);
	}
	
	public ApplicationError(String message, Throwable cause) {
		super(message, cause);
	}
	
	public ApplicationError(Code code, Object reference) {
		this(code, reference, null);
	}
		
	public ApplicationError(Code code, Object reference, Throwable cause) {
		super(code.name(), cause);
		this.code = code;
		this.reference = reference;
		this.cause = cause;
	}
	
	public Code getCode() {
		return code;
	}

	public void setCode(Code code) {
		this.code = code;
	}

	public Object getReference() {
		return reference;
	}

	public void setReference(Object reference) {
		this.reference = reference;
	}

	public Throwable getCause() {
		return cause;
	}

	public void setCause(Throwable cause) {
		this.cause = cause;
	}
	
	@Override
	@JsonIgnore
	public StackTraceElement[] getStackTrace() {
		return super.getStackTrace();
	}
}
