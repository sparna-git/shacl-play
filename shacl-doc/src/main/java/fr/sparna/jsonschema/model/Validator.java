
package fr.sparna.jsonschema.model;


public interface Validator {

    class ValidatorBuilder {

        private boolean failEarly = false;

        private ReadWriteContext readWriteContext;

        //private PrimitiveValidationStrategy primitiveValidationStrategy = PrimitiveValidationStrategy.STRICT;

        public ValidatorBuilder failEarly() {
            this.failEarly = true;
            return this;
        }

        public ValidatorBuilder readWriteContext(ReadWriteContext readWriteContext) {
            this.readWriteContext = readWriteContext;
            return this;
        }

    }

    public static ValidatorBuilder builder() {
        return new ValidatorBuilder();
    }

    //void performValidation(Schema schema, Object input);


}

