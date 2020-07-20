<!DOCTYPE html>
<html>
<head>
	<meta charset="utf-8">
	<meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no">
	<title> </title>
	
	<!-- Bootstrap -->
	<link rel="stylesheet" href="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/css/bootstrap.min.css" integrity="sha384-Gn5384xqQ1aoWXA+058RXPxPg6fy4IWvTNh0E263XmFcJlSAwiGgFAW/dAiS6JXm" crossorigin="anonymous">
	<!-- Datatable style with bootstrap -->
	<!-- <link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/1.10.20/css/dataTables.bootstrap4.min.css"> -->
	<!-- Default Datatables CSS -->
	<link rel="stylesheet" type="text/css" href="https://cdn.datatables.net/1.10.20/css/jquery.dataTables.min.css" />
	  
	  <!-- JQuery -->
	  <script
				  src="http://code.jquery.com/jquery-3.4.1.slim.min.js"
				  integrity="sha256-pasqAKBDmFT4eHoN2ndd6lN370kFiGUFyTiUHWhU7k8="
				  crossorigin="anonymous"></script>
	  
	<!-- Datatables -->
	<script type="text/javascript" charset="utf8" src="https://cdn.datatables.net/1.10.20/js/jquery.dataTables.min.js"></script>
	<!-- Datatable with bootstrap -->
	<!-- <script type="text/javascript" charset="utf8" src="https://cdn.datatables.net/1.10.20/js/dataTables.bootstrap4.min.js"></script> -->
	
	<!-- Buttons integration -->
	<script type="text/javascript" charset="utf8" src="https://cdn.datatables.net/buttons/1.6.1/js/dataTables.buttons.min.js"></script>
	<script type="text/javascript" charset="utf8" src="https://cdn.datatables.net/buttons/1.6.1/js/buttons.flash.min.js"></script>
	
	<script type="text/javascript" charset="utf8" src="https://cdnjs.cloudflare.com/ajax/libs/jszip/3.1.3/jszip.min.js"></script>
	<script type="text/javascript" charset="utf8" src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.53/pdfmake.min.js"></script>
	<script type="text/javascript" charset="utf8" src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.1.53/vfs_fonts.js"></script>
	<script type="text/javascript" charset="utf8" src="https://cdn.datatables.net/buttons/1.6.1/js/buttons.html5.min.js"></script>
	
</head>

<body>
	
	<div class="container-fluid">
		<div class="row" style="padding-top:1em;">
			<div class="col">
				<#include contentTemplate>
			</div>
		</div>
	</div>
</body>
<script>
	$(document).ready(function() {
		var table = $('#resultTable').DataTable( {
				dom: 'Bfrtip',
		        buttons: [
		            'copy', 'csv'
		        ]
		} );
	});
</script>
</html>