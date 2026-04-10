package fr.sparna.rdf.shacl.generate.visitors;

import java.util.Random;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.shacl.vocabulary.SHACLM;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//import fr.sparna.rdf.shacl.generate.ColorOption;
import fr.sparna.rdf.shacl.generate.ShaclGenerator;

public class AssignColorVisitor implements ShaclVisitorIfc {

	public enum ColorOption {
		aliceblue("#f0f8ff"),
		antiquewhite("#faebd7"),
		aqua("#00ffff"),
		aquamarine("#7fffd4"),
		azure("#f0ffff"),
		beige("#f5f5dc"),
		bisque("#ffe4c4"),
		blanchedalmond("#ffebcd"),
		blue("#0000ff"),
		blueviolet("#8a2be2"),
		brown("#a52a2a75"),
		burlywood("#deb887"),
		cadetblue("#5f9ea0"),
		chartreuse("#7fff00"),
		chocolate("#d2691e"),
		coral("#ff7f50"),
		cornflowerblue("#6495ed"),
		cornsilk("#fff8dc"),
		crimson("#dc143c"),
		cyan("#00ffff"),
		//darkblue("#00008b"),
		darkcyan("#008b8b"),
		darkgoldenrod("#b8860b"),
		darkgray("#a9a9a9"),
		darkgreen("#006400"),
		darkgrey("#a9a9a9"),
		darkkhaki("#bdb76b"),
		darkmagenta("#8b008b"),
		darkolivegreen("#556b2f"),
		darkorange("#ff8c00"),
		darkorchid("#9932cc"),
		darkred("#8b0000"),
		darksalmon("#e9967a"),
		darkseagreen("#8fbc8f"),
		darkslateblue("#483d8b"),
		//darkslategray("#2f4f4f"),
		//darkslategrey("#2f4f4f"),
		darkturquoise("#00ced1"),
		darkviolet("#9400d3"),
		deeppink("#ff1493"),
		deepskyblue("#00bfff"),
		dimgray("#696969"),
		//dimgrey("#696969"),
		dodgerblue("#1e90ff"),
		firebrick("#b22222"),
		floralwhite("#fffaf0"),
		forestgreen("#228b22"),
		fuchsia("#ff00ff"),
		gainsboro("#dcdcdc"),
		ghostwhite("#f8f8ff"),
		gold("#ffd700"),
		goldenrod("#daa520"),
		gray("#808080"),
		green("#008000"),
		greenyellow("#adff2f"),
		grey("#808080"),
		//honeydew("#f0fff0"),
		hotpink("#ff69b4"),
		indianred("#cd5c5c"),
		//indigo("#4b0082"),
		ivory("#fffff0"),
		khaki("#f0e68c"),
		lavender("#e6e6fa"),
		lavenderblush("#fff0f5"),
		lawngreen("#7cfc00"),
		lemonchiffon("#fffacd"),
		lightblue("#add8e6"),
		lightcoral("#f08080"),
		lightcyan("#e0ffff"),
		lightgoldenrodyellow("#fafad2"),
		lightgray("#d3d3d3"),
		lightgreen("#90ee90"),
		lightgrey("#d3d3d3"),
		lightpink("#ffb6c1"),
		lightsalmon("#ffa07a"),
		lightseagreen("#20b2aa"),
		lightskyblue("#87cefa"),
		lightslategray("#778899"),
		lightslategrey("#778899"),
		lightsteelblue("#b0c4de"),
		lightyellow("#ffffe0"),
		lime("#00ff00"),
		limegreen("#32cd32"),
		linen("#faf0e6"),
		magenta("#ff00ff"),
		maroon("#800000"),
		mediumaquamarine("#66cdaa"),
		mediumblue("#0000cd"),
		mediumorchid("#ba55d3"),
		//mediumpurple("#9370db"),
		mediumseagreen("#3cb371"),
		mediumslateblue("#7b68ee"),
		mediumspringgreen("#00fa9a"),
		mediumturquoise("#48d1cc"),
		mediumvioletred("#c71585"),
		//midnightblue("#191970"),
		mintcream("#f5fffa"),
		mistyrose("#ffe4e1"),
		moccasin("#ffe4b5"),
		navajowhite("#ffdead"),
		//navy("#000080"),
		oldlace("#fdf5e6"),
		olive("#808000"),
		olivedrab("#6b8e23"),
		orange("#ffa500"),
		orangered("#ff4500"),
		orchid("#da70d6"),
		palegoldenrod("#eee8aa"),
		palegreen("#98fb98"),
		paleturquoise("#afeeee"),
		palevioletred("#db7093"),
		papayawhip("#ffefd5"),
		peachpuff("#ffdab9"),
		peru("#cd853f"),
		pink("#ffc0cb"),	
		plum("#dda0dd"),
		powderblue("#b0e0e6"),
		//purple("#800080"),
		//rebeccapurple("#663399"),
		red("#ff0000"),
		rosybrown("#bc8f8f"),
		royalblue("#4169e1"),
		saddlebrown("#8b4513"),
		salmon("#fa8072"),
		sandybrown("#f4a460"),
		seagreen("#2e8b57"),
		seashell("#fff5ee"),
		sienna("#a0522d"),
		silver("#c0c0c0"),
		skyblue("#87ceeb"),
		slateblue("#6a5acd"),
		slategray("#708090"),
		slategrey("#708090"),
		snow("#fffafa"),
		springgreen("#00ff7f"),
		steelblue("#4682b4"),
		tan("#d2b48c"),
		teal("#008080"),
		thistle("#d8bfd8"),
		tomato("#ff6347"),
		turquoise("#40e0d0"),
		violet("#ee82ee"),
		wheat("#f5deb3"),
		whitesmoke("#f5f5f5"),
		yellow("#ffff00"),
		yellowgreen("#9acd32");

		private final String hexColor;
		private static final ColorOption[] VALUES = values();
		private static final Random RANDOM = new Random();

		ColorOption(String hexColor) {
			this.hexColor = hexColor;
		}

		public String getHexColor() {
			return hexColor;
		}

		public String getNameColor() {
			return name();
		}

		@Override
		public String toString() {
			return hexColor;
		}

		public static ColorOption random() {
			return VALUES[RANDOM.nextInt(VALUES.length)];
		}
	}

    private static final Logger log = LoggerFactory.getLogger(ShaclGenerator.class);

    private String colorAnnotation = "https://shacl-play.sparna.fr/ontology#background-color";
	private Model model;

    public AssignColorVisitor() {
	}
    
    @Override
	public void visitModel(Model model) {
		this.model = model;
			
		// add volipi namespace
		model.setNsPrefix("shacl-play", "https://shacl-play.sparna.fr/ontology#");
	}

    @Override
	public void visitOntology(Resource ontology) {

	}

    @Override
	public void visitNodeShape(Resource aNodeShape) {
		// read target class
		// use a toList to avoid ConcurrentModificationException
		aNodeShape.listProperties(SHACLM.targetClass).toList().stream().forEach(s -> {
            String colorNodeShape = assignColor();
            log.debug("Assigned color to NodeShape "+aNodeShape.getURI()+" : '"+colorNodeShape+"'");
			aNodeShape.addProperty(aNodeShape.getModel().createProperty(colorAnnotation), colorNodeShape);			
		});
	}


    @Override
	public void visitPropertyShape(Resource aPropertyShape, Resource aNodeShape) {

	}

	@Override
	public void leaveModel(Model model) {
		
	}

    private String assignColor () {
        return ColorOption.random().getNameColor();
    }

}
