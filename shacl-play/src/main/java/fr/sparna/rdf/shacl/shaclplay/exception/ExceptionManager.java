package fr.sparna.rdf.shacl.shaclplay.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.time.LocalDateTime;

public enum ExceptionManager {


    INTERNAL_SERVER_ERROR("Unexpected behaviour. Please try again.")
    ;

    private final static Logger LOGGER = LoggerFactory.getLogger(ExceptionManager.class);

    private final String message;

    public String getMessage(){
        return this.message;
    }

    ExceptionManager(String message){
        this.message = message;
    }

    public static void throwException(Class<? extends Exception> klass, String msg){
        if(DocException.class == klass) {
            DocException e = new DocException(msg);
            e.printStackTrace();
            throw e;
        }
        if(DrawException.class == klass){
            DrawException e = new DrawException(msg);
            e.printStackTrace();
            throw e;
        }
        //Add other exceptions here ...
    }

    public static String getStackTrace(Throwable t){
        StringWriter writer = new StringWriter();
        t.printStackTrace(new PrintWriter(writer));
        return writer.toString();
    }

    public static ResponseEntity<RestExceptionRenderer> prepareRestExceptionRenderer(Throwable t, HttpStatus status, LocalDateTime dateTime){
        return new ResponseEntity<>(
                new RestExceptionRenderer(t.getClass().getName(), t.getMessage(), status, dateTime, ExceptionManager.getStackTrace(t)), status
        );
    }

}
