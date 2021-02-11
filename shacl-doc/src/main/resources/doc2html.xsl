<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- controls output style -->
	<xsl:output indent="yes" method="xml" />

	<!-- Language parameter to the XSLT -->
	<xsl:param name="LANG">en</xsl:param>

	<!--  french labels -->
	<xsl:variable name="LABELS_FR">
		<labels>
			<entry key="TOC" label="Table des Matières" />
			<entry key="COLUMN_PROPERTY" label="Nom de la propriété" />
			<entry key="COLUMN_URI" label="URI" />
			<entry key="COLUMN_EXPECTED_VALUE" label="Valeur attendue" />
			<entry key="COLUMN_CARD" label="Card." />
			<entry key="COLUMN_DESCRIPTION" label="Description" />
		</labels>
	</xsl:variable>
	
	<!-- English labels -->
	<xsl:variable name="LABELS_EN">
		<labels>
			<entry key="TOC" label="Table des Matières" />
			<entry key="COLUMN_PROPERTY" label="Property name" />
			<entry key="COLUMN_URI" label="URI" />
			<entry key="COLUMN_EXPECTED_VALUE" label="Expected value" />
			<entry key="COLUMN_CARD" label="Card." />
			<entry key="COLUMN_DESCRIPTION" label="Description" />
		</labels>
	</xsl:variable>
	
	<!-- Select labels based on language param -->
	<xsl:variable name="LABELS" select="if($LANG = 'fr') then $LABELS_FR else $LABELS_EN" />

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
					<br />
					<ul class="nav justify-content-left">
						<div>
							<!-- Table de matieres -->
							<h2><xsl:value-of select="$LABELS/labels/entry[@key='TOC']/@label" /></h2>
							<xsl:for-each select="sections/section">
								<xsl:variable name="TitleNodeSapetab" select="dURI" />
								<xsl:variable name="Title" select="title" />
								<a href="{concat('#',$TitleNodeSapetab)}">
									<xsl:value-of select="$Title" />
								</a>
								<br />
							</xsl:for-each>
						</div>
					</ul>
				</div>
				<br />
				<br />
				<xsl:apply-templates />
			</body>
		</html>
	</xsl:template>

	<!-- <xsl:template match="shnamespaces"> <div class="container-md"> <table 
		id="TNamespace" class="table table-striped" style="width:100%"> <thead> <tr> 
		<th>Prefix</th> <th>NameSpace</th> </tr> </thead> <tbody> <xsl:apply-templates 
		select="shnamespace" /> </tbody> </table> </div> </xsl:template> <xsl:template 
		match="shnamespace"> <tr> <td> <xsl:value-of select="output_prefix" /> </td> 
		<td> <xsl:value-of select="output_namespace" /> </td> </tr> </xsl:template> -->

	<xsl:template match="sections">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="section">
		<xsl:variable name="TitleNodeSape" select="dURI" />
		<div class="container-md" id="{$TitleNodeSape}">
			<lefth>
				<h2>
					<xsl:value-of select="title" />
				</h2>
				<xsl:if test="comments != ''">
					<p>
						<em>
							<xsl:value-of select="comments" />
						</em>
					</p>
				</xsl:if>

			</lefth>
			<table class="table table-striped"
				style="width:100%">
				<thead>
					<tr>
							<th><xsl:value-of select="$LABELS/labels/entry[@key='COLUMN_PROPERTY']/@label" /> </th>
							<th><xsl:value-of select="$LABELS/labels/entry[@key='COLUMN_URI']/@label" /></th>
							<th><xsl:value-of select="$LABELS/labels/entry[@key='COLUMN_EXPECTED_VALUE']/@label" /></th>
							<th><xsl:value-of select="$LABELS/labels/entry[@key='COLUMN_CARD']/@label" /></th>
							<th><xsl:value-of select="$LABELS/labels/entry[@key='COLUMN_DESCRIPTION']/@label" /></th>
					</tr>
				</thead>
				<tbody>
					<xsl:apply-templates />
				</tbody>
			</table>
		</div>
		<br />
	</xsl:template>

	<xsl:template match="properties">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="property">
		<tr>
			<td>
				<xsl:value-of select="output_propriete" />
			</td>
			<td class="text-break">
				<xsl:if test="output_uri != null or output_uri != ''">
					<code>
						<xsl:value-of select="output_uri" />
					</code>
				</xsl:if>
			</td>
			<td>
				<xsl:value-of select="output_valeur_attendus" />
				<br />
				<p class="text-break">
					<small>
						<xsl:value-of select="output_patterns" />
					</small>
				</p>
				<xsl:if test="output_shin != null or output_shin != ''">
					<p class="text-break">
						<small>
							<xsl:value-of select="concat('(',output_shin,')')" />
						</small>
					</p>
				</xsl:if>
			</td>
			<td>
				<center />
				<xsl:value-of select="output_Cardinalite" />
			</td>
			<td class="text-break">
				<xsl:value-of select="output_description" />
			</td>
		</tr>
	</xsl:template>

	<!-- fin de format -->
	<xsl:template match="text()"></xsl:template>


</xsl:stylesheet>