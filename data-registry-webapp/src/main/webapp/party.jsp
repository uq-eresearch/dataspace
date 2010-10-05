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


        function submitParty() {
            var responseElement = dojo.byId('serverResponse');
            dojo.xhrPost({
                url:'/parties',
                contentType:"application/json",
                postData: dojo.byId('partyJson').innerHTML,
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

        function getParty() {
            var responseElement = dojo.byId('jsonResponseGet');
            var partyIdField = dojo.byId('partyId');
            dojo.xhrGet({
                url:'/parties/' + partyIdField.value,
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
                     displayEntrySections="title,authors,summary,content"
                     entrySelectionTopic="atomfeed.entry.topic">
                </div>

            </div>
        </div>


        <%--<textarea rows="15" cols="100" id="partiesXml"></textarea>--%>

        <br/>
        <b>Create Party From JSON</b>
        <br/>
        <textarea rows="8" cols="100" id="partyJson">{
            "id":"urn:uuid:335FE3DE7267B37B791285306505116",
            "title":"Party 570",
            "summary":"This is a description of New Party 570",
            "content":"Optional Content",
            "subject":[],
            "authors":["Abdul Alabri","Nigel Ward"]
            }</textarea>
        <br/>
        <input type="button" id="button" value="Add Party" onclick="submitParty()"/>
        <pre id="serverResponse"></pre>

        <br/>
        <br/>
        <b>Get Party as JSON</b>
        <br/>
        Enter Party Id: <input id="partyId" name="partyId" type="text"/>
        <br/>
        <input type="button" id="button" value="Get Party" onclick="getParty()"/>
        <pre id="jsonResponseGet"></pre>


        <br/>
        <b>Update Party from JSON</b>
        <br/>
        <textarea rows="8" cols="100" id="updatePartyJson"></textarea>
        <br/>
        <input type="button" id="button" value="Update Party" onclick="updateParty()"/>
        <pre id="jsonResponseUpdate"></pre>


        <%--<b>As XML (After modification)</b>--%>
        <%--<pre id="simpleModifiedAtomXml"></pre>--%>
    </div>
</div>
<jsp:include page="include/footer.jsp"/>
</body>
</html>