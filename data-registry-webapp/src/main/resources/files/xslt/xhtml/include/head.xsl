<?xml version='1.0'?>
<!--
          Transforms UQ collection profile of Atom Syndication Format to
          XHTML with embedded RDFa

          XSLT 1.0

          Abdul Alabri, 2010-12

    -->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="http://www.w3.org/1999/xhtml">

    <xsl:output method="html" media-type="application/xhtml+xml" indent="yes"/>
    <xsl:template name="head">
        <link rel="stylesheet" type="text/css" href="/css/style.css"/>
        <script type="text/javascript" src="/jquery/jquery.js">;</script>
        <script type="text/javascript" src="/jquery/jquery.jfeed.js">;</script>
        <script type="text/javascript" src="/jquery/login.js">;</script>
    </xsl:template>
</xsl:stylesheet>