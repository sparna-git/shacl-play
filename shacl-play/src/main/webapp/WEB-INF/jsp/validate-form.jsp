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
		
		
	</head>
	<body>

	<jsp:include page="include/navbar.jsp">
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
								them from the shapes catalog.<br/>
								For an example, you can try validating the <a href="#" onclick="$('#inputUrl').val('https://raw.githubusercontent.com/sparna-git/SHACL-Catalog/master/shacl-catalog.ttl');enabledInput('inputUrl');" title="https://raw.githubusercontent.com/sparna-git/SHACL-Catalog/master/shacl-catalog.ttl">Shapes catalog data</a> against the Shape "SHACL Play! Catalog Shapes" in the list below.
							</p>
						</div>
					</c:when>
				</c:choose>
	 
			  	<form id="upload_form" action="validate" method="POST" enctype="multipart/form-data" class="form-horizontal">
				      
				      <h2><i class="fal fa-chart-network"></i>&nbsp;&nbsp;<fmt:message key="validate.inputData.title" /></h2>
				      <!-- Include data blockquote -->
					  <%@ include file="include/data-blockquote.jsp" %>
					  
					  <c:if test="${empty data.selectedShapesKey}">
						  <h2><i class="fal fa-shapes"></i>&nbsp;&nbsp;<fmt:message key="validate.shapes.title" /></h2>
						  <!-- Include shapes blockquote -->
					  	  <%@ include file="include/shapes-blockquote.jsp" %>
					  </c:if>
					  <c:if test="${not empty data.selectedShapesKey}">
					  	<input type="hidden" name="shapesSource" value="catalog" />
					  	<input type="hidden" name="inputShapeCatalog" value="${data.selectedShapesKey}" />
					  </c:if>
					  
					  
					  
					  <h2><i class="fal fa-tools"></i>&nbsp;&nbsp;<fmt:message key="blockquote.options.title" /></h2>
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
						  <div class="form-group row">
							<div class="col-sm-12">
								<div class="form-check">
								<input class="form-check-input" type="checkbox" id="avoidResolveTargets" name="avoidResolveTargets" />
								<label class="form-check-label" for="avoidResolveTargets">
								  <fmt:message key="validate.options.avoidResolveTargets" />
								</label>
								<small class="form-text text-muted">
								  <fmt:message key="validate.options.avoidResolveTargets.help" />
								</small>
							  </div>
						  </div>
						</div>
					  </blockquote>
					  
				    <button type="submit" id="validate-button" class="btn btn-info btn-lg"><fmt:message key="validate.submit" /></button>			  	
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

					<div style="margin-top:2em;">
						<h4 id="targetsResolution">Extra target resolutions</h4>
						<p>
							By default, SHACL Play! will resolve the targets of the shapes in the SHACL file. This means that all targets specifications found in the shapes graph
							are explicitely resolved, and an extra triple is generated for each target found : <code>https://shacl-play.sparna.fr/ontology#hasFocusNode</code>, with
							the node shape as subject and the target resource as object. Based on this extra target resolution triple, 2 extra checks are applied:
						</p>
						<ul>
							<li>
								<strong>"Data coverage"</strong>: SHACL Play! will check that every resource in the input data graph has been targeted by at least one shape.
								Resources not targeted by any shape will be returned as Warnings in the validation report. This is useful to check that the input data graph does not contain
								resources that should not be present. It is similar to "unit tests coverage" in software testing, where you want to make sure that all your code is covered by tests.
							</li>
							<li>
								<strong>"Shapes coverage"</strong>: SHACL Play! will check that every shape in the shapes graph has at least one target. 
								Shapes without any target will be returned as Warnings in the validation report. This is useful to make sure that the target specifications of each shape resolved correctly,
								and to avoid situations where no violations are returned, not because the data is valid, but because the initial target specification was wrong (like a typo in a <code>sh:targetClass</code> URI).
								<br/>
								In particular if no shapes at all matched a target, the validation report will contain a specific title and status : <em>"Shapes did not match anything !"</em>
							</li>
						</ul>
						<p>
							You can disable this extra target resolution by checking the <strong>"Avoid resolving targets"</strong> checkbox in the options above. 
							When this option is checked, SHACL Play! will not resolve the targets of the shapes in the SHACL file, will not generate the extra triples, and will not apply the extra checks.
						</p>
						
					</div>
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