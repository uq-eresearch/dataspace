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

	<xsl:template name="edit-service">
		<head>
			<title>
				<xsl:choose>
					<xsl:when test="atom:title">
						<xsl:value-of select="atom:title" />
					</xsl:when>
					<xsl:otherwise>
						New Service
					</xsl:otherwise>
				</xsl:choose>
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
							<xsl:with-param name="path" select="'services'" />
							<xsl:with-param name="title" select="'Services'" />
						</xsl:call-template>
					</ul>
                     <!-- buttons -->
                    <div class="button-bar">
                        <xsl:call-template name="edit-button-bar">
                            <xsl:with-param name="path" select="'services'"/>
                            <xsl:with-param name="type" select="'service'"/>
                        </xsl:call-template>
                    </div>
					<div id="ingest-error-msg">

					</div>
					<xsl:if test="atom:id">
						<xsl:call-template name="metadata" />
					</xsl:if>

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
									<xsl:with-param name="title">Publisher</xsl:with-param>
									<xsl:with-param name="field" select="'publisher'" />
									<xsl:with-param name="relation">
										<xsl:value-of select="$ATOM_PUBLISHER" />
									</xsl:with-param>
								</xsl:call-template>
								<xsl:call-template name="lookup-edit">
									<xsl:with-param name="title">Supported By</xsl:with-param>
									<xsl:with-param name="field" select="'issupportedby'" />
									<xsl:with-param name="relation">
										<xsl:value-of select="$ATOM_IS_SUPPORTED_BY" />
									</xsl:with-param>
                                    <xsl:with-param name="classes" select="'required'"/>
								</xsl:call-template>
							</div>
						</div>
                        <input type="hidden" name="source-user" value="{$currentUser}"/>
                        <input type="hidden" name="source-email" value="{$currentEmail}"/>
					</form>
                    <!-- actions -->
                    <div class="button-bar">
                            <xsl:call-template name="edit-actions">
                                <xsl:with-param name="path" select="'services'"/>
                                <xsl:with-param name="type" select="'service'"/>
                            </xsl:call-template>
                    </div>
				</div>
			</div>
			<xsl:call-template name="footer" />
		</body>
	</xsl:template>
</xsl:stylesheet>
