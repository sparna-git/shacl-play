<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!-- setup the locale for the messages based on the language in the session -->
<fmt:setLocale
	value="${sessionScope['fr.sparna.rdf.shacl.shaclplay.SessionData'].userLocale.language}" />
<fmt:setBundle basename="fr.sparna.rdf.shacl.shaclplay.i18n.shaclplay" />

<c:set var="data" value="${requestScope['ExcelFormData']}" />

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
				//document.getElementById('shapesSource-' + selected).checked = true;
				
				document.getElementById('inputShapeUrlTemplate').disabled = selected != 'inputShapeUrlTemplate';
				document.getElementById('inputShapeUrlSource').disabled = selected != 'inputShapeUrlSource';
				
				document.getElementById('inputShapeFileTemplate').disabled = selected != 'inputShapeFileTemplate';
				document.getElementById('inputShapeFileSource').disabled = selected != 'inputShapeFileSource';
				
				
			}
		</script>


	</head>
	<body>

		<jsp:include page="navbar.jsp">
			<jsp:param name="active" value="xls" />
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
					
					<h1 class="display-3"><fmt:message key="excel.title" /></h1>
					
					<form id="upload_form" action="xls" method="POST" enctype="multipart/form-data" class="form-horizontal">
						<h2><i class="fal fa-shapes"></i>&nbsp;&nbsp;<fmt:message key="excel.shapes.title" /></h2>
						
						<blockquote class="blockquote bq-primary">
							
							<div class="form-group row">
								<label for="inputShapeFileTemplate" class="col-sm-3 col-form-label">
							    	<!--  
							    	<input 
							    			type="radio"
							    			name="shapesSource"
											id="shapesSource-inputShapeFileTemplate"
											value="file"
											checked="checked"
											onchange="enabledShapeInput('inputShapeFileTemplate')" />
											-->
							    	 <fmt:message key="excel.shapes.upload.template" />
							    	
							    </label>
							    <div class="col-sm-9">
							    		<div class="fileinput fileinput-new input-group" data-provides="fileinput">
										  <div class="form-control" data-trigger="fileinput" id="inputShapeFileTemplate">
										    <i class="fal fa-upload"></i><span class="fileinput-filename with-icon"></span>
										  </div>
										  <span class="input-group-append">
										    <span class="input-group-text fileinput-exists" data-dismiss="fileinput">
										      <fmt:message key="excel.shapes.upload.template.remove" />
										    </span>
										
										    <span class="input-group-text btn-file">
										      <span class="fileinput-new"><fmt:message key="excel.shapes.upload.template.select" /></span>
										      <span class="fileinput-exists"><fmt:message key="excel.shapes.upload.template.change" /></span>
										      <input type="file" name="inputShapeFileTemplate" multiple onchange="enabledShapeInput('inputShapeFileTemplate')">
										      
										    </span>
										  </span>
										</div>
										<small class="form-text text-muted">
											  <fmt:message key="excel.shapes.upload.template.help" />
									  </small>
							    </div>
							</div>
							
							
							<div class="form-group row">
								<label for="inputShapeFileSource" class="col-sm-3 col-form-label">
							    	<!--
							    	<input
											
											name="shapesSource"
											id="shapesSource-inputShapeFileSource"
											value="file"											
											onchange="enabledShapeInput('inputShapeFileSource')" />
									 type="radio" -->
							    	<fmt:message key="excel.shapes.upload.source" />
							    
							    </label>
							    <div class="col-sm-9">
							    		<div class="fileinput fileinput-new input-group" data-provides="fileinput">
										  <div class="form-control" data-trigger="fileinput" id="inputShapeFileSource">
										    <i class="fal fa-upload"></i><span class="fileinput-filename with-icon"></span>
										  </div>
										  <span class="input-group-append">
										    <span class="input-group-text fileinput-exists" data-dismiss="fileinput">
										      <fmt:message key="excel.shapes.upload.source.remove" />
										    </span>
										
										    <span class="input-group-text btn-file">
										      <span class="fileinput-new"><fmt:message key="excel.shapes.upload.source.select" /></span>
										      <span class="fileinput-exists"><fmt:message key="excel.shapes.upload.source.change" /></span>
										      <input type="file" name="inputShapeFileSource" multiple onchange="enabledShapeInput('inputShapeFileSource')">
										    </span>
										  </span>
										</div>
										<small class="form-text text-muted">
											  <fmt:message key="excel.shapes.upload.source.help" />
									  </small>
							    </div>
							</div>
							
							<!--  URL -->
							<div class="form-group row">
								
								
							    <label for="inputShapeUrlTemplate" class="col-sm-3 col-form-label">
							    	<!--
							    	<input name="shapesSource"
											id="sourceShape-inputShapeUrlTemplate"
											value="url"
											onchange="enabledShapeInput('inputShapeUrlTemplate')" />
									 type="radio" -->
							    	<fmt:message key="excel.shapes.url.template" />
							    </label>
							    
							    <div class="col-sm-9">
							      <input 
							      	type="text"
							      	class="form-control"
							      	id="inputShapeUrlTemplate"
							      	name="inputShapeUrlTemplate"
							      	placeholder="<fmt:message key="excel.shapes.url.template.placeholder" />"
							      	onkeypress="enabledShapeInput('inputShapeUrlTemplate');"
							      	onchange="enabledShapeInput('inputShapeUrlTemplate')"
							      >
							      <small class="form-text text-muted">
									  <fmt:message key="excel.shapes.url.template.help" />
							    </small>
							    </div>
							  </div>
							  
							  <div class="form-group row">
								
								  
							    <label for="inputShapeUrlSource" class="col-sm-3 col-form-label">
							    	<!-- 
							    	<input name="shapesSource"
											id="sourceShape-inputShapeUrlSource"
											value="url"
											onchange="enabledShapeInput('inputShapeUrlSource')" />
							    	-->	
							    	<fmt:message key="excel.shapes.url.source" />
							    </label>
							    
							    <div class="col-sm-9">
							      <input 
							      	type="text"
							      	class="form-control"
							      	id="inputShapeUrlSource"
							      	name="inputShapeUrlSource"
							      	placeholder="<fmt:message key="excel.shapes.url.source.placeholder" />"
							      	onkeypress="enabledShapeInput('inputShapeUrlSource');"
							      	onchange="enabledShapeInput('inputShapeUrlSource')"
							      >
							      <small class="form-text text-muted">
									  <fmt:message key="excel.shapes.url.source.help" />
							    </small>
							    </div>
							  </div>
							   
						</blockquote>
						
							<h2><i class="fal fa-tools"></i>&nbsp;&nbsp;<fmt:message key="excel.options.title" /></h2>
				      	
						</blockquote>
						
						<button type="submit" id="validate-button" class="btn btn-info btn-lg"><fmt:message key="excel.submit" /></button>
					</form>					
				</div>
			</div>
		</div>
		<!-- /.container-fluid -->


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
	
		<script type="text/javascript"src="<c:url value="/resources/jasny-bootstrap/jasny-bootstrap.min.js" />"></script>


		<script>
			$(document).ready(function() {
				$('#htmlOrRdf a').click(function(e) {
					e.preventDefault();
					$(this).tab('show')
				});
	
				// Initialize CodeMirror editor and the update callbacks
				var sourceText = document.getElementById('text');
				var editorOptions = {
					mode : 'text/html',
					tabMode : 'indent'
				};
	
				// CodeMirror commented for now
				// var editor = CodeMirror.fromTextArea(sourceText, editorOptions);
				// editor.on("change", function(cm, event) { enabledInput('text'); });
			});
		</script>

	</body>
</html>