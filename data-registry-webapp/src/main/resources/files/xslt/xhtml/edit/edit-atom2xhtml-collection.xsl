<?xml version='1.0'?>
<!-- Transforms UQ collection profile of Atom Syndication Format to XHTML
	with embedded RDFa XSLT 1.0 Nigel Ward, 2010-12 -->
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:atom="http://www.w3.org/2005/Atom"
	xmlns:rdfa="http://www.w3.org/ns/rdfa#" xmlns="http://www.w3.org/1999/xhtml"
	exclude-result-prefixes="atom rdfa">

	<xsl:include href="../include/header.xsl" />
	<xsl:include href="../include/head.xsl" />
	<xsl:include href="../include/footer.xsl" />
	<xsl:include href="edit-common-xhtml.xsl" />

	<xsl:output method="html" version="4.0"
		doctype-public="-//W3C//DTD HTML 4.01//EN" doctype-system="http://www.w3.org/TR/html4/strict.dtd"
		media-type="text/html;charset=utf-8" indent="yes" />
	<xsl:template match="/">
		<html xmlns="http://www.w3.org/1999/xhtml" dir="ltr" xml:lang="en"
			lang="en">
			<xsl:apply-templates />
		</html>
	</xsl:template>

	<!-- *** Atom entry *** -->

	<xsl:template match="atom:entry">
		<head>
			<meta http-equiv="content-type" content="text/html;charset=UTF-8" />
			<title>
				<xsl:value-of select="atom:title" />
			</title>
			<link href="/css/description.css" rel="stylesheet" type="text/css" />
			<xsl:call-template name="head" />
			<script type="text/javascript" src="http://openlayers.org/api/OpenLayers.js">;</script>
			<script type="text/javascript"
				src="http://maps.google.com/maps/api/js?v=3.2&amp;sensor=false">;</script>
			<script type="text/javascript" src="/js/map/map.js">;</script>
			<!--<script type="text/javascript" -->
			<!--src="http://maps.google.com/maps?file=api&amp;v=2&amp;key=ABQIAAAAjpkAC9ePGem0lIq5XcMiuhR_wWLPFku8Ix9i2SXYRVK3e45q1BQUd_beF8dtzKET_EteAjPdGDwqpQ">;</script> -->
		</head>
		<body onload="init();">
			<xsl:call-template name="header" />
			<div class="wrapper">
				<div class="pad-top pad-sides">
					<ul class="bread-crumbs-nav">
						<xsl:call-template name="edit-bread-crumbs">
							<xsl:with-param name="path" select="'collections'"/>
							<xsl:with-param name="title" select="'Collections'"/>
						</xsl:call-template>
					</ul>
					<div id="ingest-error-msg">

					</div>
					<xsl:call-template name="metadata" />

					<form id="page-form">
					<div>
						<h2>
							<label for="general">General Information</label>
						</h2>
						<div id="general">
							<xsl:call-template name="type">
								<xsl:with-param name="entity" select="'collection'"/>
							</xsl:call-template>

							<xsl:call-template name="title" />

							<xsl:call-template name="alternative-title" />

							<xsl:call-template name="content" />

						</div>
						<h2>
							<label for="data-availability">Data Availability</label>
						</h2>
						<div id="data-availability">

							<xsl:call-template name="page" />

							<xsl:call-template name="access-rights" />

							<xsl:call-template name="lookup-edit">
								<xsl:with-param name="title" select="'Access Service/s'" />
								<xsl:with-param name="field" select="'isaccessedvia'"/>
								<xsl:with-param name="relation">
									<xsl:value-of select="$ATOM_IS_ACCESSED_VIA" />
								</xsl:with-param>
							</xsl:call-template>

							<xsl:call-template name="lookup-edit">
								<xsl:with-param name="title" select="'Related Collection/s'" />
								<xsl:with-param name="field" select="'relation'"/>
								<xsl:with-param name="relation">
									<xsl:value-of select="$ATOM_RELATION" />
								</xsl:with-param>
							</xsl:call-template>

							<xsl:call-template name="edit-related-publications" />

							<xsl:call-template name="rights" />

							<xsl:call-template name="licence-type" />
						</div>
						<h2>
							<label for="people-and-projects">People &amp; Projects</label>
						</h2>
						<div id="people-and-projects">
							<xsl:call-template name="creators" />
							<xsl:call-template name="publishers" />
							<xsl:call-template name="projects" />
						</div>
						<h2>
							<label for="spacio-temporal">Time &amp; Space</label>
						</h2>
						<div id="spacio-temporal">
							<dl>
								<dt><label for="time-period">Time Period</label></dt>
								<dd>
									<xsl:call-template name="edit-time-period" />
								</dd>
							</dl>
							<xsl:call-template name="edit-locations" />
						</div>
						<h2>
							<label for="subjects">Topics</label>
						</h2>
						<div id="subjects">
							<xsl:call-template name="fields-of-research" />
							<xsl:call-template name="socio-economic-impacts" />
							<xsl:call-template name="type-of-activities" />
							<xsl:call-template name="keywords" />
						</div>
					</div>
					<div class="save-links-div">
						<a href="#" class="save-link" id="save-link" title="Save Record"
							onclick="DataSpace.ingestRecord('{atom:link[@rel = $REL_SELF]/@href}','collection',false, false); return false;">
							save
						</a>
						<a href="#" class="publish-link" id="publish-link" title="Publish Record"
							onclick="DataSpace.ingestRecord('{atom:link[@rel = $REL_SELF]/@href}','collection',false, true); return false;">
							publish
						</a>
					</div>
					</form>
				</div>
			</div>
			<xsl:call-template name="footer" />
		</body>
	</xsl:template>
</xsl:stylesheet>
