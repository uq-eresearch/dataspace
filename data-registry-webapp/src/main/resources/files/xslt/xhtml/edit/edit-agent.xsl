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

	<xsl:template name="edit-agent">
		<head>
			<title>
				<xsl:choose>
					<xsl:when test="atom:title">
						<xsl:value-of select="atom:title" />
					</xsl:when>
					<xsl:otherwise>
						New Agent
					</xsl:otherwise>
				</xsl:choose>
			</title>
			<link href="/css/description.css" rel="stylesheet" type="text/css" />
			<xsl:call-template name="head" />
		</head>
		<body>
			<xsl:call-template name="header" />
			<div class="wrapper">
				<div class="pad-top pad-sides">
					<ul class="bread-crumbs-nav">
						<xsl:call-template name="edit-bread-crumbs">
							<xsl:with-param name="path" select="'agents'" />
							<xsl:with-param name="title" select="'Agents'" />
						</xsl:call-template>
					</ul>
                     <!-- buttons -->
                    <div class="button-bar">
                        <xsl:call-template name="edit-button-bar">
                            <xsl:with-param name="path" select="'agents'"/>
                            <xsl:with-param name="type" select="'agent'"/>
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
								<xsl:call-template name="title">
									<xsl:with-param name="label" select="'Full Name'"/>
								</xsl:call-template>
								<xsl:call-template name="name-components"/>
								<xsl:call-template name="alternative-title">
									<xsl:with-param name="label" select="'Also Known As'"/>
								</xsl:call-template>
								<xsl:call-template name="type">
									<xsl:with-param name="entity" select="'agent'" />
								</xsl:call-template>

								<xsl:call-template name="email" />
								<xsl:call-template name="content" />
								<xsl:call-template name="page" />
							</div>
							<h2>
								<label for="related">Related</label>
							</h2>
							<div id="related">
								<xsl:call-template name="lookup-edit">
									<xsl:with-param name="title">
										Created
									</xsl:with-param>
									<xsl:with-param name="field" select="'iscollectorof'" />
									<xsl:with-param name="relation">
										<xsl:value-of select="$ATOM_IS_COLLECTOR_OF" />
									</xsl:with-param>
								</xsl:call-template>
								<xsl:call-template name="lookup-edit">
									<xsl:with-param name="title">
										Participating In
									</xsl:with-param>
									<xsl:with-param name="field" select="'isparticipantin'" />
									<xsl:with-param name="relation">
										<xsl:value-of select="$ATOM_IS_PARTICIPANT_IN" />
									</xsl:with-param>
								</xsl:call-template>
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
					</form>
                    <!-- actions -->
                    <div class="button-bar">
                            <xsl:call-template name="edit-actions">
                                <xsl:with-param name="type">collection</xsl:with-param>
                            </xsl:call-template>
                    </div>
				</div>
			</div>
			<xsl:call-template name="footer" />
		</body>
	</xsl:template>
</xsl:stylesheet>
