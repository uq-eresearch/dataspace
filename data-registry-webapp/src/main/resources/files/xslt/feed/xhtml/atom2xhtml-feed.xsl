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
                xmlns:fn="http://www.w3.org/2005/xpath-functions"
                xmlns="http://www.w3.org/1999/xhtml"
                exclude-result-prefixes="atom app fn">

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
                :
                <xsl:value-of select="atom:title"/>
            </title>
            <link href="/css/description.css" rel="stylesheet" type="text/css"/>
            <link rel="alternate" type="application/atom+xml" title="{atom:title}"
                  href="{atom:link[@rel = $REL_SELF]/@href}?repr=application/atom+xml"/>
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
                            <li class="bread-crumbs-last">
                                <xsl:value-of select="atom:title"/>
                            </li>
                        </ul>
                        <xsl:call-template name="browse-tab-nav">
                            <xsl:with-param name="selected" select="atom:title"/>
                        </xsl:call-template>
                        <h1><xsl:value-of select="atom:title"/></h1>
                    </div>
                </div>

                <div id="feed">
                    <div class="pad-sides pad-bottom">
                        <div class="button-bar">
                            <div class="actions">
                                <xsl:if test="$currentUser">
                                    <a class="button-bar-button" href="{concat(atom:id, '?v=new')}">New</a>
                                </xsl:if>
                                <a id="subscribe-link" class="button-bar-button" href="{atom:link[@type = $TYPE_ATOM_FEED]/@href}">
                                    <img src="/images/icons/rss16px.png" alt="Subscribe to this feed"/> Feed
                                </a>
                            </div>
                        </div>
                        <xsl:call-template name="paging"/>
                        <xsl:apply-templates select="atom:entry"/>
                        <xsl:call-template name="paging"/>
                    </div>
                </div>

                <div class="content-bottom">
                    <div class="pad-sides pad-top pad-bottom">
                        <!-- bread crumbs -->
                        <ul class="bread-crumbs-nav">
                            <li class="bread-crumbs">
                                <a href="/">Home</a>
                            </li>
                            <li class="bread-crumbs-last">
                                <xsl:value-of select="atom:title"/>
                            </li>
                        </ul>
                    </div>
                </div>
            </div>
            <xsl:call-template name="footer"/>
        </body>
    </xsl:template>

    <xsl:template name="browse-tab-nav">
        <xsl:param name="selected"/>
        <ul class="browse-tab-nav">
            <xsl:call-template name="browse-tab">
                <xsl:with-param name="selected" select="$selected"/>
                <xsl:with-param name="title">Collections</xsl:with-param>
            </xsl:call-template>
            <xsl:call-template name="browse-tab">
                <xsl:with-param name="selected" select="$selected"/>
                <xsl:with-param name="title">Agents</xsl:with-param>
            </xsl:call-template>
            <xsl:call-template name="browse-tab">
                <xsl:with-param name="selected" select="$selected"/>
                <xsl:with-param name="title">Activities</xsl:with-param>
            </xsl:call-template>
            <xsl:call-template name="browse-tab">
                <xsl:with-param name="selected" select="$selected"/>
                <xsl:with-param name="title">Services</xsl:with-param>
            </xsl:call-template>
        </ul>
    </xsl:template>

    <xsl:template name="browse-tab">
        <xsl:param name="title"/>
        <xsl:param name="selected"/>
        <li class="browse-tab">
            <xsl:if test="$selected = $title">
                <xsl:attribute name="id">selected-browse-tab</xsl:attribute>
            </xsl:if>
            <a href="/{fn:lower-case($title)}">
                <xsl:call-template name="entity-icon-type">
                    <xsl:with-param name="type" select="fn:lower-case($title)"/>
                    <xsl:with-param name="colour" select="if ($selected = $title) then 'white' else 'black'"/>
                </xsl:call-template>
                <xsl:value-of select="$title"/>
            </a>
        </li>
    </xsl:template>

    <xsl:template name="paging">
        <xsl:if test="atom:link[@rel = $REL_PREVIOUS] or atom:link[@rel = $REL_NEXT]">
            <xsl:variable name="current" select="substring-after(atom:link[@rel = $REL_CURRENT]/@href, 'page=')"/>
            <div class="paging">
                <xsl:choose>
                    <xsl:when test="atom:link[@rel = $REL_PREVIOUS]">
                        <a href="{atom:link[@rel = $REL_FIRST]}">
                            <img src="/images/icons/start_active.png" alt="first"/>
                        </a>
                        <a href="{atom:link[@rel = $REL_PREVIOUS]/@href}">
                            <img src="/images/icons/back_active.png" alt="previous"/>
                        </a>
                        <a href="{atom:link[@rel = $REL_PREVIOUS]/@href}" class="page-number">
                            <xsl:value-of select="number($current) - 1"/>
                        </a>
                    </xsl:when>
                    <xsl:otherwise>
                        <img src="/images/icons/start_inactive.png" alt="first"/>
                        <img src="/images/icons/back_inactive.png" alt="previous"/>
                    </xsl:otherwise>
                </xsl:choose>
                <span class="current-page">
                    <xsl:value-of select="$current"/>
                </span>
                <xsl:choose>
                    <xsl:when test="atom:link[@rel = $REL_NEXT]">
                        <a href="{atom:link[@rel = $REL_NEXT]/@href}" class="page-number">
                            <xsl:value-of select="number($current) + 1"/>
                        </a>
                        <a href="{atom:link[@rel = $REL_NEXT]/@href}">
                            <img src="/images/icons/forward_active.png" alt="next"/>
                        </a>
                    </xsl:when>
                    <xsl:otherwise>
                        <img src="/images/icons/forward_inactive.png" alt="next"/>
                    </xsl:otherwise>
                </xsl:choose>
            </div>
        </xsl:if>
    </xsl:template>

    <xsl:template match="atom:entry">
        <div class="record">
            <h2>
                <xsl:call-template name="entity-icon">
                    <xsl:with-param name="colour">black</xsl:with-param>
                </xsl:call-template>
                <a href="{atom:id}"><xsl:value-of select="atom:title"/></a>
            </h2>
            <xsl:if test="atom:author/atom:name">
                <p>
                    by <span class="author"><xsl:value-of select="atom:author/atom:name"/></span>
                </p>
            </xsl:if>
            <p>
                <xsl:value-of select="substring(atom:content, 0, 255)"/>
                <xsl:if test="fn:string-length(atom:content) > 255">
                    <a href="{atom:id}">
                        more...
                    </a>
                </xsl:if>
            </p>
            <p>
                updated: <xsl:value-of select="fn:format-dateTime(fn:adjust-dateTime-to-timezone(atom:updated),
                '[F,3-3], [D] [MNn] [Y], [H]:[m]:[s] [z]')"/>
                <xsl:if test="$currentUser and app:control/app:draft='yes'">
                    <span class="unpublished"> (unpublished version available)</span>
                </xsl:if>
            </p>

            <xsl:if test="$currentUser">
                <div class="controls">
                    <a  class="button-bar-button" href="{atom:id}" title="View Record">view</a>
                    <a  class="button-bar-button" href="{atom:id}?v=edit" title="Edit Record">edit</a>
                    <a  class="button-bar-button" href="#" onclick="DataSpace.deleteRecord('{atom:id}'); "
                        title="Delete Record">delete</a>
                </div>
            </xsl:if>
        </div>
    </xsl:template>

</xsl:stylesheet>
