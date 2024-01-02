package fr.sparna.rdf.shacl.generate.progress;


/**
 * Inspired by TopBraid SHACL API ProgressMonitor
 */
public interface ProgressMonitor {

	/**
	 * Informs the progress monitor that a new task has been started, with a given number of expected steps.
	 * A UI connected to the ProgressMonitor would typically display something like a progress bar and the task name.
	 * @param label  the name of the task
	 * @param totalWork  the number of steps (see <code>worked</code>) that is expected to be needed to complete the task
	 */
	void beginTask(String label, int totalWork);
	

	/**
	 * Informs the progress monitor that all is completed.
	 */
	void done();
	

	/**
	 * Changes the name or label of the current task. 
	 * @param value
	 */
	void setTaskName(String value);
	
	
	/**
	 * Sets the label that serves as sub-task, typically printed under the main task name.
	 * @param label  the subtask label
	 */
	void subTask(String label);
	
	
	/**
	 * Informs the progress monitor that one or more steps have been completed towards the current task (see <code>beginTask</code>).
	 * @param amount  the number of steps completed
	 */
	void worked(int amount);
}

