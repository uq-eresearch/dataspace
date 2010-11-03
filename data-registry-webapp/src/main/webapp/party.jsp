<html>
<head>
<jsp:include page="include/header.jsp"/>
<script type="text/javascript">
dojo.require("dijit.layout.ContentPane");
dojo.require("dijit.layout.TabContainer");
dojo.require("dojox.data.AppStore");
dojo.require("dojox.grid.DataGrid");
dojo.require("dojo.parser");
var partyLayout = [
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
                var viewURL = "<a href=\"#\" onClick=\"loadParty('" + item.toString() + "')\">Edit</a> <a href=\"#\" onClick=\"deleteParty('" + item.toString() + "')\">Delete</a>";
                return viewURL;
            }
        }
    ]
];
function submitJsonParty() {
    var responseElement = dojo.byId('serverResponse');
    dojo.xhrPost({
        url:'/parties',
        contentType:"application/json",
        headers: {
            "Cache-Control": "no-cache"
        },
        postData: dojo.byId('partyJson').value,
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
    var jsonContent = dojo.byId('updatePartyJson').value;
    dojo.xhrPut({
        url:'/parties/' + partyIdField.value,
        handleAs:"text",
        headers: {
            "Content-Type": "application/json",
            "Accept": contentTypeCombo.value,
            "Content-Encoding": "utf-8"
        },
        putData: jsonContent,
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
    tbody.insertBefore(row, dojo.byId('collection0'));
    authorCounter.value = (numberOfAuthors + 1);
}
function addCollection(tb) {
    var table = dojo.byId(tb);
    var tbody = table.getElementsByTagName('tbody')[0];
    var collectorsCounter = dojo.byId('numberOfCollections');
    var numberOfcollectors = Number(collectorsCounter.value);
    var row = dojo.create('tr');
    row.setAttribute('id', 'collection' + (numberOfcollectors + 1));
    row.appendChild(dojo.create('th', { innerHTML: 'Collection Id (URI)' }));
    row.appendChild(dojo.create('td', { innerHTML: '<input type="text" id="collectionId' + (numberOfcollectors + 1) + '"/>'}));
    tbody.insertBefore(row, dojo.byId('submitButton'));
    collectorsCounter.value = (numberOfcollectors + 1);
}

function submitAtomParty() {
    var operationElement = dojo.byId('operation');
    var authorCounter = dojo.byId('numberOfAuthors');
    var numberOfAuthors = Number(authorCounter.value);
    var subjectCounter = dojo.byId('numberOfSubjects');
    var numberOfSubjects = Number(subjectCounter.value);
    var collectionCounter = dojo.byId('numberOfCollections');
    var numberOfCollections = Number(collectionCounter.value);
    var now = new Date();
    var xmlString = '<?xml version="1.0"?><entry xmlns="http://www.w3.org/2005/Atom" xmlns:uqdata="http://dataspace.metadata.net/">';
    if (operationElement.value == 'add') {
        xmlString = xmlString + '<id>urn:uuid:' + now.getTime() + '</id>';
    } else {
        xmlString = xmlString + '<id>' + dojo.byId('partyIdXml').value + '</id>';
    }
    xmlString = xmlString + '<title type="text">' + dojo.byId('partyTitleInput').value + '</title>';
    xmlString = xmlString + '<updated>2010-10-19T01:38:40.899Z</updated>';
    xmlString = xmlString + '<summary type="text">' + dojo.byId('partySummaryInput').value + '</summary>';
    xmlString = xmlString + '<content type="text">' + dojo.byId('partyContent').value + '</content>';
    for (var i = 0; i <= numberOfAuthors; i++) {
        xmlString = xmlString + '<author><name>' + dojo.byId('authorName' + i).value + '</name></author>';
    }
    for (var i = 0; i <= numberOfSubjects; i++) {
        xmlString = xmlString + '<uqdata:subject vocabulary="' + dojo.byId('subjectVocabulary' + i).value + '" value="' + dojo.byId('subjectValue' + i).value + '" />';
    }
    for (var i = 0; i <= numberOfCollections; i++) {
        xmlString = xmlString + '<uqdata:collectorOf uri="' + dojo.byId('collectionId' + i).value + '" />';
    }
    xmlString = xmlString + '</entry>';

    var responseElement = dojo.byId('serverResponseSubmitParty');
    var url = '/parties';
    if (operationElement.value == 'edit') {
        url = dojo.byId('partyIdXml').value;
    }
    var httpArgs = {
        url:url,
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
    };
    if (operationElement.value == 'add') {
        dojo.xhrPost(httpArgs);
    } else if (operationElement.value == 'edit') {
        dojo.xhrPut(httpArgs);
    }
}

function clearResponse(preElement) {
    var element = dojo.byId(preElement);
    element.innerHTML = "";
}


function loadParty(id) {
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
            var idElement = dojo.byId('partyIdXml');
            idElement.value = response.getElementsByTagName('id')[0].childNodes[0].nodeValue;

            var titleElement = dojo.byId('partyTitleInput');
            titleElement.value = response.getElementsByTagName('title')[0].childNodes[0].nodeValue;

            var summaryElement = dojo.byId('partySummaryInput');
            summaryElement.value = response.getElementsByTagName('summary')[0].childNodes[0].nodeValue;

            var contentElement = dojo.byId('partyContent');
            contentElement.value = response.getElementsByTagName('content')[0].childNodes[0].nodeValue;

            var subjectList = response.getElementsByTagName('uqdata:subject');
            if (subjectList && subjectList.length > 0) {
                var subjectVocab = dojo.byId('subjectVocabulary0');
                var subjectValue = dojo.byId('subjectValue0');
                subjectVocab.value = subjectList[0].attributes.getNamedItem('vocabulary').value;
                subjectValue.value = subjectList[0].attributes.getNamedItem('value').value;
                if (subjectList.length > 1) {
                    for (var i = 1; i < subjectList.length; i++) {
                        addSubject('newPartyForm');
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
                    addAuthor('newPartyForm');
                    authorElement = dojo.byId('authorName' + i);
                    authorName = authorList.item(i).childNodes[0].nextSibling.childNodes[0];
                    authorElement.value = authorName.nodeValue;
                }
            }

            var collectionList = response.getElementsByTagName('uqdata:collectorOf');
            if (collectionList && collectionList.length > 0) {
                var collectionElement = dojo.byId('collectionId0');
                collectionElement.value = collectionList[0].attributes.getNamedItem('uri').value;
                if (collectionList.length > 1) {
                    for (var i = 1; i < collectionList.length; i++) {
                        addCollection('newPartyForm');
                        collectionElement = dojo.byId('collectionId' + i);
                        collectionElement.value = collectionList[i].attributes.getNamedItem('uri').value;
                    }
                }
            }
            setOperation('edit');
            dijit.byId('mainTabContainer').selectChild('tabAddUpdateParties');

            return response;
        },
        error: function(response, ioArgs) {
            responseElement.innerHTML = response;
            return response;
        }
    });
}
function deleteParty(id) {
    var responseElement = dojo.byId('serverResponseSubmitParty');
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
            removeElement('newPartyForm', 'subject' + i);
        }
    }
    var authorCounter = dojo.byId('numberOfAuthors');
    var numberOfAuthors = Number(authorCounter.value);
    if (numberOfAuthors > 0) {
        for (var i = 1; i <= numberOfAuthors; i++) {
            removeElement('newPartyForm', 'author' + i);
        }
    }
    var collectorCounter = dojo.byId('numberOfCollections');
    var numberOfCollectors = Number(collectorCounter.value);
    if (numberOfCollectors > 0) {
        for (var i = 1; i <= numberOfCollectors; i++) {
            removeElement('newPartyForm', 'collector' + i);
        }
    }
    dojo.byId('numberOfSubjects').value = 0;
    dojo.byId('numberOfAuthors').value = 0;
    dojo.byId('numberOfCollections').value = 0;
}
function setOperation(op) {
    var operationElement = dojo.byId('operation');
    operationElement.value = op;
    var submitButton = dojo.byId('partySubmitButton');

    if (op == 'add') {
        removeExtraFields();
        submitButton.value = 'Submit Party';
    } else if (op == 'edit') {
        submitButton.value = 'Update Party';
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
    <h2>Parties</h2>

    <div class="content">
        <br/>

        <div id="mainTabContainer" dojoType="dijit.layout.TabContainer" style="width:100%;height:60ex">
            <div id="tabParties" dojoType="dijit.layout.ContentPane" title="Parties"
                 style="width:40em;height:60ex;">
                <div dojoType="dojox.data.AppStore"
                     url="/parties?repr=application/atom+xml;type=feed"
                     jsId="partyStore" label="title">
                    <script type="dojo/method" event="onLoad">
                        grid.setSortIndex(1, true);
                    </script>
                </div>
                <div dojoType="dojox.grid.DataGrid" jsId="grid" store="partyStore" query="{}"
                     structure="partyLayout" noDataMessage="No parties yet. Add one."
                     style="width: 600px; height: 200px;"></div>
            </div>
            <div id="tabAddUpdateParties" dojoType="dijit.layout.ContentPane" title="Post/Put (ATOM)"
                 style="width:40em;height:60ex;">
                <form id="partyForm">
                    <table id="newPartyForm">
                        <tr>
                            <th>Title</th>
                            <td>
                                <input type="text" id="partyTitleInput" name="partyTitleInput"/>
                                <input type="hidden" id="partyIdXml" value=""/>
                                <input type="hidden" id="operation" value="add"/>
                            </td>
                        </tr>
                        <tr>
                            <th>Summary</th>
                            <td><input type="text" id="partySummaryInput" name="partySummaryInput"/></td>
                        </tr>
                        <tr>
                            <th>Content</th>
                            <td><textarea id="partyContent" rows="5" cols="50"></textarea></td>
                        </tr>
                        <tr id="subject0">
                            <th>Subject:</th>
                            <td>Vocabulary <input type="text" id="subjectVocabulary0"/> Value <input type="text"
                                                                                                     id="subjectValue0"/>
                                <input type="button" id="newSubjectButton" value="New Subject"
                                       onclick="addSubject('newPartyForm')"/>
                                <input id="numberOfSubjects" type="hidden" value="0"/>
                            </td>
                        </tr>
                        <tr id="author0">
                            <th>Author Name</th>
                            <td><input type="text" id="authorName0" name="authorName0"/><input type="button"
                                                                                               id="newAuthorButton"
                                                                                               value="New Author"
                                                                                               onclick="addAuthor('newPartyForm')"/>
                                <input id="numberOfAuthors" type="hidden" value="0"/></td>
                        </tr>
                        <tr id="collection0">
                            <th>Collection ID (URI)</th>
                            <td><input type="text" id="collectionId0" name="collectionId0"/><input type="button"
                                                                                                   id="newCollectionButton"
                                                                                                   value="New Collection"
                                                                                                   onclick="addCollection('newPartyForm')"/>
                                <input id="numberOfCollections" type="hidden" value="0"/></td>
                        </tr>
                        <tr id="submitButton">
                            <th></th>
                            <td><input type="button" id="partySubmitButton" name="partySubmitButton"
                                       value="Submit Party" onclick="submitAtomParty()"/> <input type="button"
                                                                                                 value="Clear Output"
                                                                                                 onclick="clearResponse('serverResponseSubmitParty')"/>
                                <input type="reset" value="Reset Form" onclick="setOperation('add')">
                            </td>
                        </tr>

                    </table>
                </form>
                <pre id="serverResponseSubmitParty"></pre>
            </div>


            <div id="tabAddPartyJson" dojoType="dijit.layout.ContentPane" title="Post (JSON)"
                 style="width:40em;height:60ex;">
                <br/>
                <textarea rows="8" cols="100" id="partyJson">{
                    "title":"Tea Party",
                    "summary":"Tax Enough Already",
                    "content":"Party Content",
                    "subject":[{"vocabulary": "anzsrc-for", "value": "160499"}],
                    "authors":["Abdul Alabri","Nigel Ward"],
                    "collectorOf":["4"]
                    }</textarea>
                <br/>
                <input type="button" id="postButton" value="Add Party" onclick="submitJsonParty()"/>
                <pre id="serverResponse"></pre>
            </div>
            <div id="tabEditPartyJson" dojoType="dijit.layout.ContentPane" title="Put (JSON)"
                 style="width:40em;height:60ex;">

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
                <input type="button" id="putButton" value="Update Party" onclick="updateParty()"/>
                <pre id="jsonResponseUpdate"></pre>
            </div>
            <div id="tabGetParty" dojoType="dijit.layout.ContentPane" title="Get"
                 style="width:40em;height:60ex;">
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
                <input type="button" id="getButton" value="Get Party" onclick="getParty()"/>
                <pre id="jsonResponseGet"></pre>
            </div>
        </div>
    </div>
</div>
<jsp:include page="include/footer.jsp"/>
</body>
</html>