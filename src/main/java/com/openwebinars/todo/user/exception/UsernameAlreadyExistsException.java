package com.openwebinars.todo.user.exception;

public class UsernameAlreadyExistsException extends RuntimeException {

    public UsernameAlreadyExistsException() {
        super("El username ya existe");
    }

    public UsernameAlreadyExistsException(String message) {
        super(message);
    }
}