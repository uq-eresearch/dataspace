<?xml version='1.0'?>
<!--
          Transforms UQ collection profile of Atom Syndication Format to
          Link Data represented in RDF/XML

          XSLT 1.0

          Nigel Ward, 2010-11

    -->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:rdf="http://www.w3.org/1999/02/22-rdf-syntax-ns#"
                xmlns:ore="http://www.openarchives.org/ore/terms/" xmlns:atom="http://www.w3.org/2005/Atom"
                xmlns:foaf="http://xmlns.com/foaf/0.1/" xmlns:dc="http://purl.org/dc/elements/1.1/"
                xmlns:dcterms="http://purl.org/dc/terms/"
                xmlns:cld="http://purl.org/cld/terms/"
                xmlns:ands="http://www.ands.org.au/ontologies/ns/0.1/VITRO-ANDS.owl#"
                xmlns:rdfa="http://www.w3.org/ns/rdfa#"
                xmlns:georss="http://www.georss.org/georss"
                xmlns:owl="http://www.w3.org/2002/07/owl#"
                xmlns:geo="http://www.w3.org/2003/01/geo/wgs84_pos#">

    <xsl:include href="common-rdf.xsl"/>
    <xsl:output method="xml" media-type="application/rdf+xml" indent="yes"/>

    <xsl:template match="/">
        <rdf:RDF>
            <xsl:apply-templates/>
        </rdf:RDF>
    </xsl:template>

    <!-- *** Atom entry ***-->

    <xsl:template match="atom:entry">
        <!-- the collection description itself -->
        <xsl:text>
	    </xsl:text>
        <xsl:comment>Collection description</xsl:comment>
        <xsl:text>
	    </xsl:text>

        <rdf:Description
                rdf:about="{atom:link[@rel=$RDF_DESCRIBES]/@href}">
            <!-- alternate entity URI -->
            <xsl:if test="atom:id != atom:link[@rel = $RDF_DESCRIBES]/@href">
                <xsl:apply-templates select="atom:id"/>
            </xsl:if>
            <!-- description type -->
            <xsl:apply-templates select="atom:link[@rel=$REL_TYPE]"/>
            <!-- title -->
            <xsl:apply-templates select="atom:title"/>
            <xsl:apply-templates select="rdfa:meta[@property=$RDFA_ALTERNATIVE]"/>
            <!-- description -->
            <xsl:apply-templates select="atom:content"/>
            <!-- web page -->
            <xsl:apply-templates select="atom:link[@rel=$ATOM_IS_LOCATED_AT]"/>
            <!-- email -->
            <xsl:apply-templates select="atom:link[@REL=$ATOM_MBOX]"/>
            <!-- subjects -->
            <xsl:apply-templates select="atom:category"/>
            <!-- creator -->
            <xsl:apply-templates select="atom:author"/>
            <!-- manager -->
            <xsl:apply-templates select="atom:link[@rel=$ATOM_PUBLISHER]"/>
            <!-- generating activity -->
            <xsl:apply-templates select="atom:link[@rel=$ATOM_IS_OUTPUT_OF]"/>
            <!--<xsl:apply-templates select="atom:link[@rel=$NS_GROUP]"/>-->
            <!--&lt;!&ndash; accessed via service &ndash;&gt;-->
            <xsl:apply-templates select="atom:link[@rel=$ATOM_IS_ACCESSED_VIA]"/>
            <!-- related info -->
            <xsl:apply-templates select="atom:link[@rel=$REL_RELATED]"/>
            <!-- rights descriptions -->
            <xsl:apply-templates select="atom:rights"/>
            <xsl:apply-templates select="rdfa:meta[@property=$RDFA_ACCESS_RIGHTS]"/>
            <!-- temporal coverage -->
            <xsl:apply-templates select="rdfa:meta[@property=$RDFA_TEMPORAL]"/>
            <!-- spatial coverage -->
            <xsl:apply-templates select="atom:link[@rel=$ATOM_SPATIAL]"/>
            <xsl:apply-templates select="georss:point"/>
            <xsl:apply-templates select="georss:polygon"/>
            <!-- related publications -->
            <xsl:apply-templates select="atom:link[@rel=$ATOM_IS_REFERENCED_BY]"/>

            <!-- link to metadata about the description -->
            <ore:isDescribedBy rdf:resource="{atom:link[@rel=$REL_SELF]/@href}"/>
        </rdf:Description>

        <!-- metadata about the description -->
        <xsl:text>
	    </xsl:text>
        <xsl:comment>Metadata about the description</xsl:comment>
        <xsl:text>
	    </xsl:text>
        <rdf:Description rdf:about="{atom:link[@rel=$REL_SELF]/@href}">
            <!-- description publisher -->
            <xsl:apply-templates select="atom:source/atom:link[@rel=$ATOM_PUBLISHER]"/>
            <!-- description creator -->
            <xsl:apply-templates select="atom:source/atom:author"/>
            <!-- description update date -->
            <xsl:apply-templates select="atom:updated"/>

            <!-- description source -->
            <xsl:apply-templates select="atom:source"/>
            <xsl:apply-templates select="atom:link[@rel=$REL_VIA]"/>

            <!-- alternate formats for description -->
            <xsl:apply-templates select="atom:link[@rel=$REL_ALTERNATE]"/>
        </rdf:Description>
    </xsl:template>

    <!-- *** collection description elements *** -->



    <!-- generating activity -->
    <xsl:template
            match="atom:link[@rel=$ATOM_IS_OUTPUT_OF]">
        <ands:isOutputOf>
            <ands:Activity rdf:about="{@href}">
                <xsl:if test="@title">
                    <dc:title>
                        <xsl:value-of select="@title"/>
                    </dc:title>
                </xsl:if>
            </ands:Activity>
        </ands:isOutputOf>
    </xsl:template>

    <!-- accessed via service -->
    <xsl:template
            match="atom:link[@rel=$ATOM_IS_ACCESSED_VIA]">
        <cld:isAccessedVia rdf:about="{@href}">
            <xsl:if test="@title">
                <dc:title>
                    <xsl:value-of select="@title"/>
                </dc:title>
            </xsl:if>
        </cld:isAccessedVia>
    </xsl:template>

    <!-- related publications -->
    <xsl:template match="atom:link[@rel=$ATOM_IS_REFERENCED_BY]">
        <dcterms:isReferencedBy>
            <foaf:Document rdf:about="{@href}">
                <xsl:if test="@title">
                    <dcterms:title>
                        <xsl:value-of select="@title"/>
                    </dcterms:title>
                </xsl:if>
            </foaf:Document>
        </dcterms:isReferencedBy>
    </xsl:template>

</xsl:stylesheet>
