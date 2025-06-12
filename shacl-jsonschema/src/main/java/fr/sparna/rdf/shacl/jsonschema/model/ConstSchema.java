package fr.sparna.rdf.shacl.jsonschema.model;

import fr.sparna.rdf.shacl.jsonschema.model.BooleanSchema.Builder;

public class ConstSchema extends Schema {

    public static class ConstSchemaBuilder extends Schema.Builder<ConstSchema> {

        private Object permittedValue;
        
        private String title_custom;
        
        private String description_custom;

        public ConstSchemaBuilder permittedValue(Object permittedValue) {
            this.permittedValue = permittedValue;
            return this;
        }

        public ConstSchemaBuilder title_custom(String title) {
        	this.title_custom = title;
            return this;
        }
        
        public ConstSchemaBuilder description_custom(String description) {
        	this.description_custom = description;
        	return this;
        }
        
        @Override 
        public ConstSchema build() {
            return new ConstSchema(this);
        }
    }

    public static ConstSchemaBuilder builder() {
        return new ConstSchemaBuilder();
    }

    private final Object permittedValue;

    private final String title_custom;
    
    private final String description_custom;
    
    protected ConstSchema(ConstSchemaBuilder builder) {
        super(builder);
        this.permittedValue = EnumSchema.toJavaValue(builder.permittedValue);
        this.title_custom = builder.title_custom;
        this.description_custom = builder.description_custom;
    }

    @Override 
    void accept(Visitor visitor) {
    	visitor.visitConstSchema(this);
    }

    public Object getPermittedValue() {
        return permittedValue;
    }

    public String getTitle_custom() {
		return title_custom;
	}

	public String getDescription_custom() {
		return description_custom;
	}
    
}