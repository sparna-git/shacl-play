<!DOCTYPE html>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<html>
	<head>
		<title>SHACL Play! | Wait...</title>
		<meta http-equiv="content-type" content="text/html; charset=UTF-8">
		<link rel="stylesheet" href="<c:url value="/resources/MDB-Free/css/bootstrap.min.css" />">
		<link rel="stylesheet" href="<c:url value="/resources/MDB-Free/css/mdb.min.css" />">
		<link rel="stylesheet" href="<c:url value="/resources/css/shacl-play.css" />">
	</head>
	<body>

    <div class="container-fluid">
    	<div class="col-md-8 col-md-offset-2">
			<h3>Validation in progress...</h3>
			<br />
			<div id="tail">
			    
			</div>
			<br />
			<i>Please wait, your browser window will refresh automatically once the validation is complete...</i>
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
	    	    	console.log(data.finished);
	    	    	if(data.finished == true) {
	    	    		window.location.href="show";
	    	    	}
	    	    	if(data.logs != "") {
		    	    	$("#tail").append(data.logs+"<br/>");
		    	        tailScroll();
	    	    	}
	    	        setTimeout(doPoll,1000);
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