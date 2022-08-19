<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">


	<!-- controls output style -->
	<xsl:output indent="yes" method="xml" omit-xml-declaration="yes"/>
	
	<!-- Language parameter to the XSLT -->
	<xsl:param name="LANG"/>
	<!-- Param for get diagram -->
	<xsl:param name="diagramforPDF"/>

	<!-- french labels -->
	<xsl:variable name="LABELS_FR">
		<labels>
			<entry key="TOC" label="Table des Matières" />
			<entry key="COLUMN_PROPERTY" label="Nom de la propriété" />
			<entry key="COLUMN_URI" label="URI" />
			<entry key="COLUMN_EXPECTED_VALUE" label="Valeur attendue" />
			<entry key="COLUMN_CARD" label="Card." />
			<entry key="COLUMN_DESCRIPTION" label="Description" />

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
			
			<entry key="METADATA.FORMATS" label="Télécharger les données : " />

			<entry key="DIAGRAM.TITLE_PIC" label="Diagrammes" />

			<entry key="DIAGRAM.TITLE" label="Diagramme du dataset" />
			<entry key="DIAGRAM.HELP"
				label="Cliquez sur le diagramme pour naviguer vers la section correspondante" />
			<entry key="DIAGRAM.VIEW" label="Voit le diagramme comme PNG" />

			<entry key="DESCRIPTION.TITLE" label="Titre de la documentation"/>

			<entry key="LABEL_TARGETCLASS" label="Classe cible : " />
			<entry key="LABEL_NODEKIND" label="Type de noeud : " />
			<entry key="LABEL_PATTERNS" label="URIs : " />
			<entry key="LABEL_CLOSE" label="Shape fermée" />
			<entry key="LABEL_EXAMPLE" label="Exemple : "/>
			<entry key="LABEL_SUPERCLASSES" label="Hérite de : "/>
		</labels>
	</xsl:variable>

	<!-- English labels -->
	<xsl:variable name="LABELS_EN">
		<labels>
			<entry key="TOC" label="Table of Content" />
			<entry key="COLUMN_PROPERTY" label="Property name" />
			<entry key="COLUMN_URI" label="URI" />
			<entry key="COLUMN_EXPECTED_VALUE" label="Expected value" />
			<entry key="COLUMN_CARD" label="Card." />
			<entry key="COLUMN_DESCRIPTION" label="Description" />

			<entry key="PREFIXES.TITLE" label="Namespaces" />
			<entry key="PREFIXES.COLUMN.PREFIX" label="Prefix" />
			<entry key="PREFIXES.COLUMN.URI" label="Namespace" />

			<entry key="METADATA.DATE" label="Last updated: " />
			<entry key="METADATA.VERSION" label="Version: " />
			<entry key="METADATA.INTRODUCTION" label="Abstract" />
			<entry key="METADATA.DATECREATED" label="Created date: " />
			<entry key="METADATA.DATEISSUED" label="Issued date: " />
			<entry key="METADATA.DATECOPYRIGHTED" label="Copyright date: " />
			<entry key="METADATA.LICENSE" label="License: " />
			<entry key="METADATA.CREATOR" label="Creator: " />
			<entry key="METADATA.PUBLISHER" label="Publisher: " />
			<entry key="METADATA.RIGHTHOLDER" label="Rightsholder: " />
			
			<entry key="METADATA.FORMATS" label="Download serialization : " />
			
			<entry key="DIAGRAM.TITLE_PIC" label="Diagrams" />

			<entry key="DIAGRAM.TITLE" label="Dataset diagram" />
			<entry key="DIAGRAM.HELP"
				label="Click diagram to navigate to corresponding section" />
			<entry key="DIAGRAM.VIEW" label="View as PNG" />
			
			<entry key="DESCRIPTION.TITLE" label="Description "/>

			<entry key="LABEL_TARGETCLASS" label="Target Class: " />
			<entry key="LABEL_NODEKIND" label="Nodes: " />
			<entry key="LABEL_PATTERNS" label="URI: " />
			<entry key="LABEL_CLOSE" label="Closed shape" />
			<entry key="LABEL_EXAMPLE" label="Example: "/>
			<entry key="LABEL_SUPERCLASSES" label="Inherits from: "/>
		</labels>
	</xsl:variable>


	<!-- Select labels based on language param -->
	<xsl:variable name="LABELS" select="if($LANG = 'fr') then $LABELS_FR else $LABELS_EN" />


	<!-- Principal -->
	<xsl:template match="/">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="ShapesDocumentation">
		<html lang="{$LANG}">
			<head>
				<meta charset="UTF-8"/>

				<style type="text/css">
					.anchor {
						float: left;
						padding-right: 4px;
						margin-left: -20px;
						line-height: 1;
						padding-top:12px;
					}
					
					span{
						margin: 2px;
					}

					.monospace {
						font-family: SFMono-Regular,Menlo,Monaco,Consolas,"Liberation Mono","Courier New",monospace;
						font-size: 87.5%;
					}
					
					<!-- CSS  -->
					.container{
				         width: calc(100% - 40px);
				         max-width: 1000px;
				         margin-left: auto;
				         margin-right: auto;
				      }
				
				      h2 {
				         margin: 25px 0px 10px 0px;
				      }
				
				      ul{
				         margin: 0.5em
				         margin-bottom: 2rem;
				      }
				      
				      a {
                      color: #007bff;
                      text-decoration: none;
                      background-color: transparent;
	                  }
	
	                  a:-webkit-any-link {
	                      cursor: pointer;                      
	                  }
	                  
				      
				     <!-- Table  -->
				     table {
						    display: table;
						    border-spacing: 0px;
							margin-botton: 1rem;
						}
						
						tr:nth-child(even) {
						    background-color: #eee;
						}
						
						
						.prefixes table {
						    border-collapse: collapse;
						    margin-bottom: 1rem;
						    color: #212529;
						}
						
						
						.prefixes td {
						    padding: 0.25rem;
						    vertical-align: top;
						    border-top: 1px solid #dee2e6;
						}
						
						
						
						.propertyshapes table {
						    border-collapse: collapse;
						}
			
			
						.propertyshapes thead {
						    display: table-header-group;
						    vertical-align: middle;
						    border-color: inherit;
						}
						
						
						.propertyshapes tr {
						    display: table-row;
						    vertical-align: inherit;
						    border-color: inherit;
						}
						
						p {
					      font-size: 0.875em;
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
								 @bottom-center {
								 	content: counter(page);
								 }
						}
						
						<!-- fot the cardinality colum -->
						.propertyshapes th:nth-child(4) {
						    width: 6%;
						}
						
						.propertyshapes td {
						    padding: 0.75rem;
						    vertical-align: top;
						    border-top: 1px solid #dee2e6;
						}
						
						.propertyshapes tbody {
						    display: table-row-group;
						    vertical-align: middle;
						    border-color: inherit;
						}
			
			
						.propertyshapes tbody tr {
						    display: table-row;
						    vertical-align: inherit;
						    border-color: inherit;
						}
						
						.text-break {
						    word-break: break-word;
						}
						
						body {
						         margin: 0;
						         font-family: -apple-system,BlinkMacSystemFont,"Segoe UI",Roboto,"Helvetica Neue",Arial,"Noto Sans","Liberation Sans",sans-serif,"Apple Color Emoji","Segoe UI Emoji","Segoe UI Symbol","Noto Color Emoji";
						         background-color: #fff;
						      }
				     
				     			
					<!-- fin CSS -->
					
				</style>
			</head>
			<body>
				
				<div class="container">
					<br />
					<table style="width:100%">
			            <xsl:choose>
			            	<xsl:when test="string-length(imgLogo) &gt; 0">
			            		<tr>
			            			<td width="20%"><img src="{imgLogo}"/></td>
			            			<td width="80%"><div><center><h1><xsl:value-of select="title" /></h1></center></div></td>		
			            		</tr>	
			            	</xsl:when>
			            	<xsl:otherwise>
			            			<div><center><h1><xsl:value-of select="title" /></h1></center></div>
			            	</xsl:otherwise>
			            </xsl:choose>			            
			         </table>
					<br />
					<br />
					<xsl:apply-templates select="datecreated" />
					<xsl:apply-templates select="dateissued" />
					<xsl:apply-templates select="modifiedDate" />
					<xsl:apply-templates select="yearCopyRighted" />
					<xsl:apply-templates select="versionInfo" />
					<xsl:apply-templates select="licenses" />
					<xsl:apply-templates select="creators" />
					<xsl:apply-templates select="publishers" />
					<xsl:apply-templates select="rightsHolders" />
					<br />
					<!-- section for the formats -->
					<xsl:if test="string-length(formats) &gt; 0">
						<xsl:apply-templates select="formats" />
					</xsl:if>
					<hr />
					<br />
					<xsl:apply-templates select="abstract_" />
					
					<xsl:apply-templates select="." mode="TOC" />
					

					<!--  
					<xsl:apply-templates select="svgDiagram" />
					-->
					<xsl:apply-templates select="svgDiagrams" />
					
					<xsl:apply-templates select="descriptionDocument" />
					<xsl:if test="not(svgDiagrams)">
						<xsl:apply-templates select="diagramOWLs"/>
					</xsl:if>
					<xsl:apply-templates select="prefixes" />
					<xsl:apply-templates select="sections" />
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
		<div>
			<!-- Table de matieres -->
			<h2 id="Index">
				<xsl:value-of select="$LABELS/labels/entry[@key='TOC']/@label" />
			</h2>
			<!-- Diagram -->
			<xsl:if test="svgDiagram">
				<a href="#diagram">
				<xsl:value-of
						select="$LABELS/labels/entry[@key='DIAGRAM.TITLE']/@label" />
				</a>
				<br />
			</xsl:if>
			<!-- Description -->
			<xsl:if test="descriptionDocument">
				<a href="#description">
					<xsl:value-of select="$LABELS/labels/entry[@key='DESCRIPTION.TITLE']/@label"/>
				</a>	
				<br/>
			</xsl:if>						
			<!-- Prefixes -->
			<a href="#prefixes">
				<xsl:value-of
					select="$LABELS/labels/entry[@key='PREFIXES.TITLE']/@label" />
			</a>
			<br />
			<!-- Section -->
			<xsl:for-each select="sections/section">
				<xsl:sort select="title"/>
				<xsl:variable name="TitleNodeSapetab" select="uri" />
				<xsl:variable name="Title" select="title" />
				
				<a href="{concat('#',$TitleNodeSapetab)}">
					<xsl:value-of select="$Title" />
				</a>
				<br />
			</xsl:for-each>
		</div>
		<br />
		<br />
		<xsl:if test="$diagramforPDF != ''">
			<xsl:for-each select="$diagramforPDF">
				<div>
					<h2 id="diagram">
						<xsl:value-of
						select="$LABELS/labels/entry[@key='DIAGRAM.TITLE']/@label" />
					</h2>
					<div>
						<img src="{.}" style="width:100%;"/>
					</div>
				</div>
			</xsl:for-each>
		</xsl:if>
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
	
	<!--  shared template for all values -->
	<xsl:template match="creator | publisher | rightsHolder | license">
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
		<br/>
		<div>
			<xsl:apply-templates/>
		</div>								
	</xsl:template>
	
	<xsl:template match="format">
		<span>
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
		<div>
			<h2 id="abstract">
				<xsl:value-of
					select="$LABELS/labels/entry[@key='METADATA.INTRODUCTION']/@label" />
			</h2>
			<!--  disable output escaping so that HTML is preserved -->
			<xsl:value-of select="." disable-output-escaping="yes" />
			<br />
		</div>
		<br />
	</xsl:template>
	
	<xsl:template match="diagramOWLs">
		<h2 id="DiagramOWL">
			<xsl:value-of select="$LABELS/labels/entry[@key='DIAGRAM.TITLE_PIC']/@label" />
		</h2>
		<xsl:apply-templates/>
		<br />
	</xsl:template>
	
	<xsl:template match="diagramOWL">
		<img src="{.}" style="width:100%;"/>
	</xsl:template>

	<!-- @disable-output-escaping prints the raw XML string as XML in the 
					document and removes XML-encoding of the characters
				
	<xsl:template match="svgDiagram[text() != '']">
		<div>
			<h2 id="Diagram">
				<xsl:value-of
					select="$LABELS/labels/entry[@key='DIAGRAM.TITLE']/@label" />
			</h2>
			<div>
				<xsl:value-of select="." disable-output-escaping="yes" />
			</div>
			<small class="form-text text-muted">
				<xsl:variable name="pngImg" select="../pngDiagram" />
				<xsl:value-of
					select="$LABELS/labels/entry[@key='DIAGRAM.HELP']/@label" />
				<xsl:text> | </xsl:text>
				<a href="{$pngImg}" target="_blank">
					<xsl:value-of
						select="$LABELS/labels/entry[@key='DIAGRAM.VIEW']/@label" />
				</a>
			</small>
			<br />
		</div>
	</xsl:template>
	 -->
	 
	<xsl:template match="svgDiagrams">
		<h2 id="Diagram">
			<xsl:value-of select="$LABELS/labels/entry[@key='DIAGRAM.TITLE']/@label" />
		</h2>
		<div>
			<xsl:choose>
				<xsl:when test="count(./svgDiagramMulti) &gt; 1">
					<xsl:apply-templates mode="Multi"/>					
				</xsl:when>
				<xsl:otherwise>
					<xsl:apply-templates mode="DiagramGral"/>
				</xsl:otherwise>
			</xsl:choose>			
		</div>
	</xsl:template>
	
	<xsl:template match="svgDiagramMulti" mode="Multi">
		<!-- @disable-output-escaping prints the raw XML string as XML in the 
				document and removes XML-encoding of the characters
				[text() != ''] 
		-->
		<div style="border:1px solid green;">
			<center><xsl:value-of select="." disable-output-escaping="yes" /></center>
		</div>
				
	</xsl:template>
	
	<!-- @disable-output-escaping prints the raw XML string as XML in the 
					document and removes XML-encoding of the characters 
					-->
	<xsl:template match="svgDiagramMulti[text() != '']" mode="DiagramGral">
		<div>
			<xsl:value-of select="." disable-output-escaping="yes" />
		</div>
		<small class="form-text text-muted">
			<xsl:variable name="pngImg" select="../../pngDiagram" />
			<xsl:value-of
					select="$LABELS/labels/entry[@key='DIAGRAM.HELP']/@label" />
			<xsl:text> | </xsl:text>
			<a href="{$pngImg}" target="_blank">
				<xsl:value-of select="$LABELS/labels/entry[@key='DIAGRAM.VIEW']/@label" />
			</a>			
		</small>
		
		<xsl:variable name="depictionValues" select="../../diagramOWLs"/>
		
		<xsl:if test="../../diagramOWLs">
			<br/>
			<xsl:for-each select="../../diagramOWLs/diagramOWL">
				<div class="section_Depiction">
					<img src="{.}" style="width:100%;"/>
				</div>
			</xsl:for-each>
		</xsl:if>
		<br/>
	</xsl:template>
	

	<!-- Description Title -->
	<xsl:template match="descriptionDocument[text() != '']">
		<div>
			<h2 id="Description">
				<xsl:value-of select="$LABELS/labels/entry[@key='DESCRIPTION.TITLE']/@label" />
			</h2>
			<!--  disable output escaping so that HTML is preserved -->
			<xsl:value-of select="."  disable-output-escaping="yes"/>			
		</div>
		<br/>
	</xsl:template>
	
	
	<!-- Prefix -->
	<xsl:template match="prefixes">
		<div>
			<h2 id="Prefixes">
				<xsl:value-of
					select="$LABELS/labels/entry[@key='PREFIXES.TITLE']/@label" />
			</h2>
			<table class="prefixes" style="width:60%">
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
		</div>
		<br />
	</xsl:template>

	<xsl:template match="prefixe">
		<tr>
			<td>
				<xsl:value-of select="prefix" />
			</td>
			<td>
				<span class="monospace"><a href="{namespace}" target="_blank"><xsl:value-of select="namespace" /></a></span>
			</td>
		</tr>
	</xsl:template>

	<!-- Sections -->
	<xsl:template match="sections">
		<xsl:apply-templates />
	</xsl:template>
	
	<xsl:template match="section">
		<xsl:variable name="TitleNodeSape" select="uri" />
		<div>
			<h2 id="{$TitleNodeSape}">
				<xsl:value-of select="title" />
			</h2>

			<xsl:if test="description != ''">
				<p>
					<!--  disable output escaping so that HTML is preserved -->
					<em><xsl:value-of select="description" disable-output-escaping="yes" /></em>
				</p>
			</xsl:if>
			<xsl:if
				test="targetClassLabel != '' or superClasses/link or nodeKind != '' or pattern != '' or closed='true' or skosExample != ''">
				<ul>
					<xsl:if test="targetClassLabel != ''">
						<li>
							<xsl:value-of
								select="$LABELS/labels/entry[@key='LABEL_TARGETCLASS']/@label" />
							<a href="{targetClassUri}">
								<xsl:value-of select="targetClassLabel" />
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
					<xsl:if test="closed='true'">
						<li>
							<xsl:value-of
								select="$LABELS/labels/entry[@key='LABEL_CLOSE']/@label" />
						</li>
					</xsl:if>
					<xsl:if test="nodeKind != ''">
						<li>
							<xsl:value-of
								select="$LABELS/labels/entry[@key='LABEL_NODEKIND']/@label" />
							<xsl:value-of select="nodeKind" />
						</li>
					</xsl:if>

					<xsl:if test="pattern != ''">
						<li>
							<xsl:value-of
								select="$LABELS/labels/entry[@key='LABEL_PATTERNS']/@label" />
							<span class="monospace">
								<xsl:value-of select="pattern" />
							</span>
						</li>
					</xsl:if>
					<!-- Example -->
					<xsl:if test="skosExample != ''">
						<li>
							<xsl:value-of select="$LABELS/labels/entry[@key='LABEL_EXAMPLE']/@label"/>
							<span class="monospace">
								<xsl:value-of select="skosExample"/>
							</span>							
						</li>
					</xsl:if>
				</ul>
			</xsl:if>
			<xsl:if test="count(properties/property)>0">
				<table class="propertyshapes" style="width:100%">
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
							<th>
								<xsl:value-of
									select="$LABELS/labels/entry[@key='COLUMN_DESCRIPTION']/@label" />
							</th>
						</tr>
					</thead>
					<tbody>
						<xsl:apply-templates />
					</tbody>
				</table>
			</xsl:if>
		</div>
		<br />
	</xsl:template>

	<!-- Properties -->

	<xsl:template match="properties">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="property">
		<tr>
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
								<a href="{propertyUri/href}"><xsl:value-of select="propertyUri/label" /></a>							
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
					<xsl:when test="linkNodeShape != ''">
						<code>
							<a href="{concat('#',linkNodeShapeUri)}">
								<xsl:value-of select="linkNodeShape" />
							</a>
						</code>
					</xsl:when>
					<xsl:otherwise>
						<xsl:choose>
							<xsl:when test="string-length(or) > 0">
								<xsl:variable name="nfois"
									select="count(tokenize(or,','))" />
								<xsl:for-each select="tokenize(or,',')">
									<xsl:variable name="countData">
										<xsl:choose>
											<xsl:when test="position() = 1">
												<xsl:value-of select="count(.)" />
											</xsl:when>
											<xsl:otherwise>
												<xsl:value-of select="count(.)+1" />
											</xsl:otherwise>
										</xsl:choose>
									</xsl:variable>
									<xsl:variable name="sDataOrg" select="." />
										<code>
											<a href="{concat('#',$sDataOrg)}">
												<xsl:value-of select="concat($sDataOrg,' ')" />
											</a>
										</code>
									<xsl:choose>
										<xsl:when test="$nfois &gt; $countData">
											<code>
												<xsl:text>or</xsl:text>
											</code>
										</xsl:when>
									</xsl:choose>
								</xsl:for-each>
							</xsl:when>
							<xsl:otherwise>
								<code>
									<!-- disable output espacing as we may have <sup> in rendering -->
									<xsl:value-of disable-output-escaping="yes" select="expectedValueLabel" />
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
			<!-- Description properties -->
			<td class="text-break">
				<p>
					<xsl:value-of select="description" />
				</p>
			</td>
		</tr>
	</xsl:template>

	<!-- don't print what was not matched -->
	<xsl:template match="text()"></xsl:template>


</xsl:stylesheet>