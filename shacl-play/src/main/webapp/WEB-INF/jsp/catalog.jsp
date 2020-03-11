<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!-- setup the locale for the messages based on the language in the session -->
<fmt:setLocale value="${sessionScope['fr.sparna.rdf.shacl.shaclplay.SessionData'].userLocale.language}"/>
<fmt:setBundle basename="fr.sparna.rdf.shacl.shaclplay.i18n.shaclplay"/>

<c:set var="data" value="${requestScope['CatalogData']}" />

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
	
	<style type="text/css">
		.catalog-description {
			text-align: justify;
		}
		.catalog-user {
			font-style: italic;
			text-align: right;
			font-size: smaller;
		}
		.shapes-card-container {
			margin-top: 2em;
		}
	</style>
</head>
<body>
	<jsp:include page="navbar.jsp">
		<jsp:param name="active" value="catalog"/>
	</jsp:include>

    <div class="container-fluid">
    
    	<div class="row justify-content-md-center">
            <div class="col-8">
            	<h1 class="display-3"><fmt:message key="catalog.title" /></h1>
            		<div class="row">
            			<div class="col">To see your shapes file listed here, add it to the <a href="https://github.com/sparna-git/SHACL-Catalog/blob/master/shacl-catalog.ttl">Shapes Catalog source file</a> on Github.</div>
            		</div>
            		<div class="row">
				        <c:forEach items="${data.catalog.entries}" var="entry">
				        	<div class="col-4 shapes-card-container">
			            		<div class="card" id="eli-shapes">
						          <div class="card-body">
						          	<h4 class="card-title catalog-title" title="${entry.id}">${entry.title}</h4>
						          	<p class="card-text catalog-description">${entry.description}</p>
						          		<ul style="list-style: none;" class="card-text">
						          			<c:if test="${not empty entry.keywords}"><li title="dcat:keywords"><i class="fal fa-tag"></i>&nbsp;&nbsp; ${fn:join(entry.keywordsArray, ', ')}</li></c:if>
						          			<c:if test="${not empty entry.publisher.label}"><li title="dct:publisher"><i class="fal fa-building"></i>&nbsp;&nbsp;${entry.publisher.label}</li></c:if>
						          			<c:if test="${not empty entry.creator.label}"><li title="dct:creator"><i class="fal fa-user-edit"></i>&nbsp;&nbsp;${entry.creator.label}</li></c:if>			          			
						          			<c:if test="${not empty entry.issued}"><li title="dct:issued"><i class="fal fa-calendar"></i>&nbsp;&nbsp; <fmt:formatDate type="date" value = "${entry.issued}" /></li></c:if>
						          		</ul>
	
						          	
						          	<div class="btn-group btn-group-lg float-right" role="group">
									  <c:if test="${not empty entry.landingPage}"><a type="button" class="btn btn-outline-dark" href="${entry.landingPage}"><i class="fal fa-home"></i></a></c:if>
									  <a type="button" class="btn btn-outline-dark" href="<c:url value="${entry.turtleDownloadUrl}" />"><i class="fal fa-download"></i></a>
									  <a type="button" class="btn btn-outline-dark" href="<c:url value="/validate?shapes=${entry.id}" />"><i class="fal fa-play"></i></a>
									</div>
						          </div>
						          <div class="card-footer text-muted">
									 <p class="card-text catalog-user">&nbsp;<c:if test="${not empty entry.submitted}"><fmt:message key="catalog.entry.addedOn" /> <fmt:formatDate type="date" value = "${entry.submitted}" /> <c:if test="${not empty entry.submitter.label}"><fmt:message key="catalog.entry.addedOn.by" /> ${entry.submitter.label}</c:if></c:if></p>	
								  </div>
						        </div>
					        </div>
				      	</c:forEach>
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