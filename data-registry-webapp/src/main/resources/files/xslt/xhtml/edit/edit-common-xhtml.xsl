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
                xmlns:rdfa="http://www.w3.org/ns/rdfa#" xmlns:georss="http://www.georss.org/georss/"
                xmlns="http://www.w3.org/1999/xhtml"
                exclude-result-prefixes="rdf ore atom foaf dc dcterms dctype dcam cld ands rdfa georss">
    <xsl:include href="../../constants.xsl"/>
    <xsl:param name="currentUser"/>
    <xsl:output method="html" media-type="application/xhtml+xml" indent="yes"/>
    <xsl:template name="edit-bread-crumbs">
        <xsl:param name="path"/>
        <xsl:param name="title"/>
        <li class="bread-crumbs">
            <a href="/">Home</a>
            >>
            <a href="/browse">Browse</a>
            >>
            <a href="/{$path}">
                <xsl:value-of select="$title"/>
            </a>
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
                (edit)
            </xsl:if>
        </li>
    </xsl:template>
    <xsl:template name="title">
        <input id="edit-title-text" name="title-text" type="text" value="{atom:title}"/>
    </xsl:template>
    <xsl:template name="alternative-title" match="atom:title[@rel = @REL_ALTERNATE]">
        <input id="alternative-title-text" name="alternative-title-text" type="text" value="{@title}"/>
        <a id="alternative-name-link" class="new-link" href="#"
           onclick="replicateSimpleField('alternative-title-text'); return false;" title="Add Title">new
        </a>
    </xsl:template>
    <xsl:template name="type">
        <xsl:choose>
            <xsl:when test="atom:category[@scheme=$NS_DCMITYPE]">
                <select id="collection-type-combobox" name="type-combobox">
                    <option value="collection">Collection</option>
                    <option value="dataset">Dataset</option>
                </select>
            </xsl:when>
            <xsl:when test="atom:category[@scheme=$NS_FOAF]">
                <xsl:choose>
                    <xsl:when test="atom:category[@term=$ENTITY_PARTY]">
                        <select id="agent-type-combobox" name="type-combobox">
                            <option value="group">Group</option>
                            <option value="person">Person</option>
                        </select>
                    </xsl:when>
                    <xsl:otherwise>
                        <select id="activity-type-combobox" name="type-combobox">
                            <option value="program">Program</option>
                            <option value="project">Project</option>
                        </select>
                    </xsl:otherwise>
                </xsl:choose>
            </xsl:when>
            <xsl:otherwise>
                <select id="service-type-combobox" name="type-combobox">
                    <option value="annotate">Annotate</option>
                    <option value="assemble">Assemble</option>
                    <option value="create">Create</option>
                    <option value="Generate">Generate</option>
                    <option value="harvest">Harvest</option>
                    <option value="report">Report</option>
                    <option value="search">Search</option>
                    <option value="syndicate">Syndicate</option>
                    <option value="transform">Transform</option>
                </select>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="content">
        <textarea id="content-textarea" name="content-textarea" cols="50" rows="5">
            <xsl:value-of select="atom:content"/>
        </textarea>
    </xsl:template>

    <xsl:template name="page">
        <xsl:choose>
            <xsl:when test="atom:link[@rel=$ATOM_IS_LOCATED_AT]">
                <xsl:for-each select="atom:link[@rel=$ATOM_IS_LOCATED_AT]">
                    <xsl:variable name="index" select="position() - 1"/>
                    <xsl:choose>
                        <xsl:when test="$index = 0">
                            <input id="page-text" name="page-text" type="text" value="{@href}"/>
                            <a id="other-pages-link" class="new-link" href="#"
                               onclick="replicateSimpleField('page-text'); return false;" title="Add Web Page">new
                            </a>
                        </xsl:when>
                        <xsl:otherwise>
                            <input id="page-text-{$index}" name="page-text" type="text" value="{@href}"/>
                            <a id="page-text-{$index}-remove-link" class="remove-link" href="#"
                               onclick="$('#page-text-{$index}').remove(); $(this).remove();">remove
                            </a>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:for-each>
            </xsl:when>
            <xsl:otherwise>
                <input id="page-text" name="page-text" type="text" value="{@href}"/>
                <a id="other-pages-link" class="new-link" href="#"
                   onclick="replicateSimpleField('page-text'); return false;" title="Add Web Page">new
                </a>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="edit-creators">
        <table id="edit-creators-table">
            <tr>
                <td>
                    <input id="creator-0" value="" type="text"/>
                </td>
                <td>
                    <a id="lookup-creator-link" href="#" title="Lookup">lookup</a>
                </td>
                <td></td>
            </tr>
        </table>
        <div>
            <a id="add-creator-link" href="#" title="Add Creator">add creator</a>
        </div>
    </xsl:template>

    <xsl:template name="edit-custodians">
        <table id="edit-custodians-table">
            <tr>
                <td>
                    <input id="custodian-0" value="" type="text"/>
                </td>
                <td>
                    <a id="lookup-custodian-link" href="#" title="Lookup">lookup</a>
                </td>
                <td></td>
            </tr>
        </table>
        <div>
            <a id="add-custodian-link" href="#" title="Add Custodian">add custodian</a>
        </div>
    </xsl:template>

    <xsl:template name="edit-projects">
        <table id="edit-projects-table">
            <tr>
                <td>
                    <input id="project-0" value="" type="text"/>
                </td>
                <td>
                    <a id="lookup-project-link" href="#" title="Lookup">lookup</a>
                </td>
                <td></td>
            </tr>
        </table>
        <div>
            <a id="add-project-link" href="#" title="Add Project">add project</a>
        </div>
    </xsl:template>
</xsl:stylesheet>