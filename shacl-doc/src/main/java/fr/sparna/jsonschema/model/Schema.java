package fr.sparna.jsonschema.model;

import static java.util.Collections.unmodifiableMap;

import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.json.JSONWriter;

import fr.sparna.jsonschema.ValidationException;
import fr.sparna.jsonschema.writer.JSONPrinter;

/**
 * Superclass of all other schema validator classes of this package.
 */
public abstract class Schema {

    public static final String JSON_SCHEMA_VERSION = "https://json-schema.org/draft/2020-12/schema";

    /**
     * Abstract builder class for the builder classes of {@code Schema} subclasses. This builder is
     * used to load the generic properties of all types of schemas like {@code title} or
     * {@code description}.
     *
     * @param <S>
     *         the type of the schema being built by the builder subclass.
     */
    public abstract static class Builder<S extends Schema> {

        private String title;
        
        private String schemaVersion;
        
        private String comment;

        private String description;

        private String id;
        
        private String version;
        
        private String format;

        private SchemaLocation schemaLocation;

        private Object defaultValue;

        private Boolean nullable = null;

        private Boolean readOnly = null;

        private Boolean writeOnly = null;

        public Map<String, Object> unprocessedProperties = new HashMap<>(0);

        public Map<String, Schema> embeddedSchemas = new HashMap<>(0);
        
        
        public Builder<S> title(String title) {
            this.title = title;
            return this;
        }
        
        public Builder<S> schemaVersion(String schemaVersion) {
            this.schemaVersion = schemaVersion;
            return this;
        }
        
        public Builder<S> comment(String comment) {
            this.comment = comment;
            return this;
        }

        public Builder<S> description(String description) {
            this.description = description;
            return this;
        }

        public Builder<S> id(String id) {
            this.id = id;
            return this;
        }
        
        public Builder<S> version(String version) {
            this.version = version;
            return this;
        }
        
        public Builder<S> format(String format) {
            this.format = format;
            return this;
        }

        /**
         * @deprecated Use {@link #schemaLocation(SchemaLocation)} instead.
         */
        @Deprecated
        public Builder<S> schemaLocation(String schemaLocation) {
            return schemaLocation(SchemaLocation.parseURI(schemaLocation));
        }

        public Builder<S> schemaLocation(SchemaLocation location) {
            this.schemaLocation = location;
            return this;
        }

        public Builder<S> defaultValue(Object defaultValue) {
            this.defaultValue = defaultValue;
            return this;
        }

        public Builder<S> nullable(Boolean nullable) {
            this.nullable = nullable;
            return this;
        }

        public Builder<S> readOnly(Boolean readOnly) {
            this.readOnly = readOnly;
            return this;
        }

        public Builder<S> writeOnly(Boolean writeOnly) {
            this.writeOnly = writeOnly;
            return this;
        }

        public Builder<S> unprocessedProperties(Map<String, Object> unprocessedProperties) {
            this.unprocessedProperties = unprocessedProperties;
            return this;
        }

        public Builder<S> embeddedSchemas(Map<String, Schema> embeddedSchemas) {
            this.embeddedSchemas = embeddedSchemas;
            return this;
        }
        
       
        public Builder<S> embeddedSchema(String name, Schema embeddedSchema) {
            this.embeddedSchemas.put(name, embeddedSchema);
            return this;
        }
        
        public abstract S build();

    }

    private final String schemaVersion;

    private final String title;
    
    private final String comment;

    private final String description;

    private final String id;
    
    private final String version;
    
    private final String format;

    @Deprecated
    protected final String schemaLocation;

    private final SchemaLocation location;

    private final Object defaultValue;

    private final Boolean nullable;

    private final Boolean readOnly;

    private final Boolean writeOnly;

    private final Map<String, Object> unprocessedProperties;

    private final Map<String, Schema> embeddedSchemas;
    

    /**
     * Constructor.
     *
     * @param builder
     *         the builder containing the optional title, description and id attributes of the schema
     */
    protected Schema(Builder<?> builder) {
        // always the same value
        this.schemaVersion = builder.schemaVersion;// = JSON_SCHEMA_VERSION;

        this.title = builder.title;
        this.comment = builder.comment;
        this.description = builder.description;
        this.id = builder.id;
        this.version = builder.version;
        this.format = builder.format;
        this.schemaLocation = builder.schemaLocation == null ? null : builder.schemaLocation.toString();
        this.location = builder.schemaLocation;
        this.defaultValue = builder.defaultValue;
        this.nullable = builder.nullable;
        this.readOnly = builder.readOnly;
        this.writeOnly = builder.writeOnly;
        this.unprocessedProperties = new HashMap<>(builder.unprocessedProperties);
        this.embeddedSchemas = new HashMap<>(builder.embeddedSchemas);        
    }

    /**
     * Shared method for {@link #definesProperty(String)} implementations.
     *
     * @param pointer
     * @return
     */
    String[] headAndTailOfJsonPointerFragment(String pointer) {
        String field = pointer.replaceFirst("^#", "").replaceFirst("^/", "");
        int firstSlashIdx = field.indexOf('/');
        String nextToken, remaining;
        if (firstSlashIdx == -1) {
            nextToken = field;
            remaining = null;
        } else {
            nextToken = field.substring(0, firstSlashIdx);
            remaining = field.substring(firstSlashIdx + 1);
        }
        return new String[]{nextToken, remaining, field};
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof Schema) {
            Schema schema = (Schema) o;
            return schema.canEqual(this) &&
                    Objects.equals(title, schema.title) &&
                    Objects.equals(comment, schema.comment) &&                  
                    Objects.equals(defaultValue, schema.defaultValue) &&
                    Objects.equals(description, schema.description) &&
                    Objects.equals(id, schema.id) &&
                    Objects.equals(version, schema.version) &&
                    Objects.equals(nullable, schema.nullable) &&
                    Objects.equals(readOnly, schema.readOnly) &&
                    Objects.equals(writeOnly, schema.writeOnly) &&
                    Objects.equals(unprocessedProperties, schema.unprocessedProperties);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, comment,description, id, version ,defaultValue, nullable, readOnly, writeOnly, unprocessedProperties);
    }

    public String getTitle() {
        return title;
    }
    
    
    public String getComment() {
		return comment;
	}

	public String getDescription() {
        return description;
    }

    public String getId() {
        return id;
    }
    
    public String getVersion() {
		return version;
	}
    
    public String getFormat() {
		return format;
	}

	public String getSchemaLocation() {
        return schemaLocation;
    }

    public SchemaLocation getLocation() {
        return location;
    }

    public Object getDefaultValue() {
        return this.defaultValue;
    }

    public boolean hasDefaultValue() {
        return this.defaultValue != null;
    }

    public Boolean isNullable() {
        return nullable;
    }

    public Boolean isReadOnly() {
        return readOnly;
    }

    public Boolean isWriteOnly() {
        return writeOnly;
    }

    public Map<String, Schema> getEmbeddedSchemas() {
        return embeddedSchemas;
    }    

	public String getSchema() {
        return schemaVersion;
    }

    /**
     * Returns the properties of the original schema JSON which aren't keywords of json schema
     * (therefore they weren't recognized during schema loading).
     */
    public Map<String, Object> getUnprocessedProperties() {
        return unmodifiableMap(unprocessedProperties);
    }

    /**
     * Describes the instance as a JSONObject to {@code writer}.
     * <p>
     * First it adds the {@code "title} , {@code "description"} and {@code "id"} properties then calls
     * {@link #describePropertiesTo(JSONPrinter)}, which will add the subclass-specific properties.
     * <p>
     * It is used by {@link #toString()} to serialize the schema instance into its JSON representation.
     *
     * @param writer
     *         it will receive the schema description
     */
    public void describeTo(JSONPrinter writer) {
        accept(new ToStringVisitor(writer));
    }

    /**
     * Subclasses are supposed to override this method to describe the subclass-specific attributes.
     * This method is called by {@link #describeTo(JSONPrinter)} after adding the generic properties if
     * they are present ({@code id}, {@code title} and {@code description}). As a side effect,
     * overriding subclasses don't have to open and close the object with {@link JSONWriter#object()}
     * and {@link JSONWriter#endObject()}.
     *
     * @param writer
     *         it will receive the schema description
     */
    void describePropertiesTo(JSONPrinter writer) {

    }

    abstract void accept(Visitor visitor);

    @Override
    public String toString() {
        StringWriter w = new StringWriter();
        JSONPrinter writer = new JSONPrinter(w);
        new ToStringVisitor(writer).visit(this);
        return w.getBuffer().toString();
    }

    @Deprecated
    protected ValidationException failure(String message, String keyword) {
        return new ValidationException(this, message, keyword, schemaLocation.toString());
    }

    @Deprecated
    protected ValidationException failure(Class<?> expectedType, Object actualValue) {
        return new ValidationException(this, expectedType, actualValue, "type", schemaLocation.toString());
    }

    /**
     * Since we add state in subclasses, but want those subclasses to be non final, this allows us to
     * have equals methods that satisfy the equals contract.
     * <p>
     * http://www.artima.com/lejava/articles/equality.html
     *
     * @param other
     *         the subject of comparison
     * @return {@code true } if {@code this} can be equal to {@code other}
     */
    protected boolean canEqual(Object other) {
        return (other instanceof Schema);
    }
}
