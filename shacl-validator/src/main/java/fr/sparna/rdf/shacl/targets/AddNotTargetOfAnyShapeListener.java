package fr.sparna.rdf.shacl.targets;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.topbraid.shacl.vocabulary.SH;

import fr.sparna.rdf.shacl.SHP;


/**
 * Listens for the focus nodes of shapes and adds a validation result for each subject in the data model that is not the target of any shape.
 */
public class AddNotTargetOfAnyShapeListener implements FocusNodeListener {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());

	private Model dataModel;
	private Model existingValidationReport;

	private Set<RDFNode> targetedResources = new HashSet<RDFNode>();

	public AddNotTargetOfAnyShapeListener(Model dataModel, Model existingValidationReport) {
		super();
		this.dataModel = dataModel;
		this.existingValidationReport = existingValidationReport;
	}

	@Override
	public void notifyFocusNodes(Resource shape, Model data, List<RDFNode> focusNodes) {
		targetedResources.addAll(focusNodes);
	}

	@Override
	public void notifyEndShape(Resource shape, Model data) {
		// nothing
	}

	@Override
	public void notifyEnd() {
		// every subject of any triple in the validated graph should be the target of at least one shape
		// otherwise, add a violation result in the validation report
		
		Resource report = existingValidationReport.listResourcesWithProperty(RDF.type, SH.ValidationReport).next();

		// Iterate over all subjects in the data model
		dataModel.listSubjects().forEachRemaining(subject -> {
			// Check if the subject is the target of at least one shape
			boolean isTarget = targetedResources.contains(subject);

			// If not, add a violation result in the validation report
			if (!isTarget) {
				log.debug("Subject " + subject + " is not the target of any shape");
				Resource violation = existingValidationReport.createResource();
				violation.addProperty(RDF.type, SH.ValidationResult);
				violation.addProperty(SH.resultSeverity, SH.Info);
				violation.addProperty(SH.focusNode, subject);
				violation.addProperty(SH.resultMessage, "Subject is not the target of any shape");
				violation.addProperty(SH.sourceConstraintComponent, existingValidationReport.createResource(SHP.CLOSED_GRAPH_SHAPE));
				violation.addProperty(SH.sourceShape, existingValidationReport.createResource(SHP.CLOSED_GRAPH_CONSTRAINT_COMPONENT));

				report.addProperty(SH.result, violation);
			}
		});
	}
	
}
