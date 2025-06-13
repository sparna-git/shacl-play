import java.io.InputStream;

import com.apicatalog.jsonld.JsonLd;
import com.apicatalog.jsonld.document.Document;
import com.apicatalog.jsonld.document.JsonDocument;

import jakarta.json.JsonObject;

public class TestCompact {
    public static void main(String[] args) throws Exception {
        // This is a placeholder for the main method.
        // You can add your test logic here.
        System.out.println("TestCompact class is running.");

        testCompact("test1.jsonld", "test1-context.jsonld");
        testCompact("test2.jsonld", "test2-context.jsonld");

        
    }

    public static void testCompact(String inputDocumentPath, String contextDocumentPath) throws Exception {
        // input JSON document
        // Create a JSON document from InputStream or Reader
        InputStream test = TestCompact.class.getResourceAsStream(inputDocumentPath);
        Document document = JsonDocument.of(test);

        // read the JSON context document in test1-context.jsonld
        InputStream context = TestCompact.class.getResourceAsStream(contextDocumentPath);
        Document contextDocument = JsonDocument.of(context);

        // Compaction
        JsonObject result = JsonLd.compact(document, contextDocument)
            .compactToRelative(false)
            .get();

        // debug result JSON
        System.out.println("Compacted JSON:");
        System.out.println(result.toString());
    }
}
