<?xml version='1.0'?>
<!-- Transforms UQ collection profile of Atom Syndication Format to XHTML 
	with embedded RDFa XSLT 1.0 Abdul Alabri, 2011-01 -->
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
	xmlns:ore="http://www.openarchives.org/ore/terms/" xmlns:atom="http://www.w3.org/2005/Atom"
	xmlns:foaf="http://xmlns.com/foaf/0.1/" xmlns:dc="http://purl.org/dc/elements/1.1/"
	xmlns:dcterms="http://purl.org/dc/terms/" xmlns:dctype="http://purl.org/dc/dcmitype/"
	xmlns:dcam="http://purl.org/dc/dcam/" xmlns:cld="http://purl.org/cld/terms/"
	xmlns:ands="http://www.ands.org.au/ontologies/ns/0.1/VITRO-ANDS.owl#"
	xmlns:rdfa="http://www.w3.org/ns/rdfa#" xmlns:georss="http://www.georss.org/georss/"
	xmlns:app="http://www.w3.org/2007/app"
	xmlns="http://www.w3.org/1999/xhtml" exclude-result-prefixes="rdf ore atom foaf dc dcterms dctype dcam cld ands rdfa georss app">
	<xsl:include href="../../constants.xsl" />
	<xsl:output method="html" media-type="text/html;charset=utf-8"
		indent="yes" />
	<xsl:template name="edit-bread-crumbs">
		<xsl:param name="path" />
		<xsl:param name="title" />
		<li class="bread-crumbs">
			<a href="/">Home</a>
		</li>
		<li class="bread-crumbs">
			<a href="/browse">Browse</a>
		</li>
		<li class="bread-crumbs">
			<a href="/{$path}">
				<xsl:value-of select="$title" />&#160;
				<xsl:if test="atom:link[@rel = $REL_SELF]/@href">
					<a href="{atom:link[@rel = $REL_SELF]/@href}">
						<xsl:choose>
							<xsl:when test="atom:link[@rel=$REL_SELF]/@title">
								<xsl:value-of select="atom:link[@rel=$REL_SELF]/@title" />
							</xsl:when>
							<xsl:otherwise>
								<xsl:value-of select="atom:link[@rel=$REL_SELF]/@href" />
							</xsl:otherwise>
						</xsl:choose>
					</a>
				</xsl:if>
			</a>
		</li>
		<li class="bread-crumbs-last">
			<xsl:choose>
				<xsl:when test="atom:link[@rel = $REL_SELF]/@href">
					<xsl:text> </xsl:text>
					<span>Edit</span>
				</xsl:when>
				<xsl:otherwise>
					<xsl:text> </xsl:text>
					<span>New</span>
				</xsl:otherwise>
			</xsl:choose>
		</li>
	</xsl:template>
	<xsl:template name="title">
						<dl>
		<dt>
			<label for="edit-title-text">Title</label>
		</dt>
		<dd>
			<input id="edit-title-text" name="title-text" type="text"
				value="{atom:title}" />
		</dd>
		</dl>
	</xsl:template>
	<xsl:template name="alternative-title">
		<dl>
		<dt>
			<label for="edit-alternative-title-text">Alternative Title</label>
		</dt>
		<xsl:choose>
			<xsl:when test="rdfa:meta[@property= $RDFA_ALTERNATIVE]">
				<xsl:for-each select="rdfa:meta[@property= $RDFA_ALTERNATIVE]">
					<xsl:variable name="index" select="position() - 1" />
					<xsl:choose>
						<xsl:when test="$index = 0">
						<dd>
							<input id="alternative-title-text" name="alternative-title-text"
								type="text" value="{@content}" />
						</dd>
						</xsl:when>
						<xsl:otherwise>
						<dd>
							<input id="alternative-title-text-{$index}" name="alternative-title-text"
								type="text" value="{@content}" />
							<a id="alternative-title-text-{$index}-remove-link" class="remove-link"
								href="#"
								onclick="DataSpace.$('#alternative-title-text-{$index}').remove(); $(this).remove();">remove
							</a>
						</dd>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:for-each>
			</xsl:when>
			<xsl:otherwise>
				<dd>
					<input id="alternative-title-text" name="alternative-title-text"
						type="text" value="" />
				</dd>
			</xsl:otherwise>
		</xsl:choose>
			<dd>
				<a id="alternative-name-link" class="new-link" href="#"
					onclick="DataSpace.replicateSimpleField('alternative-title-text'); return false;"
					title="Add Title">add
				</a>
			</dd>
		</dl>
	</xsl:template>
	<xsl:template name="type">
		<xsl:param name="entity" />
		<dl>
		<dt><label for="type-combobox">Type</label></dt>
		<dd>
	    <input id="record-type" type="hidden"
			value="{atom:link[@rel=$REL_TYPE]/@title}" />
		<xsl:choose>
			<!--atom:category[@scheme=$NS_DCMITYPE] -->
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
		</dd>
		</dl>
	</xsl:template>

	<xsl:template name="content">
		<dl>
			<dt>
				<label for="content-textarea">Description</label>
			</dt>
			<dd>
				<xsl:call-template name="text-area">
					<xsl:with-param name="field" select="'content'"/>
					<xsl:with-param name="path">
						<xsl:value-of select="atom:content" />
					</xsl:with-param>
				</xsl:call-template>
			</dd>
		</dl>
	</xsl:template>
	
	<xsl:template name="rights">
		<dl>
			<dt>
				<label for="rights-textarea">Rights</label>
			</dt>
			<dd>
				<xsl:call-template name="text-area">
					<xsl:with-param name="field" select="'rights'"/>
					<xsl:with-param name="path">
						<xsl:value-of select="atom:rights" />
					</xsl:with-param>
				</xsl:call-template>
			</dd>
		</dl>
	</xsl:template>

	<xsl:template name="access-rights">
		<dl>
			<dt>
				<label for="access-rights-textarea">Access</label>
			</dt>
			<dd>
				<xsl:call-template name="text-area">
					<xsl:with-param name="field" select="'access-rights'"/>
					<xsl:with-param name="path">
						<xsl:value-of select="rdfa:meta[@property=$RDFA_ACCESS_RIGHTS]/@content" />
					</xsl:with-param>
				</xsl:call-template>
			</dd>
		</dl>
	</xsl:template>

	<xsl:template name="text-area">
		<xsl:param name="field" />
		<xsl:param name="path" />
		<xsl:choose>
			<xsl:when test="$path">
				<textarea id="{$field}-textarea" name="{$field}-textarea"
					cols="50" rows="5">
					<xsl:value-of select="$path" />
				</textarea>
			</xsl:when>
			<xsl:otherwise>
				<textarea id="{$field}-textarea" name="{$field}-textarea"
					cols="50" rows="5">
				</textarea>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>

	<xsl:template name="metadata">
		<div id="metadata">
			<xsl:call-template name="status" />
			<xsl:call-template name="identifiers" />
		</div>
	</xsl:template>

	<xsl:template name="status">
		<div id="status">
			Version
			<span id="version">
				<xsl:value-of select="atom:link[@rel='self']/@title" />
			</span>
			,
			<xsl:call-template name="published" />
		</div>
	</xsl:template>


	<xsl:template name="published">
		<span id="published">
			<xsl:choose>
				<xsl:when test="app:control/app:draft/text() = 'no'">
				published
				</xsl:when>
				<xsl:otherwise>
				unpublished
				</xsl:otherwise>
			</xsl:choose>
		</span>
	</xsl:template>

	<!-- identifiers -->
	<xsl:template name="identifiers">
		<p class="identifier">
			<span class="id-property">Identifier: </span>
			<a href="{atom:id}">
				<xsl:value-of select="atom:id" />
			</a>
		</p>
		<xsl:if test="atom:id != atom:link[@rel = $RDF_DESCRIBES]/@href">
			<p class="identifier">
				<span class="id-property">Local identifier: </span>
				<a href="{atom:link[@rel = $RDF_DESCRIBES]/@href}">
					<xsl:value-of select="atom:link[@rel = $RDF_DESCRIBES]/@href" />
				</a>
			</p>
		</xsl:if>
	</xsl:template>

	<xsl:template name="creators">
		<dl>
			<dt><label for="creator">Creators</label></dt>
			<dd class="field">
				<xsl:call-template name="lookup-edit">
					<xsl:with-param name="field" select="'creator'"/>
					<xsl:with-param name="relation">
						<xsl:value-of select="$ATOM_CREATOR" />
					</xsl:with-param>
				</xsl:call-template>
			</dd>
		</dl>
	</xsl:template>
	
	<xsl:template name="publishers">
		<dl>
			<dt><label for="creator">Custodians/Contacts</label></dt>
			<dd class="field">
				<xsl:call-template name="lookup-edit">
					<xsl:with-param name="field" select="'publisher'"/>
					<xsl:with-param name="relation">
						<xsl:value-of select="$ATOM_PUBLISHER" />
					</xsl:with-param>
				</xsl:call-template>
			</dd>
		</dl>
	</xsl:template>
	
	<xsl:template name="projects">
		<dl>
			<dt><label for="creator">Projects</label></dt>
			<dd class="field">
				<xsl:call-template name="lookup-edit">
					<xsl:with-param name="field" select="'isoutputof'"/>
					<xsl:with-param name="relation">
						<xsl:value-of select="$ATOM_IS_OUTPUT_OF" />
					</xsl:with-param>
				</xsl:call-template>
			</dd>
		</dl>
	</xsl:template>
	
	<xsl:template name="fields-of-research">
		<dl>
			<dt><label for="creator">Fields of Research</label></dt>
			<dd class="field">
				<xsl:call-template name="edit-subject">
					<xsl:with-param name="field" select="'field-of-research'"/>
					<xsl:with-param name="scheme">
						<xsl:value-of select="$SCHEME_FOR" />
					</xsl:with-param>
				</xsl:call-template>
			</dd>
		</dl>
	</xsl:template>
	
	<xsl:template name="socio-economic-impacts">
		<dl>
			<dt><label for="creator">Socio-economic Impacts</label></dt>
			<dd class="field">
				<xsl:call-template name="edit-subject">
					<xsl:with-param name="field" select="'socio-economic-impact'"/>
					<xsl:with-param name="scheme">
						<xsl:value-of select="$SCHEME_SEO" />
					</xsl:with-param>
				</xsl:call-template>
			</dd>
		</dl>
	</xsl:template>

	<xsl:template name="page">
		<dl>
			<dt>Web Page</dt>
		<xsl:choose>
			<xsl:when test="atom:link[@rel=$ATOM_IS_LOCATED_AT]">
				<xsl:for-each select="atom:link[@rel=$ATOM_IS_LOCATED_AT]">
					<xsl:variable name="index" select="position() - 1" />
					<xsl:choose>
						<xsl:when test="$index = 0">
						<dd>
							<input id="page-text" name="page-text" type="text" value="{@href}" />
						</dd>
						</xsl:when>
						<xsl:otherwise>
						<dd>
							<input id="page-text-{$index}" name="page-text" type="text"
								value="{@href}" />
							<a id="page-text-{$index}-remove-link" class="remove-link"
								href="#" onclick="DataSpace.$('#page-text-{$index}').parent().remove(); return false;">remove
							</a>
						</dd>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:for-each>
			</xsl:when>
			<xsl:otherwise>
				<dd>
					<input id="page-text" name="page-text" type="text" value="{@href}" />
				</dd>
			</xsl:otherwise>
		</xsl:choose>
			<dd>
				<a id="other-pages-link" class="new-link" href="#"
					onclick="DataSpace.replicateSimpleField('page-text'); return false;"
					title="Add Web Page">new
				</a>
			</dd>
		</dl>
	</xsl:template>

	<xsl:template name="email">
		<xsl:choose>
			<xsl:when test="atom:link[@rel=$ATOM_MBOX]">
				<xsl:for-each select="atom:link[@rel=$ATOM_MBOX]">
					<xsl:variable name="index" select="position() - 1" />
					<xsl:choose>
						<xsl:when test="$index = 0">
							<input id="email-text" name="email-text" type="text" value="{@title}" />
							<xsl:text></xsl:text>
							<a id="add-email-link" class="new-link" href="#"
								onclick="DataSpace.replicateSimpleField('email-text'); return false;"
								title="Add Email">new
							</a>
						</xsl:when>
						<xsl:otherwise>
							<input id="email-text-{$index}" name="email-text" type="text"
								value="{@title}" />
							<a id="email-text-{$index}-remove-link" class="remove-link"
								href="#" onclick="DataSpace.$('#email-text-{$index}').remove(); $(this).remove();">remove
							</a>
						</xsl:otherwise>
					</xsl:choose>
				</xsl:for-each>
			</xsl:when>
			<xsl:otherwise>
				<input id="email-text" name="email-text" type="text" value="" />
				<xsl:text></xsl:text>
				<a id="add-email-link" class="new-link" href="#"
					onclick="DataSpace.replicateSimpleField('email-text'); return false;" title="Add Email">new
				</a>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>


	<xsl:template name="lookup-edit">
		<xsl:param name="field" />
		<xsl:param name="relation" />
		<table id="edit-{$field}-table" class="lookup-table">
			<tbody>
				<xsl:choose>
					<xsl:when test="atom:link[@rel=$relation]">
						<xsl:for-each select="atom:link[@rel=$relation]">
							<xsl:variable name="index" select="position() - 1" />
							<xsl:choose>
								<xsl:when test="$index = 0">
									<tr>
										<td>
											<input id="{$field}" value="{@title}" type="text" />
										</td>
										<td>
											<a id="lookup-{$field}-link" class="lookup-link" href="#"
												title="Lookup" onclick="DataSpace.showLookupDialog('{$field}'); return false;">
												lookup
											</a>
											<xsl:text></xsl:text>
										</td>
										<td class="lookup-result">
											<xsl:value-of select="@title" />
										</td>
									</tr>
								</xsl:when>
								<xsl:otherwise>
									<tr>
										<td>
											<input id="{$field}-{$index}" value="{@title}" type="text" />
										</td>
										<td>
											<a id="lookup-{$field}-link" class="lookup-link" href="#"
												title="Lookup" onclick="DataSpace.showLookupDialog('{$field}'); return false;">
												lookup
											</a>
											<xsl:text></xsl:text>
										</td>
										<td class="lookup-result">
											<a id="{$field}-{$index}-remove-link" class="remove-link"
												href="#" onclick="DataSpace.$(this).parent().parent().remove();">remove
											</a>
											<xsl:value-of select="@title" />
										</td>
									</tr>
								</xsl:otherwise>
							</xsl:choose>
						</xsl:for-each>
					</xsl:when>
					<xsl:otherwise>
						<tr>
							<td>
								<input id="{$field}" value="" type="text" />
							</td>
							<td>
								<a id="lookup-{$field}-link" class="lookup-link" href="#"
									title="Lookup" onclick="DataSpace.showLookupDialog('{$field}'); return false;">
									lookup
								</a>
								<xsl:text></xsl:text>
							</td>
							<td class="lookup-result"></td>
						</tr>
					</xsl:otherwise>
				</xsl:choose>
			</tbody>
		</table>
		<div>
			<a class="new-link" id="add-{$field}-link" href="#" title="Add"
				onclick="DataSpace.replicateLookupField('{$field}'); return false;">add
			</a>
		</div>
	</xsl:template>

	<xsl:template name="edit-subject">
		<xsl:param name="field" />
		<xsl:param name="scheme" />
		<table id="edit-{$field}-table" class="lookup-table">
			<tbody>
				<xsl:choose>
					<xsl:when test="atom:category[@scheme=$scheme]">
						<xsl:for-each select="atom:category[@scheme=$scheme]">
							<xsl:variable name="index" select="position() - 1" />
							<xsl:choose>
								<xsl:when test="$index = 0">
									<tr>
										<td>
											<input id="{$field}" value="{@label}" type="text" />
										</td>
										<td>
											<a id="lookup-{$field}-link" class="lookup-link" href="#"
												title="Lookup" onclick="DataSpace.showLookupDialog('subject'); return false;">lookup
											</a>
											<xsl:text></xsl:text>
										</td>
										<td class="lookup-result">
											<a href="{@term}">
												<xsl:value-of select="@label" />
											</a>
										</td>
									</tr>
								</xsl:when>
								<xsl:otherwise>
									<tr>
										<td>
											<input id="{$field}-{$index}" value="{@label}" type="text" />
										</td>
										<td>
											<a id="lookup-{$field}-{$index}-link" class="lookup-link"
												href="#" title="Lookup" onclick="DataSpace.showLookupDialog('subject'); return false;">lookup
											</a>
											<xsl:text></xsl:text>
										</td>
										<td class="lookup-result">
											<a href="{@term}">
												<xsl:value-of select="@label" />
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
								<input id="{$field}" value="" type="text" />
							</td>
							<td>
								<a id="lookup-{$field}-link" class="lookup-link" href="#"
									title="Lookup" onclick="DataSpace.showLookupDialog('subject'); return false;">lookup
								</a>
								<xsl:text></xsl:text>
							</td>
							<td class="lookup-result"></td>
						</tr>
					</xsl:otherwise>
				</xsl:choose>
			</tbody>
		</table>
		<div>
			<a class="new-link" id="add-{$field}-link" href="#" title="Add"
				onclick="DataSpace.replicateLookupField('{$field}'); return false;">add
			</a>
		</div>
	</xsl:template>

	<xsl:template name="type-of-activities">
		<dl>
			<dt>
				<label for="type-of-activities">Type of Activity</label>
			</dt>
			<dd>
				<input type="checkbox" class="type-of-activity" 
					name="type-of-activity"
					value="applied">
					<xsl:if test="atom:category[@scheme=$SCHEME_TOA][@term=concat($SCHEME_TOA,'/#applied')]">
						<xsl:attribute name="checked">checked</xsl:attribute>
					</xsl:if>
				</input>
				Applied research
			</dd>
			<dd>
				<input type="checkbox" class="type-of-activity" 
					name="type-of-activity"
					value="experimental">
					<xsl:if test="atom:category[@scheme=$SCHEME_TOA][@term=concat($SCHEME_TOA,'/#experimental')]">
						<xsl:attribute name="checked">checked</xsl:attribute>
					</xsl:if>
				</input>
				Experimental development
			</dd>
			<dd>
				<input type="checkbox" class="type-of-activity" 
					name="type-of-activity"
					value="basic">
					<xsl:if test="atom:category[@scheme=$SCHEME_TOA][@term=concat($SCHEME_TOA,'/#basic')]">
						<xsl:attribute name="checked">checked</xsl:attribute>
					</xsl:if>
				</input>
				Pure basic research
			</dd>
			<dd>
				<input type="checkbox" class="type-of-activity" 
					name="type-of-activity"
					value="strategic"> 
					<xsl:if test="atom:category[@scheme=$SCHEME_TOA][@term=concat($SCHEME_TOA,'/#strategic')]">
						<xsl:attribute name="checked">checked</xsl:attribute>
					</xsl:if>
				</input>
				Strategic basic research
			</dd>
		</dl>
	</xsl:template>
	
	<xsl:template name="keywords">
		<dl id="keywords-list">
			<dt>Keywords</dt>
			<xsl:if test="atom:category">
				<xsl:for-each select="atom:category">
					<xsl:choose>
					<xsl:when test="@scheme != ''"/>
					<xsl:otherwise>
					<dd>
						<span class="keyword"><xsl:value-of select="@term" /></span>
						<a href="#" class="remove-keyword" title="Remove Keyword"
							onclick="$(this).parent().remove(); return false;">x
						</a>
					</dd>
					</xsl:otherwise>
					</xsl:choose>
				</xsl:for-each>
			</xsl:if>
			<dd>
				<input id="keyword" value="" type="text" />
				<xsl:text></xsl:text>
				<a class="new-link" id="add-link" href="#" title="Add"
					onclick="DataSpace.addKeyword('keyword'); return false;">add
				</a>
			</dd>
		</dl>
	</xsl:template>

	<xsl:template name="edit-time-period">
		<table id="time-period-table" class="lookup-table">
			<tbody>
				<tr>
					<td>
						Start Time:
						<input name="start-date" id="start-date" class="date-picker" />
					</td>
				</tr>
				<tr>
					<td>
						End Time:
						<input name="end-date" id="end-date" class="date-picker" />
					</td>
				</tr>
			</tbody>
		</table>
	</xsl:template>
	<xsl:template name="edit-locations">
		<dl>
			<dt>
				<label>Locations</label>
			</dt>
			<dd>
				<input id="location-name" value="" type="text" />
				<a id="lookup-location-link" href="http://www.geonames.org/" title="Open GeoNames Search"
					onclick="window.open(this.href,'_blank'); return false;">
					Open GeoNames
				</a>
			</dd>
			<dd>
				<a class="new-link" id="add-locations-link" href="#" title="Add Location"
					onclick="DataSpace.replicateSimpleField('location-name'); return false;">add
				</a>
			</dd>
		</dl>
		<div>
		</div>
	</xsl:template>
	
	<xsl:template name="edit-region">
		<h4>Region</h4>
		<div id="map" class="smallmap" style="width: 500px; height: 400px">
		</div>
		<ul id="controlToggle">
			<li>
				<input type="radio" name="type" value="none" id="noneToggle"
					onclick="DataSpace.toggleControl(this);" checked="checked" />
				<label for="noneToggle">navigate</label>

			</li>
			<li>
				<input type="radio" name="type" value="point" id="pointToggle"
					onclick="DataSpace.toggleControl(this);" />
				<label for="pointToggle">draw point</label>
			</li>
			<li>
				<input type="radio" name="type" value="polygon" id="polygonToggle"
					onclick="DataSpace.toggleControl(this);" />
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
						<input id="publication-title" value="" type="text" />
					</td>
					<td>
						URL
						<input id="publication-url" value="" type="text" />
					</td>
					<td>

					</td>
				</tr>
			</tbody>
		</table>
		<div>
			<a class="new-link" id="add-related-publication-link" href="#"
				title="Add Publication" onclick="DataSpace.replicateLookupField('publication-title'); return false;">add
			</a>
		</div>
	</xsl:template>
	<xsl:template name="licence-type">
		<input type="hidden" id="licence-type" value="{atom:link[@rel=$REL_LICENSE]/@href}" />
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
			<form id="lookup-form" method="post" action="">
				<input type="hidden" id="lookup-type" value="" />
				<table width="100%">
					<tbody>
						<tr>
							<td>
								<input type="text" id="query" name="lookup-keyword" value="" />
							</td>
							<td>
								<input type="button" name="lookup-submit" id="lookup-submit"
									value="Search"
									onclick="lookup($('#lookup-type').val(),$('#query').val()); return false;" />
							</td>
						</tr>
					</tbody>
				</table>
			</form>
			<div id="search-result">
				<div id="navigation">
					<ul id="pager">

					</ul>

					<div id="pager-header">

					</div>
				</div>
				<div>
					<form id="docs">

					</form>
				</div>
				<input type="button" id="lookup-select" value="Select" />
			</div>
		</div>
	</xsl:template>
</xsl:stylesheet>