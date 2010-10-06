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


        function submitParty() {
            var responseElement = dojo.byId('serverResponse');
            dojo.xhrPost({
                url:'/parties',
                contentType:"application/json",
                postData: dojo.byId('partyJson').innerHTML,
                encoding: "utf-8",
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

        function getParty() {
            var responseElement = dojo.byId('jsonResponseGet');
            var partyIdField = dojo.byId('partyId');
            var contentTypeCombo = dojo.byId('contentTypeForGet');
            dojo.xhrGet({
                url:'/parties/' + partyIdField.value,
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
        function updateParty() {
            var responseElement = dojo.byId('jsonResponseUpdate');
            var contentTypeCombo = dojo.byId('contentTypeForUpdate');
            var partyIdField = dojo.byId('partyIdForUpdate');
            var jsonContent = dojo.byId('updatePartyJson').innerHTML;
            dojo.xhrPut({
                url:'/parties/' + partyIdField.value,
                headers: {
                    "Content-Type": "application/json",
                    "Accept": contentTypeCombo.value,
                    "Content-Encoding": "utf-8"
                },
                preventCache: true,
                putData: jsonContent,
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


    </script>
</head>
<body class="tundra">
<div class="wrapper">
    <jsp:include page="include/title.jsp"/>
    <div class="content">
        <br/>
        <b>All Parties</b>

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
                     url="parties"
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
                     displayEntrySections="id,title,authors,content,summary"
                     entrySelectionTopic="atomfeed.entry.topic">
                </div>

            </div>
        </div>


        <%--<textarea rows="15" cols="100" id="partiesXml"></textarea>--%>

        <br/>
        <b>Create Party From JSON</b>
        <br/>
        <textarea rows="8" cols="100" id="partyJson">{
            "id":"randomid2308322",
            "title":"Tea Party",
            "summary":"Tax Enough Already",
            "content":"Optional Content",
            "subject":[],
            "authors":["Abdul Alabri","Nigel Ward"]
            }</textarea>
        <br/>
        <input type="button" id="button" value="Add Party" onclick="submitParty()"/>
        <pre id="serverResponse"></pre>

        <br/>
        <br/>
        <b>Get Party</b>
        <br/>
        Enter Party Id: <input id="partyId" name="partyId" type="text"/>
        <br/>
        Return Content-Type <select id="contentTypeForGet" name="contentTypeForGet">
        <option value="application/atom+xml" selected="selected">application/atom+xml</option>
        <option value="application/json">application/json</option>
    </select>
        <br/>
        <input type="button" id="button" value="Get Party" onclick="getParty()"/>
        <pre id="jsonResponseGet"></pre>


        <br/>
        <b>Update Party from JSON</b>
        <br/>
        Enter Party Id: <input id="partyIdForUpdate" name="partyIdForUpdate" type="text"/>
        <br/>
        Return Content-Type <select id="contentTypeForUpdate" name="contentTypeForUpdate">
        <option value="application/atom+xml" selected="selected">application/atom+xml</option>
        <option value="application/json">application/json</option>
    </select>
        <br/>
        <textarea rows="8" cols="100" id="updatePartyJson"></textarea>
        <br/>
        <input type="button" id="button" value="Update Party" onclick="updateParty()"/>
        <pre id="jsonResponseUpdate"></pre>

    </div>
</div>
<jsp:include page="include/footer.jsp"/>
</body>
</html>