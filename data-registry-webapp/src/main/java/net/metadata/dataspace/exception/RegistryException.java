package net.metadata.dataspace.exception;

/**
 * User: alabri
 * Date: 29/09/2010
 * Time: 2:03:21 PM
 */
public class RegistryException extends Exception {

    private static final long serialVersionUID = 1L;

    public RegistryException(String message) {
        super(message);
    }

    public RegistryException(String message, Throwable rootCause) {
        super(message, rootCause);
    }
}

