package fr.sparna.rdf.shacl.jsonschema.model;

import static java.util.stream.Collectors.toList;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Enum schema validator.
 */
public class EnumSchema extends Schema {

	static Object toJavaValue(Object orig) {
        if (orig instanceof JSONArray) {
            return OrgJsonUtil.toList((JSONArray) orig);
        } else if (orig instanceof JSONObject) {
            return OrgJsonUtil.toMap((JSONObject) orig);
        } else if (orig == JSONObject.NULL) {
            return null;
        } else {
            return orig;
        }
    }

    static List<Object> toJavaValues(List<Object> orgJsons) {
        return orgJsons.stream().map(EnumSchema::toJavaValue).collect(toList());
    }

    /**
     * Builder class for {@link EnumSchema}.
     */
    public static class Builder extends Schema.Builder<EnumSchema> {

        private List<Object> possibleValues = new ArrayList<>();

        private boolean requiresString = true;

        @Override
        public EnumSchema build() {
            return new EnumSchema(this);
        }

        public Builder possibleValue(Object possibleValue) {
            possibleValues.add(possibleValue);
            return this;
        }

        public Builder possibleValues(List<Object> possibleValues) {
            this.possibleValues = possibleValues;
            return this;
        }

        public Builder possibleValues(Set<Object> possibleValues) {
            this.possibleValues = possibleValues.stream().collect(toList());
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

    private final List<Object> possibleValues;

    private final boolean requiresString;

    public EnumSchema(Builder builder) {
        super(builder);
        this.possibleValues = Collections.unmodifiableList(toJavaValues(builder.possibleValues));
        this.requiresString = builder.requiresString;   
    }

    public Set<Object> getPossibleValues() {
        return possibleValues.stream().collect(Collectors.toSet());
    }

    public List<Object> getPossibleValuesAsList() {
        return possibleValues;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o instanceof EnumSchema) {
            EnumSchema that = (EnumSchema) o;
            return that.canEqual(this) &&
                    Objects.equals(possibleValues, that.possibleValues) &&
                    super.equals(that);
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return Objects.hash(super.hashCode(), possibleValues);
    }

    @Override 
    public void accept(Visitor visitor) {
        visitor.visitEnumSchema(this);
    }

    @Override
    protected boolean canEqual(Object other) {
        return other instanceof EnumSchema;
    }

    public boolean requireString() {
        return requiresString;
    }

}