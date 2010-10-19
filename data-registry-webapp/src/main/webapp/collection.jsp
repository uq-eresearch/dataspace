<html>
<head>
    <jsp:include page="include/header.jsp"/>

    <script type="text/javascript">
        dojo.require("dojox.atom.io.model");
        dojo.require("dojox.atom.io.Connection");

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

        function addSubject(tb) {
            var table = dojo.byId(tb);
            var tbody = table.getElementsByTagName('tbody')[0];
            var subjectCounter = dojo.byId('numberOfSubjects');
            var numberOfSubjects = Number(subjectCounter.value);
            var row = dojo.create('tr');
            row.setAttribute('id', 'subject' + (numberOfSubjects + 1));
            row.appendChild(dojo.create('th', { innerHTML: 'Subject:' }));
            row.appendChild(dojo.create('td', { innerHTML: 'Vocabulary <input type="text" id="subjectVocabulary' + (numberOfSubjects + 1) + '"/> Value <input type="text" id="subjectValue' + (numberOfSubjects + 1) + '"/>'}));
            tbody.insertBefore(row, dojo.byId('author0'));
            subjectCounter.value = (numberOfSubjects + 1);
        }

        function addAuthor(tb) {
            var table = dojo.byId(tb);
            var tbody = table.getElementsByTagName('tbody')[0];
            var authorCounter = dojo.byId('numberOfAuthors');
            var numberOfAuthors = Number(authorCounter.value);
            var row = dojo.create('tr');
            row.setAttribute('id', 'author' + (numberOfAuthors + 1));
            row.appendChild(dojo.create('th', { innerHTML: 'Author Name' }));
            row.appendChild(dojo.create('td', { innerHTML: '<input type="text" id="authorName' + (numberOfAuthors + 1) + '"/>'}));
            tbody.insertBefore(row, dojo.byId('collector0'));
            authorCounter.value = (numberOfAuthors + 1);
        }
        function addCollector(tb) {
            var table = dojo.byId(tb);
            var tbody = table.getElementsByTagName('tbody')[0];
            var collectorsCounter = dojo.byId('numberOfCollectors');
            var numberOfcollectors = Number(collectorsCounter.value);
            var row = dojo.create('tr');
            row.setAttribute('id', 'collector' + (numberOfcollectors + 1));
            row.appendChild(dojo.create('th', { innerHTML: 'Collector Id' }));
            row.appendChild(dojo.create('td', { innerHTML: '<input type="text" id="collectorId' + (numberOfcollectors + 1) + '"/>'}));
            tbody.insertBefore(row, dojo.byId('submitButton'));
            collectorsCounter.value = (numberOfcollectors + 1);
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
                     displayEntrySections="title,authors,id,updated,summary,content,location"
                     entrySelectionTopic="atomfeed.entry.topic">
                </div>

            </div>
        </div>

        <script type="text/javascript">
            function submitCollection() {
                var authorCounter = dojo.byId('numberOfAuthors');
                var numberOfAuthors = Number(authorCounter.value);
                var subjectCounter = dojo.byId('numberOfSubjects');
                var numberOfSubjects = Number(subjectCounter.value);
                var collectorCounter = dojo.byId('numberOfCollectors');
                var numberOfCollectors = Number(collectorCounter.value);

                var xmlString = '<?xml version="1.0"?><entry xmlns="http://www.w3.org/2005/Atom" xmlns:uqdata="http://dataspace.metadata.net/">';
                xmlString = xmlString + '<title type="text">' + dojo.byId('collectionTitleInput').value + '</title>';
                xmlString = xmlString + '<updated>' + new Date() + '</updated>';
                xmlString = xmlString + '<summary type="text">' + dojo.byId('collectionSummary').value + '</summary>';
                for (var i = 0; i <= numberOfAuthors; i++) {
                    xmlString = xmlString + '<author><name>' + dojo.byId('authorName' + i).value + '</name></author>';
                }
                xmlString = xmlString + '<uqdata:location>' + dojo.byId('collectionLocationInput').value + '</uqdata:location>';
                for (var i = 0; i <= numberOfSubjects; i++) {
                    xmlString = xmlString + '<uqdata:subject vocabulary="' + dojo.byId('subjectVocabulary' + i).value + '" value="' + dojo.byId('subjectValue' + i).value + '" />';
                }
                for (var i = 0; i <= numberOfCollectors; i++) {
                    xmlString = xmlString + '<uqdata:collector id="' + dojo.byId('collectorId' + i).value + '" />';
                }
                xmlString = xmlString + '</entry>';

                var responseElement = dojo.byId('serverResponseSubmitCollection');
                dojo.xhrPost({
                    url:'/collections',
                    contentType:"application/atom+xml;type=entry",
                    headers: {
                        "Cache-Control": "no-cache"
                    },
                    postData: xmlString,
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
        </script>

        <br/>
        <table id="newCollectionForm">
            <tr>
                <th>Title</th>
                <td><input type="text" id="collectionTitleInput" name="collectionTitleInput"/></td>
            </tr>
            <tr>
                <th>Location</th>
                <td><input type="text" id="collectionLocationInput" name="collectionLocationInput"/></td>
            </tr>
            <tr>
                <th>Summary</th>
                <td><textarea id="collectionSummary" rows="10" cols="50"></textarea></td>
            </tr>
            <tr id="subject0">
                <th>Subject:</th>
                <td>Vocabulary <input type="text" id="subjectVocabulary0"/> Value <input type="text"
                                                                                         id="subjectValue0"/>
                    <input type="button" id="newSubjectButton" value="New Subject"
                           onclick="addSubject('newCollectionForm')"/>
                    <input id="numberOfSubjects" type="hidden" value="0"/>
                </td>
            </tr>
            <tr id="author0">
                <th>Author Name</th>
                <td><input type="text" id="authorName0" name="authorName0"/><input type="button" id="newAuthorButton"
                                                                                   value="New Author"
                                                                                   onclick="addAuthor('newCollectionForm')"/>
                    <input id="numberOfAuthors" type="hidden" value="0"/></td>
            </tr>
            <tr id="collector0">
                <th>Collector Id</th>
                <td><input type="text" id="collectorId0" name="collectorId0"/><input type="button"
                                                                                     id="newCollectorButton"
                                                                                     value="New Collector"
                                                                                     onclick="addCollector('newCollectionForm')"/>
                    <input id="numberOfCollectors" type="hidden" value="0"/></td>
            </tr>
            <tr id="submitButton">
                <th></th>
                <td><input type="button" id="collectionSubmitButton" name="collectionSubmitButton"
                           value="Submit Collection" onclick="submitCollection()"/></td>
            </tr>

        </table>
        <pre id="serverResponseSubmitCollection"></pre>

        <br/>
        <b>Create Collection From JSON</b>
        <br/>
        <textarea rows="8" cols="100" id="collectionJson">{
            "title":"Money Collection",
            "summary":"This is a cool collection of non-stone money",
            "location":"http://e-research.sbs.uq.edu.au/client/Stats.html#metadata",
            "subject":[
            {"vocabulary": "anzsrc-for", "value": "160499"},
            {"vocabulary": "anzsrc-seo","value": "910102"}
            ],
            "collector":["1", "2"],
            "authors":["John Smith","Joe Blog"]
            }</textarea>
        <br/>
        <input type="button" id="addCollectionButton" value="Add Collection" onclick="addCollection()"/>
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
        <input type="button" id="getCollectionButton" value="Get Collection" onclick="getCollection()"/>
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
        <input type="button" id="updateCollectionButton" value="Update Collection" onclick="updateCollection()"/>
        <pre id="jsonResponseUpdate"></pre>

    </div>
</div>
<jsp:include page="include/footer.jsp"/>
</body>
</html>