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
        <registryObject
                group="{atom:category[@scheme='https://services.ands.org.au/home/orca/services/getRegistryObjectGroups.php']/@term}">

            <key>
                <xsl:value-of select="atom:link[@rel='self']/@href"/>
            </key>
            <originatingSource>
                <xsl:value-of select="atom:source/atom:id"/>
            </originatingSource>

            <!-- collection -->
            <xsl:if
                    test="atom:category[@scheme='http://xmlns.com/foaf/0.1/']/@term = 'http://xmlns.com/foaf/0.1/Agent'">
                <collection type="party">
                    <!-- identifiers -->
                    <xsl:apply-templates select="atom:link[@rel='self']"/>
                    <!-- names -->
                    <xsl:apply-templates select="atom:title"/>
                    <!-- locations -->
                    <xsl:apply-templates select="atom:link[@rel='http://purl.org/cld/terms/isLocatedAt']"/>
                    <!-- coverage -->
                    <xsl:apply-templates select="rdfa:meta[@property='dcterms:temporal']"/>
                    <!-- related objects -->
                    <xsl:apply-templates select="atom:link[@rel='http://purl.org/dc/terms/creator']"/>
                    <xsl:apply-templates select="atom:link[@rel='http://purl.org/dc/terms/publisher']"/>
                    <xsl:apply-templates
                            select="atom:link[@rel='http://www.ands.org.au/ontologies/ns/0.1/VITRO-ANDS.owl#isOutputOf']"/>
                    <!-- subjects -->
                    <xsl:apply-templates
                            select="atom:category[@scheme != 'http://xmlns.com/foaf/0.1/'
                            and @scheme!='https://services.ands.org.au/home/orca/services/getRegistryObjectGroups.php']"/>
                    <!-- descriptions -->
                    <xsl:apply-templates select="atom:content"/>
                    <!-- rights descriptions -->
                    <xsl:apply-templates select="atom:rights"/>
                    <xsl:apply-templates select="rdfa:meta[@property='dcterms:accessRights']"/>
                    <!-- related info -->
                    <xsl:apply-templates select="atom:link[@rel='related']"/>
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