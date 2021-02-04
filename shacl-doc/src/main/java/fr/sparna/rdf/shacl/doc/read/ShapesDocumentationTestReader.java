package fr.sparna.rdf.shacl.doc.read;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;

import fr.sparna.rdf.shacl.doc.model.PropertyShapeDocumentation;
import fr.sparna.rdf.shacl.doc.model.ShapesDocumentation;
import fr.sparna.rdf.shacl.doc.model.ShapesDocumentationSection;

public class ShapesDocumentationTestReader implements ShapesDocumentationReaderIfc {

	@Override
	public ShapesDocumentation readShapesDocumentation(InputStream input, String fileName) {
		ShapesDocumentation shapesDocumentation = new ShapesDocumentation();
		
		// HERE : READ Model and populate shapesDocumentation
		shapesDocumentation.setTitle("Test title");
		
		ShapesDocumentationSection section1 = new ShapesDocumentationSection();
		section1.setTitle("MEP Vote");
		
		PropertyShapeDocumentation propDoc1 = new PropertyShapeDocumentation();
		/*propDoc1.setPath("http://test.fr");
		propDoc1.setLabel("my property");
		propDoc1.setCardinalities("0..n");
		propDoc1.setExpectedValue("xsd:string");
		propDoc1.setDescription("This is a definition");
		section1.setPropertySections(Arrays.asList(new PropertyShapeDocumentation[] { propDoc1 }));
		*/
		ShapesDocumentationSection section2 = new ShapesDocumentationSection();
		section2.setTitle("Vote");
		
		shapesDocumentation.setSections(Arrays.asList( new ShapesDocumentationSection[] {
			section1,
			section2
		})
		);
		
		return shapesDocumentation;
	}

	
	
}
