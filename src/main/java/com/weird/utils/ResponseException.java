package com.weird.utils;

/**
 * 对话中抛出的异常，需要回复异常内容
 *
 * @author Nidhogg
 * @date 2021.10.26
 */
public class ResponseException extends Exception {
    public ResponseException(String cause) {
        super(cause);
    }

    public ResponseException(String format, Object... args){
        super(String.format(format, args));
    }
}
