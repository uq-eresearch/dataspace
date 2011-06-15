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
                xmlns="http://ands.org.au/standards/rif-cs/registryObjects"
                xmlns:fn="http://www.w3.org/2005/xpath-functions" xmlns:str="http://exslt.org/strings"
                exclude-result-prefixes="atom ands dcterms rdfa fn str">
    <xsl:include href="../constants.xsl"/>

    <!-- object to record ID translation -->
    <!--
     RIF-CS links using the record ID; our Atom representation links using the object ID
     Hence need to change object id to record id in the RIF-CS representation of links
     -->
    <xsl:template name="object-to-record-id">
        <xsl:param name="object-id"/>
        <xsl:value-of select="tokenize(object-id, '#')[1]"/>
    </xsl:template>

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

    <!-- locations -->
    <xsl:template match="atom:link[@rel=$ATOM_IS_LOCATED_AT]">
        <location>
            <address>
                <electronic type="url">
                    <value>
                        <xsl:value-of select="@href" disable-output-escaping="no"/>
                    </value>
                </electronic>
            </address>
        </location>
    </xsl:template>
    <!-- publications -->
    <xsl:template match="atom:link[@rel=$ATOM_PUBLICATIONS]">
        <relatedInfo>
            <identifier type="uri">
                <xsl:value-of select="@href" disable-output-escaping="no"/>
            </identifier>
            <title>
                <xsl:value-of select="@title" disable-output-escaping="no"/>
            </title>
        </relatedInfo>
    </xsl:template>
    <xsl:template match="atom:link[@rel=$ATOM_MBOX]">
        <location>
            <address>
                <electronic type="email">
                    <value>
                        <xsl:choose>
                            <xsl:when test="fn:contains(@href, 'mailto:')">
                                <xsl:value-of select="fn:substring-after(@href,':')"/>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:value-of select="@href"/>
                            </xsl:otherwise>
                        </xsl:choose>
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
                <xsl:for-each select="fn:tokenize(@content,';')">
                    <xsl:variable name="field">
                        <xsl:value-of select="normalize-space(substring-before(.,'='))"/>
                    </xsl:variable>
                    <xsl:variable name="value">
                        <xsl:value-of select="normalize-space(substring-after(.,'='))"/>
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
    <xsl:template match="atom:category[@scheme = $SCHEME_FOR]">
        <subject type="anzsrc-for">
            <xsl:value-of select="substring-after(@term, $PREFIX_FOR)"/>
        </subject>
    </xsl:template>
    <xsl:template match="atom:category[@scheme = $SCHEME_SEO]">
        <subject type="anzsrc-seo">
            <xsl:value-of select="substring-after(@term, $PREFIX_SEO)"/>
        </subject>
    </xsl:template>
    <xsl:template match="atom:category[@scheme = $SCHEME_TOA]">
        <subject type="anzsrc-toa">
            <xsl:value-of select="substring-after(@term, $PREFIX_TOA)"/>
        </subject>
    </xsl:template>
    <xsl:template match="atom:category[not(@scheme)]">
        <subject type="local">
            <xsl:value-of select="@term"/>
        </subject>
    </xsl:template>

    <!-- descriptions -->
    <xsl:template match="atom:content">
        <description type="brief">
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
