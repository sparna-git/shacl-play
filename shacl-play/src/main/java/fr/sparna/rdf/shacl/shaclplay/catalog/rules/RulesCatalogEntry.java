package fr.sparna.rdf.shacl.shaclplay.catalog.rules;

import java.util.List;

import fr.sparna.rdf.shacl.shaclplay.catalog.AbstractCatalogEntry;

public class RulesCatalogEntry extends AbstractCatalogEntry {

	protected List<String> sourceModels;
	protected List<String> targetModels;

	public List<String> getSourceModels() {
		return sourceModels;
	}

	public void setSourceModels(List<String> sourceModels) {
		this.sourceModels = sourceModels;
	}

	public List<String> getTargetModels() {
		return targetModels;
	}

	public void setTargetModels(List<String> targetModels) {
		this.targetModels = targetModels;
	}
	
}
