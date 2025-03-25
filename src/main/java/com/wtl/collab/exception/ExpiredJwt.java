package com.wtl.collab.exception;

public class ExpiredJwt extends RuntimeException {
    public ExpiredJwt(String message) {
        super(message);
    }
}
