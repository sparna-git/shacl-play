<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!-- setup the locale for the messages based on the language in the session -->
<fmt:setLocale value="${sessionScope['fr.sparna.rdf.shacl.shaclplay.SessionData'].userLocale.language}"/>
<fmt:setBundle basename="fr.sparna.rdf.shacl.shaclplay.i18n.shaclplay"/>

<c:set var="data" value="${requestScope['ShaclExcelFormData']}" />

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
		<jsp:param name="active" value="shaclexcel"/>
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
				
				<h1 class="display-3"><fmt:message key="shaclexcel.title" /></h1>	
				
				<div class="form-shortdesc">
					<p>You would like to edit SHACL, but you are wondering how to get started ? Start from Excel.</p>
				</div>
 				
 				<!-- Documentation -->	
				<div style="margin-top:3em;">
					<h3 id="documentation">How-to edit SHACL specifications from Excel files</h3>					
					
					<div style="margin-top:2em;">
						<h4 id="download">Download the template file</h4>
						<p>Start by downloading the template file <strong><a href="<c:url value="/resources/example/SHACL-template.xlsx"/>">here</a></strong></p>	
					</div>

					<div style="margin-top:2em;">
						<h4 id="fill-in">Fill-in the spreadsheet</h4>
						<p>The file has the following structure:</p>
						<ul>
							<li>In tab "prefix", declare the necessary prefixes</li>
							<li>In tab "NodeShapes", declare the NodeShapes with their <code>sh:targetClass</code> and other constraints</li>
							<li>In tab "PropertyShapes", make one "section" (blue line) per node shape, and declare the property shapes attached to each node shape.:
								<ul>
									<li>The link to the node shape is made in column <code>^sh:property</code>.</li>
									<li>The predicate (or path) is in column <code>sh:path</code>. SHACL property paths are supported, you need to write them as in Turtle, e.g. <code>[sh:inversePath foaf:knows]</code>.</li>
									<li>The URI of the property shape is computed automatically in column <code>URI</code>, based on the line number.</li>
									<li>SHACL constraints are in the corresponding columns <code>sh:minCount</code>, <code>sh:maxCount</code>, <code>sh:nodeKind</code>, <code>sh:datatype</code>, <code>sh:class</code>, <code>sh:node</code>, etc.</li>
									<li>You can add more columns as needed. See <a href="https://xls2rdf.sparna.fr/rest/doc.html">the detailled xls2rdf converter documentation</a> for all possible features.</li>
								</ul>
							</li>
						</ul>	
					</div>

					<div style="margin-top:2em;">
						<h4 id="convert">Convert the Excel using xls2rdf</h4>
						<p>Once you have filled in the Excel file, you can convert it to SHACL using the <a href="https://xls2rdf.sparna.fr">xsl2rdf converter</a>. The converter is available in different packagings : online API, command-line app, Java lib, and also an <a href="https://skos-play.sparna.fr/play/convert">online Excel conversion form</a> which is the most practical solution to start with :</p>
						<ol>
							<li>Upload your Excel file</li>
							<li>Check the box at the bottom "Ignore SKOS post-processings"</li>
							<li>Click on "Convert" and download the resulting SHACL file.</li>
						</ol>
					</div>

					<div style="margin-top:2em;">
						<h4 id="test">Test in the documentation generator</h4>
						<p>Test how your SHACL looks like by uploading it in <a href="doc">the documentation generator</a>.</p>
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
    </script>
    <script>
    	inputLogoCheckbox.onclick = function(){
    		  if(inputLogoCheckbox.checked){
    		    document.getElementById("inputLogo").disabled = false;
    		  }
    		  
    		  if(!inputLogoCheckbox.checked){
    		    document.getElementById("inputLogo").disabled = true;
    		  }
    		  
    		}
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