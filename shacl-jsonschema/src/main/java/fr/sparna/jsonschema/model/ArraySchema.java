package fr.sparna.jsonschema.model;

import java.util.Objects;

/**
 * Array schema validator.
 */
public class ArraySchema
        extends Schema {

    /**
     * Builder class for {@link ArraySchema}.
     */
    public static class Builder
            extends Schema.Builder<ArraySchema> {

        private boolean requiresArray = true;

        private Integer minItems;

        private Integer maxItems;

        private boolean uniqueItems = false;

        private Schema allItemSchema;

        private boolean additionalItems = true;

        private Schema schemaOfAdditionalItems;

        private Schema containedItemSchema;
        
        private String title_custom;
        private String description_custom;

        public Builder additionalItems(final boolean additionalItems) {
            this.additionalItems = additionalItems;
            return this;
        }

        public Builder allItemSchema(final Schema allItemSchema) {
            this.allItemSchema = allItemSchema;
            return this;
        }

        @Override
        public ArraySchema build() {
            return new ArraySchema(this);
        }

        public Builder maxItems(final Integer maxItems) {
            this.maxItems = maxItems;
            return this;
        }
        
        public Builder title(final String title) {
        	this.title_custom = title;
        	return this;
        }
        
        public Builder description(final String description) {
        	this.description_custom = description;
        	return this;
        }

        public Builder minItems(final Integer minItems) {
            this.minItems = minItems;
            return this;
        }

        public Builder requiresArray(final boolean requiresArray) {
            this.requiresArray = requiresArray;
            return this;
        }

        public Builder schemaOfAdditionalItems(final Schema schemaOfAdditionalItems) {
            this.schemaOfAdditionalItems = schemaOfAdditionalItems;
            return this;
        }

        public Builder uniqueItems(final boolean uniqueItems) {
            this.uniqueItems = uniqueItems;
            return this;
        }

        public Builder containsItemSchema(Schema contained) {
            this.containedItemSchema = contained;
            return this;
        }
    }

    public static Builder builder() {
        return new Builder();
    }

    private final Integer minItems;

    private final Integer maxItems;

    private final boolean uniqueItems;

    private final Schema allItemSchema;

    private final boolean additionalItems;

    private final boolean requiresArray;

    private final Schema schemaOfAdditionalItems;

    private final Schema containedItemSchema;
    
    /* custom */
    private final String title_custom;
    /* custom */
    private final String description_custom;

    /**
     * Constructor.
     *
     * @param builder contains validation criteria.
     */
    public ArraySchema(final Builder builder) {
        super(builder);
        this.title_custom = builder.title_custom;
        this.description_custom = builder.description_custom;
        this.minItems = builder.minItems;
        this.maxItems = builder.maxItems;
        this.uniqueItems = builder.uniqueItems;
        this.allItemSchema = builder.allItemSchema;
        if (!builder.additionalItems && allItemSchema != null) {
            additionalItems = true;
        } else {
            additionalItems = builder.schemaOfAdditionalItems != null || builder.additionalItems;
        }
        this.schemaOfAdditionalItems = builder.schemaOfAdditionalItems;
        /*
        if (!(allItemSchema == null || itemSchemas == null)) {
            throw new SchemaException("cannot perform both tuple and list validation");
        }
        */
        this.requiresArray = builder.requiresArray;
        this.containedItemSchema = builder.containedItemSchema;
        
       
    }

    public Schema getAllItemSchema() {
        return allItemSchema;
    }

    public Integer getMaxItems() {
        return maxItems;
    }

    public Integer getMinItems() {
        return minItems;
    }
    
    public String getTitle_custom() {
		return title_custom;
	}

	public String getDescription_custom() {
		return description_custom;
	}

	public Schema getSchemaOfAdditionalItems() {
        return schemaOfAdditionalItems;
    }

    public Schema getContainedItemSchema() {
        return containedItemSchema;
    }

    public boolean needsUniqueItems() {
        return uniqueItems;
    }

    public boolean permitsAdditionalItems() {
        return additionalItems;
    }

    public boolean requiresArray() {
        return requiresArray;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof ArraySchema) {
            ArraySchema that = (ArraySchema) o;
            return that.canEqual(this) &&
                    uniqueItems == that.uniqueItems &&
                    additionalItems == that.additionalItems &&
                    requiresArray == that.requiresArray &&
                    Objects.equals(minItems, that.minItems) &&
                    Objects.equals(maxItems, that.maxItems) &&
                    Objects.equals(title_custom, that.title_custom) &&
                    Objects.equals(description_custom, that.description_custom) &&
                    Objects.equals(allItemSchema, that.allItemSchema) &&
                    Objects.equals(schemaOfAdditionalItems, that.schemaOfAdditionalItems) &&
                    Objects.equals(containedItemSchema, that.containedItemSchema) &&
                    super.equals(o);
        } else {
            return false;
        }
    }

    @Override
    void accept(Visitor visitor) {
        visitor.visitArraySchema(this);
    }

    @Override
    protected boolean canEqual(final Object other) {
        return other instanceof ArraySchema;
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), minItems, maxItems, uniqueItems, allItemSchema,
                additionalItems, requiresArray, schemaOfAdditionalItems, containedItemSchema, title_custom, description_custom);
    }
}