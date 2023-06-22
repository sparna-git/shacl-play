package fr.sparna.rdf.shacl.excel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CellColumns {

	public List<XslTemplate> build(List<XslTemplate> template, List<ColumnsData> modelData) {

		
		List<XslTemplate> tmp = new ArrayList<>();

		// Add fixed column URI
		XslTemplate tmpColumns = new XslTemplate();
		tmpColumns.setSh_name("URI");
		tmpColumns.setSh_description("URI of the class. This column can use prefixes declared above in the header");
		tmpColumns.setSh_path("URI");
		tmpColumns.setSh_order(1);
		tmp.add(tmpColumns);
		
		Integer nCount = 2;
		if (template.size() > 0) {
			template.sort(Comparator.comparing(XslTemplate::getSh_order).thenComparing(XslTemplate::getSh_name));
			
			for (XslTemplate c : template) {
				
				List<ColumnsData> col_in_data = modelData
						.stream()
						.filter(cdata -> cdata.getColumn_name().equals(c.getSh_path()))
						.collect(Collectors.toList());
				
				if (col_in_data.size() > 0) {
					for (ColumnsData colAdd : col_in_data) {
						XslTemplate tmpTemplateIn = new XslTemplate();
						tmpTemplateIn.setSh_name(c.getSh_name());
						tmpTemplateIn.setSh_description(c.getSh_description());
						tmpTemplateIn.setSh_path(colAdd.getColumn_datatypeValue()!=null || colAdd.getColumn_datatypeValue() != "" ? colAdd.getColumn_name()+colAdd.getColumn_datatypeValue():colAdd.getColumn_name());
						tmpTemplateIn.setSh_order(nCount++);
						tmp.add(tmpTemplateIn);
					}
				}else {
					XslTemplate tmpTemplate = new XslTemplate();
					tmpTemplate.setSh_name(c.getSh_name());
					tmpTemplate.setSh_description(c.getSh_description());
					tmpTemplate.setSh_path(c.getSh_path());
					tmpTemplate.setSh_order(nCount++);
					tmp.add(tmpTemplate);
				}	
			}
			
			
			for (ColumnsData cData : modelData) {
				
				String sproperty = cData.getColumn_datatypeValue() != null || cData.getColumn_datatypeValue() != "" ?  cData.getColumn_name()+cData.getColumn_datatypeValue() : cData.getColumn_name();
				
				boolean col_in_data = tmp
						.stream()
						.filter(cTemplate -> cTemplate.getSh_path().equals(sproperty))
						.findFirst()
						.isPresent();
						//.collect(Collectors.toList());
				
				if (!col_in_data) {
					XslTemplate tmpTemplate = new XslTemplate();
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

	public List<String> readShaclConfig(List<ShapesValues> ShaclConfig) {

		List<String> ColumnsConfig = new ArrayList<>();

		for (ShapesValues sp : ShaclConfig) {

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

}
