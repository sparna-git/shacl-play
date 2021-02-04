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
			<h2 style="color:DarkRed;text-align:center;">
				<xsl:value-of select="title" />
			</h2>
			<br />
			<br />
			<head>
				<style>
					table {
					font-family: arial, sans-serif;
					border-collapse:collapse;
					margin-left: auto;
               		margin-right: auto;
					width: 80%;
					}
					th {
					text-align:center;
					color: DarkRed;
					}
					td {
					text-align:left;
					}
				</style>
				<link
					href="https://cdn.jsdelivr.net/npm/bootstrap@5.0.0-beta1/dist/css/bootstrap.rtl.min.css"
					rel="stylesheet"
					integrity="sha384-giJF6kkoqNQ00vy+HMDP7azOuL0xtbfIcaT9wjKHr8RbDVddVHyTfAAsrekwKmP1"
					crossorigin="anonymous" />
			</head>
			<xsl:apply-templates />
		</html>
	</xsl:template>

	<xsl:template match="sections">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="section">
		<center>
			<h2>
				<xsl:value-of select="title" />
			</h2>
			<xsl:if test="comments != ''">
				<p>
					<b>Comments: </b>
					<em>
						<xsl:value-of select="comments" />
					</em>
				</p>
			</xsl:if>
			<xsl:if test="labels != ''">
				<p>
					<b>Labels: </b>
					<em>
						<xsl:value-of select="labels" />
					</em>
				</p>
			</xsl:if>
		</center>
		<table border="1" cellpadding="10" class="container table-light">
			<tr class="row row-cols-5">
				<th class="col">Property Name</th>
				<th class="col">Uri Name</th>
				<th class="col">Expected Value</th>
				<th class="col">Cardinality</th>
				<th class="col">Description</th>
			</tr>
			<xsl:apply-templates />
		</table>
		<br />
	</xsl:template>

	<xsl:template match="properties">
		<xsl:apply-templates />
	</xsl:template>

	<xsl:template match="property">
		<tr class="row row-cols-5">
			<td class="col text-break">
				<xsl:value-of select="output_propriete" />
			</td>
			<td class="col text-break">
				<xsl:value-of select="output_uri" />
			</td>
			<td class="col text-break">
				<xsl:value-of select="output_valeur_attendus" />
				<br />
				<p>
					<small>
						<xsl:value-of select="output_patterns" />
					</small>
				</p>
			</td>
			<td class="col text-break">
				<center />
				<xsl:value-of select="output_Cardinalite" />
			</td>
			<td class="col text-break">
				<xsl:value-of select="output_description" />
			</td>
		</tr>
	</xsl:template>

	<!-- fin de format -->
	<xsl:template match="text()"></xsl:template>


</xsl:stylesheet>