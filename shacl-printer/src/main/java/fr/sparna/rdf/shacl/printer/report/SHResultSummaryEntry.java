package fr.sparna.rdf.shacl.printer.report;

import org.apache.jena.query.QuerySolution;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;

public class SHResultSummaryEntry {

	protected Resource sourceShape;
	protected String message;
	
	// identical to the source shape path, so can be any valid SHACL property path, including blank node
	protected Resource resultPath;
	protected Resource resultSeverity;
	protected Resource sourceConstraintComponent;
	
	protected int count;
	protected RDFNode sampleFocusNode;
	protected RDFNode sampleValue;
	
	public static SHResultSummaryEntry fromQuerySolution(QuerySolution solution) {
		SHResultSummaryEntry result = new SHResultSummaryEntry();
		
		// pretty source shape
		Resource sourceShape = solution.getResource("sourceShape");
		result.setSourceShape(sourceShape);

		Resource sourceConstraintComponent = solution.getResource("sourceConstraintComponent");
		result.setSourceConstraintComponent(sourceConstraintComponent);

		Resource resultSeverity = solution.getResource("resultSeverity");
		result.setResultSeverity(resultSeverity);
		
		// result path
		if(solution.contains("resultPath")) {
			Resource resultPath = solution.getResource("resultPath");
			result.setResultPath(resultPath);
		}
		
		result.setMessage(solution.getLiteral("message").getLexicalForm());
		
		// count
		result.setCount(solution.getLiteral("count").getInt());
		
		// sample value URI
		if(solution.contains("sampleValue")) {
			RDFNode sampleValue = solution.get("sampleValue");
			result.setSampleValue(sampleValue);
		}
		
		// sample focus node
		if(solution.contains("sampleFocusNode")) {
			RDFNode sampleFocusNode = solution.get("sampleFocusNode");
			result.setSampleFocusNode(sampleFocusNode);
		}
		
		return result;
	}

	public Resource getSourceShape() {
		return sourceShape;
	}

	public void setSourceShape(Resource sourceShape) {
		this.sourceShape = sourceShape;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Resource getResultPath() {
		return resultPath;
	}

	public void setResultPath(Resource resultPath) {
		this.resultPath = resultPath;
	}

	public Resource getResultSeverity() {
		return resultSeverity;
	}

	public void setResultSeverity(Resource resultSeverity) {
		this.resultSeverity = resultSeverity;
	}

	public Resource getSourceConstraintComponent() {
		return sourceConstraintComponent;
	}

	public void setSourceConstraintComponent(Resource sourceConstraintComponent) {
		this.sourceConstraintComponent = sourceConstraintComponent;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public RDFNode getSampleFocusNode() {
		return sampleFocusNode;
	}

	public void setSampleFocusNode(RDFNode sampleFocusNode) {
		this.sampleFocusNode = sampleFocusNode;
	}

	public RDFNode getSampleValue() {
		return sampleValue;
	}

	public void setSampleValue(RDFNode sampleValue) {
		this.sampleValue = sampleValue;
	}
	
}
