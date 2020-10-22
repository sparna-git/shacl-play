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
		       		<p>Download validation report in <a href="<c:url value="/validate/report/download?format=csv" />" class="badge badge-pill badge-light">CSV</a>&nbsp;<a href="<c:url value="/validate/report/download?format=Turtle" />" class="badge badge-pill badge-light">Turtle</a>&nbsp;<a href="<c:url value="/validate/report/download?format=RDF/XML" />" class="badge badge-pill badge-light">RDF/XML</a></p>
		       		<c:if test="${not empty data.permalink}">
		       			<p><a href="${data.permalink}"><i class="fal fa-link"></i>&nbsp;Permalink to this report</a></p>
		       		</c:if>
		       		<c:if test="${not empty data.permalink}">
		       			<c:url value="https://img.shields.io/endpoint" var="url">
						  <c:param name="url" value="${data.permalink}&format=shields.io" />
						</c:url>
		       			<p><a href="#" data-toggle="modal" data-target="#mdModal"><i class="fal fa-badge-check"></i>&nbsp;Get your SHACL Play badge code</a> in Markdown or HTML. Or get the <a href="${url}">direct link to the badge</a> (this will trigger a new validation).</p>
		       			
		       			<div class="modal fade" id="mdModal" tabindex="-1" role="dialog" aria-labelledby="mdModalLabel" aria-hidden="true">
						  <div class="modal-dialog" role="document">
						    <div class="modal-content">
						      <div class="modal-header">
						        <h5 class="modal-title" id="exampleModalLabel">SHACL Play Badge</h5>
						        <button type="button" class="close" data-dismiss="modal" aria-label="Close">
						          <span aria-hidden="true">&times;</span>
						        </button>
						      </div>
						      <div class="modal-body">
						      	<h6>Markdown code</h6>
						      	<pre style="white-space: pre-wrap;">[![SHACL Play Badge](${url})](${data.permalink})</pre>
						      	<h6>HTML code</h6>
						      	<pre style="white-space: pre-wrap;">&lt;a href="${data.permalink}"&gt;&lt;img src="${url}" /&gt;&lt;/a&gt;</pre>
						      	<h6>JSON endpoint</h6>
						      	Link to <a href="${data.permalink}&format=shields.io">validation results for shields.io in JSON</a>
						      </div>
						      <div class="modal-footer">
						        <button type="button" class="btn btn-secondary" data-dismiss="modal">Close</button>
						      </div>
						    </div>
						  </div>
						</div>
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