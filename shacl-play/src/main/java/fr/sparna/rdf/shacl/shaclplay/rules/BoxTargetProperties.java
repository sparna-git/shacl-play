package fr.sparna.rdf.shacl.shaclplay.rules;

import java.util.ArrayList;
import java.util.List;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Property;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.topbraid.shacl.vocabulary.SH;

public class BoxTargetProperties {
	protected String ShTarget;
	protected String ShPrefixes;
	protected String ShSelect;

	public String getShTarget() {
		return ShTarget;
	}

	public void setShTarget(String shTarget) {
		ShTarget = shTarget;
	}

	public String getShPrefixes() {
		return ShPrefixes;
	}

	public void setShPrefixes(String shPrefixes) {
		ShPrefixes = shPrefixes;
	}

	public String getShSelect() {
		return ShSelect;
	}

	public void setShSelect(String shSelect) {
		ShSelect = shSelect;
	}

	public List<BoxTargetProperties> read (List<Resource> nodeShape){
		BoxTargetProperties target = new BoxTargetProperties();
		List<BoxTargetProperties> aTargetProperties = new ArrayList<>();
		
		for(Resource targetsh : nodeShape) {
			target.setShPrefixes(this.readPrefix(targetsh));
			target.setShSelect(this.readSelect(targetsh));
			aTargetProperties.add(target);
		}
		return aTargetProperties; 
	}

	public String readPrefix(Resource nodeShape) {
		String value = null;
		if (nodeShape.hasProperty(SH.prefixes)) {
			value = nodeShape.getProperty(SH.prefixes).getString();
		}
		return value;
	}

	public String readSelect(Resource nodeShape) {
		String value = null;
		if (nodeShape.hasProperty(SH.select)) {
			value = nodeShape.getProperty(SH.select).getString();
		}
		return value;
	}

}
