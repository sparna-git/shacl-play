package fr.sparna.rdf.shacl.shaclplay.exception;

import org.springframework.http.HttpStatus;

import java.time.LocalDateTime;

public class RestExceptionRenderer{

    private final String nameException;
    private final String message;
    private final HttpStatus statusCode;
    private final LocalDateTime dateTime;
    private final String stackTrace;

    public RestExceptionRenderer(String nameException, String message, HttpStatus statusCode, LocalDateTime dateTime, String stackTrace){
        this.nameException = nameException;
        this.message = message;
        this.statusCode = statusCode;
        this.dateTime = dateTime;
        this.stackTrace = stackTrace;
    }

    public String getNameException() {
        return nameException;
    }

    public String getMessage() {
        return message;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }

    public LocalDateTime getDateTime(){
        return this.dateTime;
    }

    public String getStackTrace(){
        return this.stackTrace;
    }



}
