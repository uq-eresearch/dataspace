<html>
<head>
    <jsp:include page="../include/header.jsp"/>
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

        //        var party = {
        //            "id":"urn:uuid:335FE3DE7267B37B791285306505116",
        //            "title":"New Party 570",
        //            "summary":"This is a description of New Party 570",
        //            "content":"Optional Content",
        //            "updated":"2010-09-24T05:35:05.140Z",
        //            "authors":[
        //                {
        //                    "name":"Abdul Alabri"
        //                },
        //                {
        //                    "name":"Nigel Ward"
        //                }
        //            ],
        //            "extensions":[
        //                {
        //                    "name":"collectorOf",
        //                    "attributes":{
        //                        "xmlns":"http://http://www.w3.org/2005/Atom",
        //                        "value":"a51f87e4-d040-4ecb-b8ed-0f6043dabdc1"
        //                    },
        //                    "children":[
        //                    ]
        //                }
        //            ]
        //        };

        //        function loadParties() {
        //            var conn = new dojox.atom.io.Connection();
        //            conn.getFeed("party/parties", function(feed) {
        //                //Emit both the XML (As reconstructed from the Feed object and as a JSON form.
        //                var xml = dojo.byId("partiesXml");
        //                xml.innerHTML = "";
        //                xml.appendChild(dojo.doc.createTextNode(feed.toString()));
        //
        //            }, function(err) {
        //                console.debug(err);
        //            });
        //        }

        function submitParty() {
            var responseElement = dojo.byId('serverResponse');
            dojo.xhrPost({
                url:'/party/parties',
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
        //        dojo.addOnLoad(loadParties);
    </script>
</head>
<body class="tundra">
<div class="wrapper">
    <jsp:include page="../include/title.jsp"/>
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
                     url="party/parties"
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
        <%--<b>As XML (After modification)</b>--%>
        <%--<pre id="simpleModifiedAtomXml"></pre>--%>
    </div>
</div>
<jsp:include page="../include/footer.jsp"/>
</body>
</html>