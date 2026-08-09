package fr.sparna.rdf.shacl.app.owl2shacl;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.beust.jcommander.Parameters;

import fr.sparna.rdf.shacl.owl2shacl.Owl2Shacl.Owl2ShaclStyle;

@Parameters(commandDescription = "Converts an OWL ontology into a SHACL file, in a specified 'conversion style'. See https://github.com/sparna-git/owl2shacl for more details.")
public class ArgumentsOwl2Shacl {
	
	@Parameter(
			names = { "-i", "--input" },
			description = "Path to a local RDF file or directory. This can be repeated to read multiple input files.",
			required = true,
			variableArity = true
	)
	private List<File> input;

	@Parameter(
			names = { "-o", "--output" },
			description = "Path to the output file",
			required = true
	)
	private File output;
	
	@Parameter(
			names = { "-s", "--style" },
			description = "Style of conversion. Values can be 'OPEN', 'SEMICLOSED' or 'CLOSED'. Defaults to 'OPEN'",
			required = false
	)
	private Owl2ShaclStyle style = Owl2ShaclStyle.OPEN;

	@Parameter(
			names = { "-r", "--rules" },
			description = "Path to a local file, or a URL, holding the conversion rules to apply."
					+ " Overrides --style. The predefined styles read their rules from the main"
					+ " branch of the owl2shacl repository over the network, so a conversion is"
					+ " neither reproducible nor possible offline; supply the rules explicitly to"
					+ " pin a known version, work without network access, or try rule changes"
					+ " before they are published.",
			required = false
	)
	private String rules;

	public String getRules() {
		return rules;
	}

	public void setRules(String rules) {
		this.rules = rules;
	}

	/**
	 * The rules location to convert with: the explicit --rules value if given, otherwise the
	 * URL of the selected style.
	 *
	 * @throws ParameterException if --rules is neither a readable file nor a valid URL
	 */
	public URL resolveRulesUrl() {
		if (rules == null) {
			return style.getRulesUrl();
		}
		File asFile = new File(rules);
		if (asFile.exists()) {
			try {
				return asFile.toURI().toURL();
			} catch (MalformedURLException e) {
				throw new ParameterException("--rules points to a file that cannot be addressed as a URL: " + rules, e);
			}
		}
		try {
			return new URL(rules);
		} catch (MalformedURLException e) {
			// The value is neither an existing file nor a URL. Saying which was expected is
			// more useful than a MalformedURLException naming a missing protocol.
			throw new ParameterException(
					"--rules must be the path of a readable file or a valid URL, but was: " + rules, e);
		}
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

	public Owl2ShaclStyle getStyle() {
		return style;
	}

	public void setStyle(Owl2ShaclStyle style) {
		this.style = style;
	}
	
}
