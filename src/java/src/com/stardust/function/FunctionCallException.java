package com.stardust.function;

/**
 * Created by Stardust on 2016/11/2.
 */
public class FunctionCallException extends RuntimeException {
    
	private static final long serialVersionUID = -4567412530058038940L;

	public FunctionCallException() {
        super();
    }

    public FunctionCallException(String message) {
        super(message);
    }

    public FunctionCallException(String message, Throwable cause) {
        super(message, cause);
    }

    public FunctionCallException(Throwable cause) {
        super(cause);
    }
}
