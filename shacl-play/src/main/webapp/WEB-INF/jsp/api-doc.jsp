<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!-- setup the locale for the messages based on the language in the session -->
<fmt:setLocale value="${sessionScope['fr.sparna.rdf.shacl.shaclplay.SessionData'].userLocale.language}"/>
<fmt:setBundle basename="fr.sparna.rdf.shacl.shaclplay.i18n.shaclplay"/>


<html>
	<head>
		<title><fmt:message key="window.app" /></title>
		<link rel="canonical" href="https://shacl-play.sparna.fr/play/doc" />

		<meta http-equiv="content-type" content="text/html; charset=UTF-8">

	    <!-- Font Awesome -->
	    <link rel="stylesheet" href="<c:url value="/resources/fa/css/all.min.css" />">

		<link rel="stylesheet" href="<c:url value="/resources/MDB-Free/css/bootstrap.min.css" />">
		<link rel="stylesheet" href="<c:url value="/resources/MDB-Free/css/mdb.min.css" />">
		<link rel="stylesheet" href="<c:url value="/resources/jasny-bootstrap/jasny-bootstrap.min.css" />" />
		<link rel="stylesheet" href="<c:url value="/resources/css/shacl-play.css" />" />
		 <!-- Swagger CSS / SCRIPTS -->
          <script src="${pageContext.request.contextPath}/swagger-ui/swagger-ui-bundle.js" charset="UTF-8"> </script>
          <script src="${pageContext.request.contextPath}/swagger-ui/swagger-ui-standalone-preset.js" charset="UTF-8"> </script>
          <script src="${pageContext.request.contextPath}/swagger-ui//swagger-initializer.js" charset="UTF-8"> </script>
          <link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/swagger-ui/swagger-ui.css" />

	</head>

	<jsp:include page="include/navbar.jsp">
    		<jsp:param name="active" value="doc"/>
    	</jsp:include>


    <div class="content">
        <div id="swagger-ui" class="container-fluid"></div>
    </div>


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


      </body>
</html>