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
<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<html>
<head>
    <title><%=RegistryApplication.getApplicationContext().getRegistryTitle()%> - Home</title>
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
<div id="content-holder">
    <div class="wrapper">
        <div class="home-content">
            <h1>Welcome to <%=RegistryApplication.getApplicationContext().getRegistryTitle()%></h1>

            <p>A catalog of the <strong>University of Queensland's</strong> Research Data Assets</p>

            <p><%=RegistryApplication.getApplicationContext().getRegistryTitle()%> catalogs the research data outputs of staff and students at the
             University of Queensland. It aims to improve the visibility and accessibility of
             UQ data to the wider world.</p>

            <p>The catalog contains descriptions of:</p>
            <ul>
                <li><a href="/collections">data collections</a>,</li>
                <li>the <a href="/agents">agents</a> (people and groups) that create and manage the data collections, </li>
                <li>the <a href="/activities">activities</a> (projects) that funded the data creation, and</li>
                <li>the <a href="/services">services</a> for accessing and manipulating the data</li>
            </ul>

            <div class="portlet-content">
                <div class="browse-portlet">
                    <div class="portlet-header">
                        <a href="/search">Search</a>
                    </div>
                    <table width="100%">
                        <tbody>
                        <tr>
                            <td id="searching">
                                <input type="text" id="query" name="query">
                            </td>
                            <td><input type="button" name="search-submit" id="search-submit" value="Search"
                                       onclick="goToSearchPage(); return false;" style=""/></td>
                        </tr>
                        <tr>
                            <td colspan="2">
                                <div id="topics" class="tagcloud">

                                </div>
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
            <div class="clear">

            </div>
            <p>This registry syndicates data to <br/>
                <a href="http://services.ands.org.au/home/orca/rda/">
                    <img src="/images/ands_logo.gif"></a>
            </p>
        </div>
    </div>
</div>
<jsp:include page="include/footer.jsp"/>
</body>
</html>
