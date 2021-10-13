package fr.sparna.rdf.shacl.shacl2xsd;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
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

	public Document convert(Model shacl, Model shaclControlledVocabularies) throws Exception {
		Document doc = this.initDocument();
		doConvert(shacl, shaclControlledVocabularies, doc);
		return doc;
	}

	protected void doConvert(Model shacl, Model shaclControlledVocabularies, Document document) throws Exception {

		// Read Shacl
		List<Resource> nodeShapes = shacl.listResourcesWithProperty(RDF.type, SH.NodeShape).toList();
		List<Resource> nodeShapesVocabulary = shaclControlledVocabularies
				.listResourcesWithProperty(RDF.type, SH.NodeShape).toList();

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

		// Constraint Vocabulary
		// 2. Lire toutes les box Constraints
		ShaclXsdBoxReader nodeShapeReaderConstraint = new ShaclXsdBoxReader();
		List<ShaclXsdBox> ShaclXsdBoxesConstraints = nodeShapesVocabulary.stream()
				.map(res -> nodeShapeReader.read(res, nodeShapesVocabulary)).sorted((b1, b2) -> {
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
		// Reader the constraint vocabulary
		for (ShaclXsdBox aBoxConstraints : ShaclXsdBoxesConstraints) {
			aBoxConstraints.setProperties(nodeShapeReaderConstraint.readProperties(aBoxConstraints.getNodeShape(),
					ShaclXsdBoxesConstraints, shacl));
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

		initRoot(document, sortNameSpacesectionPrefix, owlData, ShaclXsdBoxes, ShaclXsdBoxesConstraints);
		// here : do actual conversion

	}

	private void initRoot(Document doc, List<NamespaceSection> rPrefix, OntologyBox owlData, List<ShaclXsdBox> data,
			List<ShaclXsdBox> ConstraintsVocabulary) {

		//data.sort(Comparator.comparing(ShaclXsdBox::getNametargetclass));
		Boolean bReference = data.stream().anyMatch(f -> f.getUseReference());
		String isRoot = null;
		Boolean bCtrlVocabulary = false;
		if (owlData.getXsdRootElement() != null) {
			isRoot = owlData.getXsdRootElement();
		}
		/*
		 * Prefix
		 * 
		 * 
		 * 
		 */

		Element root = doc.createElementNS("http://www.w3.org/2001/XMLSchema", "xs:schema");
		root.setAttribute("xmlns:xs", "http://www.w3.org/2001/XMLSchema");
		root.setAttribute("xmlns", targetNamespace);
		root.setAttribute("targetNamespace", "http://data.europa.eu/snb/model#");

		// Section Prefix
		for (NamespaceSection rprefix : rPrefix) {
			if (!rprefix.getnamespace().equals("http://www.w3.org/2001/XMLSchema#")) {
				root.setAttribute("xmlns:" + rprefix.getprefix(), rprefix.getnamespace());

			}
		}

		for (OntologyImports rOwlImport : owlData.getOntoImports()) {
			if (rOwlImport.getImportSchema() != null) {
				root.setAttribute("xmlns:" + rOwlImport.getImportSchema().split(":")[0], rOwlImport.getImportURI());

			}
		}

		root.setAttribute("version", "1.0.0");
		root.setAttribute("elementFormDefault", "qualified");
		doc.appendChild(root);

		/*
		 * 
		 * Imports
		 * 
		 */
		for (OntologyImports rOwlImport : owlData.getOntoImports()) {
			if (rOwlImport.getImportSchema() != null) {
				Element imports = doc.createElementNS("http://www.w3.org/2001/XMLSchema", "xs:import");
				imports.setAttribute("namespace", rOwlImport.getImportURI());
				imports.setAttribute("schemaLocation", rOwlImport.getImportSchema().split(":")[0] + ".xsd");
				root.appendChild(imports);
			}
		}

		// root element
		if (isRoot != null) {
			root.appendChild(doc.createComment("Root element"));
			String m = isRoot.replaceFirst(isRoot.substring(0, 1), isRoot.substring(0, 1).toUpperCase());
			Element classElementLowerCase = doc.createElementNS("http://www.w3.org/2001/XMLSchema", "xs:element");
			classElementLowerCase.setAttribute("name", m);
			classElementLowerCase.setAttribute("type", m+"Type");
			root.appendChild(doc.createComment("keys"));
			for (ShaclXsdBox boxKeyElements : data) {
				if (boxKeyElements.getUseReference()) {
					System.out.println("Box : "+boxKeyElements.getNodeShape().getURI()+" uses references");
					String nameClasse = boxKeyElements.getNametargetclass().split(":")[1];
					String strclass = nameClasse.replaceFirst(nameClasse.substring(0, 1),nameClasse.substring(0, 1).toLowerCase());
					String getNameSpaceClass = boxKeyElements.getNametargetclass().split(":")[0];

					Element rootElement = doc.createElementNS("http://www.w3.org/2001/XMLSchema", "xs:key");
					rootElement.setAttribute("name", strclass + "Key");

					Element rootElementSelector = doc.createElementNS("http://www.w3.org/2001/XMLSchema",
							"xs:selector");
					rootElementSelector.setAttribute("xpath",
							getNameSpaceClass + ":" + strclass + "References/" + getNameSpaceClass + ":" + strclass);
					Element rootElementfield = doc.createElementNS("http://www.w3.org/2001/XMLSchema", "xs:field");
					rootElementfield.setAttribute("xpath", "@id");

					classElementLowerCase.appendChild(rootElement);
					rootElement.appendChild(rootElementSelector);
					rootElement.appendChild(rootElementfield);
				}
			}
			
			// reference element
			root.appendChild(doc.createComment("keyrefs"));
			for (ShaclXsdBox boxKeyElementsRef : data) {
				for (ShaclXsdProperty propertyResource : boxKeyElementsRef.getProperties()) {

					Boolean bReferenceSource = false;
					for (ShaclXsdBox boxKeyfind : data) {
						if (propertyResource.getValue_class_property() != null) {
							if (propertyResource.getValue_class_property().equals(
									boxKeyfind.getNametargetclass().split(":")[1]) & boxKeyfind.getUseReference()) {
								bReferenceSource = true;
								break;
							}
						}
					}

					if (bReferenceSource) {
						String classnameRef = propertyResource.getValue_class_property();
						String strclassRef = classnameRef.replaceFirst(classnameRef.substring(0, 1),
								classnameRef.substring(0, 1).toLowerCase());

						Element rootElementRef = doc.createElementNS("http://www.w3.org/2001/XMLSchema", "xs:keyref");
						rootElementRef.setAttribute("name", propertyResource.getValue_path().split(":")[1] + "Keyref");
						rootElementRef.setAttribute("refer", strclassRef + "Key");

						Element rootElementSelectorRef = doc.createElementNS("http://www.w3.org/2001/XMLSchema",
								"xs:selector");
						rootElementSelectorRef.setAttribute("xpath", propertyResource.getValue_path());
						Element rootElementfieldRef = doc.createElementNS("http://www.w3.org/2001/XMLSchema",
								"xs:field");
						rootElementfieldRef.setAttribute("xpath", "@idref");

						classElementLowerCase.appendChild(rootElementRef);
						rootElementRef.appendChild(rootElementSelectorRef);
						rootElementRef.appendChild(rootElementfieldRef);
					}
				}
			}

			root.appendChild(classElementLowerCase);
		}

		/*
		 * 
		 * List of XML elements corresponding to classes Uppercase
		 *
		 */

		root.appendChild(doc.createComment("Elements corresponding to classes - Uppercase"));
		// data.sort(Comparator.comparing(ShaclXsdBox::getNametargetclass));
		for (ShaclXsdBox aboxClass : data) {
			String nameClasse = aboxClass.getNametargetclass().split(":")[1];
			Element classElement = doc.createElementNS("http://www.w3.org/2001/XMLSchema", "xs:element");
			classElement.setAttribute("name", nameClasse);
			classElement.setAttribute("type", nameClasse + "Type");
			root.appendChild(classElement);
		}

		/*
		 * 
		 * List of XML elements corresponding to classes lowerCase, as property
		 * reference
		 * 
		 */
		root.appendChild(doc.createComment("Elements corresponding to classes - lowercase"));
		List<String> elementClass = new ArrayList<>();
		for (ShaclXsdBox boxClassproperty : data) {
			Element classElementLowerCase = doc.createElementNS("http://www.w3.org/2001/XMLSchema", "xs:element");

			String strClasse = boxClassproperty.getNametargetclass().split(":")[1];
			String m = strClasse.replaceFirst(strClasse.substring(0, 1), strClasse.substring(0, 1).toLowerCase());
			elementClass.add(m);
			classElementLowerCase.setAttribute("name", m);
			if (boxClassproperty.getUseReference()) {
				classElementLowerCase.setAttribute("type", "IdReferenceType");
			} else {
				classElementLowerCase.setAttribute("type", strClasse + "Type");
			}

			root.appendChild(classElementLowerCase);

		}

		// List of XML elements corresponding to properties
		root.appendChild(doc.createComment("Elements corresponding to properties"));
		for (ShaclXsdBox boxElements : data) {

			// 1. declare a MediaObjectReferences element, pointing to corresponding type
			// list of XML elements corresponding to references
			if (boxElements.getUseReference()) {
				Element useReference = doc.createElementNS("http://www.w3.org/2001/XMLSchema", "xs:element");
				String strClass = boxElements.getNametargetclass().split(":")[1];
				String m = strClass.replaceFirst(strClass.substring(0, 1), strClass.substring(0, 1).toLowerCase());
				useReference.setAttribute("name", m + "References");
				useReference.setAttribute("type", strClass + "ReferencesType");
				root.appendChild(useReference);
			}

			/*
			 * read properties shacl
			 * 
			 * 
			 */

			for (ShaclXsdProperty rDataProperty : boxElements.getProperties()) {
				Boolean bClass = false;
				for (String cUsed : elementClass) {
					if (cUsed.equals(rDataProperty.getValue_path().split(":")[1])) {
						bClass = true;
						break;
					}
				}

				if (rDataProperty.getValue_path() != null & !bClass) {

					Element imports = doc.createElementNS("http://www.w3.org/2001/XMLSchema", "xs:element");

					imports.setAttribute("name", rDataProperty.getValue_path().split(":")[1]);

					Boolean useReferenceNodeSape = false;
					if (rDataProperty.getValue_class_property() != null) {
						for (ShaclXsdBox useReferenceClass : data) {
							if (rDataProperty.getValue_class_property()
									.equals(useReferenceClass.getNametargetclass().split(":")[1])
									& useReferenceClass.getUseReference()) {
								useReferenceNodeSape = true;
								break;
							}
						}
					}

					if (useReferenceNodeSape) {
						imports.setAttribute("type", "IdReferenceType");
					} else {
						if (rDataProperty.getValue_class_property() != null) {
							if (rDataProperty.getValue_class_property().equals("Concept")) {
								for (ShaclXsdBox aVocabulary : ConstraintsVocabulary) {
									if (aVocabulary.getNametargetclass() != null) {
										if (aVocabulary.getNametargetclass().equals(boxElements.getNametargetclass())) {
											for (ShaclXsdProperty constraintsProperty : aVocabulary.getProperties()) {
												if (rDataProperty.getValue_path()
														.equals(constraintsProperty.getValue_path())) {
													if(constraintsProperty.getValue_node() != null) {
														imports.setAttribute("type",
																constraintsProperty.getValue_node().getLabel().split(":")[1]
																		+ "Type");
													}
													
													else {
														imports.setAttribute("type", rDataProperty.getValue_class_property() + "Type");
													}
													bCtrlVocabulary = true;
												}
											}
										}
									}
								}
							} else {
								imports.setAttribute("type", rDataProperty.getValue_class_property() + "Type");
							}
						}
						if (rDataProperty.getValue_datatype() != null) {
							imports.setAttribute("type", rDataProperty.getValue_datatype().replace("xsd:", "xs:"));
						}
						if (rDataProperty.getValue_nodeKind() != null) {
							if (rDataProperty.getValue_nodeKind().equals("sh:Literal")) {

								imports.setAttribute("type", "rdfs:LiteralType");
							}
						}
					}
					root.appendChild(imports);
				}
			}
		}

		root.appendChild(doc.createComment("Root type"));
		if (isRoot != null) {
			Element simpleContextRoot = doc.createElementNS("http://www.w3.org/2001/XMLSchema", "xs:complexType");
			simpleContextRoot.setAttribute("name", isRoot+"Type");
			Element attsequence = doc.createElementNS("http://www.w3.org/2001/XMLSchema", "xs:sequence");
			for (ShaclXsdBox boxUseReference : data) {
				if (boxUseReference.getUseReference()) {
					String strClasse = boxUseReference.getNametargetclass().split(":")[1];
					String m = strClasse.replaceFirst(strClasse.substring(0, 1),
							strClasse.substring(0, 1).toLowerCase());
					Element attelementSequence = doc.createElementNS("http://www.w3.org/2001/XMLSchema", "xs:element");
					attelementSequence.setAttribute("ref", m + "References");
					attelementSequence.setAttribute("minOccurs", "0");
					attelementSequence.setAttribute("maxOccurs", "1");
					attsequence.appendChild(attelementSequence);
				}
			}
			root.appendChild(simpleContextRoot);
			simpleContextRoot.appendChild(attsequence);

		}

		root.appendChild(doc.createComment("Types corresponding to classes"));
		Boolean bSubClassOf = false;
		String subClassOf = null;
		for (ShaclXsdBox complexTypebox : data) {
			String strClasse = "";
			Element complexType = doc.createElementNS("http://www.w3.org/2001/XMLSchema", "xs:complexType");
			/*
			String classProperty = null;
			for(ShaclXsdProperty rDataClass : complexTypebox.getProperties()) {
				if(rDataClass.getValue_class_property() != null) {
					classProperty = rDataClass.getValue_class_property();
				}
			}
			
			if(classProperty != null && classProperty.equals("Concept")) {
				strClasse = complexTypebox.getLabel().split(":")[1];
			}else {*/
			strClasse = complexTypebox.getNametargetclass().split(":")[1];
			//}
			
			complexType.setAttribute("name", strClasse + "Type");
			root.appendChild(complexType);
			
			bSubClassOf = false;
			for (OntologyClass readOwlClass : owlData.getOntoClass()) {
				if (readOwlClass.getCommentRDFS() != null & readOwlClass.getClassName().equals(strClasse)) {
					Element attAnnotation = doc.createElementNS("http://www.w3.org/2001/XMLSchema", "xs:annotation");
					Element attAnnotationDocument = doc.createElementNS("http://www.w3.org/2001/XMLSchema",
							"xs:documentation");
					attAnnotationDocument.setTextContent(readOwlClass.getCommentRDFS());

					complexType.appendChild(attAnnotation);
					attAnnotation.appendChild(attAnnotationDocument);

					if (complexTypebox.getNametargetclass().split(":")[1].equals(readOwlClass.getClassName())
							&& readOwlClass.getSubClassOfRDFS() != null) {
						subClassOf = readOwlClass.getSubClassOfRDFS();
						bSubClassOf = true;
					}
				}
			}

			/*
			 * 
			 * 
			 * 
			 */
			if (complexTypebox.getProperties().size() > 0) {
				Element attsequence = doc.createElementNS("http://www.w3.org/2001/XMLSchema", "xs:sequence");
				for (ShaclXsdProperty rDataProperty : complexTypebox.getProperties()) {
					if (rDataProperty.getValue_path() != null ) {

						Element attelementSequence = doc.createElementNS("http://www.w3.org/2001/XMLSchema","xs:element");
						attelementSequence.setAttribute("ref", rDataProperty.getValue_path().split(":")[1]);
						attelementSequence.setAttribute("maxOccurs", rDataProperty.getValue_maxCount());
						attelementSequence.setAttribute("minOccurs", rDataProperty.getValue_minCount());

						if (rDataProperty.getValue_description() != null) {
							Element elementDescription = doc.createElementNS("http://www.w3.org/2001/XMLSchema","xs:annotation");
							Element attelementDescription = doc.createElementNS("http://www.w3.org/2001/XMLSchema","xs:documentation");
							attelementDescription.setTextContent(rDataProperty.getValue_description());
							attelementSequence.appendChild(elementDescription);
							elementDescription.appendChild(attelementDescription);
						} else {
							for (OntologyObjectProperty readOwlClass : owlData.getOntoOP()) {
								if (rDataProperty.getValue_path().split(":")[1]
										.equals(readOwlClass.getPropertyName())) {
									Element elementDescription = doc.createElementNS("http://www.w3.org/2001/XMLSchema",
											"xs:annotation");
									Element attelementDescription = doc
											.createElementNS("http://www.w3.org/2001/XMLSchema", "xs:documentation");
									attelementDescription.setTextContent(readOwlClass.getCommentRDFS());
									attelementSequence.appendChild(elementDescription);
									elementDescription.appendChild(attelementDescription);

								}
							}
						}
					
						attsequence.appendChild(attelementSequence);
					}
				}

				/*
				 * Si en la clase existe la propiedad SubClassOf No debe de generar la etiqueta
				 * de Secuencia.
				 */
				if (!bSubClassOf) {
					complexType.appendChild(attsequence);
				}

				/*
				 * Si existe la propiedad SubClassOf en la Ontologia Recuperamos las
				 * propriedades de cada clase para convertirlos en elementos de la propiedad
				 * SubClassOf.
				 */

				if (bSubClassOf) {
					Element complexContent = doc.createElementNS("http://www.w3.org/2001/XMLSchema",
							"xs:complexContent");
					Element extension = doc.createElementNS("http://www.w3.org/2001/XMLSchema", "xs:extension");
					extension.setAttribute("base", subClassOf + "Type");

					for (ShaclXsdProperty rDataProperty : complexTypebox.getProperties()) {
						if (rDataProperty.getValue_path() != null) {

							Element elementextension = doc.createElementNS("http://www.w3.org/2001/XMLSchema",
									"xs:element");
							elementextension.setAttribute("ref", rDataProperty.getValue_path().split(":")[1]);
							elementextension.setAttribute("minOccurs", rDataProperty.getValue_minCount());
							elementextension.setAttribute("maxOccurs", rDataProperty.getValue_maxCount());
							extension.appendChild(elementextension);

						}
					}
					complexType.appendChild(complexContent);
					complexContent.appendChild(extension);

				}

				Element attrComplex = doc.createElementNS("http://www.w3.org/2001/XMLSchema", "xs:attribute");
				attrComplex.setAttribute("name", "id");
				attrComplex.setAttribute("type", "xs:anyURI");
				attrComplex.setAttribute("use", "required");
				complexType.appendChild(attrComplex);

			} else {
				Element simpleContent = doc.createElementNS("http://www.w3.org/2001/XMLSchema", "xs:simpleContent");
				Element extension = doc.createElementNS("http://www.w3.org/2001/XMLSchema", "xs:extension");
				extension.setAttribute("base", "xs:anyURI");
				complexType.appendChild(simpleContent);
				simpleContent.appendChild(extension);
			}

		}

		root.appendChild(doc.createComment("Types corresponding to controlled vocabularies restriction"));
		// Vocabulary
		String Class = null;
		for(ShaclXsdBox shBox : data) {
			if(shBox.getNametargetclass() != null) {
				for(ShaclXsdBox shBoxVocabulary : ConstraintsVocabulary) {
					if(shBoxVocabulary.getNametargetclass() != null) {
						for(ShaclXsdProperty rdataTarget : shBox.getProperties()) {
							for(ShaclXsdProperty rProperty : shBoxVocabulary.getProperties()) {
								if (rdataTarget.getValue_path().equals(rProperty.getValue_path())) {
									if(rProperty.getValue_node() != null) {
										Element complexTypeConstraints= doc.createElementNS("http://www.w3.org/2001/XMLSchema","xs:complexType");
										Element VocabularyContente = doc.createElementNS("http://www.w3.org/2001/XMLSchema","xs:complexContent");
										Element VocabularyRestriction = doc.createElementNS("http://www.w3.org/2001/XMLSchema","xs:restriction");
										VocabularyRestriction.setAttribute("base","skos:"+rdataTarget.getValue_class_property()+ "Type");
										Element VocabularyAtt = doc.createElementNS("http://www.w3.org/2001/XMLSchema","xs:attribute");
										VocabularyAtt.setAttribute("name", "uri");
										complexTypeConstraints.setAttribute("name", rProperty.getValue_node().getLabel().split(":")[1] + "Type");
										VocabularyAtt.setAttribute("type", rProperty.getValue_node().getLabel().split(":")[1] +"EnumType");
										root.appendChild(complexTypeConstraints);
										complexTypeConstraints.appendChild(VocabularyContente);
										VocabularyContente.appendChild(VocabularyRestriction);
										VocabularyRestriction.appendChild(VocabularyAtt);
									}
								}						
							}
						}
					}
				}
			}
		}

		root.appendChild(doc.createComment("Types corresponding to references"));
		// 2. Declare the MediaObjectReferencesType, containing mediaObject elements
		// References types, at the end, after the others
		for (ShaclXsdBox complexTypeboxUseReference : data) {
			if (complexTypeboxUseReference.getUseReference()) {
				String strClasse = complexTypeboxUseReference.getNametargetclass().split(":")[1];
				Element complexTypeuseReference = doc.createElementNS("http://www.w3.org/2001/XMLSchema",
						"xs:complexType");
				complexTypeuseReference.setAttribute("name", strClasse + "ReferencesType");

				Element attsequenceUseReference = doc.createElementNS("http://www.w3.org/2001/XMLSchema",
						"xs:sequence");
				attsequenceUseReference.setAttribute("maxOccurs", "unbounded");
				attsequenceUseReference.setAttribute("minOccurs", "0");

				String m = strClasse.replaceFirst(strClasse.substring(0, 1), strClasse.substring(0, 1).toLowerCase());

				Element attelementSequence = doc.createElementNS("http://www.w3.org/2001/XMLSchema", "xs:element");
				attelementSequence.setAttribute("name", m);
				attelementSequence.setAttribute("type", strClasse + "Type");

				root.appendChild(complexTypeuseReference);
				complexTypeuseReference.appendChild(attsequenceUseReference);
				attsequenceUseReference.appendChild(attelementSequence);
			}
		}

		root.appendChild(doc.createComment("IdReferenceType"));
		// 3. Declare this complexType, always if there is at least one element using
		// references
		if (bReference) {
			Element complexIdReference = doc.createElementNS("http://www.w3.org/2001/XMLSchema", "xs:complexType");
			complexIdReference.setAttribute("name", "IdReferenceType");

			Element refdannotation = doc.createElementNS("http://www.w3.org/2001/XMLSchema", "xs:annotation");
			Element refdocument = doc.createElementNS("http://www.w3.org/2001/XMLSchema", "xs:documentation");
			refdocument.setTextContent("A link or reference to another entity record in the document.");

			Element AttributeReference = doc.createElementNS("http://www.w3.org/2001/XMLSchema", "xs:attribute");
			AttributeReference.setAttribute("name", "idref");
			AttributeReference.setAttribute("type", "xs:anyURI");
			AttributeReference.setAttribute("use", "required");

			Element AttributeReferenceNottation = doc.createElementNS("http://www.w3.org/2001/XMLSchema",
					"xs:annotation");

			Element AttributeReferenceNottationDocument = doc.createElementNS("http://www.w3.org/2001/XMLSchema",
					"xs:documentation");
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
