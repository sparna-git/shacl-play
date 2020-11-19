package fr.sparna.rdf.shacl.printer.report;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import org.topbraid.shacl.vocabulary.SH;

public class PrintableValidationReport {

	public enum SeverityDisplayLevel {
		UNCHECKED("constraint-unchecked", "bg-secondary", "fa-home"),
		SUCCESS("constraint-success", "bg-success", "fa-thumbs-up"),
		INFO("constraint-info", "bg-info", "glyphicon-ok"),
		WARNING("constraint-warning", "bg-warning", "fa-exclamation-triangle"),
		VIOLATION("constraint-violation", "bg-danger", "fa-engine-warning"),
		OTHER("constraint-other", "bg-danger", "fa-exclamation")
		;
		
		private String constraintCssClass;
		private String headerCssClass;
		private String icon;
		
		private SeverityDisplayLevel(String constraintCssClass, String headerCssClass, String icon) {
			this.constraintCssClass = constraintCssClass;
			this.headerCssClass = headerCssClass;
			this.icon = icon;
		}
		
		public String getConstraintCssClass() {
			return constraintCssClass;
		}
		
		public String getHeaderCssClass() {
			return headerCssClass;
		}

		public String getIcon() {
			return icon;
		}

	}
	
	protected ValidationReport validationReport;
	protected List<PrintableSHResultSummaryEntry> resultsSummary;
	protected List<PrintableSHResult> results;
	
	protected final long maximumResults = 1000;

	public PrintableValidationReport(ValidationReport validationReport) {
		this.validationReport = validationReport;
	}
	
	public synchronized List<PrintableSHResultSummaryEntry> getResultsSummary() {
		if(resultsSummary == null) {
			this.resultsSummary = this.validationReport.getResultsSummary().stream()
					.map(e -> new PrintableSHResultSummaryEntry(e))
					.collect(Collectors.toList());
			
			Collections.sort(this.resultsSummary, (e1, e2) -> { 
				List<String> order = Arrays.asList(new String[] { SH.Violation.getLocalName(), SH.Warning.getLocalName(), SH.Info.getLocalName(), "Other" });
				// first sort by severity
				if(order.indexOf(e1.getResultSeverityLabel()) != order.indexOf(e2.getResultSeverityLabel())) {
					return order.indexOf(e1.getResultSeverityLabel()) - order.indexOf(e2.getResultSeverityLabel());
				} else if(e2.getCount() != e1.getCount()) {
					// then sort by count
					return e2.getCount() - e1.getCount() ;
				} else {
					// then sort by message
					return e1.getMessage().compareTo(e2.getMessage()) ;
				}
			});
			
		}
		return this.resultsSummary;		
	}
	
	public synchronized List<PrintableSHResult> getResultsFor(PrintableSHResultSummaryEntry entry) {
		return this.validationReport.getResultsFor(entry.getShResultSummaryEntry()).stream()
				.map(r -> new PrintableSHResult(r))
				.collect(Collectors.toList());
	}
	
	public synchronized List<PrintableSHResult> getLimitedResultsFor(PrintableSHResultSummaryEntry entry) {
		return this.validationReport.getResultsFor(entry.getShResultSummaryEntry()).stream()
				.limit(this.maximumResults)
				.map(r -> new PrintableSHResult(r))
				.collect(Collectors.toList());
	}

	public SeverityDisplayLevel getGlobalSeverity() {
		SeverityDisplayLevel result = SeverityDisplayLevel.UNCHECKED;
		
		// if nothing matched this is unchecked
		if(!this.validationReport.hasMatched()) {
			result = SeverityDisplayLevel.UNCHECKED;
		} else {
			if(this.validationReport.isConformant()) {
				result = SeverityDisplayLevel.SUCCESS;
			}
			
			// first look for info
			if(this.validationReport.resultsModel.contains(null, SH.resultSeverity, SH.Info)) {
				result = SeverityDisplayLevel.INFO;
			}
			// then warning
			if(this.validationReport.resultsModel.contains(null, SH.resultSeverity, SH.Warning)) {
				result = SeverityDisplayLevel.WARNING;
			}
			// then violation
			if(this.validationReport.resultsModel.contains(null, SH.resultSeverity, SH.Violation)) {
				result = SeverityDisplayLevel.VIOLATION;
			}					
		}	
		
		return result;
	}
	
	public String printHeaderLine(int numberOfViolations, int numberOfWarnings, int numberOfInfos, int numberOfOthers) {
		StringBuffer buffer = new StringBuffer();
		if(numberOfViolations > 0) {
			buffer.append(numberOfViolations+" Violation"+((numberOfViolations > 1)?"s":""));
			if(numberOfWarnings > 0 || numberOfInfos > 0 || numberOfOthers > 0) {
				buffer.append(", ");
			}
		}
		if(numberOfWarnings > 0) {
			buffer.append(numberOfWarnings+" Warning"+((numberOfWarnings > 1)?"s":""));
			if( numberOfInfos > 0 || numberOfOthers > 0) {
				buffer.append(", ");
			}
		}
		if(numberOfInfos > 0) {
			buffer.append(numberOfInfos+" Info"+((numberOfInfos > 1)?"s":""));
			if( numberOfOthers > 0) {
				buffer.append(", ");
			}
		}
		if(numberOfOthers > 0) {
			buffer.append(numberOfOthers+" Other"+((numberOfOthers > 1)?"s":""));
		}
		
		if(numberOfViolations == 0 && numberOfWarnings == 0 && numberOfInfos == 0 && numberOfOthers == 0) {
			buffer.append("All green ! (0 violation, 0 warning, 0 info)");
		}
		
		return buffer.toString();				
	}

	public long getMaximumResults() {
		return maximumResults;
	}

	public ValidationReport getValidationReport() {
		return validationReport;
	}
	
	
	
}
