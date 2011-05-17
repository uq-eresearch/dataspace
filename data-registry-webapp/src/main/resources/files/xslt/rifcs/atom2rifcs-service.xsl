<?xml version='1.0'?>
<!--
          Transforms UQ collection profile of Atom Syndication Format to
          RIF-CSv1.2

          XSLT 1.0

          Nigel Ward, 2010-12

    -->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:atom="http://www.w3.org/2005/Atom"
                xmlns:ands="http://www.ands.org.au/ontologies/ns/0.1/VITRO-ANDS.owl#"
                xmlns:dcterms="http://purl.org/dc/terms/" xmlns:rdfa="http://www.w3.org/ns/rdfa#"
                xmlns="http://ands.org.au/standards/rif-cs/registryObjects"
                exclude-result-prefixes="atom ands dcterms rdfa">

    <xsl:include href="common-rifcs.xsl"/>
    <xsl:output method="xml" media-type="application/rdf+xml" indent="yes"/>

    <xsl:template match="/">
        <registryObjects>
            <xsl:apply-templates/>
        </registryObjects>
    </xsl:template>

    <!-- *** Atom entry ***-->

    <xsl:template match="atom:entry">
        <registryObject group="{atom:source/atom:link[@rel=$ATOM_PUBLISHER]/@title}">
            <key>
                <xsl:value-of select="atom:link[@rel=$RDF_DESCRIBES]/@href"/>
            </key>
            <originatingSource>
                <xsl:value-of select="atom:source/atom:id"/>
            </originatingSource>

            <!-- collection -->
            <xsl:if
                    test="atom:category[@scheme=$NS_DCMITYPE]">
                <service type="report">
                    <!-- identifiers -->
                    <xsl:apply-templates select="atom:link[@rel=$RDF_DESCRIBES]"/>
                    <xsl:apply-templates select="/atom:entry/atom:id"/>
                    <!-- names -->
                    <xsl:apply-templates select="atom:title"/>
                    <!-- locations -->
                    <xsl:apply-templates select="atom:link[@rel=$ATOM_IS_LOCATED_AT]"/>
                    <!-- coverage -->
                    <xsl:apply-templates select="rdfa:meta[@property=$RDFA_TEMPORAL]"/>
                    <!-- related objects -->
                    <xsl:apply-templates select="atom:link[@rel=$ATOM_CREATOR]"/>
                    <xsl:apply-templates select="atom:link[@rel=$ATOM_PUBLISHER]"/>
                    <xsl:apply-templates select="atom:link[@rel=$ATOM_IS_OUTPUT_OF]"/>
                    <!-- subjects -->
                    <xsl:apply-templates select="atom:category[@scheme != $NS_VIVO]"/>
                    <!-- descriptions -->
                    <xsl:apply-templates select="atom:content"/>
                    <!-- rights descriptions -->
                    <xsl:apply-templates select="atom:rights"/>
                    <xsl:apply-templates select="rdfa:meta[@property=$RDFA_ACCESS_RIGHTS]"/>
                    <!-- related info -->
                    <xsl:apply-templates select="atom:link[@rel=$REL_RELATED]"/>
                </service>
            </xsl:if>
        </registryObject>
    </xsl:template>

    <!-- collector (party) -->
    <xsl:template match="atom:link[@rel=$ATOM_CREATOR]">
        <relatedObject>
            <key>
                <xsl:value-of select="@href"/>
            </key>
            <relation type="hasCollector"/>
        </relatedObject>
    </xsl:template>

    <!-- curator / manager (party) -->
    <xsl:template match="atom:link[@rel=$ATOM_PUBLISHER]">
        <relatedObject>
            <key>
                <xsl:value-of select="@href"/>
            </key>
            <relation type="isManagedBy"/>
        </relatedObject>
    </xsl:template>

    <!-- output of (activity) -->
    <xsl:template
            match="atom:link[@rel=$ATOM_IS_OUTPUT_OF]">
        <relatedObject>
            <key>
                <xsl:value-of select="@href"/>
            </key>
            <relation type="isOutputOf"/>
        </relatedObject>
    </xsl:template>


</xsl:stylesheet>