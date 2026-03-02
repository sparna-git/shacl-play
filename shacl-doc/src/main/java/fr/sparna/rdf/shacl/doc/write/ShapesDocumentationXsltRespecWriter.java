package fr.sparna.rdf.shacl.doc.write;


public class ShapesDocumentationXsltRespecWriter extends ShapesDocumentationXsltWriter {

	public ShapesDocumentationXsltRespecWriter(ShapesDocumentationWriterIfc.MODE mode) {
		super(
			mode,
			"doc2html_respec.xsl",
			"dataset2html_respec.xsl"			
		);
	}

}
