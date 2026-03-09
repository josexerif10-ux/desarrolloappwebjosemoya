package com.openwebinars.todo.task.exception;

public class EmptyTaskListException extends RuntimeException {
    public EmptyTaskListException(String message) {
        super(message);
    }

    public EmptyTaskListException() {

    }
}
