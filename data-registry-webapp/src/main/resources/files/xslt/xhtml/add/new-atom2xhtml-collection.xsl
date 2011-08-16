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

    <xsl:include href="../include/header.xsl"/>
    <xsl:include href="../include/head.xsl"/>
    <xsl:include href="../include/footer.xsl"/>
    <xsl:include href="../edit/edit-common-xhtml.xsl"/>

    <xsl:output method="html" version="4.0"
                doctype-public="-//W3C//DTD HTML 4.01//EN"
                doctype-system="http://www.w3.org/TR/html4/strict.dtd"
                media-type="text/html;charset=utf-8" indent="yes"/>
    <xsl:template match="/">
        <html xmlns="http://www.w3.org/1999/xhtml" dir="ltr" xml:lang="en" lang="en">
            <xsl:call-template name="new-collectin"/>
        </html>
    </xsl:template>

    <xsl:template name="new-collectin">
        <head>
            <meta http-equiv="content-type" content="text/html;charset=UTF-8"/>
            <title>New Record</title>
            <link href="/description.css" rel="stylesheet" type="text/css"/>
            <xsl:call-template name="head"/>
            <script type="text/javascript" src="http://openlayers.org/dev/OpenLayers.js">;</script>
            <script type="text/javascript" src="http://maps.google.com/maps/api/js?v=3.2&amp;sensor=false">;</script>
            <script type="text/javascript" src="/js/map/map.js">;</script>
            <!--<script type="text/javascript"-->
            <!--src="http://maps.google.com/maps?file=api&amp;v=2&amp;key=ABQIAAAAjpkAC9ePGem0lIq5XcMiuhR_wWLPFku8Ix9i2SXYRVK3e45q1BQUd_beF8dtzKET_EteAjPdGDwqpQ">;</script>-->
        </head>
        <body onload="init();">
            <xsl:call-template name="header"/>
            <div class="wrapper">
                <ul class="bread-crumbs-nav">
                    <xsl:call-template name="edit-bread-crumbs">
                        <xsl:with-param name="path">collections</xsl:with-param>
                        <xsl:with-param name="title">Collections</xsl:with-param>
                    </xsl:call-template>
                </ul>
                <div id="ingest-error-msg">

                </div>
                <div id="edit-tabs">
                    <ul>
                        <li>
                            <a href="#general">
                                <span>General</span>
                            </a>
                        </li>
                        <li>
                            <a href="#related">
                                <span>People &amp; Projects</span>
                            </a>
                        </li>
                        <li>
                            <a href="#subjects">
                                <span>Subjects</span>
                            </a>
                        </li>
                        <li>
                            <a href="#spacio-temporal">
                                <span>Time &amp; Space</span>
                            </a>
                        </li>
                        <li>
                            <a href="#related-info">
                                <span>Related Info</span>
                            </a>
                        </li>
                        <li>
                            <a href="#rights">
                                <span>Rights</span>
                            </a>
                        </li>
                    </ul>
                    <div id="general">
                        <table id="edit-general-table" class="edit-table">
                            <tr>
                                <th>Title</th>
                                <td>
                                    <xsl:call-template name="title"/>
                                </td>
                            </tr>
                            <tr>
                                <th>Alternative Title</th>
                                <td>
                                    <xsl:call-template name="alternative-title"/>
                                </td>
                            </tr>
                            <tr>
                                <th>Type</th>
                                <td>
                                    <xsl:call-template name="type">
                                        <xsl:with-param name="entity">collection</xsl:with-param>
                                    </xsl:call-template>
                                </td>
                            </tr>
                            <tr>
                                <th>Description</th>
                                <td>
                                    <xsl:call-template name="text-area">
                                        <xsl:with-param name="field">content</xsl:with-param>
                                        <xsl:with-param name="path"/>
                                    </xsl:call-template>
                                </td>
                            </tr>
                            <tr>
                                <th>Webpage</th>
                                <td>
                                    <xsl:call-template name="page"/>
                                </td>
                            </tr>
                        </table>
                    </div>
                    <div id="related">
                        <fieldset class="ui-widget ui-widget-content">
                            <legend class="ui-widget-header ui-corner-all">Creators</legend>
                            <div class="field">
                                <xsl:call-template name="lookup-edit">
                                    <xsl:with-param name="field">creator</xsl:with-param>
                                    <xsl:with-param name="relation">
                                        <xsl:value-of select="$ATOM_CREATOR"/>
                                    </xsl:with-param>
                                </xsl:call-template>
                            </div>
                        </fieldset>
                        <fieldset class="ui-widget ui-widget-content">
                            <legend class="ui-widget-header ui-corner-all">Custodians/Contacts</legend>
                            <div class="field">
                                <xsl:call-template name="lookup-edit">
                                    <xsl:with-param name="field">publisher</xsl:with-param>
                                    <xsl:with-param name="relation">
                                        <xsl:value-of select="$ATOM_PUBLISHER"/>
                                    </xsl:with-param>
                                </xsl:call-template>
                            </div>
                        </fieldset>
                        <fieldset class="ui-widget ui-widget-content">
                            <legend class="ui-widget-header ui-corner-all">Projects</legend>
                            <div class="field">
                                <xsl:call-template name="lookup-edit">
                                    <xsl:with-param name="field">isoutputof</xsl:with-param>
                                    <xsl:with-param name="relation">
                                        <xsl:value-of select="$ATOM_IS_OUTPUT_OF"/>
                                    </xsl:with-param>
                                </xsl:call-template>
                            </div>
                        </fieldset>
                    </div>
                    <div id="subjects">
                        <fieldset class="ui-widget ui-widget-content">
                            <legend class="ui-widget-header ui-corner-all">Fields of Research</legend>
                            <div class="field">
                                <xsl:call-template name="edit-subject">
                                    <xsl:with-param name="scheme" select="$SCHEME_FOR"/>
                                    <xsl:with-param name="field" select="'field-of-research'"/>
                                </xsl:call-template>
                            </div>
                        </fieldset>
                        <fieldset class="ui-widget ui-widget-content">
                            <legend class="ui-widget-header ui-corner-all">Socio-economic Impact</legend>
                            <div class="field">
                                <xsl:call-template name="edit-subject">
                                    <xsl:with-param name="scheme" select="$SCHEME_SEO"/>
                                    <xsl:with-param name="field" select="'socio-economic-impact'"/>
                                </xsl:call-template>
                            </div>
                        </fieldset>
                        <fieldset class="ui-widget ui-widget-content">
                            <legend class="ui-widget-header ui-corner-all">Type of Activity</legend>
                            <div class="field">
                                <div id="type-of-activities">
                                    <xsl:call-template name="type-of-activities"/>
                                </div>
                            </div>
                        </fieldset>
                        <fieldset class="ui-widget ui-widget-content">
                            <legend class="ui-widget-header ui-corner-all">Keywords</legend>
                            <div class="field">
                                <xsl:call-template name="keywords"/>
                            </div>
                        </fieldset>
                    </div>
                    <div id="spacio-temporal">
                        <fieldset class="ui-widget ui-widget-content">
                            <legend class="ui-widget-header ui-corner-all">Time Period</legend>
                            <div class="field">
                                <xsl:call-template name="edit-time-period"/>
                            </div>
                        </fieldset>
                        <fieldset class="ui-widget ui-widget-content">
                            <legend class="ui-widget-header ui-corner-all">Locations</legend>
                            <div class="field">
                                <xsl:call-template name="edit-locations"/>
                            </div>
                        </fieldset>
                    </div>
                    <div id="related-info">
                        <fieldset class="ui-widget ui-widget-content">
                            <legend class="ui-widget-header ui-corner-all">Related Collections</legend>
                            <div class="field">
                                <xsl:call-template name="lookup-edit">
                                    <xsl:with-param name="field">relation</xsl:with-param>
                                    <xsl:with-param name="relation">
                                        <xsl:value-of select="$ATOM_RELATION"/>
                                    </xsl:with-param>
                                </xsl:call-template>
                            </div>
                        </fieldset>
                        <fieldset class="ui-widget ui-widget-content">
                            <legend class="ui-widget-header ui-corner-all">Related Services</legend>
                            <div class="field">
                                <xsl:call-template name="lookup-edit">
                                    <xsl:with-param name="field">isaccessedvia</xsl:with-param>
                                    <xsl:with-param name="relation">
                                        <xsl:value-of select="$ATOM_IS_ACCESSED_VIA"/>
                                    </xsl:with-param>
                                </xsl:call-template>
                            </div>
                        </fieldset>
                        <fieldset class="ui-widget ui-widget-content">
                            <legend class="ui-widget-header ui-corner-all">Related Publications</legend>
                            <div class="field">
                                <xsl:call-template name="edit-related-publications"/>
                            </div>
                        </fieldset>
                    </div>
                    <div id="rights">
                        <table id="edit-rights-table" class="edit-table">
                            <tr>
                                <th>Rights</th>
                                <td>
                                    <xsl:call-template name="text-area">
                                        <xsl:with-param name="field">rights</xsl:with-param>
                                        <xsl:with-param name="path"/>
                                    </xsl:call-template>
                                </td>
                            </tr>
                            <tr>
                                <th>Access Rights</th>
                                <td>
                                    <xsl:call-template name="text-area">
                                        <xsl:with-param name="field">access-rights</xsl:with-param>
                                        <xsl:with-param name="path"/>
                                    </xsl:call-template>
                                </td>
                            </tr>
                            <tr>
                                <th>License</th>
                                <td>
                                    <xsl:call-template name="licence-type"/>
                                </td>
                            </tr>
                        </table>
                    </div>
                </div>
                <div class="save-links-div">
                    <a href="#" class="save-link" id="save-link" title="Save Record"
                       onclick="DataSpace.ingestRecord('/collections','collection',true, false); return false;">save
                    </a>
                    <a href="#" class="publish-link" id="publish-link" title="Publish Record"
                       onclick="DataSpace.ingestRecord('/collections','collection',true, true); return false;">publish
                    </a>
                </div>
                <xsl:call-template name="lookup-form"/>
            </div>
            <xsl:call-template name="footer"/>
        </body>
    </xsl:template>
</xsl:stylesheet>
