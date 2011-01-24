<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform">
    <!--Constants-->
    <xsl:variable name="NS_FOAF">http://xmlns.com/foaf/0.1/</xsl:variable>
    <xsl:variable name="NS_ANDS">http://www.ands.org.au/ontologies/ns/0.1/VITRO-ANDS.owl#</xsl:variable>
    <xsl:variable name="NS_DC">http://purl.org/dc/terms/</xsl:variable>
    <xsl:variable name="NS_DCMITYPE">http://purl.org/dc/terms/dcmitype/</xsl:variable>
    <xsl:variable name="NS_CLD">http://purl.org/cld/terms/</xsl:variable>
    <xsl:variable name="NS_VIVO">http://vivoweb.org/ontology/core#</xsl:variable>
    <xsl:variable name="NS_ORE">http://www.openarchives.org/ore/terms/</xsl:variable>
    <xsl:variable name="NS_GEORSS">http://www.georss.org/georss/</xsl:variable>
    <xsl:variable name="NS_RDFA">http://www.w3.org/ns/rdfa#</xsl:variable>

    <xsl:variable name="GROUP_LIST">https://services.ands.org.au/home/orca/services/getRegistryObjectGroups.php
    </xsl:variable>

    <xsl:variable name="REL_RELATED">related</xsl:variable>
    <xsl:variable name="REL_ALTERNATE">alternate</xsl:variable>
    <xsl:variable name="REL_LATEST_VERSION">latest-version</xsl:variable>


</xsl:stylesheet>