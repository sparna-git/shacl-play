package fr.sparna.rdf.shacl.shaclplay.catalog;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.stream.Collectors;

import org.apache.jena.datatypes.xsd.XSDDateTime;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.sparql.vocabulary.FOAF;
import org.apache.jena.vocabulary.DCAT;
import org.apache.jena.vocabulary.DCTerms;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sparna.rdf.shacl.shaclplay.catalog.AbstractCatalogEntry.Distribution;

public class AbstractCatalogEntryResourceFactory {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	public AbstractCatalogEntry populate(AbstractCatalogEntry entry, Resource r) {

		// ID
		entry.setId(r.getLocalName());
		// title + description
		entry.setTitle(r.listProperties(DCTerms.title).filterDrop(s -> s.getObject().isLiteral() && s.getObject().asLiteral().getLanguage() == null).toList().get(0).getString());
		entry.setDescription(r.listProperties(DCTerms.description).filterDrop(s -> s.getObject().isLiteral() && s.getObject().asLiteral().getLanguage() == null).toList().get(0).getString());
		
		// distributions
		populateDistributions(entry, r);
		
		// turtle download URL
		entry.setTurtleDownloadUrl(readTurtleDownloadUrl(entry));

		// submitter and submitted
		if(r.hasProperty(FOAF.isPrimaryTopicOf)) {
			r.getRequiredProperty(FOAF.isPrimaryTopicOf).getObject().asResource().listProperties(DCTerms.issued).forEachRemaining(s -> 
				entry.setSubmitted( ((XSDDateTime)s.getObject().asLiteral().getValue()).asCalendar().getTime() )
			);
			r.getRequiredProperty(FOAF.isPrimaryTopicOf).getObject().asResource().listProperties(DCTerms.creator).forEachRemaining(s -> 
				entry.setSubmitter( entry.new Agent(null, s.getObject().asLiteral().getLexicalForm()) )
			);
		}
		// keywords
		entry.setKeywords( r.listProperties(DCAT.keyword).toList().stream().map(s -> s.getObject().asLiteral().getLexicalForm()).collect(Collectors.toList()) );
		// creator and publisher
		r.listProperties(DCTerms.creator).forEachRemaining(s -> entry.setCreator( entry.new Agent(null, s.getObject().asLiteral().getLexicalForm())) );
		r.listProperties(DCTerms.publisher).forEachRemaining(s -> entry.setPublisher( entry.new Agent(null, s.getObject().asLiteral().getLexicalForm())) );
		// issued
		r.listProperties(DCTerms.issued).forEachRemaining(s -> entry.setIssued( ((XSDDateTime)s.getObject().asLiteral().getValue()).asCalendar().getTime()) );
		// landing page
		r.listProperties(DCAT.landingPage).forEachRemaining(s -> entry.setLandingPage( s.getObject().asResource().getURI()) );
		
	
		return entry;
	}
	
	protected void populateDistributions(AbstractCatalogEntry entry, Resource r) {
		r.listProperties(DCAT.distribution).forEachRemaining(distribution -> {
			String mediaType = distribution.getObject().asResource().getRequiredProperty(DCAT.mediaType).getObject().asResource().getURI();
			String downloadUrl = distribution.getObject().asResource().getRequiredProperty(DCAT.downloadURL).getObject().asResource().getURI();
			
			try {
				entry.getDistributions().add(entry.new Distribution(mediaType, new URL(downloadUrl)));
			} catch (MalformedURLException e) {
				e.printStackTrace();
			}
		});
	}
	
	protected URL readTurtleDownloadUrl(AbstractCatalogEntry entry) {
		final String TURTLE_MEDIA_TYPE = "https://www.iana.org/assignments/media-types/text/turtle";
		
		Distribution d = entry.findDistributionByMediaType(TURTLE_MEDIA_TYPE);
		
		if(d == null) {
			return null;
		} else {
			URL url = d.getDownloadUrl();
			return url;
		}
	}
	
}
