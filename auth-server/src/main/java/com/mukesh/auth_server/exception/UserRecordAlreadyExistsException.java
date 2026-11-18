package com.mukesh.auth_server.exception;

public class UserRecordAlreadyExistsException extends RuntimeException {
    public UserRecordAlreadyExistsException(String message) {
        super(message);
    }
}
