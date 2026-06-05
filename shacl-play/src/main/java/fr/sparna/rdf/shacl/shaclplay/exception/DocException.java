package fr.sparna.rdf.shacl.shaclplay.exception;

public class DocException extends RuntimeException{

    public DocException(){
        super();
    };

    public DocException(String message){
        super(message);
    }

    public DocException(Throwable cause) {
        super(cause);
    }
}
