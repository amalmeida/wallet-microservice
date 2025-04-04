package com.walletapp.domain.exception;

public class InvalidDateFormatException extends RuntimeException {
    public InvalidDateFormatException(String date) {
        super("Formato de data inválido: " + date + ". Use o formato yyyy-MM-dd'T'HH:mm:ss ou yyyy-MM-dd HH:mm:ss");
    }
}
