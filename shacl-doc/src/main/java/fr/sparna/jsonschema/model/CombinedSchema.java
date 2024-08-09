package fr.sparna.jsonschema.model;

import static java.util.Objects.requireNonNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * Validator for {@code allOf}, {@code oneOf}, {@code anyOf} schemas.
 */
public class CombinedSchema extends Schema {

    public static enum ValidationCriterion {
        ALL_CRITERION("allOf"),
        ANY_CRITERION("anyOf"),
        ONE_CRITERION("oneOf");

        private String identifier;

        private ValidationCriterion(String identifier) {
            this.identifier = identifier;
        }

        public String getIdentifier() {
            return identifier;
        }

    }

    /**
     * Builder class for {@link CombinedSchema}.
     */
    public static class Builder extends Schema.Builder<CombinedSchema> {

        private ValidationCriterion criterion;

        private Collection<Schema> subschemas = new ArrayList<>();

        private boolean synthetic;      
        

        @Override
        public CombinedSchema build() {
            return new CombinedSchema(this);
        }

        public Builder criterion(ValidationCriterion criterion) {
            this.criterion = criterion;
            return this;
        }

        public Builder subschema(Schema subschema) {
            this.subschemas.add(subschema);
            return this;
        }

        public Builder subschemas(Collection<Schema> subschemas) {
            this.subschemas = subschemas;
            return this;
        }

        public Builder isSynthetic(boolean synthetic) {
            this.synthetic = synthetic;
            return this;
        }
        
    }


    public static Builder allOf(Collection<Schema> schemas) {
        return builder(schemas).criterion(ValidationCriterion.ALL_CRITERION);
    }

    public static Builder anyOf(Collection<Schema> schemas) {
        return builder(schemas).criterion(ValidationCriterion.ANY_CRITERION);
    }

    public static Builder builder() {
        return new Builder();
    }
    
    public static Builder builder(Collection<Schema> subschemas) {
        return new Builder().subschemas(subschemas);
    }

    public static Builder oneOf(Collection<Schema> schemas) {
        return builder(schemas).criterion(ValidationCriterion.ONE_CRITERION);
    }

    private final boolean synthetic;

    private final Collection<Schema> subschemas;

    private final ValidationCriterion criterion;

    /**
     * Constructor.
     *
     * @param builder
     *         the builder containing the validation criterion and the subschemas to be checked
     */
    public CombinedSchema(Builder builder) {
        super(builder);
        this.synthetic = builder.synthetic;
        this.criterion = requireNonNull(builder.criterion, "criterion cannot be null");
        this.subschemas = sortByCombinedFirst(requireNonNull(builder.subschemas, "subschemas cannot be null"));
    }

    private static int compareBySchemaType(Schema lschema, Schema rschema) {
        boolean leftSchemaIsCombined = lschema instanceof CombinedSchema;
        boolean rightIsCombined = rschema instanceof CombinedSchema;
        int defaultRetval = lschema.hashCode() - rschema.hashCode();
        return leftSchemaIsCombined ?
                (rightIsCombined ? defaultRetval : -1) :
                (rightIsCombined ? 1 : defaultRetval);
    }

    // ensure subschemas of type CombinedSchema are always visited first
    private static Collection<Schema> sortByCombinedFirst(Collection<Schema> schemas) {
        return schemas.stream()
                .sorted(CombinedSchema::compareBySchemaType)
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public ValidationCriterion getCriterion() {
        return criterion;
    }

    public Collection<Schema> getSubschemas() {
        return subschemas;
    }

    boolean isSynthetic() {
        return synthetic;
    }

    @Override void accept(Visitor visitor) {
        visitor.visitCombinedSchema(this);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof CombinedSchema) {
            CombinedSchema that = (CombinedSchema) o;
            return that.canEqual(this) &&
                    Objects.equals(subschemas, that.subschemas) &&
                    Objects.equals(criterion, that.criterion) &&
                    synthetic == that.synthetic &&
                    super.equals(that);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), subschemas, criterion, synthetic, title_custom, description_custom);
    }

    @Override
    protected boolean canEqual(Object other) {
        return other instanceof CombinedSchema;
    }
    
    
}