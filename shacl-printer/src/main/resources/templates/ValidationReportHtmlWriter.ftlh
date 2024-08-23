<#import "ReportTitle.ftlh" as title>
<#import "SummaryEntryTitle.ftlh" as summaryEntryTitle>

<style type="text/css">
	.report-section {
		margin-top: 2em;
	}
	
	.table-outer {
		height:250px;
		overflow:hidden;
	}
	
	.table-outer.open {
		height:unset;
		overflow:visible;
	}
</style>

<div class="row  justify-content-center">
<div class="col">

<div class="report-header card">
          <div class="card-header ${ report.globalSeverity.headerCssClass }">
	      	<h3>
	      	<@title.reportTitle 
	      		hasMatched=report.validationReport.hasMatched()
	      		violations=report.validationReport.numberOfViolations
	      		warnings=report.validationReport.numberOfWarnings
	      		infos=report.validationReport.numberOfInfos
	      		others=report.validationReport.numberOfOthers
	      	/>
	      	</h3>
	      	<#if report.validationReport.hasMatched() && report.validationReport.shapesWithNoMatch?size &gt; 0>
		      	<small>Warning, following shapes did not match anything :
		      	<ul>
		      		<#list report.validationReport.shapesWithNoMatch as aShapeWithNoMatch>
		      			<li>${aShapeWithNoMatch}</li>
		      		</#list>
		      	</ul>
		      	</small>
	      	</#if>
          </div>
          <div class="card-body">
            <ul style="list-style-type: none;">
            	<#list report.resultsSummary as summaryEntry>
            		<li><@summaryEntryTitle.summaryEntryTitle summaryEntry=summaryEntry toc=true /></li>
            	</#list>
            </ul>
          </div>
</div><!-- /.report-header -->

<div class="report-body">
<#list report.resultsSummary as summaryEntry>
	<div id="${summaryEntry.hashCode()}" class="report-section">
		<h3><@summaryEntryTitle.summaryEntryTitle summaryEntry=summaryEntry toc=false /></h3>

		<div class="report-section-card card">
          <div class="card-header">
			<ul>
				<li>Shape : ${summaryEntry.sourceShape}</li>
				<li>Constraint : ${summaryEntry.sourceConstraintComponent}</li>
			</ul>
          </div>
          <div class="card-body">
          	<#if summaryEntry.count &gt; 10 >
                <div class="table-outer">
        			<table class="table table-borderless table-sm">
                        <thead>
                          <tr>
                            <th>Focus node</th>
                            <th>Property or path</th>
                            <th>Value</th>
                          </tr>
                        </thead>
                        <tbody>
                          <#list report.getLimitedResultsFor(summaryEntry) as result>
                              <tr>
                                <td><span title="${result.focusNode}">${result.focusNode}</span></td>
                                <td><span title="${result.resultPath}">${result.resultPath}</span></td>
                                <td>${result.value?no_esc}</td>
                              </tr>
                          </#list>
                        </tbody>
                    </table>
                </div>
                <div><a class="toggleTableLink" href="#"><i>show/hide <#if summaryEntry.count &gt; report.maximumResults>${report.maximumResults}<#else>${summaryEntry.count}</#if> results <#if summaryEntry.count &gt; report.maximumResults>(on a total of ${ summaryEntry.count })</#if></i></a></div>
            <#else>
        	     <table class="table table-borderless table-sm">
                    <thead>
                      <tr>
                        <th>Focus node</th>
                        <th>Property or path</th>
                        <th>Value</th>
                      </tr>
                    </thead>
                    <tbody>
                      <#list report.getLimitedResultsFor(summaryEntry) as result>
                          <tr>
                            <td><span title="${result.focusNode}">${result.focusNode}</span></td>
                            <td><span title="${result.resultPath}">${result.resultPath}</span></td>
                            <td>${result.value?no_esc}</td>
                          </tr>
                      </#list>
                    </tbody>
                </table>
            </#if>
          </div>
		</div><!-- /.report-section -->
	</div>
</#list>
</div> <!-- /.report-body -->

</div>
</div>
