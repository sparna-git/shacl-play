package fr.sparna.jsonschema.model;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static java.lang.String.format;
import static java.util.Arrays.asList;
import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableMap;

/**
 * @author erosb
 */
public enum SpecificationVersion {

    DRAFT_4 {
        @Override List<String> arrayKeywords() {
            return V4_ARRAY_KEYWORDS;
        }

        @Override List<String> objectKeywords() {
            return V4_OBJECT_KEYWORDS;
        }

        @Override public String idKeyword() {
            return "id";
        }

        @Override List<String> metaSchemaUrls() {
            return Arrays.asList(
                "http://json-schema.org/draft-04/schema",
                "https://json-schema.org/draft-04/schema",
                "http://json-schema.org/schema",
                "https://json-schema.org/schema"
            );
        }

    }, DRAFT_6 {
        @Override List<String> arrayKeywords() {
            return V6_ARRAY_KEYWORDS;
        }

        @Override List<String> objectKeywords() {
            return V6_OBJECT_KEYWORDS;
        }

        @Override public String idKeyword() {
            return "$id";
        }

        @Override List<String> metaSchemaUrls() {
            return Arrays.asList(
                "http://json-schema.org/draft-06/schema",
                "https://json-schema.org/draft-06/schema"
            );
        }

    }, DRAFT_7 {
        @Override List<String> arrayKeywords() {
            return V6_ARRAY_KEYWORDS;
        }

        @Override List<String> objectKeywords() {
            return V6_OBJECT_KEYWORDS;
        }

        @Override public String idKeyword() {
            return DRAFT_6.idKeyword();
        }

        @Override List<String> metaSchemaUrls() {
            return Arrays.asList(
                "http://json-schema.org/draft-07/schema",
                "https://json-schema.org/draft-07/schema"
            );
        }
    };

    static SpecificationVersion getByMetaSchemaUrl(String metaSchemaUrl) {
        return lookupByMetaSchemaUrl(metaSchemaUrl)
                .orElseThrow(() -> new IllegalArgumentException(
                        format("could not determine schema version: no meta-schema is known with URL [%s]", metaSchemaUrl)
                ));
    }

    public static Optional<SpecificationVersion> lookupByMetaSchemaUrl(String metaSchemaUrl) {
        return Arrays.stream(values())
                .filter(v -> v.metaSchemaUrls().stream().anyMatch(metaSchemaUrl::startsWith))
                .findFirst();
    }

    private static final List<String> V6_OBJECT_KEYWORDS = keywords("properties", "required",
            "minProperties",
            "maxProperties",
            "dependencies",
            "patternProperties",
            "additionalProperties",
            "propertyNames");

    private static final List<String> V6_ARRAY_KEYWORDS = keywords("items", "additionalItems", "minItems",
            "maxItems", "uniqueItems", "contains");

    private static final List<String> V4_OBJECT_KEYWORDS = keywords("properties", "required",
            "minProperties",
            "maxProperties",
            "dependencies",
            "patternProperties",
            "additionalProperties");

    private static final List<String> V4_ARRAY_KEYWORDS = keywords("items", "additionalItems", "minItems",
            "maxItems", "uniqueItems");

    private static List<String> keywords(String... keywords) {
        return unmodifiableList(asList(keywords));
    }

    abstract List<String> arrayKeywords();

    abstract List<String> objectKeywords();

    public abstract String idKeyword();

    abstract List<String> metaSchemaUrls();

    public boolean isAtLeast(SpecificationVersion lowerInclusiveBound) {
        return this.ordinal() >= lowerInclusiveBound.ordinal();
    }
}
