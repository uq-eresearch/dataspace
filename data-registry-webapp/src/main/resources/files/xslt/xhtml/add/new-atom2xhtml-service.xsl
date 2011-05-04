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

    <xsl:include href="../edit/edit-common-xhtml.xsl"/>
    <xsl:include href="../include/header.xsl"/>
    <xsl:include href="../include/head.xsl"/>
    <xsl:include href="../include/footer.xsl"/>

    <xsl:output method="html" media-type="application/xhtml+xml" omit-xml-declaration="yes"
                doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
                doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" indent="yes"/>
    <xsl:template match="/">
        <html xmlns="http://www.w3.org/1999/xhtml" dir="ltr" xml:lang="en" lang="en">
            <xsl:call-template name="new-service"/>
        </html>
    </xsl:template>

    <xsl:template name="new-service">
        <head>
            <title>New Service</title>
            <link href="/description.css" rel="stylesheet" type="text/css"/>
            <xsl:call-template name="head"/>
        </head>
        <body>
            <xsl:call-template name="header"/>
            <ul class="bread-crumbs-nav">
                <xsl:call-template name="edit-bread-crumbs">
                    <xsl:with-param name="path">services</xsl:with-param>
                    <xsl:with-param name="title">Services</xsl:with-param>
                </xsl:call-template>
            </ul>
            <div class="wrapper">
                <div id="edit-tabs">
                    <ul>
                        <li>
                            <a href="#general">
                                <span>General</span>
                            </a>
                        </li>
                        <li>
                            <a href="#related">
                                <span>Related</span>
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
                                        <xsl:with-param name="entity">service</xsl:with-param>
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
                            <legend class="ui-widget-header ui-corner-all">Publisher</legend>
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
                            <legend class="ui-widget-header ui-corner-all">Supported By</legend>
                            <div class="field">
                                <xsl:call-template name="lookup-edit">
                                    <xsl:with-param name="field">issupportedby</xsl:with-param>
                                    <xsl:with-param name="relation">
                                        <xsl:value-of select="$ATOM_IS_SUPPORTED_BY"/>
                                    </xsl:with-param>
                                </xsl:call-template>
                            </div>
                        </fieldset>
                    </div>
                </div>
                <div class="save-links-div">
                    <a href="#" class="save-link" id="save-link" title="Save Record"
                       onclick="ingestRecord('/services','service',true, false); return false;">save
                    </a>
                    <a href="#" class="publish-link" id="publish-link" title="Publish Record"
                       onclick="ingestRecord('/services','service',true, true); return false;">publish
                    </a>
                </div>
                <xsl:call-template name="lookup-form"/>
            </div>
            <xsl:call-template name="footer"/>
        </body>
    </xsl:template>
</xsl:stylesheet>
