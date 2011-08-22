<%@ page import="net.metadata.dataspace.app.RegistryApplication" %>
<%@ page import="net.metadata.dataspace.data.model.record.Collection" %>
<%@ page import="net.metadata.dataspace.data.model.version.CollectionVersion" %>
<%@ page import="java.util.ArrayList" %>
<%@ page import="java.util.List" %>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
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

        $(function() {
            $( "#tabs-pane" ).tabs();
        });
    </script>
</head>
<body>
<jsp:include page="include/header.jsp"/>

<div class="wrapper">
    <div class="content-top">
        <div class="pad-sides pad-top">
            <ul class="bread-crumbs-nav">
                <li class="bread-crumbs-last">
                    Home
                </li>
            </ul>
            <h1>Welcome to <%=RegistryApplication.getApplicationContext().getRegistryTitle()%></h1>

            <h2>A catalog of the University of Queensland's research data assets</h2>
        </div>
    </div>
    <div class="col-container">
        <div class="pad-sides">
            <div class="col1">
                <p><%=RegistryApplication.getApplicationContext().getRegistryTitle()%> catalogs the research data
                    outputs of staff and students at the University of Queensland. It aims to promote
                    the University of Queensland's research data to the wider world.</p>

                <p>The catalog contains descriptions of University of Queensland:</p>
                <ul>
                    <li><a href="/collections">data collections</a>;</li>
                    <li><a href="/agents">agents</a> (people and groups) that create and manage the data collections; </li>
                    <li><a href="/activities">activities</a> (projects) that funded the data creation; and</li>
                    <li><a href="/services">services</a> for accessing and manipulating the data.</li>
                </ul>

                <div class="figure">
                    <p>The concept</p>
                    <img src="/images/concept.png" alt="Collections are created by Agents, are accessed via services, and are
            the output of projects"/>
                </div>
            </div>

            <div class="col2">

                <div class="home-search">
                    <form action="/search" method="get">
                        <input class="search-field" onfocus="this.value=''; this.onfocus=null;" type="text"
                               name="q" value="Quick search - enter keywords"/>
                        <input class="search-button" type="submit" value="Search"/>
                    </form>
                </div>

                <div id="tabs-pane">
                    <ul>
                        <li><a href="#recent">Recently added collections</a></li>
                        <li><a href="#tags">Tags</a></li>
                    </ul>

                    <div id="recent">
                        <ul>
                            <%
                                for (CollectionVersion version : collectionVersions) {
                            %>
                            <li>
                                <a href="/collections/<%=version.getParent().getUriKey()%>"><%=version.getTitle()%>
                                </a>
                            </li>
                            <%
                                }
                            %>
                        </ul>
                    </div>

                    <div id="tags">
                        <div id="topics" class="tagcloud">

                        </div>
                    </div>

                </div>

                <p class="clear-bg">This registry syndicates data to</p>
                <p><a href="http://services.ands.org.au/home/orca/rda/">
                    <img src="/images/ands_logo.gif"></a>
                </p>
            </div>
        </div>
    </div>

    <div class="content-bottom">
        <div class="pad-sides pad-top pad-bottom">
            <div class="bread-crumbs-nav">
                <ul>
                    <li class="bread-crumbs-last">
                        Home
                    </li>
                </ul>
            </div>
        </div>
    </div>

</div>

<jsp:include page="include/footer.jsp"/>
</body>
</html>
