package com.example.telegrambot.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
public class DbException extends BaseException {
    private static final String MESSAGE = "DataBase error";

    public DbException(String message) {
        super(message);
    }

    public DbException(Throwable cause) {
        super(MESSAGE,cause);
    }
}
