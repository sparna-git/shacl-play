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

   	<div class="main-wrapper">
		<!-- Header -->
		<section>
			<div class="container text-center" style="padding: 0.1em;" >
				<h1 class="display-3" style"">&nbsp;SHACL Play!</h1>
            	<p>Free online RDF data validation with <a href="https://www.w3.org/TR/shacl/">SHACL</a>. SHACL Play! embeds <a href="https://github.com/TopQuadrant/shacl" target="_blank">TopBraid SHACL API</a> from <a href="https://www.topquadrant.com/" target="_blank">TopQuadrant</a>.<p>
            	<hr style="padding-bottom: 3%;"/>
			</div>
		</section>
		<!-- Body -->
		<!-- Card -->
		<section>		
			<div class="container" style="padding-bottom: 6%">
				<div class="d-flex flex-row justify-content-around">
					<!-- SHACL -->
					<div class="d-flex justify-content-start">
						<div class="card" style="width: 18rem;">
							<div class="d-flex justify-content-center" style="padding-top: 0.9em;">
								<i class="fal fa-shapes fa-6x"></i>								
							</div>
							<div class="card-body bg-light.bg-gradient">								
								<!-- Title -->
								<div style="font-size: 1.5rem; font-style: oblique; padding: 0.3em;">
									<fmt:message key="home.option1.card"/>${param.active == 'option1' ? '<span class="sr-only">(current)</span>' : ''}
								</div>
								<!-- Description of menu -->
							    <spam class="small"><fmt:message key="navbar.option1.description"/>${param.active == 'option1' ? '<span class="sr-only">(current)</span>' : ''}</spam>
							    <!-- Menu -->
								<ul style="list-style-type:none;justify-content: flex-start; padding: 0.3rem;">
									<li style="display: list-item; text-align: -webkit-match-parent; padding: 0.3rem;">
										<a href="<c:url value="/doc" />"><fmt:message key="home.doc.card-title.card" /></a>
									</li>
									<li style="display: list-item; text-align: -webkit-match-parent; padding: 0.3rem;">
										<a href="<c:url value="/draw" />"><fmt:message key="home.draw.card-title.card" /></a>
									</li>
									<li style="display: list-item; text-align: -webkit-match-parent; padding: 0.3rem;">
										<a href="<c:url value="/sparql" />"><fmt:message key="home.sparql.card-title.card" /></a>
									</li>
									<li style="display: list-item; text-align: -webkit-match-parent; padding: 0.3rem;">
										<a href="<c:url value="/context" />"><fmt:message key="home.context.card-title.card" /></a>
									</li>
									<li style="display: list-item; text-align: -webkit-match-parent; padding: 0.3rem;">
										<a href="<c:url value="/shapes-catalog" />"><fmt:message key="home.catalog-rules.card-title.card" /></a>
									</li>
								</ul>
							</div>
						</div>
					</div>
					<!-- RDF -->
					<div class="d-flex justify-content-start">
						<div class="card" style="width: 18rem;">
							<div class="d-flex justify-content-center" style="padding-top: 0.9em;">
								<i class="fal fa-chart-network fa-6x"></i>
							</div>
							<div class="card-body bg-light.bg-gradient">
								<!-- Title -->
								<div style="font-size: 1.5rem; font-style: oblique; padding: 0.3em;">
									<fmt:message key="home.option2.card"/>${param.active == 'option2' ? '<span class="sr-only">(current)</span>' : ''}
								</div>
							    <!-- Description of menu -->
								<span class="small"><fmt:message key="navbar.option2.description"/>${param.active == 'option2' ? '<span class="sr-only">(current)</span>' : ''}</span>
							    <!-- Menu -->
							    <ul style="list-style-type:none;padding: 0.3rem;">
									<li style="display: list-item; text-align: -webkit-match-parent; padding: 0.3rem;">
										<a href="<c:url value="/validate" />"><fmt:message key="home.validate.card-title.card" /></a>										
									</li>
									<li style="display: list-item; text-align: -webkit-match-parent; padding: 0.3rem;">
										<a href="<c:url value="/generate" />"><fmt:message key="home.generate.card-title.card" /></a>										
									</li>
									<li style="display: list-item; text-align: -webkit-match-parent; padding: 0.3rem;">
										<a href="<c:url value="/validate" />"><fmt:message key="home.catalog.card-title.card" /></a>										
									</li>			
								</ul>
							</div>
						</div>
					</div>
					<!-- RULES -->
					<div class="d-flex justify-content-evenly">
						<div class="card" style="width: 18rem;">
							<div class="d-flex justify-content-center" style="padding-top: 0.9em;">
								<i class="fal fa-chart-network fa-6x"></i>																			
							</div>
							<div class="card-body bg-light.bg-gradient">								
								<!-- Title -->
								<div style="font-size: 1.5rem; font-style: oblique; padding: 0.3em;">
									<fmt:message key="home.option3.card"/>${param.active == 'option3' ? '<span class="sr-only">(current)</span>' : ''}
								</div>
							    <!-- Description of menu -->
								<span><fmt:message key="navbar.option3.description"/>${param.active == 'option3' ? '<span class="sr-only">(current)</span>' : ''}</span>
								<!-- Menu -->
								<ul style="list-style-type:none;justify-content: flex-start; padding: 0.3rem;">
									<li style="display: list-item; text-align: -webkit-match-parent; padding: 0.3rem;">
										<a href="<c:url value="/validate" />"><fmt:message key="home.from-owl.title.card" /></a>										
									</li>
									<li style="display: list-item; text-align: -webkit-match-parent; padding: 0.3rem;">
										<a href="<c:url value="/convert" />"><fmt:message key="home.convert.title-rdf.card" /></a>
									</li>
									<li style="display: list-item; text-align: -webkit-match-parent; padding: 0.3rem;">
										<a href="<c:url value="/rules" />"><fmt:message key="home.convert.title-shaclrules.card" /></a>
									</li>						
								</ul>								
							</div>
						</div>
					</div>
				</div>
			</div>
		</section>
		<!-- Footer -->
		<section id="footer">
			<div class="container">
				<jsp:include page="footer.jsp" />
			</div>
		</section>
	</div>

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