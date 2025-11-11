package com.example.projectmanagement.exceptions;

public class UserAlreadyExistsException  extends  RuntimeException{
    public UserAlreadyExistsException(String message){
        super(message);
    }
}
