<%@ page import="net.metadata.dataspace.app.DataRegistryApplicationContext" %>
<%@ page import="org.springframework.context.ApplicationContext" %>
<%@ page import="org.springframework.context.support.ClassPathXmlApplicationContext" %>
<html>
<body>
<h2>Hello World! Data Registry</h2>

<%
    ApplicationContext context = new ClassPathXmlApplicationContext("conf/spring/applicationContext.xml");
    DataRegistryApplicationContext dataRegistryApplicationContext = (DataRegistryApplicationContext) context.getBean("applicationContext");
%>
Version number <%=dataRegistryApplicationContext.getVersion()%>
</body>
</html>
