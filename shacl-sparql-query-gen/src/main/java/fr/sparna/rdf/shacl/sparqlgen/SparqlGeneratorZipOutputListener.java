package fr.sparna.rdf.shacl.sparqlgen;

import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class SparqlGeneratorZipOutputListener implements SparqlGeneratorOutputListenerIfc {

	protected ZipOutputStream outputStream;

	public SparqlGeneratorZipOutputListener(ZipOutputStream outputStream) {
		super();
		this.outputStream = outputStream;
	}
	
	@Override
	public void notifyStart() throws Exception {
		// nothing to do
	}

	@Override
	public void notifyOutputQuery(String query, String filename) throws Exception {		
		ZipEntry zipFile = new ZipEntry(filename);
		outputStream.putNextEntry(zipFile);
		outputStream.write(query.getBytes(), 0, query.getBytes().length);
		outputStream.closeEntry();	
	}
	
	@Override
	public void notifyStop() throws Exception {
		outputStream.finish();
		outputStream.close();
	}
	
	
}
