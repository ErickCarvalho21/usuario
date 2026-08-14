package com.javanauta.usuario.infrastructure.exceptions;

public class ILLegalArgumentsException extends RuntimeException {
    public ILLegalArgumentsException(String message) {
        super(message);
    }

    public ILLegalArgumentsException(String mensagem, Throwable throwable){
        super(mensagem, throwable);
    }
}
