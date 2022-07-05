<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!-- setup the locale for the messages based on the language in the session -->
<fmt:setLocale value="${sessionScope['fr.sparna.rdf.shacl.shaclplay.SessionData'].userLocale.language}"/>
<fmt:setBundle basename="fr.sparna.rdf.shacl.shaclplay.i18n.shaclplay"/>

<c:set var="data" value="${requestScope['SparqlFormData']}" />

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
	
			function enabledInput(selected) {
				document.getElementById('source-' + selected).checked = true;
				document.getElementById('inputUrl').disabled = selected != 'inputUrl';
				document.getElementById('inputFile').disabled = selected != 'inputFile';
				document.getElementById('inputInline').disabled = selected != 'inputInline';
			}
			
			function enabledShapeInput(selected) {
				document.getElementById('sourceShape-' + selected).checked = true;
				document.getElementById('inputShapeUrl').disabled = selected != 'inputShapeUrl';
				document.getElementById('inputShapeFile').disabled = selected != 'inputShapeFile';
				document.getElementById('inputShapeInline').disabled = selected != 'inputShapeInline';
			}

	    </script>
		
		
	</head>
	<body>

	<jsp:include page="navbar.jsp">
		<jsp:param name="active" value="sparql"/>
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
				
				<h1 class="display-3"><c:choose><c:when test="${empty data.selectedShapesKey}"><fmt:message key="sparql.title" /></c:when><c:otherwise><fmt:message key="sparql.title.validateWith" /> ${data.catalog.getCatalogEntryById(data.selectedShapesKey).title}</c:otherwise></c:choose></h1>	
	 
			  	<form id="upload_form" action="sparql" method="POST" enctype="multipart/form-data" class="form-horizontal">
				      
				      <h2><i class="fal fa-shapes"></i>&nbsp;&nbsp;<fmt:message key="sparql.inputData.title" /></h2>
				      <blockquote class="blockquote bq-success">
				      <div class="form-group row">
				      	
					    <label for="inputFile" class="col-sm-3 col-form-label">
					    
					    	<input
									type="radio"
									name="source"
									id="source-inputFile"
									value="file"
									checked="checked"
									onchange="enabledInput('inputFile')" />
					    	<fmt:message key="sparql.inputData.upload" />
					    
					    </label>
					    <div class="col-sm-9">
					    		<div class="fileinput fileinput-new input-group" data-provides="fileinput">
								  <div class="form-control" data-trigger="fileinput" id="inputFile">
								    <i class="fal fa-upload"></i><span class="fileinput-filename with-icon"></span>
								  </div>
								  <span class="input-group-append">
								    <span class="input-group-text fileinput-exists" data-dismiss="fileinput">
								      <fmt:message key="sparql.inputData.upload.remove" />
								    </span>
								
								    <span class="input-group-text btn-file">
								      <span class="fileinput-new"><fmt:message key="sparql.inputData.upload.select" /></span>
								      <span class="fileinput-exists"><fmt:message key="sparql.inputData.upload.change" /></span>
								      <input type="file" name="inputFile" multiple onchange="enabledInput('inputFile')">
								    </span>
								  </span>
								</div>
								<small class="form-text text-muted">
								  <fmt:message key="sparql.inputData.upload.help" />
								</small>
					    </div>
					  </div>
					  <div class="form-group row">
					    <label for="inputUrl" class="col-sm-3 col-form-label">
					    
					    	<input
									type="radio"
									name="source"
									id="source-inputUrl"
									value="url"
									onchange="enabledInput('inputUrl')" />
					    	<fmt:message key="sparql.inputData.url" />
					    </label>
					    <div class="col-sm-9">
					      <input 
					      	type="text"
					      	class="form-control"
					      	id="inputUrl"
					      	name="inputUrl"
					      	placeholder="<fmt:message key="sparql.inputData.url.placeholder" />"
					      	onkeypress="enabledInput('inputUrl');"
					      	onpaste="enabledInput('inputUrl');"
					      />
					      <small class="form-text text-muted">
								  <fmt:message key="sparql.inputData.url.help" />
						  </small>
					    </div>
					  </div>
					  <div class="form-group row">
					    <label for="inputInline" class="col-sm-3 col-form-label">
					    
					    	<input
									type="radio"
									name="source"
									id="source-inputInline"
									value="inline"
									onchange="enabledInput('inputInline')" />
					    	<fmt:message key="sparql.inputData.inline" />
					    </label>
					    <div class="col-sm-9">
					      <textarea 
					      	class="form-control"
					      	id="inputInline"
					      	name="inputInline"
					      	rows="5"
					      	onkeypress="enabledInput('inputInline');"
							onpaste="enabledInput('inputInline')"
					      ></textarea>
					      <small class="form-text text-muted">
								  <fmt:message key="sparql.inputData.inline.help" />
						  </small>
					    </div>
					  </div>
					  </blockquote>
					  
					  <c:if test="${empty data.selectedShapesKey}">
						  <h2><i class="fal fa-shapes"></i>&nbsp;&nbsp;<fmt:message key="sparql.shapes.title" /></h2>
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
							    	<fmt:message key="sparql.shapes.upload" />
							    
							    </label>
							    <div class="col-sm-9">
							    		<div class="fileinput fileinput-new input-group" data-provides="fileinput">
										  <div class="form-control" data-trigger="fileinput" id="inputShapeFile">
										    <i class="fal fa-upload"></i><span class="fileinput-filename with-icon"></span>
										  </div>
										  <span class="input-group-append">
										    <span class="input-group-text fileinput-exists" data-dismiss="fileinput">
										      <fmt:message key="sparql.shapes.upload.remove" />
										    </span>
										
										    <span class="input-group-text btn-file">
										      <span class="fileinput-new"><fmt:message key="sparql.shapes.upload.select" /></span>
										      <span class="fileinput-exists"><fmt:message key="sparql.shapes.upload.change" /></span>
										      <input type="file" name="inputShapeFile" multiple onchange="enabledShapeInput('inputShapeFile')">
										    </span>
										  </span>
										</div>
										<small class="form-text text-muted">
											  <fmt:message key="sparql.shapes.upload.help" />
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
								    	<fmt:message key="sparql.shapes.catalog" />					    
								    </label>
								    <div class="col-sm-9">
								    		<select class="form-control" id="inputShapeCatalog" name="inputShapeCatalog" onchange="enabledShapeInput('inputShapeCatalog');">
										      	<c:forEach items="${data.catalog.entries}" var="entry">
										      		<option value="${entry.id}">${entry.title}</option>
										      	</c:forEach>
										    </select>
										    <small class="form-text text-muted">
												  <fmt:message key="sparql.shapes.catalog.help" />
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
							    	<fmt:message key="sparql.shapes.url" />
							    </label>
							    <div class="col-sm-9">
							      <input 
							      	type="text"
							      	class="form-control"
							      	id="inputShapeUrl"
							      	name="inputShapeUrl"
							      	placeholder="<fmt:message key="sparql.shapes.url.placeholder" />"
							      	onkeypress="enabledShapeInput('inputShapeUrl');"
							      	onchange="enabledShapeInput('inputShapeUrl')"
							      >
							      <small class="form-text text-muted">
									  <fmt:message key="sparql.shapes.url.help" />
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
							    	<fmt:message key="sparql.shapes.inline" />
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
									  <fmt:message key="sparql.shapes.inline.help" />
								  </small>
							    </div>	
						      </div>
					      </blockquote>
					  </c:if>
					  <c:if test="${not empty data.selectedShapesKey}">
					  	<input type="hidden" name="shapesSource" value="catalog" />
					  	<input type="hidden" name="inputShapeCatalog" value="${data.selectedShapesKey}" />
					  </c:if>
					  
					  
					  
					  <h2><i class="fal fa-tools"></i>&nbsp;&nbsp;<fmt:message key="sparql.options.title" /></h2>
				      <blockquote class="blockquote bq-warning">					      
					      <div class="form-group row">
					      	<div class="col-sm-12">
						      	<div class="form-check">
								  <input class="form-check-input" type="checkbox" id="formatCombine" name="formatCombine" checked="checked" />
								  <label class="form-check-label" for="formatCombine">
								    <fmt:message key="sparql.options.formatCombine" />
								  </label>
								  <small class="form-text text-muted">
									<fmt:message key="sparql.options.formatCombine.help" />
								  </small>
								</div>
							</div>
						  </div>						  
					  </blockquote>
					  
				    <button type="submit" id="sparql-button" class="btn btn-info btn-lg"><fmt:message key="sparql.validate" /></button>			  	
			  	</form>
 		
			
				<!-- Documentation -->			
				<fieldset id="documentation" style="margin-top:10em;">
					<legend><a href="#documentation" id="documentation"></a><span class="glyphicon glyphicon-ok" aria-hidden="true"></span></a>&nbsp;General Documentation</legend>
					<h4>What is this tool ?</h4>
					<p>
					SPARQL Query is a tool to convert a SHACL code into a SPARQL query, applying the rules and constraints declared in a SHACL file.
					<br />This tool will help us to have a SPARQL query according to the shacl constraint configurations such as <a href="https://www.w3.org/TR/shacl/#HasValueConstraintComponent" target="_blank">sh:hasValue</a> , <a href="https://www.w3.org/TR/shacl/#InConstraintComponent" target="_blank">sh:In</a>, <a href="https://www.w3.org/TR/shacl/#LanguageInConstraintComponent" target="_blank">sh:languageIn</a>, <a href="https://www.w3.org/TR/shacl/#NodeConstraintComponent" target="_blank">sh:node</a>, <a href="https://www.w3.org/TR/shacl/#OrConstraintComponent" target="_blank">sh:or</a>, <a href="https://www.w3.org/TR/shacl/#property-path-inverse" target="_blank">sh:inversePath</a>, which will be explained later.
					</p>
					<h4>What type of SPARQL query</h4>
					<p>
						The <a href="https://www.w3.org/TR/rdf-sparql-query/#construct" target="_blank">CONSTRUCT query</a> form returns a single RDF graph specified by a graph template.
					</p>						
				</fieldset>
				
				<fieldset style="margin-top:3em;">
					<legend><a href="#shacl-file-structure" id="shacl-file-structure"><span class="glyphicon glyphicon-link" aria-hidden="true"></span></a>Shacl File Structure</legend>
					<p>
						For the SPARQL query building process, must have the <a href="https://www.w3.org/TR/shacl/#targetClass" target="_blank">sh:target</a> and <a href="https://www.w3.org/TR/shacl/#dfn-sparql-based-constraints" target="_blank">sh:select</a> constraints set to one or the necessary Nodeshape.
						The <a href="https://www.w3.org/TR/shacl/#dfn-sparql-based-constraints">sh:select</a> constraint will be our first query to be used as a subquery. From this Nodeshape the first goal of creating a SPARQL query is started.
					</p>
					<!-- img Shacl File -->
					<img src="<c:url value="/resources/img/shacl_nodeshape.png"/>" width="100%"/>
					<br/>
					<br/>
					<h4><a href="#shacl-file-structure_properties" id="shacl-file-structure_properties"><span class="glyphicon glyphicon-ok" aria-hidden="true"></span></a>Properties Configuration</h4>
						<p>
							In the properties of the shacl file, 4 restrictions are considered as main and 3 restrictions as rules, these rules are linked to the sh:path restriction.
						</p>
						<br/>
						<a href="https://www.w3.org/TR/shacl/#property-shapes" target="_blank"><b>sh:path (Required)</b></a>
						<p>
							This property is set as an object in the CONSTRUCT clause and as a property in the WHERE clause of a SPARQL query.
							<br>
							All sh:path settings are part of the query structure as objects and predicates.							
						</p>
						<br/>
						Criterias
						<p>
							Within the property, 3 possible conditions are considered to be configured:
						</p>
						<ul>
							<li><a href="https://www.w3.org/TR/shacl/#HasValueConstraintComponent" target="_blank">sh:hasValue</a>
								<p>
									specifies the condition that at least one value node is equal to the given RDF term.
								</p>
							</li>
							<li><a href="https://www.w3.org/TR/shacl/#InConstraintComponent" target="_blank">sh:In</a>
								<p>
									specifies the condition that each value node is a member of a provided SHACL list.
								</p>
							</li>
							<li><a href="https://www.w3.org/TR/shacl/#LanguageInConstraintComponent" target="_blank">sh:languageIn</a>
								<p>
									is that the allowed language tags for each value node are limited by a given list of language tags.							
								</p>
							</li>						
						</ul>
					
						<a href="https://www.w3.org/TR/shacl/#NodeConstraintComponent" target="_blank"><b>sh:node (Optional)</b></a>
							<p>
								When a sh:path property has an evaluation with a sh:node, a SPARQL query will be generated against the sh:path property that calls the nodeshape that is configured on the sh:node.
							</p>
						<br/>
						<a href="https://www.w3.org/TR/shacl/#OrConstraintComponent" target="_blank"><b>sh:or (Optional)</b></a>
							<p>
								With this shacl constraint several queries can be generated from the property of the nodeshape that calls it, its particularity is to generate several queries according to the quantities of nodesehape configured in the sh:or.
								<br/>
								For each nodeShape value in sh:or, it generates a sparql query						
							</p>
						<br/>
						<a href="https://www.w3.org/TR/shacl/#property-path-inverse" target="_blank"><b>sh:inversePath (Optional)</b></a>
							<p>
							<p>
						<br/>
						
					<img src="<c:url value="/resources/img/shacl_properties.png"/>" width="100%"/>
				</fieldset>
				
				<fieldset>
					<legend><a href="#QuerySPARQL" id="QuerySPARQL"></a><span class="glyphicon glyphicon-ok" aria-hidden="true"></span></a>&nbsp;Convertion to SPARQL Query</legend>
					
					<!-- Shacl code convert to SPARQL Query -->
					<ol>
						<li>A SPARQL query is generated if <code>sh:select</code> has a query value.</li>
						<li> if the <code>sh:path</code> evaluates certain conditions <code>sh:hasValue</code> or <code>sh:in</code>, it should use the VALUES clause, if it is a <code>sh:languageIn</code> it will be the FILTER clause.<br/> 
							Example:<br/>
							<img src="<c:url value="/resources/img/Query0.png"/>" width="100%"/>
						</li>
						<li> if the property shape call others nodeshape in the <code>sh:node</code> or <code>sh:or</code> or <code>sh:inversePath</code>  a SPARQL query will be generated.
							
							Example:<br/>
								<p>
									this example the <code>ex:Country</code> property in the Person NodeShape, call the City NodeShape.
									<br/>
									<!-- SPARQL Query from City -->
									<img src="<c:url value="/resources/img/Query_sh_node.png"/>" width="100%"/>
								</p>
							
						</li>
					</ol>
				</fieldset>
				
			</div>
		</div>
		<br>
		<br>
		
				
			
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