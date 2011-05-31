<%@ page import="net.metadata.dataspace.app.RegistryApplication" %>
<html>
<head>
    <jsp:include page="include/head.jsp"/>
</head>
<body>
<jsp:include page="include/header.jsp"/>
<ul class="bread-crumbs-nav">
    <li class="bread-crumbs">
        <a href="/">Home</a>
    </li>
</ul>
<div class="wrapper">
    <div class="content">
        <h1>Welcome to UQ Dataspace</h1>

        <p>A catalog of the <strong>University of Queensland's</strong> Research Data Assets</p>

        <div class="portlet-content">
            <div class="browse-portlet">
                <div class="portlet-header">
                    <a href="/search">Search</a>
                </div>
                <table width="100%">
                    <tbody>
                        <tr>
                            <td id="searching">
                                <input type="text" id="query" name="query"/>
                            </td>
                            <td><input type="button" name="search-submit" id="search-submit" value="Search"
                               onclick="doSearch($('#query').val()); return false;" style=""/></td>
                        </tr>
                        <tr>
                            <td>TAG CLOUD HERE</td>
                        </tr>
                    </tbody>
                </table>
            </div>
            <div class="browse-portlet">
                <div class="portlet-header">
                    <a href="/collections">Recently added collections</a>
                </div>
                <ul class="portlet-list">
                    <li>List of recent collections here</li>
                </ul>
            </div>
        </div>

        <p>This registry syndicates data to </br>
        <a href="http://services.ands.org.au/home/orca/rda/">
        <img src="https://services.ands.org.au/home/orca/rda/_images/ANDS_logo.gif"/></a></p>
    </div>
</div>
<jsp:include page="include/footer.jsp"/>
</body>
</html>
