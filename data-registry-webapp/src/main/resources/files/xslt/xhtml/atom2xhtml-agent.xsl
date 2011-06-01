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

    <xsl:output method="html" media-type="text/html;charset=utf-8" indent="yes"/>
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
            <!-- the agent description itself -->
            <xsl:text>
            </xsl:text>
            <xsl:comment>Agent description</xsl:comment>
            <xsl:call-template name="header"/>
            <ul class="bread-crumbs-nav">
                <xsl:call-template name="bread-crumbs">
                    <xsl:with-param name="path">agents</xsl:with-param>
                    <xsl:with-param name="title">Agents</xsl:with-param>
                </xsl:call-template>
                <xsl:if test="$currentUser">
                    <xsl:call-template name="bread-crumbs-options">
                        <xsl:with-param name="path">agents</xsl:with-param>
                    </xsl:call-template>
                </xsl:if>
            </ul>
            <div class="wrapper">
                <div class="description">
                    <!-- names -->
                    <xsl:apply-templates select="atom:title"/>
                    <xsl:apply-templates select="rdfa:meta[@property=$RDFA_ALTERNATIVE]"/>
                    <!-- description -->
                    <xsl:apply-templates select="atom:content"/>
                    <!-- latest-version -->
                    <xsl:if test="$currentUser">
                        <xsl:call-template name="latest-version"/>
                    </xsl:if>
                    <!-- type -->
                    <xsl:call-template name="type"/>
                    <!-- email -->
                    <xsl:call-template name="mbox"/>
                    <!-- collections -->
                    <xsl:call-template name="collections"/>

                    <xsl:call-template name="isManagerOf"/>
                    <!-- activities -->
                    <xsl:call-template name="activities"/>
                    <!--pages-->
                    <xsl:call-template name="locations"/>
                    <!-- subjects -->
                    <xsl:call-template name="subjects"/>
                    <xsl:call-template name="keywords"/>
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

    <!-- mbox -->
    <xsl:template name="mbox">
        <xsl:if test="atom:link[@rel=$ATOM_MBOX]">
            <div class="statement">
                <div class="property">
                    <p>Email(s)</p>
                </div>
                <div class="content">
                    <xsl:apply-templates select="atom:link[@rel=$ATOM_MBOX]"/>
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
                    <p>Projects</p>
                </div>
                <div class="content">
                    <xsl:apply-templates select="atom:link[@rel=$ATOM_IS_PARTICIPANT_IN]"/>
                </div>
            </div>
        </xsl:if>
    </xsl:template>

</xsl:stylesheet>
