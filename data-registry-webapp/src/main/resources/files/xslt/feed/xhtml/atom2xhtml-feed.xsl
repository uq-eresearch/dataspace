<?xml version='1.0'?>
<!--
          Transforms UQ collection profile of Atom Syndication Format to
          XHTML with embedded RDFa

          XSLT 1.0

          Nigel Ward, 2010-12

    -->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:atom="http://www.w3.org/2005/Atom"
                xmlns:app="http://www.w3.org/2007/app"
                xmlns="http://www.w3.org/1999/xhtml"
                exclude-result-prefixes="atom app">

    <xsl:include href="../../xhtml/include/header.xsl"/>
    <xsl:include href="../../xhtml/include/head.xsl"/>
    <xsl:include href="../../xhtml/include/footer.xsl"/>
    <xsl:include href="../../constants.xsl"/>

    <xsl:output method="html" version="4.0"
                doctype-public="-//W3C//DTD HTML 4.01//EN"
                doctype-system="http://www.w3.org/TR/html4/strict.dtd"
                media-type="text/html;charset=utf-8" indent="yes"/>
    <xsl:template match="/">
        <html xmlns="http://www.w3.org/1999/xhtml" dir="ltr" xml:lang="en" lang="en">
            <xsl:apply-templates/>
        </html>
    </xsl:template>

    <!-- *** Atom entry ***-->

    <xsl:template match="atom:feed">
        <head>
            <title>
                <xsl:value-of select="$applicationName"/>
            </title>
            <link href="/css/description.css" rel="stylesheet" type="text/css"/>
            <link rel="alternate" type="application/atom+xml" title="{atom:title}"
                  href="{atom:link[@rel = $REL_SELF]/@href}"/>
            <xsl:call-template name="head"/>
        </head>
        <body>
            <xsl:call-template name="header"/>
            <div class="wrapper">
                <div class="content-top">
                    <div class="pad-top pad-sides">
                        <ul class="bread-crumbs-nav">
                            <li class="bread-crumbs">
                                <a href="/">Home</a>
                            </li>
                            <li class="bread-crumbs">
                                <a href="/browse">Browse</a>
                            </li>
                            <li class="bread-crumbs-last">
                                <xsl:value-of select="atom:title"/>
                            </li>
                            <li class="bread-crumbs-options">
                                <xsl:if test="$currentUser">
                                    <a id="new-record-link" href="{atom:id}?v=new" title="Add Record">new</a>
                                    <xsl:text> </xsl:text>
                                </xsl:if>

                            </li>
                        </ul>
                        <div class="button-bar">
                            <div class="actions">
                                <a id="subscribe-link" class="button-bar-button" href="{atom:link[@type = $TYPE_ATOM_FEED]/@href}">
                                    <img src="/images/icons/rss16px.png" alt="Subscribe to this feed"/> Feed
                                </a>
                            </div>
                        </div>
                        <h1>Browse <xsl:value-of select="atom:title"/></h1>
                    </div>
                </div>

                <div id="feed">
                    <div class="pad-sides">
                        <xsl:apply-templates select="atom:entry"/>
                    </div>
                </div>
            </div>
            <xsl:call-template name="footer"/>
        </body>
    </xsl:template>

    <xsl:template match="atom:entry">
        <div class="record">
            <h2>
                <xsl:call-template name="entity-icon"/>
                <a href="{atom:id}"><xsl:value-of select="atom:title"/></a>
            </h2>
            <xsl:if test="atom:author/atom:name">
                <p>
                    by <span class="author"><xsl:value-of select="atom:author/atom:name"/></span>
                </p>
                <p>
                    <xsl:value-of select="substring(atom:content, 0, 255)"/>
                    <a href="{atom:id}">
                        more...
                    </a>
                </p>
            </xsl:if>

            <xsl:choose>
                <xsl:when test="$currentUser">
                    <xsl:if test="app:control/app:draft='yes'">
                        <span class="draft">(draft)</span>
                    </xsl:if>
                    <xsl:if test="atom:author/atom:name">
                        <span>
                            <xsl:text> by </xsl:text>
                        </span>
                        <span class="author">
                            <xsl:value-of select="atom:author/atom:name"/>
                        </span>
                    </xsl:if>
                    <br/>
                    <xsl:value-of select="substring(atom:content, 0, 255)"/>
                    <a href="{atom:id}">
                        more...
                    </a>
                    <span class="record-date">
                        <xsl:value-of select="atom:updated"/>
                    </span>
                    <div class="controls">
                        <a id="view-record-link" href="{atom:id}" title="View Record">view</a>
                        <a id="view-record-working-copy-link" href="{atom:id}/working-copy" title="View Working Copy">
                            working copy
                        </a>
                        <a id="view-record-history-link" href="{atom:id}/version-history" title="View Record History">
                            history
                        </a>
                        <a id="edit-record-link" href="{atom:id}?v=edit" title="Edit Record">edit</a>
                        <a id="delete-record-link" href="#" onclick="deleteRecord('{atom:id}'); " title="Delete Record">
                            delete
                        </a>
                    </div>
                </xsl:when>
                <xsl:otherwise>
                    <a href="{atom:id}">
                        <xsl:value-of select="atom:title"/>
                    </a>
                    <xsl:if test="atom:author/atom:name">
                        <span>
                            <xsl:text> by </xsl:text>
                        </span>
                        <span class="author">
                            <xsl:value-of select="atom:author/atom:name"/>
                        </span>
                    </xsl:if>
                    <br/>
                    <xsl:value-of select="substring(atom:content, 0, 255)"/>
                    <a href="{atom:id}">
                        more...
                    </a>
                    <span class="record-date">
                        <xsl:value-of select="atom:updated"/>
                    </span>
                </xsl:otherwise>
            </xsl:choose>
        </div>
    </xsl:template>

</xsl:stylesheet>
