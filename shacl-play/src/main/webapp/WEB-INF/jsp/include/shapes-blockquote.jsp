<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!-- setup the locale for the messages based on the language in the session -->
<fmt:setLocale value="${sessionScope['fr.sparna.rdf.shacl.shaclplay.SessionData'].userLocale.language}"/>
<fmt:setBundle basename="fr.sparna.rdf.shacl.shaclplay.i18n.shaclplay"/>

<script type="text/javascript">
	function enabledShapeInput${param.suffix}(selected) {
		document.getElementById('sourceShape-' + selected).checked = true;
		document.getElementById('inputShapeUrl${param.suffix}').disabled = selected != 'inputShapeUrl${param.suffix}';
		if(document.getElementById('inputShapeCatalog${param.suffix}') != null) {
			document.getElementById('inputShapeCatalog${param.suffix}').disabled = selected != 'inputShapeCatalog${param.suffix}';
		}
		document.getElementById('inputShapeFile${param.suffix}').disabled = selected != 'inputShapeFile${param.suffix}';
		document.getElementById('inputShapeInline${param.suffix}').disabled = selected != 'inputShapeInline${param.suffix}';
	}
</script>

 <blockquote class="blockquote bq-primary">		  
   <div class="form-group row">

   <label for="inputShapeFile${param.suffix}" class="col-sm-3 col-form-label">
      	<input
				type="radio"
				name="shapesSource${param.suffix}"
				id="sourceShape-inputShapeFile${param.suffix}"
				value="file"
				checked="checked"
				onchange="enabledShapeInput${param.suffix}('inputShapeFile${param.suffix}')" />
    	<fmt:message key="blockquote.shapes.upload" />
    </label>
    <div class="col-sm-9">
   		<div class="fileinput fileinput-new input-group" data-provides="fileinput">
		  <div class="form-control" data-trigger="fileinput" id="inputShapeFile${param.suffix}">
		    <i class="fal fa-upload"></i><span class="fileinput-filename with-icon"></span>
		  </div>
		  <span class="input-group-append">
		    <span class="input-group-text fileinput-exists" data-dismiss="fileinput">
		      <fmt:message key="blockquote.shapes.upload.remove" />
		    </span>
		
		    <span class="input-group-text btn-file">
		      <span class="fileinput-new"><fmt:message key="blockquote.shapes.upload.select" /></span>
		      <span class="fileinput-exists"><fmt:message key="blockquote.shapes.upload.change" /></span>
		      <input type="file" name="inputShapeFile${param.suffix}" multiple onchange="enabledShapeInput${param.suffix}('inputShapeFile${param.suffix}')">
		    </span>
		  </span>
		</div>
		<small class="form-text text-muted">
			<fmt:message key="blockquote.shapes.upload.help" />
        </small>
    </div>
  </div>    
      <c:if test="${not empty data.catalog.entries}">
      <div class="form-group row">	
	    <label for="inputShapeCatalog${param.suffix}" class="col-sm-3 col-form-label">
	    	<input
					type="radio"
					name="shapesSource${param.suffix}"
					id="sourceShape-inputShapeCatalog${param.suffix}"
					value="catalog"
					onchange="enabledShapeInput${param.suffix}('inputShapeCatalog${param.suffix}')" />
	    	<fmt:message key="blockquote.shapes.catalog" />					    
	    </label>
	    <div class="col-sm-9">
    		<select class="form-control" id="inputShapeCatalog${param.suffix}" name="inputShapeCatalog${param.suffix}" onchange="enabledShapeInput${param.suffix}('inputShapeCatalog${param.suffix}');">
		      	<c:forEach items="${data.catalog.entries}" var="entry">
		      		<option value="${entry.id}">${entry.title}</option>
		      	</c:forEach>
		    </select>
		    <small class="form-text text-muted">
				  <fmt:message key="blockquote.shapes.catalog.help" />
		    </small>
	    </div>
	  </div>
  </c:if>
  
  <div class="form-group row">
    <label for="inputShapeUrl" class="col-sm-3 col-form-label">    
    	<input
				type="radio"
				name="shapesSource${param.suffix}"
				id="sourceShape-inputShapeUrl${param.suffix}"
				value="url"
				onchange="enabledShapeInput${param.suffix}('inputShapeUrl${param.suffix}')" />
    	<fmt:message key="blockquote.shapes.url" />
    </label>
    <div class="col-sm-9">
      <input 
      	type="text"
      	class="form-control"
      	id="inputShapeUrl${param.suffix}"
      	name="inputShapeUrl${param.suffix}"
      	placeholder="<fmt:message key="blockquote.shapes.url.placeholder" />"
      	onkeypress="enabledShapeInput${param.suffix}('inputShapeUrl${param.suffix}');"
      	onchange="enabledShapeInput${param.suffix}('inputShapeUrl${param.suffix}')"
      >
      <small class="form-text text-muted">
		  <fmt:message key="blockquote.shapes.url.help" />
    </small>
    </div>
  </div>
  <div class="form-group row">
    <label for="inputShapeInline${param.suffix}" class="col-sm-3 col-form-label">
    	<input
				type="radio"
				name="shapesSource${param.suffix}"
				id="sourceShape-inputShapeInline${param.suffix}"
				value="inline"
				onchange="enabledShapeInput${param.suffix}('inputShapeInline${param.suffix}')" />
    	<fmt:message key="blockquote.shapes.inline" />
    </label>
    <div class="col-sm-9">
      <textarea 
      	class="form-control"
      	id="inputShapeInline${param.suffix}"
      	name="inputShapeInline${param.suffix}"
      	rows="5"
      	onkeypress="enabledShapeInput${param.suffix}('inputShapeInline${param.suffix}');"
      	onpaste="enabledShapeInput${param.suffix}('inputShapeInline${param.suffix}');"
      ></textarea>
      <small class="form-text text-muted">
		  <fmt:message key="blockquote.shapes.inline.help" />
	  </small>
    </div>	
  </div>
  <br/>
</blockquote>