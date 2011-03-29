<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<html>
<head>
    <jsp:include page="../include/head.jsp"/>
</head>
<body>
<jsp:include page="../include/header.jsp"/>
<ul class="bread-crumbs-nav">
    <li class="bread-crumbs">
        <a href="/">Home</a> >> Search
    </li>
</ul>
<div class="wrapper">
    <div class="content">
        <form id="search-form" method="post" action="/search">
            <table width="100%">
                <tbody>
                <tr>
                    <td><input type="text" id="search-field" name="search-field" value=""/></td>
                    <td><input type="submit" name="search-submit" id="search-submit" value="Search"/></td>
                </tr>
                </tbody>
            </table>
        </form>
        <div>
            <p><c:out value="${keyword}"/></p>
        </div>
    </div>
</div>
<jsp:include page="../include/footer.jsp"/>
</body>
</html>