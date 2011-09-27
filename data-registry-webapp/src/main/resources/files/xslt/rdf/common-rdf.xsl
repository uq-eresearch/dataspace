<?xml version='1.0'?>
<!--
          Transforms UQ collection profile of Atom Syndication Format to
          Link Data represented in RDF/XML

          XSLT 1.0

          Abdul Alabri, 2010-11

    -->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                xmlns:ore="http://www.openarchives.org/ore/terms/" xmlns:atom="http://www.w3.org/2005/Atom"
                xmlns:foaf="http://xmlns.com/foaf/0.1/" xmlns:dc="http://purl.org/dc/elements/1.1/"
                xmlns:dcterms="http://purl.org/dc/terms/" xmlns:dctype="http://purl.org/dc/dcmitype/"
                xmlns:dcam="http://purl.org/dc/dcam/" xmlns:cld="http://purl.org/cld/terms/"
                xmlns:uqdata="http://dataspace.metadata.net/"
                xmlns:ands="http://www.ands.org.au/ontologies/ns/0.1/VITRO-ANDS.owl#"
                xmlns:rdfa="http://www.w3.org/ns/rdfa#"
                xmlns:owl="http://www.w3.org/2002/07/owl#">

    <xsl:include href="../constants.xsl"/>
    <xsl:output method="xml" media-type="application/rdf+xml" indent="yes"/>

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

    <!-- creator -->
    <xsl:template match="atom:link[@rel=$ATOM_CREATOR]">
        <dcterms:creator rdf:resource="{@href}"/>
    </xsl:template>

    <!-- curator -->
    <xsl:template match="atom:link[@rel=$ATOM_PUBLISHER]">
        <dcterms:publisher rdf:resource="{@href}"/>
    </xsl:template>

    <!-- subjects -->
    <xsl:template
            match="atom:category[@scheme!=$NS_DCMITYPE and @scheme!=$NS_FOAF]">
        <dcterms:subject rdf:resource="{@term}"/>
    </xsl:template>

    <!-- spatial coverage -->
    <!-- TO DO -->

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
    <!--<xsl:template-->
    <!--match="atom:category[@scheme=$NS_GROUP]">-->
    <!--<dcterms:publisher>-->
    <!--<xsl:value-of select="@term"/>-->
    <!--</dcterms:publisher>-->
    <!--</xsl:template>-->

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
            <foaf:Person>
                <xsl:apply-templates/>
            </foaf:Person>
        </dcterms:creator>
    </xsl:template>

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