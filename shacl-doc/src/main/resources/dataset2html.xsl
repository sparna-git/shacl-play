<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	
	<!-- Indicates if we are producing the HTML for an HTML output of for a PDF conversion -->
	<xsl:param name="MODE">HTML</xsl:param>

	
	<xsl:template match="/">
		<xsl:apply-templates />		
	</xsl:template>
		
	<xsl:template match="ShapesDocumentation" mode="extra-css">
		
		.sp_alert {
				    --bs-alert-bg: transparent;
				    --bs-alert-padding-x: 1rem;
				    --bs-alert-padding-y: 1rem;
				    --bs-alert-margin-bottom: 1rem;
				    --bs-alert-color: inherit;
				    --bs-alert-border-color: transparent;
				    --bs-alert-border: 1px solid #f1aeb5;
				    --bs-alert-border-radius: var(--bs-border-radius);
				    --bs-alert-link-color: inherit;
				    --bs-alert-border-radius: 0.375rem;
				    position: relative;
				    padding: var(--bs-alert-padding-y) var(--bs-alert-padding-x);
				    margin-bottom: var(--bs-alert-margin-bottom);
				    color: var(--bs-alert-color);
				    background-color: #f8d7da;
				    border: var(--bs-alert-border);
				    border-radius: var(--bs-alert-border-radius);
		}

		.sp_badge {
			vertical-align: super;
			font-size: 80%;
			background-color: #36a2eb;
			color: white;
			margin-left: 10px;
			padding: 0px 4px;
			text-align: center;
			border-radius: 7px;
		}
		
		/* chart sections */
		
		.chart {
			
		}
		
		.chart-content {
			text-align: center;
		}
		
		.chart-canvas {
			width:70%;
			display:inline-block;
		}
		
							
		<!-- Chart Library -->
		<script src="https://cdn.jsdelivr.net/npm/chart.js">//</script>
				
	</xsl:template>
	
	<xsl:template match="section/color">
		<li>
			<em>Message:</em><xsl:value-of select="MessageOfValidate"/>
		</li>
	</xsl:template>
	
	<xsl:template match="section/MessageOfValidate">
		<li>
			<em>Message:</em><xsl:value-of select="MessageOfValidate"/>
		</li>
	</xsl:template>
	
	<xsl:template match="numberOfTargets">
		<xsl:if test="number(.) &gt; 0">
			<span class="sp_badge"><xsl:value-of select="."/></span>
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="numberOfTargets" mode="numberOfTargets_text">
		<xsl:if test="number(.) != 0">
			(<xsl:value-of select="." />)
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="messages">
		<div class="sp.alert sp_alert_danger">
			<ul>
				<xsl:apply-templates select="message" />
			</ul>
		</div>
	</xsl:template>

	<xsl:template match="message">
		<li>
			<em style="{concat('color:', ../../color)}">
				<xsl:value-of select="."/>	
			</em>
		</li>
	</xsl:template>
	
	<!-- Charts -->	
	<xsl:template match="charts">
		<div class="charts">
			<xsl:apply-templates />
		</div>
	</xsl:template>
	
	<xsl:template match="chart">
		<xsl:variable name="currentSectionId">
			<xsl:apply-templates select="." mode="id" />
		</xsl:variable>
		<xsl:variable name="quote">'</xsl:variable>
	
		<div class="chart" id="{$currentSectionId}">
			<h4 class="sp_chart_title"><xsl:value-of select="../../title" /><xsl:value-of select="$LABELS/labels/entry[@key='BY']/@label" /><xsl:value-of select="title" /></h4>
			
			<div class="chart-content">
				<div class="chart-canvas">			
					<canvas id="{$currentSectionId}_canvas"></canvas>
					
					<!-- JavaScript for drawn Pie Chart  -->
					<script type="text/javascript">
						const data<xsl:value-of select="$currentSectionId"/> = {
							  labels: [<xsl:value-of select="./items/item/concat($quote,label,$quote)" separator=","/>],
							  datasets: [{
							    label: 'values',
							    data: [<xsl:value-of select="./items/item/value" separator=","/>],
							    hoverOffset: 4
							  }]
							};
			
						new Chart("<xsl:value-of select="$currentSectionId"/>_canvas",{
							type: 'pie',
						  	data: data<xsl:value-of select="$currentSectionId"/>,
						  	options: {
						  		plugins: {
						  			legend: {
						  				display: true,
						                position: 'right'
						  			}
						  		},
						  		layout: {
						  			autoPadding: false
						  		},
						  		aspectRatio: 2						  		
						  	}
						});
					</script>					
				</div>
			</div>
		</div>
	
	</xsl:template>
	
	<xsl:template match="chart" mode="id">
		<xsl:value-of select="translate(concat(../../sectionId,'_chart',count(preceding-sibling::chart)+1), ' :','__')"/>
	</xsl:template>
	
</xsl:stylesheet>