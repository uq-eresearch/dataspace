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
                xmlns:fn="http://www.w3.org/2005/xpath-functions"
                xmlns="http://www.w3.org/1999/xhtml"
                exclude-result-prefixes="rdf ore atom foaf dc dcterms dctype dcam cld ands rdfa georss fn">
    <xsl:include href="../constants.xsl"/>

    <!--<xsl:param name="currentUser"/>-->
    <xsl:output method="html" media-type="text/html;charset=utf-8" indent="yes"/>

    <!-- identifiers -->
    <xsl:template name="identifiers">
        <p class="identifier">
            <span class="id-property">Identifier: </span>
            <a href="{atom:id}"><xsl:value-of select="atom:id"/></a>
        </p>
        <xsl:if test="atom:id != atom:link[@rel = $RDF_DESCRIBES]/@href">
            <p class="identifier">
                <span class="id-property">Local identifier: </span>
                <a href="{atom:link[@rel = $RDF_DESCRIBES]/@href}"><xsl:value-of select="atom:link[@rel = $RDF_DESCRIBES]/@href"/></a>
            </p>
        </xsl:if>
    </xsl:template>

    <!-- name -->
    <xsl:template match="atom:title">
        <h1>
            <xsl:value-of select="text()"/>
        </h1>
    </xsl:template>
    <!-- alternative names -->
    <xsl:template match="rdfa:meta[@property=$RDFA_ALTERNATIVE]">
        <p class="alternate-title">
            <xsl:value-of select="@content"/>
        </p>
    </xsl:template>

    <!-- description -->
    <xsl:template match="atom:content">
        <p class="description">
            <xsl:call-template name="break">
                <xsl:with-param name="text" select="text()"/>
            </xsl:call-template>
        </p>
    </xsl:template>

    <!-- replace two carriage returns in a row with a br -->
    <xsl:template name="break">
        <xsl:param name="text" select="."/>
        <xsl:choose>
            <xsl:when test="contains($text,'&#xa;&#xa;')">
                <xsl:value-of select="substring-before($text,'&#xa;&#xa;')"/>
                <br/>
                <xsl:call-template name="break">
                    <xsl:with-param name="text" select="substring-after($text,'&#xa;&#xa;')"/>
                </xsl:call-template>
            </xsl:when>
            <xsl:otherwise>
                <xsl:value-of select="$text"/>
            </xsl:otherwise>
        </xsl:choose>
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
                    <p>Contact email/s</p>
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

    <!-- Rights -->
    <xsl:template match="atom:rights">
        <div class="statement">
            <div class="property">
                <p>Rights</p>
            </div>
            <div class="content">
                <p>
                    <xsl:call-template name="break">
                        <xsl:with-param name="text" select="text()"/>
                    </xsl:call-template>
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
                    <xsl:call-template name="break">
                        <xsl:with-param name="text" select="@content"/>
                    </xsl:call-template>
                </p>
            </div>
        </div>
    </xsl:template>

    <!-- creators -->
    <xsl:template name="creators">
        <xsl:if test="atom:author">
            <div class="statement">
                <div class="property">
                    <p>Creator/s</p>
                </div>
                <div class="content">
                    <xsl:apply-templates select="atom:author"/>
                </div>
            </div>
        </xsl:if>
    </xsl:template>
    <xsl:template match="atom:author">
        <p>
            <a href="{atom:uri}">
                <xsl:value-of select="atom:name"/>
            </a>
        </p>
    </xsl:template>

    <!-- curators -->
    <xsl:template name="managers">
        <xsl:if test="atom:link[@rel=$ATOM_PUBLISHER]">
            <div class="statement">
                <div class="property">
                    <p>Manager/s</p>
                </div>
                <div class="content">
                    <xsl:apply-templates select="atom:link[@rel=$ATOM_PUBLISHER]"/>
                </div>
            </div>
        </xsl:if>
    </xsl:template>

    <!-- locations -->
    <xsl:template name="websites">
        <xsl:if test="atom:link[@rel=$ATOM_IS_LOCATED_AT]">
            <div class="statement">
                <div class="property">
                    <p>Website/s</p>
                </div>
                <div class="content">
                    <xsl:apply-templates select="atom:link[@rel=$ATOM_IS_LOCATED_AT]"/>
                </div>
            </div>
        </xsl:if>
    </xsl:template>

    <xsl:template name="publications">
        <xsl:if test="atom:link[@rel=$ATOM_IS_REFERENCED_BY]">
            <div class="statement">
                <div class="property">
                    <p>Publication/s</p>
                </div>
                <div class="content">
                    <xsl:apply-templates select="atom:link[@rel=$ATOM_IS_REFERENCED_BY]"/>
                </div>
            </div>
        </xsl:if>
    </xsl:template>

    <!-- subjects -->
    <xsl:template name="subjects">
        <xsl:if test="atom:category[@scheme = $SCHEME_FOR]">
            <div class="statement">
                <div class="property">
                    <p>Field/s of research</p>
                </div>
                <div class="content">
                    <xsl:apply-templates select="atom:category[@scheme = $SCHEME_FOR]"/>
                </div>
            </div>
        </xsl:if>
        <xsl:if test="atom:category[@scheme = $SCHEME_SEO]">
            <div class="statement">
                <div class="property">
                    <p>Socio-economic impact/s</p>
                </div>
                <div class="content">
                    <xsl:apply-templates select="atom:category[@scheme = $SCHEME_SEO]"/>
                </div>
            </div>
        </xsl:if>
        <xsl:if test="atom:category[@scheme = $SCHEME_TOA]">
            <div class="statement">
                <div class="property">
                    <p>Type of activity</p>
                </div>
                <div class="content">
                    <xsl:apply-templates select="atom:category[@scheme = $SCHEME_TOA]"/>
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
            <xsl:variable name="fields" select="fn:tokenize(@content,';')"/>
            <xsl:variable name="tokens" select="for $field in ($fields) return
            (normalize-space(substring-before($field,'=')), normalize-space(substring-after($field,'=')))"/>
            <xsl:variable name="start-index" select="index-of($tokens, 'start')"/>
            <xsl:variable name="end-index" select="index-of($tokens, 'end')"/>
            <xsl:variable name="name-index" select="index-of($tokens, 'name')"/>
            <xsl:if test="exists($start-index)">
                <xsl:value-of select="$tokens[$start-index + 1]"/>
            </xsl:if>
            <xsl:if test="exists($start-index) or exists($end-index)">
                <xsl:text> - </xsl:text>
            </xsl:if>
            <xsl:if test="exists($end-index)">
                <xsl:value-of select="$tokens[$end-index + 1]"/>
            </xsl:if>
            <xsl:if test="exists($name-index)">
                <xsl:if test="exists($start-index) or exists($end-index)">
                    <xsl:text>; </xsl:text>
                </xsl:if>
                <xsl:value-of select="$tokens[$name-index + 1]"/>
            </xsl:if>
        </p>
    </xsl:template>

    <!-- source -->
    <xsl:template name="description-id">
        <div class="statement">
            <div class="property">
                Description identifier
            </div>
            <div class="content">
                <a href="{atom:link[@rel = $REL_SELF]/@href}"><xsl:value-of select="atom:link[@rel = $REL_SELF]/@href"/></a>
            </div>
        </div>
    </xsl:template>
    <xsl:template match="atom:source">
        <div class="statement">
            <div class="property">
                <p>Description author/s</p>
            </div>
            <div class="content">
                <xsl:for-each select="atom:author">
                    <xsl:if test="position() != 1">, </xsl:if>
                    <xsl:value-of select="atom:name"/>
                </xsl:for-each>
            </div>
        </div>
        <div class="statement">
            <div class="property">
                <p>Description publisher</p>
            </div>
            <div class="content">
                <xsl:apply-templates select="atom:link[@rel = $ATOM_PUBLISHER]"/>
            </div>
        </div>
        <div class="statement">
            <div class="property">
                <p>Sourced from</p>
            </div>
            <div class="content">
                <!-- TODO check if id starts with HTTP before making it a link -->
                <p>
                    <a href="{atom:id}">
                        <xsl:value-of select="atom:title"/>
                    </a>
                </p>
            </div>
        </div>
    </xsl:template>

    <xsl:template name="version-info">
        <xsl:if test="$currentUser">
            <div class="statement">
                <div class="property">
                    <p>This version</p>
                </div>
                <div class="content">
                    <p>
                        <xsl:call-template name="version-details"/>
                    </p>
                </div>
            </div>
        </xsl:if>
        <div class="statement">
            <div class="property">
                Updated
            </div>
            <div class="content">
                <xsl:value-of select="fn:format-dateTime(fn:adjust-dateTime-to-timezone(atom:updated),
                '[F,3-3], [D] [MNn] [Y], [H]:[m]:[s] [z]')"/>
            </div>
        </div>
        <xsl:if test="$currentUser
        and atom:link[@rel = $REL_SELF]/@href != atom:link[@rel = $REL_EDIT]/@href
        and atom:link[@rel = $REL_SELF]/@href != atom:link[@rel = $REL_LATEST_VERSION]/@href">
            <div class="statement">
                <div class="property">
                    Published version
                </div>
                <div class="content">
                    <a href="{atom:link[@rel = $REL_LATEST_VERSION]/@href}">
                        Version <xsl:value-of select="atom:link[@rel = $REL_LATEST_VERSION]/@title"/>
                    </a>
                </div>
            </div>
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
    <xsl:template match="atom:link[@rel=$REL_ALTERNATE]">
            <a class="button-bar-button" href="{@href}">
                <xsl:choose>
                    <xsl:when test="@title">
                        <xsl:value-of select="@title"/>
                    </xsl:when>
                    <xsl:otherwise>
                        <xsl:value-of select="@href"/>
                    </xsl:otherwise>
                </xsl:choose>
            </a>
    </xsl:template>

    <xsl:template name="bread-crumbs">
        <xsl:param name="path"/>
        <xsl:param name="title"/>
        <li class="bread-crumbs">
            <a href="/">Home</a>
        </li>
        <li class="bread-crumbs">
            <a href="/{$path}">
                <xsl:value-of select="$title"/>
            </a>
        </li>
        <xsl:choose>
            <xsl:when test="$currentUser">
                <li class="bread-crumbs">
                    <a href="{atom:link[@rel = $REL_EDIT]/@href}">
                        <xsl:value-of select="atom:link[@rel=$REL_SELF]/@title"/>
                    </a>
                </li>
                <li class="bread-crumbs-last">
                    <xsl:call-template name="version-details"/>
                </li>
            </xsl:when>
            <xsl:otherwise>
                <li class="bread-crumbs-last">
                    <a href="{atom:link[@rel = $REL_SELF]/@href}">
                        <xsl:value-of select="atom:link[@rel=$REL_SELF]/@title"/>
                    </a>
                </li>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="version-details">
        Version
        <xsl:choose>
            <xsl:when test="atom:link[@rel = $REL_SELF]/@href = atom:link[@rel = $REL_EDIT]/@href">
                <!-- have asked for work URL -->
<!--                <xsl:choose>
                    <xsl:when test="atom:link[@rel = $REL_LATEST_VERSION]">
                        &lt;!&ndash; being shown the latest version &ndash;&gt;
                        <xsl:value-of select="atom:link[@rel = $REL_LATEST_VERSION]/@title"/>  (published)
                        <xsl:choose>
                            <xsl:when test="atom:link[@rel = $REL_LATEST_VERSION]/@href =
                    atom:link[@rel = $REL_WORKING_COPY]/@href">
                                (latest version)
                            </xsl:when>
                            <xsl:otherwise>
                                (<a class="unpublished"
                                    href="{atom:link[@rel = $REL_WORKING_COPY]/@href}">version
                                <xsl:value-of select="atom:link[@rel = $REL_WORKING_COPY]/@title"/> available</a>)
                            </xsl:otherwise>
                        </xsl:choose>
                    </xsl:when>
                    <xsl:otherwise>
                        &lt;!&ndash; no published version, being shown the working copy &ndash;&gt;
                        <xsl:value-of select="atom:link[@rel = $REL_WORKING_COPY]/@title"/>
                        <span class="unpublished"> (unpublished)</span>
                    </xsl:otherwise>
                </xsl:choose>-->
                <!-- being shown the working copy -->
                <xsl:value-of select="atom:link[@rel = $REL_WORKING_COPY]/@title"/>
                <xsl:if test="atom:link[@rel = $REL_WORKING_COPY]/@href != atom:link[@rel = $REL_LATEST_VERSION]/@href">
                    <span class="unpublished"> (unpublished)</span>
                    (<a href="{atom:link[@rel = $REL_LATEST_VERSION]/@href}">published version
                    <xsl:value-of select="atom:link[@rel = $REL_LATEST_VERSION]/@title"/> available</a>)
                </xsl:if>
            </xsl:when>
            <xsl:otherwise>
                <!-- have asked for a particular version URL -->
                <xsl:value-of select="fn:substring-after(atom:link[@rel = $REL_SELF]/@href,
                             fn:concat(atom:link[@rel = $REL_EDIT]/@href, '/'))"/>
                <xsl:choose>
                    <xsl:when test="atom:link[@rel = $REL_SELF]/@href = atom:link[@rel = $REL_LATEST_VERSION]/@href">
                     (published)
                    </xsl:when>
                    <xsl:otherwise>
                        (<a href="{atom:link[@rel = $REL_LATEST_VERSION]/@href}">published version
                        <xsl:value-of select="atom:link[@rel = $REL_LATEST_VERSION]/@title"/></a> available)
                    </xsl:otherwise>
                </xsl:choose>
                <xsl:choose>
                    <xsl:when test="atom:link[@rel = $REL_SELF]/@href = atom:link[@rel = $REL_WORKING_COPY]/@href">
                         (latest version)
                    </xsl:when>
                    <xsl:otherwise>
                         (<a class="unpublished"
                             href="{atom:link[@rel = $REL_WORKING_COPY]/@href}">unpublished version
                        <xsl:value-of select="atom:link[@rel = $REL_WORKING_COPY]/@title"/> available</a>)
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <!-- button bar -->
    <xsl:template name="button-bar">
        <xsl:param name="path"/>
        <div class="entity-type">
            <xsl:call-template name="entity-icon">
                <xsl:with-param name="colour">white</xsl:with-param>
            </xsl:call-template>
            <span><xsl:value-of select="atom:link[@rel=$REL_TYPE]/@title"/></span>
        </div>
        <div class="arrow-right"></div>
        <xsl:call-template name="representations"/>
        <xsl:call-template name="actions">
            <xsl:with-param name="path" select="$path"/>
        </xsl:call-template>
    </xsl:template>

    <!-- representations -->
    <xsl:template name="representations">
        <xsl:if test="atom:link[@rel=$REL_ALTERNATE]">
            <div class="representations">
                Download description as:
                <xsl:apply-templates select="atom:link[@rel=$REL_ALTERNATE]"/>
            </div>
        </xsl:if>
    </xsl:template>

    <!-- actions for logged in users -->
    <xsl:template name="actions">
        <xsl:param name="path"/>
        <xsl:if test="$currentUser">
            <div class="actions">
                <a class="button-bar-button" id="new-record-link" href="/{$path}?v=new" title="Add Record">New</a>
                <xsl:text> </xsl:text>
                <a class="button-bar-button" id="edit-record-link" href="{atom:link[@rel=$REL_WORKING_COPY]/@href}?v=edit" title="Edit Latest Version">Edit</a>
                <xsl:text> </xsl:text>
                <a class="button-bar-button" id="delete-record-link" href="#" onclick="DataSpace.deleteRecord('{atom:link[@rel=$REL_SELF]/@href}'); "
                   title="Delete Record">Delete</a>
            </div>
        </xsl:if>
    </xsl:template>

</xsl:stylesheet>