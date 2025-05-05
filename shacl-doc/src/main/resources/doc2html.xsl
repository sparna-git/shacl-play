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
	<!-- this "BASE" variable can be overwritten by other stylesheets -->
	<xsl:variable name="LABELS_FR_BASE">
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
			<entry key="METADATA.IMPORTS" label="Imports : " />

			<entry key="DIAGRAM.TITLE" label="Diagrammes" />

			<entry key="DIAGRAM.HELP"
				label="Cliquez sur le diagramme pour naviguer vers la section correspondante" />
			<entry key="DIAGRAM.VIEW" label="Voir le diagramme en PNG" />

			<entry key="DOCUMENTATION.TITLE" label="Documentation du modèle"/>
			<entry key="DESCRIPTION.TITLE" label="Description"/>
			<entry key="RELEASE_NOTES.TITLE" label="Notes de version" />

			<entry key="LABEL_NODESHAPE_DESCRIPTION" label="Description"/>
			<entry key="LABEL_TARGETCLASS" label="S'applique à : " />
			<entry key="LABEL_NODEKIND" label="Type de noeud : " />
			<entry key="LABEL_PATTERNS" label="Structure d'identifiant : " />
			<entry key="LABEL_CLOSE" label="Shape fermée" />
			<entry key="LABEL_EXAMPLE" label="Exemple : "/>
			<entry key="LABEL_SUPERCLASSES" label="Hérite de : "/>
			<entry key="LABEL_OR" label=" ou "/>
			<entry key="LABEL_TARGETSUBJECTSOF" label="S'applique aux sujets de: "/>
			<entry key="LABEL_TARGETOBJECTSOF" label="S'applique aux objets de: "/>
			
			<entry key="LABEL_NO_PROPERTIES" label="Aucune propriété spécifique"/>
			
			<entry key="LABEL_CONSTRAINTS" label="Règles additionnelles"/>			
		</labels>
	</xsl:variable>
	<!-- In this stylesheet we just copy the base labels -->
	<xsl:variable name="LABELS_FR">
		<xsl:copy-of select="$LABELS_FR_BASE" />
	</xsl:variable>

	<!-- English labels -->
	<!-- this "BASE" variable can be overwritten by other stylesheets -->
	<xsl:variable name="LABELS_EN_BASE">
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
			<entry key="METADATA.IMPORTS" label="Imports : " />
			
			<entry key="METADATA.FORMATS" label="Download serialization: " />
			
			<entry key="DIAGRAM.TITLE" label="Diagrams" />
			<entry key="DIAGRAM.HELP"
				label="Click diagram to navigate to corresponding section" />
			<entry key="DIAGRAM.VIEW" label="View as PNG" />
			
			<entry key="DOCUMENTATION.TITLE" label="Model documentation"/>
			<entry key="DESCRIPTION.TITLE" label="Description"/>
			<entry key="RELEASE_NOTES.TITLE" label="Release notes" />

			<entry key="LABEL_NODESHAPE_DESCRIPTION" label="Description"/>
			<entry key="LABEL_TARGETCLASS" label="Applies to: " />
			<entry key="LABEL_NODEKIND" label="Nodes: " />
			<entry key="LABEL_PATTERNS" label="URI pattern: " />
			<entry key="LABEL_CLOSE" label="Closed shape" />
			<entry key="LABEL_EXAMPLE" label="Example: "/>
			<entry key="LABEL_SUPERCLASSES" label="Inherits from: "/>
			<entry key="LABEL_OR" label=" or "/>
			<entry key="LABEL_TARGETSUBJECTSOF" label="Applies to subjects of: "/>
			<entry key="LABEL_TARGETOBJECTSOF" label="Applies to objects of: "/>
			
			<entry key="LABEL_NO_PROPERTIES" label="No specific properties"/>
			
			<entry key="LABEL_CONSTRAINTS" label="Additionnal constraints"/>
			
		</labels>
	</xsl:variable>
	<!-- In this stylesheet we just copy the base labels -->
	<xsl:variable name="LABELS_EN">
		<xsl:copy-of select="$LABELS_EN_BASE" />
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
				<!-- Call style css -->
				<style>	
					<xsl:apply-templates select="../ShapesDocumentation" mode="style_css_doc"/>
					<xsl:apply-templates select="../ShapesDocumentation" mode="style_css_extra"/>
				</style>
				<!-- we insert a comment that can be overwritten to be replaced with extra CSS rules -->
				<xsl:comment>EXTRA CSS</xsl:comment>
				
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
				
				<xsl:if test="jsonldOWL">
					<script type="application/ld+json">
						<xsl:apply-templates select="jsonldOWL" />
					</script>
				</xsl:if>
				
				<xsl:apply-templates select="../ShapesDocumentation" mode="javascript_extra_header"/>
												
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
						<xsl:apply-templates select="OWLimports" />
						<!-- section for the formats -->
						<xsl:if test="string-length(formats) &gt; 0">
							<xsl:apply-templates select="formats" />
						</xsl:if>
						<br/>						
					</div>					
					
					<xsl:apply-templates select="abstract_" />
					<xsl:apply-templates select="." mode="TOC" />
					<xsl:apply-templates select="prefixes" />
					<xsl:if test="diagrams/diagram or depictions/depiction">						
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
					anchors.add('h2,h3');
					// for links inside tables		
					anchors.add('td[id]');
				</script>
				<xsl:apply-templates select="../ShapesDocumentation" mode="javascript_extra"/>
			</body>
		</html>
	</xsl:template>
	
	<!-- Style CSS -->
	<xsl:template match="ShapesDocumentation" mode="style_css_doc">
		
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
						font-size: 1.1em;
						font-weight: bold;
					}

					.constraint_list li {
						list-style:"> ";
						font-style: italic;
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
					
					.sp_section_nodeshape_diagram {
						padding-bottom: 16px;
					}
					
					.sp_section_nodeshape_center {
						display: flex;
						justify-content: center;
					}
					
					table {
						display: table;
						border-spacing: 0px;
						margin-bottom: 1rem;
					}
					
					.table-striped tr:nth-child(even) {
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
					
					tr.sp_propertyGroup {
						font-style: italic;
						background-color: #def0f7;
					}
					
					tr.sp_propertyGroup td {
						border-bottom: 1px solid black;
						padding-bottom: 0.25 rem;
					}
					
					.sp_serialization_badge {
						margin-right: 0.5em;
					}

					.sp_nodeshape_description {
						background: #efefef;
						padding-top:1px;
						padding-bottom:1px;
						padding-left:10px;
						padding-right:10px;
						border-radius: 5px;
						margin-bottom:10px;
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
							    max-width: 1100px;
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
							.sp_list_toc_l3 {padding-left: 8px;}					
						</xsl:otherwise>
					</xsl:choose>
		
	</xsl:template>
	
	<xsl:template match="ShapesDocumentation" mode="TOC">
		<!-- Table de matieres -->
		<div class="sp_section_title_toc toc">
			<h2 id="index"><xsl:value-of select="$LABELS/labels/entry[@key='TOC']/@label" /></h2>
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
						<xsl:apply-templates select="sections/section" mode="TOC" />												
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
	
	<xsl:template match="jsonldOWL">
		<xsl:value-of select="."/>		
	</xsl:template>

	<xsl:template match="section" mode="TOC">
		<li>
			<a href="{concat('#',sectionId)}">
				<xsl:value-of select="title" />
			</a>
		</li>
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

	<xsl:template match="creators[creator]">
		<b>
			<xsl:value-of
				select="$LABELS/labels/entry[@key='METADATA.CREATOR']/@label" />
		</b>
		<xsl:apply-templates />
		<br />
	</xsl:template>
	
	<xsl:template match="publishers[publisher]">
		<b>
			<xsl:value-of
				select="$LABELS/labels/entry[@key='METADATA.PUBLISHER']/@label" />
		</b>
		<xsl:apply-templates />
		<br />	
	</xsl:template>
	
	<xsl:template match="rightsHolders[rightsHolder]">
		<b>
			<xsl:value-of
				select="$LABELS/labels/entry[@key='METADATA.RIGHTHOLDER']/@label" />
		</b>
		<xsl:apply-templates />
		<br />
	</xsl:template>
	
	<xsl:template match="licenses[license]">
		<b>
			<xsl:value-of
				select="$LABELS/labels/entry[@key='METADATA.LICENSE']/@label" />
		</b>
		<xsl:apply-templates />
		<br />
	</xsl:template>
	
	<xsl:template match="feedbacks[feedback]">
		<b>
			<xsl:value-of
				select="$LABELS/labels/entry[@key='METADATA.FEEDBACK']/@label" />
		</b>
		<xsl:apply-templates />	
		<br />
	</xsl:template>
	
	<xsl:template match="OWLimports[OWLimport]">
		<b>
			<xsl:value-of
				select="$LABELS/labels/entry[@key='METADATA.IMPORTS']/@label" />
		</b>
		<xsl:apply-templates />
		<br />	
	</xsl:template>
	
	<!--  shared template for all values -->
	<xsl:template match="creator | publisher | rightsHolder | license | feedback | OWLimport">
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
				<img src="{pngLink}" style="width:80%;" alt="a diagram representing this application profile" />
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
			<h2 id="description" class="sp_section_subtitle" style="margin-top: 1em;">
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
				<section>
					<h2 id="prefixes" class="sp_section_subtitle">
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
				<section>
					<div class="sp_section_title_table_wrapper">
					
						<xsl:apply-templates select="title" />
						
						<xsl:if test="subtitleUri">
							<xsl:choose>
								<xsl:when test="subtitleUri/href">
									<code class="sp_section_uri"><a href="{subtitleUri/href}"><xsl:value-of select="subtitleUri/label" /></a></code>
								</xsl:when>
								<xsl:otherwise>
									<code class="sp_section_uri"><xsl:value-of select="subtitleUri/label" /></code>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:if>
					
					</div>
					
					<xsl:if test="description != ''">
						<div class="sp_nodeshape_description">
							<!--  disable output escaping so that HTML is preserved -->
							<xsl:value-of select="description" disable-output-escaping="yes" />
						</div>
					</xsl:if>

					<!-- depiction, before bullet list -->
					<xsl:apply-templates select="depictions" />

					<xsl:if
						test="
							targetClass/href
							or
							superClasses/link
							or
							nodeKind != ''
							or
							pattern != ''
							or
							closed='true'
							or
							skosExample != ''
							or
							targetSubjectsOf != ''
							or
							targetObjectsOf != ''
							or
							sparqlTarget
						"
					>
						<ul class="sp_list_description_properties">
							<xsl:if test="targetClass/targetClass">

								<li>
									<xsl:value-of select="$LABELS/labels/entry[@key='LABEL_TARGETCLASS']/@label" />
									<xsl:for-each select="targetClass/targetClass">
										<xsl:variable name="TargetClass_Href" select="href"/>
										<xsl:variable name="TargetClass_label" select="label"/>
										
										<a href="{$TargetClass_Href}">
											<xsl:value-of select="$TargetClass_label" />
										</a>
										<xsl:choose>
											<xsl:when test="position() = last()">
												<xsl:text></xsl:text>
											</xsl:when>
											<xsl:when test="position() != last()">
												<xsl:text> | </xsl:text>
											</xsl:when>											
										</xsl:choose>
									</xsl:for-each>
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
									<xsl:value-of select="$LABELS/labels/entry[@key='LABEL_PATTERNS']/@label" />
									<code><xsl:value-of select="pattern"/></code>	
								</li>
							</xsl:if>
							<!-- Example -->
							<xsl:if test="skosExample != ''">
								<li>
									<xsl:value-of select="$LABELS/labels/entry[@key='LABEL_EXAMPLE']/@label"/>
									<code><xsl:value-of select="skosExample"/></code>
								</li>
							</xsl:if>
							<xsl:if test="targetSubjectsOf">
								<li>
									<xsl:value-of select="$LABELS/labels/entry[@key='LABEL_TARGETSUBJECTSOF']/@label" />
									<xsl:value-of select="targetSubjectsOf"/>
								</li>
							</xsl:if>
							<xsl:if test="targetObjectsOf">
								<li>
									<xsl:value-of select="$LABELS/labels/entry[@key='LABEL_TARGETOBJECTSOF']/@label" />
									<xsl:value-of select="targetObjectsOf"/>
								</li>
							</xsl:if>
							<xsl:if test="sparqlTarget">
								<li>					
									<xsl:value-of select="$LABELS/labels/entry[@key='LABEL_TARGETCLASS']/@label" />
									<br/>
									<code>
										<pre>
											<xsl:value-of select="sparqlTarget" />					
										</pre>
									</code>
								</li>
							</xsl:if>
							<xsl:if test="string-length(MessageOfValidate) &gt; 0">
								<li>
									<em>Message:</em><xsl:value-of select="MessageOfValidate"/>
								</li>
							</xsl:if>
						</ul>
					</xsl:if>
					

					<!-- Diagram forEach section (NodeShape) -->
					<xsl:apply-templates select="sectionDiagrams"/>
					
					<!-- Section of add image -->
					<xsl:apply-templates select="depictionsImgs" />
					
					<!-- Properties table -->
					<xsl:apply-templates select="propertyGroups" />		
					
					<!-- Section for Pie Chart -->
					<xsl:apply-templates select="charts" />	
					
					<!-- Section for Contraints descriptions -->
					<xsl:if test="string-length(descriptionSparql) &gt; 0">
						<h4>
							<xsl:value-of select="$LABELS/labels/entry[@key='LABEL_CONSTRAINTS']/@label" />
						</h4>
						<ul class="constraint_list">
							<li><xsl:apply-templates select="descriptionSparql"/></li>
						</ul>
					</xsl:if>
						
				</section>
			</div>
		</div>
		<br/>
	</xsl:template>
	
	<xsl:template match="section/title">
		<h3 id="{../sectionId}" class="sp_section_title_table">
			<xsl:value-of select="." />
		</h3>
	</xsl:template>
	
	<!-- Diagramme for each section  -->
	<xsl:template match="sectionDiagrams">
		<xsl:apply-templates select="sectionDiagram"/>		
	</xsl:template>
	
	<xsl:template match="sectionDiagram">
		<!-- 
		<xsl:if test="displayTitle">
			<h3><xsl:value-of select="displayTitle" /></h3> 
		</xsl:if>
		<xsl:if test="diagramDescription">
			<p><xsl:value-of select="diagramDescription" /></p> 
		</xsl:if>
		 -->
		<xsl:choose>
			<xsl:when test="$MODE = 'PDF'">
				<!--  When outputting PDF, inserts the PNG image -->
				<img src="{pngLink}" style="width:45%;" alt="a diagram representing this application profile" />
			</xsl:when>
			<xsl:otherwise>
				<!-- @disable-output-escaping prints the raw XML string as XML in the 
					document and removes XML-encoding of the characters
				-->
				<div class="sp_section_nodeshape_diagram">
					<div class="sp_section_nodeshape_center">
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
				</div>
			</xsl:otherwise>
		</xsl:choose>			
	</xsl:template>
		
	<xsl:template match="depictions">
		<xsl:apply-templates select="depiction"/>		
	</xsl:template>
	
	<xsl:template match="depiction">
		<xsl:variable name="depiction_src" select="src"/>
		<xsl:variable name="depiction_title" select="title"/>
		<xsl:variable name="depiction_description" select="description"/>
		<figure>
			<a href="{$depiction_src}"><img src="{$depiction_src}" style="width:100%;"/></a>
			<xsl:if test="$depiction_title or $depiction_description">
				<figcaption>
					<xsl:if test="$depiction_title"><em><xsl:value-of select="$depiction_title"/> :</em> </xsl:if><xsl:value-of select="$depiction_description"/>
				</figcaption>
			</xsl:if>
		</figure>
	</xsl:template>
		
	<!-- Property groups -->
	<xsl:template match="propertyGroups">
		<xsl:if test="count(propertyGroup) > 0">
		
			<xsl:variable name="getBgColor">
				<xsl:choose>
					<xsl:when test="propertyGroup/properties/property/backgroundcolor != ''"></xsl:when>
					<xsl:otherwise>table-striped</xsl:otherwise>
				</xsl:choose>
			</xsl:variable>
			
			<xsl:if test="count(propertyGroup/properties/property) &gt; 0">
				<table class="sp_table_propertyshapes {$getBgColor} table-responsive">
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
							<th class="sp_description_column">
								<xsl:value-of select="$LABELS/labels/entry[@key='COLUMN_DESCRIPTION']/@label" />
							</th>		
						</tr>
					</thead>
					<tbody>
						<xsl:apply-templates select="propertyGroup" />
					</tbody>
				</table><!-- end properties table -->
			</xsl:if>
			
			
		</xsl:if>
	</xsl:template>
	
	<xsl:template match="propertyGroup">
		<!-- only display groups if there are more than 1 !! -->
		<xsl:if test="count(../propertyGroup) > 1">
			<tr class="sp_propertyGroup"><td colspan="5">Properties from <a href="{targetClass/href}"><xsl:value-of select="targetClass/label" /></a></td></tr>
		</xsl:if>
		
		<!-- Properties table -->
		<xsl:apply-templates select="properties" />
	</xsl:template>
	
	<!-- Properties -->
	<xsl:template match="properties">
		<xsl:if test="count(property) = 0">
			<tr>
				<td colspan="5"><em><xsl:value-of select="$LABELS/labels/entry[@key='LABEL_NO_PROPERTIES']/@label" /></em></td>
			</tr>
		</xsl:if>
	
		<xsl:apply-templates select="property" />
	</xsl:template>
		
	<xsl:template match="property">
	
		<xsl:variable name="guillemets">"</xsl:variable>	
		<xsl:variable name="Colors">
			<xsl:choose>
				<xsl:when test="string-length(./backgroundcolor)> 1">
					<xsl:value-of select="concat('background-color:',./backgroundcolor)"/>
				</xsl:when>
				<xsl:when test="string-length(./color) > 1">
					<xsl:value-of select="concat('color:',./color)"/>
				</xsl:when>
				<xsl:otherwise>
					''
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
	
		
		<tr style="{$Colors}">
		
			<!-- Property name -->
			<td>
				<xsl:apply-templates select="./label"/>
			</td>
			<!-- Property URI -->
			<!-- Also with the ID, if provided -->
			<td>
				<xsl:if test="sectionId">
					<xsl:attribute name="id"><xsl:value-of select="sectionId" /></xsl:attribute>
				</xsl:if>
				<xsl:apply-templates select="./propertyUri"/>				
			</td>
			<!-- Expected Value -->
			<td>
				<xsl:apply-templates select="./expectedValue"/>				
			</td>
			<!-- Cardinality -->
			<td>
				<div style="width:30px">
					<xsl:apply-templates select="./cardinalite"/>
				</div>								
			</td>
			<!-- Description properties -->
			<td class="sp_table_propertyshapes_col_description">
				<xsl:apply-templates select="./description"/>
			</td>
		</tr>
	</xsl:template>
	
	<!-- Property name -->
	<xsl:template match="property/label">
		<xsl:value-of select="." />		
	</xsl:template>
	<!-- Property URI -->
	<xsl:template match="property/propertyUri">
		<xsl:choose>
			<xsl:when test="href != ''">
				<code>
					<a href="{href}"><xsl:value-of select="label" /></a>							
				</code>
			</xsl:when>
			<xsl:otherwise>
				<code><xsl:value-of select="label" /></code>
			</xsl:otherwise>
		</xsl:choose>		
		<xsl:if test="../labelRole = 'true'">
			&#160;
			<span title="Main human-readable label of the entity (LabelRole)">
				<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 448 512" style="width:18px; vertical-align: middle;"><path class="fa-secondary" opacity=".4" fill="#af0e16" d="M16 80l0 149.5c0 12.7 5.1 24.9 14.1 33.9l176 176c18.7 18.7 49.1 18.7 67.9 0L407.4 305.9c18.7-18.7 18.7-49.1 0-67.9l-176-176c-9-9-21.2-14.1-33.9-14.1L48 48C30.3 48 16 62.3 16 80zm136 64a40 40 0 1 1 -80 0 40 40 0 1 1 80 0z"/><path class="fa-primary" fill="#af0e16" d="M16 229.5c0 12.7 5.1 24.9 14.1 33.9l176 176c18.7 18.7 49.1 18.7 67.9 0L407.4 305.9c18.7-18.7 18.7-49.1 0-67.9l-176-176c-9-9-21.2-14.1-33.9-14.1L48 48C30.3 48 16 62.3 16 80l0 149.5zm-16 0L0 80C0 53.5 21.5 32 48 32l149.5 0c17 0 33.3 6.7 45.3 18.7l176 176c25 25 25 65.5 0 90.5L285.3 450.7c-25 25-65.5 25-90.5 0l-176-176C6.7 262.7 0 246.5 0 229.5zM112 104a40 40 0 1 1 0 80 40 40 0 1 1 0-80zm24 40a24 24 0 1 0 -48 0 24 24 0 1 0 48 0z"/></svg>
			</span>				
		</xsl:if>
		<xsl:if test="../deactivated = 'true'">
			&#160;
			<span title="Deactivated property">
				<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 512 512" style="width:18px; vertical-align: middle;"><path class="fa-secondary" opacity=".4" fill="#808080" d="M48 256c0 114.9 93.1 208 208 208c48.8 0 93.7-16.8 129.1-44.9L92.9 126.9C64.8 162.3 48 207.2 48 256zM126.9 92.9L419.1 385.1C447.2 349.7 464 304.8 464 256c0-114.9-93.1-208-208-208c-48.8 0-93.7 16.8-129.1 44.9z"/><path class="fa-primary" fill="#808080" d="M385.1 419.1L92.9 126.9C64.8 162.3 48 207.2 48 256c0 114.9 93.1 208 208 208c48.8 0 93.7-16.8 129.1-44.9zm33.9-33.9C447.2 349.7 464 304.8 464 256c0-114.9-93.1-208-208-208c-48.8 0-93.7 16.8-129.1 44.9L419.1 385.1zM0 256a256 256 0 1 1 512 0A256 256 0 1 1 0 256z"/></svg>
			</span>				
		</xsl:if>
	</xsl:template>
	<!-- Expected Value -->
	<xsl:template match="property/expectedValue">
		<xsl:choose>
			<xsl:when test="expectedValue[href/text()]">
				<code>
					<a href="{expectedValue/href}"><xsl:value-of select="expectedValue/label" /></a>
				</code>
			</xsl:when>
			<xsl:when test="./ors/or">
				<xsl:for-each select="./ors/or">
					<code>
						<a href="{href}"><xsl:value-of select="label" /></a>
					</code>			
					<xsl:choose>
						<xsl:when test="position() &lt; last()">
							<code> <xsl:value-of select="$LABELS/labels/entry[@key='LABEL_OR']/@label" /> </code>
						</xsl:when>
					</xsl:choose>							
				</xsl:for-each>
			</xsl:when>
			<xsl:when test="expectedValue[label/text()]">
				<code>
					<!-- disable output espacing as we may have <sup> in rendering -->
					<xsl:value-of disable-output-escaping="yes" select="expectedValue/label" />
				</code>
			</xsl:when>
			<xsl:otherwise>
				<!-- Oups, don't know how to handle this -->
			</xsl:otherwise>
		</xsl:choose>
		<br />
		<xsl:if test="inValues/inValue">
			<p>
				<small>
					<xsl:for-each select="inValues/inValue">
						<xsl:choose>
							<xsl:when test="href/text()">
								<a href="{href}"><xsl:value-of select="label" /></a>
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="label" />
							</xsl:otherwise>
						</xsl:choose>
						<xsl:if test="position() != last()">, </xsl:if>
					</xsl:for-each>
				</small>
			</p>
		</xsl:if>
		<xsl:if test="pattern/label">
			
			<small>
			<xsl:choose>
				<xsl:when test="pattern/label">
					<a href="{href}"><xsl:value-of select="pattern/label" /></a>
				</xsl:when>
				<xsl:otherwise>
					<xsl:value-of select="label" />
				</xsl:otherwise>
			</xsl:choose>					
			</small>			
		</xsl:if>
	</xsl:template>
	
	<!-- Cardinality -->
	<xsl:template match="property/cardinalite"><xsl:value-of select="." /></xsl:template>
	<!-- Description properties -->
	<xsl:template match="property/description">
		<xsl:value-of select="." />
	</xsl:template>
	
	<xsl:template match="descriptionSparql">
		<xsl:value-of select="."/>
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
	<!-- Note the #all special keyword to apply this template to all modes -->
	<xsl:template match="*" mode="#all" />
	<xsl:template match="text()" mode="#all"></xsl:template>


</xsl:stylesheet>
