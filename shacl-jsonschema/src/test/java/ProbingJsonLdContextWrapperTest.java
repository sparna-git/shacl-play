import java.io.InputStream;


import fr.sparna.rdf.shacl.jsonschema.jsonld.ProbingJsonLdContextWrapper;
import jakarta.json.Json;
import jakarta.json.JsonValue;

public class ProbingJsonLdContextWrapperTest {

    public static void main(String[] args) throws Exception {
        
        /*
        InputStream test = TestCompact.class.getResourceAsStream("test1-context.jsonld");
        // Parse as a JSON value using a JSON parser
        JsonValue context = Json.createReader(test).readValue();
        
        ProbingJsonLdContextWrapper wrapper = new ProbingJsonLdContextWrapper(context);
        String TEST = "http://www.w3.org/ns/org#linkedTo";
        String result = wrapper.readTermForProperty(TEST, true, null, null);
        System.out.println(TEST + " --> " + result);
        boolean requiresArray = wrapper.requiresArray(TEST, true, null, null);
        System.out.println(TEST + " --> requiresArray ? " + requiresArray);
        String VALUE_TEST_OK = "http://data.europa.eu/eli/eli-draft-legislation-ontology#Vote";
        String valueOK = wrapper.readTermFromValue(VALUE_TEST_OK);
        System.out.println(VALUE_TEST_OK + " --> " + valueOK);
        String VALUE_TEST_NAMESPACE = "http://data.europa.eu/eli/eli-draft-legislation-ontology#Decision";
        String valueNAMESPACE = wrapper.readTermFromValue(VALUE_TEST_NAMESPACE);
        System.out.println(VALUE_TEST_NAMESPACE + " --> " + valueNAMESPACE);
        String VALUE_TEST_KO = "http://data.europa.eu/xxxxx#DoesNotExist";
        String valueKO = wrapper.readTermFromValue(VALUE_TEST_KO);
        System.out.println(VALUE_TEST_KO + " --> " + valueKO);

        JsonValue simplerContext = Json.createValue("https://schema.org");
        ProbingJsonLdContextWrapper simplerWrapper = new ProbingJsonLdContextWrapper(simplerContext);
        String VALUE_SCHEMA_ORG = "https://schema.org/Person";
        String valueSDO = simplerWrapper.readTermFromValue(VALUE_SCHEMA_ORG);
        System.out.println(VALUE_SCHEMA_ORG + " --> " + valueSDO);
        */


        InputStream baseTest = TestCompact.class.getResourceAsStream("jsonschema-generator-tests/25-base-uri-const-context/context.jsonld");
        // Parse as a JSON value using a JSON parser
        JsonValue baseContext = Json.createReader(baseTest).readValue();
        ProbingJsonLdContextWrapper baseWrapper = new ProbingJsonLdContextWrapper(baseContext);
        String VALUE_BASE = "https://data.europarl.europa.eu/def/document-types/TA";
        String valueBase = baseWrapper.readTermFromValue(VALUE_BASE);
        System.out.println(VALUE_BASE + " --> " + valueBase);
    }

}
