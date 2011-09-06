<?xml version='1.0'?>
<!-- Transforms UQ collection profile of Atom Syndication Format to XHTML
	with embedded RDFa XSLT 1.0 Nigel Ward, 2010-12 -->
<xsl:stylesheet version="2.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:atom="http://www.w3.org/2005/Atom"
	xmlns:rdfa="http://www.w3.org/ns/rdfa#" xmlns="http://www.w3.org/1999/xhtml"
	exclude-result-prefixes="atom rdfa">

	<xsl:include href="../edit/edit-agent.xsl" />

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
		<xsl:call-template name="edit-agent"/>
	</xsl:template>
</xsl:stylesheet>
