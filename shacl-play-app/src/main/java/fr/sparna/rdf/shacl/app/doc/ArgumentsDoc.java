package fr.sparna.rdf.shacl.app.doc;

import java.awt.Image;
import java.io.File;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

import fr.sparna.cli.SpaceSplitter;

@Parameters(commandDescription = "Validates some input RDF data against the provided SHACL file, and writes the output in one or more output file. The format "
		+ "of the output file(s) is determined based on the file extension.")
public class ArgumentsDoc {

	@Parameter(
			names = { "-i", "--input" },
			description = "Path to a input Shapes file, or directory containing multiple files.",
			required = true,
			variableArity = true
	)
	private List<File> input;
	
	@Parameter(
			names = { "-w", "--owl" },
			description = "Path to a input OWL ontology file, or directory containing multiple files.",
			variableArity = true
	)
	private List<File> ontologies;

	@Parameter(
			names = { "-o", "--output" },
			description = "Path to an output file, with extension *.html or *.pdf",
			required = true
	)
	private File output;
	
	@Parameter(
			names = { "-l", "--language" },
			description = "Language code to generate documentation in, e.g. 'en', 'fr', etc.",
			required = true
	)
	private String language;
	
	@Parameter(
			names = { "-d", "--diagram" },
			description = "Include diagram in the generated documentation.",
			required = false
	)
	private Boolean diagramShacl=false;
	
	@Parameter(
			names = { "-h", "--hide" },
			description = "hide datatype properties.",
			required = false
	)
	private Boolean hidePropertiesShacl=false;
	
	
	@Parameter(
			names = { "-m", "--img" },
			description = "Upload your logo on the document, local or through a link.",
			required = false
	)
	private String imgLogo;
	
	
	@Parameter(
			names = { "-p", "--pdf" },
			description = "print a pdf file.",
			required = false
	)
	private Boolean pdf=false;
	
	
	public Boolean getDiagramShacl() {
		return diagramShacl;
	}

	public void setDiagramShacl(Boolean diagramShacl) {
		this.diagramShacl = diagramShacl;
	}

	public Boolean getPdf() {
		return pdf;
	}

	public void setPdf(Boolean pdf) {
		this.pdf = pdf;
	}

	public String getImgLogo() {
		return imgLogo;
	}

	public void setImgLogo(String imgLogo) {
		this.imgLogo = imgLogo;
	}

	public List<File> getInput() {
		return input;
	}

	public void setInput(List<File> input) {
		this.input = input;
	}

	public File getOutput() {
		return output;
	}

	public void setOutput(File output) {
		this.output = output;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}

	public List<File> getOntologies() {
		return ontologies;
	}

	public void setOntologies(List<File> ontologies) {
		this.ontologies = ontologies;
	}

	public Boolean getHidePropertiesShacl() {
		return hidePropertiesShacl;
	}

	public void setHidePropertiesShacl(Boolean hidePropertiesShacl) {
		this.hidePropertiesShacl = hidePropertiesShacl;
	}
}
