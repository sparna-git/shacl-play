package fr.sparna.rdf.shacl.jsonschema;

import static org.junit.Assert.assertEquals;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileUtils;
import org.apache.jena.vocabulary.RDF;
import org.json.JSONObject;
import org.junit.Test;

import fr.sparna.rdf.shacl.jsonschema.model.Schema;

public class JsonSchemaGeneratorTest {

    private static final String TEST_RESOURCES_DIR = "src/test/resources/jsonschema-generator-tests/";

    @Test
    public void testSimpleNodeShape() throws Exception {
        runTest("1-simple-node-shape/shapes.ttl", "1-simple-node-shape/expected.json");
    }

    @Test
    public void testNodeShapeWithProperties() throws Exception {
        runTest("2-node-shape-with-properties/shapes.ttl", "2-node-shape-with-properties/expected.json");
    }

    @Test
    public void testNodeShapeWithEnum() throws Exception {
        runTest("3-node-shape-with-enum/shapes.ttl", "3-node-shape-with-enum/expected.json");
    }

    @Test
    public void testNodeShapeWithPattern() throws Exception {
        runTest("4-node-shape-with-pattern/shapes.ttl", "4-node-shape-with-pattern/expected.json");
    }

    @Test
    public void testDeactivatedPropertyShape() throws Exception {
        runTest("5-deactivated-property-shape/shapes.ttl", "5-deactivated-property-shape/expected.json");
    }

    @Test
    public void testClosedNodeShape() throws Exception {
        runTest("6-closed-node-shape/shapes.ttl", "6-closed-node-shape/expected.json");
    }

    @Test
    public void testHasValueConstSchema() throws Exception {
        runTest("7-has-value-const-schema/shapes.ttl", "7-has-value-const-schema/expected.json");
    }

    @Test
    public void testBooleanSchema() throws Exception {
        runTest("8-boolean-schema/shapes.ttl", "8-boolean-schema/expected.json");
    }

    @Test
    public void testNodeKindStringSchema() throws Exception {
        runTest("9-node-kind-string-schema/shapes.ttl", "9-node-kind-string-schema/expected.json");
    }

    @Test
    public void testDescriptionAndTitle() throws Exception {
        runTest("10-description-and-title/shapes.ttl", "10-description-and-title/expected.json");
    }

    @Test
    public void testShortnameAnnotation() throws Exception {
        runTest("11-shortname-annotation/shapes.ttl", "11-shortname-annotation/expected.json");
    }

    @Test
    public void testNodeLabelAndComment() throws Exception {
        runTest("12-node-label-comment/shapes.ttl", "12-node-label-comment/expected.json");
    }

    @Test
    public void testNoActivePropertyShape() throws Exception {
        runTest("13-no-active-property-shape/shapes.ttl", "13-no-active-property-shape/expected.json");
    }



    private void runTest(String inputShaclFile, String expectedJsonFile) throws Exception {
        // Load the SHACL file
        Model shaclGraph = ModelFactory.createDefaultModel();
        shaclGraph.read(new FileInputStream(TEST_RESOURCES_DIR + inputShaclFile), RDF.uri, FileUtils.guessLang(inputShaclFile, "TURTLE"));

        // Generate the JSON schema
        JsonSchemaGenerator generator = new JsonSchemaGenerator("en", Collections.singletonList("http://example.org/MainNodeShape"));
        Schema outputSchema = generator.convertToJsonSchema(shaclGraph);

        // Load the expected JSON schema
        String expectedJson = Files.readString(Path.of(TEST_RESOURCES_DIR + expectedJsonFile));
        JSONObject expectedJsonObject = new JSONObject(expectedJson);

        // Compare the generated schema with the expected schema
        JSONObject generatedJsonObject = new JSONObject(outputSchema.toString());
        // Recursively compare JSONObjects, ignoring "container_language" and "@context" in "$defs"
        assertJsonEquals(expectedJsonObject, generatedJsonObject);

    }

    private void assertJsonEquals(JSONObject expected, JSONObject actual) {
        final List<String> IGNORED_KEYS = Arrays.asList( new String[] {"container_language", "@context"} );

        for (String key : expected.keySet()) {
            if (IGNORED_KEYS.contains(key)) {
                continue; // Ignore these keys in $defs
            }
            if (!actual.has(key)) {
                throw new AssertionError("Missing key: " + key + "\n"+actual.toString(2));
            }
            Object expectedValue = expected.get(key);
            Object actualValue = actual.get(key);


            if (expectedValue instanceof JSONObject && actualValue instanceof JSONObject) {
                assertJsonEquals((JSONObject) expectedValue, (JSONObject) actualValue);
            } else if (expectedValue instanceof org.json.JSONArray && actualValue instanceof org.json.JSONArray) {
                org.json.JSONArray expectedArr = (org.json.JSONArray) expectedValue;
                org.json.JSONArray actualArr = (org.json.JSONArray) actualValue;
                if (expectedArr.length() != actualArr.length()) {
                    throw new AssertionError("Array length mismatch for key: " + key + ", actual : "+ actualArr.length() +", expected : "+ expectedArr.length() +"\n"+actual.toString(2));
                }
                // Compare arrays ignoring order of elements
                for (int i = 0; i < expectedArr.length(); i++) {
                    Object expElem = expectedArr.get(i);
                    boolean found = false;
                    for (int j = 0; j < actualArr.length(); j++) {
                        Object actElem = actualArr.get(j);
                        if (expElem instanceof JSONObject && actElem instanceof JSONObject) {
                            try {
                                assertJsonEquals((JSONObject) expElem, (JSONObject) actElem);
                                found = true;
                                break;
                            } catch (AssertionError e) {
                                // Not equal, try next
                            }
                        } else {
                            if (expElem == null && actElem == null) {
                                found = true;
                                break;
                            }
                            if (expElem != null && expElem.equals(actElem)) {
                                found = true;
                                break;
                            }
                        }
                    }
                    if (!found) {
                        throw new AssertionError("Array element not found (ignoring order) for key: " + key + " at expected index " + i + ": " + expElem + "\n" + actual.toString(2));
                    }
                }
                if (expectedArr.length() != actualArr.length()) {
                    throw new AssertionError("Array length mismatch for key: " + key + "\n" + actual.toString(2));
                }
            } else {
                if (!expectedValue.equals(actualValue)) {
                    throw new AssertionError("Value mismatch for key: " + key + " (" + expectedValue + " != " + actualValue + ")" + "\n"+actual.toString(2));
                }
            }
        }
        // Check for unexpected keys in actual (except ignored ones)
        for (String key : actual.keySet()) {
            if (IGNORED_KEYS.contains(key)) {
                continue;
            }
            if (!expected.has(key)) {
                throw new AssertionError("Unexpected key: " + key + "\n"+actual.toString(2));
            }
        }
    }
}

