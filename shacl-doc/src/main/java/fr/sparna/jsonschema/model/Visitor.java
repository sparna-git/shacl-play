package fr.sparna.jsonschema.visitor;

import java.util.List;
import java.util.Map;
import java.util.Set;

import fr.sparna.jsonschema.model.EmptySchema;
import fr.sparna.jsonschema.model.Schema;

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
