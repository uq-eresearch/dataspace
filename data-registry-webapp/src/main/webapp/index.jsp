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
        <h3>cURL Examples</h3>
        <ul>
            <li>GET:
                <ul>
                    <li>Get a <strong>feed of a Activity</strong> (atom): curl -v -X GET <a
                            href="<%=RegistryApplication.getApplicationContext().getUriPrefix()%>activities?repr=application/atom+xml"><%=RegistryApplication.getApplicationContext().getUriPrefix()%>
                        activities?repr=application/atom+xml</a>
                    </li>
                    <li>Get a <strong>feed of a Collection</strong> (atom): curl -v -X GET <a
                            href="<%=RegistryApplication.getApplicationContext().getUriPrefix()%>collections?repr=application/atom+xml"><%=RegistryApplication.getApplicationContext().getUriPrefix()%>
                        collections?repr=application/atom+xml</a>
                    </li>
                    <li>Get a <strong>feed of an Agent</strong> (atom): curl -v -X GET <a
                            href="<%=RegistryApplication.getApplicationContext().getUriPrefix()%>agents?repr=application/atom+xml"><%=RegistryApplication.getApplicationContext().getUriPrefix()%>
                        agents?repr=application/atom+xml</a>
                    </li>
                    <li>Get a <strong>feed of a Service</strong> (atom): curl -v -X GET <a
                            href="<%=RegistryApplication.getApplicationContext().getUriPrefix()%>services?repr=application/atom+xml"><%=RegistryApplication.getApplicationContext().getUriPrefix()%>
                        services?repr=application/atom+xml</a>
                    </li>
                    <li>Get a published collection <strong>(atom)</strong>: curl -v -X GET <a
                            href="<%=RegistryApplication.getApplicationContext().getUriPrefix()%>collections/1?repr=application/atom+xml"><%=RegistryApplication.getApplicationContext().getUriPrefix()%>
                        collections/1?repr=application/atom+xml</a>
                    </li>
                    <li>Get a published collection <strong>(rdf)</strong>: curl -v -X GET <a
                            href="<%=RegistryApplication.getApplicationContext().getUriPrefix()%>collections/1?repr=application/rdf+xml"><%=RegistryApplication.getApplicationContext().getUriPrefix()%>
                        collections/1?repr=application/rdf+xml</a>
                    </li>
                    <li>Get a published collection <strong>(rif-cs)</strong>: curl -v -X GET <a
                            href="<%=RegistryApplication.getApplicationContext().getUriPrefix()%>collections/1?repr=application/rifcs+xml"><%=RegistryApplication.getApplicationContext().getUriPrefix()%>
                        collections/1?repr=application/rifcs+xml</a>
                    </li>
                    <li>Get a <strong>version</strong> of a Collection (Login required): curl -v -X GET <a
                            href="<%=RegistryApplication.getApplicationContext().getUriPrefix()%>collections/1/1?repr=application/atom+xml"><%=RegistryApplication.getApplicationContext().getUriPrefix()%>
                        collections/1/1?repr=application/atom+xml</a>
                    </li>
                    <li>Get <strong>working copy</strong> of a Collection (Login required): curl -v -X GET <a
                            href="<%=RegistryApplication.getApplicationContext().getUriPrefix()%>collections/1/working-copy?repr=application/atom+xml"><%=RegistryApplication.getApplicationContext().getUriPrefix()%>
                        collections/1/working-copy?repr=application/atom+xml</a>
                    </li>
                    <li>Get <strong>version history</strong> of a collection (Login required): curl -v -X GET <a
                            href="<%=RegistryApplication.getApplicationContext().getUriPrefix()%>collections/version-history"><%=RegistryApplication.getApplicationContext().getUriPrefix()%>
                        collections/version-history</a>
                    </li>
                </ul>
            </li>
            <li>POST (Login required):
                <ul>
                    <li>Add a Activity (atom): curl -v -b -X POST
                        "<%=RegistryApplication.getApplicationContext().getUriPrefix()%>
                        login?username=test&password=test" -X POST -H "Content-Type: application/atom+xml"
                        "<%=RegistryApplication.getApplicationContext().getUriPrefix()%>activities" --data
                        @/location/activity.atom
                    </li>
                    <li>Add a Collection (atom): curl -v -b -X POST
                        "<%=RegistryApplication.getApplicationContext().getUriPrefix()%>
                        login?username=test&password=test" -X POST -H "Content-Type: application/atom+xml"
                        "<%=RegistryApplication.getApplicationContext().getUriPrefix()%>collections" --data
                        @/location/collection.atom
                    </li>
                    <li>Add an Agent (atom): curl -v -b -X POST
                        "<%=RegistryApplication.getApplicationContext().getUriPrefix()%>
                        login?username=test&password=test" -X POST -H "Content-Type: application/atom+xml"
                        "<%=RegistryApplication.getApplicationContext().getUriPrefix()%>agents" --data
                        @/location/agent.atom
                    </li>
                    <li>Add a Service (atom): curl -v -b -X POST
                        "<%=RegistryApplication.getApplicationContext().getUriPrefix()%>
                        login?username=test&password=test" -X POST -H "Content-Type: application/atom+xml"
                        "<%=RegistryApplication.getApplicationContext().getUriPrefix()%>services" --data
                        @/location/services.atom
                    </li>
                </ul>
            </li>
            <li>PUT (Login required):
                <ul>
                    <li>Edit a Activity (atom): curl -v -b -X POST
                        "<%=RegistryApplication.getApplicationContext().getUriPrefix()%>
                        login?username=test&password=test" -X PUT -H "Content-Type: application/atom+xml"
                        "<%=RegistryApplication.getApplicationContext().getUriPrefix()%>activities" --data
                        @/location/activity.atom
                    </li>
                    <li>Edit a Collection (atom): curl -v -b -X POST
                        "<%=RegistryApplication.getApplicationContext().getUriPrefix()%>
                        login?username=test&password=test" -X PUT -H "Content-Type: application/atom+xml"
                        "<%=RegistryApplication.getApplicationContext().getUriPrefix()%>collections" --data
                        @/location/collection.atom
                    </li>
                    <li>Edit an Agent (atom): curl -v -b -X POST
                        "<%=RegistryApplication.getApplicationContext().getUriPrefix()%>
                        login?username=test&password=test" -X PUT -H "Content-Type: application/atom+xml"
                        "<%=RegistryApplication.getApplicationContext().getUriPrefix()%>agents" --data
                        @/location/agent.atom
                    </li>
                    <li>Edit a Service (atom): curl -v -b -X POST
                        "<%=RegistryApplication.getApplicationContext().getUriPrefix()%>
                        login?username=test&password=test" -X PUT -H "Content-Type: application/atom+xml"
                        "<%=RegistryApplication.getApplicationContext().getUriPrefix()%>services" --data
                        @/location/services.atom
                    </li>
                </ul>
            </li>
            <li>DELETE (Login required):
                <ul>
                    <li>Delete an Activity: curl -v -b -X POST
                        "<%=RegistryApplication.getApplicationContext().getUriPrefix()%>
                        login?username=test&password=test" -X
                        DELETE <%=RegistryApplication.getApplicationContext().getUriPrefix()%>activities/1
                    </li>
                    <li>Delete a Collection: curl -v -b -X POST
                        "<%=RegistryApplication.getApplicationContext().getUriPrefix()%>
                        login?username=test&password=test" -X
                        DELETE <%=RegistryApplication.getApplicationContext().getUriPrefix()%>collections/1
                    </li>
                    <li>Delete an Agent: curl -v -b -X POST
                        "<%=RegistryApplication.getApplicationContext().getUriPrefix()%>
                        login?username=test&password=test" -X
                        DELETE <%=RegistryApplication.getApplicationContext().getUriPrefix()%>agents/1
                    </li>
                    <li>Delete a Service: curl -v -b -X POST
                        "<%=RegistryApplication.getApplicationContext().getUriPrefix()%>
                        login?username=test&password=test" -X
                        DELETE <%=RegistryApplication.getApplicationContext().getUriPrefix()%>services/1
                    </li>
                </ul>
            </li>
        </ul>
    </div>
</div>
<jsp:include page="include/footer.jsp"/>
</body>
</html>
