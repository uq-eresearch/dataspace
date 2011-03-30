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
            <xsl:call-template name="new-agent"/>
        </html>
    </xsl:template>

    <!-- *** Atom entry ***-->

    <xsl:template name="new-agent">
        <head>
            <title>New Agent</title>
            <link href="/description.css" rel="stylesheet" type="text/css"/>
            <xsl:call-template name="head"/>
        </head>
        <body>
            <xsl:call-template name="header"/>
            <ul class="bread-crumbs-nav">
                <xsl:call-template name="edit-bread-crumbs">
                    <xsl:with-param name="path">agents</xsl:with-param>
                    <xsl:with-param name="title">Agents</xsl:with-param>
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
                                <span>Projects</span>
                            </a>
                        </li>
                        <li>
                            <a href="#subjects">
                                <span>Subjects</span>
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
                                        <xsl:with-param name="entity">agent</xsl:with-param>
                                    </xsl:call-template>
                                </td>
                            </tr>
                            <tr>
                                <th>Description</th>
                                <td>
                                    <xsl:call-template name="content"/>
                                </td>
                            </tr>
                            <tr>
                                <th>Email</th>
                                <td>
                                    <xsl:call-template name="email"/>
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
                            <legend class="ui-widget-header ui-corner-all">Created</legend>
                            <div class="field">
                                <xsl:call-template name="lookup-edit">
                                    <xsl:with-param name="field">iscollectorof</xsl:with-param>
                                    <xsl:with-param name="relation">
                                        <xsl:value-of select="$ATOM_IS_COLLECTOR_OF"/>
                                    </xsl:with-param>
                                </xsl:call-template>
                            </div>
                        </fieldset>
                        <fieldset class="ui-widget ui-widget-content">
                            <legend class="ui-widget-header ui-corner-all">Participating In</legend>
                            <div class="field">
                                <xsl:call-template name="lookup-edit">
                                    <xsl:with-param name="field">isparticipantin</xsl:with-param>
                                    <xsl:with-param name="relation">
                                        <xsl:value-of select="$ATOM_IS_PARTICIPANT_IN"/>
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
                                    <xsl:with-param name="scheme" select="for"/>
                                    <xsl:with-param name="type" select="field-of-research"/>
                                </xsl:call-template>
                            </div>
                        </fieldset>
                        <fieldset class="ui-widget ui-widget-content">
                            <legend class="ui-widget-header ui-corner-all">Socio-economic Impact</legend>
                            <div class="field">
                                <xsl:call-template name="edit-subject">
                                    <xsl:with-param name="scheme" select="sei"/>
                                    <xsl:with-param name="type" select="socio-economic-impact"/>
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
                </div>
                <div class="save-links-div">
                    <a href="#" class="save-link" id="save-link" title="Save Record">save</a>
                    <a href="#" class="publish-link" id="publish-link" title="Publish Record">publish</a>
                </div>
                <xsl:call-template name="lookup-form"/>
            </div>
            <xsl:call-template name="footer"/>
        </body>
    </xsl:template>
</xsl:stylesheet>
