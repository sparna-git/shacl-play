package fr.sparna.rdf.shacl.shaclplay.validate;

import org.apache.jena.rdf.model.Literal;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.RDFList;
import org.apache.jena.rdf.model.RDFNode;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.rdf.model.Statement;
import org.apache.jena.rdf.model.StmtIterator;
import org.apache.jena.util.iterator.ExtendedIterator;
import org.apache.jena.vocabulary.RDFS;
import org.owasp.encoder.Encode;

/**
 * Utility object for rendering parts of the data model in HTML.
 * 
 * 
 * We cannot use method overloading here (methods with the same name but different signatures), this is not supported in JSP EL.
 * see http://stackoverflow.com/questions/9763619/does-el-support-overloaded-methods
 * @author thomas
 *
 */
public class HTMLRenderer {

	private Model model;
	private String language;
	/**
	 * Indicates if we are displaying validation results or just displaying a shape file without validation
	 */
	private boolean displayValidationResults = true;
	
	public HTMLRenderer(Model model, String language) {
		super();
		this.model = model;
		this.language = language;
	}


	public String parseLanguageProperty(Resource r, Property p) {
		if(this.language != null) {			
			StmtIterator i = r.listProperties(p);
			while(i.hasNext()) {
				Statement s = i.next();
				if(this.language.equals(s.getObject().asLiteral().getLanguage())) {
					return s.getObject().asLiteral().getLexicalForm();
				}
			}
			// not found, return the same as without language
		}
		
		if(r.hasProperty(p)) {
			return r.getProperty(p).getObject().asLiteral().getLexicalForm();
		} else {
			return null;
		}
	}
	
	public String printHeaderLine(int numberOfViolations, int numberOfWarnings, int numberOfInfos) {
		StringBuffer buffer = new StringBuffer();
		if(numberOfViolations > 0) {
			buffer.append(numberOfViolations+" Violation"+((numberOfViolations > 1)?"s":""));
			if(numberOfWarnings > 0 || numberOfInfos > 0) {
				buffer.append(", ");
			}
		}
		if(numberOfWarnings > 0) {
			buffer.append(numberOfWarnings+" Warning"+((numberOfWarnings > 1)?"s":""));
			if( numberOfInfos > 0) {
				buffer.append(", ");
			}
		}
		if(numberOfInfos > 0) {
			buffer.append(numberOfInfos+" Info"+((numberOfInfos > 1)?"s":""));
		}
		
		if(numberOfViolations == 0 && numberOfWarnings == 0 && numberOfInfos == 0) {
			buffer.append("All green ! (0 violation, 0 warning, 0 info)");
		}
		
		return buffer.toString();				
	}
	
	public String render(Object o) {
		if(o == null) {
			return "null";
		}
		
		if(o instanceof RDFNode) {
			return renderRDFNode((RDFNode)o);
		} else {
			throw new RuntimeException("Unsupported Conversion: " + o.getClass());
		}
			
//		if(o instanceof Target) {
//			return renderTarget((Target)o);
//		} else if(o instanceof RDFNode) {
//			return renderRDFNode((RDFNode)o);
//		} else if(o instanceof Path) {
//			return renderPathAsPlainText((Path)o);
//		} else {
//			throw new RuntimeException("Unsupported Conversion: " + o.getClass());
//		}
	}
	
//	public String renderTarget(Target target) {
//		StringBuffer buffer = new StringBuffer();
//		buffer.append("<span class=\"target\">");
//		buffer.append(render(target.getTargetType())+" : "+render(target.getTargetValue()));
//		if(target.isImplicit()) {
//			buffer.append(" <em>(implicit)</em>");
//		}
//		buffer.append("</span>");
//		return buffer.toString();
//	}
//	
//	public String renderComponent(Component c) {
//		return renderRDFNode(c.getProperty())+" : "+renderRDFNode(c.getValue());
//	}
	
	
	/**
	 * Reads the dispay label of a resource
	 * @param r
	 * @return
	 */
	public String renderRDFNode(RDFNode r) {
		
		if(r.isLiteral()) {
			Literal l = r.asLiteral();
			if(l.getDatatype() != null) {
				return "\""+Encode.forHtml(l.getLexicalForm())+"\""+"<sup>^^"+this.render(this.model.createResource(l.getDatatype().getURI()))+"</sup>";
			} else if (l.getLanguage() != null) {
				return "\""+Encode.forHtml(l.getLexicalForm())+"\""+"<sup>@"+l.getLanguage()+"</sup>";
			} else {
				return "\""+Encode.forHtml(l.getLexicalForm())+"\"";
			}			
		} else {
			Resource res = r.asResource();
			String rdfsLabel = parseLanguageProperty(res, RDFS.label);
			if(rdfsLabel == null) {
				
				if(r.canAs(RDFList.class)) {
					ExtendedIterator<RDFNode> items = res.as(RDFList.class).iterator();
					StringBuffer buffer = new StringBuffer();
		            while ( items.hasNext() ) {
		                Resource item = items.next().asResource();
		                buffer.append(renderRDFNode(item)+ ", ");
		            }
		            if(buffer.length() > 2) {
		            	buffer.deleteCharAt(buffer.length()-1);
		            	buffer.deleteCharAt(buffer.length()-1);
		            }
		            return buffer.toString();
				} else if(r.isAnon()) {
					StringBuffer buffer = new StringBuffer();
					buffer.append("[");
					StmtIterator si = r.asResource().listProperties();
					while ( si.hasNext() ) {
		                Statement s = si.next();
		                buffer.append(renderRDFNode(s.getPredicate())+" "+renderRDFNode(s.getObject())+" ;");
		            }
					if(buffer.length() > 2) {
		            	buffer.deleteCharAt(buffer.length()-1);
		            	buffer.deleteCharAt(buffer.length()-1);
		            }
					buffer.append("]");
					return buffer.toString();
				} else {
					if(res.getURI() == null) {
						return r.toString();
					}
					
					String shortForm = this.model.shortForm(res.getURI());
					if(shortForm.equals(res.getURI())) {
						return "&lt;"+shortForm+"&gt;";
					} else {
						return shortForm;
					}
				}
			} else {
				return rdfsLabel;
			}
		}
	}
	
//	public String renderPathAsPlainText(Path p) {
//		StringBuffer buffer = new StringBuffer();
//		switch(p.getPathConstruct()) {
//		case ALTERNATIVE_PATH:
//			for (Path aSubpath : p.getSubPaths()) {
//				buffer.append(renderPathAsPlainText(aSubpath));
//				buffer.append(" or ");
//			}
//			buffer.delete(buffer.length()-4, buffer.length());
//			break;
//		case INVERSE_PATH:
//			buffer.append("inverse of ");
//			buffer.append(renderRDFNode(p.getPredicate()));
//			break;
//		case ONEORMORE_PATH:
//			buffer.append("one or more ");
//			buffer.append(renderRDFNode(p.getPredicate()));
//			break;
//		case PREDICATE_PATH:
//			buffer.append(renderRDFNode(p.getPredicate()));
//			break;
//		case SEQUENCE_PATH:
//			for (Path aSubpath : p.getSubPaths()) {
//				buffer.append(renderPathAsPlainText(aSubpath));
//				buffer.append(" then ");
//			}
//			buffer.delete(buffer.length()-4, buffer.length());
//			break;
//		case ZEROORMORE_PATH:
//			buffer.append("zero or more ");
//			buffer.append(renderRDFNode(p.getPredicate()));
//			break;
//		case ZEROORONE_PATH:
//			buffer.append("zero or one ");
//			buffer.append(renderRDFNode(p.getPredicate()));
//			break;
//		default:
//			break;
//			
//		}
//		
//		return buffer.toString();
//	}


	public boolean isDisplayValidationResults() {
		return displayValidationResults;
	}

	public void setDisplayValidationResults(boolean displayValidationResults) {
		this.displayValidationResults = displayValidationResults;
	}	
	
}
