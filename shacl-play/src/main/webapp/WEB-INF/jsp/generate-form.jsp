<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!-- setup the locale for the messages based on the language in the session -->
<fmt:setLocale
	value="${sessionScope['fr.sparna.rdf.shacl.shaclplay.SessionData'].userLocale.language}" />
<fmt:setBundle basename="fr.sparna.rdf.shacl.shaclplay.i18n.shaclplay" />

<c:set var="data" value="${requestScope['GenerateFormData']}" />

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
			<jsp:param name="active" value="generate" />
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
					
					<h1 class="display-3"><fmt:message key="generate.title" /></h1>
					
					<div class="form-shortdesc">
						<p>
							This algorithm <em>derives a set of SHACL constraints from an RDF dataset</em>. It can work from an uploaded RDF dataset, or from an online SPARQL endpoint.
							Detailed documentation is available <a href="#documentation">below</a>.
						</p>
					</div>
					
					<form id="upload_form" action="generate" method="POST" enctype="multipart/form-data" class="form-horizontal">
						<h2><i class="fal fa-chart-network"></i>&nbsp;&nbsp;<fmt:message key="generate.data.title" /></h2>						
						<!-- Include data blockquote -->
					  	<%@ include file="include/data-blockquote.jsp" %>
						
						<h2><i class="fa-light fa-cloud-binary"></i>&nbsp;&nbsp;<fmt:message key="generate.sparql.title" /></h2>
						<!-- Include endpoint blockquote -->
						<%@ include file="include/endpoint-blockquote.jsp" %>
						
						<h2><i class="fal fa-tools"></i>&nbsp;&nbsp;<fmt:message key="blockquote.options.title" /></h2>
				      	<blockquote class="blockquote bq-warning">
					      

						<!-- Generate labels option -->
						<div class="form-group row"> 	
							<div class="col-sm-12">
								<div class="form-check">
								<input class="form-check-input" type="checkbox" id="generateLabels" name="generateLabels" checked="true" />
								<label class="form-check-label" for="generateLabels">
								  <fmt:message key="generate.options.generateLabels" />
								</label>
								<small class="form-text text-muted">
								  <fmt:message key="generate.options.generateLabels.help" />
								</small>
							  </div>
						  </div>			  
						</div>

						<!-- Compute statistics option -->
						 <div class="form-group row"> 	
					      	<div class="col-sm-12">
						      	<div class="form-check">
								  <input class="form-check-input" type="checkbox" id="statistics" name="statistics" />
								  <label class="form-check-label" for="statistics">
								    <fmt:message key="generate.options.statistics" />
								  </label>
								  <small class="form-text text-muted">
									<fmt:message key="generate.options.statistics.help" />
								  </small>
								</div>
							</div>			  
						  </div>	
					      
					      <div class="form-group row">
					      	<div class="col-sm-9">
								<label for="format" class="col-sm-3 col-form-label">
									<fmt:message key="generate.options.format" />					    
								</label>
							    <div class="col-sm-4">
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
						
						<button type="submit" id="validate-button" class="btn btn-info btn-lg"><fmt:message key="generate.submit" /></button>
					</form>	
					
					<!-- Documentation -->	
					<div style="margin-top:3em;">
						<h3 id="documentation">Documentation</h3>
						
						
						<p>This algorithm was derived from <a href="https://github.com/cognizone/asquare/tree/develop/cube/src/main/java/zone/cogni/asquare/cube/convertor/data2shacl">this original one</a> implemented by <a href="https://www.cogni.zone/">Cognizone</a> here. Credits to them. It was improved in significant ways:</p>
						<ul>
							<li>Used a layered visitor patterns architecture for more modularity</li>
							<li>Used sampling technique to work with large datasets</li>
							<li>Improved NodeShape derivation algorithm to exclude certain types, when entities have multiple types</li>
							<li>Added counting of entities and properties</li>
						</ul>
						<p>This can work best if the dataset:</p>
						<ul>
							<li>Uses one and only one rdf:type value per entity (although the algorithm can be smart enough to exclude some types, see below)</li>
							<li>Contains only data, not the RDFS/OWL model</li>
						</ul>
						
						
						<div style="margin-top:2em;">
							<h4 id="algorithm">SHACL generation algorithm</h4>
							<p>The algorithm follow these steps to generate the SHACL:</p>
							
							<ol>
								<li>
									<strong>Find all types in the dataset</strong>.
									Relies on <a href="https://github.com/sparna-git/shacl-play/blob/master/shacl-generate/src/main/resources/shacl/generate/select-types.rq">this SPARQL query</a>.
									Generates one <code>sh:NodeShape</code> for each type, with <code>sh:targetClass</code> set to the type.</li>
								<li>
									For each found type, <strong>find all properties used on instances of this type</strong>.
									Relies on <a href="https://github.com/sparna-git/shacl-play/blob/master/shacl-generate/src/main/resources/shacl/generate/select-properties.rq">this SPARQL query</a>.
									Generates one <code>sh:PropertyShape</code> for each property on the type, with an <code>sh:path</code> set to this property.
								</li>
								<li>
									For each property shape previously found, <strong>determine its node kind (IRI or Literal)</strong>.
									Relies on <a href="https://github.com/sparna-git/shacl-play/blob/master/shacl-generate/src/main/resources/shacl/generate/nodekind-is-blank.rq">this SPARQL query</a>,
									<a href="https://github.com/sparna-git/shacl-play/blob/master/shacl-generate/src/main/resources/shacl/generate/nodekind-is-iri.rq">this one</a>,
									and <a href="https://github.com/sparna-git/shacl-play/blob/master/shacl-generate/src/main/resources/shacl/generate/nodekind-is-literal.rq">this one</a>.
									Generates the <code>sh:nodeKind</code> constraint on the property shape accordingly.
								</li>
								<li>
									For each property shape previously found with a sh:nodeKind IRI or BlankNode, <strong>determine the types of the property values</strong>.
									Relies on <a href="https://github.com/sparna-git/shacl-play/blob/master/shacl-generate/src/main/resources/shacl/generate/select-object-types.rq">this SPARQL query</a>.
									Generates the <code>sh:class</code> constraint on the property shape accordingly. If more than one class is found, the algorithm determines if some can be removed:
									<ul>
										<li>If one class is a superset of all other classes found, (indicating that the dataset uses some redundancy on the typing of instances, e.g. assigning skos:Concept
										and a subclass of skos:Concept to entities), but is a superset of other classes as well, then the this superset class (e.g. skos:Concept) is removed from the list, 
										and only the most precise class(-es) are kept.</li>
										<li>If one class is a superset of all other classes found, and is not a superset of other classes, then only the superset class is kept, and other more precise classes
										are removed from the list</li>
									</ul>
								</li>
								<li>
									For each property shape previously found with a sh:nodeKind Literal, <strong>determine the datatype and languages of the property values</strong>.
									Relies on <a href="https://github.com/sparna-git/shacl-play/blob/master/shacl-generate/src/main/resources/shacl/generate/select-datatypes.rq">this SPARQL query</a>,
									and <a href="https://github.com/sparna-git/shacl-play/blob/master/shacl-generate/src/main/resources/shacl/generate/select-languages.rq">this one</a>.
									Generates the <code>sh:datatype</code> and <code>sh:languageIn</code> constraints on the property shape accordingly.
								</li>
								<li>
									For each property shape previously found, <strong>determine the cardinalities of the property</strong>.
									Relies on <a href="https://github.com/sparna-git/shacl-play/blob/master/shacl-generate/src/main/resources/shacl/generate/has-instance-without-property.rq">this SPARQL query</a>,
									and <a href="https://github.com/sparna-git/shacl-play/blob/master/shacl-generate/src/main/resources/shacl/generate/has-instance-with-two-properties.rq">this one</a>.
									This can determine one minimum and maximum cardinalities set to 1.
									Generates the <code>sh:minCount</code> and <code>sh:maxCount</code> constraints on the property shape accordingly.
								</li>
								<li>
									For each property shape previously found, <strong>list the values of the property if it has a limited number of possible values</strong>.
									Relies on <a href="https://github.com/sparna-git/shacl-play/blob/master/shacl-generate/src/main/resources/shacl/generate/count-distinct-values.rq">this SPARQL query</a>.
									This is done only if the property has 3 distinct values or less.
									Generates an <code>sh:in</code> or <code>sh:hasValue</code> constraint on the property shape accordingly.
								</li>
								<li>
									For each node shape previously found, <strong>determines if one of the property shape is a label of the entity</strong>.
									If a property skos:prefLabel, foaf:name, dcterms:title, schema:name or rdfs:label (in this order) is found, mark it as a label. Otherwise, tries to find
									a literal property of datatype xsd:string or rdf:langString, with a sh:minCount 1; if only one is found, mark it as a label.
									Generates a <code>dash:propertyRole</code> with <code>dash:LabelRole</code> value accordingly.
								</li>
								<li>
									If requested, for each node shape and property shape previously found, <strong>count the number of instances of node shapes, number of occurrences of property shapes, and number of distinct values.</strong>.
									This currently works only with sh:targetClass target definition, but can be easily extended to deal with other target definition.
									Generates a <code>void:Dataset</code>, <code>void:classPartition</code>, <code>void:propertyPartition</code> with a <code>dcterms:conformsTo</code> pointing to the corresponding shapes.
									Stores the counting in either <code>void:entities</code>, <code>void:triples</code>, or <code>void:distinctObjects</code> properties.
								</li>
							</ol>
							
						</div>

						<div style="margin-top:2em;">
							<h4 id="algorithm">Modelling of datasets statistics</h4>
							<p>Here is an example of how statistics are expressed:</p>
							<pre>
@prefix void:  &lt;http://rdfs.org/ns/void#> .
@prefix dct:   &lt;http://purl.org/dc/terms/> .
@prefix xsd:   &lt;http://www.w3.org/2001/XMLSchema#> .
@prefix dcat:  &lt;http://www.w3.org/ns/dcat#> .
@prefix sh:    &lt;http://www.w3.org/ns/shacl#></http:>

# The dataset being analyzed
&lt;https://xxx/sparql>
	a                    void:Dataset ;
	# one partition is created per NodeShape
	void:classPartition  &lt;https://xxx/sparql/partition_Place> ;
	# Total number of triples in the Dataset
	void:triples         "11963716"^^xsd:int ;
	# A pointer to the URI of the shapes graph being used to generate these statistics
	sh:suggestedShapesGraph
	&lt;https://xxx/shapes/> .

# A "Node Shape partition", that is, a partition of the entire dataset corresponding to all
# targets of one NodeShape
&lt;https://xxx/partition_Place>
	# Link to the NodeShape
	dct:conformsTo          &lt;https://xxx/shapes/Place> ;
	# When the NodeShape actually targets instances of a class, the partition we are describing is 
	# actually a class partition, and we can indicate the class here
	void:class              &lt;https://www.ica.org/standards/RiC/ontology#Place> ;
	# Total number of targets of that shape in the dataset
	void:entities           "4551"^^xsd:int ;
	# One property partition is created per property shape in the node shape
	void:propertyPartition  &lt;https://xxx/partition_Place_label> , &lt;https://xxx/partition_Place_sameAs> .

# A "Property Shape partition", that is, a sub-partition of a "Node Shape partition" corresponding to all
# triples matching the path of the property
&lt;https://xxx/partition_Place_label>
	# a link ot the property shape
	dct:conformsTo        &lt;https://xxx/shapes/Place_label> ;
	# number of distinct values of the property shape
	void:distinctObjects  "17330"^^xsd:int ;
	# when the property shape as a simple path as a predicate, we can repeat it here
	# and our partition is actually a real property partition
	void:property         &lt;http://www.w3.org/2000/01/rdf-schema#label> ;
	# number of triples corresponding to the property shape
	void:triples          "17567"^^xsd:int .

&lt;https://xxx/partition_Place_sameAs>
	dct:conformsTo        &lt;https://xxx/shapes/Place_sameAs> ;
	void:distinctObjects  "14847"^^xsd:int ;
	void:property         &lt;http://www.w3.org/2002/07/owl#sameAs> ;
	void:triples          "14854"^^xsd:int .
							</pre>
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