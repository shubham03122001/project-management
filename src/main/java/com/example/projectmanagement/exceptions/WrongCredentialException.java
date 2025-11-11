package com.example.projectmanagement.exceptions;

public class WrongCredentialException extends RuntimeException{

    public WrongCredentialException(String message){
        super(message);
    }
}
