package fr.sparna.rdf.shacl.validator;

import org.slf4j.Logger;
import org.topbraid.jenax.progress.ProgressMonitor;


public class Slf4jProgressMonitor implements ProgressMonitor {
	
	private String name;	
	private int currentWork;	
	private int totalWork;	
	private Logger log;
	
	public Slf4jProgressMonitor(String name, Logger log) {
		this.name = name;
		this.log = log;		
	}
	
	@Override
    public void beginTask(String label, int totalWork) {
		log.debug("Beginning task " + label + " (" + totalWork + ")");
		this.totalWork = totalWork;
		this.currentWork = 0;
	}

	
	@Override
    public void done() {
		log.debug("Done");
	}
	
	@Override
    public boolean isCanceled() {
		return false;
	}	
	
	protected void println(String text) {
		String line = name + ": " + text+"\n";
		log.debug(line);
	}
	
	@Override
	public void setCanceled(boolean value) {
	}


	@Override
	public void setTaskName(String value) {
		log.debug("Task name: " + value);
	}


	@Override
    public void subTask(String label) {
		log.debug("Subtask: " + label);
	}

	
	@Override
    public void worked(int amount) {
		currentWork += amount;
		log.debug("Worked " + amount + " : " + currentWork + " / " + totalWork);
	}
	
	public int getPercentage() {	
		return (currentWork / totalWork)*100;
	}

}