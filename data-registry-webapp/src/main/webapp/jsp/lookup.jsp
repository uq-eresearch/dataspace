<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<form id="lookup-form" method="post" action="/lookup">
    <table width="100%">
        <tbody>
        <tr>
            <td><input type="text" id="keyword" name="keyword" value=""/></td>
            <td><input type="submit" name="lookup-submit" id="lookup-submit" value="Search"/></td>
        </tr>
        </tbody>
    </table>
</form>
<p><c:out value="${result}"/></p>