package fr.sparna.rdf.shacl.shaclplay.validate;

import org.apache.jena.rdf.model.Model;

import fr.sparna.rdf.shacl.printer.report.ValidationReport;

public class ShieldsIoOutputFactory {

	private Model results;
	
	public ShieldsIoOutputFactory(Model results) {
		this.results = results;
	}
	
	public ShieldsIoOutput build() {
		ShieldsIoOutput output = new ShieldsIoOutput();
		
		output.setLabel("shacl-play");
		
		output.setMessage(this.buildMessage());
		output.setColor(this.buildColor());
		
		return output;
	}
	
	private String buildMessage() {
		ValidationReport r = new ValidationReport(results, results);
				
		StringBuffer sb = new StringBuffer();
		
		if(!r.hasMatched()) {
			sb.append("No targets found");
		} else {
			if(r.isConformant()) {
				return "Conformant";
			}
			
			if(r.getNumberOfViolations() > 0) {
				sb.append(r.getNumberOfViolations()+" violation"+((r.getNumberOfViolations() > 1)?"s":""));
				if(r.getNumberOfWarnings() > 0 || r.getNumberOfInfos() > 0 || r.getNumberOfOthers() > 0) {
					sb.append(" ");
				}
			}
			if(r.getNumberOfWarnings() > 0) {
				sb.append(r.getNumberOfWarnings()+" warning"+((r.getNumberOfWarnings() > 1)?"s":""));
				if(r.getNumberOfInfos() > 0 || r.getNumberOfOthers() > 0) {
					sb.append(" ");
				}
			}
			if(r.getNumberOfInfos() > 0) {
				sb.append(r.getNumberOfInfos()+" info"+((r.getNumberOfInfos() > 1)?"s":""));
				if(r.getNumberOfOthers() > 0) {
					sb.append(" ");
				}
			}
			if(r.getNumberOfOthers() > 0) {
				sb.append(r.getNumberOfOthers()+" other"+((r.getNumberOfOthers() > 1)?"s":""));
			}
		}
		
		return sb.toString();
	}
	
	private String buildColor() {
		ValidationReport r = new ValidationReport(results, results);
		if(!r.hasMatched()) {
			return "blueviolet";
		} else if(r.getNumberOfViolations() > 0) {
			return "red";
		}else if(r.getNumberOfWarnings() > 0) {
			return "orange";
		}else if(r.getNumberOfInfos() > 0) {
			return "yellow";
		}else if(r.getNumberOfOthers() > 0) {
			return "yellowgreen";
		}else {
			return "brightgreen";
		}
	}
}
