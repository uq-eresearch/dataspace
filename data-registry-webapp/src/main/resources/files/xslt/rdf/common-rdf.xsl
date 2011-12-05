<?xml version='1.0'?>
<!--
          Transforms UQ collection profile of Atom Syndication Format to
          Link Data represented in RDF/XML

          XSLT 1.0

          Abdul Alabri, 2010-11

    -->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                xmlns:atom="http://www.w3.org/2005/Atom"
                xmlns:foaf="http://xmlns.com/foaf/0.1/"
                xmlns:dcterms="http://purl.org/dc/terms/"
                xmlns:ands="http://www.ands.org.au/ontologies/ns/0.1/VITRO-ANDS.owl#"
                xmlns:rdfa="http://www.w3.org/ns/rdfa#"
                xmlns:owl="http://www.w3.org/2002/07/owl#"
                xmlns:georss="http://www.georss.org/georss"
                xmlns:geo="http://www.w3.org/2003/01/geo/wgs84_pos#">

    <xsl:include href="../constants.xsl"/>
    <xsl:output method="xml" media-type="application/rdf+xml" indent="yes"/>

    <!-- entity type -->
    <xsl:template match="atom:link[@rel=$REL_TYPE]">
        <rdf:type rdf:resource="{@href}"/>
    </xsl:template>

    <!-- alternate entity URI -->
    <xsl:template match="atom:id">
        <owl:sameAs rdf:resource="{text()}"/>
    </xsl:template>

    <!-- title -->
    <xsl:template match="atom:title">
        <dcterms:title>
            <xsl:value-of select="text()"/>
        </dcterms:title>
    </xsl:template>
    <xsl:template match="rdfa:meta[@property=$RDFA_ALTERNATIVE]">
        <dcterms:alternative>
            <xsl:value-of select="text()"/>
        </dcterms:alternative>
    </xsl:template>

    <!-- description -->
    <xsl:template match="atom:content">
        <xsl:if test="@type='text'">
            <dcterms:description>
                <xsl:copy-of select="child::node()"/>
            </dcterms:description>
        </xsl:if>
    </xsl:template>

    <!-- web page -->
    <xsl:template match="atom:link[@rel=$ATOM_IS_LOCATED_AT]">
        <foaf:page rdf:resource="{@href}"/>
    </xsl:template>

    <!-- email -->
    <xsl:template match="atom:link[@rel=$ATOM_MBOX]">
        <foaf:mbox rdf:resource="{@href}"/>
    </xsl:template>

    <!-- curator -->
    <xsl:template match="atom:link[@rel=$ATOM_PUBLISHER]">
        <ands:isManagedBy>
            <foaf:Agent rdf:about="{@href}">
                <xsl:if test="@title">
                    <foaf:name>
                        <xsl:value-of select="@title"/>
                    </foaf:name>
                </xsl:if>
            </foaf:Agent>
        </ands:isManagedBy>
    </xsl:template>

    <!-- subjects -->
    <xsl:template match="atom:category">
            <xsl:choose>
                <xsl:when test="@scheme">
                    <dcterms:subject rdf:resource="{@term}"/>
                </xsl:when>
                <xsl:otherwise>
                    <dcterms:subject>
                        <xsl:value-of select="@term"/>
                    </dcterms:subject>
                </xsl:otherwise>
            </xsl:choose>

    </xsl:template>

    <!-- spatial coverage -->
    <xsl:template match="atom:link[@rel=$ATOM_SPATIAL]">
        <dcterms:spatial rdf:resource="{@href}"/>
    </xsl:template>
    <xsl:template match="georss:point">
        <geo:location>
            <geo:lat>
                <xsl:value-of select="substring-before(normalize-space(.),' ')"/>
            </geo:lat>
            <geo:long>
                <xsl:value-of select="substring-after(normalize-space(.),' ')"/>
            </geo:long>
        </geo:location>
    </xsl:template>
    <xsl:template match="georss:polygon">
        <xsl:copy-of select="."/>
    </xsl:template>

    <!-- temporal coverage -->
    <xsl:template match="rdfa:meta[@property=$RDFA_TEMPORAL]">
        <dcterms:temporal>
            <xsl:value-of select="@content"/>
        </dcterms:temporal>
    </xsl:template>

    <!-- related info -->
    <xsl:template match="atom:link[@rel=$REL_RELATED]">
        <dcterms:relation>
            <rdf:Description rdf:about="{@href}">
                <dcterms:title>
                    <xsl:value-of select="@title"/>
                </dcterms:title>
            </rdf:Description>
        </dcterms:relation>
    </xsl:template>

    <!-- rights -->
    <xsl:template match="atom:rights">
        <dcterms:rights>
            <xsl:value-of select="node()"/>
        </dcterms:rights>
    </xsl:template>

    <xsl:template match="rdfa:meta[@property=$RDFA_ACCESS_RIGHTS]">
        <dcterms:accessRights>
            <xsl:value-of select="@content"/>
        </dcterms:accessRights>
    </xsl:template>

    <!-- *** elements describing the metadata itself -->


    <!-- description publisher -->
    <xsl:template match="atom:source/atom:link[@rel=$ATOM_PUBLISHER]">
        <dcterms:publisher>
            <foaf:Agent>
                <foaf:page rdf:resource="{@href}"/>
                <xsl:if test="@title">
                    <foaf:name>
                        <xsl:value-of select="@title"/>
                    </foaf:name>
                </xsl:if>
            </foaf:Agent>
        </dcterms:publisher>
    </xsl:template>

    <!-- description source -->
    <xsl:template match="atom:source">
        <dcterms:source>
            <rdf:Description rdf:about="{atom:id}">
                <dcterms:title>
                    <xsl:value-of select="atom:title"/>
                </dcterms:title>
            </rdf:Description>
        </dcterms:source>
    </xsl:template>

    <xsl:template match="atom:link[@rel='via']">
        <dcterms:isVersionOf>
            <rdf:Description rdf:about="{@href}">
                <xsl:if test="@type">
                    <dcterms:format>
                        <xsl:value-of select="@type"/>
                    </dcterms:format>
                </xsl:if>
            </rdf:Description>
        </dcterms:isVersionOf>
    </xsl:template>

    <!-- description curator -->
    <xsl:template match="atom:author">
        <dcterms:creator>
            <foaf:Agent>
                <xsl:if test="atom:uri">
                    <xsl:attribute name="rdf:about" select="atom:uri"/>
                </xsl:if>
                <xsl:apply-templates select="atom:name"/>
                <xsl:apply-templates select="atom:email"/>
            </foaf:Agent>
        </dcterms:creator>
    </xsl:template>

    <xsl:template match="atom:name">
        <foaf:name>
            <xsl:value-of select="text()"/>
        </foaf:name>
    </xsl:template>

    <xsl:template match="atom:email">
        <foaf:mbox rdf:resource="mailto:{text()}"/>
    </xsl:template>

    <!-- description update date -->
    <xsl:template match="atom:updated">
        <dcterms:modified>
            <xsl:value-of select="text()"/>
        </dcterms:modified>
    </xsl:template>

    <!-- description original creation date -->
    <xsl:template match="atom:published">
        <dcterms:created>
            <xsl:value-of select="text()"/>
        </dcterms:created>
    </xsl:template>

    <!-- alternate formats for description -->
    <xsl:template match="atom:link[@rel = $REL_ALTERNATE]">
        <xsl:if test="@href">
            <dcterms:hasFormat>
                <rdf:Description rdf:about="{@href}">
                    <xsl:if test="@type">
                        <dcterms:format>
                            <xsl:value-of select="@type"/>
                        </dcterms:format>
                    </xsl:if>
                </rdf:Description>
            </dcterms:hasFormat>
        </xsl:if>
    </xsl:template>

</xsl:stylesheet>