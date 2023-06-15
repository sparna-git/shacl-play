package fr.sparna.rdf.shacl.generate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.jena.query.QuerySolutionMap;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.ResourceFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.jena.JenaResultSetHandlers;
import fr.sparna.rdf.jena.QueryExecutionService;

public class SamplingShaclGeneratorDataProvider extends BaseShaclGeneratorDataProvider implements ShaclGeneratorDataProviderIfc {

	private static final Logger log = LoggerFactory.getLogger(SamplingShaclGeneratorDataProvider.class);

	private Map<String, List<RDFNode>> sampleInstancesCache = new HashMap<>();

	public SamplingShaclGeneratorDataProvider(PaginatedQuery paginatedQuery, String endpointUrl) {
		super(paginatedQuery, endpointUrl);
	}

	public SamplingShaclGeneratorDataProvider(PaginatedQuery paginatedQuery, Model inputModel) {
		super(paginatedQuery, inputModel);
	}

	@Override
	public List<String> getTypes() {		
		try {
			// try with normal query
			return super.getTypes();
		} catch (Exception e) {
			e.printStackTrace();
			log.warn("Could not get complete list of types, will use a sampling technique");
			
			List<String> types = new ArrayList<>();
			String ORIGINAL_QUERY = readQuery("sampling/select-one-type.rq");
	
			while(true) {
				String filterClause = (types.isEmpty())?"":"filter (?type not in("+types.stream().map(t -> "<"+t+">").collect(Collectors.joining(", "))+"))";
				String filteredQuery = ORIGINAL_QUERY.replace("FILTER_CLAUSE", filterClause);
				
				List<String> typesBatch;
				try {
					typesBatch = JenaResultSetHandlers.convertSingleColumnUriToStringList(
							this.queryExecutionService.executeSelectQuery(filteredQuery, JenaResultSetHandlers::convertToListOfMaps)
					);
				} catch (RuntimeException e2) {
					e2.printStackTrace();
					log.warn("Got an exception when sampling types, probably a timeout : breaking");
					this.messageListener.accept("Warning, list of classes could be incomplete");
					break;
				}
				
				if(typesBatch.isEmpty())
					break;
				
				types.addAll(typesBatch);
			}

			return types;
		}
	}

	@Override
	public List<String> getProperties(String classUri) {
		try {
			// try with normal query
			return super.getProperties(classUri);
		} catch (Exception e) {
			e.printStackTrace();
			log.warn("Could not get complete list of properties of "+classUri+", will use a sample");
			
			SamplingQuery samplingQuery = new SamplingQuery(2, 50000, 50);
			List<Map<String, RDFNode>> instanceRows = samplingQuery.select(
					this.queryExecutionService,
					readQuery("sampling/select-instances.rq"),
					QueryExecutionService.buildQuerySolution("type", ResourceFactory.createResource(classUri))
			);
			List<RDFNode> instances = JenaResultSetHandlers.convertSingleColumnToRDFNodeList(instanceRows);
			
			// store the instance sample in cache
			this.sampleInstancesCache.put(classUri, instances);
			
			ValuesQuery valuesQuery = new ValuesQuery(20);
			List<Map<String, RDFNode>> rows = valuesQuery.select(queryExecutionService, readQuery("sampling/select-instances-properties.rq"), "uri", instances);
			
			this.messageListener.accept("Warning, properties read on a sample of "+this.sampleInstancesCache.get(classUri).size()+" entities");
			List<String> duplicatedList = JenaResultSetHandlers.convertSingleColumnUriToStringList(rows);
			Set<String> dedupList = new HashSet<String>(duplicatedList);
			return new ArrayList<String>(dedupList);
		}
	}

	@Override
	public List<String> getDatatypes(String classUri, String propertyUri) {	
		try {
			// try with normal query
			return super.getDatatypes(classUri, propertyUri);
		} catch (Exception e) {
			e.printStackTrace();
			log.warn("Could not get complete list of datatypes for "+propertyUri+" on class "+classUri+", will use only the sample");
			
			QuerySolutionMap qs = new QuerySolutionMap();
			qs.add("type", ResourceFactory.createResource(classUri));
			qs.add("property", ResourceFactory.createResource(propertyUri));
			
			ValuesQuery valuesQuery = new ValuesQuery(20);
			List<Map<String, RDFNode>> rows = valuesQuery.select(queryExecutionService, readQuery("select-datatypes.rq"), "uri", this.sampleInstancesCache.get(classUri));
			
			this.messageListener.accept("Warning, sh:datatype read on a sample of "+this.sampleInstancesCache.get(classUri).size()+" entities");
			List<String> duplicatedList = JenaResultSetHandlers.convertSingleColumnUriToStringList(rows);
			Set<String> dedupList = new HashSet<String>(duplicatedList);
			return new ArrayList<String>(dedupList);
		}
	}

	@Override
	public List<String> getObjectTypes(String classUri, String propertyUri) {
		try {
			return super.getObjectTypes(classUri, propertyUri);
		} catch (Exception e) {
			e.printStackTrace();
			log.warn("Could not get complete list of object types for "+propertyUri+" on class "+classUri+", will use only the sample");
			
			QuerySolutionMap qs = new QuerySolutionMap();
			qs.add("type", ResourceFactory.createResource(classUri));
			qs.add("property", ResourceFactory.createResource(propertyUri));
			
			ValuesQuery valuesQuery = new ValuesQuery(20);
			List<Map<String, RDFNode>> rows = valuesQuery.select(queryExecutionService, readQuery("select-object-types.rq"), "uri", this.sampleInstancesCache.get(classUri));
			
			this.messageListener.accept("Warning, sh:class read on a sample of "+this.sampleInstancesCache.get(classUri).size()+" entities");
			List<String> duplicatedList = JenaResultSetHandlers.convertSingleColumnUriToStringList(rows);
			Set<String> dedupList = new HashSet<String>(duplicatedList);
			
			return new ArrayList<String>(dedupList);
			
			
		}
	}
	
	

}
