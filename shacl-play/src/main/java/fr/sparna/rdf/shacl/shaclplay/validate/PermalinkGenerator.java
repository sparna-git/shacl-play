package fr.sparna.rdf.shacl.shaclplay.validate;

public class PermalinkGenerator {

	/**
	 * Shapes catalog ID to compute permalink
	 */
	private String shapesCatalogId;

	/**
	 * Data URL to compute permalink
	 */
	private String dataUrl;

	/**
	 * Whether to close shapes
	 */
	private boolean closeShapes;
	
	public PermalinkGenerator(String shapesCatalogId, String dataUrl, boolean closeShapes) {
		super();
		this.shapesCatalogId = shapesCatalogId;
		this.dataUrl = dataUrl;
		this.closeShapes = closeShapes;
	}

	public String generatePermalink() {
		if(this.dataUrl != null && this.shapesCatalogId != null) {
			String permalink = shapesCatalogId+"/report?url="+dataUrl;
			if(closeShapes)  {
				permalink += "&closeShapes=true";
			}
			return "https://shacl-play.sparna.fr/play/"+permalink;
		} else {
			return null;
		}
	}
	
	public String generateBadgeLink() {
		if(this.dataUrl != null && this.shapesCatalogId != null) {
			String permalink = shapesCatalogId+"/badge?url="+dataUrl;
			if(closeShapes)  {
				permalink += "&closeShapes=true";
			}
			return "https://shacl-play.sparna.fr/play/"+permalink;
		} else {
			return null;
		}
	}

	public String getShapesCatalogId() {
		return shapesCatalogId;
	}

	public void setShapesCatalogId(String shapesCatalogId) {
		this.shapesCatalogId = shapesCatalogId;
	}

	public String getDataUrl() {
		return dataUrl;
	}

	public void setDataUrl(String dataUrl) {
		this.dataUrl = dataUrl;
	}

	public boolean isCloseShapes() {
		return closeShapes;
	}

	public void setCloseShapes(boolean closeShapes) {
		this.closeShapes = closeShapes;
	}


}
