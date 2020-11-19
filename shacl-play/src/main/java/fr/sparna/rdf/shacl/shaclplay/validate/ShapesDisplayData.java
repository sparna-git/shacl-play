package fr.sparna.rdf.shacl.shaclplay.validate;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

import org.apache.jena.rdf.model.Model;
import org.topbraid.shacl.model.SHNodeShape;
import org.topbraid.shacl.model.SHShape;
import org.topbraid.shacl.vocabulary.SH;

import fr.sparna.rdf.shacl.printer.report.ValidationReport;
import fr.sparna.rdf.shacl.printer.report.ValidationReportHtmlWriter;


public class ShapesDisplayData {

	public static final String KEY = ShapesDisplayData.class.getSimpleName();

	
	public enum SeverityDisplayLevel {
		UNCHECKED("constraint-unchecked", "bg-secondary", "fa-home"),
		SUCCESS("constraint-success", "bg-success", "fa-thumbs-up"),
		INFO("constraint-info", "bg-info", "glyphicon-ok"),
		WARNING("constraint-warning", "bg-warning", "fa-exclamation-triangle"),
		VIOLATION("constraint-violation", "bg-danger", "fa-engine-warning");
		
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
	
	/**
	 * The validated data
	 */
	private Model displayModel;
	/**
	 * Shapes graph
	 */
	private ShapesGraph shapesGraph;
	/**
	 * The validated data
	 */
	private Model dataModel;
	/**
	 * An object to render various portion of the shapes in HTML
	 */
	private HTMLRenderer renderer;
	/**
	 * The validation results to display
	 */
	private ValidationReport validationReport;
	/**
	 * Indicates if we are displaying validation results or just displaying a shape file without validation
	 */
	private boolean displayValidationResults = true;
	/**
	 * 
	 */
	private PermalinkGenerator permalinkGenerator;
	
	public ShapesDisplayData(Model displayModel, HTMLRenderer renderer, ShapesGraph shapesGraph, ValidationReport validationReport) {
		super();
		this.displayModel = displayModel;
		this.renderer = renderer;
		this.shapesGraph = shapesGraph;
		this.validationReport = validationReport;
	}
	
	public ShapesDisplayData(Model displayModel, HTMLRenderer renderer, ShapesGraph shapesGraph) {
		this(displayModel, renderer, shapesGraph, null);
	}

	public Model getDisplayModel() {
		return displayModel;
	}

	public HTMLRenderer getRenderer() {
		return renderer;
	}

	public List<SHNodeShape> getShapes() {
		return this.shapesGraph.getRootNodeShapes();
	}

	public Model getDataModel() {
		return dataModel;
	}

	public void setDataModel(Model dataModel) {
		this.dataModel = dataModel;
	}
	
	public boolean isDisplayValidationResults() {
		return displayValidationResults;
	}

	public void setDisplayValidationResults(boolean displayValidationResults) {
		this.displayValidationResults = displayValidationResults;
	}

	public int getNumberOfShapes() {
		return getShapes().size();
	}
	
	public int getNumberOfConstraints() {
		int count = 0;
		for (SHNodeShape aShape : getShapes()) {
			count += aShape.getPropertyShapes().size();
		}
		return count;
	}
	
	public long getNumberOfValidatedResources() {
		return this.dataModel.listSubjects().toList().size();
	}
	
	public long getNumberOfValidatedStatements() {
		return this.dataModel.size();
	}
	
	public String getPermalink() {
		return (this.permalinkGenerator != null)?this.permalinkGenerator.generatePermalink():null;
	}
	
	public String getBadgeLink() {
		return (this.permalinkGenerator != null)?this.permalinkGenerator.generateBadgeLink():null;
	}

	public void setPermalinkGenerator(PermalinkGenerator permalinkGenerator) {
		this.permalinkGenerator = permalinkGenerator;
	}

	public SeverityDisplayLevel getDisplayLevel(SHShape shape) {
		SeverityDisplayLevel result = SeverityDisplayLevel.VIOLATION;
		
		if(shape.getSeverity() == SH.Info) {
			result = SeverityDisplayLevel.INFO;
		} else if(shape.getSeverity() == SH.Warning) {
			result = SeverityDisplayLevel.WARNING;
		} else if(shape.getSeverity() == SH.Violation) {
			result = SeverityDisplayLevel.VIOLATION;
		} else {
			result = SeverityDisplayLevel.VIOLATION;
		}
		
//		if(this.displayValidationResults) {
//			// if the constraint is a property constraint and the shape did not matched any target, then consider the severity is unchecked
//			if(
//					(constraint instanceof NodeShape && !((NodeShape)constraint).hasMatchedTargets())
//					||
//					(constraint instanceof PropertyShape && !((PropertyShape)constraint).getParent().hasMatchedTargets())
//			) {
//				result = SeverityDisplayLevel.UNCHECKED;
//			} else if(constraint.getValidationResults() == null || constraint.getValidationResults().size() == 0) {
//				// if displaying violations, overwrite the CSS to success if there are no validation results on the constraint
//				result = SeverityDisplayLevel.SUCCESS;
//			}
//		}
		
		return result;	
	}	
	
	public SeverityDisplayLevel getGlobalSeverity() {
		// by default this is unchecked
		SeverityDisplayLevel result = SeverityDisplayLevel.UNCHECKED;
		
//		// if at least one Shape was successfully checked, result is success by default
//		for (NodeShape s : shapes.getShapes()) {
//			if(s.hasMatchedTargets()) {
//				result = SeverityDisplayLevel.SUCCESS;
//			}
//		}
		
		// first look for info
		if(this.displayModel.contains(null, SH.resultSeverity, SH.Info)) {
			result = SeverityDisplayLevel.INFO;
		}
		if(this.displayModel.contains(null, SH.resultSeverity, SH.Warning)) {
			result = SeverityDisplayLevel.WARNING;
		}
		if(this.displayModel.contains(null, SH.resultSeverity, SH.Violation)) {
			result = SeverityDisplayLevel.VIOLATION;
		}
		
		return result;		
	}
	
	public ValidationReport getValidationReport() {
		return validationReport;
	}

	public String getValidationReportFull(String lang) throws IOException {
		ValidationReportHtmlWriter w = new ValidationReportHtmlWriter(false);
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		w.write(this.validationReport, baos, Locale.forLanguageTag(lang));
		try {
			baos.close();
			return baos.toString("UTF-8");
		} catch (Exception ignore) {
			ignore.printStackTrace();
			return null;
		}
	}

}
