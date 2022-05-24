package fr.sparna.rdf.shacl.sparqlgen.construct;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import org.apache.jena.query.Query;

public class WriteFileOutput {

	public static void writeFileOutput(File outputFile, Query querywrite) throws IOException {
		try (
				FileWriter fw = new FileWriter(outputFile);
				BufferedWriter bw = new BufferedWriter(fw)
		) {
			System.out.println("Writing to "+outputFile.getCanonicalPath());
			bw.write(querywrite.toString());
			bw.newLine();
			bw.flush();
			bw.close();
			fw.close();
		}
	}
}
