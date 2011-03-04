<?xml version='1.0'?>
<!--
          Transforms UQ collection profile of Atom Syndication Format to
          XHTML with embedded RDFa

          XSLT 1.0

          Nigel Ward, 2010-12

    -->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:atom="http://www.w3.org/2005/Atom"
                xmlns:rdfa="http://www.w3.org/ns/rdfa#"
                xmlns="http://www.w3.org/1999/xhtml"
                exclude-result-prefixes="atom rdfa">
    <xsl:include href="common-xhtml.xsl"/>
    <xsl:include href="include/header.xsl"/>
    <xsl:include href="include/head.xsl"/>
    <xsl:include href="include/footer.xsl"/>

    <xsl:output method="xml" media-type="application/xhtml+xml" indent="yes"/>

    <xsl:template match="/">
        <html>
            <xsl:apply-templates/>
        </html>
    </xsl:template>

    <!-- *** Atom entry ***-->

    <xsl:template match="atom:entry">
        <head>
            <title>
                <xsl:value-of select="atom:title"/>
            </title>
            <link href="/description.css" rel="stylesheet" type="text/css"/>
            <xsl:call-template name="head"/>
        </head>
        <body>
            <!-- the collection description itself -->
            <xsl:text>
            </xsl:text>
            <xsl:comment>Collection description</xsl:comment>
            <xsl:call-template name="header"/>
            <div class="wrapper">
                <div class="description">
                    <!-- name -->
                    <xsl:apply-templates select="atom:title"/>
                    <!-- description -->
                    <xsl:apply-templates select="atom:content"/>
                    <!-- latest-version -->
                    <xsl:call-template name="latest-version"/>
                    <!-- type -->
                    <xsl:call-template name="type"/>
                    <!-- collections -->
                    <xsl:call-template name="collections"/>

                    <xsl:call-template name="isManagerOf"/>
                    <!-- activities -->
                    <xsl:call-template name="activities"/>
                    <!-- subjects -->
                    <xsl:call-template name="subjects"/>
                    <!-- representations -->
                    <xsl:call-template name="representations"/>
                    <!-- metadata about the description -->
                <xsl:text>
                </xsl:text>
                    <xsl:comment>Metadata about the description</xsl:comment>
                    <div class="about">
                        <!-- publisher -->
                        <xsl:apply-templates select="atom:category[@scheme = $NS_GROUP]"/>
                        <!-- updated and updater -->
                        <xsl:call-template name="updated"/>
                    </div>
                </div>
            </div>
            <xsl:call-template name="footer"/>
        </body>
    </xsl:template>

    <!-- object type -->
    <xsl:template name="type">
        <xsl:if test="atom:category[@term=$ENTITY_PARTY]">
            <div class="statement">
                <div class="property">
                    <p>Type</p>
                </div>
                <div class="content">
                    <p>
                        <xsl:value-of select="atom:category[@term=$ENTITY_PARTY]/@label"/>
                    </p>
                </div>
            </div>
        </xsl:if>
    </xsl:template>

    <!-- collections -->
    <xsl:template name="collections">
        <xsl:if test="atom:link[@rel=$ATOM_IS_COLLECTOR_OF]">
            <div class="statement">
                <div class="property">
                    <p>Collector Of</p>
                </div>
                <div class="content">
                    <xsl:apply-templates select="atom:link[@rel=$ATOM_IS_COLLECTOR_OF]"/>
                </div>
            </div>
        </xsl:if>
    </xsl:template>

    <!-- collections -->
    <xsl:template name="isManagerOf">
        <xsl:if test="atom:link[@rel=$ATOM_IS_MANAGER_OF]">
            <div class="statement">
                <div class="property">
                    <p>Curator Of</p>
                </div>
                <div class="content">
                    <xsl:apply-templates select="atom:link[@rel=$ATOM_IS_MANAGER_OF]"/>
                </div>
            </div>
        </xsl:if>
    </xsl:template>

    <!-- Activities -->
    <xsl:template name="activities">
        <xsl:if test="atom:link[@rel=$ATOM_IS_PARTICIPANT_IN]">
            <div class="statement">
                <div class="property">
                    <p>Participate In</p>
                </div>
                <div class="content">
                    <xsl:apply-templates select="atom:link[@rel=$ATOM_IS_PARTICIPANT_IN]"/>
                </div>
            </div>
        </xsl:if>
    </xsl:template>

    <xsl:template
            match="atom:category[@scheme != $NS_FOAF and @scheme!=$NS_GROUP]">
        <p>
            <xsl:choose>
                <xsl:when test="@label">
                    <xsl:value-of select="@label"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="@term"/>
                </xsl:otherwise>
            </xsl:choose>
        </p>
    </xsl:template>

</xsl:stylesheet>
