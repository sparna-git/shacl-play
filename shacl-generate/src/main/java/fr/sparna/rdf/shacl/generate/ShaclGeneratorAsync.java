package fr.sparna.rdf.shacl.generate;

import org.apache.jena.rdf.model.Model;

import fr.sparna.rdf.shacl.generate.providers.ShaclGeneratorDataProviderIfc;

/**
 * Runs the SHACL generation aynschronously.
 * @author thomas.francart@sparna.fr
 *
 */
public class ShaclGeneratorAsync extends ShaclGenerator implements Runnable {

	protected Model generatedShapes;
	protected Configuration configuration;
	protected ShaclGeneratorDataProviderIfc dataProvider;
	
	public ShaclGeneratorAsync(
			Configuration configuration,
			ShaclGeneratorDataProviderIfc dataProvider
	) {
		super();
		this.configuration = configuration;
		this.dataProvider = dataProvider;
	}

	@Override
	public void run() {
		this.generatedShapes = null;
		this.generatedShapes = this.generateShapes(this.configuration, this.dataProvider);
	}

	public Model getGeneratedShapes() {
		return generatedShapes;
	}

	public Configuration getConfiguration() {
		return configuration;
	}

	public ShaclGeneratorDataProviderIfc getDataProvider() {
		return dataProvider;
	}

	public boolean isFinished() {
		return this.getGeneratedShapes() != null;
	}
	
}
