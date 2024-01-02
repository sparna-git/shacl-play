package fr.sparna.rdf.shacl.generate.progress;

/**
 * A simple implementation of ProgressMonitor that prints messages to System.out.
 *
 * @author Holger Knublauch
 */
public class SimpleProgressMonitor implements ProgressMonitor {
	

	private int currentWork;
	
	private String name;
	
	private int totalWork;
	
	
	public SimpleProgressMonitor(String name) {
		this.name = name;
	}

	
	@Override
    public void beginTask(String label, int totalWork) {
		println("Beginning task " + label + " (" + totalWork + ")");
		this.totalWork = totalWork;
		this.currentWork = 0;
	}

	
	@Override
    public void done() {
		println("Done");
	}	
	
	protected void println(String text) {
		System.out.println(name + ": " + text);
	}


	@Override
	public void setTaskName(String value) {
		println("Task name: " + value);
	}


	@Override
    public void subTask(String label) {
		println("Subtask: " + label);
	}

	
	@Override
    public void worked(int amount) {
		currentWork += amount;
		println("Worked " + currentWork + " / " + totalWork);
	}
}
