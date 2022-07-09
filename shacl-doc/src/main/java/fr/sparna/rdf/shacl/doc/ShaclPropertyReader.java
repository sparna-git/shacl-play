package fr.sparna.rdf.shacl.doc;

import java.util.List;
import java.util.stream.Collectors;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.topbraid.shacl.vocabulary.SH;

public class ShaclPropertyReader {
	
	protected String lang;
	protected List<ShaclBox> allBoxes;

	public ShaclPropertyReader(String lang, List<ShaclBox> allBoxes) {
		super();
		this.lang = lang;
		this.allBoxes = allBoxes;
	}

	public ShaclProperty read(Resource constraint) {

		ShaclProperty shaclProperty = new ShaclProperty(constraint);

		shaclProperty.setShPath(this.readShPath(constraint));
		shaclProperty.setShDatatype(this.readShDatatype(constraint));
		shaclProperty.setShNodeKind(this.readShNodeKind(constraint));
		shaclProperty.setShMinCount(this.readShMinCount(constraint));
		shaclProperty.setShMaxCount(this.readShMaxCount(constraint));
		shaclProperty.setShNode(this.readNode(constraint));
		shaclProperty.setShPattern(this.readShPattern(constraint));
		shaclProperty.setShClass(this.readShClass(constraint));
		shaclProperty.setShDescription(this.readDescription(constraint));
		shaclProperty.setShName(this.readName(constraint));
		shaclProperty.setShIn(this.readShin(constraint));
		shaclProperty.setShValue(this.readShValue(constraint));
		shaclProperty.setShOrder(this.readShOrder(constraint));
		shaclProperty.setShOr(this.readShOr(constraint));
		
		return shaclProperty;
	}
	
	
	
	public List<Resource> readShOr(Resource constraint) {
		if (constraint.hasProperty(SH.or)) {			
			Resource list = constraint.getProperty(SH.or).getList().asResource();
			List<RDFNode> rdflist = list.as(RDFList.class).asJavaList();

			// read only the sh:node on list items
			return rdflist.stream().map(item -> {
				if(item.isResource()) {
					if(item.asResource().hasProperty(SH.node)) {
						return item.asResource().getPropertyResourceValue(SH.node);
					} else if(item.asResource().hasProperty(SH.class_)) {
						return item.asResource().getPropertyResourceValue(SH.class_);
					} else if(item.asResource().hasProperty(SH.datatype)) {
						return item.asResource().getPropertyResourceValue(SH.datatype);
					} else {
						return null;
					}
				} else {
					return null;
				}
			}).collect(Collectors.toList());
		} else {
			return null;
		}
	}

	public RDFNode readShValue(Resource constraint) {
		if (constraint.hasProperty(SH.hasValue)) {
			return constraint.getProperty(SH.hasValue).getObject();
		} else {
			return null;
		}
	}

	public Integer readShOrder(Resource constraint) {
		Integer value = null;
		if (constraint.hasProperty(SH.order)) {
			value = Integer.parseInt(constraint.getProperty(SH.order).getLiteral().getString());
		}
		return value;
	}

	public List<RDFNode> readShin(Resource constraint) {
		if (constraint.hasProperty(SH.in)) {
			Resource list = constraint.getProperty(SH.in).getList().asResource();
			return list.as(RDFList.class).asJavaList();
		} else {
			return null;
		}
	}

	public List<Literal> readName(Resource constraint) {
		return ConstraintValueReader.readLiteralInLang(constraint, SH.name, this.lang);
	}

	public List<Literal> readDescription(Resource constraint) {
		return ConstraintValueReader.readLiteralInLang(constraint, SH.description, this.lang);
	}

	public Resource readShPath(Resource constraint) {
		if(constraint.hasProperty(SH.path)) {
			return constraint.getPropertyResourceValue(SH.path);
		} else {
			return null;
		}
	}

	public Resource readShDatatype(Resource constraint) {
		if(constraint.hasProperty(SH.datatype)) {
			return constraint.getPropertyResourceValue(SH.datatype);
		} else {
			return null;
		}
	}

	public Resource readShNodeKind(Resource constraint) {
		if(constraint.hasProperty(SH.nodeKind)) {
			return constraint.getPropertyResourceValue(SH.nodeKind);
		} else {
			return null;
		}
	}

	
	public Integer readShMinCount(Resource constraint) {
		if (constraint.hasProperty(SH.minCount)) {
			return Integer.parseInt(constraint.getProperty(SH.minCount).getLiteral().getString());
		} else {
			return null;
		}
	}
	
	public Integer readShMaxCount(Resource constraint) {
		if (constraint.hasProperty(SH.maxCount)) {
			return Integer.parseInt(constraint.getProperty(SH.maxCount).getLiteral().getString());
		} else {
			return null;
		}
	}

	public Literal readShPattern(Resource constraint) {
		if (constraint.hasProperty(SH.pattern)) {
			return constraint.getProperty(SH.pattern).getLiteral();
		} else {
			return null;
		}
	}

	// TODO : devrait retourner un ShaclBox
	public Resource readNode(Resource constraint) {
		if (constraint.hasProperty(SH.node)) {
			return constraint.getPropertyResourceValue(SH.node);
		} else {
			return null;
		}
	}

	public Resource readShClass(Resource constraint) {
		if (constraint.hasProperty(SH.class_)) {
			return constraint.getPropertyResourceValue(SH.class_);
		} else {
			return null;
		}
	}

}
