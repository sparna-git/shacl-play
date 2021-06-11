<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- controls output style -->
	<xsl:output indent="yes" method="xml" />

	<!-- Language parameter to the XSLT -->
	<xsl:param name="LANG">
		en
	</xsl:param>

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

			<entry key="METADATA.DATE" label="Dernière modification :" />
			<entry key="METADATA.VERSION" label="Version : " />
			<entry key="METADATA.INTRODUCTION" label="Introduction" />

			<entry key="DIAGRAM.TITLE" label="Diagramme du dataset" />
			<entry key="DIAGRAM.HELP"
				label="Cliquez sur le diagramme pour naviguer vers la section correspondante" />
			<entry key="DIAGRAM.VIEW" label="Voit le diagramme comme PNG" />
				

			<entry key="LABEL_TARGETCLASS" label="Classe de Target: " />
			<entry key="LABEL_NODEKIND" label="Types de noeud : " />
			<entry key="LABEL_PATTERNS" label="URIs : " />
			<entry key="LABEL_CLOSE" label="Shape fermée" />
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

			<entry key="DIAGRAM.TITLE" label="Dataset diagram" />
			<entry key="DIAGRAM.HELP"
				label="Click diagram to navigate to corresponding section" />
			<entry key="DIAGRAM.VIEW" label="View as PNG" />	

			<entry key="LABEL_TARGETCLASS" label="Target Class: " />
			<entry key="LABEL_NODEKIND" label="Nodes: " />
			<entry key="LABEL_PATTERNS" label="URI: " />
			<entry key="LABEL_CLOSE" label="Closed shape" />
		</labels>
	</xsl:variable>


	<!-- Select labels based on language param -->
	<xsl:variable name="LABELS"
		select="if($LANG = 'fr') then $LABELS_FR else $LABELS_EN" />


	<!-- Principal -->

	<xsl:template match="/">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="ShapesDocumentation">
		<html lang="en">
			<head>
				<link rel="stylesheet"
					href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.0/dist/css/bootstrap.min.css"
					integrity="sha384-B0vP5xmATw1+K9KRQjQERJvTumQW0nPEzvF6L/Z6nronJ3oUOFUFpCjEUQouq2+l"
					crossorigin="anonymous" />

				<style type="text/css">
					.anchor {
					float: left;
					padding-right: 4px;
					margin-left: -20px;
					line-height: 1;
					padding-top:12px;
					}

					.monospace {
					font-family: SFMono-Regular,Menlo,Monaco,Consolas,"Liberation
					Mono","Courier New",monospace;
					font-size: 87.5%;
					}
				</style>
			</head>
			<body>
				 <div class="container-md">
				
					<br />
					<h1>
						<center>
							<xsl:value-of select="title" />
						</center>
					</h1>
					<br />
					<xsl:if test="modifiedDate != ''">
						<b>
							<xsl:value-of
								select="$LABELS/labels/entry[@key='METADATA.DATE']/@label" />
						</b>
						<xsl:value-of select="modifiedDate" />
						<br />
					</xsl:if>
					<xsl:if test="versionInfo !=''">
						<b>
							<xsl:value-of
								select="$LABELS/labels/entry[@key='METADATA.VERSION']/@label" />
						</b>
						<xsl:value-of select="versionInfo" />
						<br />
					</xsl:if>
					<br />
					<hr />
					<br />
					<xsl:if test="comment != ''">
						<h2>
							<xsl:value-of
								select="$LABELS/labels/entry[@key='METADATA.INTRODUCTION']/@label" />
						</h2>
						<xsl:value-of select="comment" />
						<br />
					</xsl:if>
					<br />
					

					<div>
						<!-- Table de matieres -->
						<h2>
							<xsl:value-of
								select="$LABELS/labels/entry[@key='TOC']/@label" />
						</h2>
						<xsl:if test="svgDiagram != ''">
							<a href="#diagram">
								<xsl:value-of
									select="$LABELS/labels/entry[@key='DIAGRAM.TITLE']/@label" />
							</a>
							<br />
						</xsl:if>
						<a href="#prefixes">
							<xsl:value-of
								select="$LABELS/labels/entry[@key='PREFIXES.TITLE']/@label" />
						</a>
						<br />
						<xsl:for-each select="sections/section">
							<xsl:if test="count(properties/property)>0">
								<xsl:variable name="TitleNodeSapetab" select="dURI" />
								<xsl:variable name="Title" select="title" />
								<a href="{concat('#',$TitleNodeSapetab)}">
									<xsl:value-of select="$Title" />
								</a>
								<br />
							</xsl:if>
						</xsl:for-each>
					</div>
					<br />
					<xsl:apply-templates />
				</div>
			</body>
		</html>
	</xsl:template>

	<xsl:template match="svgDiagram">
		<xsl:if test=". != ''">
			<div id="diagram">
				<h2>
					<xsl:value-of
						select="$LABELS/labels/entry[@key='DIAGRAM.TITLE']/@label" />
				</h2>
				<div>
					<!-- @disable-output-escaping prints the raw XML string as XML in the 
						document and removes XML-encoding of the characters -->					
					<xsl:value-of select="." disable-output-escaping="yes" />						 
				</div>
				<small class="form-text text-muted">
					<xsl:variable name="pngImg" select="../pngDiagram"/>
					<xsl:value-of
						select="$LABELS/labels/entry[@key='DIAGRAM.HELP']/@label" />
					<xsl:text> | </xsl:text>					
				  	<a href="{$pngImg}" target="_blank">
						<xsl:value-of select="$LABELS/labels/entry[@key='DIAGRAM.VIEW']/@label"/>
					</a>
				</small>
				<br/>									
			</div>
		</xsl:if>
	</xsl:template>
	
	
	
	<xsl:template match="prefixes">
		<div id="prefixes">
			<h2>
				<xsl:value-of
					select="$LABELS/labels/entry[@key='PREFIXES.TITLE']/@label" />
			</h2>
			<table class="table table-striped table-sm" style="width:60%">
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
				<xsl:value-of select="namespace" />
			</td>
		</tr>
	</xsl:template>

	<xsl:template match="sections">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="section">
		<xsl:variable name="TitleNodeSape" select="uri" />
		<div id="{$TitleNodeSape}">
			<h2>
				<xsl:value-of select="title" />
			</h2>

			<xsl:if test="description != ''">
				<em>
					<xsl:value-of select="description" />
				</em>
			</xsl:if>
			<xsl:if
				test="targetClassLabel != '' or nodeKind != '' or pattern != '' or (closed != '' and closed='true')">
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
					<xsl:if test="closeNS != '' and closed='true'">
						<li>
							<xsl:value-of
								select="$LABELS/labels/entry[@key='LABEL_CLOSE']/@label" />
						</li>
					</xsl:if>
				</ul>
			</xsl:if>
			<xsl:if test="count(properties/property)>0">
				<table class="table table-striped" style="width:100%">
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

	<xsl:template match="properties">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="property">
		<tr>
			<td>
				<xsl:value-of select="label" />
			</td>
			<td class="text-break">
				<xsl:if test="shortForm != null or shortForm != ''">
					<code>
						<a href="{shortFormUri}">
							<xsl:value-of select="shortForm" />
						</a>
					</code>
				</xsl:if>
			</td>
			<td>
				<xsl:choose>
					<xsl:when test="linknameNodeShape != ''">
						<a href="{concat('#',linknameNodeShapeuri)}">
							<!-- <xsl:value-of select="output_valeur_attendus" /> -->
							<xsl:value-of select="linknameNodeShape" />
						</a>
					</xsl:when>
					<xsl:otherwise>
						<xsl:choose>
							<xsl:when test="linkNodeShape != ''">
								<a href="{concat('#',linkNodeShapeUri)}">
									<xsl:value-of select="linkNodeShape" />
								</a>
							</xsl:when>
							<xsl:otherwise>
								<xsl:choose>
									<xsl:when test="string-length(or) > 0">
										<xsl:variable name="nfois" select="count(tokenize(or,','))"/>
										<xsl:for-each select="tokenize(or,',')">
											<xsl:variable name="countData">
												<xsl:choose>
													<xsl:when test="position() = 1">
														<xsl:value-of select="count(.)"/>													
													</xsl:when>
													<xsl:otherwise>
														<xsl:value-of select="count(.)+1"/>
													</xsl:otherwise>
												</xsl:choose>												
											</xsl:variable>											
											<xsl:variable name="sDataOrg" select="." />
											<a href="{concat('#',$sDataOrg)}">
												<xsl:value-of select="concat($sDataOrg,' ')"/>												
											</a>											
											<xsl:choose>
												<xsl:when test="$nfois &gt; $countData">
													<xsl:text>or </xsl:text>
												</xsl:when>												
											</xsl:choose>											
										</xsl:for-each>										
									</xsl:when>
									<xsl:otherwise>
										<span class="monospace">
											<xsl:value-of select="expectedValueLabel" />
										</span>
									</xsl:otherwise>
								</xsl:choose>
							</xsl:otherwise>
						</xsl:choose>
					</xsl:otherwise>
				</xsl:choose>

				<br />
				<xsl:if test="expectedValueAdditionnalInfoPattern/text()">
					<p class="text-break">
						<small>
							<xsl:value-of
								select="expectedValueAdditionnalInfoPattern" />
						</small>
					</p>
				</xsl:if>
				<xsl:if test="expectedValueAdditionnalInfoIn/text()">
					<p>
						<small>
							<xsl:value-of
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
			<td>
				<xsl:value-of select="cardinalite" />
			</td>
			<td>
				<xsl:value-of select="description" />
			</td>
		</tr>
	</xsl:template>

	<!-- don't print what was not matched -->
	<xsl:template match="text()"></xsl:template>


</xsl:stylesheet>