<?xml version="1.0" encoding="UTF-8"?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:xs="http://www.w3.org/2001/XMLSchema"
    xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
    xmlns:skos="http://www.w3.org/2004/02/skos/core#"
    exclude-result-prefixes="xs"
    version="2.0">
    
    <xsl:template match="rdf:RDF">
        <xsl:for-each select="./rdf:Description">
            <xsl:if test="compare(rdf:type/@rdf:resource, 'http://www.w3.org/2004/02/skos/core#ConceptScheme') = 0">
                <xs:schema xmlns:xs="http://www.w3.org/2001/XMLSchema">
                    
                    <!-- Add namespaces -->
                    <xsl:namespace name="" select="./@rdf:about"/>
                    <xsl:attribute name="targetNamespace">
                        <xsl:value-of select="./@rdf:about"/>
                    </xsl:attribute>
                    
                    <!-- Add class name -->
                    <xs:simpleType>
                        <xsl:attribute name="name">
                            <xsl:for-each select="./skos:prefLabel">
                                <xsl:if test="compare(./@xml:lang, 'en') = 0">
                                    <xsl:value-of select="concat(translate(substring-after(string(.), 'Europass Standard List of '), ' ', ''), 'EnumType')"/>
                                </xsl:if>
                            </xsl:for-each>
                        </xsl:attribute>
                        <xs:restriction base="xs:anyURI">
                            <xsl:for-each select="../rdf:Description">
                                <xsl:if test="compare(./rdf:type/@rdf:resource, 'http://www.w3.org/2004/02/skos/core#Concept') = 0">
                                    
                                    <!-- Add each controlled uri -->
                                    <xs:enumeration>
                                        <xsl:attribute name="value">
                                            <xsl:value-of select="./@rdf:about"/>
                                        </xsl:attribute>
                                        <xs:annotation>
                                            <xs:documentation>
                                                <xsl:for-each select="./skos:prefLabel">
                                                    <xsl:if test="compare(./@xml:lang, 'en') = 0">
                                                        <xsl:value-of select="."/>
                                                    </xsl:if>
                                                </xsl:for-each>
                                            </xs:documentation>
                                        </xs:annotation>
                                    </xs:enumeration>
                                </xsl:if>
                            </xsl:for-each>
                        </xs:restriction>
                    </xs:simpleType>
                </xs:schema>
            </xsl:if>
        </xsl:for-each>
    </xsl:template>
    
<!--    <xsl:template match="rdf:Description">
       
        <xsl:if test="compare(rdf:type/@rdf:resource, 'http://www.w3.org/2004/02/skos/core#ConceptScheme') = 0">           
            <xs:simpleType>
                <xsl:attribute name="name">
                    <xsl:if test="compare(./skos:prefLabel/@xml:lang, 'en') = 0">
                        <xsl:value-of select="concat(string(./skos:prefLabel), 'Type')"/>
                    </xsl:if>
                </xsl:attribute>
                <xs:restriction base="xs:anyURI">
                        <xsl:for-each select="../rdf:Description">
                            <!-\-<xsl:value-of select="."/>-\->
                            <xsl:if test="compare(./rdf:type/@rdf:resource, 'http://www.w3.org/2004/02/skos/core#Concept') = 0">
                                <xs:enumeration>
                                    <xsl:attribute name="value">
                                            <xsl:value-of select="./@rdf:about"/>
                                    </xsl:attribute>
                                    <xs:annotation>
                                        <xs:documentation>
                                            <xsl:for-each select="./skos:prefLabel">
                                                <xsl:if test="compare(./@xml:lang, 'en') = 0">
                                                    <xsl:value-of select="."/>
                                                </xsl:if>
                                            </xsl:for-each>
                                        </xs:documentation>
                                    </xs:annotation>
                                </xs:enumeration>
                            </xsl:if>
                        </xsl:for-each>
                    
                    </xs:restriction>
            </xs:simpleType>
        </xsl:if>
        
    </xsl:template>-->
</xsl:stylesheet>