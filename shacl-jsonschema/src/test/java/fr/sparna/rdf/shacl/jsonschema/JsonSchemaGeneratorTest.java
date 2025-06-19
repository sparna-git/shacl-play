package fr.sparna.rdf.shacl.jsonschema;

import java.io.FileInputStream;
import java.io.StringReader;
import java.io.File;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Stream;

import jakarta.json.Json;
import jakarta.json.JsonReader;
import jakarta.json.JsonValue;

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
    public void testAllTestCases() throws Exception {
        // List all directories in the test resources folder
        try (Stream<Path> paths = Files.list(Path.of(TEST_RESOURCES_DIR))) {
            paths.filter(Files::isDirectory)
                .map(path -> path.getFileName().toString())
                .sorted()
                .forEach(directory -> {
                    try {
                        runTest(directory);
                        System.out.println("Test passed for directory: " + directory);
                    } catch (Exception e) {
                        throw new RuntimeException("Test failed for directory: " + directory, e);
                    }
                });
        }
    }



    private void runTest(String directory) throws Exception {
        Path testDir = Path.of(TEST_RESOURCES_DIR, directory);
        if (!Files.isDirectory(testDir)) {
            throw new IllegalArgumentException("Test directory does not exist: " + testDir);
        }

        Path inputShaclFile = testDir.resolve("shapes.ttl");
        Path expectedJsonFile = testDir.resolve("expected.json");
        Path contextPath = testDir.resolve("context.jsonld");

        if (!Files.exists(inputShaclFile)) {
            throw new IllegalArgumentException("shapes.ttl not found in " + directory);
        }
        if (!Files.exists(expectedJsonFile)) {
            throw new IllegalArgumentException("expected.json not found in " + directory);
        }

        // Load the SHACL file
        Model shaclGraph = ModelFactory.createDefaultModel();
        shaclGraph.read(new FileInputStream(inputShaclFile.toFile()), RDF.uri, "TURTLE");

        // Check if there's a context file in the same directory        
        JsonValue context = null;
        if (Files.exists(contextPath)) {
            String contextJson = Files.readString(contextPath);
            try (JsonReader reader = Json.createReader(new StringReader(contextJson))) {
                context = reader.readValue();
            }
        }

        // Generate the JSON schema
        JsonSchemaGenerator generator = new JsonSchemaGenerator("en", context);
        Schema outputSchema = generator.convertToJsonSchema(
            shaclGraph,
            Collections.singletonList("http://example.org/MainNodeShape")
        );

        // Load the expected JSON schema
        String expectedJson = Files.readString(expectedJsonFile);
        JSONObject expectedJsonObject = new JSONObject(expectedJson);

        // Compare the generated schema with the expected schema
        JSONObject generatedJsonObject = new JSONObject(outputSchema.toString());
        // Recursively compare JSONObjects, ignoring "container_language" and "@context" in "$defs"
        try {
            assertJsonEquals(expectedJsonObject, generatedJsonObject);
        } catch (AssertionError e) {
            System.err.println("Test failed for directory: " + directory);
            System.err.println("Expected JSON:\n" + expectedJsonObject.toString(2));
            System.err.println("Generated JSON:\n" + generatedJsonObject.toString(2));
            throw e; // Re-throw to fail the test
        }
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

