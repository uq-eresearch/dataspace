<html>
<head>
    <jsp:include page="include/header.jsp"/>
    <script type="text/javascript">
        dojo.require("dojox.atom.io.Connection");
        dojo.require("dojox.atom.io.model");

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
        //
        //        var collection = {
        //            "id":"urn:uuid:FC8162B844A42471D41285306940156",
        //            "title":"New Collection3243",
        //            "summary":"This is a description of New Collection3243",
        //            "content":"Optional Content",
        //            "updated":"2010-09-24T05:42:20.177Z",
        //            "authors":[
        //                {
        //                    "name":"Abdul Alabri"
        //                },
        //                {
        //                    "name":"Nigel Ward"
        //                }
        //            ]
        //        };

        function loadParties() {
            var conn = new dojox.atom.io.Connection();

            conn.getFeed("party/parties", function(feed) {
                //Emit both the XML (As reconstructed from the Feed object and as a JSON form.
                var xml = dojo.byId("partiesXml");
                //                var entry = feed.getFirstEntry();
                xml.innerHTML = "";
                xml.appendChild(dojo.doc.createTextNode(feed.toString()));

                var json = dojo.byId("partiesJson");
                json.innerHTML = "";
                json.appendChild(dojo.doc.createTextNode(dojo.toJson(feed, true)));

            },
                    function(err) {
                        console.debug(err);
                    });
        }

        function submitParty() {
            var conn = new dojox.atom.io.Connection();

            conn.getFeed("party", function(feed) {
                //Emit both the XML (As reconstructed from the Feed object and as a JSON form.
                var xml = dojo.byId("simplePristineAtomXml");
                xml.innerHTML = "";
                xml.appendChild(dojo.doc.createTextNode(feed.toString()));

                //Now get an entry for mod.
                var entry = feed.getFirstEntry();

                //Make this updateable by pointing it to the app test pho script so it can properly post.
                entry.setEditHref("../../../_static/jsdojox/atom/tests/io/app.php");
                entry.updated = new Date();
                entry.setTitle('<h1>New Editable Title!</h1>', 'xhtml');
                conn.updateEntry(entry, function() {
                    var xml = dojo.byId("simpleModifiedAtomXml");
                    xml.innerHTML = "";
                    xml.appendChild(dojo.doc.createTextNode(feed.toString()));
                },
                        function(err) {
                            console.debug(err);
                        });
            },
                    function(err) {
                        console.debug(err);
                    });
        }

        dojo.addOnLoad(loadParties);
    </script>
</head>
<body class="tundra">
<div class="wrapper">
    <jsp:include page="include/title.jsp"/>
    <div class="content">
        <br/>
        <b>All Parties (XML)</b>
        <br/>
        <textarea rows="15" cols="100" id="partiesXml"></textarea>

        <br/>
        <b>All Parties JSON</b>
        <br/>
        <textarea rows="15" cols="100" id="partiesJson"></textarea>
        <br/>
        <b>Create Party from JSON</b>
        <br/>
        <textarea rows="15" cols="100"></textarea>
        <br/>
        <input type="button" id="button" value="Submit Party" onclick="submitParty()"/>
        <%--<b>As XML (After modification)</b>--%>
        <%--<pre id="simpleModifiedAtomXml"></pre>--%>
    </div>
</div>
<jsp:include page="include/footer.jsp"/>
</body>
</html>