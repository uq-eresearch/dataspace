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
    xmlns:georss="http://www.georss.org/georss"
    xmlns="http://ands.org.au/standards/rif-cs/registryObjects">

    <xsl:include href="common-rifcs.xsl"/>
    <xsl:output method="xml" media-type="application/rifcs+xml" indent="yes"/>

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
            <xsl:if test="atom:category[@scheme=$NS_DCMITYPE]/@term=$ENTITY_COLLECTION">
                <collection type="collection">
                    <!-- identifiers -->
                    <xsl:apply-templates select="atom:link[@rel=$RDF_DESCRIBES]"/>
                    <xsl:apply-templates select="/atom:entry/atom:id"/>
                    <!-- names -->
                    <xsl:apply-templates select="atom:title"/>
                    <xsl:apply-templates select="rdfa:meta[@property=$RDFA_ALTERNATIVE]"/>
                    <!-- location -->
                    <xsl:apply-templates select="atom:link[@rel=$ATOM_IS_LOCATED_AT]"/>
                    <!-- coverage -->
                    <xsl:apply-templates select="rdfa:meta[@property=$RDFA_TEMPORAL]"/>
                    <xsl:apply-templates select="georss:point"/>
                    <xsl:apply-templates select="georss:polygon"/>
                    <xsl:apply-templates select="atom:link[@rel=$ATOM_SPATIAL]"/>
                    <!-- related objects -->
                    <xsl:apply-templates select="atom:author"/>
                    <xsl:apply-templates select="atom:link[@rel=$ATOM_PUBLISHER]"/>
                    <xsl:apply-templates select="atom:link[@rel=$ATOM_IS_OUTPUT_OF]"/>
                    <xsl:apply-templates select="atom:link[@rel=$ATOM_IS_ACCESSED_VIA]"/>
                    <xsl:apply-templates select="atom:link[@rel=$REL_RELATED]"/>
                    <!-- subjects -->
                    <xsl:apply-templates
                        select="atom:category[@scheme != $NS_DCMITYPE]"/>
                    <!-- descriptions -->
                    <xsl:apply-templates select="atom:content"/>
                    <!-- rights descriptions -->
                    <xsl:apply-templates select="atom:rights"/>
                    <xsl:apply-templates select="atom:link[@rel=$REL_LICENSE]"/>
                    <xsl:apply-templates select="rdfa:meta[@property=$RDFA_ACCESS_RIGHTS]"/>
                    <!-- related info -->
                    <xsl:apply-templates select="atom:link[@rel=$ATOM_IS_REFERENCED_BY]"/>
                </collection>
            </xsl:if>
        </registryObject>
    </xsl:template>

    <!-- spatial coverage -->
    <!-- point encoded as zero area DCMI box -->
    <xsl:template match="georss:point">
        <xsl:variable name="lat">
            <xsl:value-of select="substring-before(normalize-space(.),' ')"/>
        </xsl:variable>
        <xsl:variable name="long">
            <xsl:value-of select="substring-after(normalize-space(.),' ')"/>
        </xsl:variable>
        <coverage>
            <spatial type="iso19139dcmiBox">
                northlimit=<xsl:value-of select="$lat"/>;
                southlimit=<xsl:value-of select="$lat"/>; 
                eastlimit=<xsl:value-of select="$long"/>; 
                westlimit=<xsl:value-of select="$long"/>; 
                projection=WGS84
            </spatial>
        </coverage>
    </xsl:template>
    <!-- polygon down-coded to DCMI Box -->
    <xsl:template match="georss:polygon">
        <xsl:call-template name="poly2box">
            <xsl:with-param name="list" select="tokenize(normalize-space(.), ' ')"/>
            <xsl:with-param name="latlist" select="()"/>
            <xsl:with-param name="longlist" select="()"/>
        </xsl:call-template>
    </xsl:template>
    <xsl:template name="poly2box">
        <xsl:param name="list"/>
        <xsl:param name="latlist"/>
        <xsl:param name="longlist"/>
        <xsl:choose>
            <xsl:when test="count($list) gt 1">
                <xsl:variable name="lat" select="number($list[1])"/>
                <xsl:variable name="long" select="number($list[2])"/>
                <xsl:call-template name="poly2box">
                    <xsl:with-param name="list" select="subsequence($list, 3)"/>
                    <xsl:with-param name="latlist" select="($latlist, $lat)"/>
                    <xsl:with-param name="longlist" select="($longlist, $long)"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <coverage>
                    <spatial type="iso19139dcmiBox">
                        northlimit=<xsl:value-of select="max($latlist)"/>; 
                        southlimit=<xsl:value-of select="min($latlist)"/>; 
                        eastlimit=<xsl:value-of select="min($longlist)"/>; 
                        westlimit=<xsl:value-of select="max($longlist)"/>; 
                        projection=WGS84
                    </spatial>
                </coverage>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <!-- geonames reference represented as just a name-->
    <xsl:template match="atom:link[@rel=$ATOM_SPATIAL]">
        <coverage>
            <spatial type="text">
                <xsl:value-of select="@title"/>
            </spatial>
        </coverage>
    </xsl:template>

    <!-- collector (party) -->
    <xsl:template match="atom:entry/atom:author">
        <relatedObject>
            <key>
                <xsl:value-of select="atom:uri"/>
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
    <xsl:template match="atom:link[@rel=$ATOM_IS_OUTPUT_OF]">
        <relatedObject>
            <key>
                <xsl:value-of select="@href"/>
            </key>
            <relation type="isOutputOf"/>
        </relatedObject>
    </xsl:template>
    
    <!-- is accessed via (service) -->
    <xsl:template match="atom:link[@rel=$ATOM_IS_ACCESSED_VIA]">
        <relatedObject>
            <key>
                <xsl:value-of select="@href"/>
            </key>
            <relation type="isSupportedBy"/>
        </relatedObject>
    </xsl:template>
    
    <!-- has association with (collection) -->
    <xsl:template match="atom:link[@rel=$REL_RELATED]">
        <relatedObject>
            <key>
                <xsl:value-of select="@href"/>
            </key>
            <relation type="hasAssociationWith"/>
        </relatedObject>
    </xsl:template>
    
</xsl:stylesheet>
