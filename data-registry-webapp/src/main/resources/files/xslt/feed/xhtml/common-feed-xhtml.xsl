<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns:atom="http://www.w3.org/2005/Atom"
                xmlns:app="http://www.w3.org/2007/app"
                xmlns="http://www.w3.org/1999/xhtml"
                exclude-result-prefixes="atom app">
    <xsl:param name="currentUser"/>
    <xsl:output method="html" media-type="application/xhtml+xml" indent="yes"/>
    <xsl:template match="atom:entry">
        <div class="record">
            <xsl:choose>
                <xsl:when test="$currentUser and app:control/app:draft='yes'">
                    <span class="draft">(draft)</span>
                    <a href="{atom:id}">
                        <xsl:value-of select="atom:title"/>
                    </a>
                    <span class="author">
                        <xsl:value-of select="atom:author/atom:name"/>
                    </span>
                    <br/>
                    <xsl:value-of select="atom:content"/>
                    <span class="record-date">
                        <xsl:value-of select="atom:updated"/>
                    </span>
                </xsl:when>
                <xsl:otherwise>
                    <a href="{atom:id}">
                        <xsl:value-of select="atom:title"/>
                    </a>
                    <span class="author">
                        <xsl:value-of select="atom:author/atom:name"/>
                    </span>
                    <br/>
                    <xsl:value-of select="atom:content"/>
                    <span class="record-date">
                        <xsl:value-of select="atom:updated"/>
                    </span>
                </xsl:otherwise>
            </xsl:choose>
        </div>
    </xsl:template>
</xsl:stylesheet>