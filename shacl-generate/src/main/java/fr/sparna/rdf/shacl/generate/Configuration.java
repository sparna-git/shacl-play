package fr.sparna.rdf.shacl.generate;

/**
 * Configuration parameters for SHACL generation
 * @author thomas
 *
 */
public class Configuration {

	private String shapesNamespace;
	private String shapesNamespacePrefix;

	private String shapesOntology;

	private String lang = "en";

	private ModelProcessorIfc modelProcessor;

	public Configuration(ModelProcessorIfc modelProcessor) {
		super();
		this.modelProcessor = modelProcessor;
	}

	public Configuration(ModelProcessorIfc modelProcessor, String shapesOntology, String shapesNamespacePrefix) {
		this(modelProcessor);
		this.shapesNamespace = shapesOntology;
		this.shapesNamespacePrefix = shapesNamespacePrefix;
	}
	
	public ModelProcessorIfc getModelProcessor() {
		return modelProcessor;
	}

	public void setModelProcessor(ModelProcessorIfc modelProcessor) {
		this.modelProcessor = modelProcessor;
	}

	public String getShapesNamespace() {
		return shapesNamespace;
	}

	public void setShapesNamespace(String shapesNamespace) {
		this.shapesNamespace = shapesNamespace;
	}

	public String getShapesNamespacePrefix() {
		return shapesNamespacePrefix;
	}

	public void setShapesNamespacePrefix(String shapesNamespacePrefix) {
		this.shapesNamespacePrefix = shapesNamespacePrefix;
	}

	public String getLang() {
		return this.lang;
	}

	public void setLang(String lang) {
		this.lang = lang;
	}

	public String getShapesOntology() {
		return shapesOntology;
	}

	public void setShapesOntology(String shapesOntology) {
		this.shapesOntology = shapesOntology;
	}	
	
}
