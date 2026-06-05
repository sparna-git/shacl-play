package fr.sparna.rdf.shacl.shaclplay.exception;

public class DrawException extends RuntimeException{

    public DrawException(){
        super();
    };

    public DrawException(String message){
        super(message);
    }

    public DrawException(Throwable cause) {
        super(cause);
    }
}
