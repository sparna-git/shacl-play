package fr.sparna.rdf.shacl.shaclplay.catalog.shapes;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.riot.Lang;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ShapesCatalogService {

	private Logger log = LoggerFactory.getLogger(this.getClass().getName());
	
	public ShapesCatalog getShapesCatalog() {
		Model catalogModel = ModelFactory.createDefaultModel();
		try {
			catalogModel.read("https://raw.githubusercontent.com/sparna-git/SHACL-Catalog/master/shacl-catalog.ttl", null, Lang.TURTLE.getName());
		} catch (Exception e) {
			// if we are offline, then just silently skip
			e.printStackTrace();
			log.warn("Errow when reading shapes catalog");
			
		}
		
		return new ShapesCatalogModelFactory().fromModel(catalogModel);
	}
}
