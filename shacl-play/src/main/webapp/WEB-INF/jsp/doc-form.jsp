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
		
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">

	    <!-- Font Awesome -->
	    <link rel="stylesheet" href="<c:url value="/resources/fa/css/all.min.css" />">
		
		<link rel="stylesheet" href="<c:url value="/resources/MDB-Free/css/bootstrap.min.css" />">
		<link rel="stylesheet" href="<c:url value="/resources/MDB-Free/css/mdb.min.css" />">
		<link rel="stylesheet" href="<c:url value="/resources/jasny-bootstrap/jasny-bootstrap.min.css" />" />
		<link rel="stylesheet" href="<c:url value="/resources/css/shacl-play.css" />" />	
			
		
		<script type="text/javascript">
			
			function enabledShapeInput(selected) {
				document.getElementById('sourceShape-' + selected).checked = true;
				document.getElementById('inputShapeUrl').disabled = selected != 'inputShapeUrl';
				document.getElementById('inputShapeCatalog').disabled = selected != 'inputShapeCatalog';
				document.getElementById('inputShapeFile').disabled = selected != 'inputShapeFile';
				document.getElementById('inputShapeInline').disabled = selected != 'inputShapeInline';
			}

	    </script>
		
		
	</head>
	<body>

	<jsp:include page="navbar.jsp">
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
	 
			  	<form id="upload_form" action="doc" method="POST" enctype="multipart/form-data" class="form-horizontal">
				      
					  

						  <h2><i class="fal fa-shapes"></i>&nbsp;&nbsp;<fmt:message key="doc.shapes.title" /></h2>
						  <blockquote class="blockquote bq-primary">		  
						  
						      <div class="form-group row">
		
							    <label for="inputShapeFile" class="col-sm-3 col-form-label">
							    
							    	<input
											type="radio"
											name="shapesSource"
											id="sourceShape-inputShapeFile"
											value="file"
											checked="checked"
											onchange="enabledShapeInput('inputShapeFile')" />
							    	<fmt:message key="doc.shapes.upload" />
							    
							    </label>
							    <div class="col-sm-9">
							    		<div class="fileinput fileinput-new input-group" data-provides="fileinput">
										  <div class="form-control" data-trigger="fileinput" id="inputShapeFile">
										    <i class="fal fa-upload"></i><span class="fileinput-filename with-icon"></span>
										  </div>
										  <span class="input-group-append">
										    <span class="input-group-text fileinput-exists" data-dismiss="fileinput">
										      <fmt:message key="doc.shapes.upload.remove" />
										    </span>
										
										    <span class="input-group-text btn-file">
										      <span class="fileinput-new"><fmt:message key="doc.shapes.upload.select" /></span>
										      <span class="fileinput-exists"><fmt:message key="doc.shapes.upload.change" /></span>
										      <input type="file" name="inputShapeFile" multiple onchange="enabledShapeInput('inputShapeFile')">
										    </span>
										  </span>
										</div>
										<small class="form-text text-muted">
											  <fmt:message key="doc.shapes.upload.help" />
									  </small>
							    </div>
							  </div>
						      
						      <c:if test="${not empty data.catalog.entries}">
							      <div class="form-group row">	
								    <label for="inputShapeCatalog" class="col-sm-3 col-form-label">
								    
								    	<input
												type="radio"
												name="shapesSource"
												id="sourceShape-inputShapeCatalog"
												value="catalog"
												onchange="enabledShapeInput('inputShapeCatalog')" />
								    	<fmt:message key="doc.shapes.catalog" />					    
								    </label>
								    <div class="col-sm-9">
								    		<select class="form-control" id="inputShapeCatalog" name="inputShapeCatalog" onchange="enabledShapeInput('inputShapeCatalog');">
										      	<c:forEach items="${data.catalog.entries}" var="entry">
										      		<option value="${entry.id}">${entry.title}</option>
										      	</c:forEach>
										    </select>
										    <small class="form-text text-muted">
												  <fmt:message key="doc.shapes.catalog.help" />
										    </small>
								    </div>
								  </div>
							  </c:if>
							  
							  <div class="form-group row">
							    <label for="inputShapeUrl" class="col-sm-3 col-form-label">
							    
							    	<input
											type="radio"
											name="shapesSource"
											id="sourceShape-inputShapeUrl"
											value="url"
											onchange="enabledShapeInput('inputShapeUrl')" />
							    	<fmt:message key="doc.shapes.url" />
							    </label>
							    <div class="col-sm-9">
							      <input 
							      	type="text"
							      	class="form-control"
							      	id="inputShapeUrl"
							      	name="inputShapeUrl"
							      	placeholder="<fmt:message key="doc.shapes.url.placeholder" />"
							      	onkeypress="enabledShapeInput('inputShapeUrl');"
							      	onchange="enabledShapeInput('inputShapeUrl')"
							      >
							      <small class="form-text text-muted">
									  <fmt:message key="doc.shapes.url.help" />
							    </small>
							    </div>
							  </div>
							  <div class="form-group row">
							    <label for="inputShapeInline" class="col-sm-3 col-form-label">
							    
							    	<input
											type="radio"
											name="shapesSource"
											id="sourceShape-inputShapeInline"
											value="inline"
											onchange="enabledShapeInput('inputShapeInline')" />
							    	<fmt:message key="doc.shapes.inline" />
							    </label>
							    <div class="col-sm-9">
							      <textarea 
							      	class="form-control"
							      	id="inputShapeInline"
							      	name="inputShapeInline"
							      	rows="5"
							      	onkeypress="enabledShapeInput('inputShapeInline');"
							      	onpaste="enabledShapeInput('inputShapeInline');"
							      ></textarea>
							      <small class="form-text text-muted">
									  <fmt:message key="doc.shapes.inline.help" />
								  </small>
							    </div>	
						      </div>						      		
					      </blockquote>					  
					  
					  
					  <h2><i class="fal fa-tools"></i>&nbsp;&nbsp;<fmt:message key="doc.options.title" /></h2>
				      <blockquote class="blockquote bq-warning">
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
						<div class="form-group row">
						      	<div class="col-sm-12">
							      	<div class="form-check">
									  <input class="form-check-input" type="checkbox" id="printPDF" name="printPDF" checked="checked"/>
									  <label class="form-check-label" for="printPDF">
									    <fmt:message key="doc.options.printPDF" />
									  </label>
									  <small class="form-text text-muted">
										<fmt:message key="doc.options.printPDF.help" />
									  </small>
									</div>
								</div>
						</div>
					  </blockquote>
					  
				    <button type="submit" id="validate-button" class="btn btn-info btn-lg"><fmt:message key="doc.submit" /></button>			  	
			  	</form>
 				
 				<!-- Documentation -->	
				<div id="documentation" style="margin-top:2em;">
					<h3>General Documentation</h3>

					<p>This documentation generation utility allows to print the documentation of an application profile specified in SHACL.
					<br/>
					<br/>
					   The generated documentation describes all the properties allowed on each class/shape of the application profile, and includes a diagram, namespace table, introduction, 
					   and some metadata at the top of the document.
					 <br/> 
					 <br/> 
					   To better understand how the documentation generation works you can download this <a href="<c:url value="/resources/example/PersonCountry.ttl"/>">turtle example of an application profile specified in SHACL</a>
					   , or the corresponding <a href="<c:url value="/resources/example/PersonCountry.xlsx"/>">Excel file</a>.<br/>
					   This Excel file can be converted in SHACL using the <a href="https://skos-play.sparna.fr/play/convert" target="_blank">SKOS Play xls2rdf conversion tool</a>.
					   All the details about the conversion rules are documented in the converter page. This documentation focuses on which SHACL constraints are used to generate the documentation.					   					
					</p>


					<div style="margin-top:2em;">
						<h4>owl:Ontology</h4>
						<p>The documentation generation reads the following properties on a owl:Ontology entity in the SHACL file.</p>
						
						<table class="table table-bordered">
							<thead>
								<tr align="center">
									<th scope="col">Property</th>
								    <th scope="col">Type</th>
								    <th scope="col" width="auto">Required</th>
								    <th scope="col">Description</th>
								</tr>
							</thead>
							<tbody>
								<tr>
									<th scope="row" width="30%"><code>rdfs:label</code></th>
									<td>xsd:string</td>
									<td>Yes</td>
									<td class="text-break">Will be used to generate the title of the document</td>
								</tr>
								<tr>
									<th scope="row"><code>rdfs:comment</code></th>
									<td>xsd:string</td>
									<td>No</td>
									<td class="text-break">Will be used to generate an <b>Abstract</b> section in the documentation</td>
							  	</tr>
							  	<tr>
							  		<th scope="row"><code>dcterms:description</code></th>
							  		<td>xsd:string</td>
							  		<td>No</td>
							  		<td class="text-break">Will be used to insert a <b>Description</b> section in the generated documentation</td>
							  	</tr>
							  	<tr>
							  		<th scope="row"><code>owl:versionInfo</code></th>
							  		<td>xsd:string</td>
							  		<td>No</td>
							  		<td class="text-break">Will be shown in the header of the generated documentation</td>
							  	</tr>
							  	<tr>
							  		<th scope="row"><code>dcterms:modified</code></th>
							  		<td>xsd:dateTime</td>
							  		<td>No</td>
							  		<td class="text-break">Will be shown in the header of the generated documentation</td>
							  	</tr>
							</tbody>
						</table>
												  
						<br/>
						<p>This is an example of such a header:</p>
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
						<h4>Prefixes</h4>
						The prefixes of the SHACL file are inserted in a Namespaces section at the top of the documentation
						<br/>
						<img src="<c:url value="/resources/img/shacl_doc_prefixes_input.png"/>" width="80%"/>
						<br/>
						<br/>
						<img src="<c:url value="/resources/img/shacl_doc_prefixes.png"/>" width="100%" align="middle"/>
						<br/>
						<br/>
						
					</div>
					
					<div style="margin-top:2em;">
						<h4>Documentation of Node Shapes</h4>
						<p>The following properties are read on each sh:NodeShape to populate the header of each section of the generated documentation.</p>
						
						<table class="table table-bordered">
							<thead>
								<tr align="center">
									<th scope="col">Property</th>
								    <th scope="col">Type</th>
								    <th scope="col" width="auto">Required</th>
								    <th scope="col">Description</th>
								</tr>
							</thead>
							<tbody>
								<tr>
									<th scope="row" width="30%"><code>rdfs:label</code></th>
									<td>xsd:string</td>
									<td>Yes</td>
									<td class="text-break">Label of the NodeShape that will be used as the label of the section in the documentation.</td>
								</tr>
								<tr>
									<th scope="row"><code>sh:pattern</code></th>
									<td>xsd:string</td>
									<td>No</td>
									<td class="text-break">URI pattern that the targets of the NodeShape must conform to.</td>
							  	</tr>
							  	<tr>
							  		<th scope="row"><code>skos:example</code></th>
							  		<td>xsd:string</td>
							  		<td>No</td>
							  		<td class="text-break">Example of an IRI of a target of this NodeShape.</td>
							  	</tr>
							  	<tr>
							  		<th scope="row"><code>sh:order</code></th>
							  		<td>xsd:integer</td>
							  		<td>No</td>
							  		<td class="text-break">Sections of the generated documentation are sorted according to sh:order.</td>
							  	</tr>					  	
							</tbody>
						</table>
						<br/>
						<p>If you use the SHACL Excel template, this is typically expressed like this :</p>
						<br/>
						<img src="<c:url value="/resources/img/shacl_doc_nodeshape.png"/>" width="100%"/>
						<br/>
						<br/>
						And this is an example of how it looks like in SHACL:
						<br/>
						<br/>
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
						<h4>Documentation of Property Shapes</h4>
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
									<th scope="row"><code>sh:name</code></th>
									<td>xsd:string</td>
									<td>No</td>
									<td class="text-break">Used to display the name of the property. If not provided, the column will be empty.</td>
							  	</tr>
							  	<tr>
							  		<th scope="row"><code>sh:description</code></th>
							  		<td>xsd:string</td>
							  		<td>No</td>
							  		<td class="text-break">Populated the <b>Description</b> column of the table. If not provided, the column will be empty.</td>
							  	</tr>
							  	<tr>
							  		<th scope="row"><code>sh:minCount</code> / <code>sh:maxCount</code></th>
							  		<td>xsd:integer</td>
							  		<td>No</td>
							  		<td class="text-break">Used to populate the <b>Cardinality</b> column of the table</td>
							  	</tr>
							  	<tr>
							  		<th scope="row"><code>sh:node</code></th>
							  		<td>IRI of a NodeShape</td>
							  		<td>One of sh:node, sh:class, sh:nodeKind, sh:datatype, sh:or, sh:hasValue must be provided</td>
							  		<td class="text-break">Used to populate the <b>Expected value</b> column, see below.</td>
							  	</tr>
							  	<tr>
							  		<th scope="row"><code>sh:class</code></th>
							  		<td>IRI of a class</td>
							  		<td>One of sh:node, sh:class, sh:nodeKind, sh:datatype, sh:or, sh:hasValue must be provided.</td>
							  		<td class="text-break">Used to populate the <b>Expected value</b> column, see below.</td>
							  	</tr>
							  	<tr>
							  		<th scope="row"><code>sh:nodeKind</code></th>
							  		<td>Value can be either sh:IRI or sh:Literal</td>
							  		<td>One of sh:node, sh:class, sh:nodeKind, sh:datatype, sh:or, sh:hasValue must be provided.</td>
							  		<td class="text-break">Used to populate the <b>Expected value</b> column, see below.</td>
							  	</tr>
							  	<tr>
							  		<th scope="row"><code>sh:datatype</code></th>
							  		<td>IRI of a datatype</td>
							  		<td>One of sh:node, sh:class, sh:nodeKind, sh:datatype, sh:or, sh:hasValue must be provided.</td>
							  		<td class="text-break">Used to populate the <b>Expected value</b> column, see below.</td>
							  	</tr>
							  	<tr>
							  		<th scope="row"><code>sh:hasValue</code></th>
							  		<td>RDF List of values</td>
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
						
						<p>The <b><u>Expected value</u></b> column is generated by looking at the following properties in order of precedence :</p>
						<ul>
							<li>If <code>sh:hasValue</code> is not null use it.</li>
							<li>Otherwise use <code>sh:class</code> if present.</li>
							<li>Otherwise use <code>sh:node</code> if present.</li>
							<li>Otherwise use <code>sh:datatype</code> if present.</li>
							<li>Otherwise use <code>sh:nodeKind</code> if present.</li>	
							<li>Otherwise use <code>sh:or</code> if present; in this case, get the list items and read <code>sh:node</code> on each of them.</li>
							<li>Otherwise the column is left empty.</li>
						</ul>
						<p>Additionally, if <code>sh:in</code> is present, it is inserted as an additional information in the Expected value column.</p>
						<p>This generates the following output:
							<br/>
							<br/>
							<img src="<c:url value="/resources/img/shacl_doc_properties.png"/>" width="100%"/>
							<br/>
							<br/>
							<img src="<c:url value="/resources/img/shacl_doc_properties_2.png"/>" width="100%"/>
						</p>
						
					</div>
					
				</div>			
			</div>
		</div>

    </div><!-- /.container-fluid -->

			
	<jsp:include page="footer.jsp"></jsp:include>
	
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
    
  </body>
</html>