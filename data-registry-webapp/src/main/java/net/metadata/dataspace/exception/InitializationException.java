package net.metadata.dataspace.exception;

/**
 * User: alabri
 * Date: 14/09/2010
 * Time: 10:44:11 AM
 */
public class InitializationException extends Exception {

    private static final long serialVersionUID = 1L;

    public InitializationException(String message) {
        super(message);
    }

    public InitializationException(String message, Throwable rootCause) {
        super(message, rootCause);
    }
}
