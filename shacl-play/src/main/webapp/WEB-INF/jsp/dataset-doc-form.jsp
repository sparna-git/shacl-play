<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!-- setup the locale for the messages based on the language in the session -->
<fmt:setLocale
	value="${sessionScope['fr.sparna.rdf.shacl.shaclplay.SessionData'].userLocale.language}" />
<fmt:setBundle basename="fr.sparna.rdf.shacl.shaclplay.i18n.shaclplay" />

<c:set var="data" value="${requestScope['DatasetDocFormData']}" />

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
			<jsp:param name="active" value="dataset-doc" />
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
					
					<h1 class="display-3"><fmt:message key="dataset-doc.title" /></h1>
					
					<div class="form-shortdesc">
						<p>
							
						</p>
					</div>
					
					<form id="upload_form" action="dataset-doc" method="POST" enctype="multipart/form-data" class="form-horizontal">
						<h2><i class="fal fa-chart-network"></i>&nbsp;&nbsp;<fmt:message key="dataset-doc.dataset.title" /></h2>
						
						<!-- Include data blockquote -->
					  	<%@ include file="include/data-blockquote.jsp" %>												
						
						<h2><i class="fal fa-tools"></i>&nbsp;&nbsp;<fmt:message key="blockquote.options.title" /></h2>
				      	<blockquote class="blockquote bq-warning">
							<!-- Language -->
					      	<div class="form-group row">
								<label for="language" class="col-sm-3 col-form-label">
									<fmt:message key="dataset-doc.options.language"/>				    
								</label>
							    <div class="col-sm-9">
							    	<input list="languageOption" name="language" style="width:4em;" value="en">
							    	<small class="form-text text-muted">
									  <fmt:message key="dataset-doc.options.language.help"/>
									</small>
							    </div>									    
							</div>
							
							<div class="form-group row">
								<label for="format" class="col-sm-3 col-form-label">
									<fmt:message key="dataset-doc.options.format" />
								</label>
								<div class="col-sm-4">
									<select class="form-control" id="format" name="format">
										<option value="HTML">HTML</option>
										<option value="PDF">PDF</option>
										<option value="XML">XML</option>
									</select>
								</div>
							</div>
						</blockquote>
						  
						<button type="submit" id="validate-button" class="btn btn-info btn-lg"><fmt:message key="dataset-doc.submit" /></button>
					</form>	
					
					<!-- Documentation -->	
					<div style="margin-top:3em;">
						<h3 id="documentation">Documentation</h3>
						
					</div>				
				</div>
			</div>
		</div>
		<!-- /.container-fluid -->


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
	
		<script type="text/javascript"src="<c:url value="/resources/jasny-bootstrap/jasny-bootstrap.min.js" />"></script>


		<!-- anchorjs -->
    	<script src="https://cdn.jsdelivr.net/npm/anchor-js/anchor.min.js"></script>

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