<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
	
	<!-- Language parameter to the XSLT -->
	<xsl:param name="LANG"/>

	<xsl:param name="quota">"</xsl:param>
	<xsl:param name="comma">,</xsl:param>
	
	<!-- Indicates if we are producing the HTML for an HTML output of for a PDF conversion -->
	<xsl:param name="MODE">HTML</xsl:param>

		<!-- french labels -->
	<!-- this "BASE" variable can be overwritten by other stylesheets -->
	<xsl:variable name="LABELS_FR_BASE">
		<labels>
			<entry key="TOC" label="Table des Matières" />
			// Entities Shapes
			<entry key="TOC_MAIN" label="Entités principales" />
			<entry key="TOC_SUPPORTIVE" label="Entités secondaires" />

			<entry key="COLUMN_PROPERTY" label="Nom de la propriété" />
			<entry key="COLUMN_URI" label="URI" />
			<entry key="COLUMN_EXPECTED_VALUE" label="Valeur attendue" />
			<entry key="COLUMN_CARD" label="Card." />
			<entry key="COLUMN_EXAMPLE" label="Exemple"/>
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
			<entry key="LABEL_SHNODE" label="Respecte : " />
			<entry key="LABEL_TARGETCLASS" label="S'applique à : " />
			<entry key="LABEL_NODEKIND" label="Type de noeud : " />
			<entry key="LABEL_PATTERNS" label="Structure d'identifiant : " />
			<entry key="LABEL_CLOSE" label="Shape fermée" />
			<entry key="LABEL_CLOSE_DESCRIPTION" label="&#9888; La description de cette entité est fixe, aucune propriété autre que celles listées dans le tableau ci-dessous n'est autorisée." />
			<entry key="LABEL_EXAMPLE" label="Exemple : "/>
			<entry key="LABEL_SUPERCLASSES" label="Hérite de : "/>
			<entry key="LABEL_OR" label=" ou "/>
			<entry key="LABEL_TARGETSUBJECTSOF" label="S'applique aux sujets de: "/>
			<entry key="LABEL_TARGETOBJECTSOF" label="S'applique aux objets de: "/>
			
			<entry key="LABEL_NO_PROPERTIES" label="Aucune propriété spécifique"/>
			
			<entry key="LABEL_CONSTRAINTS" label="Règles additionnelles"/>

			<!-- Respec Sections -->
			<entry key="SECTION_DESCRIPTION.TITLE" label="Description" />
			<entry key="SECTION.DIAGRAM.TITLE" label="Diagramme" />
			<entry key="SECTION.PROPERTY.TITLE" label="Propriétés" />
			

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
			<entry key="TOC_MAIN" label="Main Entities" />
			<entry key="TOC_SUPPORTIVE" label="Supportive Entities" />

			<entry key="COLUMN_PROPERTY" label="Property name" />
			<entry key="COLUMN_URI" label="URI" />
			<entry key="COLUMN_EXPECTED_VALUE" label="Expected value" />
			<entry key="COLUMN_CARD" label="Card." />
			<entry key="COLUMN_EXAMPLE" label="Example"/>
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
			<entry key="LABEL_SHNODE" label="Conforms to: " />
			<entry key="LABEL_TARGETCLASS" label="Applies to: " />
			<entry key="LABEL_NODEKIND" label="Nodes: " />
			<entry key="LABEL_PATTERNS" label="URI pattern: " />
			<entry key="LABEL_CLOSE" label="Closed shape" />
			<entry key="LABEL_CLOSE_DESCRIPTION" label="&#9888; No other properties than the ones listed in the table below are allowed." />
			<entry key="LABEL_EXAMPLE" label="Example: "/>
			<entry key="LABEL_SUPERCLASSES" label="Inherits from: "/>
			<entry key="LABEL_OR" label=" or "/>
			<entry key="LABEL_TARGETSUBJECTSOF" label="Applies to subjects of: "/>
			<entry key="LABEL_TARGETOBJECTSOF" label="Applies to objects of: "/>
			
			<entry key="LABEL_NO_PROPERTIES" label="No specific properties"/>
			
			<entry key="LABEL_CONSTRAINTS" label="Additionnal constraints"/>

			<!-- Respec Sections -->
			<entry key="SECTION_DESCRIPTION.TITLE" label="Description" />
			<entry key="SECTION.DIAGRAM.TITLE" label="Diagram" />
			<entry key="SECTION.PROPERTY.TITLE" label="Properties" />
			
		</labels>
	</xsl:variable>
	<!-- In this stylesheet we just copy the base labels -->
	<xsl:variable name="LABELS_EN">
		<xsl:copy-of select="$LABELS_EN_BASE" />
	</xsl:variable>
	<!-- Select labels based on language param -->
	<xsl:variable name="LABELS" select="if($LANG = 'fr') then $LABELS_FR else $LABELS_EN" />

	<xsl:param name="COLSPAN" select="4" />
	
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
		<html lang="{$LANG}">
			<head>

				<!-- Call style css -->
				<style>	
					<xsl:apply-templates select="../ShapesDocumentation" mode="style_css_doc"/>
					<xsl:apply-templates select="../ShapesDocumentation" mode="style_css_extra"/>
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

				<xsl:if test="jsonldOWL">
					<script type="application/ld+json">
						<xsl:apply-templates select="jsonldOWL" />
					</script>
				</xsl:if>
				
				<xsl:apply-templates select="../ShapesDocumentation" mode="javascript_extra_header"/>

				<!--  RESPEC Documentation  -->
				<script src="https://www.w3.org/Tools/respec/respec-w3c" class="remove" defer="true"></script>
    			<script class="remove">
					// Turtle
					async function loadTurtleLang() {
						//this is the function you call in 'preProcess', to load the highlighter
						const worker = await new Promise(resolve =&gt; {
							require(["core/worker"], ({ worker }) =&gt; resolve(worker));
							}
						);
						const action = "highlight-load-lang";
						const langURL = "https://cdn.jsdelivr.net/gh/redmer/highlightjs-turtle/src/languages/turtle.js";
						const propName = "hljsDefineTurtle"; // This funtion is defined in the highlighter being loaded
						const lang = "turtle"; // this is the class you use to identify the language
						
						worker.postMessage({ action, langURL, propName, lang });
						return new Promise(resolve =&gt; {
							worker.addEventListener("message", function listener({ data }) {
								const { action: responseAction, lang: responseLang } = data;
								if (responseAction === action &amp;&amp; responseLang === lang) {
									worker.removeEventListener("message", listener);
									resolve();
								}
							});
						});
    				}
					// Sparql
					async function loadSparqlLang() {
						//this is the function you call in 'preProcess', to load the highlighter
						const worker = await new Promise(resolve =&gt; {
								require(["core/worker"], ({ worker }) =&gt; resolve(worker));
							}
						);
						const action = "highlight-load-lang";
						const langURL = "https://cdn.jsdelivr.net/gh/redmer/highlightjs-sparql/src/languages/sparql.js";
						const propName = "hljsDefineSparql"; // This funtion is defined in the highlighter being loaded
						const lang = "sparql"; // this is the class you use to identify the language
						worker.postMessage({ action, langURL, propName, lang });
					
						return new Promise(resolve =&gt; {
							worker.addEventListener("message", function listener({ data }) {
								const { action: responseAction, lang: responseLang } = data;
								if (responseAction === action &amp;&amp; responseLang === lang) {
									worker.removeEventListener("message", listener);
									resolve();
								}
							});
						});
    				}
					
					// All config options at https://respec.org/docs/
					var respecConfig = {
						preProcess: [loadTurtleLang,loadSparqlLang],
						specStatus: "base",
						latestVersion: null,
						maxTocLevel: 2,
						license: "w3c-software-doc",
						<xsl:apply-templates select="dateissued" />
						<xsl:apply-templates select="modifiedDate" />
						<xsl:apply-templates select="feedbacks" />
						// this section is custom
						<xsl:choose>
							<xsl:when test="publishers/publisher">
								<xsl:apply-templates select="publishers" />								
							</xsl:when>
							<xsl:otherwise>
								editors: [{ name: " "}],
							</xsl:otherwise>
						</xsl:choose>
						// Logo
						<xsl:if test="string-length(imgLogo) &gt; 0">
		            		logos: [
			            		{
			            			src: "<xsl:value-of select="imgLogo"/>",
			            			alt: " ",
									height: 70
			            		}
		            		],							
		            	</xsl:if>
						otherLinks: [
							<xsl:apply-templates select="datecreated" />
							<xsl:apply-templates select="versionInfo" />
							<xsl:apply-templates select="creators" />
							<xsl:apply-templates select="feedbacks" />
							<xsl:apply-templates select="OWLimports" />							
						]				
					};
				</script>

				
			</head>
			<body>

				<p class="copyright">
					<xsl:if test="yearCopyRighted">
						© <xsl:apply-templates select="yearCopyRighted" />
					</xsl:if>
					
					<xsl:apply-templates select="rightsHolders" />
				</p>

				<xsl:apply-templates select="formats" />

				<xsl:apply-templates select="abstract_" />
				<xsl:apply-templates select="prefixes" />
				<xsl:if test="diagrams/diagram or depictions/depiction">						
					<section>
						<h2>
							<xsl:value-of select="$LABELS/labels/entry[@key='DIAGRAM.TITLE']/@label" />
						</h2>
						<div class="sp_section_row mt-3">
							<div class="sp_section_col">							
							<xsl:if test="depictions/depiction">
								<xsl:apply-templates select="depictions"/>
							</xsl:if>
							<xsl:if test="diagrams/diagram">
								<xsl:apply-templates select="diagrams" />
							</xsl:if>							
							</div>
						</div>	
					</section>					
				</xsl:if>				
				<xsl:apply-templates select="descriptionDocument" />
				<!-- Section for each node shape-->
				<xsl:apply-templates select="sections" />
				<!--  release notes at the end -->
				<xsl:apply-templates select="releaseNotes" />


				<!-- Anchor for the document -->
				<script src="https://cdn.jsdelivr.net/npm/anchor-js/anchor.min.js">//</script>
    			<script>				
					anchors.options = {
	                    icon: '#'
	                  };
               		anchors.options.placement = 'left';
					//anchors.add('h2,h3');
					// for links inside tables		
					anchors.add('td[id]');
				</script>
				<!-- Script for handling SVG elements -->
				<script>
					document.addEventListener('DOMContentLoaded', function() {
						document.querySelectorAll('svg').forEach(svg =&gt; {
							const div = svg.parentElement;
							if ( svg.parentElement.class = 'sp_section_nodeshape_center') {
								const rectCount = svg.querySelectorAll('rect').length;
								if (rectCount &gt;= 6) {
									div.className = 'overlarge';
								}
							}
						});
					});					
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
		
		/* Pour la colonne Description */
		td,
		th {
			white-space: normal;
			word-break: break-word;
		}
		/* Limite la largeur de la colonne Description */
		td.description,
		th.description {
			max-width: 300px;
		}
		/* Pour éviter le scroll horizontal du tableau */
		table {
			/*width: 100%;*/
			max-width: 1100px;
			table-layout: fixed;
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

		.sp_space_sectoin {
			margin-top: 50px;
		}

		/* div wrapping section title and URI below - same as a paragraph margin */
		.sp_section_title_table_wrapper {
			margin-top: -15px;
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
		
		.table-striped tbody:nth-child(even) {
			background-color: #eee;
		}

		.sp_hidden_line {
			border-block-start-style: hidden;
		}

		.sp_hidden_line td {
			padding: 0px;
		}

		/*  */
		.table-striped-prefix tr:nth-child(even) {
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

		.sp_syntax {
            border-left-style: solid;
            border-left-width: .5em;
            border-color: #d0d0d0;
            margin-bottom: 16px;
            padding: .5em 1em;
            background-color: #f6f6f6;
        }

		.sp_target {
            border-left-style: solid;
            border-left-width: .5em;
            border-color: #d0d0d0;
            margin-bottom: 16px;
            padding: .5em 1em;
            background-color: #f6f6f6;
        }

		.sp_def-header {
			color: #a0a0a0;
			font-size: 16px;
			padding-bottom: 8px;
		}

		
		/* Cacher toutes les ancres par défaut */
		.anchors-doc a {
			display: none;
		}

      	/* Afficher seulement l'ancre du tr survolé */
		tr:hover .anchors-doc a {
			position: absolute;
			margin-left: -13.25px;
			padding-left: 0.25px;
			padding-right: 0.25px;
			display: inline;
			opacity: 0.6;
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
					margin-right: 20px;
					
					/*
					width: auto;
					max-width: 1100px;
					margin-left: 300px;
					margin-right: 120px;
					*/
				}
				
				.pt-4 {
					padding-top: 2.5rem !important;
				}
			</xsl:when>
			<xsl:otherwise>
				/*
				.container {
					width: calc(100% - 40px);
					max-width: 1100px;
					margin-left: 300px;
					margin-right: auto;
				}
				.container {width: calc(100% - 500px);}
				*/
				/*
				.container {
					width: auto;
					max-width: 1100px;
					margin-left: 300px;
					margin-right: 120px;
				}
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
				*/			
			</xsl:otherwise>
		</xsl:choose>
		
	</xsl:template>
	
	<!-- JSON OWL Identifier -->
	<xsl:template match="jsonldOWL">
		<xsl:value-of select="."/>		
	</xsl:template>

	<!--
		Section of header information	
	-->

	<!-- Create Date custom -->
	<xsl:template match="datecreated">
		{
			key: <xsl:value-of select="concat($quota,$LABELS/labels/entry[@key='METADATA.DATECREATED']/@label,$quota,$comma)" />
			data: [
				{
					value: <xsl:value-of select="concat($quota,.,$quota,$comma)"/>
          			href: ""
				}
			]
		}<xsl:value-of select="$comma"/>
	</xsl:template>

	<xsl:template match="dateissued">
		publishDate: <xsl:value-of select="concat($quota,.,$quota,$comma)"/>
	</xsl:template>

	<xsl:template match="modifiedDate">
		modificationDate: <xsl:value-of select="concat($quota,.,$quota,$comma)"/>
	</xsl:template>

	<!-- Copyright -->
	<xsl:template match="yearCopyRighted">
		<xsl:value-of select="." />		
	</xsl:template>

	<xsl:template match="rightsHolders[rightsHolder]">
		<xsl:apply-templates />		
	</xsl:template>

	<xsl:template match="rightsHolder">
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

	<!-- Version custom -->
	<xsl:template match="versionInfo">
		{
			key: <xsl:value-of select="concat($quota,$LABELS/labels/entry[@key='METADATA.VERSION']/@label,$quota,$comma)" />
			data: [
				{
					value: <xsl:value-of select="concat($quota,.,$quota,$comma)"/>
          			href: ""
				}
			]
		}<xsl:value-of select="$comma"/>
	</xsl:template>

	<xsl:template match="creators[creator]">
		{
			key: <xsl:value-of select="concat($quota,$LABELS/labels/entry[@key='METADATA.CREATOR']/@label,$quota,$comma)" />
			data: [
				<xsl:apply-templates />		
			]
		}<xsl:value-of select="$comma"/>		
	</xsl:template>

	<xsl:template match="feedbacks[feedback]">
		<xsl:choose>
			<xsl:when test="contains(./feedback/href,'github')">
				github: <xsl:value-of select="./feedback/href"/>
			</xsl:when>
			<xsl:otherwise>
				{
					key: <xsl:value-of select="concat($quota,$LABELS/labels/entry[@key='METADATA.FEEDBACK']/@label,$quota,$comma)" />
					data: [
						<xsl:apply-templates />		
					]
				}<xsl:value-of select="$comma"/>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template match="OWLimports[OWLimport]">
		{
			key: <xsl:value-of select="concat($quota,$LABELS/labels/entry[@key='METADATA.IMPORTS']/@label,$quota,$comma)" />
			data: [
				<xsl:apply-templates />		
			]
		}<xsl:value-of select="$comma"/>
	</xsl:template>

	<xsl:template match="publishers[publisher]">
		editors: [
			<xsl:apply-templates />		
		]<xsl:value-of select="$comma"/>
	</xsl:template>

	<xsl:template match="publisher">
		{
			<xsl:choose>
				<xsl:when test="href">
					name: <xsl:value-of select="concat($quota,label,$quota,$comma)"/>
					url: <xsl:value-of select="concat($quota,href,$quota,$comma)"/>
				</xsl:when>
				<xsl:otherwise>
					name: <xsl:value-of select="concat($quota,label,$quota,$comma)"/>
					href: ""
				</xsl:otherwise>
			</xsl:choose>
		}<xsl:value-of select="$comma"/>
		<!-- if we have following sibling nodes, add a comma -->
		<xsl:if test="following-sibling::*">, </xsl:if>
	</xsl:template>

	<xsl:template match="creator | OWLimport | feedback ">
		{
			<xsl:choose>
				<xsl:when test="href">
					value: <xsl:value-of select="concat($quota,label,$quota,$comma)"/>
					href: <xsl:value-of select="concat($quota,href,$quota,$comma)"/>
				</xsl:when>
				<xsl:otherwise>
					value: <xsl:value-of select="concat($quota,label,$quota,$comma)"/>
					href: ""
				</xsl:otherwise>
			</xsl:choose>
		}<xsl:value-of select="$comma"/>
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
		<section class="Abstract">
			<h2><xsl:value-of select="$LABELS/labels/entry[@key='METADATA.INTRODUCTION']/@label" /></h2>
			<div class="row mt-3">
				<div class="col">
					<!--  disable output escaping so that HTML is preserved -->
					<xsl:value-of select="." disable-output-escaping="yes" />			
				</div>
			</div>
		</section>
	</xsl:template>

	<!--
		Section of prefix
	-->
	<xsl:template match="prefixes">
		<!-- this section is necessary if the shacl file cannot a descriptio or abstract section, then show the toc respec -->
		<section id="abstract" class="notoc remove">
		</section>


		<section id="prefixes">
			<h2>
				<xsl:value-of select="$LABELS/labels/entry[@key='PREFIXES.TITLE']/@label" />
			</h2>
			<table class="sp_table_prefixes table table-striped-prefix table-responsive">
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

	<!-- 
		Section of Diagram 	
	-->
	<xsl:template match="diagrams">
		<xsl:apply-templates />	
	</xsl:template>
	
	<xsl:template match="diagram">
		<section id="{displayTitle}">
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
		</section>
	</xsl:template>

	<!-- Description Title -->
	<xsl:template match="descriptionDocument[text() != '']">
		<section id="description_document">
			<h2><xsl:value-of select="$LABELS/labels/entry[@key='DESCRIPTION.TITLE']/@label" /></h2>
			<!--  disable output escaping so that HTML is preserved -->
			<xsl:value-of select="."  disable-output-escaping="yes"/>			
		</section>
	</xsl:template>

	<!-- 
		NodeShapes 
	-->
	<xsl:template match="sections">

		<!-- Show in TOC all nodeshape main principal -->
		<xsl:if test="section/mainToc='true'">
			<section class="sp_space_sectoin">
				<h2 id="main" class="sp_section_subtitle">
					<xsl:value-of select="$LABELS/labels/entry[@key='TOC_MAIN']/@label" />
				</h2>
				<xsl:apply-templates select="section[mainToc='true']" />
			</section>
		</xsl:if>	
		<!-- Show in TOC all nodeshape supportive entities -->
		<xsl:if test="section/mainToc='false'">
			<section class="sp_space_sectoin">
				<h2 id="supportive" class="sp_section_subtitle">
					<xsl:value-of select="$LABELS/labels/entry[@key='TOC_SUPPORTIVE']/@label" />
				</h2>
				<xsl:apply-templates select="section[not(mainToc='true')]" />
			</section>
		</xsl:if>
	</xsl:template>

	<!-- Subsection -->
	<xsl:template match="section">

		<section id="{sectionId}" style="margin-top: -30px;">

			<h3><xsl:apply-templates select="title" /></h3>
			<div class="sp_section_title_table_wrapper">
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

			<!-- div definition -->
			<xsl:if test="description != ''">
				<!--  disable output escaping so that HTML is preserved -->
				<div class="def">
					<div class="sp_def-header"><xsl:value-of select="$LABELS/labels/entry[@key='SECTION_DESCRIPTION.TITLE']/@label" /></div>
					<xsl:value-of select="description" disable-output-escaping="yes" />
				</div>
			</xsl:if>

			<!-- div targets -->
			<xsl:if test="
							targetClass/href
							or
							superClasses/link
							or
							targetSubjectsOf != ''
							or
							targetObjectsOf != ''
							or
							sparqlTarget							
						"
					>
				<div class="sp_target">
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
									<pre class="sparql">
										<xsl:value-of select="sparqlTarget" />					
									</pre>
								</code>
							</li>
						</xsl:if>
						<!--
						<xsl:if test="string-length(MessageOfValidate) &gt; 0">
							<li>
								<em>Message:</em><xsl:value-of select="MessageOfValidate"/>
							</li>
						</xsl:if>
						-->
					</ul>
				</div>
			</xsl:if>

			<!-- div value : nodeKind / pattern / examples -->
			<xsl:if test="
							nodeKind != ''
							or
							pattern != ''
							or
							skosExample != ''
							or
							shNode/href
						"
			>
				<div class="sp_syntax">
					<ul>
						<xsl:if test="nodeKind != ''">
							<li>
								<xsl:value-of
									select="$LABELS/labels/entry[@key='LABEL_NODEKIND']/@label" />
								<xsl:value-of select="nodeKind" />
							</li>
						</xsl:if>
						<xsl:if test="pattern != ''">
							<li>
								<xsl:value-of select="$LABELS/labels/entry[@key='LABEL_PATTERNS']/@label" />
								<code><xsl:value-of select="pattern"/></code>	
							</li>
						</xsl:if>
						<xsl:if test="skosExample != ''">
							<li>
								<xsl:value-of select="$LABELS/labels/entry[@key='LABEL_EXAMPLE']/@label"/>
								<code><xsl:value-of select="skosExample"/></code>
							</li>
						</xsl:if>
						<xsl:if test="shNode/href">
							<li>
								<xsl:value-of select="$LABELS/labels/entry[@key='LABEL_SHNODE']/@label" />
								<xsl:for-each select="shNode">
									<xsl:variable name="shNode_Href" select="href"/>
									<xsl:variable name="shNode_label" select="label"/>
									
									<a href="{$shNode_Href}">
										<xsl:value-of select="$shNode_label" />
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
					</ul>
				</div>
			</xsl:if>

			<!-- diagram -->
			<xsl:if test="sectionDiagrams != '' or depictions !=''">
				<xsl:variable name="section_diagram" select="concat('diagram-',sectionId)"/>
				<section id="{$section_diagram}">
					<h4><xsl:value-of select="$LABELS/labels/entry[@key='SECTION.DIAGRAM.TITLE']/@label" /></h4>

					<!-- depictions given as URL to an image -->
					<xsl:apply-templates select="depictions" />

					<!-- PlantUML diagram forEach section (NodeShape) -->
					<xsl:apply-templates select="sectionDiagrams"/>

				</section>
			</xsl:if>
			
			<xsl:if test="
				count(propertyGroups/propertyGroup) > 0
				or
				charts
			">
				<xsl:variable name="section_properties" select="concat('properties-',sectionId)"/>
				<section id="{$section_properties}">
					<h4><xsl:value-of select="$LABELS/labels/entry[@key='SECTION.PROPERTY.TITLE']/@label" /></h4>

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

			</xsl:if>
		</section>		
	</xsl:template>
	
	<xsl:template match="section/title">
		<xsl:value-of select="." />
	</xsl:template>

	<!-- Diagram for each section  -->
	<xsl:template match="sectionDiagrams">
		<xsl:apply-templates select="sectionDiagram"/>		
	</xsl:template>
	
	<xsl:template match="sectionDiagram">
	
		<xsl:choose>
			<xsl:when test="$MODE = 'PDF'">
				<!--  When outputting PDF, inserts the PNG image -->
				<img src="{pngLink}" style="width:45%;" alt="a UML diagram describing this entity" />
			</xsl:when>
			<xsl:otherwise>
				<!-- @disable-output-escaping prints the raw XML string as XML in the 
					document and removes XML-encoding of the characters
				-->
				<div>
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
	
	<!-- add a <figure> elements with the href read from "src", along with a title and a description -->
	<xsl:template match="depiction">
		<xsl:variable name="depiction_src" select="src"/>
		<xsl:variable name="depiction_title" select="title"/>
		<xsl:variable name="depiction_description" select="description"/>

		<xsl:variable name="title_section">
			<xsl:choose>
				<xsl:when test="$depiction_title or $depiction_description">
					<xsl:if test="$depiction_title"><em><xsl:value-of select="$depiction_title"/> :</em> </xsl:if><xsl:value-of select="$depiction_description"/>
				</xsl:when>
				<xsl:otherwise>
					
				</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<xsl:if test="$title_section != null or string-length($title_section) &gt; 0">
			<h3><xsl:value-of select="$title_section"/></h3>
		</xsl:if>

		<figcaption>
			<a href="{$depiction_src}"><img src="{$depiction_src}" style="width:100%;"/></a>
		</figcaption>
		
	</xsl:template>
		
	<!-- Property groups -->
	<xsl:template match="propertyGroups">

		<!-- Show message if the nodeshape is closed -->
		<xsl:if test="../closed='true'">
			<div class="advisement">
				<em><xsl:value-of select="$LABELS/labels/entry[@key='LABEL_CLOSE_DESCRIPTION']/@label" /></em>
			</div>
		</xsl:if>
	
		<xsl:variable name="getBgColor">
			<xsl:choose>
				<xsl:when test="propertyGroup/properties/property/backgroundcolor != ''"></xsl:when>
				<xsl:otherwise>table-striped</xsl:otherwise>
			</xsl:choose>
		</xsl:variable>
		
		<!-- Always 4 columns : label, URI, expected value, cardinalities -->
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
				</tr>
			</thead>
			<xsl:apply-templates select="propertyGroup" />					
		</table><!-- end properties table -->

	</xsl:template>
	
	<xsl:template match="propertyGroup">
		<!-- only display groups if there are more than 1 !! -->
		<xsl:if test="count(../propertyGroup) > 1">
			<tr class="sp_propertyGroup"><td colspan="{$COLSPAN}">Properties from <a href="{targetClass/href}"><xsl:value-of select="targetClass/label" /></a></td></tr>
		</xsl:if>

		<!-- Properties table -->
		<xsl:apply-templates select="properties" />
	</xsl:template>
	
	<!-- Properties -->
	<xsl:template match="properties">
		<!-- no properties, display a message -->
		<xsl:if test="count(property) = 0">
			<tr>
				<td colspan="{$COLSPAN}"><em><xsl:value-of select="$LABELS/labels/entry[@key='LABEL_NO_PROPERTIES']/@label" /></em></td>
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
		<tbody>
			<tr style="{$Colors}">			
				<!-- Property name -->
				<td>
					<xsl:apply-templates select="./label"/>
				</td>
				<!-- Property URI -->
				<!-- Also with the ID, if provided -->
				
				<xsl:choose>
					<xsl:when test="sectionId">
						<td style="position: relative; width: 100px; overflow: hidden;" onmouseover="this.style.overflow='';" onmouseout="this.style.overflow='hidden';">
							<div>
								<div class="anchors-doc">
									<a href="#{sectionId}" id="{sectionId}">#</a>									
								</div>
								<xsl:apply-templates select="./propertyUri"/>
							</div>
						</td>
					</xsl:when>
					<xsl:otherwise>
						<td>
							<xsl:apply-templates select="./propertyUri"/>
						</td>
					</xsl:otherwise>
				</xsl:choose>

					<!--
					<xsl:if test="sectionId">
						<xsl:attribute name="id"><xsl:value-of select="sectionId" /></xsl:attribute>
						< ! - - <a id="{sectionId}" href="#{sectionId}">#</a> - - >
					</xsl:if>	
					<xsl:apply-templates select="./propertyUri"/>
					-->
				
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
			</tr>
			<xsl:if test="(string-length(./description) &gt; 0) or (count(./examples) &gt; 0)" >
				<tr class="sp_hidden_line">
					<td colspan="4">
						<div style="padding-left: 3.5em; margin-top: -15px; font-size:smaller;">
							<!-- Display Description -->
							<xsl:if test="string-length(./description) &gt; 0">
								<div>
									&#10137; <xsl:apply-templates select="./description"/>
								</div>							
							</xsl:if>
							<!-- Display Example -->
							<xsl:if test="count(./examples) &gt; 0">	
								<div>
									&#10137; <xsl:value-of select="concat($LABELS/labels/entry[@key='COLUMN_EXAMPLE']/@label,': ')"/>
									<xsl:apply-templates select="./examples"/>
								</div>							
							</xsl:if>
						</div>						
					</td>			
				</tr>
			</xsl:if>
		</tbody>
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
					<a href="{href}"><xsl:value-of select="label"/></a>							
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
		
		<xsl:if test="inValues/inValue">
			<br />
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
		</xsl:if>
		<xsl:if test="pattern">	
			<br />
			<small><xsl:value-of select="pattern" /></small>			
		</xsl:if>
	</xsl:template>
	
	<!-- cardinalities -->
	<xsl:template match="property/cardinalite"><xsl:value-of select="." /></xsl:template>
	
	<!-- skos:example -->
	<xsl:template match="property/examples">
		<xsl:value-of select="."/>			
	</xsl:template>
	
	<!-- description -->
	<xsl:template match="property/description">
		<xsl:value-of select="." />
	</xsl:template>
	
	<!-- SPARQL query -->
	<xsl:template match="descriptionSparql">
		<xsl:value-of select="."/>
	</xsl:template>
	
	<!-- Release notes at the end  -->
	<xsl:template match="releaseNotes[text() != '']">
		<section id="releaseNotes">
			<h2><xsl:value-of select="$LABELS/labels/entry[@key='RELEASE_NOTES.TITLE']/@label" /></h2>
			<!--  disable output escaping so that HTML is preserved -->
			<xsl:value-of select="."  disable-output-escaping="yes"/>			
		</section>
	</xsl:template>

	<!-- don't print what was not matched -->
	<!-- Note the #all special keyword to apply this template to all modes -->
	<xsl:template match="*" mode="#all" />
	<xsl:template match="text()" mode="#all"></xsl:template>

</xsl:stylesheet>
