<%@ page import="net.metadata.dataspace.app.RegistryApplication" %>
<html>
<head>
    <jsp:include page="include/header.jsp"/>
</head>
<body>
<div class="wrapper">
    <jsp:include page="include/title.jsp"/>

    <div class="content">
        <br/>

        <form action="/login" method="post">
            <table>
                <tr>
                    <th>Username</th>
                    <td><input type="text" id="username" name="username"/></td>
                    <th>Password</th>
                    <td><input type="password" id="password" name="password"/></td>
                    <td><input type="submit" id="login" name="login" value="Sign In"/></td>
                </tr>
            </table>
        </form>
    </div>
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
                    <li>Get a <strong>feed of a Party</strong> (atom): curl -v -X GET <a
                            href="<%=RegistryApplication.getApplicationContext().getUriPrefix()%>parties?repr=application/atom+xml"><%=RegistryApplication.getApplicationContext().getUriPrefix()%>
                        parties?repr=application/atom+xml</a>
                    </li>
                    <li>Get a <strong>feed of a Service</strong> (atom): curl -v -X GET <a
                            href="<%=RegistryApplication.getApplicationContext().getUriPrefix()%>services?repr=application/atom+xml"><%=RegistryApplication.getApplicationContext().getUriPrefix()%>
                        services?repr=application/atom+xml</a>
                    </li>
                    <li>Get a <strong>published</strong> collection (atom): curl -v -X GET <a
                            href="<%=RegistryApplication.getApplicationContext().getUriPrefix()%>collections/1?repr=application/atom+xml"><%=RegistryApplication.getApplicationContext().getUriPrefix()%>
                        collections/1?repr=application/atom+xml</a>
                    </li>
                    <li>Get a <strong>published</strong> collection (rdf): curl -v -X GET <a
                            href="<%=RegistryApplication.getApplicationContext().getUriPrefix()%>collections/1?repr=application/rdf+xml"><%=RegistryApplication.getApplicationContext().getUriPrefix()%>
                        collections/1?repr=application/rdf+xml</a>
                    </li>
                    <li>Get a <strong>published</strong> collection (rif-cs): curl -v -X GET <a
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
                        login?username=test&password=test" -X POST
                        "<%=RegistryApplication.getApplicationContext().getUriPrefix()%>activities" --data
                        @/location/activity.atom
                    </li>
                    <li>Add a Collection (atom): curl -v -b -X POST
                        "<%=RegistryApplication.getApplicationContext().getUriPrefix()%>
                        login?username=test&password=test" -X POST
                        "<%=RegistryApplication.getApplicationContext().getUriPrefix()%>collections" --data
                        @/location/collection.atom
                    </li>
                    <li>Add a Party (atom): curl -v -b -X POST
                        "<%=RegistryApplication.getApplicationContext().getUriPrefix()%>
                        login?username=test&password=test" -X POST
                        "<%=RegistryApplication.getApplicationContext().getUriPrefix()%>parties" --data
                        @/location/party.atom
                    </li>
                    <li>Add a Service (atom): curl -v -b -X POST
                        "<%=RegistryApplication.getApplicationContext().getUriPrefix()%>
                        login?username=test&password=test" -X POST
                        "<%=RegistryApplication.getApplicationContext().getUriPrefix()%>services" --data
                        @/location/services.atom
                    </li>
                </ul>
            </li>
            <li>PUT (Login required):
                <ul>
                    <li>Edit a Activity (atom): curl -v -b -X POST
                        "<%=RegistryApplication.getApplicationContext().getUriPrefix()%>
                        login?username=test&password=test" -X PUT
                        "<%=RegistryApplication.getApplicationContext().getUriPrefix()%>activities" --data
                        @/location/activity.atom
                    </li>
                    <li>Edit a Collection (atom): curl -v -b -X POST
                        "<%=RegistryApplication.getApplicationContext().getUriPrefix()%>
                        login?username=test&password=test" -X PUT
                        "<%=RegistryApplication.getApplicationContext().getUriPrefix()%>collections" --data
                        @/location/collection.atom
                    </li>
                    <li>Edit a Party (atom): curl -v -b -X POST
                        "<%=RegistryApplication.getApplicationContext().getUriPrefix()%>
                        login?username=test&password=test" -X PUT
                        "<%=RegistryApplication.getApplicationContext().getUriPrefix()%>parties" --data
                        @/location/party.atom
                    </li>
                    <li>Edit a Service (atom): curl -v -b -X POST
                        "<%=RegistryApplication.getApplicationContext().getUriPrefix()%>
                        login?username=test&password=test" -X PUT
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
                    <li>Delete a Party: curl -v -b -X POST
                        "<%=RegistryApplication.getApplicationContext().getUriPrefix()%>
                        login?username=test&password=test" -X
                        DELETE <%=RegistryApplication.getApplicationContext().getUriPrefix()%>parties/1
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
