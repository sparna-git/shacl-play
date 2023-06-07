package fr.sparna.rdf.shacl.excel;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CellColumns {

	public List<XslTemplate> build(List<XslTemplate> template, List<ShapesValues> ShaclConfig) {

		
		List<XslTemplate> tmp = new ArrayList<>();

		// Add fixed column URI
		XslTemplate tmpColumns = new XslTemplate();
		tmpColumns.setSh_name("URI");
		tmpColumns.setSh_description("NodeShape URI");
		tmpColumns.setSh_path("URI");
		tmpColumns.setSh_order(1);

		tmp.add(tmpColumns);
		
		List<String> shConfig = readShaclConfig(ShaclConfig);
		
		if (template.size() > 0) {
			template.sort(Comparator.comparing(XslTemplate::getSh_order).thenComparing(XslTemplate::getSh_name));
			for (XslTemplate c : template) {
				XslTemplate tmpTemplate = new XslTemplate();
				tmpTemplate.setSh_name(c.getSh_name());
				tmpTemplate.setSh_description(c.getSh_description());
				tmpTemplate.setSh_path(c.getSh_path());
				tmpTemplate.setSh_order(c.getSh_order()+1);
				tmp.add(tmpTemplate);
			}
			
			for (String shShape : shConfig.stream().distinct().collect(Collectors.toList())) {
				if (tmp.stream().filter(p -> p.getSh_path().equals(shShape)).collect(Collectors.toList()).isEmpty()) {
					 XslTemplate tmpC = new XslTemplate();
					tmpC.setSh_name("-");
					tmpC.setSh_description("-");
					tmpC.setSh_path(shShape);
					//tmpC.setSh_order();
					tmp.add(tmpC);
				}
			}
		} else {
			for (String prop : shConfig) {
				XslTemplate tmpC = new XslTemplate();
				tmpC.setSh_name("-");
				tmpC.setSh_description("-");
				tmpC.setSh_path(prop);
				//tmpC.setSh_order();
				tmp.add(tmpC);
			}
		}
		
		return tmp;
	}

	public List<String> readShaclConfig(List<ShapesValues> ShaclConfig) {

		List<String> ColumnsConfig = new ArrayList<>();

		for (ShapesValues sp : ShaclConfig) {

			String predicate = sp.getPredicate();

			if (sp.getPredicate().equals("rdfs:label")) {
				if (sp.getObject().contains("@")) {
					String lang = sp.getObject().split("@")[1];
					predicate += "@" + lang;
				}
			}

			ColumnsConfig.add(predicate);
		}

		return ColumnsConfig;
	}

}
