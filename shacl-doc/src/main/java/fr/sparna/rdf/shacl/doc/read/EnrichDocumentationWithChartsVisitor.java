package fr.sparna.rdf.shacl.doc.read;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.shacl.vocabulary.SHACLM;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.VOID;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.jena.ModelRenderingUtils;
import fr.sparna.rdf.shacl.SHACL_PLAY;
import fr.sparna.rdf.shacl.doc.NodeShape;
import fr.sparna.rdf.shacl.doc.PropertyShape;
import fr.sparna.rdf.shacl.doc.model.Chart;
import fr.sparna.rdf.shacl.doc.model.ChartDataItem;
import fr.sparna.rdf.shacl.doc.model.PropertyShapeDocumentation;
import fr.sparna.rdf.shacl.doc.model.ShapesDocumentation;
import fr.sparna.rdf.shacl.doc.model.ShapesDocumentationSection;
import fr.sparna.rdf.shacl.generate.visitors.ShaclVisitorIfc;

public class EnrichDocumentationWithChartsVisitor implements ShaclVisitorIfc {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	private Model statisticsModel;
	private ShapesDocumentation documentation;
	private String lang;
	
	private transient ShapesDocumentationSection currentSection;
	
	public EnrichDocumentationWithChartsVisitor(Model statisticsModel, ShapesDocumentation documentation, String lang) {
		super();
		this.statisticsModel = statisticsModel;
		this.documentation = documentation;
		this.lang = lang;
	}
	
	@Override
	public void visitModel(Model model) {
		// nothing
	}

	@Override
	public void visitOntology(Resource ontology) {
		// nothing
	}

	@Override
	public void visitNodeShape(Resource aNodeShape) {
		// find & store current corresponding section
		// find corresponding section
		NodeShape ns = new NodeShape(aNodeShape);
		ShapesDocumentationSection section = this.documentation.findSectionByUriOrId(ns.getURIOrId());
		if(section != null) {
			this.currentSection = section;
			this.currentSection.setCharts(new ArrayList<Chart>());
		} else {
			this.currentSection = null;
		}
	}

	@Override
	public void visitPropertyShape(Resource aPropertyShape, Resource aNodeShape) {
		
		if(aPropertyShape.hasProperty(SHACLM.in)) {
			List<RDFNode> values = aPropertyShape.getProperty(SHACLM.in).getObject().as(RDFList.class).asJavaList();
			
			if(values.size() > 1) {
				// read corresponding property partition
				List<Resource> propertyPartitions = statisticsModel.listStatements(null, DCTerms.conformsTo, aPropertyShape).mapWith(t-> t.getSubject()).toList();
				
				if(propertyPartitions.size() == 0) {
					log.debug("Cannot find corresponding property partition for "+aPropertyShape);
				} else {
					if(propertyPartitions.size() > 1) {
						log.debug("More than one property partition found for "+aPropertyShape);
					}
					Resource aPropertyPartition = propertyPartitions.get(0);
					
					// find corresponding section in doc
					if(this.currentSection != null) {
						PropertyShape ps = new PropertyShape(aPropertyShape);
						PropertyShapeDocumentation propertySection = this.currentSection.findPropertyShapeDocumentationSectionByUriOrId(ps.getURIOrId());
						
						if(propertySection != null) {
							
							Chart cd = new Chart();
							List<ChartDataItem> dataItems = new ArrayList<>();
							
							// read object partitions for each value in the sh:in list
							// we need to sort the list first, by display labels
							SortedMap<String, RDFNode> sortedValues = new TreeMap<>();
							values.stream().forEach(v -> {
								// build a label, same as in the documentation generation
								String label = ModelRenderingUtils.render(v, true);	
								sortedValues.put(label, v);
							});
							for (Entry<String, RDFNode> anEntry : sortedValues.entrySet()) {
								// find corresponding object partition in statistics
								Resource valuePartition = findValuePartition(aPropertyPartition, anEntry.getValue());
								if(valuePartition != null) {
									// read number of distinct subjects
									int nbSubjects = valuePartition.getProperty(VOID.distinctSubjects).getInt();									
									dataItems.add(new ChartDataItem(anEntry.getKey(), nbSubjects));
								}
							}
							
							// store values
							cd.setItems(dataItems);
							
							// store label - same as documentation table
							cd.setTitle(ps.getDisplayLabel(aPropertyShape.getModel(), this.lang));
							
							// add our chart to the list
							this.currentSection.getCharts().add(cd);
						}
					}
				}
			}
		}
	}
	
	private Resource findValuePartition(Resource propertyPartition, RDFNode value) {
		List<RDFNode> valuePartitions = propertyPartition.listProperties(propertyPartition.getModel().createProperty(SHACL_PLAY.VALUE_PARTITION)).mapWith(st -> st.getObject()).toList();
		for (RDFNode aValuePartition : valuePartitions) {
			if(
					aValuePartition.canAs(Resource.class)
					&&
					aValuePartition.asResource().hasProperty(propertyPartition.getModel().createProperty(SHACL_PLAY.VALUE))
			) {
				RDFNode v = aValuePartition.asResource().getRequiredProperty(propertyPartition.getModel().createProperty(SHACL_PLAY.VALUE)).getObject();
				if(v.equals(value)) {
					return aValuePartition.asResource();
				}
			}
		}
		return null;
	}

	@Override
	public void leaveModel(Model model) {
		// nothing
	}

}
