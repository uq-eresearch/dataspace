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
                xmlns:georss="http://www.georss.org/georss"
                exclude-result-prefixes="atom rdfa">

    <xsl:include href="common-xhtml.xsl"/>
    <xsl:include href="include/header.xsl"/>
    <xsl:include href="include/head.xsl"/>
    <xsl:include href="include/footer.xsl"/>

    <xsl:output method="html" media-type="text/html;charset=utf-8" indent="yes"/>
    <xsl:template match="/">
        <html xmlns="http://www.w3.org/1999/xhtml" dir="ltr" xml:lang="en" lang="en">
            <xsl:apply-templates/>
        </html>
    </xsl:template>

    <!-- *** Atom entry ***-->

    <xsl:template match="atom:entry">
        <head>
            <meta http-equiv="content-type" content="text/html;charset=UTF-8"/>
            <title>
                <xsl:value-of select="atom:title"/>
            </title>
            <link href="/description.css" rel="stylesheet" type="text/css"/>
            <xsl:call-template name="head"/>
            <script type="text/javascript" src="http://openlayers.org/dev/OpenLayers.js">;</script>
            <script type="text/javascript" src="http://maps.google.com/maps/api/js?v=3.2&amp;sensor=false">;</script>
            <script type="text/javascript" src="/js/map/view-map.js">;</script>
        </head>
        <body onload="init();">
            <!-- the collection description itself -->
            <xsl:text>
            </xsl:text>
            <xsl:comment>Collection description</xsl:comment>
            <xsl:call-template name="header"/>
            <ul class="bread-crumbs-nav">
                <xsl:call-template name="bread-crumbs">
                    <xsl:with-param name="path">collections</xsl:with-param>
                    <xsl:with-param name="title">Collections</xsl:with-param>
                </xsl:call-template>
                <xsl:if test="$currentUser">
                    <xsl:call-template name="bread-crumbs-options">
                        <xsl:with-param name="path">collections</xsl:with-param>
                    </xsl:call-template>
                </xsl:if>
            </ul>
            <div class="wrapper">
                <div class="description">
                    <!-- name -->
                    <xsl:apply-templates select="atom:title"/>
                    <!-- description -->
                    <xsl:choose>
                        <xsl:when test="georss:point or georss:polygon">
                            <div>
                                <div id="content-text">
                                    <xsl:apply-templates select="atom:content"/>
                                </div>
                                <div id="map" class="smallmap"
                                     style="width: 50%; height: 400px; float:left; display: inline;">
                                </div>
                                <br style="clear: both;"/>
                            </div>
                        </xsl:when>
                        <xsl:otherwise>
                            <xsl:apply-templates select="atom:content"/>
                        </xsl:otherwise>
                    </xsl:choose>
                    <br style="clear: both; margin-bottom: 1em;"/>
                    <!-- latest-version -->
                    <xsl:if test="$currentUser">
                        <xsl:call-template name="latest-version"/>
                    </xsl:if>
                    <!-- type -->
                    <xsl:call-template name="type"/>
                    <!-- creators -->
                    <xsl:call-template name="creators"/>
                    <!-- curators -->
                    <xsl:call-template name="curators"/>
                    <!-- projects -->
                    <xsl:call-template name="projects"/>
                    <!-- services -->
                    <xsl:call-template name="services"/>
                    <!-- location -->
                    <xsl:call-template name="locations"/>
                    <!-- spatial -->
                    <xsl:call-template name="spatial"/>
                    <!-- temporal -->
                    <xsl:call-template name="temporal"/>
                    <!-- subjects -->
                    <xsl:call-template name="subjects"/>
                    <!-- related info -->
                    <xsl:call-template name="related"/>
                    <!-- rights -->
                    <xsl:apply-templates select="rdfa:meta[@property=$RDFA_ACCESS_RIGHTS]"/>
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

    <!-- projects -->
    <xsl:template name="projects">
        <xsl:if test="atom:link[@rel=$ATOM_IS_OUTPUT_OF]">
            <div class="statement">
                <div class="property">
                    <p>Project (s)</p>
                </div>
                <div class="content">
                    <xsl:apply-templates select="atom:link[@rel=$ATOM_IS_OUTPUT_OF]"/>
                </div>
            </div>
        </xsl:if>
    </xsl:template>

    <!-- services -->
    <xsl:template name="services">
        <xsl:if test="atom:link[@rel=$ATOM_IS_ACCESSED_VIA]">
            <div class="statement">
                <div class="property">
                    <p>Accessed Via</p>
                </div>
                <div class="content">
                    <xsl:apply-templates
                            select="atom:link[@rel=$ATOM_IS_ACCESSED_VIA]"/>
                </div>
            </div>
        </xsl:if>
    </xsl:template>

    <xsl:template
            match="atom:category[@scheme != $NS_DCMITYPE]">
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
