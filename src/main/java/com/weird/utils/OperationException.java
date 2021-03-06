package com.weird.utils;

/**
 * 操作错误抛出的异常
 *
 * @author Nidhogg
 */
public class OperationException extends Exception {
    public OperationException(String cause) {
        super(cause);
    }

    public OperationException(String format, Object... args){
        super(String.format(format, args));
    }
}
