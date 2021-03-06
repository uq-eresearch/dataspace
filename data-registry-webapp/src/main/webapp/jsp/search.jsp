<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<html>
<head>
    <title>${registryTitle} - Search</title>
    <jsp:include page="../include/head.jsp"/>

    <script type="text/javascript">
        var Manager;
        $(document).ready(function() {

            $('#query').keyup(function(event) {
                if (event.keyCode == '13') {
                    event.preventDefault();
                    doSearch($('#query').val());
                }
            });

            Manager = new AjaxSolr.Manager({solrUrl: '/solr/'});
            Manager.addWidget(new AjaxSolr.ResultWidget({id: 'result',target: '#docs'}));
            Manager.addWidget(new AjaxSolr.PagerWidget({
                        id: 'pager',
                        target: $('.pager'),
                        prevLabel: '&lt;',
                        nextLabel: '&gt;',
                        innerWindow: 1,
                        renderHeader: function (perPage, offset, total) {
                            $('.pager-header').html($('<span/>').text('displaying ' + Math.min(total, offset + 1) + ' to ' + Math.min(total, offset + perPage) + ' of ' + total));
                        }
                    }));
            Manager.init();

            var params = {
       			'qt' : 'standard',
       			'rows' : 5
            };
            for (var name in params) {
                Manager.store.addByValue(name, params[name]);
            }
            <%
            String query = request.getParameter("q");
            if(query != null) {
            %>
            $('#query').val('<%=query%>')
            doSearch('<%=query%>');
            <%}%>
            $("#query").focus();
        });
        $.fn.showIf = function (condition) {
            if (condition) {
                return this.show();
            }
            else {
                return this.hide();
            }
        };
        function doSearch(term) {
            Manager.store.addByValue('q', term);
            Manager.doRequest();
        }
        function reload(query) {
            window.location = '/search?q=' + query;
        }
    </script>
</head>
<body>
<jsp:include page="../include/header.jsp"/>

<div class="wrapper">
	<div class="pad-top pad-sides">
	    <ul class="bread-crumbs-nav">
	        <li class="bread-crumbs">
	            <a href="/">Home</a>
	        </li>
	        <li class="bread-crumbs-last">
	            Search
	        </li>
	    </ul>
	    <div class="content">
	        <div id="search-form">
	            <table width="100%">
	                <tbody>
	                <tr>
	                    <td id="searching">
	                        <input type="text" id="query" name="query"/>
	                    </td>
	                    <td><input type="button" name="search-submit" id="search-submit" value="Search"
	                               onclick="reload($('#query').val()); return false;" style=""/></td>
	                </tr>
	                </tbody>
	            </table>
	        </div>
	        <div id="search-result">
	            <div id="navigation">
	                <ul class="pager"></ul>

	                <div class="pager-header"></div>
	            </div>
	            <div id="docs"></div>
	        </div>
	    </div>
	</div>
</div>
<jsp:include page="../include/footer.jsp"/>
</body>
</html>