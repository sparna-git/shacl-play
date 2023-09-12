package fr.sparna.rdf.shacl.printer.report;

public class PrintableSHResult {

	protected SHResult shresult;
	
	public PrintableSHResult(SHResult shresult) {
		super();
		this.shresult = shresult;
	}

	public String getSourceShape() {
		return RDFRenderer.renderResource(shresult.getSourceShape());
	}

	public String getFocusNode() {
		return RDFRenderer.renderRDFNode(shresult.getFocusNode());
	}

	public String getMessage() {
		return shresult.getMessage();
	}

	public String getResultPath() {
		return RDFRenderer.renderResource(shresult.getPath());
	}

	public String getResultSeverity() {
		return RDFRenderer.renderResource(shresult.getSeverity());
	}

	public String getSourceConstraintComponent() {
		return RDFRenderer.renderResource(shresult.getSourceConstraintComponent());
	}

	public String getValue() {
		return RDFRenderer.renderRDFNode(shresult.getValue());
	}

	public SHResult getShresult() {
		return shresult;
	}
	
}
