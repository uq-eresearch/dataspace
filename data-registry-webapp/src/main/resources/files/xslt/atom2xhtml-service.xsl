<?xml version='1.0'?>
<!--
          Transforms UQ collection profile of Atom Syndication Format to
          XHTML with embedded RDFa

          XSLT 1.0

          Nigel Ward, 2010-12

    -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                xmlns:ore="http://www.openarchives.org/ore/terms/" xmlns:atom="http://www.w3.org/2005/Atom"
                xmlns:foaf="http://xmlns.com/foaf/0.1/" xmlns:dc="http://purl.org/dc/elements/1.1/"
                xmlns:dcterms="http://purl.org/dc/terms/" xmlns:dctype="http://purl.org/dc/dcmitype/"
                xmlns:dcam="http://purl.org/dc/dcam/" xmlns:cld="http://purl.org/cld/terms/"
                xmlns:uqdata="http://dataspace.metadata.net/"
                xmlns:ands="http://www.ands.org.au/ontologies/ns/0.1/VITRO-ANDS.owl#"
                xmlns:rdfa="http://www.w3.org/ns/rdfa#" xmlns="http://www.w3.org/1999/xhtml"
                xmlns:georss="http://www.georss.org/georss">

    <xsl:output method="xml" media-type="application/xhtml+xml" indent="yes"/>

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
            <link href="collection.css" rel="stylesheet" type="text/css"/>
        </head>
        <body>
            <!-- the collection description itself -->
            <xsl:text>
            </xsl:text>
            <xsl:comment>Collection description</xsl:comment>
            <dl>
                <!-- type and title -->
                <xsl:call-template name="heading"/>
                <!-- description -->
                <xsl:apply-templates select="atom:content"/>
                <!-- creators -->
                <xsl:call-template name="creators"/>
                <!-- curators -->
                <xsl:call-template name="curators"/>
                <!-- projects -->
                <xsl:call-template name="projects"/>
                <!-- location -->
                <xsl:call-template name="locations"/>
                <!-- rights -->
                <xsl:apply-templates
                        select="rdfa:meta[@property='http://purl.org/dc/terms/accessRights']"/>
                <xsl:apply-templates select="atom:rights"/>
                <!-- spatial -->
                <xsl:apply-templates select="georss:point"/>
                <xsl:apply-templates select="georss:polygon"/>
                <!-- temporal -->
                <xsl:apply-templates
                        select="rdfa:meta[@property='http://purl.org/dc/terms/temporal']"/>
                <!-- subjects -->
                <xsl:call-template name="subjects"/>
                <!-- related info -->
                <xsl:call-template name="related"/>
            </dl>

            <!-- metadata about the description -->
            <xsl:text>
            </xsl:text>
            <xsl:comment>Metadata about the description</xsl:comment>
            <h2>Description</h2>
            <dl>
                <xsl:apply-templates
                        select="atom:category[@scheme = 'https://services.ands.org.au/home/orca/services/getRegistryObjectGroups.php']"/>
                <xsl:apply-templates select="atom:source"/>
                <xsl:call-template name="updated"/>
            </dl>

        </body>
    </xsl:template>

    <!-- *** collection description elements *** -->
    <!-- object type and title -->
    <xsl:template name="heading">
        <li>
            <dt>
                <xsl:value-of select="atom:category[@scheme='http://purl.org/dc/dcmitype/']/@label"
                        />
            </dt>
            <dd>
                <h1>
                    <xsl:value-of select="atom:title"/>
                </h1>
            </dd>
        </li>
    </xsl:template>

    <!-- description -->
    <xsl:template match="atom:content">
        <li>
            <dt>Description</dt>
            <dd>
                <xsl:value-of select="text()"/>
            </dd>
        </li>
    </xsl:template>

    <!-- creators -->
    <xsl:template name="creators">
        <li>
            <dt>Creator(s)</dt>
            <xsl:apply-templates select="atom:link[@rel='http://purl.org/dc/terms/creator']"/>
        </li>
    </xsl:template>

    <!-- curators -->
    <xsl:template name="curators">
        <li>
            <dt>Curator(s)</dt>
            <xsl:apply-templates select="atom:link[@rel='http://purl.org/dc/terms/publisher']"/>
        </li>
    </xsl:template>

    <!-- projects -->
    <xsl:template name="projects">
        <li>
            <dt>Project (s)</dt>
            <xsl:apply-templates
                    select="atom:link[@rel='http://www.ands.org.au/ontologies/ns/0.1/VITRO-ANDS.owl#isOutputOf']"
                    />
        </li>
    </xsl:template>

    <!-- locations -->
    <xsl:template name="locations">
        <li>
            <dt>Location</dt>
            <xsl:apply-templates select="atom:link[@rel='http://purl.org/cld/terms/isLocatedAt']"/>
        </li>
    </xsl:template>

    <!-- Rights -->
    <xsl:template match="rdfa:meta[@property='http://purl.org/dc/terms/accessRights']">
        <li>
            <dt>Access rights</dt>
            <dd>
                <xsl:value-of select="@content"/>
            </dd>
        </li>
    </xsl:template>

    <xsl:template match="atom:rights">
        <li>
            <dt>Rights</dt>
            <dd>
                <xsl:value-of select="text()"/>
            </dd>
        </li>
    </xsl:template>

    <!-- spatial -->
    <xsl:template match="georss:point">
        <li>
            <dt>Coverage</dt>
            <dd>
                <xsl:value-of select="text()"/>
            </dd>
        </li>
    </xsl:template>
    <xsl:template match="georss:polygon">
        <li>
            <dt>Coverage</dt>
            <dd>
                <xsl:value-of select="text()"/>
            </dd>
        </li>
    </xsl:template>

    <!-- temporal -->
    <xsl:template match="rdfa:meta[@property='http://purl.org/dc/terms/temporal']">
        <li>
            <dt>Temporal coverage</dt>
            <dd>
                <xsl:value-of select="@content"/>
            </dd>
        </li>
    </xsl:template>

    <!-- subjects -->
    <xsl:template name="subjects">
        <li>
            <dt>Subjects</dt>
            <xsl:apply-templates
                    select="atom:category[@scheme != 'http://purl.org/dc/dcmitype/' and @scheme!='https://services.ands.org.au/home/orca/services/getRegistryObjectGroups.php']"
                    />
        </li>
    </xsl:template>

    <xsl:template
            match="atom:category[@scheme != 'http://purl.org/dc/dcmitype/' and @scheme!='https://services.ands.org.au/home/orca/services/getRegistryObjectGroups.php']">
        <dd>
            <xsl:choose>
                <xsl:when test="@label">
                    <xsl:value-of select="@label"/>
                </xsl:when>
                <xsl:otherwise>
                    <xsl:value-of select="@term"/>
                </xsl:otherwise>
            </xsl:choose>
        </dd>
    </xsl:template>

    <!-- related info -->
    <xsl:template name="related">
        <li>
            <dt>Related information</dt>
            <xsl:apply-templates select="atom:link[@rel = 'related']"/>
        </li>
    </xsl:template>


    <!-- displayed links -->
    <xsl:template
            match="atom:link[@rel='http://purl.org/dc/terms/creator' or @rel='http://purl.org/dc/terms/publisher' or @rel='http://www.ands.org.au/ontologies/ns/0.1/VITRO-ANDS.owl#isOutputOf' or @rel='http://purl.org/cld/terms/isLocatedAt' or @rel='related']">
        <dd>
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
        </dd>
    </xsl:template>


    <!-- *** description of the metadata itself -->


    <!-- description publisher -->
    <xsl:template
            match="atom:category[@scheme='https://services.ands.org.au/home/orca/services/getRegistryObjectGroups.php']">
        <li>
            <dt>Published by</dt>
            <dd>
                <xsl:value-of select="@term"/>
            </dd>
        </li>
    </xsl:template>

    <!-- source -->
    <!-- TO DO: check if id starts with HTTP before macking it a link -->
    <xsl:template match="atom:source">
        <li>
            <dt>Source</dt>
            <dd>
                <a href="{atom:id}">
                    <xsl:value-of select="atom:title"/>
                </a>
            </dd>
        </li>
    </xsl:template>

    <!-- updated -->
    <xsl:template name="updated">
        <li>
            <dt>updated</dt>
            <dd>
                <xsl:value-of select="atom:updated"/> by
                <xsl:value-of select="atom:author/atom:name"/>
            </dd>
        </li>
    </xsl:template>


    <!-- alternate formats for description -->
    <xsl:template match="atom:link[@rel = 'alternate']">
        <xsl:if test="@href">
            <dcterms:hasFormat>
                <rdf:Description rdf:about="{@href}">
                    <xsl:if test="@type">
                        <dcterms:format>
                            <xsl:value-of select="@type"/>
                        </dcterms:format>
                    </xsl:if>
                </rdf:Description>
            </dcterms:hasFormat>
        </xsl:if>
    </xsl:template>

</xsl:stylesheet>
