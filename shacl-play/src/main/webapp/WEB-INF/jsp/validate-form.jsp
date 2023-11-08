<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!-- setup the locale for the messages based on the language in the session -->
<fmt:setLocale value="${sessionScope['fr.sparna.rdf.shacl.shaclplay.SessionData'].userLocale.language}"/>
<fmt:setBundle basename="fr.sparna.rdf.shacl.shaclplay.i18n.shaclplay"/>

<c:set var="data" value="${requestScope['ValidateFormData']}" />

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
				document.getElementById('inputShapeCatalog').disabled = selected != 'inputShapeCatalog';
				document.getElementById('inputShapeFile').disabled = selected != 'inputShapeFile';
				document.getElementById('inputShapeInline').disabled = selected != 'inputShapeInline';
			}

	    </script>
		
		
	</head>
	<body>

	<jsp:include page="navbar.jsp">
		<jsp:param name="active" value="validate"/>
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
				
				<h1 class="display-3"><c:choose><c:when test="${empty data.selectedShapesKey}"><fmt:message key="validate.title" /></c:when><c:otherwise><fmt:message key="validate.title.validateWith" /> ${data.catalog.getCatalogEntryById(data.selectedShapesKey).title}</c:otherwise></c:choose></h1>	
	 
	 			<c:choose>
		 			<c:when test="${empty data.selectedShapesKey}">
			 			<div class="form-shortdesc">
							<p>
								This utility <em>validates the conformity of an RDF dataset against a SHACL specification</em>. You need to provide the RDF data to be validated,
								either by uploading it, providing its URL, or copy-pasting inline RDF. You also need to provide the SHACL shapes in the same ways, or by selecting
								them from the shapes catalog.
							</p>
						</div>
					</c:when>
				</c:choose>
	 
			  	<form id="upload_form" action="validate" method="POST" enctype="multipart/form-data" class="form-horizontal">
				      
				      <h2><i class="fal fa-chart-network"></i>&nbsp;&nbsp;<fmt:message key="validate.inputData.title" /></h2>
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
					    	<fmt:message key="validate.inputData.upload" />
					    
					    </label>
					    <div class="col-sm-9">
					    		<div class="fileinput fileinput-new input-group" data-provides="fileinput">
								  <div class="form-control" data-trigger="fileinput" id="inputFile">
								    <i class="fal fa-upload"></i><span class="fileinput-filename with-icon"></span>
								  </div>
								  <span class="input-group-append">
								    <span class="input-group-text fileinput-exists" data-dismiss="fileinput">
								      <fmt:message key="validate.inputData.upload.remove" />
								    </span>
								
								    <span class="input-group-text btn-file">
								      <span class="fileinput-new"><fmt:message key="validate.inputData.upload.select" /></span>
								      <span class="fileinput-exists"><fmt:message key="validate.inputData.upload.change" /></span>
								      <input type="file" name="inputFile" multiple onchange="enabledInput('inputFile')">
								    </span>
								  </span>
								</div>
								<small class="form-text text-muted">
								  <fmt:message key="validate.inputData.upload.help" />
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
					    	<fmt:message key="validate.inputData.url" />
					    </label>
					    <div class="col-sm-9">
					      <input 
					      	type="text"
					      	class="form-control"
					      	id="inputUrl"
					      	name="inputUrl"
					      	placeholder="<fmt:message key="validate.inputData.url.placeholder" />"
					      	onkeypress="enabledInput('inputUrl');"
					      	onpaste="enabledInput('inputUrl');"
					      />
					      <small class="form-text text-muted">
								  <fmt:message key="validate.inputData.url.help" />
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
					    	<fmt:message key="validate.inputData.inline" />
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
								  <fmt:message key="validate.inputData.inline.help" />
						  </small>
					    </div>
					  </div>
					  </blockquote>
					  
					  <c:if test="${empty data.selectedShapesKey}">
						  <h2><i class="fal fa-shapes"></i>&nbsp;&nbsp;<fmt:message key="validate.shapes.title" /></h2>
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
							    	<fmt:message key="validate.shapes.upload" />
							    
							    </label>
							    <div class="col-sm-9">
							    		<div class="fileinput fileinput-new input-group" data-provides="fileinput">
										  <div class="form-control" data-trigger="fileinput" id="inputShapeFile">
										    <i class="fal fa-upload"></i><span class="fileinput-filename with-icon"></span>
										  </div>
										  <span class="input-group-append">
										    <span class="input-group-text fileinput-exists" data-dismiss="fileinput">
										      <fmt:message key="validate.shapes.upload.remove" />
										    </span>
										
										    <span class="input-group-text btn-file">
										      <span class="fileinput-new"><fmt:message key="validate.shapes.upload.select" /></span>
										      <span class="fileinput-exists"><fmt:message key="validate.shapes.upload.change" /></span>
										      <input type="file" name="inputShapeFile" multiple onchange="enabledShapeInput('inputShapeFile')">
										    </span>
										  </span>
										</div>
										<small class="form-text text-muted">
											  <fmt:message key="validate.shapes.upload.help" />
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
								    	<fmt:message key="validate.shapes.catalog" />					    
								    </label>
								    <div class="col-sm-9">
								    		<select class="form-control" id="inputShapeCatalog" name="inputShapeCatalog" onchange="enabledShapeInput('inputShapeCatalog');">
										      	<c:forEach items="${data.catalog.entries}" var="entry">
										      		<option value="${entry.id}">${entry.title}</option>
										      	</c:forEach>
										    </select>
										    <small class="form-text text-muted">
												  <fmt:message key="validate.shapes.catalog.help" />
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
							    	<fmt:message key="validate.shapes.url" />
							    </label>
							    <div class="col-sm-9">
							      <input 
							      	type="text"
							      	class="form-control"
							      	id="inputShapeUrl"
							      	name="inputShapeUrl"
							      	placeholder="<fmt:message key="validate.shapes.url.placeholder" />"
							      	onkeypress="enabledShapeInput('inputShapeUrl');"
							      	onchange="enabledShapeInput('inputShapeUrl')"
							      >
							      <small class="form-text text-muted">
									  <fmt:message key="validate.shapes.url.help" />
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
							    	<fmt:message key="validate.shapes.inline" />
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
									  <fmt:message key="validate.shapes.inline.help" />
								  </small>
							    </div>	
						      </div>
					      </blockquote>
					  </c:if>
					  <c:if test="${not empty data.selectedShapesKey}">
					  	<input type="hidden" name="shapesSource" value="catalog" />
					  	<input type="hidden" name="inputShapeCatalog" value="${data.selectedShapesKey}" />
					  </c:if>
					  
					  
					  
					  <h2><i class="fal fa-tools"></i>&nbsp;&nbsp;<fmt:message key="validate.options.title" /></h2>
				      <blockquote class="blockquote bq-warning">					      
					      <div class="form-group row">
					      	<div class="col-sm-12">
						      	<div class="form-check">
								  <input class="form-check-input" type="checkbox" id="closeShapes" name="closeShapes" />
								  <label class="form-check-label" for="closeShapes">
								    <fmt:message key="validate.options.closeShapes" />
								  </label>
								  <small class="form-text text-muted">
									<fmt:message key="validate.options.closeShapes.help" />
								  </small>
								</div>
							</div>
						  </div>
						  <div class="form-group row">
					      	<div class="col-sm-12">
						      	<div class="form-check">
								  <input class="form-check-input" type="checkbox" id="createDetails" name="createDetails" />
								  <label class="form-check-label" for="createDetails">
								    <fmt:message key="validate.options.createDetails" />
								  </label>
								  <small class="form-text text-muted">
									<fmt:message key="validate.options.createDetails.help" />
								  </small>
								</div>
							</div>
						  </div>
						  <div class="form-group row">
					      	<div class="col-sm-12">
						      	<div class="form-check">
								  <input class="form-check-input" type="checkbox" id="infer" name="infer" />
								  <label class="form-check-label" for="infer">
								    <fmt:message key="validate.options.infer" />
								  </label>
								  <small class="form-text text-muted">
									<fmt:message key="validate.options.infer.help" />
								  </small>
								</div>
							</div>
						  </div>
					  </blockquote>
					  
				    <button type="submit" id="validate-button" class="btn btn-info btn-lg"><fmt:message key="validate.validate" /></button>			  	
			  	</form>
			  	
			  	<!-- Documentation -->	
				<div style="margin-top:3em;">
					<h3 id="documentation">Documentation</h3>
					
					<div style="margin-top:2em;">
						<h4 id="direct-links">Providing direct links to validation reports</h4>
						<p>If the RDF dataset to be validated is online, and <em>if and only if the SHACL shapes are registered in <a href="shapes-catalog">SHACL Play shapes catalog</a></em>,
						you can provide direct links to a validation report with the following URL : <code>https://shacl-play.sparna.fr/play/{shapes-catalog-entry-id}/report?url={URL of the RDF data file to validate}</code></p>
						<p>For example : <a href="https://shacl-play.sparna.fr/play/shaclplay-catalog/report?url=https://raw.githubusercontent.com/sparna-git/SHACL-Catalog/master/shacl-catalog.ttl">https://shacl-play.sparna.fr/play/shaclplay-catalog/report?url=https://raw.githubusercontent.com/sparna-git/SHACL-Catalog/master/shacl-catalog.ttl</a></p>
						
					</div>
					
					<div style="margin-top:2em;">
						<h4 id="badges">Generate <a href="https://img.shields.io">shields.io</a> validation badges</h4>
						<p>This validation utility is available as an API to generate shields validation badges for dataset. The API URL will return a JSON data that can be tunneled to <code>https://img.shields.io/endpoint?url=</code>.</p>
						<p>This works <em>if and only if the SHACL shapes are registered in <a href="shapes-catalog">SHACL Play shapes catalog</a></em>.</p>
						<p>The API general synopsis is <code>https://shacl-play.sparna.fr/play/{shapes-catalog-entry-id}/badge?url={URL of the RDF data file to validate}</code></p>
						<p>For example : <code><a href="https://shacl-play.sparna.fr/play/shaclplay-catalog/badge?url=https://raw.githubusercontent.com/sparna-git/SHACL-Catalog/master/shacl-catalog.ttl">https://shacl-play.sparna.fr/play/shaclplay-catalog/badge?url=https://raw.githubusercontent.com/sparna-git/SHACL-Catalog/master/shacl-catalog.ttl</a></code></p>
						<p>The full badge URL is then <code><a href="https://img.shields.io/endpoint?url=https%3a%2f%2fshacl-play.sparna.fr%2fplay%2fshaclplay-catalog%2fbadge%3furl%3dhttps%3a%2f%2fraw.githubusercontent.com%2fsparna-git%2fSHACL-Catalog%2fmaster%2fshacl-catalog.ttl">https://img.shields.io/endpoint?url=https%3a%2f%2fshacl-play.sparna.fr%2fplay%2fshaclplay-catalog%2fbadge%3furl%3dhttps%3a%2f%2fraw.githubusercontent.com%2fsparna-git%2fSHACL-Catalog%2fmaster%2fshacl-catalog.ttl</a></code></p>
						<p>When you insert the badge on your Github repository, you can use it to link to the "/report" URL (see <a href="#direct-links">above</a>). See the <a href="https://github.com/sparna-git/SHACL-Catalog/blob/master/README.md?plain=1">Shapes catalog repository README</a> for an example</p>
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