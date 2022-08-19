package fr.sparna.rdf.shacl.diagram;

import java.util.ArrayList;
import java.util.List;

public class BoxforDiagram {

//	public List<List<PlantUmlDiagram>> readBox(List<PlantUmlBox> AllBox){
//		
//		//List<List<PlantUmlBox>> nBox = new ArrayList<>();
//		List<List<PlantUmlDiagram>> pUmlDiagram = new ArrayList<>();		
//		for(PlantUmlBox BoxUnit : AllBox) {
//			if(BoxUnit.getDiagrams().size() > 0) {
//				List<PlantUmlDiagram> pDiagram = new ArrayList<PlantUmlDiagram>();
//				List<PlantUmlBox> nBoxSection = new ArrayList<>();
//				for (String node : BoxUnit.getDiagrams()) {
//					for (PlantUmlBox puml : AllBox) {
//						if(puml.getLabel().toString().equals(node)) {
//							PlantUmlDiagram pDiagramLink = new PlantUmlDiagram();
//							//nBoxSection.add(puml);
//							pDiagramLink.setDiagrams(puml);
//							pDiagram.add(pDiagramLink);
//							break;
//						}
//						
//					}
//				}
//				nBoxSection.add(BoxUnit);
//				PlantUmlDiagram plantDiagram = new PlantUmlDiagram();
//				plantDiagram.setDiagrams(BoxUnit);
//				plantDiagram.setDescription(BoxUnit.getDiagramaName());
//				pDiagram.add(plantDiagram);
//				//nBox.add(nBoxSection);
//				pUmlDiagram.add(pDiagram);
//			}
//		}
//		return pUmlDiagram;
//	}
}
