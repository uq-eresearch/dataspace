<?xml version='1.0'?>
<!--
          Transforms UQ collection profile of Atom Syndication Format to
          XHTML with embedded RDFa

          XSLT 1.0

          Abdul Alabri, 2010-12

    -->
<xsl:stylesheet version="2.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="http://www.w3.org/1999/xhtml"
                xmlns:atom="http://www.w3.org/2005/Atom"
                exclude-result-prefixes="atom">

    <xsl:param name="currentUser"/>
    <xsl:param name="applicationName"/>
    <xsl:template name="header">
        <div id="header">
            <div id="header-inner">
                <h1>
                    <a href="http://www.uq.edu.au/" title="Home" accesskey="1">The University of Queensland</a>
                </h1>
                <h2 style="z-index: 500;">
                    <a href="/" title="{$applicationName}">
                        <xsl:value-of select="$applicationName"/>
                    </a>
                </h2>
                <div id="mininav">
                    <ul>
                        <li>
                            <a href="http://www.uq.edu.au/contacts/">Contacts</a>
                        </li>
                        <li>
                            <a href="http://www.uq.edu.au/search/">Search</a>
                        </li>
                        <li>
                            <a href="http://www.uq.edu.au/study/">Study</a>
                        </li>
                        <li>
                            <a href="http://www.uq.edu.au/maps/">Maps</a>
                        </li>
                        <li>
                            <a href="http://www.uq.edu.au/news/">News</a>
                        </li>
                        <li>
                            <a href="http://www.uq.edu.au/events/">Events</a>
                        </li>
                        <li>
                            <a href="http://www.library.uq.edu.au/">Library</a>
                        </li>
                        <li>
                            <a href="http://my.uq.edu.au/">my.UQ</a>
                        </li>
                    </ul>
                </div>
                <div id="search">
                    <form id="searchbox" method="get" action="/search">
                        <fieldset>
                            <label for="search-entry">Search Entry</label>
                            <input id="search-entry" size="15" type="text" value="Search..." name="q" class="s"/>
                            <input name="submit" value="" class="submit" title="Search UQ" type="submit"/>
                        </fieldset>
                    </form>
                </div>
            </div>
        </div>
        <div id="topnav">
            <div id="topnav-inner">
                <ul>
                    <li>
                        <a href="/">Home</a>
                    </li>
                    <li>
                        <a href="/collections">Browse</a>
                    </li>
                    <li>
                        <a href="/search">Search</a>
                    </li>
                    <li>
                        <a href="/about">About</a>
                    </li>
                </ul>
                <xsl:call-template name="loginlink"/>
            </div>

        </div>
    </xsl:template>

	<xsl:template name="loginlink">
		<xsl:choose>
			<xsl:when test="$currentUser">
				<a href="#" class="signout" id="signout-link">Sign out <span id="current-user"><xsl:value-of select="$currentUser"/></span></a>
			</xsl:when>
			<xsl:otherwise>
				<a href="#" class="signin" id="signin-link">Sign in</a>
			</xsl:otherwise>
		</xsl:choose>
	</xsl:template>
</xsl:stylesheet>