<%@ page import="net.metadata.dataspace.app.RegistryApplication" %>
<div class="title">
    <div align="center"><%=RegistryApplication.getApplicationContext().getRegistryTitle()%>
    </div>
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
</div>