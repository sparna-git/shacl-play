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
			<jsp:param name="active" value="schema" />
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
					
					<h1 class="display-3"><fmt:message key="schema.title" /></h1>
					
					<div class="form-shortdesc">
						<p>
							This utility <em>generates a <a href="https://json-schema.org/">JSON Schema document</a> from a SHACL specification</em>.
							Detailed documentation is available <a href="#documentation">below</a>.
						</p>
					</div>
					
					<form id="upload_form" action="jsonschema" method="POST" enctype="multipart/form-data" class="form-horizontal">
						<h2><i class="fal fa-shapes"></i>&nbsp;&nbsp;<fmt:message key="schema.shapes.title" /></h2>
						
						<!-- Include shapes blockquote -->
					  	<%@ include file="include/shapes-blockquote.jsp" %>
					  	
					  	<h2><i class="fal fa-tools"></i>&nbsp;&nbsp;<fmt:message key="blockquote.options.title" /></h2>
					  	
					  	<blockquote class="blockquote bq-warning">
					      	<label for="format" class="col-sm-3 col-form-label">
								<fmt:message key="schema.options.url" />					    
							</label>
							<div class="col-sm-9">
							      <input 
							      	type="text"
							      	class="form-control"
							      	id="IdUrl"
							      	name="IdUrl"
							      	placeholder="<fmt:message key="schema.options.urlRoot.placeholder" />"
							      	onkeypress="enabledShapeInput('IdUrl');"
							      	onchange="enabledShapeInput('IdUrl')"
							      >
							      <small class="form-text text-muted">
									  <fmt:message key="schema.options.url.help" />
							    </small>
							</div>
						</blockquote>
					  	
						
						<button type="submit" id="validate-button" class="btn btn-info btn-lg"><fmt:message key="schema.submit" /></button>
					</form>	
					
					<!-- Documentation -->	
					<div style="margin-top:3em;">
						<h3 id="documentation">Documentation</h3>
						
						
												
						<div style="margin-top:2em;">
							<h4 id="algorithm">JSON Schema document generation algorithm</h4>
							<p>The algorithm follow these steps to generate the JSON Schema:</p>
							<h5>Constant declarations</h5>
							<ul>
								<li>A declaration is always added for <code>@context</code> since we are targeting JSON-LD</li>
								<li>schema version is always set to <code>https://json-schema.org/draft/2020-12/schema</code></li>
								<li>a <code>container_language</code> declaration is always added with a <code>patternProperty</code> declaration with properties
									being language codes to deal with <code>"@container": "language"</code> properties.								
								</li>
							</ul>
							<h5>Schema header</h5>
							<ul>
								<li><code>title</code> is populated from an owl:Ontology <code>dct:title</code> or <code>rdfs:label</code></li>
								<li><code>version</code> is populated from an owl:Ontology <code>owl:versionInfo</code></li>
								<li><code>description</code> is populated from an owl:Ontology <code>dct:description</code></li>
							</ul>
							<h5>Node shapes conversion</h5>
							<p>Each node shape with at least one non-deactivated property shape is turned into an object schema in the <a href="https://json-schema.org/understanding-json-schema/structuring#defs"><code>$defs</code></a> section of the schema, with the URI 
								local name as the name of the schema. Node shapes with no non-deactivated property shapes are turned into string schemas being simple <a href="https://json-schema.org/understanding-json-schema/reference/string#resource-identifiers"><code>iri-reference</code></a>.</p>
							<ul>
								<li><code>title</code> is populated from the node shape <code>rdfs:label</code></li>
								<li><code>description</code> is populated from the node shape <code>rdfs:comment</code></li>
								<li>There is always a required <code>id</code> property</li>
								<li>If node shape is closed, then <code>additionalProperties</code> is set to false</li>
								<li>Then each property shape is processed as described below</li>
							</ul>
							<h5>Property shapes conversion</h5>
							<p>Non-deactivated property shapes are processed this way:</p>
							<ul>
								<li>The corresponding JSON key is read from <code>shacl-play:shortName</code> annotation, otherwise the local name of the property in sh:path is used as the JSON key</li>
								<li><code>title</code> is populated from the property shape <code>sh:name</code></li>
								<li><code>description</code> is populated from the property shape <code>sh:description</code></li>
								
								<li>The property shape is mapped to a schema this way:
									<ul>
										<li>If the property shape has an <code>sh:hasValue</code>, a <a href="https://json-schema.org/understanding-json-schema/reference/const"><code>const</code></a> schema is created with the value</li>
										<li>If the property shape has an <code>sh:in</code>, an <a href="https://json-schema.org/understanding-json-schema/reference/enum"><code>enum</code></a> schema is created with the list of possible values</li>
										<li>If the property shape has an <code>sh:node</code>, then :
											<ul>
												<li>If the property shape is annotated with <code>shacl-play:embed shacl-play:EmbedNever</code>, then a string schema with format <a href="https://json-schema.org/understanding-json-schema/reference/string#resource-identifiers"><code>iri-reference</code></a> is generated</li>
												<li>Otherwise, create a <a href="https://json-schema.org/understanding-json-schema/structuring#dollarref"><code>$ref</code></a> schema with a reference to one of the schemas in the <code>#/$defs</code> section</li>
												<li>If there is no sh:maxCount or a sh:maxCount that is > 1, then wrap the generated schema into an <a href="https://json-schema.org/understanding-json-schema/reference/array"><code>array</code></a> schema</li>
											</ul>
										</li>
										<li>If the property shape has an <code>sh:pattern</code>, then turn it into a string schema with a <a href="https://json-schema.org/understanding-json-schema/reference/string#regexp"><code>pattern</code></a> constraint.</li>
										<li>If the property shape has an <code>sh:datatype</code> (with a consistent value for the same property across the SHACL spec), then:
											<ul>
												<li>If it is rdf:langString, then make a reference to the <code>container_language</code> in the <code>#/$defs</code> section</li>
												<li>Create a string, <a href="https://json-schema.org/understanding-json-schema/reference/boolean">boolean</a> or <a href="https://json-schema.org/understanding-json-schema/reference/numeric">numeric</a> schema according to the <a href="https://github.com/sparna-git/shacl-play/blob/e4742a704dc919905db7613b6a6c35add75c11e4/shacl-doc/src/main/java/fr/sparna/jsonschema/DatatypeToJsonSchemaMapping.java">datatype-to-schema mapping</a></li>
											</ul>
										</li>
										<li>If the property shape has an <code>sh:nodeKind</code> pointing to sh:IRI, generate a string schema of format <a href="https://json-schema.org/understanding-json-schema/reference/string#resource-identifiers"><code>iri-reference</code></a>.</li>
										<li>Otherwise, return an <code>empty</code> schema</li>
									</ul>
								</li>
								<li>If the <code>sh:minCount</code> is > 0, the JSON key is added to the list of <code>requiredProperties</code> of the schema</li>
							</ul>
							<h5>Root node shape</h5>
							<p>With the provided root node shape IRI, create a reference to the corresponding schema from the <code>#/$defs</code> section.</p>
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