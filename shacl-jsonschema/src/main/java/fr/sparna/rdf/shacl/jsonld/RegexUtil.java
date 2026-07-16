package fr.sparna.rdf.shacl.jsonld;

import java.util.List;

import com.github.curiousoddman.rgxgen.RgxGen;
import com.github.curiousoddman.rgxgen.config.RgxGenOption;
import com.github.curiousoddman.rgxgen.config.RgxGenProperties;
import com.github.curiousoddman.rgxgen.model.RgxGenCharsDefinition;

public class RegexUtil {

    public static String cleanLastVariablePartFromRegex(String pattern) {
        // remove trailing $ if any
        if(pattern.endsWith("$")) {
            pattern = pattern.substring(0, pattern.length()-1);
        }

        String subPattern = null;
		// check if the pattern ends with .* or (.*) or .+ or (.+) with an optional $ at the end
		if(pattern.endsWith(".*") || pattern.endsWith(".+")) {
			subPattern = pattern.substring(0, pattern.length()-2);
		} else if(pattern.endsWith("(.*)") || pattern.endsWith("(.+)")) {
			subPattern = pattern.substring(0, pattern.length()-4);
		} 
        
        // if suPattern ends with [...]+ or [...]* or [...]{x,y}, remove that part too
        else if(pattern.matches(".*\\[.+\\][+*](\\$)?$")) {
            int lastIndex = pattern.lastIndexOf('[');
            subPattern = pattern.substring(0, lastIndex);
        // if suPattern ends with [...]{x,y} or [...]{x}, remove that part too
        } else if(pattern.matches(".*\\[.+\\]\\{\\d+(,\\d+)?}(\\$)?$")) {
            int lastIndex = pattern.lastIndexOf('[');
            subPattern = pattern.substring(0, lastIndex);
        }
        // matches https://data.europarl.europa.eu/org/ep-(?<parliamentaryTerm>[0-9]{1,2})
        else if(pattern.matches(".*\\(\\?<[^>]+>.+\\)")) {
            int lastIndex = pattern.lastIndexOf('(');
            subPattern = pattern.substring(0, lastIndex);
        }

        if(subPattern != null) {
            if(!subPattern.equals(pattern)) {
                // looks like a regex with more complex pattern, try to recurse once more
                if(subPattern.contains("*") || subPattern.contains("+") || subPattern.contains("?") || subPattern.contains("[") || subPattern.contains("(")) {
                    // remove trailing / or #
                    if(subPattern.endsWith("/") || subPattern.endsWith("#")) {
                        subPattern = subPattern.substring(0, subPattern.length()-1);
                    }
                    // and then recurse
                    return cleanLastVariablePartFromRegex(subPattern);
                } else {
                    // no hint that this is still a regex, return result
                    return subPattern;
                }
            } else {
                // cannot clean more, return
                return subPattern;
            }
        } else {
            return null;
        }


    }

	public static String extractHttpBaseUriFromPattern(String pattern) {
        if(!pattern.contains("http")) {
            return null;
        }

		// remove starting ^ if any
		if(pattern.startsWith("^")) {
			pattern = pattern.substring(1);
		}

        // and clean all trailing variable parts, recursively (e.g. "eli/dl/doc/[A-Za-z0-9\\-_]+/[a-z]{2,3}$")
        return cleanLastVariablePartFromRegex(pattern);
	}

    public static String generateMatchingString(String regex) {
        // EP uses named capturing group, with syntax (?<xxxx> ...)
        // e.g. "^https://data.europarl.europa.eu/eli/dl/doc/PV-(?<parliamentaryTerm>[0-9]{1,2})-(?<date>(19[5-9][0-9]|20[0-9][0-9])-(0[1-9]|1[0-2])-(0[1-9]|[12][0-9]|3[01]))$"
        // but this is not supported by RgxGen, so we need to remove the named capturing groups
        regex = regex.replaceAll("\\(\\?<[^>]+>\\s*", "("); // Remove named capturing groups

        // to ensure that at least one character is generated for each wildcard, we replace .* with .+ in the regex
        regex = regex.replaceAll("\\.\\*", "\\.\\+");

        RgxGenProperties properties = new RgxGenProperties();
        // when matching a dot, always use the special \u0000 character for replacement
        // this is because EP uses regexes like "^https://data.europarl.europa.eu/eli/dl/doc/[A-Za-z0-9\-_]+/[a-z][a-z]$"
        // that directly contains a dot
        RgxGenOption.DOT_MATCHES_ONLY.setInProperties(properties, RgxGenCharsDefinition.of("\u0000"));
        // when generating any number of values, generate 3 characters max (can be zero)
        RgxGenOption.INFINITE_PATTERN_REPETITION.setInProperties(properties, 3);

        RgxGen rgxGen = RgxGen.parse(properties, regex);
        String generatedValue = rgxGen.generate();

        // replace the value inserted for dots back with a dot
        String output = generatedValue.replaceAll("\u0000", ".").replaceAll(" ", "_");

        System.out.println("Generated value : " + output);
        return output;
    }

    public static String findCommonBaseUri(List<String> uris) {
        if(uris == null || uris.size() == 0) {
            return null;
        }

        String commonBase = uris.get(0);
        for(int i=1; i<uris.size(); i++) {
            String uri = uris.get(i);
            int minLength = Math.min(commonBase.length(), uri.length());
            int j=0;
            for(; j<minLength; j++) {
                if(commonBase.charAt(j) != uri.charAt(j)) {
                    break;
                }
            }
            commonBase = commonBase.substring(0, j);
            // if at any point the common base is empty, return null
            if(commonBase.isEmpty()) {
                return null;
            }
        }

        // check that the common base is at least "http://" - otherwise, it's not a valid base URI
        if(commonBase.length() > "http://".length()) {
            return commonBase;
        } else {
            return null;
        }
    }

    public static void main(String[] args) throws Exception {

        final String TEST_1 = "^https://www.iana.org/assignments/media-types/application/.*$";
        final String TEST_2 = "^http://publications.europa.eu/resource/authority/file-type/.*$";
        final String TEST_3 = "^^https://data.europarl.europa.eu/eli/dl/doc/[A-Za-z0-9\\-_]+/[a-z]{2,3}/.*$";

        /*
        String TEST_CONTEXT = "{\"p\": { \"@id\": \"https://data.europarl.europa.eu/p\", \"@context\": { \"@base\" : \"https://www.iana.org/assignments/media-types/application/\", \"@vocab\" : \"https://www.iana.org/assignments/media-types/application/\" } }, \"@base\": \"https://data.europarl.europa.eu/\"}";
        JsonValue baseContext = Json.createReader(new StringReader(TEST_CONTEXT)).readValue();
        ProbingJsonLdContextWrapper wrapper = new ProbingJsonLdContextWrapper(baseContext);
        for(int i =0; i<10; i++) {
            System.out.println("Generated value for TEST_1: " + wrapper.simplifyPattern(TEST_1, "https://data.europarl.europa.eu/p", false));
        }
        */
       System.out.println(RegexUtil.extractHttpBaseUriFromPattern(TEST_3));
    }
}
