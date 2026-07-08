package fr.sparna.rdf.vocabularies;

public class SHACL_PLAY {

	public static String NAMESPACE = "https://shacl-play.sparna.fr/ontology#";
	
	public static String VALUE_PARTITION = NAMESPACE + "valuePartition";
	
	public static String VALUE = NAMESPACE + "value";
	
	public static String COLOR = NAMESPACE + "color";
	
	public static String BACKGROUNDCOLOR = NAMESPACE + "background-color";
	
	public static String PACKAGE = NAMESPACE + "package";
	
	public static String EMBED = NAMESPACE + "embed";

	public static String EMBED_NEVER = NAMESPACE + "EmbedNever";

	public static String SHORTNAME = NAMESPACE + "shortname";

	public static String MAIN = NAMESPACE + "main";

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
	
}
