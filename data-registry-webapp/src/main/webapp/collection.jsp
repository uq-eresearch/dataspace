<html>
<head>
<jsp:include page="include/header.jsp"/>

<script type="text/javascript">
dojo.require("dijit.layout.ContentPane");
dojo.require("dijit.layout.TabContainer");
dojo.require("dojox.data.AppStore");
dojo.require("dojox.grid.DataGrid");
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
    row.appendChild(dojo.create('th', { innerHTML: 'Collector Id (URI)' }));
    row.appendChild(dojo.create('td', { innerHTML: '<input type="text" id="collectorId' + (numberOfcollectors + 1) + '"/>'}));
    tbody.insertBefore(row, dojo.byId('submitButton'));
    collectorsCounter.value = (numberOfcollectors + 1);
}

function submitCollection() {
    var operationElement = dojo.byId('operation');
    var authorCounter = dojo.byId('numberOfAuthors');
    var numberOfAuthors = Number(authorCounter.value);
    var subjectCounter = dojo.byId('numberOfSubjects');
    var numberOfSubjects = Number(subjectCounter.value);
    var collectorCounter = dojo.byId('numberOfCollectors');
    var numberOfCollectors = Number(collectorCounter.value);
    var now = new Date();
    var xmlString = '<?xml version="1.0"?><entry xmlns="http://www.w3.org/2005/Atom" xmlns:uqdata="http://dataspace.metadata.net/">';
    if (operationElement.value == 'add') {
        xmlString = xmlString + '<id>urn:uuid:' + now.getTime() + '</id>';
    } else {
        xmlString = xmlString + '<id>' + dojo.byId('collectionIdXml').value + '</id>';
    }
    xmlString = xmlString + '<title type="text">' + dojo.byId('collectionTitleInput').value + '</title>';
    xmlString = xmlString + '<updated>2010-10-19T01:38:40.899Z</updated>';
    xmlString = xmlString + '<summary type="text">' + dojo.byId('collectionSummaryInput').value + '</summary>';
    xmlString = xmlString + '<content type="text">' + dojo.byId('collectionContent').value + '</content>';
    for (var i = 0; i <= numberOfAuthors; i++) {
        xmlString = xmlString + '<author><name>' + dojo.byId('authorName' + i).value + '</name></author>';
    }
    xmlString = xmlString + '<uqdata:location>' + dojo.byId('collectionLocationInput').value + '</uqdata:location>';
    for (var i = 0; i <= numberOfSubjects; i++) {
        xmlString = xmlString + '<uqdata:subject vocabulary="' + dojo.byId('subjectVocabulary' + i).value + '" value="' + dojo.byId('subjectValue' + i).value + '" />';
    }
    for (var i = 0; i <= numberOfCollectors; i++) {
        xmlString = xmlString + '<uqdata:collector uri="' + dojo.byId('collectorId' + i).value + '" />';
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
            return response;
        },
        error: function(response, ioArgs) {
            responseElement.innerHTML = response;
            return response;
        }
    });

}

function clearResponse(preElement) {
    var element = dojo.byId(preElement);
    element.innerHTML = "";
}

function loadCollection(id) {
    dojo.xhrGet({
        url:id,
        handleAs:"xml",
        headers: {
            "Content-Type": "text/plain",
            "Accept": "application/atom+xml;type=entry",
            "Content-Encoding": "utf-8"
        },
        preventCache: true,
        timeout: 5000,
        load: function(response, ioArgs) {
            removeExtraFields();
            var idElement = dojo.byId('collectionIdXml');
            idElement.value = response.getElementsByTagName('id')[0].childNodes[0].nodeValue;

            var titleElement = dojo.byId('collectionTitleInput');
            titleElement.value = response.getElementsByTagName('title')[0].childNodes[0].nodeValue;

            var locationElement = dojo.byId('collectionLocationInput');
            locationElement.value = response.getElementsByTagName('uqdata:location')[0].childNodes[0].nodeValue;

            var summaryElement = dojo.byId('collectionSummaryInput');
            summaryElement.value = response.getElementsByTagName('summary')[0].childNodes[0].nodeValue;

            var descriptionElement = dojo.byId('collectionContent');
            descriptionElement.value = response.getElementsByTagName('content')[0].childNodes[0].nodeValue;

            var subjectList = response.getElementsByTagName('uqdata:subject');

            if (subjectList && subjectList.length > 0) {
                var subjectValue = dojo.byId('subjectValue0');
                var subjectVocab = dojo.byId('subjectVocabulary0');
                subjectVocab.value = subjectList[0].attributes.getNamedItem('vocabulary').value;
                subjectValue.value = subjectList[0].attributes.getNamedItem('value').value;
                if (subjectList.length > 1) {
                    for (var i = 1; i < subjectList.length; i++) {
                        addSubject('newCollectionForm');
                        subjectVocab = dojo.byId('subjectVocabulary' + i);
                        subjectValue = dojo.byId('subjectValue' + i);
                        subjectVocab.value = subjectList[i].attributes.getNamedItem('vocabulary').value;
                        subjectValue.value = subjectList[i].attributes.getNamedItem('value').value;
                    }
                }
            }
            var authorList = response.getElementsByTagName('author');
            var authorElement = dojo.byId('authorName0');
            var authorName = authorList.item(0).childNodes[0].nextSibling.childNodes[0];
            authorElement.value = authorName.nodeValue;
            if (authorList.length > 1) {
                for (var i = 1; i < authorList.length; i++) {
                    addAuthor('newCollectionForm');
                    authorElement = dojo.byId('authorName' + i);
                    authorName = authorList.item(i).childNodes[0].nextSibling.childNodes[0];
                    authorElement.value = authorName.nodeValue;
                }
            }

            var collectorList = response.getElementsByTagName('uqdata:collector');
            if (collectorList && collectorList.length > 0) {
                var collectorElement = dojo.byId('collectorId0');
                collectorElement.value = collectorList[0].attributes.getNamedItem('uri').value;
                if (collectorList.length > 1) {
                    for (var i = 1; i < collectorList.length; i++) {
                        addCollector('newCollectionForm');
                        collectorElement = dojo.byId('collectorId' + i);
                        collectorElement.value = collectorList[i].attributes.getNamedItem('uri').value;
                    }
                }
            }
            setOperation('edit');
            dijit.byId('mainTabContainer').selectChild('tabAddUpdateCollections');

            return response;
        },
        error: function(response, ioArgs) {
            responseElement.innerHTML = response;
            return response;
        }
    });
}
function deleteCollection(id) {
    var responseElement = dojo.byId('serverResponseSubmitCollection');
    dojo.xhrDelete({
        url:id,
        handleAs:"xml",
        headers: {
            "Content-Type": "text/plain",
            "Accept": "application/atom+xml;type=entry",
            "Content-Encoding": "utf-8"
        },
        preventCache: true,
        timeout: 5000,
        load: function(response, ioArgs) {
            return response;
        },
        error: function(response, ioArgs) {
            responseElement.innerHTML = response;
            return response;
        }
    });
}

function removeExtraFields() {

    var subjectCounter = dojo.byId('numberOfSubjects');
    var numberOfSubjects = Number(subjectCounter.value);
    if (numberOfSubjects > 0) {
        for (var i = 1; i <= numberOfSubjects; i++) {
            removeElement('newCollectionForm', 'subject' + i);
        }
    }
    var authorCounter = dojo.byId('numberOfAuthors');
    var numberOfAuthors = Number(authorCounter.value);
    if (numberOfAuthors > 0) {
        for (var i = 1; i <= numberOfAuthors; i++) {
            removeElement('newCollectionForm', 'author' + i);
        }
    }
    var collectorCounter = dojo.byId('numberOfCollectors');
    var numberOfCollectors = Number(collectorCounter.value);
    if (numberOfCollectors > 0) {
        for (var i = 1; i <= numberOfCollectors; i++) {
            removeElement('newCollectionForm', 'collector' + i);
        }
    }
    dojo.byId('numberOfSubjects').value = 0;
    dojo.byId('numberOfAuthors').value = 0;
    dojo.byId('numberOfCollectors').value = 0;
}
function setOperation(op) {
    var operationElement = dojo.byId('operation');
    operationElement.value = op;
    var submitButton = dojo.byId('collectionSubmitButton');

    if (op == 'add') {
        removeExtraFields();
        submitButton.value = 'Submit Collection';
    } else if (op == 'edit') {
        submitButton.value = 'Update Collection';
    }
}
function removeElement(tb, elementId) {
    var table = dojo.byId(tb);
    var tbody = table.getElementsByTagName('tbody')[0];
    var element = dojo.byId(elementId);
    tbody.removeChild(element);
}
</script>
</head>
<body class="tundra">
<div class="wrapper">
    <jsp:include page="include/title.jsp"/>
    <h2>Collections</h2>

    <div class="content">
        <script type="text/javascript">
            var collectionLayout = [
                [
                    {
                        field: "title",
                        name: "Title",
                        width: 15
                    },
                    {
                        field: "author",
                        name: "Authors",
                        width: '10',
                        formatter: function(value) {
                            var ret = "";
                            if (value.name) {
                                ret = value.name;
                            }
                            if (value.email) {
                                if (value.name) {
                                    ret += " (" + value.email + ")";
                                } else {
                                    ret = value.email;
                                }
                            }
                            return ret;
                        }
                    },
                    {
                        field: "updated",
                        name: "Last Modified",
                        width: 'auto'
                    },
                    {
                        field: "id",
                        name: "Action",
                        width: 10,
                        formatter: function(item) {
                            var viewURL = "<a href=\"#\" onClick=\"loadCollection('" + item.toString() + "')\">Edit</a> <a href=\"#\" onClick=\"deleteCollection('" + item.toString() + "')\">Delete</a>";
                            return viewURL;
                        }
                    }
                ]
            ];
        </script>
        <div id="mainTabContainer" dojoType="dijit.layout.TabContainer" style="width:100%;height:60ex">
            <div id="tabCollections" dojoType="dijit.layout.ContentPane" title="Collections"
                 style="width:40em;height:60ex;">
                <div dojoType="dojox.data.AppStore"
                     url="/collections"
                     jsId="collectionStore" label="title">
                    <script type="dojo/method" event="onLoad">
                        grid.setSortIndex(1, true);
                    </script>
                </div>
                <div jsId="grid" dojoType="dojox.grid.DataGrid" store="collectionStore" query="{}"
                     structure="collectionLayout" style="width: 600px; height: 200px;"></div>
            </div>
            <div id="tabAddUpdateCollections" dojoType="dijit.layout.ContentPane" title="Post/Put (ATOM)"
                 style="width:40em;height:60ex;">
                <form id="collectionForm">
                    <table id="newCollectionForm">
                        <tr>
                            <th>Title</th>
                            <td>
                                <input type="text" id="collectionTitleInput" name="collectionTitleInput"/>
                                <input type="hidden" id="collectionIdXml" value=""/>
                                <input type="hidden" id="operation" value=""/>
                            </td>
                        </tr>
                        <tr>
                            <th>Location</th>
                            <td><input type="text" id="collectionLocationInput" name="collectionLocationInput"/></td>
                        </tr>
                        <tr>
                            <th>Summary</th>
                            <td><input type="text" id="collectionSummaryInput" name="collectionSummaryInput"/></td>
                        </tr>
                        <tr>
                            <th>Content</th>
                            <td><textarea id="collectionContent" rows="5" cols="50"></textarea></td>
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
                            <td><input type="text" id="authorName0" name="authorName0"/><input type="button"
                                                                                               id="newAuthorButton"
                                                                                               value="New Author"
                                                                                               onclick="addAuthor('newCollectionForm')"/>
                                <input id="numberOfAuthors" type="hidden" value="0"/></td>
                        </tr>
                        <tr id="collector0">
                            <th>Collector ID (URI)</th>
                            <td><input type="text" id="collectorId0" name="collectorId0"/><input type="button"
                                                                                                 id="newCollectorButton"
                                                                                                 value="New Collector"
                                                                                                 onclick="addCollector('newCollectionForm')"/>
                                <input id="numberOfCollectors" type="hidden" value="0"/></td>
                        </tr>
                        <tr id="submitButton">
                            <th></th>
                            <td><input type="button" id="collectionSubmitButton" name="collectionSubmitButton"
                                       value="Submit Collection" onclick="submitCollection()"/> <input type="button"
                                                                                                       value="Clear Output"
                                                                                                       onclick="clearResponse('serverResponseSubmitCollection')"/>
                                <input type="reset" value="Reset Form" onclick="setOperation('add')">
                            </td>
                        </tr>

                    </table>
                </form>
                <pre id="serverResponseSubmitCollection"></pre>
            </div>
            <div id="tabJsonCollections" dojoType="dijit.layout.ContentPane" title="Post (JSON)"
                 style="width:40em;height:60ex;">
                <br/>
                <b>Create Collection From JSON</b>
                <br/>
                <textarea rows="8" cols="100" id="collectionJson">{
                    "title":"Money Collection",
                    "summary":"This is a cool collection of non-stone money",
                    "content":"This is a cool collection of non-stone money description",
                    "location":"http://e-research.sbs.uq.edu.au/client/Stats.html#metadata",
                    "subject":[
                    {"vocabulary": "anzsrc-for", "value": "160499"},
                    {"vocabulary": "anzsrc-seo","value": "910102"}
                    ],
                    "collector":["1", "2"],
                    "authors":["John Smith","Joe Blog"]
                    }</textarea>
                <br/>
                <input type="button" id="addCollectionButton" value="Add Collection" onclick="addCollection()"/> <input
                    type="button" value="Clear Output" onclick="clearResponse('serverResponse')"/>
                <pre id="serverResponse"></pre>

            </div>
            <div id="tabPutCollections" dojoType="dijit.layout.ContentPane" title="Put (JSON)"
                 style="width:40em;height:60ex;">

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
                <input type="button" id="updateCollectionButton" value="Update Collection"
                       onclick="updateCollection()"/>
                <input type="button" value="Clear Output" onclick="clearResponse('jsonResponseUpdate')"/>
                <pre id="jsonResponseUpdate"></pre>
            </div>
            <div id="tabGetCollections" dojoType="dijit.layout.ContentPane" title="Get" style="width:40em;height:60ex;">

                <b>Get Collection</b>
                <br/>
                Enter Collection Id: <input id="collectionId" name="collectionId" type="text"/>
                <br/>
                Return Content-Type <select id="contentTypeForGet" name="contentTypeForGet">
                <option value="application/atom+xml" selected="selected">application/atom+xml</option>
                <option value="application/json">application/json</option>
            </select>
                <br/>
                <input type="button" id="getCollectionButton" value="Get Collection" onclick="getCollection()"/> <input
                    type="button" value="Clear Output" onclick="clearResponse('jsonResponseGet')"/>
                <pre id="jsonResponseGet"></pre>
            </div>

        </div>
    </div>
</div>
<jsp:include page="include/footer.jsp"/>
</body>
</html>