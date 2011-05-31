<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <xsl:variable name="NS_FOAF">http://xmlns.com/foaf/0.1/</xsl:variable>
    <xsl:variable name="NS_ANDS">http://www.ands.org.au/ontologies/ns/0.1/VITRO-ANDS.owl#</xsl:variable>
    <xsl:variable name="NS_DC">http://purl.org/dc/terms/</xsl:variable>
    <xsl:variable name="NS_DCMITYPE">http://purl.org/dc/dcmitype/</xsl:variable>
    <xsl:variable name="NS_CLD">http://purl.org/cld/terms/</xsl:variable>
    <xsl:variable name="NS_VIVO">http://vivoweb.org/ontology/core#</xsl:variable>
    <xsl:variable name="NS_ORE">http://www.openarchives.org/ore/terms/</xsl:variable>
    <xsl:variable name="NS_GEORSS">http://www.georss.org/georss/</xsl:variable>
    <xsl:variable name="NS_RDFA">http://www.w3.org/ns/rdfa#</xsl:variable>
    <xsl:variable name="NS_RDF_99">http://www.w3.org/1999/02/22-rdf-syntax-ns</xsl:variable>
    <xsl:variable name="NS_EF">http://www.e-framework.org/Contributions/ServiceGenres/</xsl:variable>

    <xsl:variable name="TYPE_ATOM_FEED">application/atom+xml; type=feed</xsl:variable>

    <xsl:variable name="REL_SELF">self</xsl:variable>
    <xsl:variable name="REL_VIA">via</xsl:variable>
    <xsl:variable name="REL_RELATED">related</xsl:variable>
    <xsl:variable name="REL_ALTERNATE">alternate</xsl:variable>
    <xsl:variable name="REL_LATEST_VERSION">latest-version</xsl:variable>
    <xsl:variable name="REL_LICENSE">license</xsl:variable>

    <xsl:variable name="REL_TYPE">
        <xsl:value-of select="concat($NS_RDF_99 ,'#type')"/>
    </xsl:variable>
    <xsl:variable name="ATOM_CREATOR">
        <xsl:value-of select="concat($NS_DC ,'creator')"/>
    </xsl:variable>
    <xsl:variable name="ATOM_IS_MANAGER_OF">
        <xsl:value-of select="concat($NS_ANDS ,'isManagerOf')"/>
    </xsl:variable>
    <xsl:variable name="ATOM_PUBLISHER">
        <xsl:value-of select="concat($NS_DC ,'publisher')"/>
    </xsl:variable>
    <xsl:variable name="ATOM_IS_ACCESSED_VIA">
        <xsl:value-of select="concat($NS_CLD ,'isAccessedVia')"/>
    </xsl:variable>
    <xsl:variable name="ATOM_IS_LOCATED_AT">
        <xsl:value-of select="concat($NS_FOAF ,'page')"/>
    </xsl:variable>
    <xsl:variable name="ATOM_IS_OUTPUT_OF">
        <xsl:value-of select="concat($NS_ANDS ,'isOutputOf')"/>
    </xsl:variable>
    <xsl:variable name="ATOM_IS_SUPPORTED_BY">
        <xsl:value-of select="concat($NS_ANDS, 'isSupportedBy')"/>
    </xsl:variable>
    <xsl:variable name="ATOM_HAS_PARTICIPANT">
        <xsl:value-of select="concat($NS_ANDS ,'hasParticipant')"/>
    </xsl:variable>
    <xsl:variable name="ATOM_HAS_OUTPUT">
        <xsl:value-of select="concat($NS_ANDS ,'hasOutput')"/>
    </xsl:variable>
    <xsl:variable name="ATOM_IS_COLLECTOR_OF">
        <xsl:value-of select="concat($NS_FOAF ,'made')"/>
    </xsl:variable>
    <xsl:variable name="ATOM_MBOX">
        <xsl:value-of select="concat($NS_FOAF ,'mbox')"/>
    </xsl:variable>
    <xsl:variable name="ATOM_IS_PARTICIPANT_IN">
        <xsl:value-of select="concat($NS_FOAF ,'currentProject')"/>
    </xsl:variable>
    <xsl:variable name="ATOM_HONORIFIC">
        <xsl:value-of select="concat($NS_FOAF, 'title')"/>
    </xsl:variable>
    <xsl:variable name="ATOM_GIVEN_NAME">
        <xsl:value-of select="concat($NS_FOAF, 'givenName')"/>
    </xsl:variable>
    <xsl:variable name="ATOM_FAMILY_NAME">
        <xsl:value-of select="concat($NS_FOAF, 'familyName')"/>
    </xsl:variable>
    <xsl:variable name="ATOM_RELATION">
        <xsl:value-of select="concat($NS_DC ,'relation')"/>
    </xsl:variable>
    <xsl:variable name="ATOM_SPATIAL">
        <xsl:value-of select="concat($NS_DC ,'spatial')"/>
    </xsl:variable>
    <xsl:variable name="ATOM_IS_REFERENCED_BY">
        <xsl:value-of select="concat($NS_DC, 'isReferencedBy')"/>
    </xsl:variable>

    <xsl:variable name="RDF_DESCRIBES">
        <xsl:value-of select="concat($NS_ORE, 'describes')"/>
    </xsl:variable>
    <xsl:variable name="RDFA_TEMPORAL">
        <xsl:value-of select="concat($NS_DC ,'temporal')"/>
    </xsl:variable>
    <xsl:variable name="RDFA_ACCESS_RIGHTS">
        <xsl:value-of select="concat($NS_DC ,'accessRights')"/>
    </xsl:variable>

    <xsl:variable name="RDFA_ALTERNATIVE">
        <xsl:value-of select="concat($NS_DC ,'alternative')"/>
    </xsl:variable>

    <xsl:variable name="ENTITY_COLLECTION">
        <xsl:value-of select="concat($NS_DCMITYPE, 'Collection')"/>
    </xsl:variable>
    <xsl:variable name="ENTITY_DATASET">
        <xsl:value-of select="concat($NS_DCMITYPE, 'Dataset')"/>
    </xsl:variable>
    <xsl:variable name="ENTITY_PERSON">
        <xsl:value-of select="concat($NS_FOAF, 'Person')"/>
    </xsl:variable>
    <xsl:variable name="ENTITY_GROUP">
        <xsl:value-of select="concat($NS_FOAF, 'Group')"/>
    </xsl:variable>
    <xsl:variable name="ENTITY_PROJECT">
        <xsl:value-of select="concat($NS_FOAF , 'Project')"/>
    </xsl:variable>
        <xsl:variable name="ENTITY_PROGRAM">
        <xsl:value-of select="concat($NS_VIVO , 'Program')"/>
    </xsl:variable>

    <xsl:variable name="ENTITY_CREATE">
        <xsl:value-of select="concat($NS_EF,'Service')"/>
    </xsl:variable>
    <xsl:variable name="ENTITY_GENERATE">
        <xsl:value-of select="concat($NS_EF,'Generate')"/>
    </xsl:variable>
    <xsl:variable name="ENTITY_REPORT">
        <xsl:value-of select="concat($NS_EF,'Report')"/>
    </xsl:variable>
    <xsl:variable name="ENTITY_ANNOTATE">
        <xsl:value-of select="concat($NS_EF,'Annotate')"/>
    </xsl:variable>
    <xsl:variable name="ENTITY_TRANSFORM">
        <xsl:value-of select="concat($NS_EF,'Transform')"/>
    </xsl:variable>
    <xsl:variable name="ENTITY_ASSEMBLE">
        <xsl:value-of select="concat($NS_EF,'Assemble')"/>
    </xsl:variable>
    <xsl:variable name="ENTITY_HARVEST">
        <xsl:value-of select="concat($NS_EF,'Harvest')"/>
    </xsl:variable>
    <xsl:variable name="ENTITY_SEARCH">
        <xsl:value-of select="concat($NS_EF,'Search')"/>
    </xsl:variable>
        <xsl:variable name="ENTITY_SYNDICATE">
        <xsl:value-of select="concat($NS_EF,'Syndicate')"/>
    </xsl:variable>

    <xsl:variable name="SCHEME_FOR">http://purl.org/anzsrc/for</xsl:variable>
    <xsl:variable name="PREFIX_FOR">
        <xsl:value-of select="concat($SCHEME_FOR, '/#field_')"></xsl:value-of>
    </xsl:variable>
    <xsl:variable name="SCHEME_SEO">http://purl.org/anzsrc/seo</xsl:variable>
    <xsl:variable name="PREFIX_SEO">
        <xsl:value-of select="concat($SCHEME_SEO, '/#field_')"></xsl:value-of>
    </xsl:variable>
    <xsl:variable name="SCHEME_TOA">http://purl.org/anzsrc/toa</xsl:variable>
    <xsl:variable name="PREFIX_TOA">
        <xsl:value-of select="concat($SCHEME_TOA, '/#field_')"></xsl:value-of>
    </xsl:variable>

</xsl:stylesheet>