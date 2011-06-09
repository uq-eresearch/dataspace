<%@ page import="net.metadata.dataspace.app.RegistryApplication" %>
<!--START: Header -->
<div id="header">
    <div id="header-inner">
        <h1><a href="http://www.uq.edu.au/" title="Home" accesskey="1">The University of Queensland</a></h1>

        <h2 style="z-index: 500;">
            <a href="/"
               title="<%=RegistryApplication.getApplicationContext().getRegistryTitle()%>"><%=RegistryApplication.getApplicationContext().getRegistryTitle()%>
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
            <form id="searchbox" method="get" action="/search">
                <fieldset>
                    <label for="search-entry">Search Entry</label>
                    <input id="search-entry" size="15" type="text" value="Search..." name="q" class="s"/>
                    <input name="submit" value="" class="submit" title="Search UQ" type="submit"/>
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
            <li><a href="/about">About</a></li>
        </ul>
    </div>
    <a href="#" class="signin" id="signin-link">Sign in</a>
    <fieldset id="signin_menu">
        <form action="#" method="post" id="signin" onsubmit="login(); return false;">
            <p id="login-error" style="color:#ff0000;">

            </p>

            <p>
                <label for="username">Username</label>
                <input id="username" name="username" value="" title="username" type="text"/>
            </p>

            <p>
                <label for="password">Password</label>
                <input id="password" name="password" value="" title="password" type="password"/>
            </p>

            <p class="remember">
                <input id="signin_submit" value="Sign in" type="submit"/>
            </p>
        </form>
    </fieldset>
</div>
