package fr.sparna.rdf.shacl.printer.report;

import org.topbraid.shacl.vocabulary.SH;

public class PrintableSHResultSummaryEntry {

	protected SHResultSummaryEntry shResultSummaryEntry;

	public PrintableSHResultSummaryEntry(SHResultSummaryEntry shResultSummaryEntry) {
		super();
		this.shResultSummaryEntry = shResultSummaryEntry;
	}
	
	public String getSourceShape() {
		return RDFRenderer.renderResource(shResultSummaryEntry.getSourceShape());
	}

	public String getMessage() {
		return shResultSummaryEntry.getMessage();
	}

	public String getResultPath() {
		return RDFRenderer.renderResource(shResultSummaryEntry.getResultPath());
	}

	public String getResultSeverity() {
		return RDFRenderer.renderResource(shResultSummaryEntry.getResultSeverity());
	}

	public String getSourceConstraintComponent() {
		return RDFRenderer.renderResource(shResultSummaryEntry.getSourceConstraintComponent());
	}
	
	public int getCount() {
		return shResultSummaryEntry.getCount();
	}
	
	public String getSampleFocusNode() {
		return RDFRenderer.renderRDFNode(shResultSummaryEntry.getSampleFocusNode());
	}
	
	public String getSampleValue() {
		return RDFRenderer.renderRDFNode(shResultSummaryEntry.getSampleValue());
	}

	public SHResultSummaryEntry getShResultSummaryEntry() {
		return shResultSummaryEntry;
	}
	
	public String getResultSeverityLabel() {
		if(
				!shResultSummaryEntry.getResultSeverity().getLocalName().equals(SH.Violation.getLocalName())
				&&
				!shResultSummaryEntry.getResultSeverity().getLocalName().equals(SH.Warning.getLocalName())
				&&
				!shResultSummaryEntry.getResultSeverity().getLocalName().equals(SH.Info.getLocalName())
				) {
			return "Other";
		} else {
			return shResultSummaryEntry.getResultSeverity().getLocalName();
		}
	}
	
	
	
	@Override
	public int hashCode() {
		// GROUP BY ?sourceShape ?sourceConstraintComponent ?resultSeverity ?resultPath ?message
		return (this.getSourceShape()+this.getSourceConstraintComponent()+this.getResultPath()).hashCode();
	}
	
}
