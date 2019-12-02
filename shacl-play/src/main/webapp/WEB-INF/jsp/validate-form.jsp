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
	 
			  	<form id="upload_form" action="validate" method="POST" enctype="multipart/form-data" class="form-horizontal">
				      
				      <h2><i class="fal fa-share-alt"></i>&nbsp;&nbsp;<fmt:message key="validate.inputData.title" /></h2>
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
					      />
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
					    	<fmt:message key="validate.inputData.copypaste" />
					    </label>
					    <div class="col-sm-9">
					      <textarea class="form-control" id="inputInline" rows="5" onkeypress="enabledInput('inputInline');"></textarea>
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
										      		<option values="${entry.id}">${entry.title}</option>
										      	</c:forEach>
										    </select>
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
							      <input type="text" class="form-control" id="inputShapeUrl" name="inputShapeUrl" placeholder="<fmt:message key="validate.shapes.url.placeholder" />" onkeypress="enabledShapeInput('inputShapeUrl');">
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
							    	<fmt:message key="validate.shapes.copypaste" />
							    </label>
							    <div class="col-sm-9">
							      <textarea class="form-control" id="inputShapeInline" rows="5" onkeypress="enabledShapeInput('inputShapeInline');"></textarea>
							    </div>	
						      </div>
					      </blockquote>
					  </c:if>
					  <c:if test="${not empty data.selectedShapesKey}">
					  	<input type="hidden" name="shapesSource" value="url" />
					  	<input type="hidden" name="inputShapeUrl" value="${data.catalog.getCatalogEntryById(data.selectedShapesKey).turtleDownloadUrl}" />
					  </c:if>
					  
				    <button type="submit" id="validate-button" class="btn btn-info btn-lg"><fmt:message key="validate.validate" /></button>			  	
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