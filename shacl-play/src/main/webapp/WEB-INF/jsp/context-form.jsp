<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!-- setup the locale for the messages based on the language in the session -->
<fmt:setLocale
	value="${sessionScope['fr.sparna.rdf.shacl.shaclplay.SessionData'].userLocale.language}" />
<fmt:setBundle basename="fr.sparna.rdf.shacl.shaclplay.i18n.shaclplay" />

<c:set var="data" value="${requestScope['ConvertFormData']}" />

<html>
	<head>
		<title><fmt:message key="window.app" /></title>
		<link rel="canonical" href="https://shacl-play.sparna.fr/play/context" />

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
			<jsp:param name="active" value="context" />
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
					
					<h1 class="display-3"><fmt:message key="context.title" /></h1>
					
					<div class="form-shortdesc">
						<p>
							This utility <em>generates a <a href="https://www.w3.org/TR/json-ld/#the-context">JSON-LD context document</a> from a SHACL specification</em>.
							Detailed documentation is available <a href="#documentation">below</a>.
						</p>
					</div>
					
					<form id="upload_form" action="context" method="POST" enctype="multipart/form-data" class="form-horizontal">
						<h2><i class="fal fa-shapes"></i>&nbsp;&nbsp;<fmt:message key="context.shapes.title" /></h2>
						
						<!-- Include shapes blockquote -->
					  	<%@ include file="include/shapes-blockquote.jsp" %>
						
						<!--  
						<h2><i class="fal fa-tools"></i>&nbsp;&nbsp;<fmt:message key="blockquote.options.title" /></h2>
				      	-->
				      	
				      	<button type="submit" id="validate-button" class="btn btn-info btn-lg"><fmt:message key="context.submit" /></button>
					</form>	
					
					<!-- Documentation -->	
					<div style="margin-top:3em;">
						<h3 id="documentation">Documentation</h3>
						
						
												
						<div style="margin-top:2em;">
							<h4 id="algorithm">JSON-LD context document generation algorithm</h4>
							<p>The algorithm follow these steps to generate the JSON-LD context:</p>
							<ol>
								<li>
									Always add a mapping from <code>id</code>, <code>type</code> and <code>graph</code> 
									to <code>@id</code>, <code>@type</code> and <code>@graph</code> respectively.</li>
								<li>
									Add <a href="https://www.w3.org/TR/json-ld/#compact-iris">prefix mappings</a> for each known prefixes in the SHACL file.
								</li>
								<li>
									Add mappings with the URI of NodeShapes in the SHACL, using the local part of the URI as JSON key.
								</li>
								<li>
									Add mappings with the URI of properties referred to in <code>sh:path</code> in the SHACL. SHACL property paths are not supported.
									By default, the local part of the property URI is used as key, but a different JSON key can be specified by annotating the property shape
									with the property <code>https://shacl-play.sparna.fr/ontology#shortname</code> (if the same property is referred to by multiple property shapes
									annotated with different shortname,	the first one is used). Then :
									<ol>
										<li>
											Determine the <code>@type</code> from datatypes : read the <code>sh:datatype</code> on property shapes referring to the property. 
											If there is one, use it as the value of @type (if the same property is referred to by multiple property shapes
											with different <code>sh:datatype</code>, the first one is used.)
										</li>
										<li>
											Determine the <code>@type</code> from URI reference : if there are <code>sh:class</code>, <code>sh:node</code>, or if
											<code>sh:nodeKind</code> = <code>sh:IRI</code> or <code>sh:BlankNodeOrIRI</code>, set the <code>@type</code> to <code>@id</code>
										</li>
									</ol>
								</li>
							</ol>
							
						</div>
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