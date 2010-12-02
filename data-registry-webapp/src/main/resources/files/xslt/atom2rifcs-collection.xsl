<?xml version='1.0'?>
<!--
          Transforms UQ collection profile of Atom Syndication Format to
          RIF-CSv1.2

          XSLT 1.0

          Nigel Ward, 2010-12    
    -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:atom="http://www.w3.org/2005/Atom"
                xmlns="http://ands.org.au/standards/rif-cs/registryObjects">

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
                    test="atom:category[@scheme='http://purl.org/dc/dcmitype/']/@term = 'http://purl.org/dc/dcmitype/Collection'">
                <collection type="collection">
                    <!-- identifiers -->
                    <xsl:apply-templates select="atom:link[@rel='self']"/>
                    <!-- names -->
                    <xsl:apply-templates select="atom:title"/>
                    <!-- locations -->
                    <xsl:apply-templates select="atom:link[@rel='http://purl.org/cld/terms/isLocatedAt']"/>
                    <!-- related objects -->
                    <xsl:apply-templates select="atom:link[@rel='http://purl.org/dc/terms/creator']"/>
                    <xsl:apply-templates select="atom:link[@rel='http://purl.org/dc/terms/publisher']"/>
                    <xsl:apply-templates
                            select="atom:link[@rel='http://www.ands.org.au/ontologies/ns/0.1/VITRO-ANDS.owl#isOutputOf']"/>
                    <!-- subjects -->
                    <xsl:apply-templates
                            select="atom:category[@scheme != 'http://purl.org/dc/dcmitype/' and @scheme!='https://services.ands.org.au/home/orca/services/getRegistryObjectGroups.php']"/>
                    <!-- descriptions -->
                    <xsl:apply-templates select="atom:content"/>
                </collection>
            </xsl:if>
        </registryObject>
    </xsl:template>

    <!-- identifier -->
    <xsl:template match="atom:link[@rel='self']">
        <identifier>
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


</xsl:stylesheet>