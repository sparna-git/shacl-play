<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c"%>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn"%>

<!-- setup the locale for the messages based on the language in the session -->
<fmt:setLocale value="${sessionScope['fr.sparna.rdf.shacl.shaclplay.SessionData'].userLocale.language}"/>
<fmt:setBundle basename="fr.sparna.rdf.shacl.shaclplay.i18n.shaclplay"/>

<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8">
	<title><fmt:message key="window.app" /></title>
	<link rel="canonical" href="https://shacl-play.sparna.fr/play" />
	
    <!-- Font Awesome -->
    <link rel="stylesheet" href="<c:url value="/resources/fa/css/all.min.css" />">
    
	<link rel="stylesheet" href="<c:url value="/resources/MDB-Free/css/bootstrap.min.css" />">
	<link rel="stylesheet" href="<c:url value="/resources/MDB-Free/css/mdb.min.css" />">
	<link rel="stylesheet" href="<c:url value="/resources/css/shacl-play.css" />">
</head>
<body>
	<jsp:include page="include/navbar.jsp">
		<jsp:param name="active" value="ontology"/>
	</jsp:include>

   	<div class="main-wrapper">
		<div class="container">
			<div class="row">
				<section about="https://shacl-play.sparna.fr/ontology">
					<h1 property="rdfs:label" lang="en">SHACL Play ontology</h1>
					<meta property="rdf:type" resource="owl:Ontology" />
					<div class="container">
						<div class="row">
							<div class="col-md"><span><strong>IRI:</strong> <code>https://shacl-play.sparna.fr/ontology</code></span></div>
						</div>
						<div class="row">
							<div class="col-md-2">Documentation:</div>
							<div class="col-md-10">
								<ul>
									<li><strong>Label:</strong> <span property="rdfs:label" lang="en">SHACL Play ontology</span><sup>@en</sup></li>
									<li>
										<strong>Description:</strong> <span property="rdfs:comment" lang="en">The SHACL Play ontology extends SHACL with custom properties to 1/ annotate shapes graphs 2/ extend the content of a validation report 3/ express detailled statistics of a dataset linked to a shapes graph.</span>
									</li>
									<li>
										<strong>Note:</strong> <span property="skos:note" lang="en">the machine-readable description of the ontology is encoded using RDFa annotations inside this page. To retrieve it, parse the content of this page with an RDFa parser.</span>
									</li>										
								</ul>
							</div>
						</div>
						<div class="row">
							<div class="col-md-2">Specification:</div>
							<div class="col-md-10">
								<ul>
									<li><strong>Imports:</strong> <code><a property="owl:imports" href="http://www.w3.org/ns/shacl">shacl</a></code></li>
								</ul>

							</div>	
						</div>
					</div>

					<div property="rdfs:comment" lang="en"></div>
				</section>
			</div>
			<div class="row">
				<section>
					<section style="margin-bottom:2em;">
						<h3>Classes</h3>
						<div><em>No classes are defined in this ontology.</em></div>
					</section>
					<section style="margin-bottom:2em;">
						<h3>Properties</h3>

						<div class="property blockquote bq-success" about="https://shacl-play.sparna.fr/ontology#background-color" id="background-color">
							<h4>background-color&nbsp;<sup class="type-dp" title="data property" property="rdf:type" resource="owl:DatatypeProperty">(datatype property)</sup></h4>
							<div class="container">
								<div class="row">
									<div class="col-md"><span><strong>IRI:</strong> <code>https://shacl-play.sparna.fr/ontology#background-color</code></span></div>
								</div>
								<div class="row">
									<div class="col-md-2">Documentation:</div>
									<div class="col-md-10">
										<ul>
											<li><strong>Label:</strong> <span property="rdfs:label" lang="en">background color</span><sup>@en</sup></li>
											<li>
												<strong>Definition:</strong> <span property="rdfs:comment" lang="en">Indicates the background color of a resource.</span>
											</li>											
											<li>
												<strong>Usage note:</strong> <span property="skos:scopeNote" lang="en">Can be used on property shapes to color lines in the table or node shapes to color boxes in the diagrams.</span>
											</li>
											<li><strong>See also:</strong> <a property="rdfs:seeAlso" href="https://shacl-play.sparna.fr/ontology#color">shacl-play:color</a></li>
										</ul>
									</div>
								</div>
								<div class="row">
									<div class="col-md-2">Specification:</div>
									<div class="col-md-10">
										<ul>
											<li><strong>Domain:</strong> <em>none</em></li>
											<li><strong>Range:</strong> <code><a property="rdfs:range" href="http://www.w3.org/2001/XMLSchema#string">xsd:string</a></code></li>
											<li><strong>Subproperty of:</strong> <code><a property="owl:equivalentProperty" href="http://data.sparna.fr/ontologies/volipi#color">volipi:color</a></code></li>
										</ul>

									</div>	
								</div>
							</div>
						</div>

						<div class="property blockquote bq-success" about="https://shacl-play.sparna.fr/ontology#color" id="color">
							<h4>color&nbsp;<sup class="type-dp" title="data property" property="rdf:type" resource="owl:DatatypeProperty">(datatype property)</sup></h4>
							<div class="container">
								<div class="row">
									<div class="col-md"><span><strong>IRI:</strong> <code>https://shacl-play.sparna.fr/ontology#color</code></span></div>
								</div>
								<div class="row">
									<div class="col-md-2">Documentation:</div>
									<div class="col-md-10">
										<ul>
											<li><strong>Label:</strong> <span property="rdfs:label" lang="en">color</span><sup>@en</sup></li>
											<li>
												<strong>Definition:</strong> <span property="rdfs:comment" lang="en">Indicates the color of a resource.</span>
											</li>											
											<li>
												<strong>Usage note:</strong> <span property="skos:scopeNote" lang="en">Can be used to indicate the text color to be used to display a resource.</span>
											</li>
											<li><strong>See also:</strong> <a property="rdfs:seeAlso" href="https://shacl-play.sparna.fr/ontology#background-color">shacl-play:background-color</a></li>
										</ul>
									</div>
								</div>
								<div class="row">
									<div class="col-md-2">Specification:</div>
									<div class="col-md-10">
										<ul>
											<li><strong>Domain:</strong> <em>none</em></li>
											<li><strong>Range:</strong> <code><a property="rdfs:range" href="http://www.w3.org/2001/XMLSchema#string">xsd:string</a></code></li>
											<li><strong>Subproperty of:</strong> <code><a property="owl:subPropertyOf" href="http://data.sparna.fr/ontologies/volipi#color">volipi:color</a></code></li>
										</ul>

									</div>	
								</div>
							</div>
						</div>

						<div class="property blockquote bq-success" about="https://shacl-play.sparna.fr/ontology#embed" id="embed">
							<h4>embed&nbsp;<sup class="type-op" title="object property" property="rdf:type" resource="owl:ObjectProperty">(object property)</sup></h4>
							<div class="container">
								<div class="row">
									<div class="col-md"><span><strong>IRI:</strong> <code>https://shacl-play.sparna.fr/ontology#embed</code></span></div>
								</div>
								<div class="row">
									<div class="col-md-2">Documentation:</div>
									<div class="col-md-10">
										<ul>
											<li><strong>Label:</strong> <span property="rdfs:label" lang="en">embed</span><sup>@en</sup></li>
											<li>
												<strong>Definition:</strong> <span property="rdfs:comment" lang="en">Indicates the embedding policy that a property shape should follow, that is whether in hierarchical output the value of the corresponding path of the property should be hierarchically embedded below.</span>
											</li>											
											<li>
												<strong>Usage note:</strong> <span property="skos:scopeNote" lang="en">This is used in JSON schema generation. Embedding is assumed by default, unless the value of this property is <a property="rdfs:seeAlso" href="https://shacl-play.sparna.fr/ontology#EmbedNever">shacl-play:EmbedNever</a>.</span>
											</li>
											<li><strong>See also:</strong> <a property="rdfs:seeAlso" href="https://shacl-play.sparna.fr/ontology#EmbedNever">shacl-play:EmbedNever</a> and <a  property="rdfs:seeAlso" href="https://www.w3.org/TR/json-ld11-framing/#object-embed-flag">JSON-LD framing @embed</a></li>
										</ul>
									</div>
								</div>
								<div class="row">
									<div class="col-md-2">Specification:</div>
									<div class="col-md-10">
										<ul>
											<li><strong>Domain:</strong> <code><a property="rdfs:domain" href="http://www.w3.org/ns/shacl#PropertyShape">sh:PropertyShape</a></code></li>
											<li><strong>Range:</strong> <em>none</em> but the only supported value is <code><a property="rdfs:seeAlso" href="https://shacl-play.sparna.fr/ontology#EmbedNever">shacl-play:EmbedNever</a></code></li>
											<li><strong>Subproperty of:</strong> <em>none</em></li>
										</ul>

									</div>	
								</div>
							</div>
						</div>


						<div class="property blockquote bq-success" about="https://shacl-play.sparna.fr/ontology#hasFocusNode" id="hasFocusNode">
							<h4>hasFocusNode&nbsp;<sup class="type-op-or-dp" title="data property" property="rdf:type" resource="rdf:Property">(object or datatype property)</sup></h4>
							<div class="container">
								<div class="row">
									<div class="col-md"><span><strong>IRI:</strong> <code>https://shacl-play.sparna.fr/ontology#hasFocusNode</code></span></div>
								</div>
								<div class="row">
									<div class="col-md-2">Documentation:</div>
									<div class="col-md-10">
										<ul>
											<li><strong>Label:</strong> <span property="rdfs:label" lang="en">has focus node</span><sup>@en</sup></li>
											<li>
												<strong>Definition:</strong> <span property="rdfs:comment" lang="en">Links a SHACL node shape to a focus node it is targeting.</span>
											</li>											
											<li>
												<strong>Usage note:</strong> <span property="skos:scopeNote" lang="en">This is computed by SHACL Play by resolving the shape targets.</span>
											</li>
										</ul>
									</div>
								</div>
								<div class="row">
									<div class="col-md-2">Specification:</div>
									<div class="col-md-10">
										<ul>
											<li><strong>Domain:</strong> <code><a property="rdfs:domain" href="http://www.w3.org/ns/shacl#NodeShape">sh:NodeShape</a></code></li>
											<li><strong>Range:</strong> <em>none</em> (can be either IRI or Literals depending on the shape target specification)</li>
										</ul>

									</div>	
								</div>
							</div>
						</div>


						<div class="property blockquote bq-success" about="https://shacl-play.sparna.fr/ontology#hasMatched" id="hasMatched">
							<h4>hasMatched&nbsp;<sup class="type-dp" title="data property" property="rdf:type" resource="owl:DatatypeProperty">(datatype property)</sup></h4>
							<div class="container">
								<div class="row">
									<div class="col-md"><span><strong>IRI:</strong> <code>https://shacl-play.sparna.fr/ontology#hasMatched</code></span></div>
								</div>
								<div class="row">
									<div class="col-md-2">Documentation:</div>
									<div class="col-md-10">
										<ul>
											<li><strong>Label:</strong> <span property="rdfs:label" lang="en">has matched</span><sup>@en</sup></li>
											<li>
												<strong>Definition:</strong> <span property="rdfs:comment" lang="en">Boolean flag on the validation report itself indicating if at least one shape matched a focus node.</span>
											</li>											
											<li>
												<strong>Usage note:</strong> <span property="skos:scopeNote" lang="en">This is added by SHACL Play in the validation report.</span>
											</li>
										</ul>
									</div>
								</div>
								<div class="row">
									<div class="col-md-2">Specification:</div>
									<div class="col-md-10">
										<ul>
											<li><strong>Domain:</strong> <code><a property="rdfs:domain" href="http://www.w3.org/ns/shacl#ValidationReport">sh:ValidationReport</a></code></li>
											<li><strong>Range:</strong> <code><a property="rdfs:range" href="http://www.w3.org/2001/XMLSchema#boolean">xsd:boolean</a></code></li>
										</ul>

									</div>	
								</div>
							</div>
						</div>

						<div class="property blockquote bq-success" about="https://shacl-play.sparna.fr/ontology#isRootOf" id="isRootOf">
							<h4>isRootOf&nbsp;<sup class="type-dp" title="object property" property="rdf:type" resource="owl:DatatypeProperty">(object property)</sup></h4>
							<div class="container">
								<div class="row">
									<div class="col-md"><span><strong>IRI:</strong> <code>https://shacl-play.sparna.fr/ontology#isRootOf</code></span></div>
								</div>
								<div class="row">
									<div class="col-md-2">Documentation:</div>
									<div class="col-md-10">
										<ul>
											<li><strong>Label:</strong> <span property="rdfs:label" lang="en">is root of</span><sup>@en</sup></li>
											<li>
												<strong>Definition:</strong> <span property="rdfs:comment" lang="en">Indicates that a Node Shape is a root node shape of a Shapes graph, that is if it should be considered as a top-level entry point, compared to other node shapes.</span>
											</li>											
											<li>
												<strong>Usage note:</strong> <span property="skos:scopeNote" lang="en">This is used to separate sections between "main entities" and "supportive entities" in the documentation, and to help flagging the root node shapes when generating JSON schema. By default, a Node Shape is considered a root shape if it has a target. This property can be used to override this default behavior.</span>
											</li>
											<li><strong>See also:</strong> <a property="rdfs:seeAlso" href="https://hanami.app/ontology#isRoot">hanami:isRoot</a></li>
										</ul>
									</div>
								</div>
								<div class="row">
									<div class="col-md-2">Specification:</div>
									<div class="col-md-10">
										<ul>
											<li><strong>Domain:</strong> <code><a property="rdfs:domain" href="http://www.w3.org/ns/shacl#NodeShape">sh:NodeShape</a></code></li>
											<li><strong>Range:</strong> <code><a property="rdfs:range" href="http://www.w3.org/ns/shacl#ShapesGraph">sh:ShapesGraph</a></code></li>
											<li><strong>Subproperty of:</strong> <code><a property="owl:subPropertyOf" href="http://www.w3.org/2000/01/rdf-schema#isDefinedBy">rdfs:isDefinedBy</a></code></li>
										</ul>

									</div>	
								</div>
							</div>
						</div>


						<div class="property blockquote bq-success" about="https://shacl-play.sparna.fr/ontology#shortname" id="shortname">
							<h4>shortname&nbsp;<sup class="type-dp" title="data property" property="rdf:type" resource="owl:DatatypeProperty">(datatype property)</sup></h4>
							<div class="container">
								<div class="row">
									<div class="col-md"><span><strong>IRI:</strong> <code>https://shacl-play.sparna.fr/ontology#shortname</code></span></div>
								</div>
								<div class="row">
									<div class="col-md-2">Documentation:</div>
									<div class="col-md-10">
										<ul>
											<li><strong>Label:</strong> <span property="rdfs:label" lang="en">short name</span><sup>@en</sup></li>
											<li>
												<strong>Definition:</strong> <span property="rdfs:comment" lang="en">Indicates the key or technical identifier corresponding to a resource. This is deprecated in favor of sh:codeIdentifier.</span>
											</li>											
											<li>
												<strong>Usage note:</strong> <span property="skos:scopeNote" lang="en">To be used to indicate the JSON key to use in JSON context generation</span>
											</li>
										</ul>
									</div>
								</div>
								<div class="row">
									<div class="col-md-2">Specification:</div>
									<div class="col-md-10">
										<ul>
											<li><strong>This property is <code property="owl:deprecated" datatype="xsd:boolean" content="true">deprecated</code></strong></li>
											<li><strong>Domain:</strong> <em>none</em></li>
											<li><strong>Range:</strong> <code><a property="rdfs:range" href="http://www.w3.org/2001/XMLSchema#string">xsd:string</a></code></li>
											<li><strong>Equivalent property:</strong> <code><a property="owl:equivalentProperty" href="http://www.w3.org/ns/shacl#codeIdentifier">sh:codeIdentifier</a></code></li>
										</ul>

									</div>	
								</div>
							</div>
						</div>


						<div class="property blockquote bq-success" about="https://shacl-play.sparna.fr/ontology#targetMatched" id="targetMatched">
							<h4>targetMatched&nbsp;<sup class="type-dp" title="data property" property="rdf:type" resource="owl:DatatypeProperty">(datatype property)</sup></h4>
							<div class="container">
								<div class="row">
									<div class="col-md"><span><strong>IRI:</strong> <code>https://shacl-play.sparna.fr/ontology#targetMatched</code></span></div>
								</div>
								<div class="row">
									<div class="col-md-2">Documentation:</div>
									<div class="col-md-10">
										<ul>
											<li><strong>Label:</strong> <span property="rdfs:label" lang="en">target matched</span><sup>@en</sup></li>
											<li>
												<strong>Definition:</strong> <span property="rdfs:comment" lang="en">Boolean flag on a SHACL shape indicating if it matched at least focus node after validation.</span>
											</li>											
											<li>
												<strong>Usage note:</strong> <span property="skos:scopeNote" lang="en">This is added by SHACL Play in the validation report.</span>
											</li>
										</ul>
									</div>
								</div>
								<div class="row">
									<div class="col-md-2">Specification:</div>
									<div class="col-md-10">
										<ul>
											<li><strong>Domain:</strong> <code><a property="rdfs:domain" href="http://www.w3.org/ns/shacl#NodeShape">sh:NodeShape</a></code></li>
											<li><strong>Range:</strong> <code><a property="rdfs:range" href="http://www.w3.org/2001/XMLSchema#boolean">xsd:boolean</a></code></li>
										</ul>

									</div>	
								</div>
							</div>
						</div>


						<div class="property blockquote bq-success" about="https://shacl-play.sparna.fr/ontology#value" id="value">
							<h4>value&nbsp;<sup class="type-op-or-dp" title="object data property" property="rdf:type" resource="rdf:Property">(object or datatype)</sup></h4>
							<div class="container">
								<div class="row">
									<div class="col-md"><span><strong>IRI:</strong> <code>https://shacl-play.sparna.fr/ontology#value</code></span></div>
								</div>
								<div class="row">
									<div class="col-md-2">Documentation:</div>
									<div class="col-md-10">
										<ul>
											<li><strong>Label:</strong> <span property="rdfs:label" lang="en">value</span><sup>@en</sup></li>
											<li>
												<strong>Definition:</strong> <span property="rdfs:comment" lang="en">Specifies the value for which this value partition is defined.</span>
											</li>											
											<li>
												<strong>Usage note:</strong> <span property="skos:scopeNote" lang="en">This is used to further subdivide property partitions based on specific values.</span>
											</li>
											<li><strong>See also:</strong> <a property="rdfs:seeAlso" href="https://shacl-play.sparna.fr/ontology#valuePartition">shacl-play:valuePartition</a>, <a href="http://rdfs.org/ns/void#propertyPartition">void:propertyPartition</a></li>
										</ul>

									</div>
								</div>
								<div class="row">
									<div class="col-md-2">Specification:</div>
									<div class="col-md-10">
										<ul>
											<li><strong>Domain:</strong> <code><a property="rdfs:domain" href="http://rdfs.org/ns/void#Dataset">void:Dataset</a></code></li>
											<li><strong>Range:</strong> <em>none</em></li>
											<li><strong>Subproperty of:</strong> <em>none</em></li>
										</ul>

									</div>	
								</div>
							</div>
						</div>


						
						<div class="property blockquote bq-success" about="https://shacl-play.sparna.fr/ontology#valuePartition" id="valuePartition">
							<h4>value partition&nbsp;<sup class="type-op" title="object property" property="rdf:type" resource="owl:ObjectProperty">(object property)</sup></h4>
							<div class="container">
								<div class="row">
									<div class="col-md"><span><strong>IRI:</strong> <code>https://shacl-play.sparna.fr/ontology#valuePartition</code></span></div>
								</div>
								<div class="row">
									<div class="col-md-2">Documentation:</div>
									<div class="col-md-10">
										<ul>
											<li><strong>Label:</strong> <span property="rdfs:label" lang="en">value partition</span><sup>@en</sup></li>
											<li>
												<strong>Definition:</strong> <span property="rdfs:comment" lang="en">A dataset that is the shacl-play:valuePartition of another dataset must have exactly one shacl-play:value property. This partition dataset contains all triples that have the specified value as their object.</span>
											</li>											
											<li>
												<strong>Usage note:</strong> <span property="skos:scopeNote" lang="en">This is used to further subdivide property partitions based on specific values of the property. Each value partition can then express the number of triples that have the corresponding value.</span>
											</li>
											<li><strong>See also:</strong> <a property="rdfs:seeAlso" href="https://shacl-play.sparna.fr/ontology#value">shacl-play:value</a>, <a href="http://rdfs.org/ns/void#propertyPartition">void:propertyPartition</a></li>
										</ul>

									</div>
								</div>
								<div class="row">
									<div class="col-md-2">Specification:</div>
									<div class="col-md-10">
										<ul>
											<li><strong>Domain:</strong> <code><a property="rdfs:domain" href="http://rdfs.org/ns/void#Dataset">void:Dataset</a></code></li>
											<li><strong>Range:</strong> <code><a property="rdfs:range" href="http://rdfs.org/ns/void#Dataset">void:Dataset</a></code></li>
											<li><strong>Subproperty of:</strong> <code><a property="rdfs:subPropertyOf" href="http://rdfs.org/ns/void#subset">void:subset</a></code></li>
										</ul>

									</div>	
								</div>
							</div>
						</div>

					</section>
					<section style="margin-bottom:2em;">
						<h3>Individuals</h3>

						<div class="property blockquote bq-warning" about="https://shacl-play.sparna.fr/ontology#EmbedNever" id="EmbedNever">
							<h4>EmbedNever</h4>
							<div class="container">
								<div class="row">
									<div class="col-md"><span><strong>IRI:</strong> <code>https://shacl-play.sparna.fr/ontology#EmbedNever</code></span></div>
								</div>
								<div class="row">
									<div class="col-md-2">Documentation:</div>
									<div class="col-md-10">
										<ul>
											<li><strong>Label:</strong> <span property="rdfs:label" lang="en">Never embed</span><sup>@en</sup></li>
											<li>
												<strong>Definition:</strong> <span property="rdfs:comment" lang="en">Indicates that embedding should never be applied.</span>
											</li>											
											<li>
												<strong>Usage note:</strong> <span property="skos:scopeNote" lang="en">To be used as a value for shacl-play:embed.</span>
											</li>
											<li><strong>See also:</strong> <a property="rdfs:seeAlso" href="https://shacl-play.sparna.fr/ontology#embed">shacl-play:embed</a></li>
										</ul>
									</div>
								</div>
							</div>
						</div>

					</section>
				</section>
			</div>
		</div>

		<!-- Footer -->
		<section id="footer">
			<div class="container">
				<jsp:include page="include/footer.jsp" />
			</div>
		</section>
	</div>

	<!-- SCRIPTS -->
    <!-- JQuery -->
    <script type="text/javascript" src="<c:url value="/resources/MDB-Free/js/jquery.min.js" />"></script>
    <!-- Bootstrap tooltips -->
    <script type="text/javascript" src="<c:url value="/resources/MDB-Free/js/popper.min.js" />"></script>
    <!-- Bootstrap core JavaScript -->
    <script type="text/javascript" src="<c:url value="/resources/MDB-Free/js/bootstrap.min.js" />"></script>
    <!-- MDB core JavaScript -->
    <script type="text/javascript" src="<c:url value="/resources/MDB-Free/js/mdb.min.js" />"></script>
    
	
</body>
</html>