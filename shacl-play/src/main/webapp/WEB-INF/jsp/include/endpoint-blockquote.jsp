<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!-- setup the locale for the messages based on the language in the session -->
<fmt:setLocale value="${sessionScope['fr.sparna.rdf.shacl.shaclplay.SessionData'].userLocale.language}"/>
<fmt:setBundle basename="fr.sparna.rdf.shacl.shaclplay.i18n.shaclplay"/>


<blockquote class="blockquote bq-success">
  <div class="form-group row">
    <label for="inputUrlEndpoint" class="col-sm-3 col-form-label">    
    	<input
				type="radio"
				name="source"
				id="source-inputUrlEndpoint"
				value="endpoint"
				onchange="enabledInput('inputUrlEndpoint')" />
    	<fmt:message key="blockquote.endpoint.sparql.url" />
    </label>
    <div class="col-sm-9">
      <input 
      	type="text"
      	class="form-control"
      	id="inputUrlEndpoint"
      	name="inputUrlEndpoint"
      	placeholder="<fmt:message key="blockquote.endpoint.sparql.url.placeholder" />"
      	onkeypress="enabledInput('inputUrlEndpoint');"
      	onpaste="enabledInput('inputUrlEndpoint');"
      />
      <small class="form-text text-muted">
		<fmt:message key="blockquote.endpoint.sparql.url.help" />
	  </small>
    </div>
  </div>
</blockquote>