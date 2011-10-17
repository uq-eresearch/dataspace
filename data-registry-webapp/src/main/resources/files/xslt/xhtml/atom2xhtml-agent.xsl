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
                xmlns:fn="http://www.w3.org/2005/xpath-functions"
                xmlns="http://www.w3.org/1999/xhtml"
                exclude-result-prefixes="atom rdfa fn">
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
                <xsl:value-of select="fn:concat($applicationName, ': ', atom:title)"/>
            </title>
            <link href="/css/description.css" rel="stylesheet" type="text/css"/>
            <xsl:call-template name="head"/>
        </head>
        <body>
            <!-- the agent description itself -->
            <xsl:text>
            </xsl:text>
            <xsl:comment>Agent description</xsl:comment>
            <xsl:call-template name="header"/>
            <div class="wrapper">
                <div class="content-top">
                    <div class="pad-top pad-sides">
                        <!-- bread crumbs -->
                        <ul class="bread-crumbs-nav">
                            <xsl:call-template name="bread-crumbs">
                                <xsl:with-param name="path">agents</xsl:with-param>
                                <xsl:with-param name="title">Agents</xsl:with-param>
                            </xsl:call-template>
                        </ul>
                        <!-- buttons -->
                        <div class="button-bar">
                            <xsl:call-template name="button-bar">
                                <xsl:with-param name="path">agents</xsl:with-param>
                            </xsl:call-template>
                        </div>
                        <!-- identifier -->
                        <xsl:call-template name="identifiers"/>
                        <!-- names -->
                        <xsl:apply-templates select="atom:title"/>
                        <xsl:apply-templates select="rdfa:meta[@property=$RDFA_ALTERNATIVE]"/>
                    </div>
                </div>
                <div class="metadata">
                    <div class="pad-sides">
                        <!-- description -->
                        <xsl:apply-templates select="atom:content"/>

                        <h2>General information</h2>
                        <xsl:call-template name="websites"/>
                        <xsl:call-template name="mbox"/>
                        <xsl:call-template name="agent-publications"/>
                        <xsl:call-template name="activities"/>

                        <h2>Data</h2>
                        <xsl:call-template name="collections"/>
                        <xsl:call-template name="isManagerOf"/>

                        <xsl:if test="atom:category">
                            <h2>Topics</h2>
                            <xsl:call-template name="subjects"/>
                            <xsl:call-template name="keywords"/>
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
                                <xsl:with-param name="path">agents</xsl:with-param>
                                <xsl:with-param name="title">Agents</xsl:with-param>
                            </xsl:call-template>
                        </ul>
                    </div>
                </div>
            </div>
            <xsl:call-template name="footer"/>
        </body>
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
                    <p>Project/s</p>
                </div>
                <div class="content">
                    <xsl:apply-templates select="atom:link[@rel=$ATOM_IS_PARTICIPANT_IN]"/>
                </div>
            </div>
        </xsl:if>
    </xsl:template>
    <!-- Services -->
    <xsl:template name="manages-services">
        <xsl:if test="atom:link[@rel=$ATOM_MANAGES_SERVICE]">
            <div class="statement">
                <div class="property">
                    <p>Manage</p>
                </div>
                <div class="content">
                    <xsl:apply-templates select="atom:link[@rel=$ATOM_MANAGES_SERVICE]"/>
                </div>
            </div>
        </xsl:if>
    </xsl:template>

    <!-- agent publications page -->
    <xsl:template name="agent-publications">
        <xsl:if test="atom:link[@rel=$ATOM_PUBLICATIONS]">
            <div class="statement">
                <div class="property">
                    <p>Publications</p>
                </div>
                <div class="content">
                    <xsl:apply-templates select="atom:link[@rel=$ATOM_PUBLICATIONS]"/>
                </div>
            </div>
        </xsl:if>
    </xsl:template>

</xsl:stylesheet>
