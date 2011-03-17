<%@ page import="net.metadata.dataspace.app.RegistryApplication" %>
<!--START: Header -->
<div id="header">
    <div id="header-inner">
        <h1><a href="http://www.uq.edu.au/" title="Home" accesskey="1">The University of Queensland</a></h1>

        <h2 style="z-index: 500;">
            <a href="/" title="UQ Data Space"><%=RegistryApplication.getApplicationContext().getRegistryTitle()%> (beta)
            </a>
        </h2>

        <div id="mininav">
            <ul>
                <li><a href="http://www.uq.edu.au/contacts/">Contacts</a></li>
                <li><a href="http://www.uq.edu.au/search/">Search</a></li>
                <li><a href="http://www.uq.edu.au/study/">Study</a></li>
                <li><a href="http://www.uq.edu.au/maps/">Maps</a></li>
                <li><a href="http://www.uq.edu.au/news/">News</a></li>
                <li><a href="http://www.uq.edu.au/events/">Events</a></li>
                <li><a href="http://www.library.uq.edu.au/">Library</a></li>
                <li><a href="http://my.uq.edu.au/">my.UQ</a></li>
            </ul>
            <div class="clear">&nbsp;</div>
        </div>
        <div id="search">
            <form id="searchbox" method="get" action="http://www.uq.edu.au/search">
                <fieldset>
                    <label for="search-entry">Search Entry</label>
                    <input id="search-entry" size="15" type="text" value="Search..." name="q" class="s"
                           onfocus="if (this.value == 'Search...') {this.value = '';}"
                           onblur="if (this.value == '') {this.value = 'Search...';}" tabindex="1"/>
                    <input name="submit" value="" class="submit" title="Search UQ" type="submit"/>
                    <input type="hidden" name="output" value="xml_no_dtd"/>
                    <input type="hidden" name="client" value="ws"/>
                    <input type="hidden" name="proxystylesheet" value="ws"/>
                </fieldset>
            </form>
        </div>
        <div class="clear">&nbsp;</div>
    </div>
</div>

<div id="topnav">
    <div id="topnav-inner">
        <ul>
            <li><a href="/">Home</a></li>
            <li><a href="/browse">Browse</a></li>
            <li><a href="/search">Search</a></li>
            <li><a href="http://www.uq.edu.au/about/">About</a></li>
        </ul>
        <%--<div class="clear">&nbsp;</div>--%>
    </div>
    <a href="#" class="signin" id="signin-link">Sign in</a>
    <fieldset id="signin_menu">
        <span id="login-error" style="color:#ff0000;"></span>

        <form id="signin" onsubmit="login(); return false;">
            <p>
                <label for="username">Username</label>
                <input id="username" name="username" value="" title="username" tabindex="4" type="text">
            </p>

            <p>
                <label for="password">Password</label>
                <input id="password" name="password" value="" title="password" tabindex="5" type="password">
            </p>

            <p class="remember">
                <input id="signin_submit" value="Sign in" tabindex="6" type="submit">
            </p>
        </form>
    </fieldset>
</div>
