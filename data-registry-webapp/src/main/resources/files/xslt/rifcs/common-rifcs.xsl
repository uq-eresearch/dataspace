<?xml version='1.0'?>
<!--
          Transforms UQ collection profile of Atom Syndication Format to
          RIF-CSv1.2

          XSLT 1.0

          Abdul Alabri, 2010-11

    -->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
    xmlns:atom="http://www.w3.org/2005/Atom"
    xmlns:ands="http://www.ands.org.au/ontologies/ns/0.1/VITRO-ANDS.owl#"
    xmlns:dcterms="http://purl.org/dc/terms/" xmlns:rdfa="http://www.w3.org/ns/rdfa#"
    xmlns="http://ands.org.au/standards/rif-cs/registryObjects">
    <xsl:include href="../constants.xsl"/>


    <!-- identifiers -->
    <xsl:template match="atom:link[@rel=$RDF_DESCRIBES]">
        <identifier type="url">
            <xsl:value-of select="@href"/>
        </identifier>
    </xsl:template>
    <xsl:template match="/atom:entry/atom:id">
        <identifier type="url">
            <xsl:value-of select="self::node()"/>
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
    <xsl:template match="rdfa:meta[@property=$RDFA_ALTERNATIVE]">
        <name type="alternative">
            <namePart>
                <xsl:value-of select="@content"/>
            </namePart>
        </name>
    </xsl:template>

    <!-- location -->
    <xsl:template match="atom:link[@rel=$ATOM_IS_LOCATED_AT]">
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
    <!-- NB: this assumes W3CDTF date encoding-->
    <xsl:template match="rdfa:meta[@property=$RDFA_TEMPORAL]">
        <coverage>
            <temporal>
                <xsl:for-each select="tokenize(@content,';')">
                    <xsl:variable name="field">
                        <xsl:value-of select="substring-before(.,'=')"/>
                    </xsl:variable>
                    <xsl:variable name="value">
                        <xsl:value-of select="substring-after(.,'=')"/>
                    </xsl:variable>
                    <xsl:choose>
                        <xsl:when test="$field = 'start'">
                            <date type="from" dateFormat="W3CDTF">
                                <xsl:value-of select="$value"/>
                            </date>
                        </xsl:when>
                        <xsl:when test="$field = 'end'">
                            <date type="to" dateFormat="W3CDTF">
                                <xsl:value-of select="$value"/>
                            </date>
                        </xsl:when>
                    </xsl:choose>
                </xsl:for-each>
            </temporal>
        </coverage>
    </xsl:template>

    <!-- subjects -->
    <xsl:template match="atom:category[@scheme != $NS_DCMITYPE]">
        <subject>
            <xsl:choose>
                <xsl:when test="@scheme = $SCHEME_FOR">
                    <xsl:attribute name="type">anzsrc-for</xsl:attribute>
                    <xsl:value-of select="substring-after(@term, $PREFIX_FOR)"/>
                </xsl:when>
                <xsl:when test="@scheme = $SCHEME_SEO">
                    <xsl:attribute name="type">anzsrc-seo</xsl:attribute>
                    <xsl:value-of select="substring-after(@term, $PREFIX_SEO)"/>
                </xsl:when>
                <xsl:when test="@scheme = $SCHEME_TOA">
                    <xsl:attribute name="type">anzsrc-toa</xsl:attribute>
                    <xsl:value-of select="substring-after(@term, $PREFIX_TOA)"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:attribute name="type"><xsl:value-of select="@scheme"/></xsl:attribute>
                    <xsl:value-of select="@term"/>
                </xsl:otherwise>
            </xsl:choose>
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

    <xsl:template match="rdfa:meta[@property=$RDFA_ACCESS_RIGHTS]">
        <description type="accessRights">
            <xsl:value-of select="@content"/>
        </description>
    </xsl:template>

    <xsl:template match="atom:link[@rel=$REL_LICENSE]">
        <xsl:if test="@title">
            <description type="rights">
                <xsl:value-of select="@title"/>
            </description>
        </xsl:if>
    </xsl:template>

    <!-- related info -->
    <xsl:template match="atom:link[@rel=$ATOM_IS_REFERENCED_BY]">
        <relatedInfo>
            <identifier type="uri">
                <xsl:value-of select="@href"/>
            </identifier>
            <title>
                <xsl:value-of select="@title"/>
            </title>
        </relatedInfo>
    </xsl:template>
</xsl:stylesheet>
