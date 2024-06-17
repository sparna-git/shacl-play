package fr.sparna.jsonschema;

import fr.sparna.jsonschema.model.EmptySchema;
import fr.sparna.jsonschema.model.Schema.Builder;

public class Main {
    
public static void main(String... args) throws Exception {
    Builder<EmptySchema> b = EmptySchema.builder().id("myId").title("This is my schema");

    Builder<EmptySchema> b2 = EmptySchema.builder().id("id2").title("schema 2");

    b.embeddedSchema("AdoptedText", b2.build());

    System.out.println(b.build().toString());
}

}
