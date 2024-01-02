<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!-- setup the locale for the messages based on the language in the session -->
<fmt:setLocale value="${sessionScope['fr.sparna.rdf.shacl.shaclplay.SessionData'].userLocale.language}"/>
<fmt:setBundle basename="fr.sparna.rdf.shacl.shaclplay.i18n.shaclplay"/>

<script type="text/javascript">
	function enabledInput(selected) {
		document.getElementById('source-' + selected).checked = true;
		document.getElementById('inputUrl').disabled = selected != 'inputUrl';
		document.getElementById('inputFile').disabled = selected != 'inputFile';
		document.getElementById('inputInline').disabled = selected != 'inputInline';
		document.getElementById('inputUrlEndpoint').disabled = selected != 'inputUrlEndpoint';
	}
</script>

<blockquote class="blockquote bq-success">
  <div class="form-group row">
     	
    <label for="inputFile" class="col-sm-3 col-form-label">
    
    	<input
				type="radio"
				name="source"
				id="source-inputFile"
				value="file"
				checked="checked"
				onchange="enabledInput('inputFile')" />
    	<fmt:message key="blockquote.data.upload" />
    
    </label>
    <div class="col-sm-9">
    		<div class="fileinput fileinput-new input-group" data-provides="fileinput">
			  <div class="form-control" data-trigger="fileinput" id="inputFile">
			    <i class="fal fa-upload"></i><span class="fileinput-filename with-icon"></span>
			  </div>
			  <span class="input-group-append">
			    <span class="input-group-text fileinput-exists" data-dismiss="fileinput">
			      <fmt:message key="blockquote.data.upload.remove" />
			    </span>
			
			    <span class="input-group-text btn-file">
			      <span class="fileinput-new"><fmt:message key="blockquote.data.upload.select" /></span>
			      <span class="fileinput-exists"><fmt:message key="blockquote.data.upload.change" /></span>
			      <input type="file" name="inputFile" multiple onchange="enabledInput('inputFile')">
			    </span>
			  </span>
			</div>
			<small class="form-text text-muted">
			  <fmt:message key="blockquote.data.upload.help" />
			</small>
    </div>
  </div>
  <div class="form-group row">
    <label for="inputUrl" class="col-sm-3 col-form-label">
    
    	<input
				type="radio"
				name="source"
				id="source-inputUrl"
				value="url"
				onchange="enabledInput('inputUrl')" />
    	<fmt:message key="blockquote.data.url" />
    </label>
    <div class="col-sm-9">
      <input 
      	type="text"
      	class="form-control"
      	id="inputUrl"
      	name="inputUrl"
      	placeholder="<fmt:message key="blockquote.data.url.placeholder" />"
      	onkeypress="enabledInput('inputUrl');"
      	onpaste="enabledInput('inputUrl');"
      />
      <small class="form-text text-muted">
			  <fmt:message key="blockquote.data.url.help" />
	  </small>
    </div>
  </div>
  <div class="form-group row">
    <label for="inputInline" class="col-sm-3 col-form-label">
    
    	<input
				type="radio"
				name="source"
				id="source-inputInline"
				value="inline"
				onchange="enabledInput('inputInline')" />
    	<fmt:message key="blockquote.data.inline" />
    </label>
    <div class="col-sm-9">
      <textarea 
      	class="form-control"
      	id="inputInline"
      	name="inputInline"
      	rows="5"
      	onkeypress="enabledInput('inputInline');"
		onpaste="enabledInput('inputInline')"
      ></textarea>
      <small class="form-text text-muted">
			  <fmt:message key="blockquote.data.inline.help" />
	  </small>
    </div>
  </div>
</blockquote>