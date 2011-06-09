<?xml version='1.0'?>
<!--
          Transforms UQ collection profile of Atom Syndication Format to
          XHTML with embedded RDFa

          XSLT 1.0

          Abdul Alabri, 2011-01

    -->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                xmlns:ore="http://www.openarchives.org/ore/terms/" xmlns:atom="http://www.w3.org/2005/Atom"
                xmlns:foaf="http://xmlns.com/foaf/0.1/" xmlns:dc="http://purl.org/dc/elements/1.1/"
                xmlns:dcterms="http://purl.org/dc/terms/" xmlns:dctype="http://purl.org/dc/dcmitype/"
                xmlns:dcam="http://purl.org/dc/dcam/" xmlns:cld="http://purl.org/cld/terms/"
                xmlns:ands="http://www.ands.org.au/ontologies/ns/0.1/VITRO-ANDS.owl#"
                xmlns:rdfa="http://www.w3.org/ns/rdfa#" xmlns:georss="http://www.georss.org/georss"
                xmlns="http://www.w3.org/1999/xhtml"
                exclude-result-prefixes="rdf ore atom foaf dc dcterms dctype dcam cld ands rdfa georss">
    <xsl:include href="../constants.xsl"/>
    <!--<xsl:param name="currentUser"/>-->
    <xsl:output method="html" media-type="text/html;charset=utf-8" indent="yes"/>

    <!-- name -->
    <xsl:template match="atom:title">
        <h1>
            <xsl:value-of select="text()"/>
        </h1>
    </xsl:template>
    <!-- alternative names -->
    <xsl:template match="rdfa:meta[@property=$RDFA_ALTERNATIVE]">
        <p>
            <xsl:value-of select="@content"/>
        </p>
    </xsl:template>

    <!-- description -->
    <xsl:template match="atom:content">
        <p>
            <xsl:value-of select="text()"/>
        </p>
    </xsl:template>
    <!-- object type -->
    <xsl:template name="type">
        <xsl:if test="atom:link[@rel=$REL_TYPE]">
            <div class="statement">
                <div class="property">
                    <p>Type</p>
                </div>
                <div class="content">
                    <p>
                        <xsl:value-of select="atom:link[@rel=$REL_TYPE]/@title"/>
                    </p>
                </div>
            </div>
        </xsl:if>
    </xsl:template>
    <!-- mbox -->
    <xsl:template name="mbox">
        <xsl:if test="atom:link[@rel=$ATOM_MBOX]">
            <div class="statement">
                <div class="property">
                    <p>Email(s)</p>
                </div>
                <div class="content">
                    <xsl:apply-templates select="atom:link[@rel=$ATOM_MBOX]"/>
                </div>
            </div>
        </xsl:if>
    </xsl:template>

    <!-- Latest version -->
    <xsl:template name="latest-version">
        <xsl:if test="atom:link[@rel = $REL_LATEST_VERSION]">
            <div class="statement">
                <div class="property">
                    <p>Latest Version</p>
                </div>
                <div class="content">
                    <xsl:apply-templates select="atom:link[@rel = $REL_LATEST_VERSION]"/>
                </div>
            </div>
        </xsl:if>
    </xsl:template>

    <!-- updated -->
    <xsl:template name="updated">
        <p>Last updated
            <xsl:value-of select="atom:updated"/> by
            <xsl:value-of select="atom:source/atom:author/atom:name"/>
        </p>
    </xsl:template>

    <!-- representations -->
    <xsl:template name="representations">
        <xsl:if test="atom:link[@rel=$REL_ALTERNATE]">
            <p class="alternate">Download as:
                <span>
                    <xsl:apply-templates select="atom:link[@rel=$REL_ALTERNATE]"/>
                </span>
            </p>
        </xsl:if>
    </xsl:template>

    <!-- Rights -->
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
    <!-- published -->
    <xsl:template match="atom:published">
        <div class="statement">
            <div class="property">
                <p>Published</p>
            </div>
            <div class="content">
                <p>
                    <xsl:value-of select="text()"/>
                </p>
            </div>
        </div>
    </xsl:template>
    <xsl:template match="rdfa:meta[@property=$RDFA_ACCESS_RIGHTS]">
        <div class="statement">
            <div class="property">
                <p>Access</p>
            </div>
            <div class="content">
                <p>
                    <xsl:value-of select="@content"/>
                </p>
            </div>
        </div>
    </xsl:template>

    <!-- related info -->
    <xsl:template name="related">
        <xsl:if test="atom:link[@rel = $REL_RELATED]">
            <div class="statement">
                <div class="property">
                    <p>Related information</p>
                </div>
                <div class="content">
                    <xsl:apply-templates select="atom:link[@rel = $REL_RELATED]"/>
                </div>
            </div>
        </xsl:if>
    </xsl:template>

    <!-- creators -->
    <xsl:template name="creators">
        <div class="statement">
            <div class="property">
                <p>Creator(s)</p>
            </div>
            <div class="content">
                <xsl:apply-templates select="atom:author"/>
            </div>
        </div>
    </xsl:template>
    <xsl:template match="atom:author">
        <p>
            <a href="{atom:uri}">
                <xsl:value-of select="atom:name"/>
            </a>
        </p>
    </xsl:template>

    <!-- curators -->
    <xsl:template name="curators">
        <xsl:if test="atom:link[@rel=$ATOM_PUBLISHER]">
            <div class="statement">
                <div class="property">
                    <p>Custodian(s)</p>
                </div>
                <div class="content">
                    <xsl:apply-templates select="atom:link[@rel=$ATOM_PUBLISHER]"/>
                </div>
            </div>
        </xsl:if>
    </xsl:template>

    <!-- locations -->
    <xsl:template name="locations">
        <xsl:if test="atom:link[@rel=$ATOM_IS_LOCATED_AT]">
            <div class="statement">
                <div class="property">
                    <p>Pages(s)</p>
                </div>
                <div class="content">
                    <xsl:apply-templates select="atom:link[@rel=$ATOM_IS_LOCATED_AT]"/>
                </div>
            </div>
        </xsl:if>
    </xsl:template>
    <!-- locations -->
    <xsl:template name="agent-publications">
        <xsl:if test="atom:link[@rel=$ATOM_PUBLICATIONS]">
            <div class="statement">
                <div class="property">
                    <p>Publication(s)</p>
                </div>
                <div class="content">
                    <xsl:apply-templates select="atom:link[@rel=$ATOM_PUBLICATIONS]"/>
                </div>
            </div>
        </xsl:if>
    </xsl:template>
    <!-- locations -->
    <xsl:template name="publications">
        <xsl:if test="atom:link[@rel=$ATOM_IS_REFERENCED_BY]">
            <div class="statement">
                <div class="property">
                    <p>Publication(s)</p>
                </div>
                <div class="content">
                    <xsl:apply-templates select="atom:link[@rel=$ATOM_IS_REFERENCED_BY]"/>
                </div>
            </div>
        </xsl:if>
    </xsl:template>

    <!-- source -->
    <!-- TO DO: check if id starts with HTTP before making it a link -->
    <xsl:template match="atom:source">via
        <a href="{atom:id}">
            <xsl:value-of select="atom:title"/>
        </a>
    </xsl:template>

    <!-- description publisher -->
    <xsl:template
            name="description-publisher">
        <p>Description published by
            <xsl:apply-templates select="atom:source/atom:link[@rel = $ATOM_PUBLISHER]"/>
        </p>
    </xsl:template>

    <!-- subjects -->
    <xsl:template name="subjects">
        <xsl:if test="atom:category[@scheme]">
            <div class="statement">
                <div class="property">
                    <p>Subject(s)</p>
                </div>
                <div class="content">
                    <xsl:apply-templates select="atom:category[@scheme]"/>
                </div>
            </div>
        </xsl:if>
    </xsl:template>
    <xsl:template
            match="atom:category[@scheme]">
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

    <!-- keywords -->
    <xsl:template name="keywords">
        <xsl:if test="atom:category[not(@scheme)]">
            <div class="statement">
                <div class="property">
                    <p>Keyword(s)</p>
                </div>
                <div class="content">
                    <p>
                        <xsl:apply-templates select="atom:category[not(@scheme)]"/>
                    </p>
                </div>
            </div>
        </xsl:if>
    </xsl:template>
    <xsl:template match="atom:category[not(@scheme)]">
        <span>
            <a href="/search?q={@term}">
                <xsl:value-of select="@term"/>
            </a>
        </span>
    </xsl:template>

    <!-- spatial -->
    <xsl:template name="spatial">
        <xsl:apply-templates select="georss:point"/>
        <xsl:apply-templates select="georss:polygon"/>
        <xsl:if test="atom:link[@rel=$ATOM_SPATIAL]">
            <div class="statement">
                <div class="property">
                    <p>Spatial Coverage</p>
                </div>
                <div class="content">
                    <xsl:apply-templates select="atom:link[@rel=$ATOM_SPATIAL]"/>
                </div>
            </div>
        </xsl:if>
    </xsl:template>

    <xsl:template match="georss:point">
        <input type="hidden" class="georss-point" value="{text()}"/>
    </xsl:template>

    <xsl:template match="georss:polygon">
        <input type="hidden" class="georss-polygon" value="{text()}"/>
    </xsl:template>

    <xsl:template match="atom:link[@rel=$ATOM_SPATIAL]">
        <p>
            <a href="{@href}">
                <xsl:value-of select="@title"/>
            </a>
        </p>
    </xsl:template>

    <!-- temporal -->
    <xsl:template name="temporal">
        <xsl:if test="rdfa:meta[@property=$RDFA_TEMPORAL]">
            <div class="statement">
                <div class="property">
                    <p>Temporal coverage</p>
                </div>
                <div class="content">
                    <xsl:apply-templates select="rdfa:meta[@property=$RDFA_TEMPORAL]"/>

                </div>
            </div>
        </xsl:if>
    </xsl:template>

    <xsl:template match="rdfa:meta[@property=$RDFA_TEMPORAL]">
        <p>
            <xsl:value-of select="@content"/>
        </p>
    </xsl:template>

    <xsl:template name="record-id">
        <xsl:if test="atom:link[@rel=$REL_LATEST_VERSION]">
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
        </xsl:if>
    </xsl:template>

    <!-- displayed links -->
    <xsl:template
            match="atom:link[@rel=$ATOM_CREATOR
            or @rel=$ATOM_IS_ACCESSED_VIA
            or @rel=$ATOM_IS_OUTPUT_OF
            or @rel=$ATOM_IS_SUPPORTED_BY
            or @rel=$ATOM_HAS_PARTICIPANT
            or @rel=$ATOM_HAS_OUTPUT
            or @rel=$ATOM_IS_LOCATED_AT
            or @rel=$ATOM_PUBLICATIONS
            or @rel=$ATOM_IS_COLLECTOR_OF
            or @rel=$ATOM_IS_MANAGER_OF
            or @rel=$ATOM_IS_MANAGED_BY
            or @rel=$ATOM_MANAGES_SERVICE
            or @rel=$ATOM_IS_PARTICIPANT_IN
            or @rel=$ATOM_MBOX
            or @rel=$REL_RELATED
            or @rel=$ATOM_PUBLISHER
            or @rel=$ATOM_IS_REFERENCED_BY
            or @rel=$REL_LATEST_VERSION]">
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
    <xsl:template
            match="atom:link[@rel=$REL_ALTERNATE]">

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
        <xsl:text> </xsl:text>
    </xsl:template>

    <xsl:template name="bread-crumbs-options">
        <xsl:param name="path"/>
        <li class="bread-crumbs-options">
            <a id="new-record-link" href="/{$path}?v=new" title="Add Record">new</a>
            <xsl:text> </xsl:text>
            <a id="edit-record-link" href="{atom:link[@rel=$REL_SELF]/@href}?v=edit" title="Edit Record">edit</a>
            <xsl:text> </xsl:text>
            <a id="delete-record-link" href="#" onclick="deleteRecord('{atom:link[@rel=$REL_SELF]/@href}'); "
               title="Delete Record">
                delete
            </a>
        </li>
    </xsl:template>

    <xsl:template name="bread-crumbs">
        <xsl:param name="path"/>
        <xsl:param name="title"/>
        <li class="bread-crumbs">
            <a href="/">Home</a>
        </li>
        <li class="bread-crumbs">
            <a href="/browse">Browse</a>
        </li>
        <li class="bread-crumbs">
            <a href="/{$path}">
                <xsl:value-of select="$title"/>
            </a>
        </li>
        <li class="bread-crumbs-last">
            <xsl:if test="atom:link[@rel = $REL_SELF]/@href">
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
    </xsl:template>


</xsl:stylesheet>