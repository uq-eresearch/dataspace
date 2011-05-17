<?xml version='1.0'?>
<!--
          Transforms UQ collection profile of Atom Syndication Format to
          XHTML with embedded RDFa

          XSLT 1.0

          Abdul Alabri, 2010-12

    -->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="http://www.w3.org/1999/xhtml">

    <xsl:output method="html" media-type="application/xhtml+xml" omit-xml-declaration="yes"
                doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
                doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" indent="yes"/>
    <xsl:template name="head">

        <link rel="stylesheet" type="text/css" href="/css/style.css"/>
        <link href="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/themes/base/jquery-ui.css" rel="stylesheet"
              type="text/css"/>
        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4/jquery.js">;</script>
        <script type="text/javascript"
                src="http://ajax.googleapis.com/ajax/libs/jqueryui/1.8/jquery-ui.min.js">;</script>
        <script type="text/javascript" src="/jquery/login.js">;</script>
        <script type="text/javascript" src="/jquery/scripts.js">;</script>


        <script type="text/javascript" src="/js/solr/search.js">;</script>
        <script type="text/javascript" src="/js/solr/core/Core.js">;</script>
        <script type="text/javascript" src="/js/solr/core/AbstractManager.js">;</script>
        <script type="text/javascript" src="/js/solr/managers/Manager.jquery.js">;</script>
        <script type="text/javascript" src="/js/solr/core/Parameter.js">;</script>
        <script type="text/javascript" src="/js/solr/core/ParameterStore.js">;</script>
        <script type="text/javascript" src="/js/solr/core/AbstractWidget.js">;</script>
        <script type="text/javascript" src="/js/solr/widgets/ResultWidget.js">;</script>
        <script type="text/javascript" src="/js/solr/helpers/jquery/ajaxsolr.theme.js">;</script>
        <script type="text/javascript" src="/js/solr/widgets/reuters.theme.js">;</script>
        <script type="text/javascript" src="/js/solr/helpers/jquery/jquery.livequery.js">;</script>
        <script type="text/javascript" src="/js/solr/widgets/PagerWidget.js">;</script>
        <script type="text/javascript" src="/js/solr/core/AbstractFacetWidget.js">;</script>
        <script type="text/javascript" src="/js/solr/ext/jquery.autocomplete.js">;</script>
        <script type="text/javascript" src="/js/solr/widgets/AutocompleteWidget.js">;</script>
        <script type="text/javascript" src="/js/solr/helpers/ajaxsolr.support.js">;</script>
        <script type="text/javascript" src="/js/solr/helpers/ajaxsolr.theme.js">;</script>
    </xsl:template>
</xsl:stylesheet>