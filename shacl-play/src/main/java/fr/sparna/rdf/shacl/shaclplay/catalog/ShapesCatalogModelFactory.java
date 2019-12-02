package fr.sparna.rdf.shacl.shaclplay.catalog;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.DCTerms;
import org.apache.jena.vocabulary.RDF;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ShapesCatalogModelFactory {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	public ShapesCatalog fromModel(Model model) {
		ShapesCatalog catalog = new ShapesCatalog();
		
		model.listSubjectsWithProperty(RDF.type, DCAT.Dataset).forEachRemaining(r -> {
			log.debug("Parsing Catalog entry "+r.getLocalName());
			CatalogEntry entry = new CatalogEntry();
			// ID
			entry.setId(r.getLocalName());
			// title + description
			entry.setTitle(r.listProperties(DCTerms.title).filterDrop(s -> s.getObject().isLiteral() && s.getObject().asLiteral().getLanguage() == null).toList().get(0).getString());
			entry.setDescription(r.listProperties(DCTerms.description).filterDrop(s -> s.getObject().isLiteral() && s.getObject().asLiteral().getLanguage() == null).toList().get(0).getString());
			// turtle download URL
			entry.setTurtleDownloadUrl(readTurtleDownloadUrl(readDistributionUrls(r)));
			// submitter and submitted
			if(r.hasProperty(FOAF.isPrimaryTopicOf)) {
				r.getRequiredProperty(FOAF.isPrimaryTopicOf).getObject().asResource().listProperties(DCTerms.issued).forEachRemaining(s -> 
					entry.submitted = ((XSDDateTime)s.getObject().asLiteral().getValue()).asCalendar().getTime()
				);
				r.getRequiredProperty(FOAF.isPrimaryTopicOf).getObject().asResource().listProperties(DCTerms.creator).forEachRemaining(s -> 
					entry.submitter = entry.new Agent(null, s.getObject().asLiteral().getLexicalForm())
				);
			}
			// keywords
			entry.keywords = r.listProperties(DCAT.keyword).toList().stream().map(s -> s.getObject().asLiteral().getLexicalForm()).collect(Collectors.toList());
			// creator and publisher
			r.listProperties(DCTerms.creator).forEachRemaining(s -> entry.creator = entry.new Agent(null, s.getObject().asLiteral().getLexicalForm()));
			r.listProperties(DCTerms.publisher).forEachRemaining(s -> entry.publisher = entry.new Agent(null, s.getObject().asLiteral().getLexicalForm()));
			// issued
			r.listProperties(DCTerms.issued).forEachRemaining(s -> entry.issued = ((XSDDateTime)s.getObject().asLiteral().getValue()).asCalendar().getTime());
			// landing page
			r.listProperties(DCAT.landingPage).forEachRemaining(s -> entry.landingPage = s.getObject().asResource().getURI());
			
			catalog.getEntries().add(entry);
		});
		
		return catalog;
	}
	
	protected Map<String, String> readDistributionUrls(Resource entry) {
		HashMap<String, String> distributions = new HashMap<>();
		
		entry.listProperties(DCAT.distribution).forEachRemaining(distribution -> {
			String mediaType = distribution.getObject().asResource().getRequiredProperty(DCAT.mediaType).getObject().asResource().getURI();
			String downloadUrl = distribution.getObject().asResource().getRequiredProperty(DCAT.downloadURL).getObject().asResource().getURI();
			
			distributions.put(mediaType, downloadUrl);
		});
		
		return distributions;
	}
	
	protected URL readTurtleDownloadUrl(Map<String, String> distributions) {
		final String TURTLE_MEDIA_TYPE = "https://www.iana.org/assignments/media-types/text/turtle";
		
		String s = distributions.get(TURTLE_MEDIA_TYPE);
		
		if(s == null) {
			return null;
		} else {
			try {
				return new URL(s);
			} catch (MalformedURLException e) {
				e.printStackTrace();
				return null;
			}
		}
	}
	
}
