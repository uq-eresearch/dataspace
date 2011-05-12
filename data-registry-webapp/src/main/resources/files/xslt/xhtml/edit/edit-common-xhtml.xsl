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
            <xsl:choose>
                <xsl:when test="atom:link[@rel = $REL_SELF]/@href">
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
                    <xsl:text> </xsl:text>
                    <span>(edit)</span>
                </xsl:when>
                <xsl:otherwise>
                    <span>>> (new)</span>
                </xsl:otherwise>
            </xsl:choose>
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
                            <xsl:text> </xsl:text>
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
                <xsl:text> </xsl:text>
                <a id="alternative-name-link" class="new-link" href="#"
                   onclick="replicateSimpleField('alternative-title-text'); return false;" title="Add Title">new
                </a>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template name="type">
        <xsl:param name="entity"/>
        <input id="record-type" type="hidden" value="{atom:category[@scheme=$NS_DCMITYPE]/@label}"/>
        <xsl:choose>
            <!--atom:category[@scheme=$NS_DCMITYPE]-->
            <xsl:when test="$entity = 'collection'">
                <select id="type-combobox" name="type-combobox">
                    <option value="Collection">Collection</option>
                    <option value="Dataset">Dataset</option>
                </select>
            </xsl:when>
            <xsl:when test="$entity = 'agent'">
                <select id="type-combobox" name="type-combobox">
                    <option value="Group">Group</option>
                    <option value="Person">Person</option>
                </select>
            </xsl:when>
            <xsl:when test="$entity = 'activity'">
                <select id="type-combobox" name="type-combobox">
                    <option value="Program">Program</option>
                    <option value="Project">Project</option>
                </select>
            </xsl:when>
            <xsl:otherwise>
                <select id="type-combobox" name="type-combobox">
                    <option value="Annotate">Annotate</option>
                    <option value="Assemble">Assemble</option>
                    <option value="Create">Create</option>
                    <option value="Generate">Generate</option>
                    <option value="Harvest">Harvest</option>
                    <option value="Report">Report</option>
                    <option value="Search">Search</option>
                    <option value="Syndicate">Syndicate</option>
                    <option value="Transform">Transform</option>
                </select>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="text-area">
        <xsl:param name="field"/>
        <xsl:param name="path"/>
        <xsl:choose>
            <xsl:when test="$path">
                <textarea id="{$field}-textarea" name="{$field}-textarea" cols="50" rows="5">
                    <xsl:value-of select="$path"/>
                </textarea>
            </xsl:when>
            <xsl:otherwise>
                <textarea id="{$field}-textarea" name="{$field}-textarea" cols="50" rows="5">
                </textarea>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="page">
        <xsl:choose>
            <xsl:when test="atom:link[@rel=$ATOM_IS_LOCATED_AT]">
                <xsl:for-each select="atom:link[@rel=$ATOM_IS_LOCATED_AT]">
                    <xsl:variable name="index" select="position() - 1"/>
                    <xsl:choose>
                        <xsl:when test="$index = 0">
                            <input id="page-text" name="page-text" type="text" value="{@href}"/>
                            <xsl:text> </xsl:text>
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
                <xsl:text> </xsl:text>
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
                            <xsl:text> </xsl:text>
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
                <xsl:text> </xsl:text>
                <a id="add-email-link" class="new-link" href="#"
                   onclick="replicateSimpleField('email-text'); return false;" title="Add Email">new
                </a>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>


    <xsl:template name="lookup-edit">
        <xsl:param name="field"/>
        <xsl:param name="relation"/>
        <table id="edit-{$field}-table" class="lookup-table">
            <tbody>
                <xsl:choose>
                    <xsl:when test="atom:link[@rel=$relation]">
                        <xsl:for-each select="atom:link[@rel=$relation]">
                            <xsl:variable name="index" select="position() - 1"/>
                            <xsl:choose>
                                <xsl:when test="$index = 0">
                                    <tr>
                                        <td>
                                            <input id="{$field}" value="{@title}" type="text"/>
                                        </td>
                                        <td>
                                            <a id="lookup-{$field}-link" class="lookup-link" href="#" title="Lookup"
                                               onclick="doLookup(); return false;">
                                                lookup
                                            </a>
                                            <xsl:text> </xsl:text>
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
                                            <a id="lookup-{$field}-link" class="lookup-link" href="#" title="Lookup"
                                               onclick="doLookup(); return false;">
                                                lookup
                                            </a>
                                            <xsl:text> </xsl:text>
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
                                <a id="lookup-{$field}-link" class="lookup-link" href="#" title="Lookup"
                                   onclick="doLookup(); return false;">
                                    lookup
                                </a>
                                <xsl:text> </xsl:text>
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

    <xsl:template name="edit-subject">
        <xsl:param name="field"/>
        <xsl:param name="scheme"/>
        <table id="edit-{$field}-table" class="lookup-table">
            <tbody>
                <xsl:choose>
                    <xsl:when test="atom:category[@scheme=$scheme]">
                        <xsl:for-each select="atom:category[@scheme=$scheme]">
                            <xsl:variable name="index" select="position() - 1"/>
                            <xsl:choose>
                                <xsl:when test="$index = 0">
                                    <tr>
                                        <td>
                                            <input id="{$field}" value="{@label}" type="text"/>
                                        </td>
                                        <td>
                                            <a id="lookup-{$field}-link" class="lookup-link" href="#" title="Lookup"
                                               onclick="doLookup(); return false;">lookup
                                            </a>
                                            <xsl:text> </xsl:text>
                                        </td>
                                        <td class="lookup-result">
                                            <a href="{@term}">
                                                <xsl:value-of select="@label"/>
                                            </a>
                                        </td>
                                    </tr>
                                </xsl:when>
                                <xsl:otherwise>
                                    <tr>
                                        <td>
                                            <input id="{$field}-{$index}" value="{@label}" type="text"/>
                                        </td>
                                        <td>
                                            <a id="lookup-{$field}-{$index}-link" class="lookup-link" href="#"
                                               title="Lookup"
                                               onclick="doLookup(); return false;">lookup
                                            </a>
                                            <xsl:text> </xsl:text>
                                        </td>
                                        <td class="lookup-result">
                                            <a href="{@term}">
                                                <xsl:value-of select="@label"/>
                                            </a>
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
                                <a id="lookup-{$field}-link" class="lookup-link" href="#" title="Lookup"
                                   onclick="doLookup(); return false;">lookup
                                </a>
                                <xsl:text> </xsl:text>
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

    <xsl:template name="type-of-activities">
        <xsl:choose>
            <xsl:when test="atom:category[@scheme=$SCHEME_TOA]">
                <input type="checkbox" class="type-of-activity" name="applied-research" value="applied-research"/>
                Applied research
                <input type="checkbox" class="type-of-activity" name="pure-basic-research" value="pure basic research"/>
                Pure basic research
                <input type="checkbox" class="type-of-activity" name="experimental-development"
                       value="experimental development"/>
                Experimental development
                <input type="checkbox" class="type-of-activity" name="strategic-basic-research"
                       value="strategic basic research"/>
                Strategic basic research
            </xsl:when>
            <xsl:otherwise>
                <input type="checkbox" class="type-of-activity" name="applied-research" value="applied-research"/>
                Applied research
                <input type="checkbox" class="type-of-activity" name="pure-basic-research" value="pure basic research"/>
                Pure basic research
                <input type="checkbox" class="type-of-activity" name="experimental-development"
                       value="experimental development"/>
                Experimental development
                <input type="checkbox" class="type-of-activity" name="strategic-basic-research"
                       value="strategic basic research"/>
                Strategic basic research
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
    <xsl:template name="keywords">
        <table id="keywords-table" class="lookup-table">
            <tbody>
                <tr>
                    <td>
                        <ul id="keywords-list">
                            <xsl:if test="atom:category[@label='keyword']">
                                <xsl:for-each select="atom:category[@label='keyword']">
                                    <li class="keyword">
                                        <xsl:value-of select="@term"/>
                                        <a id="{@term}" href="#" class="remove-keyword" title="Remove Keyword"
                                           onclick="$('#{@term}').parent().remove();">x
                                        </a>
                                    </li>
                                </xsl:for-each>
                            </xsl:if>
                        </ul>
                    </td>
                </tr>

                <tr>
                    <td>
                        <input id="keyword" value="" type="text"/>
                        <xsl:text> </xsl:text>
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
                        <input name="start-date" id="start-date" class="date-picker"/>
                    </td>
                </tr>
                <tr>
                    <td>
                        End Time:
                        <input name="end-date" id="end-date" class="date-picker"/>
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
        <div id="map" class="smallmap" style="width: 500px; height: 400px">
        </div>
        <ul id="controlToggle">
            <li>
                <input type="radio" name="type" value="none" id="noneToggle"
                       onclick="toggleControl(this);" checked="checked"/>
                <label for="noneToggle">navigate</label>

            </li>
            <li>
                <input type="radio" name="type" value="point" id="pointToggle" onclick="toggleControl(this);"/>
                <label for="pointToggle">draw point</label>
            </li>
            <li>
                <input type="radio" name="type" value="polygon" id="polygonToggle" onclick="toggleControl(this);"/>
                <label for="polygonToggle">draw polygon</label>
            </li>
        </ul>
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
    <xsl:template name="licence-type">
        <input type="hidden" id="licence-type" value="{atom:link[@rel=$REL_LICENSE]/@href}"/>
        <select id="licence-type-combobox" name="type-combobox">
            <option value="none">None</option>
            <option value="http://creativecommons.org/licenses/by/3.0/rdf">CC-BY</option>
            <option value="http://creativecommons.org/licenses/by-sa/3.0/rdf">CC-BY-SA</option>
            <option value="http://creativecommons.org/licenses/by-nd/3.0/rdf">CC-BY-ND</option>
            <option value="http://creativecommons.org/licenses/by-nc/3.0/rdf">CC-BY-NC</option>
            <option value="http://creativecommons.org/licenses/by-nc-sa/3.0/rdf">CC-BY-NC-SA</option>
            <option value="http://creativecommons.org/licenses/by-nc-nd/3.0/rdf">CC-BY-NC-ND</option>
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