package fr.sparna.rdf.shacl.validator;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.helpers.MessageFormatter;

public class ShaclValidatorException extends RuntimeException {

	private static final long serialVersionUID = 1L;

	public static void when(boolean test) {
		if (test) {
			throw new ShaclValidatorException("Assertion failed");
		}
	}

	public static void when(boolean test, String message) {
		if (test) {
			throw new ShaclValidatorException(message);
		}
	}

	public static void when(boolean test, String message, Object... parameters) {
		if (test) {
			throw new ShaclValidatorException(message, parameters);
		}
	}

	public static ShaclValidatorException rethrow(Throwable exception) {
		if (exception instanceof Error) {
			throw (Error) exception;
		}
		if (exception instanceof RuntimeException) {
			throw (RuntimeException) exception;
		}
		throw new ShaclValidatorException(exception);
	}

	public static <T> T failIfNotInstance(Object object, Class<T> clazz, String message, Object... parameters) {
		when(!clazz.isInstance(object), message, parameters);
		//noinspection unchecked
		return (T) object;
	}

	public static <T> T failIfNull(T value, String message, Object... parameters) {
		when(null == value, message, parameters);
		//noinspection ConstantConditions
		return value;
	}

	public static <T extends CharSequence> T failIfBlank(T value, String message, Object... parameters) {
		when(StringUtils.isBlank(value), message, parameters);
		//noinspection ConstantConditions
		return value;
	}

	public ShaclValidatorException() {
	}

	public ShaclValidatorException(String message) {
		super(message);
	}

	public ShaclValidatorException(Throwable cause, String message, Object... parameters) {
		super(MessageFormatter.arrayFormat(message, parameters).getMessage(), cause);
	}

	public ShaclValidatorException(String message, Object... parameters) {
		super(MessageFormatter.arrayFormat(message, parameters).getMessage());
	}

	public ShaclValidatorException(Throwable cause) {
		super(cause);
	}
}
