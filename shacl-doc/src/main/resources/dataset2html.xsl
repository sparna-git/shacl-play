<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	
	<!-- Language parameter to the XSLT -->
	<xsl:param name="LANG"/>
	
	
	<!-- Indicates if we are producing the HTML for an HTML output of for a PDF conversion -->
	<xsl:param name="MODE">HTML</xsl:param>

	<!-- controls output style -->
	<!--
	<xsl:output indent="yes" method="xhtml" omit-xml-declaration="yes"/>
	 -->

	<!-- french labels -->
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

	<xsl:template match="ShapesDocumentation">
		<!-- <xsl:text disable-output-escaping='yes'>&lt;!DOCTYPE html&gt;</xsl:text> -->
		<!-- <xsl:text disable-output-escaping='yes'>&lt;!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd"&gt;</xsl:text>  -->
		<html lang="{$LANG}">
			<head>
				
				<style>
					body {
						margin: 0;
						font-family: -apple-system, BlinkMacSystemFont, "Segoe UI", Roboto,
							"Helvetica Neue", Arial, "Noto Sans", "Liberation Sans", sans-serif,
							"Apple Color Emoji", "Segoe UI Emoji", "Segoe UI Symbol",
							"Noto Color Emoji";
						background-color: #fff;
					}
					
					.anchor {
						float: left;
						padding-right: 4px;
						margin-left: -20px;
						line-height: 1;
						padding-top: 12px;
					}
							
					a {
						color: #007bff;
						text-decoration: none;
						background-color: transparent;
					}
					
					a:-webkit-any-link {
						cursor: pointer;
					}
					
					.p {
						font-size: 0.875em;
					}
					
					h2 {
						margin: 25px 0px 10px 0px;
					}
					
					h3 {
						font-size: 1.4em;
					}
					
					h4 {
						font-size: 1.2em;
					}
					
					@media only print {
					}
					
					@page {
						size: A4 portrait;
						margin-top: 1.2cm;
						margin-bottom: 1.2cm;
						margin-left: 1.2cm;
						margin-right: 1.2cm;
						background-repeat: no-repeat;
						background-position: 40px 10px;
						
						@bottom-center
						{
						content
						:
						counter(
						page
						);
					}
					
					}
							
					/* This section draw the format web */
					.ul_type_none {
						list-style-type: none;
					}
					
					.sp_list_description_properties {
						/* don't set it lower otherwise it gets hidden in PDF */
						padding-left: 20px;
					}
					
					dl, ol, ul {
						margin-top: 0;
						margin-bottom: 1rem;
					}
					
					li {
						display: list-item;
						text-align: -webkit-match-parent;
					}
					
					.sp_section_title_header {
						font-family: Georgia, Garamond, serif;	
						margin-top: 25px;
						margin-bottom: 2.5rem;
					    font-size: 1.5625rem;
						display: block;
						font-weight: 500;
						color: #1e1e1f;
						line-height: 1.2em;   
					}
					
					.sp_section_subtitle {
						font-family: Georgia, Garamond, serif;	
						margin-top: 0;
						margin-bottom: 0.5rem;
						display: block;
						font-weight: 500;
						color: #1e1e1f;
						line-height: 1.2em;  
					}
					
					.sp_section_title_table {
						font-family: Georgia, Garamond, serif;	
						/* 0 because URI is right under */
						margin-bottom: 0rem;
						display: block;
						font-weight: 500;
						color: #1e1e1f;
						line-height: 1.2em; 
					}
							

					.sp_section_title_toc {
						font-family: Georgia, Garamond, serif;	
					}

					/* URI below the title of the section */
					.sp_section_uri	 {
						margin-top: 0px;
					}

					/* div wrapping section title and URI below - same as a paragraph margin */
					.sp_section_title_table_wrapper {
						margin-bottom: 16px;
						border-bottom: 1px solid;	
					}
					
					 
					table {
						display: table;
						border-spacing: 0px;
						margin-bottom: 1rem;
					}
					
					tr:nth-child(even) {
						background-color: #eee;
					}
							
					.sp_table_prefixes table {
						border-collapse: collapse;
						margin-bottom: 1rem;
						color: #212529;
					}
											
					.sp_table_prefixes td {
						padding: 0.25rem;
						vertical-align: top;
						border-top: 1px solid #dee2e6;
					}
					
					.sp_table_propertyshapes {
						border-collapse: collapse;
						width: 100%;
					}
										
					.sp_table_propertyshapes thead {
						display: table-header-group;
						vertical-align: middle;
						border-color: inherit;
					}
													
					.sp_table_propertyshapes tr {
					    display: table-row;
					    vertical-align: inherit;
					    border-color: inherit;
					}
													
					.sp_table_propertyshapes th:nth-child(4) {
						width: 6%;
					}
													
					.sp_table_propertyshapes td {
						padding: 0.75rem;
						border-top: 1px solid #dee2e6;
					}
													
					.sp_table_propertyshapes tbody {
						display: table-row-group;
						vertical-align: middle;
						border-color: inherit;
					}
										
					.sp_table_propertyshapes_col_description {
						word-break: break-word;
					}
					
					.sp_serialization_badge {
						margin-right: 0.5em;
					}
					
					.sp_alert_danger {						
						    --bs-alert-color: #58151c;
						    --bs-alert-bg: #f8d7da;
						    --bs-alert-border-color: #f1aeb5;
						    --bs-alert-link-color: #58151c;
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
					
					.sp_chart_title {
						font-family: Georgia, Garamond, serif;
						margin-bottom: 0rem;
						font-weight: 500;
						color: #1e1e1f;
						line-height: 1.2em;
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
					
			</body>
		</html>
	</xsl:template>
	
	<xsl:template match="ShapesDocumentation" mode="TOC">
		<!-- Table de matieres -->
		<div class="sp_section_title_toc toc">
			<h2 id="Index"><xsl:value-of select="$LABELS/labels/entry[@key='TOC']/@label" /></h2>
			<ul role="list" class="sp_list_toc ul_type_none t-x-mode">
				<li>
					<!-- Prefixes -->
					<a href="#prefixes"><xsl:value-of select="$LABELS/labels/entry[@key='PREFIXES.TITLE']/@label" /></a>
				</li>
				<!-- Diagram -->
				<xsl:if test="string-length(diagrams) &gt; 0 or string-length(depictions) &gt; 0">					
					<li>
						<a href="#diagrams">
							<xsl:value-of select="$LABELS/labels/entry[@key='DIAGRAM.TITLE']/@label" />
						</a>
					</li>
				</xsl:if>
				<!-- Description -->
				<xsl:if test="descriptionDocument">
					<li>
						<a href="#description">
							<xsl:value-of select="$LABELS/labels/entry[@key='DESCRIPTION.TITLE']/@label"/>
						</a>
					</li>
				</xsl:if>
				<li>
					<a href="#documentation">
							<xsl:value-of select="$LABELS/labels/entry[@key='DOCUMENTATION.TITLE']/@label"/>
					</a>
					<ul role="list" class="ul_type_none sp_list_toc_l2">
						<!-- Section -->
						<xsl:for-each select="sections/section">			
							<li>
								<!-- print number of instances in TOC -->
								<a href="{concat('#',sectionId)}">
									<xsl:choose>
										<xsl:when test="numberOfTargets">										
											<xsl:value-of select="title" />  (<xsl:value-of select="numberOfTargets"/>)
										</xsl:when>
										<xsl:otherwise>
											<xsl:value-of select="title" />									
										</xsl:otherwise>
									</xsl:choose>	
								</a>
								<xsl:if test="count(charts/chart) > 0">
									<ul class="ul_type_none sp_list_toc_l3">										
										<xsl:for-each select="charts/chart">
											<xsl:variable name="chartSectionId"><xsl:apply-templates select="." mode="id" /></xsl:variable>
											<li><a href="#{$chartSectionId}"><xsl:value-of select="$LABELS/labels/entry[@key='BY']/@label" /><xsl:value-of select="title" /></a></li>
										</xsl:for-each>
									</ul>
								</xsl:if>								
								<xsl:apply-templates select="title" />
							</li>
						</xsl:for-each>
					</ul>
				</li>
				<!-- Release notes -->
				<xsl:if test="releaseNotes">
					<li>
						<a href="#releaseNotes">
							<xsl:value-of select="$LABELS/labels/entry[@key='RELEASE_NOTES.TITLE']/@label"/>
						</a>
					</li>
				</xsl:if>
			</ul>
		</div>		
	</xsl:template>

	<xsl:template match="datecreated">
		<b>
			<xsl:value-of
				select="$LABELS/labels/entry[@key='METADATA.DATECREATED']/@label" />
		</b>
		<xsl:value-of select="." />	
		<br />
	</xsl:template>
	
	<xsl:template match="dateissued">
		<b>
			<xsl:value-of
				select="$LABELS/labels/entry[@key='METADATA.DATEISSUED']/@label" />
		</b>
		<xsl:value-of select="." />
		<br />
	</xsl:template>
	
	<xsl:template match="modifiedDate">
		<b>
			<xsl:value-of
				select="$LABELS/labels/entry[@key='METADATA.DATE']/@label" />
		</b>
		<xsl:value-of select="." />
		<br />	
	</xsl:template>
	
	<xsl:template match="yearCopyRighted">
		<b>
			<xsl:value-of
				select="$LABELS/labels/entry[@key='METADATA.DATECOPYRIGHTED']/@label" />
		</b>
		<xsl:value-of select="." />
		<br />
	</xsl:template>
	
	<xsl:template match="versionInfo">
		<b>
			<xsl:value-of
				select="$LABELS/labels/entry[@key='METADATA.VERSION']/@label" />
		</b>
		<xsl:value-of select="." />
		<br />	
	</xsl:template>

	<xsl:template match="creators">
		<b>
			<xsl:value-of
				select="$LABELS/labels/entry[@key='METADATA.CREATOR']/@label" />
		</b>
		<xsl:apply-templates />
		<br />
	</xsl:template>
	
	<xsl:template match="publishers">
		<b>
			<xsl:value-of
				select="$LABELS/labels/entry[@key='METADATA.PUBLISHER']/@label" />
		</b>
		<xsl:apply-templates />
		<br />	
	</xsl:template>
	
	<xsl:template match="rightsHolders">
		<b>
			<xsl:value-of
				select="$LABELS/labels/entry[@key='METADATA.RIGHTHOLDER']/@label" />
		</b>
		<xsl:apply-templates />
		<br />
	</xsl:template>
	
	<xsl:template match="licenses">
		<b>
			<xsl:value-of
				select="$LABELS/labels/entry[@key='METADATA.LICENSE']/@label" />
		</b>
		<xsl:apply-templates />
		<br />
	</xsl:template>
	
	<xsl:template match="feedbacks">
		<b>
			<xsl:value-of
				select="$LABELS/labels/entry[@key='METADATA.FEEDBACK']/@label" />
		</b>
		<xsl:apply-templates />	
		<br />
	</xsl:template>
	
	<!--  shared template for all values -->
	<xsl:template match="creator | publisher | rightsHolder | license | feedback">
		<xsl:choose>
			<xsl:when test="href">
				<a href="{href}" target="_blank"><xsl:value-of select="label" /></a>
			</xsl:when>
			<xsl:otherwise>
				<xsl:value-of select="label"/>				
			</xsl:otherwise>
		</xsl:choose>
		<!-- if we have following sibling nodes, add a comma -->
		<xsl:if test="following-sibling::*">, </xsl:if>
	</xsl:template>
	
	<xsl:template match="formats">
		<b id="formats">
			<xsl:value-of select="$LABELS/labels/entry[@key='METADATA.FORMATS']/@label" />
		</b>
		<div>
			<xsl:apply-templates/>
		</div>								
	</xsl:template>
	
	<xsl:template match="format">
		<span class="sp_serialization_badge">
			<a href="{dcatURL}" target="_blank">
				<!-- JSON -->
				<xsl:if test="dctFormat = 'https://www.iana.org/assignments/media-types/application/ld+json'">
					<img src="https://img.shields.io/badge/Format-JSON_LD-blue.png" alt="JSON-LD" /> 
				</xsl:if>
				<!-- XML -->
				<xsl:if test="dctFormat = 'https://www.iana.org/assignments/media-types/application/rdf+xml'">
					<img src="https://img.shields.io/badge/Format-RDF/XML-blue.png" alt="RDF/XML" /> 
				</xsl:if>			
				<!-- N3 -->
				<xsl:if test="dctFormat = 'https://www.iana.org/assignments/media-types/application/n-triples'">
					<img src="https://img.shields.io/badge/Format-N_Triples-blue.png" alt="N-Triples" /> 			
				</xsl:if>
				<!-- ttl -->
				<xsl:if test="dctFormat = 'https://www.iana.org/assignments/media-types/text/turtle'">
					<img src="https://img.shields.io/badge/Format-TTL-blue.png" alt="TTL" /> 
				</xsl:if>				
			</a>
		</span>
	</xsl:template>
	
	<xsl:template match="abstract_">
		<div class="row mt-3">
			<div class="col">
				<h2 id="abstract" class="sp_section_subtitle">
					<xsl:value-of
						select="$LABELS/labels/entry[@key='METADATA.INTRODUCTION']/@label" />
				</h2>
				<!--  disable output escaping so that HTML is preserved -->
				<xsl:value-of select="." disable-output-escaping="yes" />			
			</div>
		</div>
	</xsl:template>
	
	<xsl:template match="depictions">
		<xsl:apply-templates/>		
	</xsl:template>
	
	<xsl:template match="depiction">
		<img src="{.}" style="width:100%;"/>
	</xsl:template>
	 
	<xsl:template match="diagrams">
		<xsl:apply-templates />		
	</xsl:template>
	
	<xsl:template match="diagram">
		<xsl:if test="displayTitle">
			<h3><xsl:value-of select="displayTitle" /></h3> 
		</xsl:if>
		<xsl:if test="diagramDescription">
			<p><xsl:value-of select="diagramDescription" /></p> 
		</xsl:if>
		<xsl:choose>
			<xsl:when test="$MODE = 'PDF'">
				<!--  When outputting PDF, inserts the PNG image -->
				<img src="{pngLink}" style="width:100%;" alt="a diagram representing this application profile" />
			</xsl:when>
			<xsl:otherwise>
				<!-- @disable-output-escaping prints the raw XML string as XML in the 
					document and removes XML-encoding of the characters
				-->
				<div>
					<xsl:value-of select="svg" disable-output-escaping="yes" />
				</div>
				<small class="form-text text-muted">
					<xsl:value-of
							select="$LABELS/labels/entry[@key='DIAGRAM.HELP']/@label" />
					<xsl:text> | </xsl:text>
					<a href="{pngLink}" target="_blank">
						<xsl:value-of select="$LABELS/labels/entry[@key='DIAGRAM.VIEW']/@label" />
					</a>			
				</small>
				<xsl:comment>
					<xsl:value-of select="plantUmlString" disable-output-escaping="yes" />
				</xsl:comment>	
			</xsl:otherwise>
		</xsl:choose>			
	</xsl:template>

	<!-- Description Title -->
	<xsl:template match="descriptionDocument[text() != '']">
		<div>
			<h2 id="description" class="sp_section_subtitle">
				<xsl:value-of select="$LABELS/labels/entry[@key='DESCRIPTION.TITLE']/@label" />
			</h2>
			<!--  disable output escaping so that HTML is preserved -->
			<xsl:value-of select="."  disable-output-escaping="yes"/>			
		</div>
	</xsl:template>
		
	<!-- Prefix -->
	<xsl:template match="prefixes">
		<div class="row mt-3">
			<div class="col">
				<section id="prefixes">
					<h2 class="sp_section_subtitle">
						<xsl:value-of
							select="$LABELS/labels/entry[@key='PREFIXES.TITLE']/@label" />
					</h2>
					<table class="sp_table_prefixes table table-striped table-responsive">
						<thead>
							<tr>
								<th>
									<xsl:value-of
										select="$LABELS/labels/entry[@key='PREFIXES.COLUMN.PREFIX']/@label" />
								</th>
								<th>
									<xsl:value-of
										select="$LABELS/labels/entry[@key='PREFIXES.COLUMN.URI']/@label" />
								</th>
							</tr>
						</thead>
						<tbody>
							<xsl:apply-templates />
						</tbody>
					</table>
				</section>
			</div>
		</div>
		<br/>		
	</xsl:template>

	<xsl:template match="prefixe">
		<tr>
			<td>
				<xsl:value-of select="prefix" />
			</td>
			<td>
				<a href="{namespace}" target="_blank"><xsl:value-of select="namespace" /></a>
			</td>
		</tr>
	</xsl:template>

	<!-- Sections -->
	<xsl:template match="sections">
		<h2 id="documentation" class="sp_section_subtitle">
			<xsl:value-of select="$LABELS/labels/entry[@key='DOCUMENTATION.TITLE']/@label" />			
		</h2>
		<xsl:apply-templates select="section" />		
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
					<code><pre>
<xsl:value-of select="sparqlTarget" />					
					</pre></code>
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
	
	<xsl:template match="numberOfTargets">
		<span class="sp_badge"><xsl:value-of select="."/></span>
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
											<code> or </code>
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
			<!-- Description properties 
			<td class="sp_table_propertyshapes_col_description">
				<xsl:value-of select="description" />
			</td>	
			-->
			<!-- Number of triples -->
			<td>				
				<xsl:value-of select="triples" />
			</td>
			<!-- Distinct objects -->
			<td>
				<xsl:value-of select="distinctObjects" />
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