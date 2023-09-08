package fr.sparna.rdf.shacl.generate.visitors;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.shacl.vocabulary.SHACLM;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;
import org.apache.jena.vocabulary.RDFS;
import org.apache.jena.vocabulary.SKOS;
import org.apache.jena.vocabulary.XSD;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.shacl.DASH;
import fr.sparna.rdf.shacl.generate.ShaclGenerator;

public class AssignLabelRoleVisitor implements ShaclVisitorIfc {

	private static final Logger log = LoggerFactory.getLogger(ShaclGenerator.class);

	private Model model;
	
	public AssignLabelRoleVisitor() {
	}
	
	@Override
	public void visitModel(Model model) {
		this.model = model;
		
		// add dash namespace
		model.setNsPrefix("dash", "http://datashapes.org/dash#");
	}

	@Override
	public void visitOntology(Resource ontology) {

	}

	@Override
	public void visitNodeShape(Resource aNodeShape) {
		List<Resource> properties = Arrays.asList(new Resource[] {
				SKOS.prefLabel,
				FOAF.name,
				DCTerms.title,
				// don't use SchemaDO as this is Jena 4
				ModelFactory.createDefaultModel().createProperty("http://schema.org/name"),				
				RDFS.label
		});
		
		// try with each property in turn
		boolean found = false;
		for (Resource resource : properties) {
			Resource propertyShape = findPropertyShapeWithPath(aNodeShape, resource);
			if(propertyShape != null) {
				// does not include a check on maxCount
				if(canBeLabelRole(propertyShape, false)) {
					log.debug("Setting LabelRole on "+aNodeShape.getURI()+" to shape "+propertyShape+" (property "+propertyShape.getRequiredProperty(SHACLM.path).getObject()+")");
					propertyShape.addProperty(DASH.propertyRole, DASH.LabelRole);
					// break on first found
					found = true;
					break;
				}
			}
		}
		
		if(!found) {
			List<Resource> labelRolePropertyShapes = new ArrayList<Resource>();
			// try to find a single property shape with the required characteristic, and use it as label
			ShaclVisit sm = new ShaclVisit(aNodeShape.getModel());
			Set<Resource> shapes = sm.listPropertyShapes(aNodeShape);
			for (Resource aPropertyShape : shapes) {
				// does include a check on maxCount
				if(canBeLabelRole(aPropertyShape, true)) {
					labelRolePropertyShapes.add(aPropertyShape);
				}
			}
			
			if(labelRolePropertyShapes.size() > 0) {
				if(labelRolePropertyShapes.size() > 1) {
					log.warn("Unable to set LabelRole on "+aNodeShape.getURI()+", because found "+labelRolePropertyShapes.size() +" property shapes that can be label roles.");
				} else {
					log.debug("Setting LabelRole on "+aNodeShape.getURI()+" to shape "+labelRolePropertyShapes.get(0)+" (property "+labelRolePropertyShapes.get(0).getRequiredProperty(SHACLM.path).getObject()+")");
					labelRolePropertyShapes.get(0).addProperty(DASH.propertyRole, DASH.LabelRole);
				}
			}
			
		}
	}

	@Override
	public void visitPropertyShape(Resource aPropertyShape, Resource aNodeShape) {

	}

	@Override
	public void leaveModel(Model model) {
		
	}
	
	private Resource findPropertyShapeWithPath(Resource nodeShape, Resource path) {
		ShaclVisit sm = new ShaclVisit(nodeShape.getModel());
		Set<Resource> shapes = sm.listPropertyShapes(nodeShape);
		Resource pShape = shapes.stream().filter(s -> {
			return 
					s.getRequiredProperty(SHACLM.path).getObject().isURIResource()
					&&
					s.getRequiredProperty(SHACLM.path).getObject().asResource().getURI().equals(path.getURI())
					;
		}).findFirst().orElse(null);
		return pShape;
	}
	
	private boolean canBeLabelRole(Resource propertyShape, boolean includeMaxCardCheck) {
		// the property must have a global min card of 1, or be unique per language
		return(
				(
						(
								hasDatatypeString(propertyShape)
								||
								hasDatatypeLangString(propertyShape)
								||
								hasDatatypeStringOrLangString(propertyShape)
						)
						||
						hasNodeKindLiteral(propertyShape)
				)
				&&
				hasMinCountOne(propertyShape)
				&&
				(
						!includeMaxCardCheck
						||
						(
							isUniqueLang(propertyShape)
							||
							hasMaxCountOne(propertyShape)
						)
				)
		);
				
	}
	
	private boolean hasDatatypeStringOrLangString(Resource propertyShape) {
		List<Statement> statements = propertyShape.listProperties(SHACLM.datatype).toList();
		
		
		if(
				!statements.isEmpty()
				&&
				statements.get(0).getObject().isResource()
				&&
				statements.get(0).getObject().asResource().canAs(RDFList.class)
		) {
			List<RDFNode> theList = statements.get(0).getObject().asResource().as(RDFList.class).asJavaList();
			if(
					theList.size() == 2
					&&
					theList.contains(XSD.xstring)
					&&
					theList.contains(RDF.langString)
			) {
				return true;
			}
		}
		
		return false;
	}
	
	private boolean hasDatatypeString(Resource propertyShape) {
		List<Statement> statements = propertyShape.listProperties(SHACLM.datatype).toList();
		return !statements.isEmpty() && statements.get(0).getObject().isURIResource() && (
				statements.get(0).getObject().asResource().getURI().equals(XSD.xstring.getURI())
		);	
	}
	
	private boolean hasDatatypeLangString(Resource propertyShape) {
		List<Statement> statements = propertyShape.listProperties(SHACLM.datatype).toList();
		return !statements.isEmpty() && statements.get(0).getObject().isURIResource() && (
				statements.get(0).getObject().asResource().getURI().equals(RDF.langString)
		);	
	}
	
	private boolean hasNodeKindLiteral(Resource propertyShape) {
		List<Statement> statements = propertyShape.listProperties(SHACLM.nodeKind).toList();
		return !statements.isEmpty() && statements.get(0).getObject().isURIResource() && (
				statements.get(0).getObject().asResource().getURI().equals(SHACLM.Literal.getURI())
		);	
	}
	
	private boolean hasMinCountOne(Resource propertyShape) {
		List<Statement> statements = propertyShape.listProperties(SHACLM.minCount).toList();
		return !statements.isEmpty() && statements.get(0).getObject().isLiteral() && (statements.get(0).getObject().asLiteral().getInt() == 1);
	}
	
	private boolean hasMaxCountOne(Resource propertyShape) {
		List<Statement> statements = propertyShape.listProperties(SHACLM.maxCount).toList();
		return !statements.isEmpty() && statements.get(0).getObject().isLiteral() && (statements.get(0).getObject().asLiteral().getInt() == 1);
	}
	
	private boolean isUniqueLang(Resource propertyShape) {
		List<Statement> statements = propertyShape.listProperties(SHACLM.uniqueLang).toList();
		return !statements.isEmpty() && statements.get(0).getObject().isLiteral() && statements.get(0).getObject().asLiteral().getBoolean();
	}

}
