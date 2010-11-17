package net.metadata.dataspace.error;

/**
 * Author: alabri
 * Date: 16/11/2010
 * Time: 3:37:08 PM
 */
public class AdapterException extends Exception {
    private static final long serialVersionUID = 1L;

    public AdapterException() {
    }

    public AdapterException(String message) {
        super(message);
    }

    public AdapterException(Throwable e) {
        this.setStackTrace(e.getStackTrace());
    }

    public AdapterException(String message, Throwable throwable) {
        super(message, throwable);
    }

}
