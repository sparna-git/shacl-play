package fr.sparna.rdf.shacl.jsonschema.model;


/**
 * A schema not specifying any restrictions, ie. accepting any values.
 */
public class EmptySchema extends Schema {

    public static final EmptySchema INSTANCE = new EmptySchema(builder());

    /**
     * Builder class for {@link EmptySchema}.
     */
    public static class Builder extends Schema.Builder<EmptySchema> {

        @Override
        public EmptySchema build() {
            return new EmptySchema(this);
        }

    }

    public static Builder builder() {
        return new Builder();
    }

    public EmptySchema(Builder builder) {
        super(builder);
    }

    @Override 
    public void accept(Visitor visitor) {
        visitor.visitEmptySchema(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o instanceof EmptySchema) {
            EmptySchema that = (EmptySchema) o;
            return that.canEqual(this) && super.equals(that);
        } else {
            return false;
        }
    }

    @Override
    protected boolean canEqual(Object other) {
        return other instanceof EmptySchema;
    }

    @Override
    public int hashCode() {
        return super.hashCode();
    }
}
