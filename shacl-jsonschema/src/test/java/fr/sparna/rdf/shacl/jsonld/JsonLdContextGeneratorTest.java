package fr.sparna.rdf.shacl.jsonld;

import java.io.FileInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.stream.Stream;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.vocabulary.RDF;
import org.json.JSONObject;
import org.junit.Test;


public class JsonLdContextGeneratorTest {

    private static final String TEST_RESOURCES_DIR = "src/test/resources/jsonld-context-generator-tests/";

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

        if (!Files.exists(inputShaclFile)) {
            throw new IllegalArgumentException("shapes.ttl not found in " + directory);
        }
        if (!Files.exists(expectedJsonFile)) {
            throw new IllegalArgumentException("expected.json not found in " + directory);
        }

        // Load the SHACL file
        Model shaclGraph = ModelFactory.createDefaultModel();
        shaclGraph.read(new FileInputStream(inputShaclFile.toFile()), RDF.uri, "TURTLE");

        // Generate the JSON-LD context
        JsonLdContextGenerator generator = new JsonLdContextGenerator();
        String outputContext = generator.generateJsonLdContext(shaclGraph);

        // Load the expected JSON-LD context
        String expectedJson = Files.readString(expectedJsonFile);
        JSONObject expectedJsonObject = new JSONObject(expectedJson);

        // Compare the generated schema with the expected schema
        JSONObject generatedJsonObject = new JSONObject(outputContext.toString());
        // Recursively compare JSONObjects
        try {
            assertJsonEquals(expectedJsonObject, generatedJsonObject);
        } catch (AssertionError e) {
            System.err.println("Test failed for directory: " + directory);
            System.err.println("Expected JSON:\n" + expectedJson);
            System.err.println("Generated JSON:\n" + outputContext);
            throw e; // Re-throw to fail the test
        }
    }

    private void assertJsonEquals(JSONObject expected, JSONObject actual) {

        for (String key : expected.keySet()) {
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
            if (!expected.has(key)) {
                throw new AssertionError("Unexpected key: " + key + "\n"+actual.toString(2));
            }
        }
    }
}

