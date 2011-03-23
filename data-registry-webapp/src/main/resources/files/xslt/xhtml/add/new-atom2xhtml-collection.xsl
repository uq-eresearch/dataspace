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

    <xsl:include href="new-common-xhtml.xsl"/>
    <xsl:include href="../include/header.xsl"/>
    <xsl:include href="../include/head.xsl"/>
    <xsl:include href="../include/footer.xsl"/>

    <xsl:output method="html" media-type="application/xhtml+xml" omit-xml-declaration="yes"
                doctype-public="-//W3C//DTD XHTML 1.0 Transitional//EN"
                doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" indent="yes"/>
    <xsl:template match="/">
        <html xmlns="http://www.w3.org/1999/xhtml" dir="ltr" xml:lang="en" lang="en">
            <xsl:call-template name="content"/>
        </html>
    </xsl:template>

    <xsl:template name="content">
        <head>
            <title>New Record</title>
            <link href="/description.css" rel="stylesheet" type="text/css"/>
            <xsl:call-template name="head"/>
            <script type="text/javascript" src="http://maps.google.com/maps/api/js?sensor=true">;</script>
            <script type="text/javascript">
                var shape;

                function initialize() {
                    var mapDiv = document.getElementById('map-canvas');
                    var map = new google.maps.Map(mapDiv, {
                        center: new google.maps.LatLng(24.886436490787712, -70.2685546875),
                        zoom: 4,
                        mapTypeId: google.maps.MapTypeId.ROADMAP
                    });

                    shape = new google.maps.Polygon({
                        strokeColor: '#ff0000',
                        strokeOpacity: 0.8,
                        strokeWeight: 2,
                        fillColor: '#ff0000',
                        fillOpacity: 0.35
                    });

                    shape.setMap(map);

                    google.maps.event.addListener(map, 'click', addPoint);
                }

                function addPoint(e) {
                    var vertices = shape.getPath();

                    vertices.push(e.latLng);
                }


                google.maps.event.addDomListener(window, 'load', initialize);
            </script>
        </head>
        <body>
            <xsl:call-template name="header"/>
            <ul class="bread-crumbs-nav">
                <xsl:call-template name="edit-bread-crumbs">
                    <xsl:with-param name="path">collections</xsl:with-param>
                    <xsl:with-param name="title">Collections</xsl:with-param>
                </xsl:call-template>
            </ul>
            <div class="wrapper">
                <div id="edit-tabs">
                    <ul>
                        <li>
                            <a href="#fragment-1">
                                <span>General</span>
                            </a>
                        </li>
                        <li>
                            <a href="#fragment-2">
                                <span>People &amp; Projects</span>
                            </a>
                        </li>
                        <li>
                            <a href="#fragment-3">
                                <span>Subjects</span>
                            </a>
                        </li>
                        <li>
                            <a href="#fragment-4">
                                <span>Time &amp; Space</span>
                            </a>
                        </li>
                        <li>
                            <a href="#fragment-5">
                                <span>Related Info</span>
                            </a>
                        </li>
                        <li>
                            <a href="#fragment-6">
                                <span>Rights</span>
                            </a>
                        </li>
                    </ul>
                    <div id="fragment-1">
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
                                    <xsl:call-template name="collection-type"/>
                                </td>
                            </tr>
                            <tr>
                                <th>Description</th>
                                <td>
                                    <xsl:call-template name="description"/>
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
                    <div id="fragment-2">
                        <fieldset class="ui-widget ui-widget-content">
                            <legend class="ui-widget-header ui-corner-all">Creators</legend>
                            <div class="field">
                                <xsl:call-template name="edit-creators"/>
                            </div>
                        </fieldset>
                        <fieldset class="ui-widget ui-widget-content">
                            <legend class="ui-widget-header ui-corner-all">Custodians/Contacts</legend>
                            <div class="field">
                                <xsl:call-template name="edit-custodians"/>
                            </div>
                        </fieldset>
                        <fieldset class="ui-widget ui-widget-content">
                            <legend class="ui-widget-header ui-corner-all">Projects</legend>
                            <div class="field">
                                <xsl:call-template name="edit-projects"/>
                            </div>
                        </fieldset>
                    </div>
                    <div id="fragment-3">
                        <fieldset class="ui-widget ui-widget-content">
                            <legend class="ui-widget-header ui-corner-all">Fields of Research</legend>
                            <div class="field">
                                <xsl:call-template name="edit-fields-of-research"/>
                            </div>
                        </fieldset>
                        <fieldset class="ui-widget ui-widget-content">
                            <legend class="ui-widget-header ui-corner-all">Socio-economic Impact</legend>
                            <div class="field">
                                <xsl:call-template name="edit-socio-economic-impact"/>
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
                    <div id="fragment-4">
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
                    <div id="fragment-5">
                        <fieldset class="ui-widget ui-widget-content">
                            <legend class="ui-widget-header ui-corner-all">Related Collections</legend>
                            <div class="field">
                                <xsl:call-template name="edit-related-collections"/>
                            </div>
                        </fieldset>
                        <fieldset class="ui-widget ui-widget-content">
                            <legend class="ui-widget-header ui-corner-all">Related Services</legend>
                            <div class="field">
                                <xsl:call-template name="edit-related-services"/>
                            </div>
                        </fieldset>
                        <fieldset class="ui-widget ui-widget-content">
                            <legend class="ui-widget-header ui-corner-all">Related Publications</legend>
                            <div class="field">
                                <xsl:call-template name="edit-related-publications"/>
                            </div>
                        </fieldset>
                    </div>
                    <div id="fragment-6">
                        <table id="edit-rights-table" class="edit-table">
                            <tr>
                                <th>Rights</th>
                                <td>
                                    <xsl:call-template name="rights"/>
                                </td>
                            </tr>
                            <tr>
                                <th>Access Rights</th>
                                <td>
                                    <xsl:call-template name="access-rights"/>
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
                    <a href="#" class="save-link" id="save-link" title="Save Record">save</a>
                    <a href="#" class="publish-link" id="publish-link" title="Publish Record">publish</a>
                </div>
            </div>
            <xsl:call-template name="footer"/>
        </body>
    </xsl:template>
</xsl:stylesheet>
