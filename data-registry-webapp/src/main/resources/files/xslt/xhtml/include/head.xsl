<?xml version='1.0'?>
<!--
          Transforms UQ collection profile of Atom Syndication Format to
          XHTML with embedded RDFa

          XSLT 1.0

          Abdul Alabri, 2010-12

    -->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="http://www.w3.org/1999/xhtml">

    <xsl:output method="html" media-type="text/html;charset=utf-8" indent="yes"/>
    <xsl:template name="head">

        <link rel="stylesheet" href="/css/jquery-ui-1.8.14.custom.css" type="text/css"/>
        <link rel="stylesheet" type="text/css" href="/css/style.css"/>

        <script type="text/javascript" src="http://ajax.googleapis.com/ajax/libs/jquery/1.4/jquery.js">;</script>
        <script type="text/javascript" src="/js/jquery-ui-1.8.14.custom.min.js">;</script>

        <script type="text/javascript" src="/js/login.js">;</script>
        <script type="text/javascript" src="/js/scripts.js">;</script>

        <script type="text/javascript" src="/js/solr/search.js">;</script>
        <script type="text/javascript" src="/js/solr/core/Core.js">;</script>
        <script type="text/javascript" src="/js/solr/core/AbstractManager.js">;</script>
        <script type="text/javascript" src="/js/solr/managers/Manager.jquery.js">;</script>
        <script type="text/javascript" src="/js/solr/core/Parameter.js">;</script>
        <script type="text/javascript" src="/js/solr/core/ParameterStore.js">;</script>
        <script type="text/javascript" src="/js/solr/core/AbstractWidget.js">;</script>
        <script type="text/javascript" src="/js/solr/widgets/ResultWidget.js">;</script>
        <script type="text/javascript" src="/js/solr/helpers/jquery/ajaxsolr.theme.js">;</script>
        <!--<script type="text/javascript" src="/js/solr/widgets/reuters.theme.js">;</script>-->
        <script type="text/javascript" src="/js/solr/helpers/jquery/jquery.livequery.js">;</script>
        <script type="text/javascript" src="/js/solr/widgets/PagerWidget.js">;</script>
        <script type="text/javascript" src="/js/solr/core/AbstractFacetWidget.js">;</script>
        <script type="text/javascript" src="/js/solr/ext/jquery.autocomplete.js">;</script>
        <script type="text/javascript" src="/js/solr/widgets/AutocompleteWidget.js">;</script>
        <script type="text/javascript" src="/js/solr/helpers/ajaxsolr.support.js">;</script>
        <script type="text/javascript" src="/js/solr/helpers/ajaxsolr.theme.js">;</script>
    </xsl:template>
</xsl:stylesheet>