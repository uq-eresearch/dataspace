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

    <xsl:output method="html" version="4.0"
                doctype-public="-//W3C//DTD HTML 4.01//EN"
                doctype-system="http://www.w3.org/TR/html4/strict.dtd"
                media-type="text/html;charset=utf-8" indent="yes"/>
    <xsl:template match="/">
        <html>
            <xsl:apply-templates/>
        </html>
    </xsl:template>

    <!-- *** Atom entry ***-->

    <xsl:template match="atom:entry">
        <head>
            <title>
                <xsl:value-of select="$applicationName"/>
            </title>
            <link href="/css/description.css" rel="stylesheet" type="text/css"/>
            <xsl:call-template name="head"/>
        </head>
        <body>
            <!-- the collection description itself -->
            <xsl:text>
            </xsl:text>
            <xsl:comment>Collection description</xsl:comment>
            <xsl:call-template name="header"/>
            <div class="wrapper">
                <ul class="bread-crumbs-nav">
                    <xsl:call-template name="bread-crumbs">
                        <xsl:with-param name="path">activities</xsl:with-param>
                        <xsl:with-param name="title">Activities</xsl:with-param>
                    </xsl:call-template>
                    <xsl:if test="$currentUser">
                        <xsl:call-template name="bread-crumbs-options">
                            <xsl:with-param name="path">activities</xsl:with-param>
                        </xsl:call-template>
                    </xsl:if>
                </ul>
                <div class="description">
                    <!-- name -->
                    <xsl:apply-templates select="atom:title"/>
                    <!-- description -->
                    <xsl:apply-templates select="atom:content"/>
                    <!-- latest-version -->
                    <xsl:if test="$currentUser">
                        <xsl:call-template name="latest-version"/>
                    </xsl:if>
                    <!-- type -->
                    <xsl:call-template name="type"/>
                    <!-- homepage -->
                    <xsl:call-template name="locations"/>
                    <!-- participants -->
                    <xsl:call-template name="participants"/>
                    <!-- collections -->
                    <xsl:call-template name="output"/>
                    <!-- subjects -->
                    <xsl:call-template name="subjects"/>
                    <xsl:call-template name="keywords"/>
                    <!-- rights -->
                    <xsl:apply-templates select="atom:rights"/>
                    <!-- metadata about the description -->
                <xsl:text>
                </xsl:text>
                    <xsl:comment>Metadata about the description</xsl:comment>
                    <div class="about">
                        <!-- publisher -->
                        <xsl:call-template name="description-publisher"/>
                        <!-- updated and updater -->
                        <xsl:call-template name="updated"/>
                        <!-- representations -->
                        <xsl:call-template name="representations"/>
                    </div>
                </div>
            </div>
            <xsl:call-template name="footer"/>
        </body>
    </xsl:template>

    <!-- collections -->
    <xsl:template name="participants">
        <xsl:if test="atom:link[@rel=$ATOM_HAS_PARTICIPANT]">
            <div class="statement">
                <div class="property">
                    <p>Participants</p>
                </div>
                <div class="content">
                    <xsl:apply-templates select="atom:link[@rel=$ATOM_HAS_PARTICIPANT]"/>
                </div>
            </div>
        </xsl:if>
    </xsl:template>

    <!-- collections -->
    <xsl:template name="output">
        <xsl:if test="atom:link[@rel=$ATOM_HAS_OUTPUT]">
            <div class="statement">
                <div class="property">
                    <p>Collections created</p>
                </div>
                <div class="content">
                    <xsl:apply-templates select="atom:link[@rel=$ATOM_HAS_OUTPUT]"/>
                </div>
            </div>
        </xsl:if>
    </xsl:template>

</xsl:stylesheet>
