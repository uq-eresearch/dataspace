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
            >> (new)
        </li>
    </xsl:template>
    <xsl:template name="title">
        <input id="title-text" name="title-text" type="text" value=""/>
    </xsl:template>
    <xsl:template name="alternative-title">
        <input id="alternative-title-text" name="alternative-title-text" type="text" value=""/>
        <a id="new-record-link" href="#" title="Add Title">new</a>
    </xsl:template>
    <xsl:template name="type">
        <select id="type-combobox" name="type-combobox">
            <option value="collection">Collection</option>
            <option value="dataset">Dataset</option>
        </select>
    </xsl:template>
    <xsl:template match="atom:content">
        <textarea id="content-textarea" name="content-textarea" cols="50" rows="5">
            <xsl:value-of select="text()"/>
        </textarea>
    </xsl:template>
    <xsl:template name="page">
        <input id="page-text" name="page-text" type="text" value=""/>
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