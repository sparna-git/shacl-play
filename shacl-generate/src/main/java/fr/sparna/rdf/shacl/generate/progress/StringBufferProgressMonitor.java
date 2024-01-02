package fr.sparna.rdf.shacl.generate.progress;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;


public class StringBufferProgressMonitor implements ProgressMonitor {
	
	private StringBuffer buffer;
	private String name;	
	private int currentWork;	
	private int totalWork;	
	
	private int polledLogLines = 0;
	
	
	public StringBufferProgressMonitor(String name) {
		this.name = name;
		this.buffer = new StringBuffer();		
	}
	
	@Override
    public void beginTask(String label, int totalWork) {
		println("Begin task " + label + " (" + totalWork + " steps) at "+SimpleDateFormat.getTimeInstance().format(new Date()));
		this.totalWork = totalWork;
		this.currentWork = 0;
	}

	
	@Override
    public void done() {
		println("Done at "+SimpleDateFormat.getTimeInstance().format(new Date()));
	}
	
	
	protected void println(String text) {
		String log = name + ": " + text+"\n";
		buffer.append(log);
	}


	@Override
	public void setTaskName(String value) {
		println("Task name: " + value);
	}


	@Override
    public void subTask(String label) {
		println("Subtask " + label);
	}

	
	@Override
    public void worked(int amount) {
		currentWork += amount;
		println("Done " + currentWork + " / " + totalWork + ((amount > 1)?"(+"+amount+")":""));
	}

	public StringBuffer getBuffer() {
		return buffer;
	}
	
	public int getPercentage() {
		if(totalWork == 0) {
			// don't know why but this can happen...
			return 0;
		}
		return (currentWork / totalWork)*100;
	}
	
	/**
	 * Returns the latest log lines, to be displayed in a tail-ed display
	 * @return
	 */
	public String pollLogs() {	
		String[] logLines = getBuffer().toString().split("\r\n|\r|\n");
		String[] result = Arrays.copyOfRange(logLines, this.polledLogLines, logLines.length);
		
		// store number of lines to skip for next call
		this.polledLogLines = logLines.length;
		
		return String.join("\n", result);
	}
}