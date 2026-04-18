package com.openwebinars.todo.user.exception;

public class EmailAlreadyExistsException extends RuntimeException {

    public EmailAlreadyExistsException() {
        super("El email ya existe");
    }

    public EmailAlreadyExistsException(String message) {
        super(message);
    }
}