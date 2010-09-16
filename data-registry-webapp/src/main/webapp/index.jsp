<%@ page import="net.metadata.dataspace.app.DataRegistryApplicationConfigurationImpl" %>
<%@ page import="org.springframework.context.ApplicationContext" %>
<%@ page import="org.springframework.context.support.ClassPathXmlApplicationContext" %>
<html>
<body>
<h2>Hello World! Data Registry</h2>

<%
    ApplicationContext context = new ClassPathXmlApplicationContext("conf/spring/applicationContext.xml");
    DataRegistryApplicationConfigurationImpl dataRegistryApplicationConfigurationImplrationImpl = (DataRegistryApplicationConfigurationImpl) context.getBean("applicationContext");
%>
Version number <%=dataRegistryApplicationConfigurationImplrationImpl.getVersion()%>
</body>
</html>
