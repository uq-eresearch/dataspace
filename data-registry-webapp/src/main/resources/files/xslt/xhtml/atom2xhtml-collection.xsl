<?xml version="1.0" encoding="ISO-8859-1"?>
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
                xmlns:fn="http://www.w3.org/2005/xpath-functions"
                exclude-result-prefixes="atom rdfa georss fn">

    <xsl:include href="common-xhtml.xsl"/>
    <xsl:include href="include/header.xsl"/>
    <xsl:include href="include/head.xsl"/>
    <xsl:include href="include/footer.xsl"/>

    <xsl:output method="html" version="4.0"
                doctype-public="-//W3C//DTD HTML 4.01//EN"
                doctype-system="http://www.w3.org/TR/html4/strict.dtd"
                media-type="text/html;charset=utf-8" indent="yes"/>
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
                <xsl:value-of select="fn:concat($applicationName, ': ', atom:title)"/>
            </title>
            <link href="/css/description.css" rel="stylesheet" type="text/css"/>
            <xsl:call-template name="head"/>

            <xsl:if test="georss:point or georss:polygon">
                <script type="text/javascript" src="/js/openlayers/OpenLayers.js">;</script>
                <script type="text/javascript" src="http://maps.google.com/maps/api/js?v=3.5&amp;sensor=false">;</script>
                <script type="text/javascript" src="/js/map/map.js">;</script>
            </xsl:if>
        </head>
        <body>
            <!-- the collection description itself -->
            <xsl:text>
            </xsl:text>
            <xsl:comment>Collection description</xsl:comment>
            <xsl:call-template name="header"/>
            <div class="wrapper">
                <div class="content-top">
                    <div class="pad-top pad-sides">
                        <!-- bread crumbs -->
                        <ul class="bread-crumbs-nav">
                            <xsl:call-template name="bread-crumbs">
                                <xsl:with-param name="path">collections</xsl:with-param>
                                <xsl:with-param name="title">Collections</xsl:with-param>
                            </xsl:call-template>
                        </ul>
                        <!-- buttons -->
                        <div class="button-bar">
                            <xsl:call-template name="button-bar">
                                <xsl:with-param name="path">collections</xsl:with-param>
                            </xsl:call-template>
                        </div>
                        <!-- TODO versions
                  <xsl:if test="$currentUser">
                      <xsl:call-template name="latest-version"/>
                  </xsl:if>      -->
                        <!-- identifiers -->
                        <xsl:call-template name="identifiers"/>
                        <!-- names -->
                        <xsl:apply-templates select="atom:title"/>
                        <xsl:apply-templates select="rdfa:meta[@property=$RDFA_ALTERNATIVE]"/>
                    </div>
                </div>
                <div class="metadata">
                    <div class="pad-sides">
                        <!-- description -->
                        <xsl:choose>
                            <xsl:when test="georss:point or georss:polygon">
                                <div class="desc-and-map">
                                    <div class="float-map">
                                        <div id="map" class="smallmap">
                                            <xsl:text> </xsl:text>
                                        </div>
                                        <script type="text/javascript">
                                            $(document).ready(function(){
                                            var initialData = '<xsl:copy-of select="georss:*"/>';
                                            var target = $('#map');
                                            var map = new MapEditor(target);
                                            if (initialData != '') {
                                            map.loadData(initialData);
                                            }
                                            target.prop('map', map);
                                            });
                                        </script>
                                    </div>
                                    <xsl:apply-templates select="atom:content"/>
                                </div>
                            </xsl:when>
                            <xsl:otherwise>
                                <xsl:apply-templates select="atom:content"/>
                            </xsl:otherwise>
                        </xsl:choose>

                        <h2>Data availability</h2>
                        <xsl:call-template name="websites"/>
                        <xsl:call-template name="mbox"/>
                        <xsl:call-template name="services"/>
                        <xsl:apply-templates select="rdfa:meta[@property=$RDFA_ACCESS_RIGHTS]"/>
                        <xsl:apply-templates select="atom:rights"/>

                        <h2>People and projects</h2>
                        <xsl:call-template name="creators"/>
                        <xsl:call-template name="managers"/>
                        <xsl:call-template name="projects"/>

                        <xsl:if test="georss:point or georss:polygon or atom:link[@rel=$ATOM_SPATIAL]">
                            <h2>Coverage</h2>
                            <!-- spatial -->
                            <xsl:call-template name="spatial"/>
                            <!-- temporal -->
                            <xsl:call-template name="temporal"/>
                        </xsl:if>

                        <xsl:if test="atom:category">
                            <h2>Topics</h2>
                            <xsl:call-template name="subjects"/>
                            <xsl:call-template name="keywords"/>
                        </xsl:if>

                        <xsl:if test="atom:link[@rel=$ATOM_IS_REFERENCED_BY] or atom:link[@rel=$ATOM_IS_ACCESSED_VIA]">
                            <h2>Related work</h2>
                            <xsl:call-template name="publications"/>
                            <xsl:call-template name="related-collections"/>
                        </xsl:if>

                        <div class="provenance">
                            <h2>About the description</h2>
                            <xsl:call-template name="description-id"/>
                            <xsl:apply-templates select="atom:source"/>
                            <xsl:call-template name="version-info"/>
                        </div>
                    </div>
                </div>
                <div class="content-bottom">
                    <div class="pad-sides pad-top pad-bottom">
                        <!-- bread crumbs -->
                        <ul class="bread-crumbs-nav">
                            <xsl:call-template name="bread-crumbs">
                                <xsl:with-param name="path">collections</xsl:with-param>
                                <xsl:with-param name="title">Collections</xsl:with-param>
                            </xsl:call-template>
                        </ul>
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
                    <p>Project/s</p>
                </div>
                <div class="content">
                    <xsl:apply-templates select="atom:link[@rel=$ATOM_IS_OUTPUT_OF]"/>
                </div>
            </div>
        </xsl:if>
    </xsl:template>

    <!-- related collections -->
    <xsl:template name="related-collections">
        <xsl:if test="atom:link[@rel=$REL_RELATED]">
            <div class="statement">
                <div class="property">
                    <p>Related collection/s</p>
                </div>
                <div class="content">
                    <xsl:apply-templates
                            select="atom:link[@rel=$REL_RELATED]"/>
                </div>
            </div>
        </xsl:if>
    </xsl:template>

    <!-- services -->
    <xsl:template name="services">
        <xsl:if test="atom:link[@rel=$ATOM_IS_ACCESSED_VIA]">
            <div class="statement">
                <div class="property">
                    <p>Access service/s</p>
                </div>
                <div class="content">
                    <xsl:apply-templates
                            select="atom:link[@rel=$ATOM_IS_ACCESSED_VIA]"/>
                </div>
            </div>
        </xsl:if>
    </xsl:template>


</xsl:stylesheet>
