package com.xenoamess.krakatau_java_wrapper.exception;

/**
 * @author XenoAmess
 */
public class CannotFindOutputException extends RuntimeException {

    public CannotFindOutputException() {
        super();
    }

    public CannotFindOutputException(
            String message
    ) {
        super(message);
    }

    public CannotFindOutputException(
            String message,
            Throwable cause
    ) {
        super(message, cause);
    }

    public CannotFindOutputException(
            Throwable cause
    ) {
        super(cause);
    }

    protected CannotFindOutputException(
            String message,
            Throwable cause,
            boolean enableSuppression,
            boolean writableStackTrace
    ) {
        super(message, cause, enableSuppression, writableStackTrace);
    }

}
