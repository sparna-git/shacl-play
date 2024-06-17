package fr.sparna.jsonschema;

public class Main {
    
public static void main(String... args) throws Exception {
    EmptySchema s = EmptySchema.builder().id("myId").title("This is my schema").build();
    System.out.println(s.toString());
}

}
