<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<!-- controls output style -->
	<xsl:output indent="yes" method="xml" />

	<xsl:template match="/">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="ShapesDocumentation">
		<html lang="en">
			<br />
			<h1 style="color:DarkRed;text-align:center;">
				<xsl:value-of select="title" />
			</h1>
			<p style="text-align:center;">
				<xsl:value-of select="subtitle" />
			</p>
			<br />
			<br />
			<head>
				<link rel="stylesheet"
					href="https://cdn.jsdelivr.net/npm/bootstrap@4.6.0/dist/css/bootstrap.min.css"
					integrity="sha384-B0vP5xmATw1+K9KRQjQERJvTumQW0nPEzvF6L/Z6nronJ3oUOFUFpCjEUQouq2+l"
					crossorigin="anonymous" />
			</head>
			<body>
				<ul class="nav justify-content-center">
					<div>
						<!-- Table de matiers -->
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
		<div class="container-md">
			<lefth>
				<h1>
					<xsl:value-of select="title" />
				</h1>
				<xsl:if test="comments != ''">
					<p>
						<em>
							<xsl:value-of select="comments" />
						</em>
					</p>
				</xsl:if>

			</lefth>
			<table id="{$TitleNodeSape}" class="table table-striped"
				style="width:100%">
				<thead>
					<tr>
						<xsl:if test="properties/property[output_language = 'en'] ">
							<th>Property Name</th>
							<th>Uri Name</th>
							<th>Expected Value</th>
							<th>Cardinality</th>
							<th>Description</th>
						</xsl:if>
						<xsl:if
							test="properties/property[output_language = 'fr'] or properties/property[output_language = null]">
							<th>Nom de la Propriété</th>
							<th>Nom de l'URI</th>
							<th>Valeur Attendue</th>
							<th>Cardinalité</th>
							<th>Description</th>
						</xsl:if>
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