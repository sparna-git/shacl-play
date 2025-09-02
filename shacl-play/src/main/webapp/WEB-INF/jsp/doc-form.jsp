<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!-- setup the locale for the messages based on the language in the session -->
<fmt:setLocale value="${sessionScope['fr.sparna.rdf.shacl.shaclplay.SessionData'].userLocale.language}"/>
<fmt:setBundle basename="fr.sparna.rdf.shacl.shaclplay.i18n.shaclplay"/>

<c:set var="data" value="${requestScope['DocFormData']}" />

<html>
	<head>
		<title><fmt:message key="window.app" /></title>
		<link rel="canonical" href="https://shacl-play.sparna.fr/play/doc" />
		
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">

	    <!-- Font Awesome -->
	    <link rel="stylesheet" href="<c:url value="/resources/fa/css/all.min.css" />">
		
		<link rel="stylesheet" href="<c:url value="/resources/MDB-Free/css/bootstrap.min.css" />">
		<link rel="stylesheet" href="<c:url value="/resources/MDB-Free/css/mdb.min.css" />">
		<link rel="stylesheet" href="<c:url value="/resources/jasny-bootstrap/jasny-bootstrap.min.css" />" />
		<link rel="stylesheet" href="<c:url value="/resources/css/shacl-play.css" />" />	
			
	</head>
	<body>

	<jsp:include page="include/navbar.jsp">
		<jsp:param name="active" value="doc"/>
	</jsp:include>

    <div class="container-fluid">
    
    	<div class="row justify-content-md-center">
            <div class="col-6">
 
	    		<div class="messages">
					<c:if test="${not empty data.errorMessage}">
						<div class="alert alert-danger" role="alert">
							<button type="button" class="close" data-dismiss="alert" aria-label="Close"><span aria-hidden="true">&times;</span></button>
							Error
							${data.errorMessage}
						</div>
					</c:if>
				</div>
				
				<h1 class="display-3"><fmt:message key="doc.title" /></h1>	
				
				<div class="form-shortdesc">
					<p>This documentation generation utility prints an <em>application profile specified in SHACL</em>. It supports a subset of SHACL constraints. The generated documentation describes all the properties allowed on each class/shape of the application profile, and includes a diagram, namespace table, introduction, 
					   and some metadata at the top of the document. Detailed documentation is available <a href="#documentation">below</a>.</p>
					<p>Good examples of how the generated documentation looks like are the <a href="https://europarl.github.io/org-ep/">European Parliament application profiles</a> or the <a href="https://rdafr.fr/profil-application/">RDA-FR profile</a>.</p>
					<p>This documentation generation utility also supports <a href="#documentation-dataset">SHACL-based dataset statistics documentation generation</a></p>
				</div>
	 
			  	<form id="upload_form" action="doc" method="POST" enctype="multipart/form-data" class="form-horizontal">
  
					  <h2><i class="fal fa-shapes"></i>&nbsp;&nbsp;<fmt:message key="doc.shapes.title" /></h2>
					  <!-- Include shapes blockquote -->
					  <%@ include file="include/shapes-blockquote.jsp" %>
					  
					  <h2><i class="fal fa-tools"></i>&nbsp;&nbsp;<fmt:message key="blockquote.options.title" /></h2>
				      <blockquote class="blockquote bq-warning">
				      	<!-- Language -->
				      	<div class="form-group row">
							<label for="language" class="col-sm-3 col-form-label">
								<fmt:message key="doc.options.language"/>				    
							</label>
						    <div class="col-sm-9">
						    	<input list="languageOption" name="language" style="width:4em;" value="en">
						    	<small class="form-text text-muted">
								  <fmt:message key="doc.options.language.help"/>
								</small>
						    </div>									    
						</div>

				      	<!-- Logo -->
						<div class="form-group row">
						    <div class="col-sm-12">
								<div class="form-check">
									<input class="form-check-input" type="checkbox" id="inputLogoCheckbox" name="inputLogoCheckbox"/>
									<label class="form-check-label" for="inputLogoCheckbox">
										<fmt:message key="doc.options.logo" />
									</label>
									<div class="col-sm-9">
								      	<input type="text" 
								      			class="form-control" id="inputLogo" name="inputLogo" 
								      			oninput="inputtextlogo()"
								      			placeholder="<fmt:message key="doc.options.logo.placeholder"/>"
								      			disabled
								      		>
							      
										<small class="form-text text-muted">
											<fmt:message key="doc.options.logo.help" />
										</small>
									</div>
								</div>
							</div>
						</div>
				      	<!-- Diagram -->
				      	<div class="form-group row">
					      	<div class="col-sm-12">
						      	<div class="form-check">
								  <input class="form-check-input" type="checkbox" id="includeDiagram" name="includeDiagram" />
								  <label class="form-check-label" for="includeDiagram">
								    <fmt:message key="doc.options.includeDiagram" />
								  </label>
								  <small class="form-text text-muted">
									<fmt:message key="doc.options.includeDiagram.help" />
								  </small>
								</div>
							</div>
						</div>
						<!-- Split Diagram -->
						<div class="form-group row">
							<div class="col-sm-12">
								<div class="form-check">
									<input class="form-check-input" type="checkbox" id="sectionDiagram" name="sectionDiagram" checked/>
									<label class="form-check-label" for="sectionDiagram">
								    	<fmt:message key="doc.options.sectionDiagram" />								  		
								  	</label>
								  	<small class="form-text text-muted">
										<fmt:message key="doc.options.sectionDiagram.help" />
								  </small>						
								</div>
							</div>
						</div>
						
						<!-- Hide Properties -->
						<div class="form-group row">
					      	<div class="col-sm-12">
						      	<div class="form-check">
								  <input class="form-check-input" type="checkbox" id="hideProperties" name="hideProperties" />
								  <label class="form-check-label" for="hideProperties">
								    <fmt:message key="doc.options.hideProperties" />
								  </label>
								  <small class="form-text text-muted">
									<fmt:message key="doc.options.hideProperties.help" />
								  </small>
								</div>
							</div>
						</div>						
						<!-- Output format option -->
						<div class="form-group row">
							<label for="format" class="col-sm-3 col-form-label">
								<fmt:message key="doc.options.format" />					    
							</label>
						    <div class="col-sm-4">
					    		<select class="form-control" id="format" name="format" >
					    			<option value="HTML">HTML</option>
					    			<option value="PDF">PDF</option>
					    			<option value="XML">XML</option>					    			
							    </select>
						    </div>									    
						 </div>
						 
						<!--   
						<div class="form-group row">
						      	<div class="col-sm-12">
							      	<div class="form-check">
									  <input class="form-check-input" type="checkbox" id="printPDF" name="printPDF" onclick='chkbPDFClick(this)' />
									  <label class="form-check-label" for="printPDF">
									    <fmt:message key="doc.options.printPDF" />
									  </label>
									  <small class="form-text text-muted">
										<fmt:message key="doc.options.printPDF.help" />
									  </small>
									</div>
								</div>
						</div>
						 -->
					  </blockquote>
					  
				    <button type="submit" id="validate-button" class="btn btn-info btn-lg"><fmt:message key="doc.submit" /></button>			  	
			  	</form>
 				
 				<!-- Documentation -->	
				<div style="margin-top:3em;">
					<h3 id="documentation">Documentation</h3>
					<p>Note : the diagram generation is <a href="draw#documentation">documented in a separate page</a>.<p/>
					<h4 id="Sample-file">Sample file</h4>
					<p> 
					   To test, and to better understand how the documentation generation works you can download this <a href="<c:url value="/resources/example/PersonCountry.ttl"/>">turtle example of an application profile specified in SHACL</a>
					   , or the corresponding <a href="<c:url value="/resources/example/PersonCountry.xlsx"/>">Excel file</a> This Excel file can be converted in SHACL using 
					   the <a href="https://skos-play.sparna.fr/play/convert" target="_blank">SKOS Play xls2rdf conversion tool</a>. All the details about the conversion rules 
					   are documented in the converter page. This documentation focuses on which SHACL constraints are used to produce the documentation.					   					
					</p>


					<div style="margin-top:2em;">
						<h4 id="ontology">owl:Ontology header</h4>
						<p>The documentation generation reads the following properties on a owl:Ontology entity in the SHACL file.</p>
						
						<table class="table table-bordered">
							<thead>
								<tr align="center">
									<th scope="col">Property</th>
								    <th scope="col">Expected value</th>
								    <th scope="col" width="auto">Required</th>
								    <th scope="col">Description</th>
								</tr>
							</thead>
							<tbody>
								<tr>
									<th scope="row" width="30%"><code>dcterms:title</code> if present, or <code>rdfs:label</code></th>
									<td>xsd:string</td>
									<td>Yes</td>
									<td class="text-break">Generates the title of the document</td>
								</tr>
								<tr>
									<th scope="row"><code>dcterms:abstract</code> (+ <code>rdfs:comment</code>)</th>
									<td>xsd:string</td>
									<td>No</td>
									<td class="text-break">Generates an <b>Abstract</b> section in the documentation, if present. If dcterms:abstract values is null, use rdfs:comment. Content is interpreted as Markdown.</td>
							  	</tr>
							  	<tr>
							  		<th scope="row"><code>dcterms:description</code></th>
							  		<td>xsd:string</td>
							  		<td>No</td>
							  		<td class="text-break">Generates a <b>Description</b> section in the generated documentation, if present. Content is interpreted as Markdown.</td>
							  	</tr>
							  	<tr>
							  		<th scope="row"><code>adms:versionNodes</code></th>
							  		<td>xsd:string</td>
							  		<td>No</td>
							  		<td class="text-break">Generates a <b>Release notes</b> section at the end of the documentation, if present. Content is interpreted as Markdown.</td>
							  	</tr>
							  	<tr>
							  		<th scope="row"><code>dcat:distribution</code> <br/>(+ <code>dcterms:format</code> + <code>dcat:downloadURL</code>)</th>
							  		<td>IRI or blank node</td>
							  		<td>No</td>
							  		<td class="text-break">Generates download links for the various distributions of the application profiles. Each <code>dcat:distribution</code>
							  		must point to a resource (IRI or blank node) that have both a <code>dcterms:format</code> indicating the format of the file and a
							  		<code>dcat:downloadURL</code> giving the download URL. Supported values for dcterms:format are:
							  		<ul>
							  			<li>https://www.iana.org/assignments/media-types/text/turtle for Turtle distribution</li>
							  			<li>https://www.iana.org/assignments/media-types/application/rdf+xml for RDF/XML distribution</li>
							  			<li>https://www.iana.org/assignments/media-types/application/n-triples for NTriple distribution </li>
							  			<li>https://www.iana.org/assignments/media-types/application/ld+json for JSON-LD distribution</li>
							  		</ul>
							  		This can be repeated to point to multiple distributions.
							  		</td>
							  	</tr>
							  	<tr>
							  		<th scope="row"><code>foaf:depiction</code></th>
							  		<td>IRI</td>
							  		<td>No</td>
							  		<td class="text-break">If present, refer to the URL of a diagram giving a depiction of the application profile, that will 
							  		be included in the "Diagrams" section of the generated documentation. This can be repeated to include more than one diagram.
							  		See the <a href="draw#documentation">diagram generation documentation</a> for more information.</td>
							  	</tr>
							</tbody>
						</table>
						
						<p>The following properties are also read on the owl:Ontology to populate the metadata header at the top of the document.</p>
						
						<table class="table table-bordered">
							<thead>
								<tr align="center">
									<th scope="col">Property</th>
								    <th scope="col">Expected value</th>
								    <th scope="col" width="auto">Required</th>
								    <th scope="col">Description</th>
								</tr>
							</thead>
							<tbody>
							  	<tr>
							  		<th scope="row"><code>dcterms:modified</code></th>
							  		<td>xsd:dateTime</td>
							  		<td>No</td>
							  		<td class="text-break">Shown in the header of the generated documentation</td>
							  	</tr>
							  	<tr>
							  		<th scope="row"><code>dcterms:created</code></th>
							  		<td>xsd:dateTime</td>
							  		<td>No</td>
							  		<td class="text-break">Shown in the header of the generated documentation</td>
							  	</tr>
							  	<tr>
							  		<th scope="row"><code>dcterms:creator</code> (+ <code>rdfs:label</code>)</th>
							  		<td>IRI or Literal</td>
							  		<td>No</td>
							  		<td class="text-break">Shown in the header of the generated documentation. 
							  		If the value is an IRI, and if it has an rdfs:label, use this as the label of the link; otherwise the URI will be shown.</td>
							  	</tr>
							  	<tr>
							  		<th scope="row"><code>dcterms:dateCopyrighted</code></th>
							  		<td>xsd:dateTime</td>
							  		<td>No</td>
							  		<td class="text-break">Shown in the header of the generated documentation</td>
							  	</tr>
							  	<tr>
							  		<th scope="row"><code>dcterms:issued</code></th>
							  		<td>xsd:dateTime</td>
							  		<td>No</td>
							  		<td class="text-break">Shown in the header of the generated documentation</td>
							  	</tr>							  	
							  	<tr>
							  		<th scope="row"><code>dcterms:license</code> (+ <code>rdfs:label</code>)</th>
							  		<td>IRI or Literal</td>
							  		<td>No</td>
							  		<td class="text-break">Shown in the header of the generated documentation. 
							  		If the value is an IRI, and if it has an rdfs:label, use this as the label of the link; otherwise the URI will be shown.</td>
							  	</tr>							  	
							  	<tr>
							  		<th scope="row"><code>dcterms:publisher</code> (+ <code>rdfs:label</code>)</th>
							  		<td>IRI or Literal</td>
							  		<td>No</td>
							  		<td class="text-break">Shown in the header of the generated documentation. 
							  		If the value is an IRI, and if it has an rdfs:label, use this as the label of the link; otherwise the URI will be shown.</td>
							  	</tr>
							  	<tr>
							  		<th scope="row"><code>dcterms:rightsHolder</code> (+ <code>rdfs:label</code>)</th>
							  		<td>IRI or Literal</td>
							  		<td>No</td>
							  		<td class="text-break">Shown in the header of the generated documentation. 
							  		If the value is an IRI, and if it has an rdfs:label, use this as the label of the link; otherwise the URI will be shown.</td>
							  	</tr>
							  	<tr>
							  		<th scope="row"><code>owl:versionInfo</code></th>
							  		<td>xsd:string</td>
							  		<td>No</td>
							  		<td class="text-break">Shown in the header of the generated documentation</td>
							  	</tr>
							  	<tr>
							  		<th scope="row"><code>doap:repository</code></th>
							  		<td>IRI</td>
							  		<td>No</td>
							  		<td class="text-break">Shown in the header of the generated documentation</td>
							  	</tr>
							</tbody>
						</table>
					</div>
					
					<div style="margin-top:2em;">
						<h4 id="ontology-example">Example</h4>
						
						<br/>
						<p>This is an example of metadata on the owl:Ontology:</p>
						<br/>
						<!-- OWL -->
						<img src="<c:url value="/resources/img/shacl_doc_owl.png"/>" width="100%"/>
						<br/>
						<br/>
						This generates the following output:
						<br/>
						<br/>
						<img src="<c:url value="/resources/img/shacl_doc_header.png"/>" width="100%"/>
						<br/>						
					</div>
					
					<div style="margin-top:2em;">
						<h4 id="prefix">Prefixes table</h4>
						<p>The prefixes of the SHACL file are inserted in a ""Namespaces section at the top of the documentation.
						For example the following prefixes:</p>
						<img src="<c:url value="/resources/img/shacl_doc_prefixes_input.png"/>" width="80%"/>
						<br/>
						<br/>
						<p>Will generate the corresponding output table:</p>
						<img src="<c:url value="/resources/img/shacl_doc_prefixes.png"/>" width="80%" />
						
					</div>
					
					<div style="margin-top:2em;">
						<h4 id="nodeShape">Documentation of Node Shapes</h4>
						<p>The following properties are read on each sh:NodeShape to populate the header of each section of the generated documentation.</p>
						
						<table class="table table-bordered">
							<thead>
								<tr align="center">
									<th scope="col">Property</th>
								    <th scope="col">Expected value</th>
								    <th scope="col" width="auto">Required</th>
								    <th scope="col">Description</th>
								</tr>
							</thead>
							<tbody>
								<tr>
									<th scope="row" width="30%"><code>rdfs:label</code> (or <code>skos:prefLabel</code> or <code>rdfs:label</code> on class)</th>
									<td>rdf:langString</td>
									<td>Yes</td>
									<td class="text-break">Label of the NodeShape used as the title of the section in the documentation. If not provided, and if sh:targetClass points to a URI, then a <code>skos:prefLabel</code> or <code>rdfs:label</code>
							  		are searched on the target class.</td>
								</tr>
								<tr>
									<th scope="row"><code>rdf:type rdfs:Class</code></th>
									<td>IRI</td>
									<td>No</td>
									<td class="text-break">If the NodeShape is also an instance of <code>rdfs:Class</code>, then its URI will be displayed under the section title.</td>
							  	</tr>
								<tr>
									<th scope="row" width="30%"><code>rdfs:comment</code> (or <code>skos:definition</code> or <code>rdfs:comment</code> on class)</th>
									<td>rdf:langString</td>
									<td>No</td>
									<td class="text-break">Small descriptive paragraph under the section title. If not provided, and if sh:targetClass points to a URI, then a <code>skos:definition</code> or <code>rdfs:comment</code>
							  		are searched on the target class.</td>
								</tr>
								<tr>
									<th scope="row"><code>sh:targetClass</code> (can be repeated)</th>
									<td>IRI</td>
									<td>No</td>
									<td class="text-break">The class to which the NodeShape applies. This can be repeated multiple times</td>
							  	</tr>
								  <tr>
									<th scope="row"><code>sh:targetSubjectsOf</code> or <code>sh:targetObjectsOf</code></th>
									<td>IRI</td>
									<td>No</td>
									<td class="text-break">The property for which this shape will target the subjects or the objects.</td>
							  	</tr>
							  	<tr>
									<th scope="row"><code>sh:target/sh:select</code></th>
									<td>xsd:string</td>
									<td>No</td>
									<td class="text-break">If the target of the Shape is provided as a SPARQL query, it will be included in the section header.</td>
							  	</tr>
							  	<tr>
									<th scope="row"><code>sh:closed</code></th>
									<td>xsd:boolean</td>
									<td>No</td>
									<td class="text-break">Indicates if the shape is closed.</td>
							  	</tr>
								<tr>
									<th scope="row"><code>sh:pattern</code></th>
									<td>xsd:string</td>
									<td>No</td>
									<td class="text-break">Regex specifying the URI pattern that the targets of the NodeShape must conform to.</td>
							  	</tr>
							  	<tr>
							  		<th scope="row"><code>skos:example</code></th>
							  		<td>IRI or xsd:string</td>
							  		<td>No</td>
							  		<td class="text-break">Example of an IRI for a target of this NodeShape. Can be given as an IRI reference or a string.</td>
							  	</tr>
							  	<tr>
							  		<th scope="row"><code>sh:order</code></th>
							  		<td>xsd:integer</td>
							  		<td>No</td>
							  		<td class="text-break">Sections of the generated documentation are sorted according to sh:order on sh:NodeShape, or by label if not present.</td>
							  	</tr>
							  	<tr>
							  		<th scope="row"><code>rdfs:subClassOf</code> or <code>sh:targetClass/rdfs:subClassOf</code></th>
							  		<td>IRI</td>
							  		<td>No</td>
							  		<td class="text-break">
							  			If the NodeShape itself or its target class are subclasses of another class, then this is indicated in the header.
							  			<br />
							  			Also note that the properties table will be populated with property shapes from the super classes, in different sections "� la"
							  			 schema.org.							  		
							  		</td>
							  	</tr>
								<tr>
									<th scope="row"><code>sh:sparql/dct:description</code></th>
									<td>rdf:langString</td>
									<td>No</td>
									<td class="text-break">The description of a business rule associated to the node shape. This will be displayed below the properties table.</td>
							  	</tr>			  	
							</tbody>
						</table>

						<p>This is how it can typically look like in SHACL:</p>
						<img src="<c:url value="/resources/img/shacl_doc_nodeshape_shacl.png"/>" width="100%"/>
						<br/>
						<br/>
						<p>This generates the following output:</p>
						<img src="<c:url value="/resources/img/nodeshape_person.png"/>" width="100%"/>
						<br/>
						<br/>
						<img src="<c:url value="/resources/img/nodeshape_country.png"/>" width="100%"/>
						<br/>
						<br/>
						<img src="<c:url value="/resources/img/nodeshape_referenceOrganization.png"/>" width="100%"/>
						<br/>
						<br/>
						<img src="<c:url value="/resources/img/nodeshape_referencePerson.png"/>" width="100%"/>
						<br/>
						<br/>
					</div>
					
					<div style="margin-top:2em;">
						<h4 id="propertyShape">Documentation of Property Shapes</h4>
						<p>The following properties are read on each sh:PropertyShape to populate the properties table in the generated documentation.</p>
						
						<table class="table table-bordered">
							<thead>
								<tr align="center">
									<th scope="col">Property</th>
								    <th scope="col">Expected value</th>
								    <th scope="col" width="auto">Required</th>
								    <th scope="col">Description</th>
								</tr>
							</thead>
							<tbody>
								<tr>
									<th scope="row" width="30%"><code>sh:path</code></th>
									<td>IRI</td>
									<td>Yes</td>
									<td class="text-break">Property or property path - each property shape generates one line in the table.</td>
								</tr>
								<tr>
									<th scope="row"><code>sh:name</code> (or <code>skos:prefLabel</code> or <code>rdfs:label</code>)</th>
									<td>rdf:langString</td>
									<td>No</td>
									<td class="text-break">Used to display the name of the property. If not provided, and if sh:path points to a URI, then a <code>skos:prefLabel</code> or <code>rdfs:label</code>
							  		are searched on the property URI indicated in the sh:path. This implies the SHACL file also contains the OWL definition. Otherwise, the column will be empty.</td>
							  	</tr>
							  	<tr>
							  		<th scope="row"><code>sh:description</code> (or <code>skos:definition</code> or <code>rdfs:comment</code>)</th>
							  		<td>rdf:langString</td>
							  		<td>No</td>
							  		<td class="text-break">Populates the <b>Description</b> column of the table. If not provided, and if sh:path points to a URI, then a <code>skos:definition</code> or <code>rdfs:comment</code>
							  		is searched on the property URI indicated in the sh:path. This implies the SHACL file also contains the OWL definition. Otherwise the column will be empty.</td>
							  	</tr>
							  	<tr>
							  		<th scope="row"><code>sh:minCount</code> / <code>sh:maxCount</code></th>
							  		<td>xsd:integer</td>
							  		<td>No</td>
							  		<td class="text-break">Used to populate the <b>Cardinality</b> column of the table</td>
							  	</tr>
							  	<tr>
							  		<th scope="row"><code>sh:node</code></th>
							  		<td>IRI (of a NodeShape)</td>
							  		<td>One of sh:node, sh:class, sh:nodeKind, sh:datatype, sh:or, sh:hasValue must be provided</td>
							  		<td class="text-break">Used to populate the <b>Expected value</b> column, see below.</td>
							  	</tr>
							  	<tr>
							  		<th scope="row"><code>sh:class</code></th>
							  		<td>IRI (of a class)</td>
							  		<td>One of sh:node, sh:class, sh:nodeKind, sh:datatype, sh:or, sh:hasValue must be provided.</td>
							  		<td class="text-break">Used to populate the <b>Expected value</b> column, see below.</td>
							  	</tr>
							  	<tr>
							  		<th scope="row"><code>sh:nodeKind</code></th>
							  		<td>either `sh:IRI` or `sh:Literal`</td>
							  		<td>One of sh:node, sh:class, sh:nodeKind, sh:datatype, sh:or, sh:hasValue must be provided.</td>
							  		<td class="text-break">Used to populate the <b>Expected value</b> column, see below.</td>
							  	</tr>
							  	<tr>
							  		<th scope="row"><code>sh:datatype</code></th>
							  		<td>IRI (of a datatype)</td>
							  		<td>One of sh:node, sh:class, sh:nodeKind, sh:datatype, sh:or, sh:hasValue must be provided.</td>
							  		<td class="text-break">Used to populate the <b>Expected value</b> column, see below.</td>
							  	</tr>
							  	<tr>
							  		<th scope="row"><code>sh:hasValue</code></th>
							  		<td>any literal or IRI</td>
							  		<td>One of sh:node, sh:class, sh:nodeKind, sh:datatype, sh:or, sh:hasValue must be provided.</td>
							  		<td class="text-break">Used to populate the <b>Expected value</b> column, see below.</td>
							  	</tr>
							  	<tr>
							  		<th scope="row"><code>sh:or</code></th>
							  		<td>RDF List of blank nodes with a <code>sh:node</code></td>
							  		<td>One of sh:node, sh:class, sh:nodeKind, sh:datatype, sh:or, sh:hasValue must be provided.</td>
							  		<td class="text-break">Used to populate the <b>Expected value</b> column, see below.</td>
							  	</tr>
							  	<tr>
							  		<th scope="row"><code>sh:in</code></th>
							  		<td>RDF List of values</td>
							  		<td>No</td>
							  		<td class="text-break">Used to indicate the possible list of values, as an additionnal information in the <b>Expected value</b> column.</td>
							  	</tr>
							  	<tr>
							  		<th scope="row"><code>sh:order</code></th>
							  		<td>xsd:integer</td>
							  		<td>No</td>
							  		<td class="text-break">Lines of the generated table are sorted according to <code>sh:order</code>.</td>
							  	</tr>
							  	
							</tbody>
						</table>
						
						<p>The <b>Expected value</b> column is generated by looking at the following properties in order of precedence :</p>
						<ul>
							<li>Use <code>sh:hasValue</code> if present.</li>
							<li>Otherwise use <code>sh:class</code></li>
							<li>Otherwise use <code>sh:node</code></li>
							<li>Otherwise use <code>sh:datatype</code></li>
							<li>Otherwise use <code>sh:nodeKind</code></li>	
							<li>Otherwise use <code>sh:or</code>; in this case, get the list items and read <code>sh:node</code> on each of them.</li>
							<li>Otherwise the column is left empty.</li>
						</ul>
						<p>Additionally, if <code>sh:in</code> is present, it is inserted as an additional information in the Expected value column.</p>
						<p>This generates the following output:</p>
						<img src="<c:url value="/resources/img/shacl_doc_properties.png"/>" width="100%"/>
						<br/>
						<br/>
						<img src="<c:url value="/resources/img/shacl_doc_properties_2.png"/>" width="100%"/>
						<br/>
						<br/>
						<p>Additionally, if <code>skos:example</code> is present, one new example column show in the properties table.</p>
						<img src="<c:url value="/resources/img/shacl_doc_properties_3.png"/>" width="100%"/>
					</div>
					
				</div>	<!-- end div documentation -->		

				<div style="margin-top:3em;">
					<h3 id="documentation-dataset">Dataset statistics documentation</h3>
					<h4 id="dataset-statistics-intro">Introduction</h4>
					<p>A variant of this documentation generation utility generates a documentation of a <em>dataset statistics information, based on a certain SHACL
						profile.</em> This documentation is useful to provide information about the distribution of the data in the dataset. The following displayed are printed:
					</p>
					<ul>
						<li>Total number of triples in the dataset</li>
						<li>Number of instances of each shape/class</li>
						<li>Number of occurrences of each property shape</li>
						<li>Number of distinct values of each property shape</li>
						<li>For properties with a small number of values, the number of occurrences of each value, in a pie-chart diagram</li>
					</ul>
					<p>These counts are produced by the <a href="generate">SHACL generation algorithm</a>, following <a href="generate#statistics-documentation">this structure</a>,
						which is an extension of the VOID vocabulary.</p>
					<h4 id="how-to-use">How to use it</h4>
					<p>To trigger this feature, you need to upload a file that encodes a SHACL specification, PLUS some statistics information. Precisely it needs to contain :</p>
					<ul>
						<li>"node shape partitions" : resources (optionaly of type <code>void:Dataset</code>), with a <code>dcterms:conformsTo</code> predicate pointing a NodeShape, and with a <code>void:entities</code> predicate,
						holding an integer value, which expresses the <strong>number of targets of the node shape</strong>.</li>
						<li>"property shape partitions" : resources (optional of type <code>void:Dataset</code>), that is itself the object of a <code>void:propertyPartition</code> from 
						a "node shape partition" entity, and that is the subject of :
						<ul>
							<li>a <code>dcterms:conformsTo</code> pointing to a property shape.</li>
							<li>a <code>void:triples</code> predicate holding an integer value, and representing the <strong>number of occurrences</strong> of the property shape path.</li>
							<li>a <code>void:distinctObjects</code> predicate holding an integer value, and representing the <strong>number of distinct values</strong> of the property shape path.</li>
						</ul>
					</ul>
					<h4 id="sample-file">Sample file</h4>
					<p>To test this feature, you can use this <a href="">provided sample file</a>.</p>
					<h4 id="results">What it does</h4>
					<h5>Documentation header</h5>
					<p>...</p>
					<h5>Documentation section</h5>
					<p>...</p>
					<h5>Properties table</h5>
					<p>...</p>
				</div>
			</div>
		</div>

    </div><!-- /.container-fluid -->

			
	<jsp:include page="include/footer.jsp"></jsp:include>
	
	<!-- SCRIPTS -->
    <!-- JQuery -->
    <script type="text/javascript" src="<c:url value="/resources/MDB-Free/js/jquery.min.js" />"></script>
    <!-- Bootstrap tooltips -->
    <script type="text/javascript" src="<c:url value="/resources/MDB-Free/js/popper.min.js" />"></script>
    <!-- Bootstrap core JavaScript -->
    <script type="text/javascript" src="<c:url value="/resources/MDB-Free/js/bootstrap.min.js" />"></script>
    <!-- MDB core JavaScript -->
    <script type="text/javascript" src="<c:url value="/resources/MDB-Free/js/mdb.min.js" />"></script>
	
    <script type="text/javascript" src="<c:url value="/resources/jasny-bootstrap/jasny-bootstrap.min.js" />"></script>

    <!-- anchorjs -->
    <script src="https://cdn.jsdelivr.net/npm/anchor-js/anchor.min.js"></script>
    
    <script>
    	$(document).ready(function () {
	    	$('#htmlOrRdf a').click(function (e) {
	    	  e.preventDefault();
	    	  $(this).tab('show')
	    	});
	    	
	        // Initialize CodeMirror editor and the update callbacks
	        var sourceText = document.getElementById('text');
	        var editorOptions = {
	          mode: 'text/html',
	          tabMode: 'indent'
	        };
	        
	        // CodeMirror commented for now
	        // var editor = CodeMirror.fromTextArea(sourceText, editorOptions);
	        // editor.on("change", function(cm, event) { enabledInput('text'); });
    	});
    </script>
    <script>
    	inputLogoCheckbox.onclick = function(){
    		  if(inputLogoCheckbox.checked){
    		    document.getElementById("inputLogo").disabled = false;
    		  }
    		  
    		  if(!inputLogoCheckbox.checked){
    		    document.getElementById("inputLogo").disabled = true;
    		  }
    		  
    		}
    </script>
    
    <!-- API Anchor -->
    <script>
		anchors.options = {
			  icon: '#'
			};
		anchors.options.placement = 'left';
		anchors.add();		
	</script>
  </body>
</html>