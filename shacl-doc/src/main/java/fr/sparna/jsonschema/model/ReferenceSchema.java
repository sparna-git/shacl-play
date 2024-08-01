package fr.sparna.jsonschema.model;

import static java.util.Objects.requireNonNull;

import java.util.HashMap;
import java.util.Map;
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
        
        private String title_custom;
        
        private String description_custom;

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
        
        public Builder title_custom(String title) {
        	this.title_custom = title;
        	return this;
        }
        
        public Builder description_custom(String description) {
        	this.description_custom = description;
        	return this;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private Schema referredSchema;

    private final String refValue;
    
    private final String title_custom;
    
    private final String description_custom;


    public ReferenceSchema(final Builder builder) {
        super(builder);
        this.refValue = requireNonNull(builder.refValue, "refValue cannot be null");
        this.title_custom = builder.title_custom;
        this.description_custom = builder.description_custom;
    }


    public Schema getReferredSchema() {
        return referredSchema;
    }

    public String getReferenceValue() {
        return refValue;
    }
    
    

    public String getTitle_custom() {
		return title_custom;
	}


	public String getDescription_custom() {
		return description_custom;
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
        return Objects.hash(super.hashCode(), refValue, title_custom, description_custom, title_custom, description_custom);
    }

    @Override
    protected boolean canEqual(Object other) {
        return other instanceof ReferenceSchema;
    }

    @Override void accept(Visitor visitor) {
        visitor.visitReferenceSchema(this);
    }

}