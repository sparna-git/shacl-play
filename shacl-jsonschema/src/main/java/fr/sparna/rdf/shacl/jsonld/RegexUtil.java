package fr.sparna.rdf.shacl.jsonld;

import com.github.curiousoddman.rgxgen.RgxGen;
import com.github.curiousoddman.rgxgen.config.RgxGenOption;
import com.github.curiousoddman.rgxgen.config.RgxGenProperties;
import com.github.curiousoddman.rgxgen.model.RgxGenCharsDefinition;

public class RegexUtil {

	public static String extractHttpBaseUriFromPattern(String pattern) {

        if(!pattern.contains("http")) {
            return null;
        }

		// remove starting ^ if any
		if(pattern.startsWith("^")) {
			pattern = pattern.substring(1);
		}

        String subPattern = null;
		// check if the pattern ends with .* or (.*) with an optional $ at the end
		if(pattern.endsWith(".*")) {
			subPattern = pattern.substring(0, pattern.length()-2);
		} else if(pattern.endsWith("(.*)")) {
			subPattern = pattern.substring(0, pattern.length()-4);
		} else if(pattern.endsWith(".*$")) {
			subPattern = pattern.substring(0, pattern.length()-3);
		} else if(pattern.endsWith("(.*)$")) {
			subPattern = pattern.substring(0, pattern.length()-5);
		}

        // looks like a regex with more complex pattern, give up
        if(subPattern.contains("*") || subPattern.contains("+") || subPattern.contains("?") || subPattern.contains("[") || subPattern.contains("(")) {
            return null;
        }

		return subPattern;
	}

    public static String generateMatchingString(String regex) {
        // EP uses named capturing group, with syntax (?<xxxx> ...)
        // e.g. "^https://data.europarl.europa.eu/eli/dl/doc/PV-(?<parliamentaryTerm>[0-9]{1,2})-(?<date>(19[5-9][0-9]|20[0-9][0-9])-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01]))$"
        // but this is not supported by RgxGen, so we need to remove the named capturing groups
        regex = regex.replaceAll("\\(\\?<[^>]+>\\s*", "("); // Remove named capturing groups

        RgxGenProperties properties = new RgxGenProperties();
        // when matching a dot, always use the special \u0000 character for replacement
        // this is because EP uses regexes like "^https://data.europarl.europa.eu/eli/dl/doc/[A-Za-z0-9\-_]+/[a-z][a-z]$"
        // that directly contains a dot
        RgxGenOption.DOT_MATCHES_ONLY.setInProperties(properties, RgxGenCharsDefinition.of("\u0000"));
        // when generating any number of values, generate 3 characters
        RgxGenOption.INFINITE_PATTERN_REPETITION.setInProperties(properties, 3);

        RgxGen rgxGen = RgxGen.parse(properties, regex);
        String output = rgxGen.generate();

        // replace the value inserted for dots back with a dot
        return output.replaceAll("\u0000", ".").replaceAll(" ", "_");
    }
}
