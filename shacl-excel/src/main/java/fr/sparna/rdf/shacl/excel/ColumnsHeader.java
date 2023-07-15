package fr.sparna.rdf.shacl.excel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import fr.sparna.rdf.shacl.excel.model.ColumnsHeader_Input;
import fr.sparna.rdf.shacl.excel.model.PropertyShapeTemplate;


public class ColumnsHeader {

	public List<PropertyShapeTemplate> build(List<PropertyShapeTemplate> template, List<ColumnsHeader_Input> modelData) {

		
		List<PropertyShapeTemplate> tmp = new ArrayList<>();

		// Add fixed column URI
		PropertyShapeTemplate tmpColumns = new PropertyShapeTemplate();
		tmpColumns.setSh_name("URI");
		tmpColumns.setSh_description("URI of the class. This column can use prefixes declared above in the header");
		tmpColumns.setSh_path("URI");
		tmpColumns.setSh_order(1.0);
		tmp.add(tmpColumns);
		
		Double nCount = 2.0;
		if (template.size() > 0) {
			template.sort(Comparator.comparing(PropertyShapeTemplate::getSh_order).thenComparing(PropertyShapeTemplate::getSh_name));
			
			for (PropertyShapeTemplate c : template) {
				
				List<ColumnsHeader_Input> col_in_data = modelData
						.stream()
						.filter(cdata -> cdata.getColumn_name().equals(c.getSh_path()))
						.collect(Collectors.toList());
				
				if (col_in_data.size() > 0) {
					for (ColumnsHeader_Input colAdd : col_in_data) {
						PropertyShapeTemplate tmpTemplateIn = new PropertyShapeTemplate();
						tmpTemplateIn.setSh_name(c.getSh_name());
						tmpTemplateIn.setSh_description(c.getSh_description());
						tmpTemplateIn.setSh_path(colAdd.getColumn_datatypeValue()!=null || colAdd.getColumn_datatypeValue() != "" ? colAdd.getColumn_name()+colAdd.getColumn_datatypeValue():colAdd.getColumn_name());
						tmpTemplateIn.setSh_order(nCount++);
						tmp.add(tmpTemplateIn);
					}
				}else {
					PropertyShapeTemplate tmpTemplate = new PropertyShapeTemplate();
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
					PropertyShapeTemplate tmpTemplate = new PropertyShapeTemplate();
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

	/*
	public List<String> readShaclConfig(List<InputValues> ShaclConfig) {

		List<String> ColumnsConfig = new ArrayList<>();

		for (InputValues sp : ShaclConfig) {

			String predicate = sp.getPredicate();			
			
			if (!sp.getObject().equals(sp.getDatatype())) {
				if(sp.getDatatype().toString().equals("rdf:langString")){
					String lang = sp.getObject().split("@")[1];
					predicate += "@" + lang;
				}else {
					String others = sp.getDatatype().toString();
					predicate += "^^<" + others+">";
				}				
			}
			ColumnsConfig.add(predicate);
		}
		
		
		// Other validate
		List<String> Columns = ColumnsConfig.stream().sorted((a,b) -> {
			if (!a.toString().isEmpty()) {
				if(!b.toString().isEmpty()) {
					return a.toString().compareTo(b.toString());
				}else {
					return -1;
				}
			}else {
				if (b.toString().isEmpty()) {
					return 1;
				}else {
					return b.toString().compareTo(a.toString());
				}
			}			
		})
				.distinct()
				.collect(Collectors.toList());
		return Columns;
	}
	*/
}
