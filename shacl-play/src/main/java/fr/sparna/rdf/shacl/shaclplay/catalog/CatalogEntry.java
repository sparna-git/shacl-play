package fr.sparna.rdf.shacl.shaclplay.catalog;

import java.net.URI;
import java.net.URL;
import java.util.Date;
import java.util.List;

public class CatalogEntry {

	protected String id;
	protected String title;
	protected String description;
	protected URL turtleDownloadUrl;
	
	protected List<Distribution> distributions;
	protected List<String> keywords;
	
	protected Agent creator;
	protected Agent publisher;
	protected Agent submitter;
	protected Date issued;
	protected Date submitted;
	protected URI contactPoint;
	protected String language;
	protected String landingPage;
	
	public String[] getKeywordsArray() {
		if(this.keywords == null) return null;
		return this.keywords.toArray(new String[] {});
	}
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getTitle() {
		return title;
	}
	public void setTitle(String title) {
		this.title = title;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public URL getTurtleDownloadUrl() {
		return turtleDownloadUrl;
	}
	public void setTurtleDownloadUrl(URL turtleDownloadUrl) {
		this.turtleDownloadUrl = turtleDownloadUrl;
	}
	
	public List<Distribution> getDistributions() {
		return distributions;
	}
	public void setDistributions(List<Distribution> distributions) {
		this.distributions = distributions;
	}
	public List<String> getKeywords() {
		return keywords;
	}
	public void setKeywords(List<String> keywords) {
		this.keywords = keywords;
	}
	public Agent getCreator() {
		return creator;
	}
	public void setCreator(Agent creator) {
		this.creator = creator;
	}
	public Agent getPublisher() {
		return publisher;
	}
	public void setPublisher(Agent publisher) {
		this.publisher = publisher;
	}
	public Agent getSubmitter() {
		return submitter;
	}
	public void setSubmitter(Agent submitter) {
		this.submitter = submitter;
	}
	public Date getIssued() {
		return issued;
	}
	public void setIssued(Date issued) {
		this.issued = issued;
	}
	public Date getSubmitted() {
		return submitted;
	}
	public void setSubmitted(Date submitted) {
		this.submitted = submitted;
	}
	public URI getContactPoint() {
		return contactPoint;
	}
	public void setContactPoint(URI contactPoint) {
		this.contactPoint = contactPoint;
	}
	public String getLanguage() {
		return language;
	}
	public void setLanguage(String language) {
		this.language = language;
	}
	public String getLandingPage() {
		return landingPage;
	}
	public void setLandingPage(String landingPage) {
		this.landingPage = landingPage;
	}




	public class Distribution {
		protected String mediaType;
		protected URL downloadUrl;
		
		public Distribution(String mediaType, URL downloadUrl) {
			super();
			this.mediaType = mediaType;
			this.downloadUrl = downloadUrl;
		}
		
		public String getMediaType() {
			return mediaType;
		}
		public void setMediaType(String mediaType) {
			this.mediaType = mediaType;
		}
		public URL getDownloadUrl() {
			return downloadUrl;
		}
		public void setDownloadUrl(URL downloadUrl) {
			this.downloadUrl = downloadUrl;
		}		
	}
	
	public class Agent {
		protected String uri;
		protected String label;
		
		public Agent(String uri, String label) {
			super();
			this.uri = uri;
			this.label = label;
		}
		
		public String getUri() {
			return uri;
		}
		public void setUri(String uri) {
			this.uri = uri;
		}
		public String getLabel() {
			return label;
		}
		public void setLabel(String label) {
			this.label = label;
		}
		
		
	}
	
}
