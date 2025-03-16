package fr.sparna.rdf.shacl;

public class SHP {

	public static final String NAMESPACE = "https://shacl-play.sparna.fr/ontology#";

	public static final String NAMESPACE_SHAPES = "https://shacl-play.sparna.fr/shapes#";
	
	/**
	 * Boolean flag on a SHACL shape indicating if it matched at least focus node
	 */
	public static final String TARGET_MATCHED = NAMESPACE+"targetMatched";
	
	/**
	 * Boolean flag on the validation report itself indicating if at least one shape matched a focus node
	 */
	public static final String HAS_MATCHED = NAMESPACE+"hasMatched";

	/**
	 * The property that links a SHACL shape to a focus node it is targeting
	 */
	public static final String HAS_FOCUS_NODE = NAMESPACE+"hasFocusNode";

	public static final String CLOSED_GRAPH_SHAPE = NAMESPACE_SHAPES+"ClosedGraphShape";

	public static final String CLOSED_GRAPH_CONSTRAINT_COMPONENT = NAMESPACE_SHAPES+"ClosedGraphConstraintComponent";
	
}
