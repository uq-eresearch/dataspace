<%@ page import="net.metadata.dataspace.app.DataRegistryApplication" %>
<html>
<body>
<h2>Hello World! Data Registry</h2>
Version Number: <%= DataRegistryApplication.getConfiguration().getVersion()%>
</body>
</html>
