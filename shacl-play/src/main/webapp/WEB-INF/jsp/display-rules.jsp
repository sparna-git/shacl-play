<!DOCTYPE html>
<%@taglib uri="http://www.springframework.org/tags/form" prefix="form"%>
<%@taglib uri="http://www.springframework.org/tags" prefix="spring"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>

<!-- setup the locale for the messages based on the language in the session -->
<fmt:setLocale
	value="${sessionScope['fr.sparna.rdf.shacl.shaclplay.SessionData'].userLocale.language}" />
<fmt:setBundle basename="fr.sparna.rdf.shacl.shaclplay.i18n.shaclplay" />

<c:set var="data" value="${requestScope['RulesTodo']}" />

<html>
<head>
	<title>Display rules !</title>
	<title><fmt:message key="window.app" /></title>
	
	<meta http-equiv="content-type" content="text/html; charset=UTF-8">

	<link rel="stylesheet"
            href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.0/dist/css/bootstrap.min.css"
            integrity="sha384-B0vP5xmATw1+K9KRQjQERJvTumQW0nPEzvF6L/Z6nronJ3oUOFUFpCjEUQouq2+l"
            crossorigin="anonymous"/>
	
	<!-- Font Awesome -->
	<link rel="stylesheet" href="<c:url value="/resources/fa/css/all.min.css" />">
	<link rel="stylesheet" href="<c:url value="/resources/MDB-Free/css/bootstrap.min.css" />">
	<link rel="stylesheet" href="<c:url value="/resources/MDB-Free/css/mdb.min.css" />">
	<link rel="stylesheet" href="<c:url value="/resources/jasny-bootstrap/jasny-bootstrap.min.css" />" />
	<link rel="stylesheet" href="<c:url value="/resources/css/shacl-play.css" />" />
	
	<link rel="stylesheet" href="<c:url value="/resources/rules/lib/codemirror.css" />">
	<script type="text/javascript" src="<c:url value="/resources/rules/lib/codemirror.js" />"></script>
	<script type="text/javascript" src="<c:url value="/resources/rules/mode/sparql/sparql.js" />"></script>
		
	<style>
		.CodeMirror {height: auto;}
		.banner { align-content: center; }
		.list-group{
				    max-height: 300px;
				    margin-bottom: 10px;
				    overflow:scroll;
				    -webkit-overflow-scrolling: touch;
				}	
		.anchor {
					float: left;
					padding-right: 4px;
					margin-left: -20px;
					line-height: 1;
					padding-top:12px;
					}

		.monospace {
					font-family: SFMono-Regular,Menlo,Monaco,Consolas,"Liberation
					Mono","Courier New",monospace;
					font-size: 87.5%;
					}
	</style>
</head>
<body>
	<br>
	<div class="container-md" id="title">
		<h1><center>${data.getLabel()}</center></h1>
		<br>
		${data.getComments()}
	</div>
	
	<div class="container-md" id="Index">
		<br>
		<h2>Table of Content</h2>
		<u>
			<li><a href="#Namespaces">Namespaces</a></li>
			<c:forEach items="${data.getShapeRules()}" var="Shape">
			<li><a href="#${Shape.getLabel()}">${Shape.getLabel()}</a>
				<ul>
					<li><a href="#Target">Target</a></li>
					<details>						 
						<summary><a href="#Rules">Rules</a></summary>
						<ul>
							<c:forEach items="${Shape.getRules()}" var="rules">
							<li><a href="#${rules.getShSparqlRuleName()}">${rules.getShSparqlRuleName()}</a></li>
							</c:forEach>	
						</ul>
					</details>
															
				</ul>
			</li>		
			</c:forEach>	
		</u>
	</div>
	
	<article id="Namespaces">
		<section>
			<div class="container-md">
				<h1>Namespaces</h1><p><a href="#Index"><u>Retourn</u></a></p> 
				<table class="table table-striped table-sm">
					<tr>
						<th>Prefix</th>
						<th>NameSpace</th>
					</tr>
					<c:forEach items="${data.getNameSpaceRules()}" var="namespace">
					<tr>
					    <td>${namespace.getPrefix()}</td>
					    <td><a href="namespace.getNameSpace()">${namespace.getNameSpace()}</a></td>
					</tr>
					</c:forEach>
				</table>
			</div>
		</section>
	</article>
	
	<article id="NodeShape">
		<div class="container-md">
		<c:forEach items="${data.getShapeRules()}" var="Shape">
		<section id="${Shape.getLabel()}">
			<h1>${Shape.getLabel()}</h1>
			<article id="Target">
				<h2>Target</h2>
				<form>
					<c:forEach items="${Shape.getTarget()}" var="target">
					<textarea class="js_editor">${target.getShSelect()}
					</textarea>
					</c:forEach>
				</form>
			</article>
			<br>
			<article id="Rules">
				<h2>Rules</h2>
				<c:forEach items="${Shape.getRules()}" var="rules">
				<section id="${rules.getShSparqlRuleName()}">
					<br>
					<h3><a href="${rules.getShSparqlRuleName()}">${rules.getShSparqlRuleName()}</a></h3>
					
					<table class="table table table-striped">
						<thead>	
							<tbody>						
								<tr>
									<td>rdfs:label</td>
									<td><i>${rules.getRdfsLabel()}</i></td>
								</tr>
								<tr>
									<td>rdfs:comment</td>
									<td><i>${rules.getRdfsComments()}</i></td>
								</tr>
								<tr>
									<td>sh:order</td>
									<td><i>${rules.getShOrder()}</i></td>
								</tr>
							</tbody>
						</table>
						<div div style="max-width: 50em; margin-bottom: 1em">
							<form>
								<textarea class="js_editor">${rules.getShConstruct()}
								</textarea>
							</form>
						</div>		
				</section>		
				</c:forEach>
			</article>
		</section>
		</c:forEach>
		</div>
	</article>
	
	

<script src="https://code.jquery.com/jquery-3.3.1.js"></script>
<script src="https://maxcdn.bootstrapcdn.com/bootstrap/4.0.0/js/bootstrap.min.js"></script>

<script type="text/javascript">
	//Source: https://stackoverflow.com/questions/4480137/can-codemirror-be-used-on-multiple-textareas
	var js_editor = document.getElementsByClassName("js_editor");
	Array.prototype.forEach.call(js_editor, function(el) {
    var editor = CodeMirror.fromTextArea(el, {
        mode: "application/sparql-query",
        readOnly: true,
        lineNumbers: true
      });
    // Update textarea
    function updateTextArea() {
        editor.save();
    }
    editor.on('change', updateTextArea);
});
</script>	
	

</body>
</html>