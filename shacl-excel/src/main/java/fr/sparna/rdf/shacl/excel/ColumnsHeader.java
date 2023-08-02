package fr.sparna.rdf.shacl.excel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import fr.sparna.rdf.shacl.excel.model.ColumnsHeader_Input;
import fr.sparna.rdf.shacl.excel.model.ShapeTemplate;


public class ColumnsHeader {

	public List<ShapeTemplate> build(List<ShapeTemplate> template, List<ColumnsHeader_Input> modelData) {

		
		List<ShapeTemplate> tmp = new ArrayList<>();

		// Add fixed column URI
		ShapeTemplate tmpColumns = new ShapeTemplate();
		tmpColumns.setSh_name("URI");
		tmpColumns.setSh_description("URI of the class. This column can use prefixes declared above in the header");
		tmpColumns.setSh_path("URI");
		tmpColumns.setSh_order(1);
		tmp.add(tmpColumns);
		
		Integer nCount = 2;
		if (template.size() > 0) {
			template.sort(Comparator.comparing(ShapeTemplate::getSh_order).thenComparing(ShapeTemplate::getSh_name));
			
			for (ShapeTemplate c : template) {
				
				List<ColumnsHeader_Input> col_in_data = modelData
						.stream()
						.filter(cdata -> cdata.getColumn_name().equals(c.getSh_path()))
						.collect(Collectors.toList());
				
				if (col_in_data.size() > 0) {
					for (ColumnsHeader_Input colAdd : col_in_data) {
						ShapeTemplate tmpTemplateIn = new ShapeTemplate();
						tmpTemplateIn.setSh_name(c.getSh_name());
						tmpTemplateIn.setSh_description(c.getSh_description());
						tmpTemplateIn.setSh_path(colAdd.getColumn_datatypeValue()!=null || colAdd.getColumn_datatypeValue() != "" ? colAdd.getColumn_name()+colAdd.getColumn_datatypeValue():colAdd.getColumn_name());
						tmpTemplateIn.setSh_order(nCount++);
						tmp.add(tmpTemplateIn);
					}
				}else {
					ShapeTemplate tmpTemplate = new ShapeTemplate();
					tmpTemplate.setSh_name(c.getSh_name());
					tmpTemplate.setSh_description(c.getSh_description());
					tmpTemplate.setSh_path(c.getSh_path());
					tmpTemplate.setSh_order(nCount++);
					tmp.add(tmpTemplate);
				}	
			}
			
			
			for (ColumnsHeader_Input cData : modelData) {
				
				String sproperty = cData.getColumn_datatypeValue() != null || cData.getColumn_datatypeValue() != "" ?  cData.getColumn_name()+cData.getColumn_datatypeValue() : cData.getColumn_name();
				
				boolean col_in_data = tmp
						.stream()
						.filter(cTemplate -> cTemplate.getSh_path().equals(sproperty))
						.findFirst()
						.isPresent();
						//.collect(Collectors.toList());
				
				if (!col_in_data) {
					ShapeTemplate tmpTemplate = new ShapeTemplate();
					tmpTemplate.setSh_name("-");
					tmpTemplate.setSh_description("-");
					tmpTemplate.setSh_path(sproperty);
					tmpTemplate.setSh_order(nCount++);
					tmp.add(tmpTemplate);
				}
			}
		}		
		return tmp;
		}	
}
