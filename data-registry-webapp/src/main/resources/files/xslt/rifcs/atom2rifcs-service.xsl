<?xml version='1.0'?>
<!--
          Transforms UQ collection profile of Atom Syndication Format to
          RIF-CSv1.2

          XSLT 1.0

          Nigel Ward, 2010-12

    -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:atom="http://www.w3.org/2005/Atom"
                xmlns:ands="http://www.ands.org.au/ontologies/ns/0.1/VITRO-ANDS.owl#"
                xmlns:dcterms="http://purl.org/dc/terms/" xmlns:rdfa="http://www.w3.org/ns/rdfa#"
                xmlns="http://ands.org.au/standards/rif-cs/registryObjects">

    <xsl:include href="common-rifcs.xsl"/>
    <xsl:output method="xml" media-type="application/rdf+xml" indent="yes"/>

    <xsl:template match="/">
        <registryObjects>
            <xsl:apply-templates/>
        </registryObjects>
    </xsl:template>

    <!-- *** Atom entry ***-->

    <xsl:template match="atom:entry">
        <registryObject group="{atom:category[@scheme=$GROUP_LIST]/@term}">
            <key>
                <xsl:value-of select="atom:link[@rel=$REL_SELF]/@href"/>
            </key>
            <originatingSource>
                <xsl:value-of select="atom:source/atom:id"/>
            </originatingSource>

            <!-- collection -->
            <xsl:if
                    test="atom:category[@scheme=$NS_VIVO]/@term =$ENTITY_SERVICE">
                <collection type="service">
                    <!-- identifiers -->
                    <xsl:apply-templates select="atom:link[@rel=$REL_SELF]"/>
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
                    <xsl:apply-templates select="atom:category[@scheme != $NS_VIVO and @scheme!=$GROUP_LIST]"/>
                    <!-- descriptions -->
                    <xsl:apply-templates select="atom:content"/>
                    <!-- rights descriptions -->
                    <xsl:apply-templates select="atom:rights"/>
                    <xsl:apply-templates select="rdfa:meta[@property=$RDFA_ACCESS_RIGHTS]"/>
                    <!-- related info -->
                    <xsl:apply-templates select="atom:link[@rel=$REL_RELATED]"/>
                </collection>
            </xsl:if>
        </registryObject>
    </xsl:template>

    <!-- collector (party) -->
    <xsl:template match="atom:link[@rel='http://purl.org/dc/terms/creator']">
        <relatedObject>
            <key>
                <xsl:value-of select="@href"/>
            </key>
            <relation type="hasCollector"/>
        </relatedObject>
    </xsl:template>

    <!-- curator / manager (party) -->
    <xsl:template match="atom:link[@rel='http://purl.org/dc/terms/publisher']">
        <relatedObject>
            <key>
                <xsl:value-of select="@href"/>
            </key>
            <relation type="isManagedBy"/>
        </relatedObject>
    </xsl:template>

    <!-- output of (activity) -->
    <xsl:template
            match="atom:link[@rel='http://www.ands.org.au/ontologies/ns/0.1/VITRO-ANDS.owl#isOutputOf']">
        <relatedObject>
            <key>
                <xsl:value-of select="@href"/>
            </key>
            <relation type="isOutputOf"/>
        </relatedObject>
    </xsl:template>


</xsl:stylesheet>