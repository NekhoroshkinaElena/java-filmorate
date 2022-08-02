package ru.yandex.practicum.filmorate.exeption;

public class ValidationException extends Exception{

    public ValidationException(){
        super();
    }

    public ValidationException(String message){
        super(message);
    }
}
