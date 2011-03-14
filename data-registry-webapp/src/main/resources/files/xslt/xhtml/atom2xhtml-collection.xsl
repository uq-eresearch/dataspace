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

    <xsl:include href="common-xhtml.xsl"/>
    <xsl:include href="include/header.xsl"/>
    <xsl:include href="include/head.xsl"/>
    <xsl:include href="include/footer.xsl"/>

    <xsl:output method="html" media-type="application/xhtml+xml" omit-xml-declaration="yes"
                doctype-public="-//W3C//DTD XHTML 1.0 Strict//EN"
                doctype-system="http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd" indent="yes"/>
    <xsl:template match="/">
        <html xmlns="http://www.w3.org/1999/xhtml" dir="ltr" xml:lang="en" lang="en">
            <xsl:apply-templates/>
        </html>
    </xsl:template>

    <!-- *** Atom entry ***-->

    <xsl:template match="atom:entry">
        <head>
            <title>
                <xsl:value-of select="atom:title"/>
            </title>
            <link href="/description.css" rel="stylesheet" type="text/css"/>
            <xsl:call-template name="head"/>
        </head>
        <body>
            <!-- the collection description itself -->
            <xsl:text>
            </xsl:text>
            <xsl:comment>Collection description</xsl:comment>
            <xsl:call-template name="header"/>
            <ul class="bread-crumbs-nav">
                <li class="bread-crumbs">
                    <a href="/">Browse</a>
                    >>
                    <a href="/collections">Collections</a>
                    <xsl:if test="atom:link[@rel = $REL_SELF]/@href">
                        >>
                        <a href="{atom:link[@rel = $REL_SELF]/@href}">
                            <xsl:choose>
                                <xsl:when test="atom:link[@rel=$REL_SELF]/@title">
                                    <xsl:value-of select="atom:link[@rel=$REL_SELF]/@title"/>
                                </xsl:when>
                                <xsl:otherwise>
                                    <xsl:value-of select="atom:link[@rel=$REL_SELF]/@href"/>
                                </xsl:otherwise>
                            </xsl:choose>
                        </a>
                    </xsl:if>
                </li>
                <xsl:if test="$currentUser">
                    <li class="bread-crumbs-options">
                        <a id="new-record-link" href="#" title="Add Record">new</a>
                        <a id="edit-record-link" href="#" title="Edit Record">edit</a>
                        <a id="delete-record-link" href="#" onclick="deleteRecord('{atom:id}'); " title="Delete Record">
                            delete
                        </a>
                    </li>
                </xsl:if>
            </ul>
            <div class="wrapper">
                <div class="description">
                    <!-- name -->
                    <xsl:apply-templates select="atom:title"/>
                    <!-- description -->
                    <xsl:apply-templates select="atom:content"/>
                    <!-- latest-version -->
                    <xsl:call-template name="latest-version"/>
                    <!-- type -->
                    <xsl:call-template name="type"/>
                    <!-- creators -->
                    <xsl:call-template name="creators"/>
                    <!-- curators -->
                    <xsl:call-template name="curators"/>
                    <!-- projects -->
                    <xsl:call-template name="projects"/>
                    <!-- services -->
                    <xsl:call-template name="services"/>
                    <!-- location -->
                    <xsl:call-template name="locations"/>
                    <!-- rights -->
                    <xsl:apply-templates select="rdfa:meta[@property=$RDFA_ACCESS_RIGHTS]"/>
                    <xsl:apply-templates select="atom:rights"/>
                    <!-- spatial -->
                    <xsl:call-template name="spatial"/>
                    <!-- temporal -->
                    <xsl:call-template name="temporal"/>
                    <!-- subjects -->
                    <xsl:call-template name="subjects"/>
                    <!-- related info -->
                    <xsl:call-template name="related"/>
                    <!-- representations -->
                    <xsl:call-template name="representations"/>
                    <!-- metadata about the description -->
                <xsl:text>
                </xsl:text>
                    <xsl:comment>Metadata about the description</xsl:comment>
                    <div class="about">
                        <!-- publisher -->
                        <xsl:apply-templates select="atom:category[@scheme = $NS_GROUP]"/>
                        <!-- updated and updater -->
                        <xsl:call-template name="updated"/>
                    </div>
                </div>
            </div>
            <xsl:call-template name="footer"/>
        </body>
    </xsl:template>

    <!-- object type -->
    <xsl:template name="type">
        <xsl:if test="atom:category[@term=$ENTITY_COLLECTION]">
            <div class="statement">
                <div class="property">
                    <p>Type</p>
                </div>
                <div class="content">
                    <p>
                        <xsl:value-of select="atom:category[@term=$ENTITY_COLLECTION]/@label"/>
                    </p>
                </div>
            </div>
        </xsl:if>
    </xsl:template>

    <!-- projects -->
    <xsl:template name="projects">
        <xsl:if test="atom:link[@rel=$ATOM_IS_OUTPUT_OF]">
            <div class="statement">
                <div class="property">
                    <p>Project (s)</p>
                </div>
                <div class="content">
                    <xsl:apply-templates select="atom:link[@rel=$ATOM_IS_OUTPUT_OF]"/>
                </div>
            </div>
        </xsl:if>
    </xsl:template>

    <!-- services -->
    <xsl:template name="services">
        <xsl:if test="atom:link[@rel=$ATOM_IS_ACCESSED_VIA]">
            <div class="statement">
                <div class="property">
                    <p>Accessed Via</p>
                </div>
                <div class="content">
                    <xsl:apply-templates
                            select="atom:link[@rel=$ATOM_IS_ACCESSED_VIA]"/>
                </div>
            </div>
        </xsl:if>
    </xsl:template>

    <xsl:template
            match="atom:category[@scheme != $NS_DCMITYPE
            and @scheme!=$NS_GROUP]">
        <p>
            <xsl:choose>
                <xsl:when test="@label">
                    <xsl:value-of select="@label"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="@term"/>
                </xsl:otherwise>
            </xsl:choose>
        </p>
    </xsl:template>

</xsl:stylesheet>
