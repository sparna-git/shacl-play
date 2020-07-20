package fr.sparna.rdf.shacl.closeShapes;

import org.apache.jena.query.Dataset;
import org.apache.jena.query.DatasetFactory;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.riot.Lang;
import org.apache.jena.riot.RDFDataMgr;
import org.junit.Assert;
import org.junit.Test;
import org.topbraid.shacl.vocabulary.SH;

public class CloseShapesTest {

	@Test
	public void test() {
		CloseShapes cs = new CloseShapes();
		
		Dataset d = DatasetFactory.create();
		RDFDataMgr.read(d, this.getClass().getClassLoader().getResourceAsStream("CloseShapes.ttl"), Lang.TURTLE);
    	
        Model withClosedShapes = cs.closeShapes(d.getDefaultModel());
        Assert.assertTrue(withClosedShapes.listSubjectsWithProperty(SH.closed).toList().size() == 1);
	}

}
