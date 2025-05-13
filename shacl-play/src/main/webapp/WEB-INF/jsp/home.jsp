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
	<link rel="canonical" href="https://shacl-play.sparna.fr/play" />
	
    <!-- Font Awesome -->
    <link rel="stylesheet" href="<c:url value="/resources/fa/css/all.min.css" />">
    
	<link rel="stylesheet" href="<c:url value="/resources/MDB-Free/css/bootstrap.min.css" />">
	<link rel="stylesheet" href="<c:url value="/resources/MDB-Free/css/mdb.min.css" />">
	<link rel="stylesheet" href="<c:url value="/resources/css/shacl-play.css" />">
</head>
<body>
	<jsp:include page="include/navbar.jsp">
		<jsp:param name="active" value="home"/>
	</jsp:include>

   	<div class="main-wrapper">
		<!-- Header -->
		<section>
			<div class="container text-center" style="padding: 0.1em;" >
				<h1 class="display-3" style"">&nbsp;SHACL Play!</h1>
				<p><fmt:message key="home.shortdesc" /></p>
            	<hr style="padding-bottom: 3%;"/>
			</div>
		</section>
		<!-- Body -->
		<!-- Card -->
		<section>		
			<div class="container">
				<div class="row">
					<!-- Card 1 -->
					<div class="col-4 d-flex">
						<div class="card">
							<div class="d-flex justify-content-center" style="padding-top: 0.9em;">
								<i class="fal fa-shapes fa-6x"></i>								
							</div>
							<div class="card-body bg-light.bg-gradient">								
								<!-- Title -->
								<div style="font-size: 1.5rem; font-style: oblique; padding: 0.3em;">
									<fmt:message key="home.card1.title"/>
								</div>
								<!-- Description of menu -->
							    <span class="small"><fmt:message key="home.card1.description"/></span>
							    <!-- Menu -->
								<ul style="list-style-type:none;justify-content: flex-start; padding: 0.3rem;">
									<li style="display: list-item; text-align: -webkit-match-parent; padding: 0.3rem;">
										<a href="<c:url value="/shaclexcel" />" title="<fmt:message key="navbar.menu1.shaclexcel.tooltip" />"><fmt:message key="navbar.menu1.shaclexcel.label" /></a>
									</li>
									<li style="display: list-item; text-align: -webkit-match-parent; padding: 0.3rem;">
										<a href="<c:url value="/doc" />" title="<fmt:message key="navbar.menu1.doc.tooltip" />"><fmt:message key="navbar.menu1.doc.label" /></a> | <small>&nbsp;<a href="<c:url value="/doc#documentation" />" title="documentation"><i class="fal fa-book"></i></a></small>
									</li>
									<li style="display: list-item; text-align: -webkit-match-parent; padding: 0.3rem;">
										<a href="<c:url value="/draw" />" title="<fmt:message key="navbar.menu1.draw.tooltip" />"><fmt:message key="navbar.menu1.draw.label" /></a> | <small>&nbsp;<a href="<c:url value="/draw#documentation" />" title="documentation"><i class="fal fa-book"></i></a></small>
									</li>
									<li style="display: list-item; text-align: -webkit-match-parent; padding: 0.3rem;">
										<a href="<c:url value="/context" />" title="<fmt:message key="navbar.menu1.context.tooltip" />"><fmt:message key="navbar.menu1.context.label" /></a> | <small>&nbsp;<a href="<c:url value="/context#documentation" />" title="documentation"><i class="fal fa-book"></i></a></small>
									</li>
									<li style="display: list-item; text-align: -webkit-match-parent; padding: 0.3rem;">
										<a href="<c:url value="/jsonschema" />" title="<fmt:message key="navbar.menu1.jsonschema.tooltip" />"><fmt:message key="navbar.menu1.jsonschema.label" /></a> | <small>&nbsp;<a href="<c:url value="/jsonschema#documentation" />" title="documentation"><i class="fal fa-book"></i></a></small>
									</li>
									<li style="display: list-item; text-align: -webkit-match-parent; padding: 0.3rem;">
										<a href="<c:url value="/sparql" />" title="<fmt:message key="navbar.menu1.sparql.tooltip" />"><fmt:message key="navbar.menu1.sparql.label" /></a> | <small>&nbsp;<a href="<c:url value="/sparql#documentation" />" title="documentation"><i class="fal fa-book"></i></a></small>
									</li>
									<li style="display: list-item; text-align: -webkit-match-parent; padding: 0.3rem;">
										<a href="<c:url value="/excel" />" title="<fmt:message key="navbar.menu1.excel.tooltip" />"><fmt:message key="navbar.menu1.excel.label" /></a> | <small>&nbsp;<a href="<c:url value="/excel#documentation" />" title="documentation"><i class="fal fa-book"></i></a></small>
									</li>
									<li style="display: list-item; text-align: -webkit-match-parent; padding: 0.3rem;">
										<a href="<c:url value="/shapes-catalog" />" title="<fmt:message key="navbar.menu1.shapes-catalog.tooltip" />"><fmt:message key="navbar.menu1.shapes-catalog.label" /></a>
									</li>
								</ul>
							</div>
						</div>
					</div>

					<!-- Card 2 -->
					<div class="col-4 d-flex">
						<div class="card">
							<div class="d-flex justify-content-center" style="padding-top: 0.9em;">
								<i class="fal fa-chart-network fa-6x"></i>
							</div>
							<div class="card-body bg-light.bg-gradient">
								<!-- Title -->
								<div style="font-size: 1.5rem; font-style: oblique; padding: 0.3em;">
									<fmt:message key="home.card2.title"/>
								</div>
							    <!-- Description of menu -->
								<span class="small"><fmt:message key="home.card2.description"/></span>
							    <!-- Menu -->
							    <ul style="list-style-type:none;padding: 0.3rem;">
									<li style="display: list-item; text-align: -webkit-match-parent; padding: 0.3rem;">
										<a href="<c:url value="/validate" />" title="<fmt:message key="navbar.menu2.validate.tooltip" />"><fmt:message key="navbar.menu2.validate.label" /></a> | <small>&nbsp;<a href="<c:url value="/validate#documentation" />" title="documentation"><i class="fal fa-book"></i></a></small>										
									</li>
									<li style="display: list-item; text-align: -webkit-match-parent; padding: 0.3rem;">
										<a href="<c:url value="/generate" />" title="<fmt:message key="navbar.menu2.generate.tooltip" />"><fmt:message key="navbar.menu2.generate.label" /></a> | <small>&nbsp;<a href="<c:url value="/generate#documentation" />" title="documentation"><i class="fal fa-book"></i></a></small>										
									</li>
									<li style="display: list-item; text-align: -webkit-match-parent; padding: 0.3rem;">
										<a href="<c:url value="/analyze" />" title="<fmt:message key="navbar.menu2.analyze.tooltip" />"><fmt:message key="navbar.menu2.analyze.label" /></a>
									</li>
									<li style="display: list-item; text-align: -webkit-match-parent; padding: 0.3rem;">
										<a href="<c:url value="/validate#badges" />" title="<fmt:message key="navbar.menu2.badge.tooltip" />"><fmt:message key="navbar.menu2.badge.label" /></a>										
									</li>			
								</ul>
							</div>
						</div>
					</div>
					<!-- Card 3 -->
					<div class="col-4 d-flex">
						<div class="card">
							<div class="d-flex justify-content-center" style="padding-top: 0.9em;">
								<!-- Version 5.15.4  -->
								<i class="fal fa-sort-shapes-down-alt fa-6x"></i>																
							</div>
							<div class="card-body bg-light.bg-gradient">								
								<!-- Title -->
								<div style="font-size: 1.5rem; font-style: oblique; padding: 0.3em;">
									<fmt:message key="home.card3.title"/>
								</div>
							    <!-- Description of menu -->
								<span class="small"><fmt:message key="home.card3.description"/></span>
								<!-- Menu -->
								<ul style="list-style-type:none;justify-content: flex-start; padding: 0.3rem;">
									<li style="display: list-item; text-align: -webkit-match-parent; padding: 0.3rem;">
										<a href="<c:url value="/convert" />" title="<fmt:message key="navbar.menu3.from-owl.tooltip" />"><fmt:message key="navbar.menu3.from-owl.label" /></a>										
									</li>
									<li style="display: list-item; text-align: -webkit-match-parent; padding: 0.3rem;">
										<a href="<c:url value="/rules" />" title="<fmt:message key="navbar.menu3.rules.tooltip" />"><fmt:message key="navbar.menu3.rules.label" /></a>
									</li>
									<li style="display: list-item; text-align: -webkit-match-parent; padding: 0.3rem;">
										<a href="<c:url value="/rules-catalog" />" title="<fmt:message key="navbar.menu3.rules-catalog.tooltip" />"><fmt:message key="navbar.menu3.rules-catalog.label" /></a>
									</li>						
								</ul>								
							</div>
						</div>
					</div>
				</div>
			</div>
		</section>
		<section>
			<div class="container">
				<div class="row">
					
				</div>
			</div>
		</section>
		<!-- Footer -->
		<section id="footer">
			<div class="container">
				<jsp:include page="include/footer.jsp" />
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
    
	
</body>
</html>