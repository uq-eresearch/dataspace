<?xml version='1.0'?>
<!--
          Transforms UQ collection profile of Atom Syndication Format to
          XHTML with embedded RDFa

          XSLT 1.0

          Abdul Alabri, 2010-12

    -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
                xmlns="http://www.w3.org/1999/xhtml">

    <xsl:param name="currentUser"/>
    <xsl:output method="html" media-type="application/xhtml+xml" indent="yes"/>
    <xsl:template name="header">
        <div id="header">
            <div id="header-inner">
                <h1>
                    <a href="http://www.uq.edu.au/" title="Home" accesskey="1">The University of Queensland</a>
                </h1>
                <h2 style="z-index: 500;">
                    <a href="/" title="UQ Data Space">UQ Data Collections Registry</a>
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
                    <form id="searchbox" method="get" action="http://www.uq.edu.au/search">
                        <fieldset>
                            <label for="search-entry">Search Entry</label>
                            <input id="search-entry" size="15" type="text" value="Search..." name="q" class="s"
                                   onfocus="if (this.value == 'Search...') {this.value = ''}"
                                   onblur="if (this.value == '') {this.value = 'Search...'}" tabindex="1"/>
                            <input name="submit" value="" class="submit" title="Search UQ" type="submit"/>
                            <input type="hidden" name="output" value="xml_no_dtd"/>
                            <input type="hidden" name="client" value="ws"/>
                            <input type="hidden" name="proxystylesheet" value="ws"/>
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
                        <a href="#" class="browse" id="browse">Browse</a>
                        <fieldset id="browse_menu">
                            <ul id="browse_submenu" class="submenu">
                                <li>
                                    <a href="/collections">Collections</a>
                                </li>
                                <li>
                                    <a href="/parties">Parties</a>
                                </li>
                                <li>
                                    <a href="/services">Services</a>
                                </li>
                                <li>
                                    <a href="/activities">Activities</a>
                                </li>
                            </ul>
                        </fieldset>
                    </li>
                    <li>
                        <a href="/search">Search</a>
                    </li>
                    <li>
                        <a href="http://www.uq.edu.au/about/">About</a>
                    </li>
                </ul>
            </div>
            <xsl:call-template name="loginlink"/>
            <fieldset id="signin_menu">

                <form action="#" method="post" id="signin" onsubmit="login(); return false;">
                    <p id="login-error" style="color:#ff0000;">

                    </p>
                    <p>
                        <label for="username">Username</label>
                        <input id="username" name="username" value="" title="username" tabindex="4" type="text"/>
                    </p>
                    <p>
                        <label for="password">Password</label>
                        <input id="password" name="password" value="" title="password" tabindex="5" type="password"/>
                    </p>
                    <p class="remember">
                        <input id="signin_submit" value="Sign in" tabindex="6" type="submit"/>
                    </p>
                </form>
            </fieldset>
        </div>
    </xsl:template>

    <xsl:template name="loginlink">
        <xsl:choose>
            <xsl:when test="$currentUser">
                <a href="/logout" class="signout" id="signin-link">Sign out</a>
            </xsl:when>
            <xsl:otherwise>
                <a href="logins" class="signin" id="signin-link">Sign in</a>
            </xsl:otherwise>
        </xsl:choose>
    </xsl:template>
</xsl:stylesheet>