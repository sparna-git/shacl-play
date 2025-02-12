<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="doc2html.xsl"/>
	
	<xsl:variable name="LABELS_FR">
		<labels>
			<xsl:copy-of select="$LABELS_FR_BASE/labels/*[@key != 'DOCUMENTATION.TITLE']" />
			<entry key="DOCUMENTATION.TITLE" label="Documentation des données"/>
			<entry key="BY" label=" par " />
		</labels>
	</xsl:variable>

	<!-- English labels -->
	<xsl:variable name="LABELS_EN">
		<labels>
			<xsl:copy-of select="$LABELS_EN_BASE/labels/*[@key != 'DOCUMENTATION.TITLE']" />
			<entry key="DOCUMENTATION.TITLE" label="Dataset documentation"/>
			<entry key="BY" label=" by " />
		</labels>
	</xsl:variable>


	<!-- Select labels based on language param -->
	<xsl:variable name="LABELS" select="if($LANG = 'fr') then $LABELS_FR else $LABELS_EN" />
	
	
	<xsl:template match="ShapesDocumentation" mode="style_css_extra">		
			.sp_serialization_badge {
				margin-right: 0.5em;
				}

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
			
	</xsl:template>
	
	<xsl:template match="ShapesDocumentation" mode="javascript_extra_header">
		<!-- Chart Library -->
		<script src="https://cdn.jsdelivr.net/npm/chart.js">//</script>	
	</xsl:template>
	
	<xsl:template match="ShapesDocumentation" mode="javascript_extra">
		
		<!-- Only for generate a structure format -->
		<script type="text/javascript">					
			const listCodeSparql = document.querySelectorAll(".sparqlQuery");
			// listCodeSparql.forEach((t) => t.innerHTML = t.innerHTML.replace("SELECT","&lt;code&gt;&lt;span style=\"color: blue;\"&gt;&lt;strong&gt;SELECT&lt;/strong&gt;&lt;/span&gt;").replace("?result","?result &lt;br&gt;").replace("WHERE","&lt;span style=\"color: blue;\"&gt;&lt;strong&gt;WHERE&lt;/strong&gt;&lt;/span&gt;").replace("{","&lt;span style=\"color: #FF5733;\"&gt;&lt;strong&gt;&lt;br&gt;{&lt;br&gt;&lt;/strong&gt;&lt;/span&gt;").replace("}","&lt;span style=\"color: #FF5733;\"&gt;&lt;strong>&lt;br&gt;}&lt;/strong&gt;&lt;/span&gt;&lt;/code&gt;")) 
			// listCodeSparql.forEach((t) => t.innerHTML = t.innerHTML.replace("?result","?result &lt;br&gt;").replace("{","&lt;br&gt;{&lt;br&gt;").replace("}","&lt;br&gt;}"))
			// listCodeSparql.forEach((t) => t.innerHTML = t.innerHTML.replace("\n","&lt;br /&gt;"));
			listCodeSparql.forEach((t) => t.innerHTML = t.innerHTML.replace(/\n/g, "<br />"));
		</script>

		<!-- Tippy -->
		<script src="https://unpkg.com/@popperjs/core@2">//</script>
    	<script src="https://unpkg.com/tippy.js@6">//</script>
		<script type="text/javascript">					
			// Instance 
			tippy(".sparqlPopup",{
				content(reference) {
					return reference.parentElement.querySelector('.sparqlQuery').innerHTML;
					// return reference.children.getElementsByTagName('span').innerHTML;
				},
				allowHTML: true,
				trigger: 'click',
				// this allows to select the text inside the popup
				interactive: true
			});
		</script>
	</xsl:template>
	
	<xsl:template match="section/title">
		<xsl:variable name="style">
			<xsl:if test="string-length(../color) &gt; 0">
				color:<xsl:value-of select="../color"/>
			</xsl:if>
		</xsl:variable>
		
		<h3 id="{../sectionId}" class="sp_section_title_table" style="{$style}">
			<xsl:value-of select="."/>
			<xsl:apply-templates select="../numberOfTargets" />
		</h3>
	</xsl:template>

	<xsl:template match="numberOfTargets">
		<xsl:if test="number(.) &gt; 0">
			<span class="sp_badge">
				<xsl:value-of select="." />
			</span>
		</xsl:if>
	</xsl:template>

	<xsl:template match="section" mode="TOC">
		<li>
			<a href="{concat('#',sectionId)}">
				<xsl:value-of select="title" />
				<!-- Add indicator of number of target  -->
				<xsl:apply-templates select="./numberOfTargets" mode="TOC"/>
			</a>
			<xsl:if test="count(charts/chart) > 0">
				<ul class="ul_type_none sp_list_toc_l3">										
					<xsl:for-each select="charts/chart">
						<xsl:variable name="chartSectionId"><xsl:apply-templates select="." mode="id" /></xsl:variable>
						<li><a href="#{$chartSectionId}"><xsl:value-of select="$LABELS/labels/entry[@key='BY']/@label" /><xsl:value-of select="title" /></a></li>
					</xsl:for-each>
				</ul>
			</xsl:if>	
		</li>
	</xsl:template>

	<xsl:template match="numberOfTargets" mode="TOC">
		<xsl:if test="number(.) != 0">
			(<xsl:value-of select="." />)
		</xsl:if>
	</xsl:template>

	<!--  to be re-introduced later
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
				<xsl:value-of select="." />
			</em>
		</li>
	</xsl:template>
	-->

	<!-- Property groups -->
	<xsl:template match="propertyGroups">
		<xsl:if test="count(propertyGroup) > 0">
			<table class="sp_table_propertyshapes table-striped table-responsive">
				<thead>
					<tr>
						<th>
							<xsl:value-of
								select="$LABELS/labels/entry[@key='COLUMN_PROPERTY']/@label" />
						</th>
						<th>
							<xsl:value-of
								select="$LABELS/labels/entry[@key='COLUMN_URI']/@label" />
						</th>
						<th>
							<xsl:value-of
								select="$LABELS/labels/entry[@key='COLUMN_EXPECTED_VALUE']/@label" />
						</th>
						<th>
							<xsl:value-of
								select="$LABELS/labels/entry[@key='COLUMN_CARD']/@label" />
						</th>
						<th>
							<xsl:value-of
								select="$LABELS/labels/entry[@key='COLUMN_NUMBEROCCURRENCES']/@label" />
						</th>
						<th>
							<xsl:value-of
								select="$LABELS/labels/entry[@key='COLUMN_VALUESDISTINCTS']/@label" />
						</th>	
					</tr>
				</thead>
				<tbody>
					<xsl:apply-templates select="propertyGroup" />
				</tbody>
			</table><!-- end properties table -->
		</xsl:if>
	</xsl:template>

	<xsl:template match="property">
		<tr>
			<!-- Property name -->
			<td>
				<xsl:apply-templates select="./label"/>
			</td>
			<!-- Property URI -->
			<td>
				<xsl:apply-templates select="./propertyUri"/>
			</td>
			<!-- Expected Value -->
			<td>
				<xsl:apply-templates select="./expectedValue"/>
			</td>
			<!-- Cardinality -->
			<td>
				<xsl:apply-templates select="./cardinalite"/>
			</td>
			<!-- triples -->
			<td>
				<xsl:apply-templates select="./triples"/>
			</td>
			<!-- Distinct Objects -->
			<td>
				<div>
					<div>
						<xsl:apply-templates select="./distinctObjects"/>
						<span class="sparqlPopup" style="font-size:smaller; cursor: pointer;">&#160;[sparql]</span>
						<!-- <span class="sparqlPopup" style="font-size:smaller; cursor: pointer;"><br />list&#160;in&#160;sparql</span> -->
						<!-- Code Sparql Query -->
						<xsl:apply-templates select="./sparqlQueryProperty"/>
					</div>					
				</div>	
			</td>
		</tr>
	</xsl:template>
	
	<!-- triples -->
	<xsl:template match="property/triples">
		<xsl:value-of select="." />
	</xsl:template>

	<!-- distinct Objects -->
	<xsl:template match="property/distinctObjects">		
			<xsl:value-of select="."/>			
	</xsl:template>
	
	<!-- Sparql Query -->
	<xsl:template match="property/sparqlQueryProperty">
		<span class="sparqlQuery" style="display: none;">
			<xsl:value-of select="." />
		</span>
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
		<xsl:variable name="quote">
			'
		</xsl:variable>

		<div class="chart" id="{$currentSectionId}">
			<h4 class="sp_chart_title">
				<xsl:value-of select="../../title" />
				<xsl:value-of
					select="$LABELS/labels/entry[@key='BY']/@label" />
				<xsl:value-of select="title" />
			</h4>

			<div class="chart-content">
				<div class="chart-canvas">
					<canvas id="{$currentSectionId}_canvas"></canvas>

					<!-- JavaScript for drawn Pie Chart -->
					<script type="text/javascript">
						const data<xsl:value-of select="$currentSectionId" /> = {
							labels: [
								<xsl:value-of select="./items/item/normalize-space(concat($quote,label,$quote))" separator="," />
							],
							datasets: [{
								label: 'values',
								data: [
								<xsl:value-of select="./items/item/value" separator="," />
								],
								hoverOffset: 4
							}]
						};

						new Chart("<xsl:value-of select="$currentSectionId" />_canvas",{
							type: 'pie',
							data: data<xsl:value-of select="$currentSectionId" />,
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
		<xsl:value-of
			select="translate(concat(../../sectionId,'_chart',count(preceding-sibling::chart)+1), ' :','__')" />
	</xsl:template>

</xsl:stylesheet>