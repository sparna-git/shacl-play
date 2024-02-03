<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:import href="doc2html.xsl"/>

	<!-- Language parameter to the XSLT -->
	<xsl:param name="LANG"/>
	
	<!-- Indicates if we are producing the HTML for an HTML output of for a PDF conversion -->
	<xsl:param name="MODE">HTML</xsl:param>
	
	<xsl:variable name="LABELS_FR">
		<labels>
			<entry key="TOC" label="Table des Matières" />
			<entry key="COLUMN_PROPERTY" label="Nom de la propriété" />
			<entry key="COLUMN_URI" label="URI" />
			<entry key="COLUMN_EXPECTED_VALUE" label="Valeur attendue" />
			<entry key="COLUMN_CARD" label="Card." />
			<entry key="COLUMN_DESCRIPTION" label="Description" />
			<entry key="COLUMN_NUMBEROCCURRENCES" label="Triplets" />
			<entry key="COLUMN_VALUESDISTINCTS" label="Valeurs" />

			<entry key="PREFIXES.TITLE" label="Espaces de nom" />
			<entry key="PREFIXES.COLUMN.PREFIX" label="Préfixe" />
			<entry key="PREFIXES.COLUMN.URI" label="Espace de nom" />

			<entry key="METADATA.VERSION" label="Version : " />
			<entry key="METADATA.INTRODUCTION" label="Introduction" />
			<entry key="METADATA.DATECREATED" label="Date de création : " />
			<entry key="METADATA.DATE" label="Dernière modification : " />
			<entry key="METADATA.DATEISSUED" label="Date de publication : " />
			<entry key="METADATA.DATECOPYRIGHTED" label="Date de copyright : " />
			<entry key="METADATA.LICENSE" label="License : " />
			<entry key="METADATA.CREATOR" label="Auteur : " />
			<entry key="METADATA.PUBLISHER" label="Editeur : " />
			<entry key="METADATA.RIGHTHOLDER" label="Titulaire des droits : " />
			<entry key="METADATA.FEEDBACK" label="Contact : " />
			
			
			<entry key="METADATA.FORMATS" label="Télécharger les données : " />

			<entry key="DIAGRAM.TITLE" label="Diagrammes" />

			<entry key="DIAGRAM.HELP"
				label="Cliquez sur le diagramme pour naviguer vers la section correspondante" />
			<entry key="DIAGRAM.VIEW" label="Voir le diagramme en PNG" />

			<entry key="DOCUMENTATION.TITLE" label="Documentation des données"/>
			<entry key="DESCRIPTION.TITLE" label="Description"/>
			<entry key="RELEASE_NOTES.TITLE" label="Notes de version" />

			<entry key="LABEL_TARGETCLASS" label="S'applique à : " />
			<entry key="LABEL_NODEKIND" label="Type de noeud : " />
			<entry key="LABEL_PATTERNS" label="Structure des URIs: " />
			<entry key="LABEL_CLOSE" label="Shape fermée" />
			<entry key="LABEL_EXAMPLE" label="Exemple : "/>
			<entry key="LABEL_SUPERCLASSES" label="Hérite de : "/>
			<entry key="LABEL_OR" label=" ou "/>
			
			<entry key="BY" label=" par " />
		</labels>
	</xsl:variable>

	<!-- English labels -->
	<xsl:variable name="LABELS_EN">
		<labels>
			<entry key="TOC" label="Table of Contents" />
			<entry key="COLUMN_PROPERTY" label="Property name" />
			<entry key="COLUMN_URI" label="URI" />
			<entry key="COLUMN_EXPECTED_VALUE" label="Expected value" />
			<entry key="COLUMN_CARD" label="Card." />
			<entry key="COLUMN_DESCRIPTION" label="Description" />
			<entry key="COLUMN_NUMBEROCCURRENCES" label="Triples" />
			<entry key="COLUMN_VALUESDISTINCTS" label="Values" />

			<entry key="PREFIXES.TITLE" label="Namespaces" />
			<entry key="PREFIXES.COLUMN.PREFIX" label="Prefix" />
			<entry key="PREFIXES.COLUMN.URI" label="Namespace" />

			<entry key="METADATA.DATE" label="Last updated: " />
			<entry key="METADATA.VERSION" label="Version: " />
			<entry key="METADATA.INTRODUCTION" label="Abstract" />
			<entry key="METADATA.DATECREATED" label="Creation date: " />
			<entry key="METADATA.DATEISSUED" label="Issue date: " />
			<entry key="METADATA.DATECOPYRIGHTED" label="Copyright date: " />
			<entry key="METADATA.LICENSE" label="License: " />
			<entry key="METADATA.CREATOR" label="Creator: " />
			<entry key="METADATA.PUBLISHER" label="Publisher: " />
			<entry key="METADATA.RIGHTHOLDER" label="Rightsholder: " />
			<entry key="METADATA.FEEDBACK" label="Feedback: " />
			<entry key="METADATA.VERSIONNOTES" label="Version notes: " />
			
			<entry key="METADATA.FORMATS" label="Download serialization: " />
			
			<entry key="DIAGRAM.TITLE" label="Diagrams" />
			<entry key="DIAGRAM.HELP"
				label="Click diagram to navigate to corresponding section" />
			<entry key="DIAGRAM.VIEW" label="View as PNG" />
			
			<entry key="DOCUMENTATION.TITLE" label="Dataset documentation"/>
			<entry key="DESCRIPTION.TITLE" label="Description"/>
			<entry key="RELEASE_NOTES.TITLE" label="Release notes" />

			<entry key="LABEL_TARGETCLASS" label="Applies to: " />
			<entry key="LABEL_NODEKIND" label="Nodes: " />
			<entry key="LABEL_PATTERNS" label="URI pattern: " />
			<entry key="LABEL_CLOSE" label="Closed shape" />
			<entry key="LABEL_EXAMPLE" label="Example: "/>
			<entry key="LABEL_SUPERCLASSES" label="Inherits from: "/>
			<entry key="LABEL_OR" label=" or "/>
			
			<entry key="BY" label=" by " />
		</labels>
	</xsl:variable>


	<!-- Select labels based on language param -->
	<xsl:variable name="LABELS" select="if($LANG = 'fr') then $LABELS_FR else $LABELS_EN" />


	<!-- Principal -->
	<xsl:template match="/">
		<xsl:variable name="method">
			<xsl:choose>
				<xsl:when test="$MODE='PDF'">xhtml</xsl:when>
				<xsl:otherwise>html</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		<xsl:result-document method="{$method}" omit-xml-declaration="yes" indent="yes">
			<xsl:apply-templates />
		</xsl:result-document>
	</xsl:template>
	
	
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
			
	</xsl:template>
	
	<xsl:template match="ShapesDocumentation" mode="javascript_extra_header">
		<!-- Chart Library -->
		<script src="https://cdn.jsdelivr.net/npm/chart.js">//</script>	
	</xsl:template>
	
	<xsl:template match="ShapesDocumentation" mode="javascript_extra">
		
		<!-- Only for generate a structure format -->
		<script type="text/javascript">					
			const listCodeSparql = document.querySelectorAll("span#sparqlquery");
			listCodeSparql.forEach((t) => t.innerHTML = t.innerHTML.replace("SELECT","&lt;code&gt;&lt;span style=\"color: blue;\"&gt;&lt;strong&gt;SELECT&lt;/strong&gt;&lt;/span&gt;").replace("?result","?result &lt;br&gt;").replace("WHERE","&lt;span style=\"color: blue;\"&gt;&lt;strong&gt;WHERE&lt;/strong&gt;&lt;/span&gt;").replace("{","&lt;span style=\"color: #FF5733;\"&gt;&lt;strong&gt;&lt;br&gt;{&lt;br&gt;&lt;/strong&gt;&lt;/span&gt;").replace("}","&lt;span style=\"color: #FF5733;\"&gt;&lt;strong>&lt;br&gt;}&lt;/strong&gt;&lt;/span&gt;&lt;/code&gt;")) 
		</script>

		<!-- Tippy -->
		<script src="https://unpkg.com/@popperjs/core@2">//</script>
    	<script src="https://unpkg.com/tippy.js@6">//</script>
		<script type="text/javascript">					
			// Instance 
			tippy("div#sparql",{
				content(reference) {
					return reference.children.sparqlquery.innerHTML;
				},
				allowHTML: true,
			});
		</script>
	</xsl:template>
	
	
	
	
	
		
	<xsl:template match="section">	
		<div class="row mt-3">
			<div class="col">
				<section id="{sectionId}">
					<div class="sp_section_title_table_wrapper">
						
						<xsl:variable name="style">
							<xsl:if test="string-length(color) &gt; 0">
								color:<xsl:value-of select="color"/>
							</xsl:if>
						</xsl:variable>
						
						<h3 class="sp_section_title_table" style="{$style}">
							<xsl:value-of select="title"/>
							<xsl:apply-templates select="numberOfTargets" />
						</h3>
									
						<xsl:if test="subtitleUri">
							<code class="sp_section_uri"><xsl:value-of select="subtitleUri" /></code>
						</xsl:if>
					</div>
					
					<!-- Messages -->
					<xsl:apply-templates select="messages" />
					
					
					<xsl:if test="description != ''">
						<p>
							<!--  disable output escaping so that HTML is preserved -->
							<em><xsl:value-of select="description" disable-output-escaping="yes" /></em>
						</p>
					</xsl:if>
					<xsl:if
						test="targetClass/href or superClasses/link or nodeKind != '' or pattern != '' or closed='true' or skosExample != ''">
						<ul class="sp_list_description_properties">
							<xsl:if test="targetClass/href">
								<li>
									<xsl:value-of
										select="$LABELS/labels/entry[@key='LABEL_TARGETCLASS']/@label" />
									<a href="{targetClass/href}">
										<xsl:value-of select="targetClass/label" />
									</a>
								</li>
							</xsl:if>
							<xsl:if test="superClasses/link">
								<li>
									<xsl:value-of
										select="$LABELS/labels/entry[@key='LABEL_SUPERCLASSES']/@label" />
									<xsl:for-each select="superClasses/link">
										<xsl:choose>
											<xsl:when test="position() = 1">
								                <a href="{href}"><xsl:value-of select="label" /></a>
								            </xsl:when>
								            <xsl:otherwise>
								                , <a href="{href}"><xsl:value-of select="label" /></a>
								            </xsl:otherwise>
										</xsl:choose>
									</xsl:for-each>
								</li>
							</xsl:if>
							<xsl:if test="nodeKind != ''">
								<li>
									<xsl:value-of
										select="$LABELS/labels/entry[@key='LABEL_NODEKIND']/@label" />
									<xsl:value-of select="nodeKind" />
								</li>
							</xsl:if>
							<xsl:if test="closed='true'">
								<li>
									<xsl:value-of
										select="$LABELS/labels/entry[@key='LABEL_CLOSE']/@label" />
								</li>
							</xsl:if>
							<xsl:if test="pattern != ''">
								<li>
									<xsl:value-of
										select="$LABELS/labels/entry[@key='LABEL_PATTERNS']/@label" />
									<code><xsl:value-of select="pattern" /></code>							
								</li>
							</xsl:if>
							<!-- Example -->
							<xsl:if test="skosExample != ''">
								<li>
									<xsl:value-of select="$LABELS/labels/entry[@key='LABEL_EXAMPLE']/@label"/>
									<code><xsl:value-of select="skosExample"/></code>
								</li>
							</xsl:if>
							<xsl:if test="string-length(MessageOfValidate) &gt; 0">
								<li>
									<em>Message:</em><xsl:value-of select="MessageOfValidate"/>
								</li>
							</xsl:if>
						</ul>
					</xsl:if>
					<xsl:if test="sparqlTarget">
					<xsl:value-of select="$LABELS/labels/entry[@key='LABEL_TARGETCLASS']/@label" /><br/>
					<code>
						<pre>
							<xsl:value-of select="sparqlTarget" />					
						</pre>
					</code>
					</xsl:if>
					
					<!-- Properties table -->
					<xsl:apply-templates select="properties" />
					
					<!-- Section for Pie Chart -->
					<xsl:apply-templates select="charts" />		
					
					
				</section>
			</div>
		</div>
		<br/>
	</xsl:template>

	<xsl:template match="section/color">
		<li>
			<em>Message:</em>
			<xsl:value-of select="MessageOfValidate" />
		</li>
	</xsl:template>

	<xsl:template match="section/MessageOfValidate">
		<li>
			<em>Message:</em>
			<xsl:value-of select="MessageOfValidate" />
		</li>
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

	<!-- Properties -->
	<xsl:template match="properties">
		<xsl:if test="count(property)>0">
			<table
				class="sp_table_propertyshapes table-striped table-responsive">
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
					<xsl:apply-templates select="property" />
				</tbody>
			</table>
		</xsl:if><!-- end properties table -->
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
				<div id="sparql">
					<xsl:apply-templates select="./distinctObjects"/>
					<!-- Code Sparql Query -->
					<xsl:apply-templates select="./sparqlQueryProperty"/>
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
		<span id="sparqlquery" style="display: none;">
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