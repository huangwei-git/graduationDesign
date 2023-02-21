package com.songlian.logistics.exception;

public class RequestExpcetion extends RuntimeException{
    private int code;

    public RequestExpcetion(int code,String message) {
        super(message);
        this.code = code;
    }
}
