<?xml version='1.0'?>
<!--
          Transforms UQ collection profile of Atom Syndication Format to
          Dublin Core Collections application profile represented in RDF/XML

          XSLT 1.0

          Nigel Ward, 2010-11

    -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                xmlns:ore="http://www.openarchives.org/ore/terms/" xmlns:atom="http://www.w3.org/2005/Atom"
                xmlns:foaf="http://xmlns.com/foaf/0.1/" xmlns:dc="http://purl.org/dc/elements/1.1/"
                xmlns:dcterms="http://purl.org/dc/terms/" xmlns:dctype="http://purl.org/dc/dcmitype/"
                xmlns:dcam="http://purl.org/dc/dcam/" xmlns:cld="http://purl.org/cld/terms/"
                xmlns:uqdata="http://dataspace.metadata.net/"
                xmlns:ands="http://www.ands.org.au/ontologies/ns/0.1/VITRO-ANDS.owl#">

    <xsl:output method="xml" media-type="application/rdf+xml" indent="yes"/>

    <xsl:template match="/">
        <rdf:RDF>
            <xsl:apply-templates/>
        </rdf:RDF>
    </xsl:template>

    <!-- *** RFC 4287 : 4.1. Container Elements ***-->

    <xsl:template match="atom:entry">
        <!-- the collection description itself -->

        <xsl:comment>Collection description</xsl:comment>

        <rdf:Description
                rdf:about="{atom:link[@rel='http://www.openarchives.org/ore/terms/describes']/@href}">
            <xsl:if
                    test="atom:category[@term='http://purl.org/dc/dcmitype/Collection' or @term='http://purl.org/dc/dcmitype/Dataset']">
                <dcterms:type rdf:resource="{atom:category/@term}"/>
            </xsl:if>
            <xsl:apply-templates select="atom:title"/>
            <xsl:apply-templates select="atom:content"/>

            <xsl:apply-templates
                    select="atom:category[@term!='http://purl.org/dc/dcmitype/Collection' and @term!='http://purl.org/dc/dcmitype/Dataset']"/>

            <xsl:apply-templates select="atom:link[@type='http://purl.org/cld/terms/isLocatedAt']"/>
            <xsl:apply-templates select="atom:link[@type='http://purl.org/dc/terms/creator']"/>
            <xsl:apply-templates select="atom:link[@type='http://purl.org/dc/terms/publisher']"/>

            <ore:isDescribedBy rdf:resource="{atom:id}"/>
        </rdf:Description>

        <!-- metadata about the description -->
	    <xsl:text>
	    </xsl:text>
        <xsl:comment>Metadata about the description</xsl:comment>
        <rdf:Description rdf:about="{atom:id}">
            <xsl:apply-templates select="atom:author"/>
            <xsl:apply-templates select="atom:updated"/>
            <xsl:apply-templates select="atom:link[@rel='self' or @rel='alternate']"/>
            <xsl:apply-templates select="atom:link[@rel='via']"/>
        </rdf:Description>

        <!-- metadata about the registry -->
	    <xsl:text>
	    </xsl:text>
        <xsl:comment>Metadata about the registry</xsl:comment>
        <xsl:if test="atom:link[@rel='via']">
            <rdf:Description rdf:about="{atom:link[@rel='via']/@href}">
                <dcterms:title>
                    <xsl:value-of select="atom:link[@rel='via']/@title"/>
                </dcterms:title>
            </rdf:Description>
        </xsl:if>
    </xsl:template>

    <xsl:template match="atom:content">
        <xsl:if test="@type='text'">
            <dcterms:description>
                <xsl:copy-of select="child::node()"/>
            </dcterms:description>
        </xsl:if>
    </xsl:template>

    <!-- *** RFC 4287 : 3.2. Person Constructs *** -->

    <xsl:template match="atom:name">
        <foaf:name>
            <xsl:value-of select="text()"/>
        </foaf:name>
    </xsl:template>

    <xsl:template match="atom:uri">
        <foaf:page rdf:resource="{text()}"/>
        <xsl:apply-templates/>
    </xsl:template>

    <xsl:template match="atom:email">
        <foaf:mbox rdf:resource="mailto:{text()}"/>
        <xsl:apply-templates/>
    </xsl:template>

    <!-- *** RFC 4287 : 4.2. Metadata Elements *** -->

    <xsl:template match="atom:author">
        <dcterms:creator>
            <foaf:Person>
                <xsl:apply-templates/>
            </foaf:Person>
        </dcterms:creator>
    </xsl:template>

    <xsl:template match="atom:id">
        <dcterms:identifier>
            <xsl:copy-of select="child::node()"/>
        </dcterms:identifier>
    </xsl:template>

    <xsl:template match="atom:link[@rel = 'alternate' or @rel = 'self']">
        <xsl:if test="@href">
            <dcterms:hasFormat>
                <rdf:Description rdf:about="{@href}">
                    <xsl:if test="@type">
                        <dcterms:format>
                            <rdf:Description>
                                <dcam:memberOf rdf:resource="http://purl.org/dc/terms/IMT"/>
                                <rdf:value>
                                    <xsl:value-of select="@type"/>
                                </rdf:value>
                            </rdf:Description>
                        </dcterms:format>
                    </xsl:if>
                </rdf:Description>
            </dcterms:hasFormat>
        </xsl:if>
    </xsl:template>

    <xsl:template match="atom:link[@rel='via']">
        <dcterms:source rdf:resource="{@href}"/>
    </xsl:template>

    <xsl:template match="atom:title">
        <dcterms:title>
            <xsl:value-of select="text()"/>
        </dcterms:title>
    </xsl:template>

    <xsl:template match="atom:updated">
        <dcterms:modified>
            <xsl:value-of select="text()"/>
        </dcterms:modified>
    </xsl:template>

    <xsl:template match="atom:source">
        <xsl:if test="atom:generator">
            <dcterms:source rdf:resource="{atom:generator/@uri}"/>
        </xsl:if>
    </xsl:template>

    <!-- *** UQ data extensions *** -->
    <xsl:template match="atom:category">
        <dcterms:subject rdf:resource="{@term}"/>
    </xsl:template>

    <xsl:template match="atom:link[@type='http://purl.org/cld/terms/isLocatedAt']">
        <cld:isLocatedAt rdf:resource="{@href}"/>
    </xsl:template>

    <xsl:template match="atom:link[@type='http://purl.org/dc/terms/creator']">
        <dcterms:creator rdf:resource="{@href}"/>
    </xsl:template>

    <xsl:template match="atom:link[@type='http://purl.org/dc/terms/publisher']">
        <dcterms:publisher rdf:resource="{@href}"/>
    </xsl:template>

</xsl:stylesheet>        