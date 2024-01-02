package fr.sparna.rdf.shacl.generate.progress;

/**
 * A ProgressMonitor that does nothing
 */
public class NoOpProgressMonitor implements ProgressMonitor {
	
	@Override
    public void beginTask(String label, int totalWork) {
		// nothing
	}

	
	@Override
    public void done() {
		// nothing
	}	

	@Override
	public void setTaskName(String value) {
		// nothing
	}


	@Override
    public void subTask(String label) {
		// nothing
	}

	
	@Override
    public void worked(int amount) {
		// nothing
	}
}
