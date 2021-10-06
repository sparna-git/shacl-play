package fr.sparna.rdf.shacl.shacl2xsd;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.Resource;
import org.apache.jena.vocabulary.RDF;
import org.topbraid.shacl.vocabulary.SH;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

public class Shacl2XsdConverter {

	protected String targetNamespace;

	public Shacl2XsdConverter(String targetNamespace) {
		super();
		this.targetNamespace = targetNamespace;
	}

	public Document convert(Model shacl) throws Exception {
		Document doc = this.initDocument();
		doConvert(shacl, doc);
		return doc;
	}

	protected void doConvert(Model shacl, Document document) throws Exception {

		// Read Shacl
		List<Resource> nodeShapes = shacl.listResourcesWithProperty(RDF.type, SH.NodeShape).toList();

		OntologyBoxRead owlRead = new OntologyBoxRead();
		OntologyBox owlData = owlRead.readOntology(shacl);

		// 1. Lire toutes les box
		ShaclXsdBoxReader nodeShapeReader = new ShaclXsdBoxReader();
		List<ShaclXsdBox> ShaclXsdBoxes = nodeShapes.stream().map(res -> nodeShapeReader.read(res, nodeShapes))
				.sorted((b1, b2) -> {
					if (b1.getNametargetclass() != null) {
						if (b2.getNametargetclass() != null) {
							return b2.getNametargetclass().compareTo(b1.getNametargetclass());
						} else {
							return -1;
						}
					} else {
						if (b2.getNametargetclass() != null) {
							return 1;
						} else {
							return b1.getLabel().compareTo(b2.getLabel());
						}
					}
				}).collect(Collectors.toList());

		// 2. Une fois qu'on a toute la liste, lire les proprietes
		for (ShaclXsdBox aBox : ShaclXsdBoxes) {
			aBox.setProperties(nodeShapeReader.readProperties(aBox.getNodeShape(), ShaclXsdBoxes, shacl));
		}

		// 3. Lire les prefixes

		HashSet<String> gatheredPrefixes = new HashSet<>();
		for (ShaclXsdBox abox : ShaclXsdBoxes) {
			List<String> prefixes = nodeShapeReader.readPrefixes(abox.getNodeShape());
			gatheredPrefixes.addAll(prefixes);
		}
		Map<String, String> necessaryPrefixes = ShaclPrefixReader.gatherNecessaryPrefixes(shacl.getNsPrefixMap(),
				gatheredPrefixes);
		List<NamespaceSection> namespaceSections = NamespaceSection.fromMap(necessaryPrefixes);
		List<NamespaceSection> sortNameSpacesectionPrefix = namespaceSections.stream().sorted((s1, s2) -> {
			if (s1.getprefix() != null) {
				if (s2.getprefix() != null) {
					return s1.getprefix().toString().compareTo(s2.getprefix().toString());
				} else {
					return -1;
				}
			} else {
				if (s2.getprefix() != null) {
					return 1;
				} else {
					return s1.getprefix().compareTo(s2.getprefix());
				}
			}
		}).collect(Collectors.toList());

		initRoot(document, sortNameSpacesectionPrefix, owlData, ShaclXsdBoxes);
		// here : do actual conversion

	}

	private void initRoot(Document doc, List<NamespaceSection> rPrefix, OntologyBox owlData, List<ShaclXsdBox> data) {

		data.sort(Comparator.comparing(ShaclXsdBox::getNametargetclass));
		//
		Boolean bReference = data.stream().anyMatch(f -> f.getUseReference());
		
		Element root = doc.createElementNS("http://www.w3.org/2001/XMLSchema#", "xs:schema");
		root.setAttribute("xmlns:xs", "http://www.w3.org/2001/XMLSchema#");
		root.setAttribute("xmlns", targetNamespace);
		root.setAttribute("targetNamespace", "http://data.europa.eu/snb/model#");

		// Section Prefix
		for (NamespaceSection rprefix : rPrefix) {
			root.setAttribute("xmlns:" + rprefix.getprefix(), rprefix.getnamespace());
		}
		root.setAttribute("version", "1.0.0");
		root.setAttribute("elementFormDefault", "qualified");
		doc.appendChild(root);

		for (OntologyImports rOwlImport : owlData.getOntoImports()) {
			if (rOwlImport.getImportSchema() != null) {
				Element imports = doc.createElementNS("http://www.w3.org/2001/XMLSchema#", "xs:import");
				imports.setAttribute("namespace", rOwlImport.getImportURI());
				imports.setAttribute("schemaLocation", rOwlImport.getImportSchema().split(":")[0] + ".xsd");
				root.appendChild(imports);
			}
		}
		// <!-- List of XML elements corresponding to classes -->
		for (ShaclXsdBox abox : data) {
			Element classElement = doc.createElementNS("http://www.w3.org/2001/XMLSchema#", "xs:element");
			classElement.setAttribute("name", abox.getNametargetclass());
			classElement.setAttribute("type", abox.getNametargetclass() + "Type");
						
			root.appendChild(classElement);
		}

		// <!-- List of XML elements corresponding to classes, as property reference -->
		List<String> elementClass = new ArrayList<>();
		for (ShaclXsdBox bbox : data) {
			Element classElementLowerCase = doc.createElementNS("http://www.w3.org/2001/XMLSchema#", "xs:element");
			String str = bbox.getNametargetclass();
			String m = str.replaceFirst(str.substring(0, 1), str.substring(0, 1).toLowerCase());
			elementClass.add(m);
			classElementLowerCase.setAttribute("name", m);
			//classElementLowerCase.setAttribute("type", str + "Type");
			if(bbox.getUseReference()) {
				classElementLowerCase.setAttribute("type", "IdReferenceType");
			}else {
				classElementLowerCase.setAttribute("type", bbox.getNametargetclass() + "Type");
			}			
			root.appendChild(classElementLowerCase);
		}

		// <!-- list of XML elements corresponding to properties -->
		for (ShaclXsdBox complexTypebox : data) {

			// <!-- 1. declare a MediaObjectReferences element, pointing to corresponding
			// type -->
			// <!-- list of XML elements corresponding to references -->
			if (complexTypebox.getUseReference()) {
				Element useReference = doc.createElementNS("http://www.w3.org/2001/XMLSchema#", "xs:element");
				String str = complexTypebox.getNametargetclass();
				String m = str.replaceFirst(str.substring(0, 1), str.substring(0, 1).toLowerCase());
				useReference.setAttribute("name", m + "References");
				useReference.setAttribute("type", complexTypebox.getNametargetclass() + "ReferencesType");
				root.appendChild(useReference);
			}

			for (ShaclXsdProperty rDataProperty : complexTypebox.getProperties()) {

				Boolean bClass = false;
				for (String cUsed : elementClass) {
					if (cUsed.equals(rDataProperty.getValue_path().split(":")[1])) {
						bClass = true;
						break;
					}
				}

				if (rDataProperty.getValue_path() != null & !bClass) {
					Element imports = doc.createElementNS("http://www.w3.org/2001/XMLSchema#", "xs:element");
					imports.setAttribute("name", rDataProperty.getValue_path().split(":")[1]);
					if (bReference) {
						imports.setAttribute("type", "IdReferenceType");
					} else {
						if (rDataProperty.getValue_class_property() != null) {
							imports.setAttribute("type", rDataProperty.getValue_class_property() + "Type");
						} else {
							imports.setAttribute("type", rDataProperty.getValue_datatype());
						}
					}
					root.appendChild(imports);
				}
			}
		}

		for (ShaclXsdBox complexTypebox : data) {
			Element complexType = doc.createElementNS("http://www.w3.org/2001/XMLSchema#", "xs:complexType");
			String str = complexTypebox.getNametargetclass();
			complexType.setAttribute("name", str + "Type");
			root.appendChild(complexType);

			if (complexTypebox.getProperties().size() > 0) {
				
				Element attComplex = doc.createElementNS("http://www.w3.org/2001/XMLSchema#", "xs:attribute");
				attComplex.setAttribute("name", "id");
				attComplex.setAttribute("type", "xs:anyURI");
				attComplex.setAttribute("use", "required");
				complexType.appendChild(attComplex);

				for (OntologyClass readOwlClass : owlData.getOntoClass()) {
					if (readOwlClass.getCommentRDFS() != null) {
						Element attAnnotation = doc.createElementNS("http://www.w3.org/2001/XMLSchema#", "xs:annotation");
						Element attAnnotationDocument = doc.createElementNS("http://www.w3.org/2001/XMLSchema#","xs:documentation");
						attAnnotationDocument.setTextContent(readOwlClass.getCommentRDFS());

						complexType.appendChild(attAnnotation);
						attAnnotation.appendChild(attAnnotationDocument);
					}
				}
				
				
				Element attsequence = doc.createElementNS("http://www.w3.org/2001/XMLSchema#", "xs:sequence");
				for (ShaclXsdProperty rDataProperty : complexTypebox.getProperties()) {
					if (rDataProperty.getValue_path() != null) {
						Element attelementSequence = doc.createElementNS("http://www.w3.org/2001/XMLSchema#","xs:element");
						attelementSequence.setAttribute("ref", rDataProperty.getValue_path().split(":")[1]);
						attelementSequence.setAttribute("maxOccurs", rDataProperty.getValue_maxCount());
						attelementSequence.setAttribute("minOccurs", rDataProperty.getValue_minCount());

						if (rDataProperty.getValue_description() != null) {
							Element elementDescription = doc.createElementNS("http://www.w3.org/2001/XMLSchema#","xs:annotation");
							Element attelementDescription = doc.createElementNS("http://www.w3.org/2001/XMLSchema#","xs:documentation");
							attelementDescription.setTextContent(rDataProperty.getValue_description());

							attelementSequence.appendChild(elementDescription);
							elementDescription.appendChild(attelementDescription);
						} else {
							for (OntologyObjectProperty readOwlClass : owlData.getOntoOP()) {
								if (rDataProperty.getValue_path().split(":")[1].equals(readOwlClass.getPropertyName())) {
									Element elementDescription = doc.createElementNS("http://www.w3.org/2001/XMLSchema#", "xs:annotation");
									Element attelementDescription = doc.createElementNS("http://www.w3.org/2001/XMLSchema#", "xs:documentation");
									attelementDescription.setTextContent(readOwlClass.getCommentRDFS());
									attelementSequence.appendChild(elementDescription);
									elementDescription.appendChild(attelementDescription);

								}
							}
						}
						attsequence.appendChild(attelementSequence);
					}
				}
				complexType.appendChild(attsequence);
			} else {
				Element simpleContent = doc.createElementNS("http://www.w3.org/2001/XMLSchema#", "xs:simpleContent");
				Element extension = doc.createElementNS("http://www.w3.org/2001/XMLSchema#", "xs:extension");
				extension.setAttribute("base","xs:anyURI");
				complexType.appendChild(simpleContent);
				simpleContent.appendChild(extension);
			}
				

			for (OntologyClass readOwlClass : owlData.getOntoClass()) {
				if (readOwlClass.getSubClassOfRDFS() != null) {
					Element complexContent = doc.createElementNS("http://www.w3.org/2001/XMLSchema#",
							"xs:complexContent");
					Element extension = doc.createElementNS("http://www.w3.org/2001/XMLSchema#", "xs:extension");
					extension.setAttribute("base", readOwlClass.getSubClassOfRDFS() + "Type");

					complexType.appendChild(complexContent);
					complexContent.appendChild(extension);
				}
			}
		}

		// <!-- 2. Declare the MediaObjectReferencesType, containing mediaObject
		// elements -->
		// <!-- References types, at the end, after the others -->
		for (ShaclXsdBox complexTypeboxUseReference : data) {
			if (complexTypeboxUseReference.getUseReference()) {
				
				Element complexTypeuseReference = doc.createElementNS("http://www.w3.org/2001/XMLSchema#","xs:complexType");
				complexTypeuseReference.setAttribute("name",complexTypeboxUseReference.getNametargetclass()+"ReferencesType");
				
				Element attsequenceUseReference = doc.createElementNS("http://www.w3.org/2001/XMLSchema#","xs:sequence");
				attsequenceUseReference.setAttribute("maxOccurs", "unbounded");
				attsequenceUseReference.setAttribute("minOccurs", "0");
				
				String str = complexTypeboxUseReference.getNametargetclass();
				String m = str.replaceFirst(str.substring(0, 1), str.substring(0, 1).toLowerCase());
				
				Element attelementSequence = doc.createElementNS("http://www.w3.org/2001/XMLSchema#","xs:element");
				attelementSequence.setAttribute("name", m);
				attelementSequence.setAttribute("type",complexTypeboxUseReference.getNametargetclass()+ "Type");
						
				root.appendChild(complexTypeuseReference);
				complexTypeuseReference.appendChild(attsequenceUseReference);
				attsequenceUseReference.appendChild(attelementSequence);
			}
		}
		
		//<!-- 3. Declare this complexType, always if there is at least one element using references -->
		if(bReference) {
			Element complexIdReference= doc.createElementNS("http://www.w3.org/2001/XMLSchema#","xs:complexType");
			complexIdReference.setAttribute("name","IdReferenceType");
			
			Element refdannotation = doc.createElementNS("http://www.w3.org/2001/XMLSchema#", "xs:annotation");
			Element refdocument = doc.createElementNS("http://www.w3.org/2001/XMLSchema#", "xs:documentation");
			refdocument.setTextContent("A link or reference to another entity record in the document.");
			
			Element AttributeReference = doc.createElementNS("http://www.w3.org/2001/XMLSchema#", "xs:attribute");
			AttributeReference.setAttribute("name", "idref");
			AttributeReference.setAttribute("type", "xs:anyURI");
			AttributeReference.setAttribute("use", "required");
			
			Element AttributeReferenceNottation = doc.createElementNS("http://www.w3.org/2001/XMLSchema#", "xs:annotation");
			
			Element AttributeReferenceNottationDocument = doc.createElementNS("http://www.w3.org/2001/XMLSchema#", "xs:documentation");
			AttributeReferenceNottationDocument.setTextContent("The id of the referenced entity (record).");
			
			root.appendChild(complexIdReference);
			complexIdReference.appendChild(refdannotation);
			refdannotation.appendChild(refdocument);
			complexIdReference.appendChild(AttributeReference);
			AttributeReference.appendChild(AttributeReferenceNottation);
			AttributeReferenceNottation.appendChild(AttributeReferenceNottationDocument);
		}
		
		
	}

	private Document initDocument() {
		Document document = null;
		DocumentBuilderFactory factory = null;

		try {
			factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			document = builder.newDocument();
		} catch (Exception e) {
			e.printStackTrace();
		}

		return document;
	}

}
