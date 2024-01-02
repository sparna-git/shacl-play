<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!-- setup the locale for the messages based on the language in the session -->
<fmt:setLocale value="${sessionScope['fr.sparna.rdf.shacl.shaclplay.SessionData'].userLocale.language}"/>
<fmt:setBundle basename="fr.sparna.rdf.shacl.shaclplay.i18n.shaclplay"/>

<html>
	<head>
		<title><fmt:message key="window.app" /> | Wait...</title>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		
		<!-- Font Awesome -->
	    <link rel="stylesheet" href="<c:url value="/resources/fa/css/all.min.css" />">
		
		<link rel="stylesheet" href="<c:url value="/resources/MDB-Free/css/bootstrap.min.css" />">
		<link rel="stylesheet" href="<c:url value="/resources/MDB-Free/css/mdb.min.css" />">
		<link rel="stylesheet" href="<c:url value="/resources/css/shacl-play.css" />">
	</head>
	<body>

	<nav class="navbar navbar-expand-lg navbar-light" id="globalnav" >	
	    <!-- Navbar brand -->
	    <span class="navbar-brand"><i class="fal fa-home"></i>&nbsp;&nbsp;<fmt:message key="navbar.brand" /></span>
	</nav>
	

    <div class="container">
    	<div class="col col-md-offset-2">
			<h3><i id="spinner" class="fal fa-shapes fa-spin"></i>&nbsp;In progress...</h3>
			<br />
			<div id="tail">
			    
			</div>
			<br />
			<i>Please wait, your browser window will refresh automatically once the process is complete...</i>
		</div>

    </div><!-- /.container-fluid -->

	
	<!-- SCRIPTS -->
    <!-- JQuery -->
    <script type="text/javascript" src="<c:url value="/resources/MDB-Free/js/jquery.min.js" />"></script>
    <!-- Bootstrap tooltips -->
    <script type="text/javascript" src="<c:url value="/resources/MDB-Free/js/popper.min.js" />"></script>
    <!-- Bootstrap core JavaScript -->
    <script type="text/javascript" src="<c:url value="/resources/MDB-Free/js/bootstrap.min.js" />"></script>
    <!-- MDB core JavaScript -->
    <script type="text/javascript" src="<c:url value="/resources/MDB-Free/js/mdb.min.js" />"></script>
    
    <script type="application/javascript">
	    function doPoll(){
	    	
	    	$.ajax({ 
	    	    type: 'GET', 
	    	    url: 'progress', 
	    	    // data: { get_param: 'value' }, 
	    	    dataType: 'json',
	    	    success: function (data) {
	    	    	if(data.logs != "") {
		    	    	$("#tail").append(data.logs+"<br/>");
		    	        tailScroll();
	    	    	}
	    	    	
	    	    	console.log(data.finished);
	    	    	
	    	    	if(data.finished == true) {
	    	    		// unspin icon
	    	    		$("#spinner").removeClass("fa-spin");
	    	    		window.location.href="show";
	    	    	} else {
	    	    		setTimeout(doPoll,2500);	
	    	    	}
	    	    }
	    	});
	    	
	    }
	    
		$(document).ready(function() {
			doPoll();
        });
		
		// tail effect
		function tailScroll() {
		    var height = $("#tail").get(0).scrollHeight;
		    $("#tail").animate({
		        scrollTop: height
		    }, 500);
		}
		
	</script>
  </body>
</html>