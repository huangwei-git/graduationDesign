package com.songlian.logistics.exception;

public class RequestExpcetion extends Exception{
    private int code;

    public RequestExpcetion(int code,String message) {
        super(message);
        this.code = code;
    }
}
