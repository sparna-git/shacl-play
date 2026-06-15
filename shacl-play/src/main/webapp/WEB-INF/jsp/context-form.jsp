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
									to <code>@id</code>, <code>@type</code> and <code>@graph</code> respectively. The <code>type</code> mapping is added only if no property shape in the graph is already <code>rdf:type</code>
								    to avoid conflicting declarations.</li>
								<li>
									Add <a href="https://www.w3.org/TR/json-ld/#compact-iris">prefix mappings</a> for each known prefixes in the SHACL file.
								</li>
								<li>
									Add mappings with the URI of targets of NodeShapes in the SHACL (reading <code>sh:targetClass</code>, or for shapes that are themselves classes), using the local part of the URI as JSON key.
								</li>
								<li>
									Add mappings with the URI of properties referred to in <code>sh:path</code> in the SHACL. Only direct property URIs, or inverse path are supported, and more complex paths are ignored.
									By default, the local part of the property URI is used as key, but a different JSON key can be specified by annotating the property shape
									with the property <code>https://shacl-play.sparna.fr/ontology#shortname</code> (if the same property is referred to by multiple property shapes
									annotated with different shortnames, then multiple mappings are generated). Then :
									<ol>
										<li>
											If the property path is an inverse path, use <code>@reverse</code> in the mapping.
										</li>
										<li>
											If the property is <code>rdf:type</code>, map the shortname to <code>@type</code> instead of the property URI, so that compaction algorithm can compact it.
											Otherwise map to the property URI.
										</li>
										<li><u>For every reading of a property characteristic below (<code>sh:datatype</code>, <code>sh:pattern</code>, <code>sh:languageIn</code>, <code>sh:class</code>, <code>sh:nodeKind</code>), the lookup is always done on the property shape(s) 
										but also on the node shape being referred to by <code>sh:node</code> on these property shapes.</u></li>
										<li>
											Determine the <code>@type</code> from datatypes : read the <code>sh:datatype</code>. If there is one, use it as the value of @type (if the same property is referred to by multiple property shapes
											with different <code>sh:datatype</code>, the first one is used, and a warning is output).
											<ol>
												<li>If the datatype starts with <code>http://www.w3.org/2001/XMLSchema#</code> but is not <code>xsd:string</code>, use the value <code>xsd:xxxxx</code> as the value of @type</li>
												<li>If the datatype is <code>rdf:langString</code>, then if there is a single value for <code>sh:languageIn</code>, then use this language in a <code>@language</code> annotation on this mapping, otherwise set <code>@container: @language</code> on this mapping</li>
												<li>Otherwise, set the short form of the datatype URI as the value of @type</li>
											</ol>
										</li>
										<li>
											Determine the <code>@type</code> from URI reference : if there is a <code>sh:class</code>, or if
											<code>sh:nodeKind</code> is <code>sh:IRI</code> or <code>sh:BlankNodeOrIRI</code>, set the <code>@type</code> to <code>@id</code> (note : <code>sh:node</code> is not considered here as it can point to a node shape actually describing a literal value).
										</li>
										<li>
											In order to have short values for IRI references in the JSON, determine is there is a common URI prefix for the values of the property in order to set an inner context. Search for a <code>sh:pattern</code> (on property shape reference node shape through <code>sh:node</code>).
											<ol>
												<li>
													If there is one, and if is a regex describing the beginning of an http or https URI, add a scoped context for this property with a <code>@vocab</code> + <code>@base</code> 
													indication using the beginning of the URI extracted from the regex, and set the <code>@type</code> to <code>@vocab</code>.
													See <a href="https://github.com/sparna-git/shacl-play/issues/301">this discussion</a> for why both <code>@vocab</code> and <code>@base</code> are set.
												</li>
												<li>Otherwise, if there is some <code>sh:or</code> on the property shape(s) using this predicate (or associated with this shortname), then read the <code>sh:pattern</code> on each of them, and determine if there is a single common root for all of the them. If there is, then use it to set <code>@vocab</code> + <code>@base</code> on an inner <code>@context</code></li>
											</ol>
											
										</li>
										<li>
											Determine if <code>@container: @set</code> should be added : if no other value for <code>@container</code> was set (e.g. with <code>@language</code>), then by default set <code>@container: @set</code> unles :
											<ol>
												<li>no <code>sh:maxCount</code> is found, but there is an <code>sh:hasValue</code>, indicating that the property cannot have multiple values.</li>
												<li>all occurrences of the property in property shapes do have an <code>sh:maxCount</code>, and all the values are 1.</li>
											</ol>	
											
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