<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!-- setup the locale for the messages based on the language in the session -->
<fmt:setLocale value="${sessionScope['fr.sparna.rdf.shacl.shaclplay.SessionData'].userLocale.language}"/>
<fmt:setBundle basename="fr.sparna.rdf.shacl.shaclplay.i18n.shaclplay"/>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title><fmt:message key="window.app" /></title>
	
    <!-- Font Awesome -->
    <link rel="stylesheet" href="<c:url value="/resources/fa/css/all.min.css" />">
    
	<link rel="stylesheet" href="<c:url value="/resources/MDB-Free/css/bootstrap.min.css" />">
	<link rel="stylesheet" href="<c:url value="/resources/MDB-Free/css/mdb.min.css" />">
	<link rel="stylesheet" href="<c:url value="/resources/css/shacl-play.css" />">
</head>
<body>
	<jsp:include page="navbar.jsp">
		<jsp:param name="active" value="home"/>
	</jsp:include>

    <div class="container-fluid">
    
    	<div class="row justify-content-md-center">
            <div class="col-10">
            	<h1 class="display-3"><i class="fal fa-home"></i>&nbsp;SHACL Play!</h1>
            	<p>Free online RDF data validation with <a href="https://www.w3.org/TR/shacl/">SHACL</a>. SHACL Play! embeds <a href="https://github.com/TopQuadrant/shacl" target="_blank">TopBraid SHACL API</a> from <a href="https://www.topquadrant.com/" target="_blank">TopQuadrant</a>.<p>

            	
				<div class="row">
  					<div class="col-4" style="margin-bottom: 2em;">  
					  <div class="card">
					    <a href="<c:url value="/validate" />"><img src="<c:url value="/resources/img/home-report.png" />" class="card-img-top" alt="SHACL Play report"></a>
					    <div class="card-body">
					      <h5 class="card-title">Validate RDF data using SHACL</h5>
					      <p class="card-text">Get a human-readable report from a SHACL validation. Upload your RDF or validate online RDF file at some URL. Also download a CSV report, or raw SHACL Turtle report.</p>
					      <a href="<c:url value="/validate" />" class="btn btn-primary">Start validating</a>
					    </div>
					  </div>				  
				  	</div>
				  	<div class="col-4" style="margin-bottom: 2em;">  
					  <div class="card">
					    <a href="<c:url value="/validate" />"><img src="<c:url value="/resources/img/home-badges.png" />" class="card-img-top" alt="SHACL Play badges" /></a>
					    <div class="card-body">
					      <h5 class="card-title">Get validation badge for your file</h5>
					      <p class="card-text">If your Shape file is registered in the <a href="<c:url value="/shapes-catalog" />">Shapes catalog</a> and you validate an online file, get Markdown or URL code to display a validation badge on your Github project ! See <a href="https://github.com/sparna-git/SHACL-Catalog">an exemple here</a>.</p>
						  <a href="<c:url value="/validate" />" class="btn btn-primary">Start validating</a>
					    </div>
					  </div>				  
				  	</div>
				  	<div class="col-4" style="margin-bottom: 2em;">
					  <div class="card">
					    <a href="<c:url value="/draw" />"><img src="<c:url value="/resources/img/home-uml.png" />" class="card-img-top" alt="SHACL Play report"></a>
					    <div class="card-body">
					      <h5 class="card-title">Draw UML diagrams from Shapes</h5>
					      <p class="card-text">Looking for something more visual ? Generate UML diagrams in SVG from your SHACL file ! Works with <a href="https://plantuml.com/">PlantUML</a></p>
					      <a href="<c:url value="/draw" />" class="btn btn-primary">Start drawing</a>
					    </div>
					  </div>
					</div>
				  	<div class="col-4" style="margin-bottom: 2em;">
					  <div class="card">
					    <a href="<c:url value="/shapes-catalog" />"><img src="<c:url value="/resources/img/home-catalog.png" />" class="card-img-top" alt="Shapes catalog" /></a>
					    <div class="card-body">
					      <h5 class="card-title">Register SHACL rules in SHACL catalog</h5>
					      <p class="card-text">The SHACL catalog is a curated set of online reusable SHACL files. Add yours !</p>
					      <a href="<c:url value="/shapes-catalog" />" class="btn btn-primary">View Shapes Catalog</a>
					    </div>
					  </div>
				  	</div>
					<div class="col-4" style="margin-bottom: 2em;">
					  <div class="card">
					    <div class="card-body">
					      <h5 class="card-title">Convert OWL to SHACL</h5>
					      <p class="card-text">So you have an OWL file and you would like to derive SHACL from it ? use the OWL-to-SHACL conversion rules to generate your SHACL constraint file.</p>
					      <a href="<c:url value="/convert" />" class="btn btn-primary">Convert OWL to SHACL</a>
					    </div>
					  </div>
					</div>
					<div class="col-4" style="margin-bottom: 2em;">
					  <div class="card">
					    <div class="card-body">
					      <h5 class="card-title">Apply generic SHACL Rules</h5>
					      <p class="card-text">Want more than OWL-2-SHACL rules ? apply custom <a href="https://www.w3.org/TR/shacl-af/#rules">SHACL Rules</a> on your data to derive new data.</p>
						  <a href="<c:url value="/convert" />" class="btn btn-primary">Convert RDF using SHACL Rules</a>
					    </div>
					  </div>
					</div>
				</div>		
            	
            </div>
            
        </div>
        
    </div> 

	<jsp:include page="footer.jsp" />

	<!-- SCRIPTS -->
    <!-- JQuery -->
    <script type="text/javascript" src="<c:url value="/resources/MDB-Free/js/jquery.min.js" />"></script>
    <!-- Bootstrap tooltips -->
    <script type="text/javascript" src="<c:url value="/resources/MDB-Free/js/popper.min.js" />"></script>
    <!-- Bootstrap core JavaScript -->
    <script type="text/javascript" src="<c:url value="/resources/MDB-Free/js/bootstrap.min.js" />"></script>
    <!-- MDB core JavaScript -->
    <script type="text/javascript" src="<c:url value="/resources/MDB-Free/js/mdb.min.js" />"></script>
	
	<script type="text/javascript">
		$(document).ready(function() {			

		});
	</script>

</body>
</html>