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
<ul>
    <c:forEach items="${result}" var="res">
        <li><input type="checkbox"/> <c:out value="${res}"/></li>
    </c:forEach>
    <li><input type="button" value="Select"/></li>
</ul>