<html>
<head>
    <jsp:include page="include/header.jsp"/>

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
                headers: {
                    "Cache-Control": "no-cache"
                },
                postData: dojo.byId('collectionJson').value,
                encoding: "utf-8",
                preventCache: true,
                timeout: 5000,
                load: function(response, ioArgs) {
                    responseElement.innerHTML = response;
                    dijit.byId('fv1').setFeedFromUrl("collections");
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
            var contentTypeCombo = dojo.byId('contentTypeForGet');
            dojo.xhrGet({
                url:'/collections/' + collectionIdField.value,
                handleAs:"text",
                headers: {
                    "Content-Type": "text/plain",
                    "Accept": contentTypeCombo.value,
                    "Content-Encoding": "utf-8"
                },
                preventCache: true,
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

        function updateCollection() {
            var responseElement = dojo.byId('jsonResponseUpdate');
            var contentTypeCombo = dojo.byId('contentTypeForUpdate');
            var collectionIdField = dojo.byId('collectionIdForUpdate');
            var jsonContent = dojo.byId('updateCollectionJson').value;
            alert(jsonContent);
            dojo.xhrPut({
                url:'/collections/' + collectionIdField.value,
                headers: {
                    "Cache-Control": "no-cache",
                    "Content-Type": "application/json",
                    "Accept": contentTypeCombo.value,
                    "Content-Encoding": "utf-8"
                },
                preventCache: true,
                putData: jsonContent,
                timeout: 5000,
                load: function(response, ioArgs) {
                    responseElement.innerHTML = response;
                    dijit.byId('fv1').setFeedFromUrl("collections");
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
                 executeScripts="true">
                <div dojoType="dojox.atom.widget.FeedViewer"
                     id="fv1"
                     url="collections"
                     entrySelectionTopic="atomfeed.entry.topic">
                </div>
            </div>


            <div dojoType="dijit.layout.ContentPane"
                 id="EditorPaneID"
                 executeScripts="true">
                <div dojoType="dojox.atom.widget.FeedEntryViewer"
                     id="feedEditor"
                     enableMenu="true"
                     enableMenuFade="true"
                     enableEdit="true"
                     displayEntrySections="id,title,authors,summary,content"
                     entrySelectionTopic="atomfeed.entry.topic">
                </div>

            </div>
        </div>


        <%--<textarea rows="15" cols="100" id="partiesXml"></textarea>--%>

        <br/>
        <b>Create Collection From JSON</b>
        <br/>
        <textarea rows="8" cols="100" id="collectionJson">{
            "id":"randomid",
            "title":"Collection Name",
            "summary":"Collection Description",
            "content":"Optional Content",
            "location":"http://dataspace.uq.edu.au/location",
            "subject":[],
            "collector":[],
            "authors":["John Smith","Joe Blog"]
            }</textarea>
        <br/>
        <input type="button" id="button" value="Add Collection" onclick="addCollection()"/>
        <pre id="serverResponse"></pre>

        <br/>
        <br/>


        <b>Get Collection</b>
        <br/>
        Enter Collection Id: <input id="collectionId" name="collectionId" type="text"/>
        <br/>
        Return Content-Type <select id="contentTypeForGet" name="contentTypeForGet">
        <option value="application/atom+xml" selected="selected">application/atom+xml</option>
        <option value="application/json">application/json</option>
    </select>
        <br/>
        <input type="button" id="button" value="Get Collection" onclick="getCollection()"/>
        <pre id="jsonResponseGet"></pre>


        <br/>
        <b>Update Collection from JSON</b>
        <br/>
        Enter Collection Id: <input id="collectionIdForUpdate" name="collectionIdForUpdate" type="text"/>
        <br/>
        Return Content-Type <select id="contentTypeForUpdate" name="contentTypeForUpdate">
        <option value="application/atom+xml" selected="selected">application/atom+xml</option>
        <option value="application/json">application/json</option>
    </select>
        <br/>
        <textarea rows="8" cols="100" id="updateCollectionJson" name="updateCollectionJson"></textarea>
        <br/>
        <input type="button" id="button" value="Update Collection" onclick="updateCollection()"/>
        <pre id="jsonResponseUpdate"></pre>

    </div>
</div>
<jsp:include page="include/footer.jsp"/>
</body>
</html>