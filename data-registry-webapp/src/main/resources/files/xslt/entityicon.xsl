<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
        xmlns:atom="http://www.w3.org/2005/Atom"
        exclude-result-prefixes="atom">

    <xsl:include href="constants.xsl"/>

    <xsl:template name="entity-icon">
        <xsl:choose>
            <xsl:when test="atom:link[@rel = $REL_TYPE]/@href = $ENTITY_COLLECTION or
             atom:link[@rel = $REL_TYPE]/@href = $ENTITY_COLLECTION">
                <xsl:call-template name="entity-icon-type">
                    <xsl:with-param name="type">collection</xsl:with-param>
                </xsl:call-template>
            </xsl:when>
            <xsl:when test="atom:link[@rel = $REL_TYPE]/@href = $ENTITY_PERSON or
            atom:link[@rel = $REL_TYPE]/@href = $ENTITY_GROUP">
                <xsl:with-param name="type">agent</xsl:with-param>
            </xsl:when>
            <xsl:when test="atom:link[@rel = $REL_TYPE]/@href = $ENTITY_PROJECT or
            atom:link[@rel = $REL_TYPE]/@href = $ENTITY_PROGRAM">
                <xsl:with-param name="type">activity</xsl:with-param>
            </xsl:when>
            <xsl:when test="atom:link[@rel = $REL_TYPE]/@href = $ENTITY_ANNOTATE or
            atom:link[@rel = $REL_TYPE]/@href = $ENTITY_ASSEMBLE or
            atom:link[@rel = $REL_TYPE]/@href = $ENTITY_CREATE or
            atom:link[@rel = $REL_TYPE]/@href = $ENTITY_GENERATE or
            atom:link[@rel = $REL_TYPE]/@href = $ENTITY_HARVEST or
            atom:link[@rel = $REL_TYPE]/@href = $ENTITY_REPORT or
            atom:link[@rel = $REL_TYPE]/@href = $ENTITY_SEARCH or
            atom:link[@rel = $REL_TYPE]/@href = $ENTITY_SYNDICATE or
            atom:link[@rel = $REL_TYPE]/@href = $ENTITY_TRANSFORM">
                <xsl:with-param name="type">service</xsl:with-param>
            </xsl:when>
        </xsl:choose>
    </xsl:template>

    <xsl:template name="entity-icon-type">
        <xsl:param name="type"/>
        <xsl:choose>
            <xsl:when test="$type = 'collection'">
                <img src="/images/icons/ic_white_collections.png" alt="Collection"/>
            </xsl:when>
            <xsl:when test="$type = 'agent'">
                <img src="/images/icons/ic_white_agents.png" alt="Agent"/>
            </xsl:when>
            <xsl:when test="$type = 'activity'">
                <img src="/images/icons/ic_white_activities.png" alt="Activity"/>
            </xsl:when>
            <xsl:when test="$type = 'service'">
                <img src="/images/icons/ic_white_services.png" alt="Service"/>
            </xsl:when>
        </xsl:choose>
    </xsl:template>


</xsl:stylesheet>