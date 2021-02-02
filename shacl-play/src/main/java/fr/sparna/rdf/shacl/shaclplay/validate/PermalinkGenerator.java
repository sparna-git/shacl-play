package fr.sparna.rdf.shacl.shaclplay.validate;

import java.net.URL;

public class PermalinkGenerator {

	/**
	 * Shapes catalog ID to compute permalink
	 */
	private String shapesCatalogId;

	/**
	 * Data URL to compute permalink
	 */
	private URL dataUrl;

	/**
	 * Shapes URL
	 */
	private URL shapesUrl;
	
	/**
	 * Whether to close shapes
	 */
	private boolean closeShapes;
	
	public PermalinkGenerator(String shapesCatalogId, URL dataUrl, boolean closeShapes) {
		super();
		this.shapesCatalogId = shapesCatalogId;
		this.dataUrl = dataUrl;
		this.closeShapes = closeShapes;
	}
	
	public PermalinkGenerator(URL shapesUrl, URL dataUrl, boolean closeShapes) {
		super();
		this.shapesUrl = shapesUrl;
		this.dataUrl = dataUrl;
		this.closeShapes = closeShapes;
	}

	public String generatePermalink() {
		String permalink = null;
		if(this.dataUrl != null) {
			if(this.shapesCatalogId != null) {
				permalink = shapesCatalogId+"/report?url="+dataUrl;
			} else if(this.shapesUrl != null) {
				permalink = "validate?url="+dataUrl+"&shapesUrl="+this.shapesUrl;				
			}
			
			if(permalink != null) {
				if(closeShapes)  {
					permalink += "&closeShapes=true";
				}
				permalink = "https://shacl-play.sparna.fr/play/"+permalink;
			}
		}
		return permalink;
	}
	
	public String generateBadgeLink() {
		
		String permalink = null;
		if(this.dataUrl != null) {
			if(this.shapesCatalogId != null) {
				permalink = shapesCatalogId+"/badge?url="+dataUrl;
			} else if(this.shapesUrl != null) {
				permalink = "badge?url="+dataUrl+"&shapesUrl="+this.shapesUrl;				
			}
			
			if(permalink != null) {
				if(closeShapes)  {
					permalink += "&closeShapes=true";
				}
				permalink = "https://shacl-play.sparna.fr/play/"+permalink;
			}
		}
		return permalink;
	}

	public String getShapesCatalogId() {
		return shapesCatalogId;
	}

	public void setShapesCatalogId(String shapesCatalogId) {
		this.shapesCatalogId = shapesCatalogId;
	}

	public URL getDataUrl() {
		return dataUrl;
	}

	public void setDataUrl(URL dataUrl) {
		this.dataUrl = dataUrl;
	}

	public boolean isCloseShapes() {
		return closeShapes;
	}

	public void setCloseShapes(boolean closeShapes) {
		this.closeShapes = closeShapes;
	}


}
