<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jstl/core" %>
<html>
<head>
    <jsp:include page="../include/head.jsp"/>
    <script type="text/javascript">
        $(document).ready(function() {
            showCollectionFeeds('collection');
            showCollectionFeeds('agent');
            showCollectionFeeds('service');
            showCollectionFeeds('activity');
        });

        function showCollectionFeeds(type) {
            var Manager = getSearchManager();
            Manager.addWidget(new AjaxSolr.ResultWidget({
                        id: 'result',
                        target: '#' + type + '-feed',
                        afterRequest: function () {
                            $(this.target).empty();
                            for (var i = 0, l = this.manager.response.response.docs.length; i < l; i++) {
                                var doc = this.manager.response.response.docs[i];
                                $(this.target).append(AjaxSolr.theme(type + 'Feed', doc));
                            }
                        }
                    }));
            Manager.store.addByValue('q', type + ':*');
            Manager.doRequest();
        }

        function getSearchManager() {
            var Manager = new AjaxSolr.Manager({solrUrl: '/solr/'});
            Manager.init();
            var params = {
                'qt':'standard',
                'json.nl': 'map'
            };
            for (var name in params) {
                Manager.store.addByValue(name, params[name]);
            }
            return Manager;
        }
    </script>
</head>
<body>
<jsp:include page="../include/header.jsp"/>
<ul class="bread-crumbs-nav">
    <li class="bread-crumbs">
        <a href="/">Home</a> >> Browse
    </li>
</ul>
<div class="wrapper">
    <div class="content">
        <div class="browse-portlet">
            <a href="/collections">Collections</a>
            <br/>

            <div id="collection-feed"></div>
        </div>
        <div class="browse-portlet">
            <a href="/agents">Agents</a>
            <br/>

            <div id="agent-feed"></div>
        </div>
        <div class="browse-portlet">
            <a href="/services">Services</a>
            <br/>

            <div id="service-feed"></div>
        </div>
        <div class="browse-portlet">
            <a href="/activities">Activities</a>
            <br/>

            <div id="activity-feed"></div>
        </div>
    </div>
</div>
<jsp:include page="../include/footer.jsp"/>
</body>
</html>