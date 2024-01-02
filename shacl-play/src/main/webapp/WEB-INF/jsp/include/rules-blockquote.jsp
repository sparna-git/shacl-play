<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!-- setup the locale for the messages based on the language in the session -->
<fmt:setLocale value="${sessionScope['fr.sparna.rdf.shacl.shaclplay.SessionData'].userLocale.language}"/>
<fmt:setBundle basename="fr.sparna.rdf.shacl.shaclplay.i18n.shaclplay"/>

<script type="text/javascript">
	function enabledShapeInput(selected) {
		document.getElementById('sourceShape-' + selected).checked = true;
		document.getElementById('inputShapeUrl').disabled = selected != 'inputShapeUrl';
		document.getElementById('inputShapeCatalog').disabled = selected != 'inputShapeCatalog';
		document.getElementById('inputShapeFile').disabled = selected != 'inputShapeFile';
		document.getElementById('inputShapeInline').disabled = selected != 'inputShapeInline';
	}
</script>
  
  <blockquote class="blockquote bq-primary">		  
  
      <div class="form-group row">

	    <label for="inputShapeFile" class="col-sm-3 col-form-label">
	    
	    	<input
					type="radio"
					name="shapesSource"
					id="sourceShape-inputShapeFile"
					value="file"
					checked="checked"
					onchange="enabledShapeInput('inputShapeFile')" />
	    	<fmt:message key="blockquote.rules.upload" />
	    
	    </label>
	    <div class="col-sm-9">
	    		<div class="fileinput fileinput-new input-group" data-provides="fileinput">
				  <div class="form-control" data-trigger="fileinput" id="inputShapeFile">
				    <i class="fal fa-upload"></i><span class="fileinput-filename with-icon"></span>
				  </div>
				  <span class="input-group-append">
				    <span class="input-group-text fileinput-exists" data-dismiss="fileinput">
				      <fmt:message key="blockquote.rules.upload.remove" />
				    </span>
				
				    <span class="input-group-text btn-file">
				      <span class="fileinput-new"><fmt:message key="blockquote.rules.upload.select" /></span>
				      <span class="fileinput-exists"><fmt:message key="blockquote.rules.upload.change" /></span>
				      <input type="file" name="inputShapeFile" multiple onchange="enabledShapeInput('inputShapeFile')">
				    </span>
				  </span>
				</div>
				<small class="form-text text-muted">
					  <fmt:message key="blockquote.rules.upload.help" />
			  </small>
	    </div>
	  </div>
      
      <c:if test="${not empty data.catalog.entries}">
	      <div class="form-group row">	
		    <label for="inputShapeCatalog" class="col-sm-3 col-form-label">
		    
		    	<input
						type="radio"
						name="shapesSource"
						id="sourceShape-inputShapeCatalog"
						value="catalog"
						onchange="enabledShapeInput('inputShapeCatalog')" />
		    	<fmt:message key="blockquote.rules.catalog" />					    
		    </label>
		    <div class="col-sm-9">
		    		<select class="form-control" id="inputShapeCatalog" name="inputShapeCatalog" onchange="enabledShapeInput('inputShapeCatalog');">
				      	<c:forEach items="${data.catalog.entries}" var="entry">
				      		<option value="${entry.id}">${entry.title}</option>
				      	</c:forEach>
				    </select>
				    <small class="form-text text-muted">
						  <fmt:message key="blockquote.rules.catalog.help" />
				    </small>
		    </div>
		  </div>
	  </c:if>
	  
	  <div class="form-group row">
	    <label for="inputShapeUrl" class="col-sm-3 col-form-label">
	    
	    	<input
					type="radio"
					name="shapesSource"
					id="sourceShape-inputShapeUrl"
					value="url"
					onchange="enabledShapeInput('inputShapeUrl')" />
	    	<fmt:message key="blockquote.rules.url" />
	    </label>
	    <div class="col-sm-9">
	      <input 
	      	type="text"
	      	class="form-control"
	      	id="inputShapeUrl"
	      	name="inputShapeUrl"
	      	placeholder="<fmt:message key="blockquote.rules.url.placeholder" />"
	      	onkeypress="enabledShapeInput('inputShapeUrl');"
	      	onchange="enabledShapeInput('inputShapeUrl')"
	      >
	      <small class="form-text text-muted">
			  <fmt:message key="blockquote.rules.url.help" />
	    </small>
	    </div>
	  </div>
	  <div class="form-group row">
	    <label for="inputShapeInline" class="col-sm-3 col-form-label">
	    
	    	<input
					type="radio"
					name="shapesSource"
					id="sourceShape-inputShapeInline"
					value="inline"
					onchange="enabledShapeInput('inputShapeInline')" />
	    	<fmt:message key="blockquote.rules.inline" />
	    </label>
	    <div class="col-sm-9">
	      <textarea 
	      	class="form-control"
	      	id="inputShapeInline"
	      	name="inputShapeInline"
	      	rows="5"
	      	onkeypress="enabledShapeInput('inputShapeInline');"
	      	onpaste="enabledShapeInput('inputShapeInline');"
	      ></textarea>
	      <small class="form-text text-muted">
			  <fmt:message key="blockquote.rules.inline.help" />
		  </small>
	    </div>	
      </div>
     </blockquote>
