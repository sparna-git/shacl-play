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

            	<!-- SHACL -->
            	<h2 style="font-style: !important;"><fmt:message key="navbar.option1"/>${param.active == 'option2' ? '<span class="sr-only">(current)</span>' : ''}</h2>
            	<hr/>
            	<div class="row">					
					<!-- Documentation -->
					<div class="col-4" style="margin-bottom: 2em;">
					  <div class="card">
					    <a href="<c:url value="/doc" />"><img src="<c:url value="/resources/img/home-doc.png" />" class="card-img-top" alt="SHACL Play documentation generator"></a>
					    <div class="card-body">
					      <h5 class="card-title"><fmt:message key="home.doc.card-title" /></h5>
					      <p class="card-text"><fmt:message key="home.doc.card-title.help" /></p>
					      <a href="<c:url value="/doc" />" class="btn btn-primary"><fmt:message key="home.doc.button" /></a>
					    </div>
					  </div>
					</div>
				  	<!-- Draw -->
				  	<div class="col-4" style="margin-bottom: 2em;">
					  <div class="card">
					    <a href="<c:url value="/draw" />"><img src="<c:url value="/resources/img/home-uml.png" />" class="card-img-top" alt="SHACL Play UML diagram"></a>
					    <div class="card-body">
					      <h5 class="card-title"><fmt:message key="home.draw.card-title" /></h5>
					      <p class="card-text"><fmt:message key="home.draw.card-title.help" /></a></p>
					      <a href="<c:url value="/draw" />" class="btn btn-primary"><fmt:message key="home.draw.button" /></a>
					    </div>
					  </div>
					</div>
					<!-- Context -->
					<div class="col-4" style="margin-bottom: 2em;">
					  <div class="card">
					    <a href="<c:url value="/context" />"><img src="<c:url value="/resources/img/home-context_jsonld.png" />" class="card-img-top" alt="SHACL Play UML diagram"></a>
					    <div class="card-body">
					      <h5 class="card-title"><fmt:message key="home.context.card-title" /></h5>
					      <p class="card-text"><fmt:message key="home.context.card-title.help" /></a></p>
					      <a href="<c:url value="/context" />" class="btn btn-primary"><fmt:message key="home.context.button" /></a>
					    </div>
					  </div>
					</div>
					<!-- SPARQL -->
					<div class="col-4" style="margin-bottom: 2em;">
					  <div class="card">
					    <a href="<c:url value="/sparql" />"><img src="<c:url value="/resources/img/home-sparql.png" />" class="card-img-top" alt="SHACL Play Sparql Query"></a>
					    <div class="card-body">
					      <h5 class="card-title"><fmt:message key="home.sparql.card-title" /></h5>
					      <p class="card-text"><fmt:message key="home.sparql.card-title.help" /></p>
					      <a href="<c:url value="/sparql" />" class="btn btn-primary"><fmt:message key="home.sparql.button" /></a>
					    </div>
					  </div>
					</div>
					<!-- Catalog SHACL -->					
				  	<div class="col-4" style="margin-bottom: 2em;">
					  <div class="card">
					    <a href="<c:url value="/shapes-catalog" />"><img src="<c:url value="/resources/img/home-catalog.png" />" class="card-img-top" alt="Shapes catalog" /></a>
					    <div class="card-body">
					      <h5 class="card-title"><fmt:message key="home.catalog-rules.card-title" /></h5>
					      <p class="card-text"><fmt:message key="home.catalog-rules.card-title.help" /></p>
					      <a href="<c:url value="/shapes-catalog" />" class="btn btn-primary"><fmt:message key="home.catalog-rules.button" /></a>
					    </div>
					  </div>
				  	</div>
				</div>
				
				<!-- RDF Data -->
				<h2 style="font-style: !important;"><fmt:message key="navbar.option2"/>${param.active == 'option2' ? '<span class="sr-only">(current)</span>' : ''}</h2>
				<hr>
				<div class="row">					
					<!-- Validate -->
  					<div class="col-4" style="margin-bottom: 2em;">  
					  <div class="card">
					    <a href="<c:url value="/validate" />"><img src="<c:url value="/resources/img/home-report.png" />" class="card-img-top" alt="SHACL Play report"></a>
					    <div class="card-body">
					      <!--  
					      <h5 class="card-title">Validate RDF data using SHACL</h5>
					      <p class="card-text">Get a human-readable report from a SHACL validation. Upload your RDF or validate online RDF file at some URL. Also download a CSV report, or raw SHACL Turtle report.</p>
					      -->
					      <h5 class="card-title"><fmt:message key="home.validate.card-title" /></h5>
					      <p class="card-text"><fmt:message key="home.validate.card-title.help" /></p>
					      <a href="<c:url value="/validate" />" class="btn btn-primary"><fmt:message key="home.validate.button" /></a>
					    </div>
					  </div>				  
				  	</div>
				  	<!-- Generate -->
					<div class="col-4" style="margin-bottom: 2em;">  
					  <div class="card">
					    <a href="<c:url value="/generate" />"><img src="<c:url value="/resources/img/home-generate.png" />" class="card-img-top" alt="SHACL Play report"></a>
					    <div class="card-body">
					      <h5 class="card-title"><fmt:message key="home.generate.card-title" /></h5>
					      <p class="card-text"><fmt:message key="home.generate.card-title.help" /></p>
					      <a href="<c:url value="/generate" />" class="btn btn-primary"><fmt:message key="home.generate.button" /></a>
					    </div>
					  </div>				  
				  	</div>		  	
				 </div>
				 
				 
				 <!-- SHACL Rules -->
				 <h2 style="font-style: !important;"><fmt:message key="navbar.option3"/>${param.active == 'option2' ? '<span class="sr-only">(current)</span>' : ''}</h2>
				 <hr>
				 <div class="row">	
				 		
					<!-- Validate -->
				  	<div class="col-4" style="margin-bottom: 2em;">  
					  <div class="card">
					    <a href="<c:url value="/validate" />"><img src="<c:url value="/resources/img/home-badges.png" />" class="card-img-top" alt="SHACL Play badges" /></a>
					    <div class="card-body">
					      <h5 class="card-title"><fmt:message key="home.catalog.card-title" /></h5>
					      <p class="card-text"><fmt:message key="home.catalog.card-title.help" /></p>
						  <a href="<c:url value="/validate" />" class="btn btn-primary"><fmt:message key="home.catalog.button" /></a>
					    </div>
					  </div>				  
				  	</div>
				  
				  	<!-- Convert -->
					<div class="col-4" style="margin-bottom: 2em;">
					  <div class="card">
					    <div class="card-body">
					      <h5 class="card-title"><fmt:message key="home.from-owl.title" /></h5>
					      <p class="card-text"><fmt:message key="home.from-owl.title.help" /></p>
					      <a href="<c:url value="/convert" />" class="btn btn-primary"><fmt:message key="home.from-owl.button" /></a>					      
					    </div>
					  </div>
					</div>
					
					<!-- Convert Rules -->
					<div class="col-4" style="margin-bottom: 2em;">
					  <div class="card">
					    <div class="card-body">
					      <h5 class="card-title"><fmt:message key="home.convert.card-title" /></h5>
					      <p class="card-text"><fmt:message key="home.convert.card-title.help" /></p>
					      <div class="btn-group" role="group" aria-label="Basic example">
					      	<a href="<c:url value="/convert" />" class="btn btn-primary"><p font-size:14><fmt:message key="home.convert.button-rdf" /></p></a>
					      	<a href="<c:url value="/rules" />" class="btn btn-primary"><p font-size:14><fmt:message key="home.convert.button-shaclrules" /></p></a>
						  </div>
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