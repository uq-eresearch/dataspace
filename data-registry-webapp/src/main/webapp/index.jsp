<%@ page import="net.metadata.dataspace.app.RegistryApplication" %>
<%@ page import="net.metadata.dataspace.data.model.record.Collection" %>
<%@ page import="net.metadata.dataspace.data.model.version.CollectionVersion" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%

    List<Collection> recentPublishedCollections = RegistryApplication.getApplicationContext().getDaoManager().getCollectionDao().getRecentPublished(5);
    List<CollectionVersion> collectionVersions = new ArrayList<CollectionVersion>();
    for (Collection collection : recentPublishedCollections) {
        collectionVersions.add(collection.getPublished());
    }
%>
<html>
<head>
    <jsp:include page="include/head.jsp"/>
    <script type="text/javascript">
        $(document).ready(function() {
            $("#query").focus();
            $('#query').keyup(function(event) {
                if (event.keyCode == '13') {
                    event.preventDefault();
                    goToSearchPage();
                }
            });


//            Manager = new AjaxSolr.Manager({solrUrl: 'http://evolvingweb.ca/solr/reuters/'});
            Manager = new AjaxSolr.Manager({solrUrl: '/solr/'});
            var fields = ['topics'];
            for (var i = 0, l = fields.length; i < l; i++) {
                Manager.addWidget(new AjaxSolr.TagcloudWidget({
                            id: fields[i],
                            target: '#' + fields[i],
                            field: fields[i]
                        }));
            }
            Manager.init();
            Manager.store.addByValue('q', '*:*');
            var params = {
                facet: true,
                'facet.field': [ 'topics' ],
                'qt':'standard',
                'facet.limit': 20,
                'facet.mincount': 1,
                'f.topics.facet.limit': 50,
                'json.nl': 'map'
            };
            for (var name in params) {
                Manager.store.addByValue(name, params[name]);
            }
            Manager.doRequest();
        });

        function goToSearchPage() {
            window.location = '/search?q=' + $('#query').val();
        }
    </script>
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
                                   onclick="goToSearchPage(); return false;" style=""/></td>
                    </tr>
                    <tr>
                        <td id="topics" class="tagcloud" colspan="2">
                            <%--<div id="topics" class="tagcloud">--%>

                            <%--</div>--%>
                        </td>
                    </tr>
                    </tbody>
                </table>
            </div>
            <div class="browse-portlet">
                <div class="portlet-header">
                    <a href="/collections">Recently added collections</a>
                </div>
                <ul class="portlet-list">
                    <%
                        for (CollectionVersion version : collectionVersions) {
                    %>
                    <li><a href="/collections/<%=version.getParent().getUriKey()%>"><%=version.getTitle()%>
                    </a>
                    </li>
                    <%
                        }
                    %>

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
