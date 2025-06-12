package fr.sparna.rdf.shacl.jsonschema.model;

import java.io.StringWriter;

import fr.sparna.rdf.shacl.jsonschema.model.StringSchema.Builder;


/**
 * Boolean schema validator.
 */
public class BooleanSchema extends Schema {

	/**
     * Builder class for {@link BooleanSchema}.
     */
    public static class Builder extends Schema.Builder<BooleanSchema> {

    	private String title_custom;
        
        private String description_custom;
    	
        @Override
        public BooleanSchema build() {
            return new BooleanSchema(this);
        }
        
        public Builder title_custom(final String title) {
        	this.title_custom = title;
            return this;
        }
        
        public Builder description_custom(final String description) {
        	this.description_custom = description;
        	return this;
        }

    }

    public static final BooleanSchema INSTANCE = new BooleanSchema(builder());
    
    private final String title_custom;
    
    private final String description_custom;

    public static Builder builder() {
        return new Builder();
    }

    public BooleanSchema(final Builder builder) {
        super(builder);
        this.title_custom = builder.title_custom;
        this.description_custom = builder.description_custom;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof BooleanSchema) {
            BooleanSchema that = (BooleanSchema) o;
            return that.canEqual(this) && super.equals(that);
        } else {
            return false;
        }
    }

    @Override 
    void accept(Visitor visitor) {
        visitor.visitBooleanSchema(this);
    }

    @Override
    protected boolean canEqual(final Object other) {
        return other instanceof BooleanSchema;
    }

	public String getTitle_custom() {
		return title_custom;
	}

	public String getDescription_custom() {
		return description_custom;
	}
}