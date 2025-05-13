<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!-- setup the locale for the messages based on the language in the session -->
<fmt:setLocale value="${sessionScope['fr.sparna.rdf.shacl.shaclplay.SessionData'].userLocale.language}"/>
<fmt:setBundle basename="fr.sparna.rdf.shacl.shaclplay.i18n.shaclplay"/>

<c:set var="data" value="${requestScope['ConvertFormData']}" />

<html>
	<head>
		<title><fmt:message key="window.app" /></title>
		<link rel="canonical" href="https://shacl-play.sparna.fr/play/convert" />
		
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
		<jsp:param name="active" value="convert"/>
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
				
				<h1 class="display-3">
				<c:choose>
					<c:when test="${empty data.selectedShapesKey}"><fmt:message key="convert.title" /></c:when>
					<c:otherwise><fmt:message key="convert.title.convertWith" /> ${data.catalog.getCatalogEntryById(data.selectedShapesKey).title}</c:otherwise>
				</c:choose>
				</h1>	
				
				<c:choose>
		 			<c:when test="${empty data.selectedShapesKey}">
			 			<div class="form-shortdesc">
							<p>
								This utility <em>applies a set of SHACL rules to convert an RDF dataset into another structure</em>. Some SHACL rules
								are provided in the catalog to convert OWL ontologies to SHACL.
							</p>
						</div>
					</c:when>
				</c:choose>
	 
	 			
	 
			  	<form id="upload_form" action="convert" method="POST" enctype="multipart/form-data" class="form-horizontal">
				      
				      <h2><i class="fal fa-chart-network"></i>&nbsp;&nbsp;<fmt:message key="convert.inputData.title" /></h2>
					  <!-- Include data blockquote -->
					  <%@ include file="include/data-blockquote.jsp" %>
					  
					  <c:if test="${empty data.selectedShapesKey}">
						  <h2><i class="fal fa-shapes"></i>&nbsp;&nbsp;<fmt:message key="convert.shapes.title" /></h2>
						  <!-- Include rules blockquote -->
					  	  <%@ include file="include/rules-blockquote.jsp" %>						  
					  </c:if>
					  <c:if test="${not empty data.selectedShapesKey}">
					  	<input type="hidden" name="shapesSource" value="catalog" />
					  	<input type="hidden" name="inputShapeCatalog" value="${data.selectedShapesKey}" />
					  </c:if>
					  
					  
					  
					  <h2><i class="fal fa-tools"></i>&nbsp;&nbsp;<fmt:message key="blockquote.options.title" /></h2>
				      <blockquote class="blockquote bq-warning">
					      <div class="form-group row">
					      	<div class="col-sm-12">
								<label for="inputShapeCatalog" class="col-sm-3 col-form-label">
									<fmt:message key="draw.options.format" />					    
								</label>
							    <div class="col-sm-2">
						    		<select class="form-control" id="format" name="format" >
						    			<option value="Turtle">Turtle</option>
						    			<option value="RDF/XML">RDF/XML</option>
						    			<option value="N-Triples">N-Triples</option>
						    			<option value="N-Quads">N-Quads</option>
						    			<option value="N3">N3</option>
						    			<option value="TriG">TriG</option>
						    			<option value="JSON-LD">Json-LD</option>
									</select>
								</div>
							</div>
						  </div>
					  </blockquote>
					  
				    <button type="submit" id="validate-button" class="btn btn-info btn-lg"><fmt:message key="convert.submit" /></button>			  	
			  	</form>
 		
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