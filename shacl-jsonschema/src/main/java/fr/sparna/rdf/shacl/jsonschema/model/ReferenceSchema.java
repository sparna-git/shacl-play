package fr.sparna.rdf.shacl.jsonschema.model;

import static java.util.Objects.requireNonNull;

import java.util.Objects;

/**
 * This class is used by {@link org.everit.json.schema.loader.SchemaLoader} to resolve JSON pointers
 * during the construction of the schema. This class has been made mutable to permit the loading of
 * recursive schemas.
 */
public class ReferenceSchema extends Schema {

    /**
     * Builder class for {@link ReferenceSchema}.
     */
    public static class Builder extends Schema.Builder<ReferenceSchema> {

        private ReferenceSchema retval;

        /**
         * The value of {@code "$ref"}
         */
        private String refValue = "";

        /**
         * This method caches its result, so multiple invocations will return referentially the same
         * {@link ReferenceSchema} instance.
         */
        @Override
        public ReferenceSchema build() {
            if (retval == null) {
                retval = new ReferenceSchema(this);
            }
            return retval;
        }

        public Builder refValue(String refValue) {
            this.refValue = refValue;
            return this;
        }

        public Builder copy() {
            Builder copy = new Builder();
            if (this.retval != null) {
                copy.build().setReferredSchema(this.retval.getReferredSchema());
            }
            return copy;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private Schema referredSchema;

    private final String refValue;;


    public ReferenceSchema(final Builder builder) {
        super(builder);
        this.refValue = requireNonNull(builder.refValue, "refValue cannot be null");
    }


    public Schema getReferredSchema() {
        return referredSchema;
    }

    public String getReferenceValue() {
        return refValue;
    }


	/**
     * Called by {@link org.everit.json.schema.loader.SchemaLoader#load()} to set the referred root
     * schema after completing the loading process of the entire schema document.
     *
     * @param referredSchema
     *         the referred schema
     */
    public void setReferredSchema(final Schema referredSchema) {
        if (this.referredSchema != null) {
            throw new IllegalStateException("referredSchema can be injected only once");
        }
        this.referredSchema = referredSchema;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof ReferenceSchema) {
            ReferenceSchema that = (ReferenceSchema) o;
            return that.canEqual(this) &&
                    Objects.equals(refValue, that.refValue) &&
                    super.equals(that);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), refValue);
    }

    @Override
    protected boolean canEqual(Object other) {
        return other instanceof ReferenceSchema;
    }

    @Override void accept(Visitor visitor) {
        visitor.visitReferenceSchema(this);
    }

}