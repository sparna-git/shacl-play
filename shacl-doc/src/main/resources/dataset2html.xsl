<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	
	<xsl:import href="doc2html.xsl"/>
	
	<!-- Language parameter to the XSLT -->
	<xsl:param name="LANG"/>
	
	
	<!-- Indicates if we are producing the HTML for an HTML output of for a PDF conversion -->
	<xsl:param name="MODE">HTML</xsl:param>


	<xsl:template match="ShapesDocumentation" mode="extra-css">
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
					
					.sp_chart_title {
						font-family: Georgia, Garamond, serif;
						margin-bottom: 0rem;
						font-weight: 500;
						color: #1e1e1f;
						line-height: 1.2em;
					}
					
					/* Tooltip section */
					
					.tooltip-sh {
						position: relative;
						display: inline-block;
						border-bottom: 1px dotted darkgray;
					}
					
					.tooltip-sh .tooltip-query {
					
						visibility: hidden;
						width: auto;
						text-align: left;
						border-radius: 6px;
						padding: 5px 0;
						fond-size: 0.875em;
						
						/* Position the tooltip */
						position: absolute;
						z-index: 1;	
						/* Box Tooltip */
						background-color: #D3D3D3;
						-webkit-box-shadow: 0px 0px 3px 1px rgba(50, 50, 50, 0.4);
						-moz-box-shadow: 0px 0px 3px 1px rgba(50, 50, 50, 0.4);
					  	box-shadow: 0px 0px 3px 1px rgba(50, 50, 50, 0.4);					
					  	-webkit-border-radius: 5px;
					  -moz-border-radius: 5px;

					  padding: 7px 12px;
					  position: absolute;

					  word-wrap: break-word;
					}
					
					.tooltip-sh:hover .tooltip-query {
						visibility: visible;
					}		
							
					<xsl:choose>
						<xsl:when test="$MODE = 'PDF'">
							.toc { }
							.sp_section_title_toc {
								margin-block-start: 0.83em;
							    margin-block-end: 0.83em;
							    margin-inline-start: 0px;
							    margin-inline-end: 0px;    
								margin-bottom: 0.5rem;
								display: block;
								font-weight: 500;
								color: #1e1e1f;
								line-height: 1.2em;
							}
							
							.sp_container_principal {
								width: calc(100% - 40px);
								max-width: 1000px;
								margin-left: auto;
								margin-right: auto;
							}
							
							.pt-4 {
								padding-top: 2.5rem !important;
							}
						</xsl:when>
						<xsl:otherwise>
							.container {
							    width: calc(100% - 40px);
							    max-width: 1000px;
							    margin-left: 300px;
							    margin-right: auto;
							}
							.container {width: calc(100% - 500px);}
							.toc {
								position: fixed;
								top: 0;
								left: 0;
								font-size: small;
								padding: 10px 10px;
								width: auto;
								border-right: solid 2px #eeeeee;
								bottom: 0;
								overflow-y: scroll;
								background-color:white;
								max-width:255px;
							}
							.sp_list_toc {padding-left: 0px;}
							.sp_list_toc_l2 {padding-left: 10px;}
							.sp_list_toc_l3 {padding-left: 8px; margin-bottom: 0.2rem;}						
						</xsl:otherwise>
					</xsl:choose>
					
				</style>
				
				<meta charset="UTF-8"/>
				<meta http-equiv="Content-Type" content="text/html; charset=UTF-8"/>
				<meta name="lang" content="{$LANG}"/>
				<meta property="og:locale" content="{$LANG}"/>
				
				<xsl:variable name="var_title" select="title"/>
				<xsl:if test="$var_title != ''">
					<title><xsl:value-of select="$var_title"/></title>
					<meta name="apple-mobile-web-app-title" content="{$var_title}"/>
					<meta name="twitter:title" content="{$var_title}"/>
					<meta property="og:title" content="{$var_title}"/>
				</xsl:if>
				
				<!-- Chart Library -->
				<script src="https://cdn.jsdelivr.net/npm/chart.js">//</script>
							
			</head>
			<body>
				
				<div class="sp_container_principal container pt-4">
					
			        <xsl:choose>
			            	<xsl:when test="string-length(imgLogo) &gt; 0">
			            		<table style="width:100%">
				            		<tr>
				            			<td width="20%"><img src="{imgLogo}"/></td>
				            			<td width="80%"><h1 class="mb-4 sp_section_title_header"><xsl:value-of select="title" /></h1></td>		
				            		</tr>	
			            		</table>
			            	</xsl:when>
			            	<xsl:otherwise>
			            			<h1 class="mb-4 sp_section_title_header"><xsl:value-of select="title" /></h1>
			            	</xsl:otherwise>
			            </xsl:choose>			            
			         
					<div>
						<xsl:apply-templates select="datecreated" />
						<xsl:apply-templates select="dateissued" />
						<xsl:apply-templates select="modifiedDate" />
						<xsl:apply-templates select="yearCopyRighted" />
						<xsl:apply-templates select="versionInfo" />
						<xsl:apply-templates select="licenses" />
						<xsl:apply-templates select="creators" />
						<xsl:apply-templates select="publishers" />
						<xsl:apply-templates select="rightsHolders" />
						<xsl:apply-templates select="feedbacks" />
						<!-- section for the formats -->
						<xsl:if test="string-length(formats) &gt; 0">
							<xsl:apply-templates select="formats" />
						</xsl:if>
						<br/>						
					</div>					
					
					<xsl:apply-templates select="abstract_" />
					<xsl:apply-templates select="." mode="TOC" />
					<xsl:apply-templates select="prefixes" />
					<xsl:if test="diagrams or depictions">						
						<xsl:if test="string-length(diagrams) &gt; 0 ">
							<div class="sp_section_row mt-3">
								<div class="sp_section_col">
									<section>
										<h2 id="diagrams" class="sp_section_subtitle">
											<xsl:value-of select="$LABELS/labels/entry[@key='DIAGRAM.TITLE']/@label" />
										</h2>
										<xsl:apply-templates select="diagrams" />
										<xsl:apply-templates select="depictions"/>
									</section>
								</div>
							</div>	
						</xsl:if>						
					</xsl:if>
				
					<xsl:apply-templates select="descriptionDocument" />
						
					<xsl:apply-templates select="sections" />
					
					<!--  release notes at the end -->
					<xsl:apply-templates select="releaseNotes" />
				</div>
				
				
				
				<!-- Anchor for the document -->
				<script src="https://cdn.jsdelivr.net/npm/anchor-js/anchor.min.js">//</script>
    			<script>				
					anchors.options = {
	                    icon: '#'
	                  };
               		anchors.options.placement = 'left';
					anchors.add();		
				</script>
				
				<!-- Format a sparql code -->
				<script type="text/javascript">
					
					const listCodeSparql = document.querySelectorAll("code#SparqlQuery")
	
					listCodeSparql.forEach((t) => t.innerHTML = t.innerHTML.replace("?result","?result &lt;br&gt;").replace("{","&lt;br&gt;{&lt;br&gt;").replace(";","; &lt;br&gt;").replace("}","&lt;br&gt;}") )
	
				</script>	
					
			</body>
		</html>
	</xsl:template>

	<xsl:template match="section/title">
		<xsl:variable name="style">
			<xsl:if test="string-length(../color) &gt; 0">
				color:<xsl:value-of select="../color"/>
			</xsl:if>
		</xsl:variable>
	
		<h3 class="sp_section_title_table" style="{$style}">
			<xsl:value-of select="."/>
			<xsl:apply-templates select="../numberOfTargets" />
		</h3>
	</xsl:template>
	
	<xsl:template match="numberOfTargets">
		<span class="sp_badge"><xsl:value-of select="."/></span>
	</xsl:template>


	<xsl:template match="message">
		<li>
			<em style="{concat('color:', ../../color)}">
				<xsl:value-of select="."/>	
			</em>
		</li>
	</xsl:template>
	
	
	<!-- Properties -->
	<xsl:template match="properties">
		<xsl:if test="count(property)>0">
			<table class="sp_table_propertyshapes table-striped table-responsive">
				<thead>
					<tr>
						<th>
							<xsl:value-of
								select="$LABELS/labels/entry[@key='COLUMN_PROPERTY']/@label" />
						</th>
						<th >
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
						<!--  
						<th class="sp_description_column">
							<xsl:value-of
								select="$LABELS/labels/entry[@key='COLUMN_DESCRIPTION']/@label" />
						</th>
						-->
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
					<xsl:apply-templates select="property" />
				</tbody>
			</table>
		</xsl:if><!-- end properties table -->
	</xsl:template>

	<xsl:template match="property">
	
		<xsl:element name="tr">
			<xsl:if test="string-length(color) &gt; 0">
				<xsl:attribute name="style">background-color:<xsl:value-of select="'#E58787'"/></xsl:attribute>				
			</xsl:if>
			<!-- Property name -->
			<td>
				<xsl:value-of select="label" />
			</td>
			<!-- Property URI -->
			<td>
				<xsl:if test="propertyUri">
					<xsl:choose>
						<xsl:when test="propertyUri/href != ''">
							<code>
								<xsl:choose>
									<xsl:when test="color != null">
										<a href="{propertyUri/href}" style="{color}"><xsl:value-of select="propertyUri/label" /></a>
									</xsl:when>
									<xsl:otherwise>
										<a href="{propertyUri/href}"><xsl:value-of select="propertyUri/label" /></a>
									</xsl:otherwise>
								</xsl:choose>															
							</code>	
						</xsl:when>
						<xsl:otherwise>
							<code><xsl:value-of select="propertyUri/label" /></code>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:if>				
			</td>
			<!-- Expected Value -->
			<td>
				<xsl:choose>
					<xsl:when test="expectedValue/linkNodeShape[node()]">
						<code>
							<a href="{concat('#',expectedValue/linkNodeShapeUri)}">
								<xsl:value-of select="expectedValue/linkNodeShape" />
							</a>
						</code>
					</xsl:when>
					<xsl:otherwise>
						<xsl:choose>
							<xsl:when test="string-length(expectedValue/or) > 0">
								<xsl:variable name="length" select="count(expectedValue/or/or)" />
								<xsl:for-each select="expectedValue/or/or">
								<xsl:variable name="current" select="normalize-space(.)" />
									<xsl:choose>
										<xsl:when test="starts-with($current,'xsd:') or starts-with($current,'sh:') or starts-with($current,'rdf:')">
											<code><xsl:value-of select="$current" /></code>
										</xsl:when>
										<xsl:otherwise>
											<code>
												<a href="{concat('#',$current)}">
													<xsl:value-of select="$current" />
												</a>
											</code>
										</xsl:otherwise>
									</xsl:choose>
									
									<xsl:choose>
										<xsl:when test="position() &lt; $length">
											<code> <xsl:value-of select="$LABELS/labels/entry[@key='LABEL_OR']/@label" /> </code>
										</xsl:when>
									</xsl:choose>
								</xsl:for-each>
							</xsl:when>
							<xsl:otherwise>
								<code>
									<!-- disable output espacing as we may have <sup> in rendering -->
									<xsl:value-of disable-output-escaping="yes" select="expectedValue/expectedValueLabel" />
								</code>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:otherwise>
				</xsl:choose>
				<br />
				<xsl:if test="expectedValueAdditionnalInfoIn/text()">
					<p>
						<small>
							<!-- disable output espacing as we may have <sup> in rendering -->
							<xsl:value-of disable-output-escaping="yes"
								select="concat('(',expectedValueAdditionnalInfoIn,')')" />
						</small>
					</p>
				</xsl:if>
				<xsl:if test="expectedValueAdditionnalInfoValue/text()">
					<p>
						<small>
							<xsl:value-of
								select="expectedValueAdditionnalInfoValue" />
						</small>
					</p>
				</xsl:if>
			</td>
			<!-- Cardinality -->
			<td>
				<div style="width:30px">
					<xsl:value-of select="cardinalite" />
				</div>								
			</td>
			<!-- Number of triples -->
			<td>				
				<xsl:value-of select="triples" />
			</td>
			<!-- Distinct objects -->
			<td>
				<xsl:variable name="sparqlQuery" select="sparqlQueryProperty"/>
				<div class="tooltip-sh"><xsl:value-of select="distinctObjects" />
					<span class="tooltip-query">					
						<code id="SparqlQuery">
							<xsl:value-of select="$sparqlQuery"/>
						</code>
				</span>
				</div>
			</td>
		</xsl:element>
		
	</xsl:template>
	
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
	
	<!-- Release notes at the end  -->
	<xsl:template match="releaseNotes[text() != '']">
		<div>
			<h2 id="releaseNotes" class="sp_section_subtitle">
				<xsl:value-of select="$LABELS/labels/entry[@key='RELEASE_NOTES.TITLE']/@label" />
			</h2>
			<!--  disable output escaping so that HTML is preserved -->
			<xsl:value-of select="."  disable-output-escaping="yes"/>			
		</div>
	</xsl:template>

	<!-- don't print what was not matched -->
	<xsl:template match="text()"></xsl:template>

	

</xsl:stylesheet>