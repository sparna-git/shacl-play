<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!-- setup the locale for the messages based on the language in the session -->
<fmt:setLocale value="${sessionScope['fr.sparna.rdf.shacl.shaclplay.SessionData'].userLocale.language}"/>
<fmt:setBundle basename="fr.sparna.rdf.shacl.shaclplay.i18n.shaclplay"/>

<c:set var="data" value="${requestScope['DrawFormData']}" />

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
		<jsp:param name="active" value="draw"/>
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
				
				<h1 class="display-3"><fmt:message key="draw.title" /></h1>	
	 
			  	<form id="upload_form" action="draw" method="POST" enctype="multipart/form-data" class="form-horizontal">
				      
					  

						  <h2><i class="fal fa-shapes"></i>&nbsp;&nbsp;<fmt:message key="draw.shapes.title" /></h2>
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
							    	<fmt:message key="draw.shapes.upload" />
							    
							    </label>
							    <div class="col-sm-9">
							    		<div class="fileinput fileinput-new input-group" data-provides="fileinput">
										  <div class="form-control" data-trigger="fileinput" id="inputShapeFile">
										    <i class="fal fa-upload"></i><span class="fileinput-filename with-icon"></span>
										  </div>
										  <span class="input-group-append">
										    <span class="input-group-text fileinput-exists" data-dismiss="fileinput">
										      <fmt:message key="draw.shapes.upload.remove" />
										    </span>
										
										    <span class="input-group-text btn-file">
										      <span class="fileinput-new"><fmt:message key="draw.shapes.upload.select" /></span>
										      <span class="fileinput-exists"><fmt:message key="draw.shapes.upload.change" /></span>
										      <input type="file" name="inputShapeFile" multiple onchange="enabledShapeInput('inputShapeFile')">
										    </span>
										  </span>
										</div>
										<small class="form-text text-muted">
											  <fmt:message key="draw.shapes.upload.help" />
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
								    	<fmt:message key="draw.shapes.catalog" />					    
								    </label>
								    <div class="col-sm-9">
								    		<select class="form-control" id="inputShapeCatalog" name="inputShapeCatalog" onchange="enabledShapeInput('inputShapeCatalog');">
										      	<c:forEach items="${data.catalog.entries}" var="entry">
										      		<option value="${entry.id}">${entry.title}</option>
										      	</c:forEach>
										    </select>
										    <small class="form-text text-muted">
												  <fmt:message key="draw.shapes.catalog.help" />
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
							    	<fmt:message key="draw.shapes.url" />
							    </label>
							    <div class="col-sm-9">
							      <input 
							      	type="text"
							      	class="form-control"
							      	id="inputShapeUrl"
							      	name="inputShapeUrl"
							      	placeholder="<fmt:message key="draw.shapes.url.placeholder" />"
							      	onkeypress="enabledShapeInput('inputShapeUrl');"
							      	onchange="enabledShapeInput('inputShapeUrl')"
							      >
							      <small class="form-text text-muted">
									  <fmt:message key="draw.shapes.url.help" />
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
							    	<fmt:message key="draw.shapes.inline" />
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
									  <fmt:message key="draw.shapes.inline.help" />
								  </small>
							    </div>	
						      </div>
					      </blockquote>					  
					  
					  
					  <h2><i class="fal fa-tools"></i>&nbsp;&nbsp;<fmt:message key="draw.options.title" /></h2>
				      <blockquote class="blockquote bq-warning">
				      <div class="form-group row">

							<label for="inputShapeCatalog" class="col-sm-3 col-form-label">
								<fmt:message key="draw.options.format" />					    
							</label>
						    <div class="col-sm-9">
					    		<select class="form-control" id="format" name="format" >
					    			<option value="svg">SVG</option>
					    			<option value="png">PNG</option>
					    			<option value="txt">TXT</option>
							    </select>
						    </div>
								    
					  </div>
					  </blockquote>
					  
				    <button type="submit" id="validate-button" class="btn btn-info btn-lg"><fmt:message key="draw.submit" /></button>			  	
			  	</form>
 		
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