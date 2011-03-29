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
    <xsl:template name="alternative-title">
        <xsl:choose>
            <xsl:when test="rdfa:meta[@property= $RDFA_ALTERNATIVE]">
                <xsl:for-each select="rdfa:meta[@property= $RDFA_ALTERNATIVE]">
                    <xsl:variable name="index" select="position() - 1"/>
                    <xsl:choose>
                        <xsl:when test="$index = 0">
                            <input id="alternative-title-text" name="alternative-title-text" type="text"
                                   value="{@content}"/>
                            <a id="alternative-name-link" class="new-link" href="#"
                               onclick="replicateSimpleField('alternative-title-text'); return false;"
                               title="Add Title">new
                            </a>
                        </xsl:when>
                        <xsl:otherwise>
                            <input id="alternative-title-text-{$index}" name="alternative-title-text" type="text"
                                   value="{@content}"/>
                            <a id="alternative-title-text-{$index}-remove-link" class="remove-link" href="#"
                               onclick="$('#alternative-title-text-{$index}').remove(); $(this).remove();">remove
                            </a>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:for-each>
            </xsl:when>
            <xsl:otherwise>
                <input id="alternative-title-text" name="alternative-title-text" type="text" value=""/>
                <a id="alternative-name-link" class="new-link" href="#"
                   onclick="replicateSimpleField('alternative-title-text'); return false;" title="Add Title">new
                </a>
            </xsl:otherwise>
        </xsl:choose>
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

    <xsl:template name="email">
        <xsl:choose>
            <xsl:when test="atom:link[@rel=$ATOM_MBOX]">
                <xsl:for-each select="atom:link[@rel=$ATOM_MBOX]">
                    <xsl:variable name="index" select="position() - 1"/>
                    <xsl:choose>
                        <xsl:when test="$index = 0">
                            <input id="email-text" name="email-text" type="text" value="{@title}"/>
                            <a id="add-email-link" class="new-link" href="#"
                               onclick="replicateSimpleField('email-text'); return false;" title="Add Email">new
                            </a>
                        </xsl:when>
                        <xsl:otherwise>
                            <input id="email-text-{$index}" name="email-text" type="text" value="{@title}"/>
                            <a id="email-text-{$index}-remove-link" class="remove-link" href="#"
                               onclick="$('#email-text-{$index}').remove(); $(this).remove();">remove
                            </a>
                        </xsl:otherwise>
                    </xsl:choose>
                </xsl:for-each>
            </xsl:when>
            <xsl:otherwise>
                <input id="email-text" name="email-text" type="text" value=""/>
                <a id="add-email-link" class="new-link" href="#"
                   onclick="replicateSimpleField('email-text'); return false;" title="Add Email">new
                </a>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>


    <xsl:template name="edit-creators">
        <xsl:param name="field"/>
        <table id="edit-{$field}-table" class="lookup-table">
            <tbody>
                <xsl:choose>
                    <xsl:when test="atom:link[@rel=$ATOM_CREATOR]">
                        <xsl:for-each select="atom:link[@rel=$ATOM_CREATOR]">
                            <xsl:variable name="index" select="position() - 1"/>
                            <xsl:choose>
                                <xsl:when test="$index = 0">
                                    <tr>
                                        <td>
                                            <input id="{$field}" value="{@title}" type="text"/>
                                        </td>
                                        <td>
                                            <a id="lookup-{$field}-link" href="#" title="Lookup"
                                               onclick="doLookup(); return false;">
                                                lookup
                                            </a>
                                        </td>
                                        <td class="lookup-result">
                                            <xsl:value-of select="@title"/>
                                        </td>
                                    </tr>
                                </xsl:when>
                                <xsl:otherwise>
                                    <tr>
                                        <td>
                                            <input id="{$field}-{$index}" value="{@title}" type="text"/>
                                        </td>
                                        <td>
                                            <a id="lookup-{$field}-link" href="#" title="Lookup"
                                               onclick="doLookup(); return false;">
                                                lookup
                                            </a>
                                        </td>
                                        <td class="lookup-result">
                                            <a id="{$field}-{$index}-remove-link" class="remove-link" href="#"
                                               onclick="$(this).parent().parent().remove();">remove
                                            </a>
                                            <xsl:value-of select="@title"/>
                                        </td>
                                    </tr>
                                </xsl:otherwise>
                            </xsl:choose>
                        </xsl:for-each>
                    </xsl:when>
                    <xsl:otherwise>
                        <tr>
                            <td>
                                <input id="{$field}" value="" type="text"/>
                            </td>
                            <td>
                                <a id="lookup-{$field}-link" href="#" title="Lookup"
                                   onclick="doLookup(); return false;">
                                    lookup
                                </a>
                            </td>
                            <td class="lookup-result"></td>
                        </tr>
                    </xsl:otherwise>
                </xsl:choose>
            </tbody>
        </table>
        <div>
            <a class="new-link" id="add-{$field}-link" href="#" title="Add"
               onclick="replicateLookupField('{$field}'); return false;">add
            </a>
        </div>
    </xsl:template>


    <xsl:template name="edit-custodians">
        <table id="edit-custodians-table" class="lookup-table">
            <tbody>
                <tr>
                    <td>
                        <input id="custodian" value="" type="text"/>
                    </td>
                    <td>
                        <a id="lookup-custodian-link" href="#" title="Lookup" onclick="doLookup(); return false;">lookup
                        </a>
                    </td>
                    <td class="lookup-result"></td>
                </tr>
            </tbody>
        </table>
        <div>
            <a class="new-link" id="add-custodian-link" href="#" title="Add Custodian"
               onclick="replicateLookupField('custodian'); return false;">add
            </a>
        </div>
    </xsl:template>

    <xsl:template name="edit-projects">
        <table id="edit-projects-table" class="lookup-table">
            <tbody>
                <tr>
                    <td>
                        <input id="project" value="" type="text"/>
                    </td>
                    <td>
                        <a id="lookup-project-link" href="#" title="Lookup" onclick="doLookup(); return false;">lookup
                        </a>
                    </td>
                    <td class="lookup-result"></td>
                </tr>
            </tbody>
        </table>
        <div>
            <a class="new-link" id="add-project-link" href="#" title="Add Project"
               onclick="replicateLookupField('project'); return false;">add
            </a>
        </div>
    </xsl:template>

    <xsl:template name="edit-collection">
        <table id="edit-collections-table" class="lookup-table">
            <tbody>
                <tr>
                    <td>
                        <input id="collection" value="" type="text"/>
                    </td>
                    <td>
                        <a id="lookup-collection-link" href="#" title="Lookup" onclick="doLookup(); return false;">
                            lookup
                        </a>
                    </td>
                    <td class="lookup-result"></td>
                </tr>
            </tbody>
        </table>
        <div>
            <a class="new-link" id="add-collection-link" href="#" title="Add Project"
               onclick="replicateLookupField('collection'); return false;">add
            </a>
        </div>
    </xsl:template>


    <xsl:template name="edit-socio-economic-impact">
        <table id="edit-socio-economic-impact-table" class="lookup-table">
            <tbody>
                <tr>
                    <td>
                        <input id="impact-name" value="" type="text"/>
                    </td>
                    <td>
                        <a id="lookup-impact-link" href="#" title="Lookup" onclick="doLookup(); return false;">lookup
                        </a>
                    </td>
                    <td class="lookup-result"></td>
                </tr>
            </tbody>
        </table>
        <div>
            <a class="new-link" id="add-socio-economic-impact-link" href="#" title="Add Term"
               onclick="replicateLookupField('impact-name'); return false;">add
            </a>
        </div>
    </xsl:template>
    <xsl:template name="edit-fields-of-research">
        <table id="edit-fields-of-research-table" class="lookup-table">
            <tbody>
                <tr>
                    <td>
                        <input id="research-field-name" value="" type="text"/>
                    </td>
                    <td>
                        <a id="lookup-field-link" href="#" title="Lookup" onclick="doLookup(); return false;">lookup</a>
                    </td>
                    <td class="lookup-result"></td>
                </tr>
            </tbody>
        </table>
        <div>
            <a class="new-link" id="add-fields-of-research-link" href="#" title="Add Term"
               onclick="replicateLookupField('research-field-name'); return false;">add
            </a>
        </div>
    </xsl:template>

    <xsl:template name="type-of-activities">
        <input type="checkbox" class="type-of-activity" name="applied-research" value="applied-research"/>
        Applied Research
        <input type="checkbox" class="type-of-activity" name="pure-basic-research" value="pure basic research"/>
        Pure Basic Research
        <input type="checkbox" class="type-of-activity" name="experimental-development"
               value="experimental development"/>
        Experimental Development
        <input type="checkbox" class="type-of-activity" name="strategic-basic-research"
               value="strategic basic research"/>
        Strategic Basic Research
    </xsl:template>
    <xsl:template name="keywords">
        <table id="keywords-table" class="lookup-table">
            <tbody>
                <tr>
                    <td>
                        <ul id="keywords-list">
                        </ul>
                    </td>
                </tr>
                <tr>
                    <td>
                        <input id="keyword" value="" type="text"/>
                        <a class="new-link" id="add-link" href="#" title="Add"
                           onclick="addKeyword('keyword', 'keywords-list')">add
                        </a>
                    </td>
                </tr>
            </tbody>
        </table>
    </xsl:template>

    <xsl:template name="edit-time-period">
        <table id="time-period-table" class="lookup-table">
            <tbody>
                <tr>
                    <td>
                        Start Time:
                    </td>
                </tr>
                <tr>
                    <td>
                        End Time:
                    </td>
                </tr>
            </tbody>
        </table>
    </xsl:template>
    <xsl:template name="edit-locations">
        <table id="edit-locations-table" class="lookup-table">
            <tbody>
                <tr>
                    <td>
                        <input id="location-name" value="" type="text"/>
                    </td>
                    <td>
                        <a id="lookup-location-link" href="#" title="Lookup" onclick="doLookup(); return false;">lookup
                        </a>
                    </td>
                    <td class="lookup-result"></td>
                </tr>
            </tbody>
        </table>
        <div>
            <a class="new-link" id="add-locations-link" href="#" title="Add Location"
               onclick="replicateLookupField('location-name'); return false;">add
            </a>
        </div>
        <h4>Region</h4>
        <div id="map-canvas" style="width: 500px; height: 400px">
        </div>
    </xsl:template>


    <xsl:template name="edit-related-collections">
        <table id="edit-related-collections-table" class="lookup-table">
            <tbody>
                <tr>
                    <td>
                        <input id="related-collection" value="" type="text"/>
                    </td>
                    <td>
                        <a id="lookup-related-collection-link" href="#" title="Lookup"
                           onclick="doLookup(); return false;">lookup
                        </a>
                    </td>
                    <td class="lookup-result"></td>
                </tr>
            </tbody>
        </table>
        <div>
            <a class="new-link" id="add-related-collection-link" href="#" title="Add Collection"
               onclick="replicateLookupField('related-collection'); return false;">add
            </a>
        </div>
    </xsl:template>
    <xsl:template name="edit-related-services">
        <table id="edit-related-services-table" class="lookup-table">
            <tbody>
                <tr>
                    <td>
                        <input id="related-service" value="" type="text"/>
                    </td>
                    <td>
                        <a id="lookup-related-service-link" href="#" title="Lookup" onclick="doLookup(); return false;">
                            lookup
                        </a>
                    </td>
                    <td class="lookup-result"></td>
                </tr>
            </tbody>
        </table>
        <div>
            <a class="new-link" id="add-related-service-link" href="#" title="Add Service"
               onclick="replicateLookupField('related-service'); return false;">add
            </a>
        </div>
    </xsl:template>
    <xsl:template name="edit-related-publications">
        <table id="edit-related-publications-table" class="lookup-table">
            <tbody>
                <tr>
                    <td>
                        Title
                        <input id="publication-title" value="" type="text"/>
                    </td>
                    <td>
                        URL
                        <input id="publication-url" value="" type="text"/>
                    </td>
                    <td>

                    </td>
                </tr>
            </tbody>
        </table>
        <div>
            <a class="new-link" id="add-related-publication-link" href="#" title="Add Publication"
               onclick="replicateLookupField('publication-title'); return false;">add
            </a>
        </div>
    </xsl:template>


    <xsl:template name="rights">
        <textarea id="rights-textarea" name="rights-textarea" cols="50" rows="5">
            <xsl:value-of select="atom:rights"/>
        </textarea>
    </xsl:template>
    <xsl:template name="access-rights">
        <textarea id="access-rights-textarea" name="access-rights-textarea" cols="50" rows="5">
            <xsl:value-of select="rdfa:meta[@property=$RDFA_ACCESS_RIGHTS]/@content"/>
        </textarea>
    </xsl:template>
    <xsl:template name="licence-type">
        <select id="licence-type-combobox" name="type-combobox">
            <option value="none">None</option>
            <option value="cc-by">CC-BY</option>
            <option value="cc-by-sa">CC-BY-SA</option>
            <option value="cc-by-nd">CC-BY-ND</option>
        </select>
    </xsl:template>
    <xsl:template name="lookup-form">
        <div id="lookup-div" style="display:none;">
            <form id="lookup-form" method="post" action="lookup" onsubmit="doLookup();return false;">
                <table width="100%">
                    <tbody>
                        <tr>
                            <td>
                                <input type="text" id="lookup-keyword" name="lookup-keyword" value=""/>
                            </td>
                            <td>
                                <input type="submit" name="lookup-submit" id="lookup-submit" value="Search"/>
                            </td>
                        </tr>
                    </tbody>
                </table>
            </form>
            <ul id="lookup-result">

            </ul>
        </div>
    </xsl:template>
</xsl:stylesheet>