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
                xmlns:ands="http://www.ands.org.au/ontologies/ns/0.1/VITRO-ANDS.owl#"
                xmlns:rdfa="http://www.w3.org/ns/rdfa#" xmlns:georss="http://www.georss.org/georss"
                xmlns="http://www.w3.org/1999/xhtml"
                exclude-result-prefixes="rdf ore atom foaf dc dcterms dctype dcam cld ands rdfa georss">

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
            <link href="/description.css" rel="stylesheet" type="text/css"/>
        </head>
        <body>
            <!-- the collection description itself -->
            <xsl:text>
            </xsl:text>
            <xsl:comment>Collection description</xsl:comment>
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
                <xsl:apply-templates select="rdfa:meta[@property='http://purl.org/dc/terms/temporal']"/>
                <!-- subjects -->
                <xsl:call-template name="subjects"/>
                <!-- related info -->
                <xsl:call-template name="related"/>
                <!-- representations -->
                <xsl:call-template name="representations"/>
                <!-- metadata about the description -->
                <xsl:text>
                </xsl:text>
                <div class="clear"></div>
                <xsl:comment>Metadata about the description</xsl:comment>
                <div class="about">
                    <!-- publisher -->
                    <xsl:apply-templates
                            select="atom:category[@scheme = 'https://services.ands.org.au/home/orca/services/getRegistryObjectGroups.php']"/>

                    <!-- updated and updater -->
                    <xsl:call-template name="updated"/>
                </div>
            </div>

        </body>
    </xsl:template>

    <!-- *** collection description elements *** -->

    <!-- name -->
    <xsl:template match="atom:title">
        <h1>
            <xsl:value-of select="text()"/>
        </h1>
    </xsl:template>

    <!-- description -->
    <xsl:template match="atom:content">
        <p>
            <xsl:value-of select="text()"/>
        </p>
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

    <!-- creators -->
    <xsl:template name="creators">
        <xsl:if test="atom:link[@rel='http://purl.org/dc/terms/creator']">
            <div class="statement">
                <div class="property">
                    <p>Creator(s)</p>
                </div>
                <div class="content">

                    <xsl:apply-templates select="atom:link[@rel='http://purl.org/dc/terms/creator']"/>

                </div>
            </div>
        </xsl:if>
    </xsl:template>

    <!-- curators -->
    <xsl:template name="curators">
        <xsl:if test="atom:link[@rel='http://purl.org/dc/terms/publisher']">
            <div class="statement">
                <div class="property">
                    <p>Custodian(s)</p>
                </div>
                <div class="content">

                    <xsl:apply-templates select="atom:link[@rel='http://purl.org/dc/terms/publisher']"/>

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

    <!-- locations -->
    <xsl:template name="locations">
        <xsl:if test="atom:link[@rel='http://purl.org/cld/terms/isLocatedAt']">
            <div class="statement">
                <div class="property">
                    <p>Location</p>
                </div>
                <div class="content">

                    <xsl:apply-templates
                            select="atom:link[@rel='http://purl.org/cld/terms/isLocatedAt']"/>

                </div>
            </div>
        </xsl:if>
    </xsl:template>

    <!-- representations -->
    <xsl:template name="representations">
        <xsl:if test="atom:link[@rel='alternate']">
            <div class="statement">
                <div class="property">
                    <p>Representations</p>
                </div>
                <div class="content">
                    <xsl:apply-templates select="atom:link[@rel='alternate']"/>

                </div>
            </div>
        </xsl:if>
    </xsl:template>

    <!-- Rights -->
    <xsl:template match="rdfa:meta[@property='http://purl.org/dc/terms/accessRights']">
        <div class="statement">
            <div class="property">
                <p>Access rights</p>
            </div>
            <div class="content">
                <p>
                    <xsl:value-of select="@content"/>
                </p>
            </div>
        </div>
    </xsl:template>

    <xsl:template match="atom:rights">
        <div class="statement">
            <div class="property">
                <p>Rights</p>
            </div>
            <div class="content">
                <p>
                    <xsl:value-of select="text()"/>
                </p>
            </div>
        </div>
    </xsl:template>

    <!-- spatial -->
    <xsl:template name="spatial">
        <xsl:if test="georss">
            <div class="statement">
                <div class="property">
                    <p>Coverage</p>
                </div>
                <div class="content">
                    <xsl:apply-templates select="georss:point"/>
                    <xsl:apply-templates select="georss:polygon"/>
                    <xsl:apply-templates select="georss:featureName"/>
                </div>
            </div>
        </xsl:if>
    </xsl:template>

    <xsl:template match="georss:point">
        <p>
            <xsl:value-of select="text()"/>
        </p>
    </xsl:template>

    <xsl:template match="georss:polygon">
        <p>
            <xsl:value-of select="text()"/>
        </p>
    </xsl:template>

    <xsl:template match="georss:featureName">
        <p>
            <xsl:value-of select="text()"/>
        </p>
    </xsl:template>

    <!-- temporal -->
    <xsl:template match="rdfa:meta[@property='http://purl.org/dc/terms/temporal']">
        <div class="statement">
            <div class="property">
                <p>Coverage</p>
            </div>
            <div class="content">
                <p>
                    <xsl:value-of select="@content"/>
                </p>
            </div>
        </div>
    </xsl:template>

    <!-- subjects -->
    <xsl:template name="subjects">
        <div class="statement">
            <div class="property">
                <p>Subjects</p>
            </div>
            <div class="content">

                <xsl:apply-templates
                        select="atom:category[@scheme != 'http://purl.org/dc/dcmitype/' and @scheme!='https://services.ands.org.au/home/orca/services/getRegistryObjectGroups.php']"/>

            </div>
        </div>
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

    <!-- related info -->
    <xsl:template name="related">
        <xsl:if test="atom:link[@rel = 'related']">
            <div class="statement">
                <div class="property">
                    <p>Related information</p>
                </div>
                <div class="content">
                    <xsl:apply-templates select="atom:link[@rel = 'related']"/>
                </div>
            </div>
        </xsl:if>
    </xsl:template>
    <!-- Latest version -->
    <xsl:template name="latest-version">
        <xsl:if test="atom:link[@rel = 'latest-version']">
            <div class="statement">
                <div class="property">
                    <p>Latest Version</p>
                </div>
                <div class="content">
                    <xsl:apply-templates select="atom:link[@rel = 'latest-version']"/>
                </div>
            </div>
        </xsl:if>
    </xsl:template>


    <!-- displayed links -->
    <xsl:template
            match="atom:link[@rel='http://purl.org/dc/terms/creator' or @rel='http://purl.org/dc/terms/publisher' or @rel='http://www.ands.org.au/ontologies/ns/0.1/VITRO-ANDS.owl#isOutputOf' or @rel='http://purl.org/cld/terms/isLocatedAt' or @rel='related' or @rel='alternate' or @rel='latest-version']">
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


    <!-- *** description of the metadata itself -->


    <!-- description publisher -->
    <xsl:template
            match="atom:category[@scheme='https://services.ands.org.au/home/orca/services/getRegistryObjectGroups.php']">
        <p>Description published by
            <xsl:value-of select="@term"/>
            <xsl:apply-templates select="//atom:source"/>
        </p>
    </xsl:template>

    <!-- source -->
    <!-- TO DO: check if id starts with HTTP before making it a link -->
    <xsl:template match="atom:source">via
        <a href="{atom:id}">
            <xsl:value-of select="atom:title"
                    />
        </a>
    </xsl:template>

    <!-- updated -->
    <xsl:template name="updated">
        <p>Last updated
            <xsl:value-of select="atom:updated"/> by
            <xsl:value-of
                    select="atom:author/atom:name"/>
        </p>
    </xsl:template>

    <!-- link relations: TO DO -->

</xsl:stylesheet>
