<#macro reportTitle hasMatched violations warnings infos others>

<#if violations &gt; 0>
	${ violations } Violation<#if violations &gt; 1>s</#if>
	<#if (warnings &gt; 0 || infos &gt; 0 || others &gt; 0)>,</#if>
</#if>
<#if warnings &gt; 0>
	${ warnings } Warning<#if warnings &gt; 1>s</#if>
	<#if (infos &gt; 0 || others &gt; 0)>,</#if>
</#if>
<#if infos &gt; 0>
	${ infos } Info<#if infos &gt; 1>s</#if>
	<#if (others &gt; 0)>,</#if>
</#if>
<#if others &gt; 0>
	${ others } Other<#if others&gt; 1>s</#if>
</#if>

<#if violations == 0 && warnings == 0 && infos == 0 && others == 0>
	<#if hasMatched>
		<span class="badge badge-pill badge-success">Valid</span>&nbsp;Data is conformant !
	<#else>
		<span class="badge badge-pill badge-secondary">No Match</span>&nbsp;Shapes did not match anything !
	</#if>
</#if>

</#macro>