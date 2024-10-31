package fr.sparna.rdf.shacl.doc;

import org.commonmark.node.Node;
import org.commonmark.parser.Parser;
import org.commonmark.renderer.html.HtmlRenderer;

public class MarkdownRenderer {
	
	private static MarkdownRenderer instance;

	private HtmlRenderer renderer;
	private Parser parser;
	
	private MarkdownRenderer() {
		// init markdown parser & renderer
		this.parser = Parser.builder().build();
		this.renderer = HtmlRenderer.builder().build();
	}

	public static MarkdownRenderer getInstance() {
		if(MarkdownRenderer.instance == null) {
			instance = new MarkdownRenderer();
		}
		return MarkdownRenderer.instance;
	}

	public String renderMarkdown(String mdString) {
		if(mdString == null) return null;
		Node document = parser.parse(mdString);					
		return renderer.render(document);	
	}
}
