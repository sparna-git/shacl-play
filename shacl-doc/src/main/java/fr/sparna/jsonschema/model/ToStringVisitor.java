package fr.sparna.jsonschema.visitor;

import java.util.List;
import java.util.Map;
import java.util.Set;


import org.json.JSONException;

import fr.sparna.jsonschema.model.EmptySchema;
import fr.sparna.jsonschema.model.Schema;
import fr.sparna.jsonschema.model.SpecificationVersion;
import fr.sparna.jsonschema.model.TrueSchema;
import fr.sparna.jsonschema.writer.JSONPrinter;

class ToStringVisitor extends Visitor {

    private final JSONPrinter writer;

    private boolean jsonObjectIsOpenForCurrentSchemaInstance = false;

    private boolean skipNextObject = false;

    private SpecificationVersion deducedSpecVersion;

    ToStringVisitor(JSONPrinter writer) {
        this.writer = writer;
    }

    @Override void visitSchema(Schema schema) {
        if (schema == null) {
            return;
        }
        if (!jsonObjectIsOpenForCurrentSchemaInstance) {
            writer.object();
        }
        writer.ifPresent("title", schema.getTitle());
        writer.ifPresent("description", schema.getDescription());
        writer.ifPresent("nullable", schema.isNullable());
        writer.ifPresent("default", schema.getDefaultValue());
        writer.ifPresent("readOnly", schema.isReadOnly());
        writer.ifPresent("writeOnly", schema.isWriteOnly());
        super.visitSchema(schema);
        Object schemaKeywordValue = schema.getUnprocessedProperties().get("$schema");
        String idKeyword = deduceSpecVersion(schemaKeywordValue).idKeyword();
        writer.ifPresent(idKeyword, schema.getId());
        schema.getUnprocessedProperties().forEach((key, val) -> writer.key(key).value(val));
        schema.describePropertiesTo(writer);
        if (!jsonObjectIsOpenForCurrentSchemaInstance) {
            writer.endObject();
        }
    }

    @Override 
    void visit(Schema schema) {
        boolean orig = jsonObjectIsOpenForCurrentSchemaInstance;
        jsonObjectIsOpenForCurrentSchemaInstance = false;
        super.visit(schema);
        jsonObjectIsOpenForCurrentSchemaInstance = orig;
    }

    private SpecificationVersion deduceSpecVersion(Object schemaKeywordValue) {
        if (deducedSpecVersion != null) {
            return deducedSpecVersion;
        }
        if (schemaKeywordValue instanceof String) {
            return deducedSpecVersion = SpecificationVersion.lookupByMetaSchemaUrl((String) schemaKeywordValue)
                    .orElse(SpecificationVersion.DRAFT_4);
        } else {
            return deducedSpecVersion = SpecificationVersion.DRAFT_4;
        }
    }

    private void printInJsonObject(Runnable task) {
        if (skipNextObject) {
            skipNextObject = false;
            jsonObjectIsOpenForCurrentSchemaInstance = true;
            task.run();
            jsonObjectIsOpenForCurrentSchemaInstance = false;
        } else {
            writer.object();
            jsonObjectIsOpenForCurrentSchemaInstance = true;
            task.run();
            writer.endObject();
            jsonObjectIsOpenForCurrentSchemaInstance = false;
        }
    }

    @Override void visitEmptySchema(EmptySchema emptySchema) {
        if (emptySchema instanceof TrueSchema) {
            writer.value(true);
        } else {
            printInJsonObject(() -> super.visitEmptySchema(emptySchema));
        }
    }

}
