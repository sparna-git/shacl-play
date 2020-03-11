<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!-- setup the locale for the messages based on the language in the session -->
<c:set var="lang" value="${sessionScope['fr.sparna.rdf.shacl.shaclplay.SessionData'].userLocale.language}" />
<fmt:setLocale value="${lang}"/>
<fmt:setBundle basename="fr.sparna.rdf.shacl.shaclplay.i18n.shaclplay"/>

<c:set var="data" value="${requestScope['ShapesDisplayData']}" />
<c:set var="shapes" value="${data.shapes}" />
<c:set var="renderer" value="${data.renderer}" />

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
		<jsp:param name="active" value="validate"/>
	</jsp:include>

	<div class="container-fluid">
		
	
		<div class="row justify-content-center" style="padding-top:1em;">
			<div class="col-10">
				<div class="page-header">
		       		<h1>Validation results <small>of ${data.numberOfShapes} shapes</small></h1>
		       		<p>Download validation report in <a href="<c:url value="/validate/report/download?lang=csv" />" class="badge badge-pill badge-light">CSV</a>&nbsp;<a href="<c:url value="/validate/report/download?lang=Turtle" />" class="badge badge-pill badge-light">Turtle</a>&nbsp;<a href="<c:url value="/validate/report/download?lang=RDF/XML" />" class="badge badge-pill badge-light">RDF/XML</a></p>
		       		<c:if test="${not empty data.permalink}">
		       			<p><a href="${data.permalink}"><i class="fal fa-link"></i>&nbsp;Permalink to this report</a></p>
		       		</c:if>
		     	</div>
				${data.getValidationReportFull(lang)}
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
            // add the toggle link behavior
            $( ".toggleTableLink" ).click(function(event) {
        		$(this).parent().prev(".table-outer").toggleClass("open");
        		return false;
            });
		});
	</script>

</body>
</html>