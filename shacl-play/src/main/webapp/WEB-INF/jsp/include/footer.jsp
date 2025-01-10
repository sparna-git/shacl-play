<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!-- setup the locale for the messages based on the language in the session -->
<fmt:setLocale value="${sessionScope['fr.sparna.rdf.shacl.shaclplay.SessionData'].userLocale.language}"/>
<fmt:setBundle basename="fr.sparna.rdf.shacl.shaclplay.i18n.shaclplay"/>

<footer id="footer" style="margin-top:30px;">
	    SHACL Play! by <a href="http://blog.sparna.fr" target="_blank">Thomas Francart</a>, <a href="http://sparna.fr" target="_blank"><img src="<c:url value="/resources/img/sparna.png" />" /></a>
      	&nbsp;|&nbsp;
      	version : ${applicationScope.applicationData.buildVersion} (${applicationScope.applicationData.buildTimestamp})
      	&nbsp;|&nbsp;
      	SHACL Play! embeds <a href="https://github.com/TopQuadrant/shacl" target="_blank">TobBraid SHACL API</a> from <a href="https://www.topquadrant.com/" target="_blank">TopQuadrant</a>
      	&nbsp;|&nbsp;
		<a href="https://github.com/sparna-git/shacl-play"><i class="fa-brands fa-github"></i></a>
		<br />
      	<br />
</footer>