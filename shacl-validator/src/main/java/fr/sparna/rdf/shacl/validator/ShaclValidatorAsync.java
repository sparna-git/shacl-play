package fr.sparna.rdf.shacl.validator;

import org.apache.jena.rdf.model.Model;

/**
 * Runs the SHACL validation aynschronously.
 * @author thomas.francart@sparna.fr
 *
 */
public class ShaclValidatorAsync extends ShaclValidator implements Runnable {

	protected Model dataModel;
	protected Model results;
	
	public ShaclValidatorAsync(Model shapesModel, Model dataModel, Model complimentaryModel) {
		super(shapesModel);
		this.dataModel = dataModel;
		this.complimentaryModel = complimentaryModel;
	}

	@Override
	public void run() {
		this.results = this.validate(this.dataModel);
	}

	public Model getDataModel() {
		return dataModel;
	}

	public Model getResults() {
		return results;
	}

	public boolean isFinished() {
		return this.getResults() != null;
	}
	
}
