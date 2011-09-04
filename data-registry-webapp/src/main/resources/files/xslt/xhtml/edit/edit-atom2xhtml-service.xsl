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
			<title>
				<xsl:value-of select="atom:title" />
			</title>
			<link href="/css/description.css" rel="stylesheet" type="text/css" />
			<xsl:call-template name="head" />
		</head>
		<body>
			<!-- the collection description itself -->
			<xsl:text>
            </xsl:text>
			<xsl:comment>
				Service description
			</xsl:comment>
			<xsl:call-template name="header" />
			<div class="wrapper">
				<div class="pad-top pad-sides">
					<ul class="bread-crumbs-nav">
						<xsl:call-template name="edit-bread-crumbs">
							<xsl:with-param name="path" select="'services'"/>
							<xsl:with-param name="title">
								Services
							</xsl:with-param>
						</xsl:call-template>
					</ul>
					<div id="ingest-error-msg">

					</div>
					<form id="page-form">
						<div>
							<h2>
								<label for="general">General Information</label>
							</h2>
							<div id="general">
								<xsl:call-template name="title" />
								<xsl:call-template name="alternative-title" />
								<xsl:call-template name="type">
									<xsl:with-param name="entity" select="'service'" />
								</xsl:call-template>
								<xsl:call-template name="content" />
								<xsl:call-template name="page" />
							</div>
							<h2>
								<label for="related">Related</label>
							</h2>
							<div id="related">
								<xsl:call-template name="lookup-edit">
									<xsl:with-param name="title">
										Publisher
									</xsl:with-param>
									<xsl:with-param name="field" select="'publisher'" />
									<xsl:with-param name="relation">
										<xsl:value-of select="$ATOM_PUBLISHER" />
									</xsl:with-param>
								</xsl:call-template>
								<xsl:call-template name="lookup-edit">
									<xsl:with-param name="title">
										Supported By
									</xsl:with-param>
									<xsl:with-param name="field" select="'issupportedby'" />
									<xsl:with-param name="relation">
										<xsl:value-of select="$ATOM_IS_SUPPORTED_BY" />
									</xsl:with-param>
								</xsl:call-template>
							</div>
						</div>
						<div class="save-links-div">
							<a href="#" class="save-link" id="save-link" title="Save Record"
								onclick="DataSpace.ingestRecord('{atom:link[@rel = $REL_SELF]/@href}','service',false, false); return false;">
								save
							</a>
							<a href="#" class="publish-link" id="publish-link" title="Publish Record"
								onclick="DataSpace.ingestRecord('{atom:link[@rel = $REL_SELF]/@href}','service',false, true); return false;">
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
