package fr.sparna.rdf.shacl.doc.write;


public class ShapesDocumentationXsltShaclPlayWriter extends ShapesDocumentationXsltWriter {

	public ShapesDocumentationXsltShaclPlayWriter(ShapesDocumentationWriterIfc.MODE mode) {
		super(
			mode,
			"doc2html.xsl",
			"dataset2html.xsl"			
		);
	}

}
