package fr.sparna.rdf.shacl.shacl2xsd;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.util.FileUtils;
import org.apache.jena.vocabulary.RDF;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.xmlunit.builder.DiffBuilder;
import org.xmlunit.builder.Input;
import org.xmlunit.diff.ComparisonResult;
import org.xmlunit.diff.Diff;
import org.xmlunit.diff.Difference;

import junit.framework.AssertionFailedError;
import junit.framework.Test;
import junit.framework.TestResult;

/**
 * Don't rename this class otherwise it could be picked up by Maven plugin to execute test.
 * @author thomas
 *
 */
public class Shacl2XsdTestExecution implements Test {

	protected File testFolder;
	protected Shacl2XsdConverter converter;
	
	public Shacl2XsdTestExecution(File testFolder) {
		super();
		this.testFolder = testFolder;

		this.converter = new Shacl2XsdConverter("http://data.europa.eu/snb/model#");
	}

	@Override
	public int countTestCases() {
		return 1;
	}

	@Override
	public void run(TestResult result) {
		result.startTest(this);
		final File input = new File(this.testFolder, "input.ttl");
		final File expected = new File(this.testFolder, "expected.xsd");
		System.out.println("Testing "+input.getAbsolutePath());
		try {
			Model shacl = ModelFactory.createDefaultModel();
			shacl.read(new FileInputStream(input), RDF.uri, FileUtils.guessLang(input.getName(), "RDF/XML"));
			
			Document output = null;
			try {
				output = converter.convert(shacl);
			} catch (Exception e) {
				e.printStackTrace();
				result.addError(this, e);
				result.endTest(this);
				return;
			}
		
			System.out.println(nodeToString(output.getDocumentElement()));
			
			if(expected.exists()) {

				DiffBuilder builder = 
				DiffBuilder
						.compare(Input.fromFile(expected).build())
						.ignoreWhitespace()
						.ignoreComments()
						.checkForSimilar()
						.withTest(Input.fromDocument(output).build());
				
				Diff diff = builder.build();
				
				List<Difference> differences = new ArrayList<>();
				diff.getDifferences().forEach(differences::add);
				
				// ignore differences in tags ordering
				if(differences.stream().anyMatch(d -> d.getResult() == ComparisonResult.DIFFERENT)) {
					result.addFailure(this, new AssertionFailedError("Test failed on "+this.testFolder+":\n"+diff.toString()));
				}
			} else {				
				result.endTest(this);
			}
		} catch (Exception e1) {
			result.addError(this, e1);
		}
		result.endTest(this);
	}
	
	private String nodeToString(Node node) {
		StringWriter sw = new StringWriter();
		try {
			Transformer t = TransformerFactory.newInstance().newTransformer();
			t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
			t.setOutputProperty(OutputKeys.INDENT, "yes");
			t.transform(new DOMSource(node), new StreamResult(sw));
		} catch (TransformerException te) {
			System.out.println("nodeToString Transformer Exception");
		}
		return sw.toString();
	}

	@Override
	public String toString() {
		return testFolder.getName();
	}
	
	

}
