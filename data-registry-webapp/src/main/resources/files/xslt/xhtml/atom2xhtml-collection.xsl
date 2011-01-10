<?xml version='1.0'?>
<!--
          Transforms UQ collection profile of Atom Syndication Format to
          XHTML with embedded RDFa

          XSLT 1.0

          Nigel Ward, 2010-12

    -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:ore="http://www.openarchives.org/ore/terms/" xmlns:atom="http://www.w3.org/2005/Atom"
                xmlns:foaf="http://xmlns.com/foaf/0.1/" xmlns:dc="http://purl.org/dc/elements/1.1/"
                xmlns:dcterms="http://purl.org/dc/terms/" xmlns:dctype="http://purl.org/dc/dcmitype/"
                xmlns:dcam="http://purl.org/dc/dcam/" xmlns:cld="http://purl.org/cld/terms/"
                xmlns:ands="http://www.ands.org.au/ontologies/ns/0.1/VITRO-ANDS.owl#"
                xmlns:rdfa="http://www.w3.org/ns/rdfa#"
                xmlns="http://www.w3.org/1999/xhtml"
                exclude-result-prefixes="ore atom foaf dc dcterms dctype dcam cld ands rdfa">

    <xsl:include href="common.xsl"/>
    <xsl:include href="include/header.xsl"/>
    <xsl:include href="include/head.xsl"/>
    <xsl:include href="include/footer.xsl"/>

    <xsl:output method="html" media-type="application/xhtml+xml" indent="yes"/>
    <xsl:template match="/">
        <html>
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
                    <xsl:apply-templates select="rdfa:meta[@property='http://purl.org/dc/terms/accessRights']"/>
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
                        <xsl:apply-templates
                                select="atom:category[@scheme = 'https://services.ands.org.au/home/orca/services/getRegistryObjectGroups.php']"/>

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
        <xsl:if test="atom:category[@scheme='http://purl.org/dc/dcmitype/']">
            <div class="statement">
                <div class="property">
                    <p>Type</p>
                </div>
                <div class="content">
                    <p>
                        <xsl:value-of
                                select="atom:category[@scheme='http://purl.org/dc/dcmitype/']/@label"/>
                    </p>
                </div>
            </div>
        </xsl:if>
    </xsl:template>

    <!-- projects -->
    <xsl:template name="projects">
        <xsl:if test="atom:link[@rel='http://www.ands.org.au/ontologies/ns/0.1/VITRO-ANDS.owl#isOutputOf']">
            <div class="statement">
                <div class="property">
                    <p>Project (s)</p>
                </div>
                <div class="content">

                    <xsl:apply-templates
                            select="atom:link[@rel='http://www.ands.org.au/ontologies/ns/0.1/VITRO-ANDS.owl#isOutputOf']"/>

                </div>
            </div>
        </xsl:if>
    </xsl:template>

    <!-- services -->
    <xsl:template name="services">
        <xsl:if test="atom:link[@rel='http://purl.org/cld/terms/isAccessedVia']">
            <div class="statement">
                <div class="property">
                    <p>Accessed Via</p>
                </div>
                <div class="content">
                    <xsl:apply-templates
                            select="atom:link[@rel='http://purl.org/cld/terms/isAccessedVia']"/>
                </div>
            </div>
        </xsl:if>
    </xsl:template>

    <xsl:template
            match="atom:category[@scheme != 'http://purl.org/dc/dcmitype/' and @scheme!='https://services.ands.org.au/home/orca/services/getRegistryObjectGroups.php']">
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


    <!-- displayed links -->
    <xsl:template
            match="atom:link[@rel='http://purl.org/dc/terms/creator'
            or @rel='http://purl.org/dc/terms/publisher'
            or @rel='http://purl.org/cld/terms/isAccessedVia'
            or @rel='http://www.ands.org.au/ontologies/ns/0.1/VITRO-ANDS.owl#isOutputOf'
            or @rel='http://purl.org/cld/terms/isLocatedAt'
            or @rel='related'
            or @rel='alternate'
            or @rel='latest-version']">
        <p>
            <a href="{@href}">
                <xsl:choose>
                    <xsl:when test="@title">
                        <xsl:value-of select="@title"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="@href"/>
                    </xsl:otherwise>
                </xsl:choose>
            </a>
        </p>
    </xsl:template>
</xsl:stylesheet>
