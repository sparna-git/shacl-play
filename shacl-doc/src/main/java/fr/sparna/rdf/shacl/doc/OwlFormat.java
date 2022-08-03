package fr.sparna.rdf.shacl.doc;

import org.apache.jena.rdf.model.Resource;

public class OwlFormat {

	protected Resource dctFormat;
	protected Resource dcatURL;
	
	
	public Resource getDctFormat() {
		return dctFormat;
	}
	public void setDctFormat(Resource dctFormat) {
		this.dctFormat = dctFormat;
	}
	public Resource getDcatURL() {
		return dcatURL;
	}
	public void setDcatURL(Resource dcatURL) {
		this.dcatURL = dcatURL;
	}
	
	
}
