<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>


<!-- setup the locale for the messages based on the language in the session -->
<fmt:setLocale value="${sessionScope['fr.sparna.rdf.shacl.shaclplay.SessionData'].userLocale.language}"/>
<c:set var="data" value="${sessionScope['fr.sparna.rdf.shacl.shaclplay.SessionData']}" />
<fmt:setBundle basename="fr.sparna.rdf.shacl.shaclplay.i18n.shaclplay"/>

<!--Navbar-->
<nav class="navbar navbar-expand-lg navbar-light" id="globalnav" >
	
	
    <!-- Navbar brand -->
    <a class="navbar-brand" href="<c:url value="/" />"><i class="fal fa-home"></i>&nbsp;<fmt:message key="navbar.brand" /></a>

    <!-- Collapse button -->
    <button class="navbar-toggler" type="button" data-toggle="collapse" data-target="#navbarSupportedContent" aria-controls="navbarSupportedContent"
        aria-expanded="false" aria-label="Toggle navigation"><span class="navbar-toggler-icon"></span></button>

    <!-- Collapsible content -->
    <div class="collapse navbar-collapse" id="navbarSupportedContent">
        <!-- Menu  -->
        <ul class="navbar-nav mr-auto">
        	<li class="nav-item dropdown">
	            <a class="nav-link dropdown-toggle" id="navbarDropdownMenuLink" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
	            	<fmt:message key="navbar.menu1"/>
	            </a>
	            <div class="dropdown-menu dropdown-primary" aria-labelledby="navbarDropdownMenuLink">
	            	<a class="dropdown-item" href="<c:url value="/shaclexcel" />" title="<fmt:message key="navbar.menu1.shaclexcel.tooltip" />"><fmt:message key="navbar.menu1.shaclexcel.label" /></a>
		            <a class="dropdown-item" href="<c:url value="/doc" />" title="<fmt:message key="navbar.menu1.doc.tooltip" />"><fmt:message key="navbar.menu1.doc.label" /></a>
		            <a class="dropdown-item" href="<c:url value="/draw" />" title="<fmt:message key="navbar.menu1.draw.tooltip" />"><fmt:message key="navbar.menu1.draw.label" /></a>
		            <a class="dropdown-item" href="<c:url value="/context" />" title="<fmt:message key="navbar.menu1.context.tooltip" />"><fmt:message key="navbar.menu1.context.label"/></a>		            
					<a class="dropdown-item" href="<c:url value="/jsonschema" />" title="<fmt:message key="navbar.menu1.jsonschema.tooltip" />"><fmt:message key="navbar.menu1.jsonschema.label"/></a>		            
		            <a class="dropdown-item" href="<c:url value="/sparql" />" title="<fmt:message key="navbar.menu1.sparql.tooltip" />"><fmt:message key="navbar.menu1.sparql.label" /></a>
		            <a class="dropdown-item" href="<c:url value="/excel" />" title="<fmt:message key="navbar.menu1.excel.tooltip" />"><fmt:message key="navbar.menu1.excel.label" /></a>
					<a class="dropdown-item" href="<c:url value="/shapes-catalog" />" title="<fmt:message key="navbar.menu1.shapes-catalog.tooltip" />"><fmt:message key="navbar.menu1.shapes-catalog.label" /></a>
	            </div>
	        </li>
	        <li class="nav-item dropdown">
	            <a class="nav-link dropdown-toggle" id="navbarDropdownMenuLink" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
	            	<fmt:message key="navbar.menu2"/>
	            </a>
	            <div class="dropdown-menu dropdown-primary" aria-labelledby="navbarDropdownMenuLink">
	            	<a class="dropdown-item" href="<c:url value="/validate" />" title="<fmt:message key="navbar.menu2.validate.tooltip" />"><fmt:message key="navbar.menu2.validate.label" /></a>
	            	<a class="dropdown-item" href="<c:url value="/generate" />" title="<fmt:message key="navbar.menu2.generate.tooltip" />"><fmt:message key="navbar.menu2.generate.label" /></a>
					<a class="dropdown-item" href="<c:url value="/analyze" />" title="<fmt:message key="navbar.menu2.analyze.tooltip" />"><fmt:message key="navbar.menu2.analyze.label" /></a>
	            	<a class="dropdown-item" href="<c:url value="/validate#badges" />" title="<fmt:message key="navbar.menu2.badge.tooltip" />"><fmt:message key="navbar.menu2.badge.label" /></a>
	            </div>
        	</li>
			<li class="nav-item dropdown">
            	<a class="nav-link dropdown-toggle" id="navbarDropdownMenuLink" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
            		<fmt:message key="navbar.menu3"/>
            	</a>
	            <div class="dropdown-menu dropdown-primary" aria-labelledby="navbarDropdownMenuLink">
	            	<a class="dropdown-item" href="<c:url value="/convert" />" title="<fmt:message key="navbar.menu3.from-owl.tooltip" />"><fmt:message key="navbar.menu3.from-owl.label" /></a>
	            	<a class="dropdown-item" href="<c:url value="/rules" />" title="<fmt:message key="navbar.menu3.rules.tooltip" />"><fmt:message key="navbar.menu3.rules.label" /></a>
	            	<a class="dropdown-item" href="<c:url value="/rules-catalog" />" title="<fmt:message key="navbar.menu3.rules-catalog.tooltip" />"><fmt:message key="navbar.menu3.rules-catalog.label" /></a>	            	
	            </div>
	        </li>
        </ul>
        
        
        <!-- /Links -->
        
        <ul class="navbar-nav ml-auto nav-flex-icons">
          <li class="nav-item dropdown">
            <a class="nav-link dropdown-toggle" id="langMenuLink" data-toggle="dropdown" aria-haspopup="true" aria-expanded="false">
              <span style="font-size:1.25rem;"><i class="fal fa-globe-africa"></i></span>&nbsp;&nbsp;${data.userLocale.language}
            </a>
            <div class="dropdown-menu dropdown-menu-right dropdown-default" aria-labelledby="langMenuLink">
              <a class="dropdown-item" href="<c:url value="/home?lang=fr" />">fr</a>
              <a class="dropdown-item" href="<c:url value="/home?lang=en" />">en</a>
              <a class="dropdown-item" href="<c:url value="/home?lang=es" />">es</a>
            </div>
          </li>
        </ul>

    </div>
    <!-- Collapsible content -->

</nav>
<!--/.Navbar-->
                