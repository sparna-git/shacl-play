package fr.sparna.rdf.shacl.app.filter;

import java.io.File;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

@Parameters(commandDescription = "Filters a shape model against a statistics file.")
public class ArgumentsFilter {
	
	
	@Parameter(
			names = { "-o", "--output" },
			description = "Path where the statistics file will be written. The format of the file is determined based"
					+ " on the file extension : '*.ttl, *.rdf, *.n3, *.nq, *.nt, *.trig, *.jsonld' ",
			required = true
	)
	private File output;

	@Parameter(
			names = { "-s", "--shapes" },
			description = "Path to the shapes file against which the input data should be analyzed",
			required = true
	)
	private File shapes;
	
	@Parameter(
			names = { "-st", "--statistics" },
			description = "Path to the statistics file containing the void:triples count and void:distinctObject count for each shapes",
			required = true
	)
	private File statistics;
	
	@Parameter(
			names = { "-d", "--description" },
			description = "Copies statistics on the description of shapes. Defaults to false"
	)
	private boolean description = false;

	
	public File getOutput() {
		return output;
	}

	public void setOutput(File output) {
		this.output = output;
	}

	public File getShapes() {
		return shapes;
	}

	public void setShapes(File shapes) {
		this.shapes = shapes;
	}

	public boolean isDescription() {
		return description;
	}

	public void setDescription(boolean description) {
		this.description = description;
	}

	public File getStatistics() {
		return statistics;
	}

	public void setStatistics(File statistics) {
		this.statistics = statistics;
	}



	
	
}