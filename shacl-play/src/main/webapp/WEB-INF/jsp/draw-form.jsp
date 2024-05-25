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
		
	</head>
	<body>

	<jsp:include page="include/navbar.jsp">
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
				
				<div class="form-shortdesc">
					<p>This diagram generation utility displays an <em>application profile specified in SHACL</em> in the form of one or more diagrams,
					using <a href="https://plantuml.com/fr/class-diagram">PlantUML</a>. It supports a subset of SHACL constraints.
					The diagrams generated can be embedded in the <a href="doc">documentation generation utility.</a>
					Detailed documentation is available <a href="#documentation">below</a>.
					</p>
				</div>
	 
			  	<form id="upload_form" action="draw" method="POST" enctype="multipart/form-data" class="form-horizontal">

					  <h2><i class="fal fa-shapes"></i>&nbsp;&nbsp;<fmt:message key="draw.shapes.title" /></h2>
					  <!-- Include shapes blockquote -->
					  <%@ include file="include/shapes-blockquote.jsp" %>
					  
					  <h2><i class="fal fa-tools"></i>&nbsp;&nbsp;<fmt:message key="blockquote.options.title" /></h2>
				      <blockquote class="blockquote bq-warning">
				      <div class="form-group row">

							<label for="format" class="col-sm-3 col-form-label">
								<fmt:message key="draw.options.format" />					    
							</label>
						    <div class="col-sm-9">
					    		<select class="form-control" id="format" name="format" >
					    			<option value="SVG">SVG</option>
					    			<option value="PNG">PNG</option>
					    			<option value="TXT">TXT</option>
					    			<option value="HTML">HTML (with SVG inside) (includes all diagrams)</option>
							    </select>
						    </div>
								    
					  </div>
					  </blockquote>
					  
				    <button type="submit" id="validate-button" class="btn btn-info btn-lg"><fmt:message key="draw.submit" /></button>			  	
			  	</form>
 		
 		
 				<!-- Documentation -->	
				<div style="margin-top:3em;">
					<h3 id="documentation">Documentation</h3>
					
					<div style="margin-top:2em;">
						<h4 id="arrows">Finding links between boxes</h4>
						<p>The diagram generation utility will generate a link between 2 boxes if it finds:</p>
						<ul>
							<li>An <code>sh:node</code> reference to another NodeShape</li>
							<li>An <code>sh:class</code> reference that is the <code>sh:targetClass</code> of another NodeShape</li>
							<li>An <code>sh:class</code> reference that is not the <code>sh:targetClass</code> of another NodeShape (in that case a box will be generated corresponding to the sh:class)</li>
							<li>An <code>sh:or</code> containing sh:node or sh:class resolved using the same algorithm as above</li>
						</ul>	
					</div>
					
					<div style="margin-top:2em;">
						<h4 id="simplification">Limiting the number of arrows/boxes</h4>
						<p>If a property shape refer to a node shape that does not have any property, and only one reference is made to that node shape, then the arrow will
						not be printed and will be replaced by a property inside the node shape box.</p>	
					</div>

					<div style="margin-top:2em;">
						<h4 id="custom-nodeShapes">Customizing NodeShapes</h4>
						<p>In addition to SHACL constraints, the following properties can be used to customise the output of NodeShapes</p>
						<table class="table table-bordered">
							<thead>
								<tr align="center">
									<th scope="col">Property</th>
								    <th scope="col">Type</th>
								    <th scope="col">Description</th>
								</tr>
							</thead>
							<tbody>
								<tr>
									<th scope="row" width="30%"><code>foaf:depiction</code><br />+ <code>dcterms:title</code> <br />+ <code>dcterms:description</code><br />+ <code>sh:order</code></th>
									<td>IRI or blank node</td>
									<td class="text-break">
									<p>Indicates the IRI of a diagram in which the NodeShape will appear. This allows to split the visual representation
									in multiple diagrams.</p>
									<p>If <code>foaf:depiction</code> is never used on any NodeShape
									in the SHACL data, a single diagram will be generated with all the NodeShapes. If <code>foaf:depiction</code> is used at least once on a NodeShape,
									then one diagram will be generated for each value of <code>foaf:depiction</code>, and each diagram will include only the NodeShapes that refer
									to it.</p>
									<p>Each diagram can be described with <code>dcterms:title</code> and <code>dcterms:description</code> to indicate its title and description. 
									<code>sh:order</code> can be used with an xsd:integer value to control the order of the diagrams.</p>
									<p>Depending on the output format option, you will get a ZIP file containing all PNGs or SVGs, or an HTML page containing all generated diagrams.</p>
									</td>
								</tr>
								<tr>
									<th scope="row" width="30%"><code>rdfs:subClassOf</code></th>
									<td>IRI</td>
									<td class="text-break">
									<code>rdfs:subClassOf</code> links are displayed in bold grey. It implies the NodeShapes are also classes, and it of course implies that the submitted
									file contains the <code>rdfs:subClassOf</code> links.
									</td>
								</tr>
								<tr>
									<th scope="row" width="30%"><code>https://shacl-play.sparna.fr/ontology#background-color</code></th>
									<td>xsd:string</td>
									<td class="text-break">Color of the box itself. Values can be a color name, like <code>"Green"</code> or an hex code, like <code>"4456BB"</code>
									</td>
								</tr>
								<tr>
									<th scope="row" width="30%"><code>https://shacl-play.sparna.fr/ontology#color</code></th>
									<td>xsd:string</td>
									<td class="text-break">Color of the text inside the box. Values can be a color name, like <code>"Green"</code> or an hex code, like <code>"4456BB"</code>
									</td>
								</tr>
							</tbody>
						</table>
					</div>
					
					<div style="margin-top:2em;">
						<h4 id="custom-propertyShapes">Customizing PropertyShapes</h4>
						<p>In addition to SHACL constraints, the following properties can be used to customize the appearance of Property Shapes</p>
						<table class="table table-bordered">
							<thead>
								<tr align="center">
									<th scope="col">Property</th>
								    <th scope="col">Type</th>
								    <th scope="col">Description</th>
								</tr>
							</thead>
							<tbody>
								<tr>
									<th scope="row" width="30%"><code>sh:order</code></th>
									<td>xsd:integer</td>
									<td class="text-break">
									Properties are sorted according to sh:order
									</td>
								</tr>
								<tr>
									<th scope="row" width="30%"><code>https://shacl-play.sparna.fr/ontology#color</code></th>
									<td>xsd:string</td>
									<td class="text-break">Color of the property name (inside the box) or arrow link. Values can be a color name, like <code>"Green"</code>
									or an hex code, like <code>"4456BB"</code>
									</td>
								</tr>
								<tr>
									<th scope="row" width="30%"><code>owl:inverseOf</code> on the IRI indicated on <code>sh:path</code></th>
									<td>IRI</td>
									<td class="text-break">If the property indicated in <code>sh:path</code> is known to have an inverse, then a single arrow
									will be printed instead of 2.</td>
								</tr>
							</tbody>
						</table>
					</div>

					<div style="margin-top:2em;">
						<h4 id="gallery">Diagram gallery</h4>

						<p>
							<a href="https://github.com/euBusinessGraph/eubg-data/raw/master/model/ebg-shapes.shacl">EBG shapes</a>
							<br/>
							<img src="<c:url value="/resources/img/diagram-ebg-shapes.png"/>" width="100%"/>
						</p>

						<p>
							<a href="https://gist.githubusercontent.com/tfrancart/cd7af66ba629352f353bec49b76efb98/raw/ScoLOMFR-V6-AP-SHACL.ttl">ScoloMFR Application profile</a>, SKOS based
							<br/>
							<img src="<c:url value="/resources/img/diagram-ScoLOMFR-V6-AP-SHACL.png"/>" width="100%"/>
						</p>

						<p>
							<a href="https://docs.google.com/spreadsheets/d/195NKb43Ck1yPGrIK4H8_HYGw9GTPnbtY">Sparnatural configuration profile</a>
							<br/>
							<img src="<c:url value="/resources/img/diagram-sparnatural-configuration.png"/>" width="100%"/>
						</p>

					</div>

				</div>
				

 		
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

    <!-- anchorjs -->
    <script src="https://cdn.jsdelivr.net/npm/anchor-js/anchor.min.js"></script>
    
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
    	
		anchors.options = {
			  icon: '#'
			};
		anchors.options.placement = 'left';
		anchors.add();		
    </script>
    
  </body>
</html>