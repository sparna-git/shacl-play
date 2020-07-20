<#macro summaryEntryTitle summaryEntry toc>

<#if summaryEntry.resultSeverityLabel == "Violation">
	<span class="badge badge-pill badge-danger">Violation</span>
<#elseif summaryEntry.resultSeverityLabel == "Warning">
	<span class="badge badge-pill badge-warning">Warning</span>
<#elseif summaryEntry.resultSeverityLabel == "Info">
	<span class="badge badge-pill badge-info">Info</span>
<#else>
	<span class="badge badge-pill badge-secondary">Other</span>
</#if>

<#if toc>
	${summaryEntry.count} <em>"${summaryEntry.message}"</em>
	<#if summaryEntry.resultPath?has_content || summaryEntry.shResultSummaryEntry.sourceShape.isURIResource()>
		(<#if summaryEntry.resultPath?has_content>${ summaryEntry.resultPath } </#if><#if summaryEntry.shResultSummaryEntry.sourceShape.isURIResource()>in shape ${summaryEntry.sourceShape}</#if>)
    </#if>
	&nbsp;<small><a href="#${summaryEntry.hashCode()}">see details</a></small>
<#else>
	${summaryEntry.count} <em>"${summaryEntry.message}"</em>
	<#if summaryEntry.resultPath?has_content >
		(${ summaryEntry.resultPath })
	</#if>
</#if>

</#macro>