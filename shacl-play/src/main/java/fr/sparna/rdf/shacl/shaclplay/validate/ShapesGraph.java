package fr.sparna.rdf.shacl.shaclplay.validate;

import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDFS;
import org.topbraid.jenax.util.JenaUtil;
import org.topbraid.shacl.engine.Shape;
import org.topbraid.shacl.model.SHFactory;
import org.topbraid.shacl.model.SHNodeShape;
import org.topbraid.shacl.model.SHShape;
import org.topbraid.shacl.vocabulary.SH;

public class ShapesGraph {

	protected Model shapesModel;
	
	private List<SHNodeShape> rootNodeShapes;

	public ShapesGraph(Model shapesModel) {
		super();
		this.shapesModel = shapesModel;
	}
	
	
	/**
	 * Gets all non-deactivated shapes from the graph
	 * @return the root shapes
	 */
	public synchronized List<SHNodeShape> getRootNodeShapes() {
		if(rootNodeShapes == null) {
			
			// Collect all shapes, as identified by target and/or type
			Set<Resource> candidates = new HashSet<>();
			candidates.addAll(shapesModel.listSubjectsWithProperty(SH.target).toList());
			candidates.addAll(shapesModel.listSubjectsWithProperty(SH.targetClass).toList());
			candidates.addAll(shapesModel.listSubjectsWithProperty(SH.targetNode).toList());
			candidates.addAll(shapesModel.listSubjectsWithProperty(SH.targetObjectsOf).toList());
			candidates.addAll(shapesModel.listSubjectsWithProperty(SH.targetSubjectsOf).toList());
			for(Resource shape : JenaUtil.getAllInstances(shapesModel.getResource(SH.NodeShape.getURI()))) {
				if(JenaUtil.hasIndirectType(shape, RDFS.Class)) {
					candidates.add(shape);
				}
			}
			for(Resource shape : JenaUtil.getAllInstances(shapesModel.getResource(SH.PropertyShape.getURI()))) {
				if(JenaUtil.hasIndirectType(shape, RDFS.Class)) {
					candidates.add(shape);
				}
			}

			// Turn the shape Resource objects into Shape instances
			this.rootNodeShapes = new LinkedList<SHNodeShape>();
			for(Resource candidate : candidates) {
				SHNodeShape shape = SHFactory.asNodeShape(candidate);
				if(!shape.isDeactivated()) {
					this.rootNodeShapes.add(shape);
				}
			}
		}
		
		return rootNodeShapes;
	}


	public Model getShapesModel() {
		return shapesModel;
	}
	
}
