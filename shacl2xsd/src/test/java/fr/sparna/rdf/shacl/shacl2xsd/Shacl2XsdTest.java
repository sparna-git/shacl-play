package fr.sparna.rdf.shacl.shacl2xsd;

import java.io.File;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.runner.RunWith;
import org.junit.runners.AllTests;

import junit.framework.TestSuite;

/**
 * Don't rename this class , it has to end with *Test to be picked up my Maven surfefire plugin
 *
 */
@RunWith(AllTests.class)
public class Shacl2XsdTest {
	
    public static TestSuite suite() {
        TestSuite ts = new TestSuite();

        File testDir = new File("src/test/resources/testsuite");
        List<File> sortedList = Arrays.asList(testDir.listFiles());
        Collections.sort(sortedList);
        for (File aDir : sortedList) {
        	if(
        			aDir.isDirectory()
        			&&
        			(
        					System.getProperty("test") == null
        					||
        					System.getProperty("test").equals(aDir.getName())
        			)
        	) {
        		ts.addTest(new Shacl2XsdTestExecution(aDir));
        	}
		}
        

        return ts;
    }

}
