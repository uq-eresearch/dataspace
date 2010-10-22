<html>
<head>
    <jsp:include page="include/header.jsp"/>
</head>
<body>
<div class="wrapper">
    <jsp:include page="include/title.jsp"/>

    <div class="content">
        <ul>
            <li><a href="parties?repr=application/atom+xml;type=feed" title="Parties Feed">Parties Feed</a></li>
            <li><a href="parties" title="Parties HTML">Parties HTML</a></li>
            <li><a href="collections?repr=application/atom+xml;type=feed" title="Collection Feed">Collections Feed</a>
            </li>
            <li><a href="collections" title="Collections HTML">Collections HTML</a></li>
        </ul>
    </div>
</div>
<jsp:include page="include/footer.jsp"/>
</body>
</html>
