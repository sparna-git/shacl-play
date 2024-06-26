package fr.sparna.jsonschema.model;

import static java.util.Objects.requireNonNull;
//import static org.everit.json.schema.FormatValidator.NONE;

import java.util.Objects;

//import fr.sparna.jsonschema.regexp.JavaUtilRegexpFactory;
//import fr.sparna.jsonschema.regexp.Regexp;

/**
 * {@code String} schema validator.
 */
public class StringSchema extends Schema {

    /**
     * Builder class for {@link StringSchema}.
     */
    public static class Builder extends Schema.Builder<StringSchema> {

        private Integer minLength;

        private Integer maxLength;

        private String pattern; //Regexp
        
        private String Format;

        private boolean requiresString = true;

        @Override
        public StringSchema build() {
            return new StringSchema(this);
        }

        public Builder maxLength(final Integer maxLength) {
            this.maxLength = maxLength;
            return this;
        }

        public Builder minLength(final Integer minLength) {
            this.minLength = minLength;
            return this;
        }

        /*
        public Builder pattern(final String pattern) {
            return pattern; //(new JavaUtilRegexpFactory().createHandler(pattern));
        }
        */

        public Builder pattern(String pattern) {
            this.pattern = pattern;
            return this;
        }

        public Builder requiresString(final boolean requiresString) {
            this.requiresString = requiresString;
            return this;
        }

    }

    public static Builder builder() {
        return new Builder();
    }

    private final Integer minLength;

    private final Integer maxLength;

    private final String pattern; //Regexp

    private final boolean requiresString;

    public StringSchema() {
        this(builder());
    }

    /**
     * Constructor.
     *
     * @param builder
     *         the builder object containing validation criteria
     */
    public StringSchema(final Builder builder) {
        super(builder);
        this.minLength = builder.minLength;
        this.maxLength = builder.maxLength;
        this.requiresString = builder.requiresString;
        this.pattern = builder.pattern;
        //this.formatValidator = builder.formatValidator;
    }

    public Integer getMaxLength() {
        return maxLength;
    }

    public Integer getMinLength() {
        return minLength;
    }

    String getRegexpPattern() { //Regexp 
        return pattern;
    }

    public String getPattern() { //java.util.regex.Pattern
        if (pattern == null) {
            return null;
        } else {
            return pattern.toString(); //java.util.regex.Pattern.compile(pattern.toString());
        }
    }

    @Override 
    void accept(Visitor visitor) {
        visitor.visitStringSchema(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof StringSchema) {
            StringSchema that = (StringSchema) o;
            return that.canEqual(this) &&
                    requiresString == that.requiresString &&
                    Objects.equals(minLength, that.minLength) &&
                    Objects.equals(maxLength, that.maxLength) &&
                    Objects.equals(pattern, that.pattern) &&
                    //sameFormatAs(that) &&
                    super.equals(that);
        } else {
            return false;
        }
    }

    /*
    private boolean sameFormatAs(StringSchema that) {
        if ((formatValidator == null) !=  (that.formatValidator == null)) {
            return false;
        }
        if (formatValidator == null) {
            return true;
        }
        if (!formatValidator.getClass().equals(that.formatValidator.getClass())) {
            return false;
        }
        return Objects.equals(formatValidator.formatName(), that.formatValidator.formatName());
    }

    */

    @Override
    public int hashCode() {
        //return Objects.hash(super.hashCode(), minLength, maxLength, pattern, requiresString, formatValidator);
    	return Objects.hash(super.hashCode(), minLength, maxLength, pattern, requiresString);
    }

    @Override
    protected boolean canEqual(Object other) {
        return other instanceof StringSchema;
    }

    public boolean requireString() {
        return requiresString;
    }
}