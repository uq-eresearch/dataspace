<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <!--Constants-->
    <xsl:variable name="NS_FOAF">http://xmlns.com/foaf/0.1/</xsl:variable>
    <xsl:variable name="NS_ANDS">http://www.ands.org.au/ontologies/ns/0.1/VITRO-ANDS.owl#</xsl:variable>
    <xsl:variable name="NS_DC">http://purl.org/dc/terms/</xsl:variable>
    <xsl:variable name="NS_DCMITYPE">http://purl.org/dc/dcmitype/</xsl:variable>
    <xsl:variable name="NS_CLD">http://purl.org/cld/terms/</xsl:variable>
    <xsl:variable name="NS_VIVO">http://vivoweb.org/ontology/core#</xsl:variable>
    <xsl:variable name="NS_ORE">http://www.openarchives.org/ore/terms/</xsl:variable>
    <xsl:variable name="NS_GEORSS">http://www.georss.org/georss/</xsl:variable>
    <xsl:variable name="NS_RDFA">http://www.w3.org/ns/rdfa#</xsl:variable>

    <xsl:variable name="GROUP_LIST">https://services.ands.org.au/home/orca/services/getRegistryObjectGroups.php
    </xsl:variable>

    <xsl:variable name="REL_SELF">self</xsl:variable>
    <xsl:variable name="REL_VIA">via</xsl:variable>
    <xsl:variable name="REL_RELATED">related</xsl:variable>
    <xsl:variable name="REL_ALTERNATE">alternate</xsl:variable>
    <xsl:variable name="REL_LATEST_VERSION">latest-version</xsl:variable>

    <xsl:variable name="ATOM_CREATOR">
        <xsl:value-of select="concat($NS_DC ,'creator')"/>
    </xsl:variable>
    <xsl:variable name="ATOM_PUBLISHER">
        <xsl:value-of select="concat($NS_DC ,'publisher')"/>
    </xsl:variable>
    <xsl:variable name="ATOM_IS_ACCESSED_VIA">
        <xsl:value-of select="concat($NS_CLD ,'isAccessedVia')"/>
    </xsl:variable>
    <xsl:variable name="ATOM_IS_LOCATED_AT">
        <xsl:value-of select="concat($NS_CLD ,'isLocatedAt')"/>
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
    <xsl:variable name="ATOM_IS_PARTICIPANT_IN">
        <xsl:value-of select="concat($NS_FOAF ,'currentProject')"/>
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

    <xsl:variable name="ENTITY_ACTIVITY">
        <xsl:value-of select="concat($NS_FOAF , 'Project')"/>
    </xsl:variable>
    <xsl:variable name="ENTITY_COLLECTION">
        <xsl:value-of select="concat($NS_DCMITYPE, 'Collection')"/>
    </xsl:variable>
    <xsl:variable name="ENTITY_PARTY">
        <xsl:value-of select="concat($NS_FOAF, 'Agent')"/>
    </xsl:variable>
    <xsl:variable name="ENTITY_SERVICE">
        <xsl:value-of select="concat($NS_VIVO,'Service')"/>
    </xsl:variable>

</xsl:stylesheet>