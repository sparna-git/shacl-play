package fr.sparna.jsonschema.model;

import java.util.List;
import java.util.Map;
import java.util.Set;

abstract class Visitor {

    void visitSchema(Schema schema) {

    }

    void visit(Schema schema) {
        schema.accept(this);
    }

    void visitEmptySchema(EmptySchema emptySchema) {
        visitSchema(emptySchema);
    }

}
