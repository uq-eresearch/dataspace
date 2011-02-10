<?xml version='1.0'?>
<!--
          Transforms UQ collection profile of Atom Syndication Format to
          XHTML with embedded RDFa

          XSLT 1.0

          Nigel Ward, 2010-12

    -->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:atom="http://www.w3.org/2005/Atom"
                xmlns="http://www.w3.org/1999/xhtml"
                exclude-result-prefixes="atom">

    <xsl:include href="common-feed-xhtml.xsl"/>
    <xsl:include href="../../xhtml/include/header.xsl"/>
    <xsl:include href="../../xhtml/include/head.xsl"/>
    <xsl:include href="../../xhtml/include/footer.xsl"/>

    <xsl:output method="html" media-type="application/xhtml+xml" indent="yes"/>
    <xsl:template match="/">
        <html>
            <xsl:apply-templates/>
        </html>
    </xsl:template>

    <!-- *** Atom entry ***-->

    <xsl:template match="atom:feed">
        <head>
            <title>
                <xsl:value-of select="atom:title"/>
            </title>
            <link href="/description.css" rel="stylesheet" type="text/css"/>
            <xsl:call-template name="head"/>
        </head>
        <body>
            <xsl:call-template name="header"/>
            <div class="wrapper">
                <div class="description">
                    <div id="result">
                        <h2>
                            <a href="{atom:id}">
                                <xsl:apply-templates select="atom:title"/>
                            </a>
                        </h2>
                        <xsl:apply-templates select="atom:entry"/>
                    </div>
                </div>
            </div>
            <xsl:call-template name="footer"/>
        </body>
    </xsl:template>

</xsl:stylesheet>
