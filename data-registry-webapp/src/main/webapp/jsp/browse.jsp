<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<html>
<head>
    <jsp:include page="../include/head.jsp"/>
    <script type="text/javascript">
        $(document).ready(function() {

        });
    </script>
</head>
<body>
<jsp:include page="../include/header.jsp"/>
<div class="wrapper">

    <ul class="bread-crumbs-nav">
        <li class="bread-crumbs">
            <a href="/">Home</a>
        </li>
        <li class="bread-crumbs-last">
            Browse
        </li>
    </ul>
    <div class="portlet-content">
        <div class="browse-portlet">
            <div class="portlet-header">
                <a href="/collections">Collections</a>
            </div>
            <ul class="portlet-list">
                <c:forEach var="version" items="${collections}">
                    <li><a
                            href="<c:out value="${registryUri}"/>collections/<c:out value="${version.parent.uriKey}"/>">
                        <c:out value="${version.title}"/></a>
                    </li>
                </c:forEach>
            </ul>
        </div>
        <div class="browse-portlet">
            <div class="portlet-header">
                <a href="/agents">Agents</a>
            </div>
            <ul class="portlet-list">
                <c:forEach var="version" items="${agents}">
                    <li><a href="<c:out value="${registryUri}"/>agents/<c:out value="${version.parent.uriKey}"/>">
                        <c:out value="${version.title}"/></a></li>
                </c:forEach>
            </ul>
        </div>
        <div style="clear:both;">

        </div>
        <div class="browse-portlet">
            <div class="portlet-header">
                <a href="/services">Services</a>
            </div>
            <ul class="portlet-list">
                <c:forEach var="version" items="${services}">
                    <li><a href="<c:out value="${registryUri}"/>services/<c:out value="${version.parent.uriKey}"/>">
                        <c:out value="${version.title}"/></a></li>
                </c:forEach>
            </ul>
        </div>
        <div class="browse-portlet">
            <div class="portlet-header">
                <a href="/activities">Activities</a>
            </div>
            <ul class="portlet-list">
                <c:forEach var="version" items="${activities}">
                    <li><a href="<c:out value="${registryUri}"/>activities/<c:out value="${version.parent.uriKey}"/>">
                        <c:out value="${version.title}"/></a></li>
                </c:forEach>
            </ul>
        </div>
        <div style="clear:both;">

        </div>
    </div>
</div>
<jsp:include page="../include/footer.jsp"/>
</body>
</html>