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
		<link rel="canonical" href="https://shacl-play.sparna.fr/jsonschema" />

		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		
		<!-- Font Awesome -->
		<link rel="stylesheet" href="<c:url value="/resources/fa/css/all.min.css" />">
		
		<link rel="stylesheet" href="<c:url value="/resources/MDB-Free/css/bootstrap.min.css" />">
		<link rel="stylesheet" href="<c:url value="/resources/MDB-Free/css/mdb.min.css" />">
		<link rel="stylesheet" href="<c:url value="/resources/jasny-bootstrap/jasny-bootstrap.min.css" />" />
		<link rel="stylesheet" href="<c:url value="/resources/css/shacl-play.css" />" />
		
		<link href="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/css/select2.min.css" rel="stylesheet" />
		<script src="https://ajax.googleapis.com/ajax/libs/jquery/2.1.1/jquery.min.js"></script>
		<script src="https://cdn.jsdelivr.net/npm/select2@4.1.0-rc.0/dist/js/select2.min.js"></script>
		<script src="https://cdnjs.cloudflare.com/ajax/libs/select2/4.0.8/js/select2.min.js" defer></script>
		

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
					  		<!-- Load Context -->
					  		<div class="form-group row">
					  			<label for="format" class="col-sm-3 col-form-label">
									<fmt:message key="schema.options.context" />
								</label>
								<div class="col-sm-9">
									<!--  
						    		<div class="fileinput fileinput-new input-group" data-provides="fileinput">
									  <div class="form-control" data-trigger="fileinput" id="inputFileContext">
									    <i class="fal fa-upload"></i><span class="fileinput-filename with-icon"></span>
									  </div>
									  <span class="input-group-append">
									    <span class="input-group-text fileinput-exists" data-dismiss="fileinput">
									      <fmt:message key="blockquote.rules.upload.remove" />
									    </span>
									
									    <span class="input-group-text btn-file">
									      <span class="fileinput-new"><fmt:message key="blockquote.rules.upload.select" /></span>
									      <span class="fileinput-exists"><fmt:message key="blockquote.rules.upload.change" /></span>
									      <input type="file" name="inputFileContext" multiple onchange="enabledShapeInput('inputFileContext')">
									    </span>
									  </span>
									</div>
									-->
									<textarea 
								      	class="form-control"
								      	id="inputContextInline"
								      	name="inputContextInline"
								      	rows="5"
								      	onkeypress="enabledShapeInput('inputContextInline');"
								      	onpaste="enabledShapeInput('inputContextInline');"
								      ></textarea>
									<small class="form-text text-muted">
									  <fmt:message key="schema.options.context.help" />
								  	</small>
						   	 	</div>
								
					  		</div>
					  		<!-- Load URI -->
					  		<div class="form-group row">
						  		<label for="format" class="col-sm-3 col-form-label">
									<fmt:message key="schema.options.url" />					    
								</label>
								<div class="col-sm-9">
							  		<select class="select2-select-multiple js-states form-control" 
							  				id="IdUrl" 
							  				onfocus="setInitial(this);" 
							  				multiple="multiple"
							  				name="IdUrl"
							  				style="height: 40%;">
							  				<option value=""><fmt:message key="schema.options.urlRoot.placeholder"/></option>
							  		</select>
							  		
						  			<small class="form-text text-muted">
										<fmt:message key="schema.options.url.help" />
									</small>
						  		</div>						  							  		
							</div>
							<!-- Ignore Properties Sh:In and sh:hasValues -->
							<div class="form-group row">
						      	<div class="col-sm-12">
							      	<div class="form-check">
									  <input class="form-check-input" type="checkbox" id="ignoreProperties" name="ignoreProperties" />
									  <label class="form-check-label" for="ignoreProperties">
									    <fmt:message key="schema.options.ignoreProperties" />
									  </label>
									  <small class="form-text text-muted">
										<fmt:message key="schema.options.ignoreProperties.help" />
									  </small>
									</div>
								</div>
							</div>
							
							<!-- Ignore Properties Sh:In and sh:hasValues -->
							<div class="form-group row">
						      	<div class="col-sm-12">
							      	<div class="form-check">
									  <input class="form-check-input" type="checkbox" id="optAddProperties" name="optAddProperties" />
									  <label class="form-check-label" for="optAddProperties">
									    <fmt:message key="schema.options.optAddProperties" />
									  </label>
									  <small class="form-text text-muted">
										<fmt:message key="schema.options.optAddProperties.help" />
									  </small>
									</div>
								</div>
							</div>
					  	</blockquote>
					  	
					  	<button type="submit" id="validate-button" class="btn btn-info btn-lg"><fmt:message key="schema.submit" /></button>
					</form>
					
					<!-- Documentation -->	
					<div style="margin-top:3em;">
						<h3 id="documentation">Documentation</h3>
						
						
												
						<div style="margin-top:2em;">
							<h4 id="algorithm">JSON Schema generation from SHACL</h4>
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
								<li>If there is an <code>sh:pattern</code> associated to the node shape, it is turned into a <code>pattern</code> constraint on the id key</li>
								<li>If there is a <code>skos:example</code> associated to the node shape, it is turned into an <code>example</code> constraint on the id key</li>
								<li>If node shape is closed, then <code>additionalProperties</code> is set to false, except if the flag "never set additional properties" is set.</li>
								<li>Then each property shape is processed as described below</li>
							</ul>
							<h5>Property shapes conversion</h5>
							<p>Non-deactivated property shapes are processed this way:</p>
							<ul>
								<li>If a JSON-LD context was provided, it is probed to determine the term for the property. The context probing works if it matches the property @type in the context
									(either an @id property, or the datatype for literal properties).</li>
								<li>If context was not provided, the corresponding JSON key is read from <code>shacl-play:shortName</code> annotation, otherwise the local name of the property in <code>sh:path</code> is used as the JSON key</li>
								<li><code>title</code> is populated from the property shape <code>sh:name</code></li>
								<li><code>description</code> is populated from the property shape <code>sh:description</code></li>
								
								<li>The property shape is mapped to a schema this way:
									<ul>
										<li>If the property shape has an <code>sh:hasValue</code>, a <a href="https://json-schema.org/understanding-json-schema/reference/const"><code>const</code></a> schema is created with the value.
										If a JSON-LD context was provided, an attempt is made to simplify the value to its actual mapping from the context, either because it is directly declared in the vocab, or because a prefix is declared.
										</li>
										<li>If the property shape has an <code>sh:in</code>, an <a href="https://json-schema.org/understanding-json-schema/reference/enum"><code>enum</code></a> schema is created with the list of possible values.
										If a JSON-LD context was provided, an attempt is made to simplify the list of possible values to their actual mapping from the context, either because they are directly declared in the vocab, or because a prefix is declared.</li>
										<li>If the property shape has an <code>sh:pattern</code>, a string schema is generated with a pattern constraint.
											If a JSON-LD context was provided, the pattern constraint is "reduced" so that patterns matching complete IRIs may match only the end of IRI if they are shortened due to @base in the context</li>
										<li>If the property shape has an <code>sh:node</code>, then :
											<ul>
												<li>If the property shape is annotated with <code>shacl-play:embed shacl-play:EmbedNever</code>, then a string schema with format <a href="https://json-schema.org/understanding-json-schema/reference/string#resource-identifiers"><code>iri-reference</code></a> is generated</li>
												<li>Otherwise, create a <a href="https://json-schema.org/understanding-json-schema/structuring#dollarref"><code>$ref</code></a> schema with a reference to one of the schemas in the <code>#/$defs</code> section</li>
												<li>Then the algorithm determines whether the property requires an array:
													<ul>
														<li>If a JSON-LD context was provided, try to compact a test of the property to determine if the context mandates a <code>@container : @set</code>.
															If the compaction test returns an array, then an array will be declared in the output schema
														</li>
														<li>Otherwise, if there is a <code>sh:qualifiedMaxCount</code> and it is > 1, then wrap the generated schema into an <a href="https://json-schema.org/understanding-json-schema/reference/array"><code>array</code></a> schema</li>
														<li>Otherwise, if there is no <code>sh:maxCount</code> or a <code>sh:maxCount</code> that is > 1, then wrap the generated schema into an <a href="https://json-schema.org/understanding-json-schema/reference/array"><code>array</code></a> schema</li>
													</ul>
												</li>
												
											</ul>
										</li>
										<li>If the property shape has an <code>sh:pattern</code>, then turn it into a string schema with a <a href="https://json-schema.org/understanding-json-schema/reference/string#regexp"><code>pattern</code></a> constraint.</li>
										<li>If the property shape has an <code>sh:datatype</code>, or an <code>sh:qualifiedValueShape</code> that has an <code>sh:datatype</code> then:
											<ul>
												<li>If it is rdf:langString, then make a reference to the <code>container_language</code> in the <code>#/$defs</code> section</li>
												<li>Create a string, <a href="https://json-schema.org/understanding-json-schema/reference/boolean">boolean</a> or <a href="https://json-schema.org/understanding-json-schema/reference/numeric">numeric</a> schema according to the <a href="https://github.com/sparna-git/shacl-play/blob/e4742a704dc919905db7613b6a6c35add75c11e4/shacl-doc/src/main/java/fr/sparna/jsonschema/DatatypeToJsonSchemaMapping.java">datatype-to-schema mapping</a></li>
											</ul>
										</li>
										<li>If the property shape has an <code>sh:nodeKind</code> pointing to sh:IRI, generate a string schema of format <a href="https://json-schema.org/understanding-json-schema/reference/string#resource-identifiers"><code>iri-reference</code></a>.</li>
										<li>Otherwise, return an <code>empty</code> schema</li>
									</ul>
								</li>
								<li>If the <code>sh:minCount</code>, or the <code>sh:qualifiedMinCount</code> of the property is > 0, the JSON key is added to the list of <code>requiredProperties</code> of the schema</li>
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

	    <script>
			
	    	// 
	    	document.getElementById("validate-button").disabled = true
	    	// anchors placement
			anchors.options = {
				  icon: '#'
				};
			anchors.options.placement = 'left';
			anchors.add();			
		</script>
		
		<script type="text/javascript">
	
			function setInitial(){
				var formData = new FormData(document.getElementById('upload_form'));
				
				const request = new XMLHttpRequest();
				request.open("POST", "rootShapes", true);
				request.onreadystatechange = () => {
					if (request.readyState === 4 && request.status === 200) {
						// $('select[name="IdUrl"]').prop( "disabled", false );
						$('select[id="IdUrl"]').empty();
						$('select[id="IdUrl"]').append('<option value=""></option>');
						$.each(JSON.parse(request.responseText), function(key, value) {
							$('select[id="IdUrl"]').append($('<option>').text(value).attr('value', value)).trigger("chosen:updated");            
						});
					}
				};
				request.send(formData);
				// populate Select2
		    	$(document).ready(function() {
			    	$('.select2-select-multiple').select2();
			    	// Enable button
			    	// document.getElementById("validate-button").disabled = false
				});				
			 }

			 $('.select2-select-multiple').on('change', function (e) {
				var data = $(this).select2('data');
				// check that the selected value is not the first empty option
			    if(data.length > 0){
			    	// Enable button
			    	document.getElementById("validate-button").disabled = false
				} else {
					document.getElementById("validate-button").disabled = true
				}
			});
			
		</script>
	</body>
</html>