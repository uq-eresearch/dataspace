<html>
<head>
    <jsp:include page="include/header.jsp"/>
    <link type="text/css" href="http://o.aolcdn.com/dojo/1.4.0/dojo/resources/dojo.css"/>
    <link type="text/css"
          href="http://archive.dojotoolkit.org/nightly/dojotoolkit/dojox/atom/widget/templates/css/EntryHeader.css"/>

    <link type="text/css"
          href="http://archive.dojotoolkit.org/nightly/dojotoolkit/dojox/atom/widget/templates/css/HtmlFeedViewer.css"/>
    <link type="text/css"
          href="http://archive.dojotoolkit.org/nightly/dojotoolkit/dojox/atom/widget/templates/css/HtmlFeedViewerGrouping.css"/>
    <link type="text/css"
          href="http://archive.dojotoolkit.org/nightly/dojotoolkit/dojox/atom/widget/templates/css/HtmlFeedViewerEntry.css"/>
    <link type="text/css"
          href="http://archive.dojotoolkit.org/nightly/dojotoolkit/dojox/atom/widget/templates/css/HtmlFeedEntryViewer.css"/>


    <style type="text/css">
        #ViewPane {
            overflow: auto;
            overflow-x: auto;
            overflow-y: auto;
            padding: 5px;
            font-family: Myriad, Tahoma, Verdana, sans-serif;
            font-size: small; /*background: lightgrey;*/
            background: #FFF;
            word-wrap: normal;
        }

        #EditorPane {
            overflow: auto; /* We want scrolling*/
            overflow-x: auto;
            overflow-y: auto;
            padding: 0px;
            background: #FFF;
        }

        #ActionContainer {
            height: 400px;
        }

    </style>

    <script type="text/javascript">
        dojo.require("dojox.atom.io.Connection");
        dojo.require("dojox.atom.io.model");

        dojo.require("dojox.atom.widget.FeedViewer");
        dojo.require("dojox.atom.widget.FeedEntryViewer");
        dojo.require("dijit.layout.ContentPane");
        dojo.require("dijit.layout.SplitContainer");
        dojo.require("dojo.parser");

        function addCollection() {
            var responseElement = dojo.byId('serverResponse');
            dojo.xhrPost({
                url:'/collections',
                contentType:"application/json",
                postData: dojo.byId('collectionJson').innerHTML,
                encoding: "utf-8",
                timeout: 5000,
                load: function(response, ioArgs) {
                    responseElement.innerHTML = response;
                    return response;
                },
                error: function(response, ioArgs) {
                    responseElement.innerHTML = response;
                    return response;
                }
            });
        }

        function getCollection() {
            var responseElement = dojo.byId('jsonResponseGet');
            var collectionIdField = dojo.byId('collectionId');
            dojo.xhrGet({
                url:'/collections/' + collectionIdField.value,
                handleAs:"text",
                headers: {
                    "Content-Type": "text/plain",
                    "Accept": "application/json",
                    "Content-Encoding": "utf-8"
                },
                timeout: 5000,
                load: function(response, ioArgs) {
                    responseElement.innerHTML = response;
                    return response;
                },
                error: function(response, ioArgs) {
                    responseElement.innerHTML = response;
                    return response;
                }
            });
        }
    </script>
</head>
<body class="tundra">
<div class="wrapper">
    <jsp:include page="include/title.jsp"/>
    <div class="content">
        <br/>
        <b>All Collections</b>


        <div id="ActionContainer"
             dojoType="dijit.layout.SplitContainer"
             orientation="horizontal"
             sizerWidth="1"
             activeSizing="false">


            <div dojoType="dijit.layout.ContentPane"
                 id="ViewPaneID"
                 executeScripts="true"
                    >
                <div dojoType="dojox.atom.widget.FeedViewer"
                     id="fv1"
                     url="collections"
                     entrySelectionTopic="atomfeed.entry.topic">
                </div>
            </div>


            <div dojoType="dijit.layout.ContentPane"
                 id="EditorPaneID"
                 executeScripts="true"
                    >
                <div dojoType="dojox.atom.widget.FeedEntryViewer"
                     id="feedEditor"
                     enableMenu="true"
                     enableMenuFade="true"
                     enableEdit="true"
                     displayEntrySections="title,authors,summary,content"
                     entrySelectionTopic="atomfeed.entry.topic">
                </div>

            </div>
        </div>


        <%--<textarea rows="15" cols="100" id="partiesXml"></textarea>--%>

        <br/>
        <b>Create Collection From JSON</b>
        <br/>
        <textarea rows="8" cols="100" id="collectionJson">{
            "id":"urn:uuid:335FE3DE7267B37B791285306505116",
            "title":"Collection Name",
            "summary":"Collection Description",
            "content":"Optional Content",
            "location":"http://dataspace.uq.edu.au/335FE3DE7267B37B791285306505116",
            "subject":[],
            "collector":[],
            "authors":["John Smith","Joe Blog"]
            }</textarea>
        <br/>
        <input type="button" id="button" value="Add Collection" onclick="addCollection()"/>
        <pre id="serverResponse"></pre>

        <br/>
        <br/>
        <b>Get Collection as JSON</b>
        <br/>
        Enter Collection Id: <input id="collectionId" name="collectionId" type="text"/>
        <br/>
        <input type="button" id="button" value="Get Collection" onclick="getCollection()"/>
        <pre id="jsonResponseGet"></pre>


        <br/>
        <b>Update Collection from JSON</b>
        <br/>
        <textarea rows="8" cols="100" id="updateCollectionJson"></textarea>
        <br/>
        <input type="button" id="button" value="Update Collection" onclick="updateCollection()"/>
        <pre id="jsonResponseUpdate"></pre>


        <%--<b>As XML (After modification)</b>--%>
        <%--<pre id="simpleModifiedAtomXml"></pre>--%>
        <%--<b>As XML (After modification)</b>--%>
        <%--<pre id="simpleModifiedAtomXml"></pre>--%>
    </div>
</div>
<jsp:include page="include/footer.jsp"/>
</body>
</html>