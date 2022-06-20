package fr.sparna.rdf.shacl.sparqlgen;

import java.io.File;
import java.io.FileOutputStream;
import java.nio.file.Files;

public class SparqlGeneratorFileOutputListener implements SparqlGeneratorOutputListenerIfc {

	protected File outputDir;

	public SparqlGeneratorFileOutputListener(File outputDir) {
		super();
		this.outputDir = outputDir;
	}
	
	@Override
	public void notifyStart() throws Exception {
		// Create folder is not exist
		if(!this.outputDir.exists()) {
			Files.createDirectory(this.outputDir.toPath());
		} else {
			deleteFileResult(this.outputDir);
		}		
	}

	@Override
	public void notifyOutputQuery(String query, String filename) throws Exception {
		File outputFile = new File(outputDir, filename);
		FileOutputStream out = new FileOutputStream(outputFile);
		org.apache.commons.io.IOUtils.write(query, out, "UTF-8");
		out.close();
	}	
	
	@Override
	public void notifyStop() throws Exception {		
	}
	
	private void deleteFileResult(File outputDir) {
		for(File qFile :outputDir.listFiles()) {
			String filename = qFile.toString();
			int index = filename.indexOf('.');
			if(index > 0) {
				String extension = filename.substring(index+1);
				if(extension.equals("rq")) {
					qFile.delete();
				}
			}
		}
	}
	
	
	
}
