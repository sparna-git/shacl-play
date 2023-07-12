package fr.sparna.rdf.shacl.excel;

import java.util.ArrayList;
import java.util.List;

import fr.sparna.rdf.shacl.excel.model.ColumnsHeader_Input;

public class Output_ColumnsHeader {
	
	
	protected List<ColumnsHeader_Input> readData(List<ColumnsHeader_Input> PropertiesColumns){
		
		List<ColumnsHeader_Input> output_col_header = new ArrayList<>();
		for (ColumnsHeader_Input val : PropertiesColumns) {
			ColumnsHeader_Input colData = new ColumnsHeader_Input();
			boolean truevalue = output_col_header
					.stream()
					.filter(
							s -> s.getColumn_name().equals(val.getColumn_name())
								 &&
								 s.getColumn_datatypeValue().equals(val.getColumn_datatypeValue())
							)
					.findFirst()
					.isPresent();
			
			if (!truevalue) {
				colData.setColumn_name(val.getColumn_name());
				colData.setColumn_datatypeValue(val.getColumn_datatypeValue());
				output_col_header.add(colData);
			}	
		}
		return output_col_header; 
	}

}
