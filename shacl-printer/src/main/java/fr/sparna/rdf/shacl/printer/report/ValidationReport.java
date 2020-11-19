package fr.sparna.rdf.shacl.printer.report;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.io.IOUtils;
import org.apache.jena.query.QueryExecution;
import org.apache.jena.query.QueryExecutionFactory;
import org.apache.jena.query.ResultSet;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.vocabulary.XSD;
import org.topbraid.jenax.util.JenaUtil;
import org.topbraid.shacl.model.SHFactory;
import org.topbraid.shacl.model.SHResult;
import org.topbraid.shacl.vocabulary.SH;

import fr.sparna.rdf.shacl.SHP;

public class ValidationReport {

	protected Model resultsModel;
	protected Model fullModel;
	
	private List<SHResult> results;

	public ValidationReport(Model resultsModel, Model fullModel) {
		super();
		this.resultsModel = resultsModel;
		this.fullModel = fullModel;
		
		// ensure namespaces
		this.resultsModel.setNsPrefix("sh", SH.BASE_URI);
		this.resultsModel.setNsPrefix("xsd", XSD.NS);
		this.fullModel.setNsPrefix("sh", SH.BASE_URI);
		this.fullModel.setNsPrefix("xsd", XSD.NS);
	}
	

	public synchronized List<SHResult> getResults() {
		if(results == null) {
			// Collect all results			
			Set<Resource> results = JenaUtil.getAllInstances(resultsModel.getResource(SH.ValidationResult.getURI()));
			
			// Turn the results Resource objects into SHResult instances
			this.results = new LinkedList<SHResult>();
			for(Resource candidate : results) {
				// SHResult result = SHFactory.asResult(candidate);
				SHResult result = candidate.as(SHResult.class);
				this.results.add(result);
			}
		}
		
		return results;
	}
	
	public synchronized List<SHResultSummaryEntry> getResultsSummary() {
		List<SHResultSummaryEntry> entries = new ArrayList<>();
		QueryExecution execution = null;
		try {
			execution = QueryExecutionFactory.create(
					IOUtils.toString(this.getClass().getResource(this.getClass().getSimpleName()+".rq"), "UTF-8"),
					this.getFullModel()
			);
			ResultSet resultSet = execution.execSelect();
			resultSet.forEachRemaining(solution -> {
				entries.add(SHResultSummaryEntry.fromQuerySolution(solution));
			});
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if(execution != null) execution.close();
		}
		
		return entries;
	}
	
	public synchronized List<SHResult> getResultsFor(SHResultSummaryEntry entry) {
		// GROUP BY ?sourceShape ?sourceConstraintComponent ?resultSeverity ?resultPath ?message
		List<SHResult> results = getResults().stream().filter(r -> {
			return (
					r.getSourceShape().equals(entry.getSourceShape())
					&&
					r.getSourceConstraintComponent().equals(entry.getSourceConstraintComponent())
					&&
					r.getSeverity().equals(entry.getResultSeverity())
					// to work with blank nodes we deactivate comparison on resultPath
					// for property shapes this is anyway identical to the path of the source shape
//					&&
//					(
//							(entry.getResultPath() == null && r.getPath() == null)
//							||
//							(entry.getResultPath() != null && r.getPath().equals(entry.getResultPath()))
//					)
					&&
					r.getMessage().equals(entry.getMessage())
			);
		}).collect(Collectors.toList());
		
		Collections.sort(results, new Comparator<SHResult>() {

			@Override
			public int compare(SHResult r1, SHResult r2) {
				if(!r1.getFocusNode().toString().equals(r2.getFocusNode().toString())) {
					return r1.getFocusNode().toString().compareTo(r2.getFocusNode().toString());
				} else {
					return 0;
				}
			}
			
		});
		
		return results;
	}	
	
	public long getNumberOfViolations() {
		return getResults().stream().filter(vr -> vr.getSeverity().equals(SH.Violation)).count();
	}
	
	public long getNumberOfWarnings() {
		return getResults().stream().filter(vr -> vr.getSeverity().equals(SH.Warning)).count();
	}
	
	public long getNumberOfInfos() {
		return getResults().stream().filter(vr -> vr.getSeverity().equals(SH.Info)).count();
	}
	
	public long getNumberOfOthers() {
		return getResults().stream().filter(vr -> !vr.getSeverity().equals(SH.Violation) && !vr.getSeverity().equals(SH.Warning) && !vr.getSeverity().equals(SH.Info)).count();
	}
	
	public List<Resource> getShapesWithNoMatch() {
		List<Statement> targetMatchedFalseStatements = this.fullModel.listStatements(null, this.fullModel.createProperty(SHP.TARGET_MATCHED), this.fullModel.createTypedLiteral(false)).toList();
		return targetMatchedFalseStatements.stream().map(s -> s.getSubject()).collect(Collectors.toList());
	}
	
	public boolean isConformant() {
		return this.resultsModel.containsLiteral(null, SH.conforms, true);
	}
	
	public boolean hasMatched() {
		// if not explicitely false, consider it true
		return !this.fullModel.containsLiteral(null, this.fullModel.createProperty(SHP.HAS_MATCHED), false);
	}

	public Model getResultsModel() {
		return resultsModel;
	}

	public Model getFullModel() {
		return fullModel;
	}	
	
}
