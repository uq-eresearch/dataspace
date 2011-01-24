<?xml version='1.0'?>
<!--
          Transforms UQ collection profile of Atom Syndication Format to
          RIF-CSv1.2

          XSLT 1.0

          Abdul Alabri, 2010-11

    -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:atom="http://www.w3.org/2005/Atom"
                xmlns:ands="http://www.ands.org.au/ontologies/ns/0.1/VITRO-ANDS.owl#"
                xmlns:dcterms="http://purl.org/dc/terms/" xmlns:rdfa="http://www.w3.org/ns/rdfa#"
                xmlns="http://ands.org.au/standards/rif-cs/registryObjects">

    <xsl:output method="xml" media-type="application/rdf+xml" indent="yes"/>

    <!-- identifier -->
    <xsl:template match="atom:link[@rel='self']">
        <identifier type="url">
            <xsl:value-of select="@href"/>
        </identifier>
    </xsl:template>

    <!-- name -->
    <xsl:template match="atom:title">
        <name type="primary">
            <namePart>
                <xsl:value-of select="self::node()"/>
            </namePart>
        </name>
    </xsl:template>

    <!-- location -->
    <xsl:template match="atom:link[@rel='http://purl.org/cld/terms/isLocatedAt']">
        <location>
            <address>
                <electronic type="url">
                    <value>
                        <xsl:value-of select="@href"/>
                    </value>
                </electronic>
            </address>
        </location>
    </xsl:template>

    <!-- temporal coverage -->
    <!-- NB: the XSL below is fragile. It assumes a restricted DCMI Period encoding, with only start and end components,
in that order, and encoded as W3CDTF dates-->
    <xsl:template match="rdfa:meta[@property='http://purl.org/dc/terms/temporal']">
        <coverage>
            <temporal>
                <date type="from" dateFormat="W3CDTF">
                    <xsl:value-of select="substring-after(substring-before(@content,';'),'start:')"/>
                </date>
                <date type="to" dateFormat="W3CDTF">
                    <xsl:value-of select="substring-after(substring-after(@content,';'),'end:')"/>
                </date>
            </temporal>
        </coverage>
    </xsl:template>
    <!-- subjects -->
    <xsl:template
            match="atom:category[@scheme != 'http://purl.org/dc/dcmitype/' and @scheme!='https://services.ands.org.au/home/orca/services/getRegistryObjectGroups.php']">
        <subject>
            <xsl:attribute name="type">
                <xsl:choose>
                    <xsl:when test="@scheme = 'http://purl.org/anzsrc/for/#field_'">anzsrc-for</xsl:when>
                    <xsl:when test="@scheme = 'http://purl.org/anzsrc/seo/#field_'">anzsrc-seo</xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="@scheme"/>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:attribute>
            <xsl:value-of select="@term"/>
        </subject>
    </xsl:template>

    <!-- descriptions -->
    <xsl:template match="atom:content">
        <description type="full">
            <xsl:value-of select="node()"/>
        </description>
    </xsl:template>

    <xsl:template match="atom:rights">
        <description type="rights">
            <xsl:value-of select="node()"/>
        </description>
    </xsl:template>

    <xsl:template match="rdfa:meta[@property='http://purl.org/dc/terms/accessRights']">
        <description type="accessRights">
            <xsl:value-of select="@content"/>
        </description>
    </xsl:template>

    <!-- related info -->
    <xsl:template match="atom:link[@rel='related']">
        <relatedInfo>
            <identifier type="url">
                <xsl:value-of select="@href"/>
            </identifier>
            <title>
                <xsl:value-of select="@title"/>
            </title>
        </relatedInfo>
    </xsl:template>
</xsl:stylesheet>