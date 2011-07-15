/**
 * Constants
 */
var UQ_REGISTRY_URI_PREFIX = 'http://localhost:8080/';
var PERSISTENT_URL = "http://purl.org/";
var NS_FOAF = "http://xmlns.com/foaf/0.1/";
var NS_ANDS = "http://www.ands.org.au/ontologies/ns/0.1/VITRO-ANDS.owl#";
var NS_DC = PERSISTENT_URL + "dc/terms/";
var NS_DCMITYPE = PERSISTENT_URL + "dc/dcmitype/";
var NS_CLD = PERSISTENT_URL + "cld/terms/";
var NS_ANZSRC = PERSISTENT_URL + "anzsrc/";
var NS_VIVO = "http://vivoweb.org/ontology/core#";
var NS_ORE = "http://www.openarchives.org/ore/terms/";
var NS_GEORSS = "http://www.georss.org/georss/";
var NS_RDFA = "http://www.w3.org/ns/rdfa#";
var NS_EFS = "http://www.e-framework.org/Contributions/ServiceGenres/";
var NS_ATOM = "http://www.w3.org/2005/Atom";
var NS_APP = "http://www.w3.org/2007/app";
var REL_ACCESS_RIGHTS = NS_DC + "accessRights";
var REL_ALTERNATE = "alternate";
var REL_CREATOR = NS_DC + "creator";
var REL_DESCRIBES = NS_ORE + "describes";
var REL_HAS_PARTICIPANT = NS_ANDS + "hasParticipant";
var REL_HAS_OUTPUT = NS_ANDS + "hasOutput";
var REL_IS_MANAGER_OF = NS_ANDS + "isManagerOf";
var REL_IS_ACCESSED_VIA = NS_CLD + "isAccessedVia";
var REL_MADE = NS_FOAF + "made";
var REL_MBOX = NS_FOAF + "mbox";
var REL_IS_DESCRIBED_BY = NS_ORE + "isDescribedBy";
var REL_IS_LOCATED_AT = NS_CLD + "isLocatedAt";
var REL_IS_OUTPUT_OF = NS_ANDS + "isOutputOf";
var REL_IS_REFERENCED_BY = NS_DC + "isReferencedBy";
var REL_CURRENT_PROJECT = NS_FOAF + "currentProject";
var REL_IS_SUPPORTED_BY = NS_ANDS + "isSupportedBy";
var REL_LATEST_VERSION = "latest-version";
var REL_PAGE = NS_FOAF + "page";
var REL_PREDECESSOR_VERSION = "predecessor-version";
var REL_PUBLISHER = NS_DC + "publisher";
var REL_RELATED = "related";
var REL_SELF = "self";
var REL_LICENSE = "license";
var REL_SUCCESSOR_VERSION = "successor-version";
var REL_TEMPORAL = NS_DC + "temporal";
var REL_ALTERNATIVE = NS_DC + "alternative";
var REL_SPATIAL = NS_DC + "spatial";
var REL_VIA = "via";
var TERM_ANDS_GROUP = "The University of Queensland";
var TERM_ACTIVITY = NS_FOAF + "Project";
var TERM_COLLECTION = NS_DCMITYPE + "Collection";
var TERM_AGENT_AS_GROUP = NS_FOAF + "Group";
var TERM_AGENT_AS_AGENT = NS_FOAF + "Agent";
var TERM_SERVICE = NS_VIVO + "Service";
var LABEL_KEYWORD = "keyword";
var SCHEME_DCMITYPE = NS_DCMITYPE;
var SCHEME_KEYWORD = UQ_REGISTRY_URI_PREFIX + "keyword";
var SCHEME_ANZSRC_FOR = NS_ANZSRC + "for";
var SCHEME_ANZSRC_SEO = NS_ANZSRC + "seo";
var SCHEME_ANZSRC_TOA = NS_ANZSRC + "toa";
var PROPERTY_TITLE = NS_FOAF + "title";
var PROPERTY_GIVEN_NAME = NS_FOAF + "givenName";
var PROPERTY_FAMILY_NAME = NS_FOAF + "familyName";

$(document).ready(function() {
    prepareFields();
    styleTables();
    setRecordType();
    setLicenseType();
});

function prepareFields() {
    $("#edit-tabs").tabs();
    $('.date-picker').datepicker();
    $('#search-entry').focus(function() {
        if (this.value == this.defaultValue) {
            this.value = '';
        }
        if (this.value != this.defaultValue) {
            this.select();
        }
    });
    $('#search-entry').blur(function() {
        if ($.trim(this.value) == '') {
            this.value = (this.defaultValue ? this.defaultValue : '');
        }
    });
}

function ingestRecord(url, type, isNew, isPublished) {
    if (confirm("Are you sure you want to create this record?")) {
        var record = null;
        if (type == 'activity') {
            record = getActivityAtom(isNew, isPublished);
        } else if (type == 'agent') {
            record = getAgentAtom(isNew, isPublished);
        } else if (type == 'collection') {
            record = getCollectionAtom(isNew, isPublished);
        } else if (type == 'service') {
            record = getServiceAtom(isNew, isPublished);
        }
        var method = 'PUT';
        if (isNew) {
            method = 'POST';
        }
        $.ajax({
                    type: method,
                    url: url,
                    data: serializeToString(record.context),
                    contentType: "application/atom+xml",
                    processData: false,
                    dataType: 'xml',
                    success: function(data, textStatus, XMLHttpRequest) {
                        var loc = XMLHttpRequest.getResponseHeader('Location');
                        window.location.href = loc;
                    },
                    error: function(XMLHttpRequest, textStatus, errorThrown) {
                        $('#ingest-error-msg').html('<span style="color:red;">' + textStatus + '</span>');
                    }
                });
    }
    return false;
}

function deleteRecord(url) {
    if (confirm("Are you sure you want to delete this record?")) {
        $.ajax({
                    type: 'DELETE',
                    url: url,
                    success: function(data) {
                        location.reload();
                    },
                    error: function(xhr, textStatus, errorThrown) {
                        alert("Could not deleted record");
                    }
                });
    }
    return false;
}

function replicateSimpleField(inputField) {
    var parentTd = $('#' + inputField).parent();
    var numberOfFields = parentTd.find('input').length;
    var newInputField = $('#' + inputField).clone(true);
    var newFieldId = inputField + '-' + numberOfFields;
    newInputField.attr('id', newFieldId);
    parentTd.append(newInputField);
    newInputField.val("");
    parentTd.append(' <a href="#" class="remove-link" id="' + newFieldId + '-remove-link">remove</a>');
    $('#' + newFieldId + '-remove-link').click(function() {
        $('#' + newFieldId).remove();
        $(this).remove();
    });
    return false;
}

function replicateLookupField(inputField) {
    var parentRow = $('#' + inputField).parent().parent();
    var newRow = parentRow.clone(true);
    var numberOfRows = parentRow.parent().find('tr').length;
    var newFieldId = inputField + '-' + numberOfRows;
    var resultTd = newRow.find('td').eq(2);
    resultTd.text('');
    resultTd.append(' <a href="#" class="remove-link" id="' + newFieldId + '-remove-link">remove</a>');
    newRow.find('input').val('');
    parentRow.parent().append(newRow);
    $('#' + newFieldId + '-remove-link').click(function() {
        $(this).parent().parent().remove();
    });
    styleTables();
    return false;
}

function addKeyword(inputFieldId, listId) {
    var inputField = $('#' + inputFieldId);
    var keyword = inputField.val();
    if (keyword) {
        var list = $('#' + listId);
        list.append('<li class="keyword">' + keyword + '<a class="remove-keyword" id="' + keyword + '" href="#" title="Remove Keyword">x</a></li>');
        $('#' + keyword).click(function() {
            $(this).parent().remove();
        });
        inputField.val('');
        styleTables();
    }
    return false;
}

function showLookupDialog(type) {
    $('#lookup-type').val(type);
    $('#lookup-div').dialog({
                modal: true,
                open: function() {
                    $('#query').val('');
                    $('#docs').html('');
                    $('#pager').html('');
                    $('#pager-header').html('');
                    $(this).css('display', '');
                },
                height: 400,
                width: 600,
                title: 'Lookup'
            });
}
function styleTables() {
    $(".edit-table > tr:even").css("background-color", "#F4F4F8");
    $(".edit-table > tr:odd").css("background-color", "#d4d4d4");
    $(".lookup-table > tbody > tr:even").css("background-color", "#F4F4F8");
    $(".lookup-table > tbody > tr:odd").css("background-color", "#d4d4d4");
}

function setRecordType() {
    var hiddenField = $("#record-type");
    if (hiddenField) {
        var recordType = hiddenField.val();
        $('#type-combobox').val(recordType);
    }
}

function setLicenseType() {
    var hiddenField = $("#licence-type");
    if (hiddenField) {
        var licenceType = hiddenField.val();
        $('#licence-type-combobox').val(licenceType);
    }
}

/**
 *
 *
 * Functional javascript
 *
 */

function getActivityAtom(isNew, isPublished) {
    var record = getAtomEntryElement();

    //id
    var id = getSimpleElementWithText('id', UQ_REGISTRY_URI_PREFIX + 'activities/abc');
    if (!isNew) {
        //TODO id needs to be retrieved from UI
    }
    record.append(id);

    //type
    var recordType = $('#type-combobox').val();
    if (recordType) {
        var typeCategory = getCategoryElement(NS_DCMITYPE, NS_FOAF + recordType, recordType);
        record.append(typeCategory);
    }
    //title
    var titleValue = $('#edit-title-text').val();
    if (titleValue) {
        var title = getSimpleElementWithText('title', titleValue);
        title.attr('type', 'text');
        record.append(title);
    }

    //Alternative titles
    addAlternativeTitles(record);

    var contentValue = $('#content-textarea').val();
    if (contentValue) {
        var content = getSimpleElementWithText('content', contentValue);
        content.attr('type', 'text');
        record.append(content);
    }

    //pages
    addPages(record);

    //add TOA
    addTOA(record);

    //keywords
    addKeywordToCollection(record);

    //updated, format: '2010-10-08T05:58:02.781Z'
    var updated = getSimpleElementWithText('updated', '2010-10-08T05:58:02.781Z');
    record.append(updated);

    //add source
    addSource(record);

    setPublished(record, isPublished);

    return record;
}
function getAgentAtom(isNew, isPublished) {
    var record = getAtomEntryElement();

    //id
    var id = getSimpleElementWithText('id', UQ_REGISTRY_URI_PREFIX + 'agents/abc');
    if (!isNew) {
        //TODO id needs to be retrieved from UI
    }
    record.append(id);

    //type
    var recordType = $('#type-combobox').val();
    if (recordType) {
        var typeCategory = getCategoryElement(NS_DCMITYPE, NS_FOAF + recordType, recordType);
        record.append(typeCategory);
    }
    //title
    var titleValue = $('#edit-title-text').val();
    if (titleValue) {
        var title = getSimpleElementWithText('title', titleValue);
        title.attr('type', 'text');
        record.append(title);
    }

    //Alternative titles
    addAlternativeTitles(record);

    var contentValue = $('#content-textarea').val();
    if (contentValue) {
        var content = getSimpleElementWithText('content', contentValue);
        content.attr('type', 'text');
        record.append(content);
    }

    //Emails
    addEmails(record);

    //pages
    addPages(record);

    //add TOA
    addTOA(record);

    //keywords
    addKeywordToCollection(record);

    //updated, format: '2010-10-08T05:58:02.781Z'
    var updated = getSimpleElementWithText('updated', '2010-10-08T05:58:02.781Z');
    record.append(updated);

    //add source
    addSource(record);

    setPublished(record, isPublished);

    return record;
}
function getCollectionAtom(isNew, isPublished) {
    var record = getAtomEntryElement();

    //id
    var id = getSimpleElementWithText('id', UQ_REGISTRY_URI_PREFIX + 'collections/abc');
    if (!isNew) {
        //TODO id needs to be retrieved from UI
    }
    record.append(id);

    //type
    var recordType = $('#type-combobox').val();
    if (recordType) {
        var typeCategory = getCategoryElement(NS_DCMITYPE, NS_DCMITYPE + recordType, recordType);
        record.append(typeCategory);
    }
    //title
    var titleValue = $('#edit-title-text').val();
    if (titleValue) {
        var title = getSimpleElementWithText('title', titleValue);
        title.attr('type', 'text');
        record.append(title);
    }

    //Alternative titles
    addAlternativeTitles(record);

    var contentValue = $('#content-textarea').val();
    if (contentValue) {
        var content = getSimpleElementWithText('content', contentValue);
        content.attr('type', 'text');
        record.append(content);
    }
    //pages
    addPages(record);

    //add authors
    addAuthors(record);

    //add publishers
    addPublishers(record);

    addOutputOf(record);

    //add TOA
    addTOA(record);

    //keywords
    addKeywordToCollection(record);

    //temporal
    addTemporal(record);

    //spatial
    addSpatial(record);

    //publications
    addPublications(record);

    //rights
    var rightsValue = $('#rights-textarea').val();
    if (rightsValue) {
        var rights = getSimpleElementWithText('rights', rightsValue);
        record.append(rights);
    }
    //access rights
    addAccessRights(record);

    addLicense(record);

    //updated, format: '2010-10-08T05:58:02.781Z'
    var updated = getSimpleElementWithText('updated', '2010-10-08T05:58:02.781Z');
    record.append(updated);

    //add source
    addSource(record);

    setPublished(record, isPublished);

    return record;
}

function getServiceAtom(isNew, isPublished) {
    var record = getAtomEntryElement();

    //id
    var id = getSimpleElementWithText('id', UQ_REGISTRY_URI_PREFIX + 'services/abc');
    if (!isNew) {
        //TODO id needs to be retrieved from UI
    }
    record.append(id);

    //type
    var recordType = $('#type-combobox').val();
    if (recordType) {
        var typeCategory = getCategoryElement(NS_DCMITYPE, NS_EFS + recordType, recordType);
        record.append(typeCategory);
    }
    //title
    var titleValue = $('#edit-title-text').val();
    if (titleValue) {
        var title = getSimpleElementWithText('title', titleValue);
        title.attr('type', 'text');
        record.append(title);
    }

    //Alternative titles
    addAlternativeTitles(record);

    var contentValue = $('#content-textarea').val();
    if (contentValue) {
        var content = getSimpleElementWithText('content', contentValue);
        content.attr('type', 'text');
        record.append(content);
    }

    //pages
    addPages(record);

    //updated, format: '2010-10-08T05:58:02.781Z'
    var updated = getSimpleElementWithText('updated', '2010-10-08T05:58:02.781Z');
    record.append(updated);

    //add source
    addSource(record);

    setPublished(record, isPublished);

    return record;
}

/**
 *
 * Specific functions
 *
 */

function addAccessRights(record) {
    var accessRights = getSimpleElementWithNameSpace('rdfa:meta', NS_RDFA);
    var val = $('#access-rights-textarea').val();
    if (val) {
        var attributes = [
            {name: 'property', value: REL_ACCESS_RIGHTS },
            {name: 'content', value:val }
        ];
        for (var i = 0; i < attributes.length; i++) {
            var attribute = attributes[i];
            accessRights.attr(attribute.name, attribute.value);
        }
        record.append(accessRights);
    }
}

function addLicense(record) {
    var license = $('#licence-type-combobox');
    if ($(license).val() != 'none') {
        var licenseLink = getLinkElement($(license).val(), REL_LICENSE, $(license).text());
        licenseLink.attr('type', 'application/rdf+xml');
        record.append(licenseLink);
    }
}

function addAlternativeTitles(record) {
    $('input[id|="alternative-title-text"]').each(function () {
        var alternativeTitle = getSimpleElementWithNameSpace('rdfa:meta', NS_RDFA);
        var attributes = [
            {name: 'property', value: REL_ALTERNATIVE },
            {name: 'content', value:$(this).val() }
        ];
        for (var i = 0; i < attributes.length; i++) {
            var attribute = attributes[i];
            alternativeTitle.attr(attribute.name, attribute.value);
        }
        record.append(alternativeTitle);
    });
}

function addPages(record) {
    $('input[id|="page-text"]').each(function () {
        var href = $(this).val();
        if (href) {
            var linkElement = getLinkElement(href, REL_PAGE);
            record.append(linkElement);
        }
    });
}
function addEmails(record) {
    $('input[id|="email-text"]').each(function () {
        var href = $(this).val();
        if (href) {
            var linkElement = getLinkElement(href, REL_MBOX);
            record.append(linkElement);
        }
    });
}

function addAuthors(record) {
    //TODO authros needs to be retrieved from the UI
    var author = getAuthorElement('Dr Hamish Campbell', 'hamish.campbell@uq.edu.au');
    record.append(author);
}

function addPublishers(record) {
    //TODO publishers needs to be retrieved from the UI
    var publisher = getLinkElement(UQ_REGISTRY_URI_PREFIX + 'agents/1', REL_PUBLISHER, 'Abdul Alabri');
    record.append(publisher);
}

function addOutputOf(record) {
    //TODO publishers needs to be retrieved from the UI
    var activity = getLinkElement(UQ_REGISTRY_URI_PREFIX + 'activities/1', REL_IS_OUTPUT_OF);
    record.append(activity);
}

function addAccessedVia(record) {
    //TODO publishers needs to be retrieved from the UI
    var service = getLinkElement(UQ_REGISTRY_URI_PREFIX + 'services/1', REL_IS_ACCESSED_VIA);
    record.append(service);
}

function addPublications(record) {
    $('input[id|="publication-title"]').each(function () {
        var title = $(this).val();
        var href = $(this).parent().parent().find('input[id|="publication-url"]').eq(0).val();
        if (href) {
            var linkElement = getLinkElement(href, REL_IS_REFERENCED_BY, title);
            record.append(linkElement);
        }
    });
}

function addTOA(record) {
    $('div[id="type-of-activities"]').children('input:checked').each(function() {
        var category = getCategoryElement(SCHEME_ANZSRC_TOA, SCHEME_ANZSRC_TOA + '/' + $(this).val(), $(this).val());
        record.append(category);
    });
}

function addKeywordToCollection(record) {
    $('ul[id="keywords-list"]').children().each(function() {
        var keyword = $(this).clone();
        $(keyword).children().remove('a');
        var category = getCategoryElement(null, $(keyword).text(), null);
        record.append(category);
    });
}
function addTemporal(record) {
    var termporal = getSimpleElementWithNameSpace('rdfa:meta', NS_RDFA);
    var startDate = $('input[id|="start-date"]').val();
    var endDate = $('input[id|="end-date"]').val();
    var attributes = [];
    var content = '';
    if (startDate) {
        content = content + 'start=' + startDate + ' ';
    }
    if (endDate) {
        content = content + 'end=' + endDate;
    }
    if (content != '') {
        attributes[0] = {name: 'property', value: REL_TEMPORAL };
        attributes[1] = {name: 'content', value:content.trim() };
        for (var i = 0; i < attributes.length; i++) {
            var attribute = attributes[i];
            termporal.attr(attribute.name, attribute.value);
        }
        record.append(termporal);
    }
}

function addSpatial(record) {
    $('input[id|="location-name"]').each(function () {
        var title = $(this).val();
        var href = 'http://sws.geonames.org/';
        if (title) {
            var linkElement = getLinkElement(href + title, REL_SPATIAL, title);
            record.append(linkElement);
        }
    });
}


function addSource(record) {
    var source = getSimpleElement('source');
    var id = getSimpleElementWithText('id', 'http://dataspace.uq.edu.au');
    source.append(id);
    var title = getSimpleElementWithText('title', 'The University of Queensland Data Collections Registry');
    title.attr('type', 'text');
    source.append(title);

    var author = getAuthorElement('Abdul Alabri', 'a.alabri@uq.edu.au');
    source.append(author);

    record.append(source);
}

function setPublished(record, isPublished) {
    var appElement = getSimpleElementWithNameSpace('app:control', NS_APP);
    var draftElement = getSimpleElementWithNameSpace('app:draft', NS_APP);
    if (isPublished) {
        draftElement.text('no');
    } else {
        draftElement.text('yes');
    }
    appElement.append(draftElement);
    record.append(appElement);
}


function getLinkElement(href, rel, title) {
    var attributes = [];
    if (href) {
        attributes[0] = {name:'href',value: href};
    }
    if (rel) {
        attributes[1] = {name:'rel',value: rel};
    }
    if (title) {
        attributes[2] = {name:'title',value: title};
    }
    var link = getElementWithAttributes('link', attributes);
    return link;
}

function getAtomEntryElement() {
    var attributes = [
        {name: "xmlns", value:NS_ATOM},
        {name: "xmlns:app", value:NS_APP},
        {name: "xmlns:georss", value:NS_GEORSS},
        {name: "xmlns:rdfa", value:NS_RDFA}
    ];
    var entry = getElementWithAttributes("entry", attributes);
    return entry;
}

function getCategoryElement(scheme, term, label) {
    var attributes = [];
    attributes[0] = {name:'scheme',value: scheme};
    attributes[1] = {name:'term',value: term};
    attributes[2] = {name:'label',value: label};
    var category = getElementWithAttributes('category', attributes);
    return category;
}

function getAuthorElement(name, email, uri) {
    var author = getSimpleElement('author');
    if (name) {
        var nameElement = getSimpleElement('name');
        nameElement.text(name);
        author.append(nameElement);
    }
    if (email) {
        var emailElement = getSimpleElement('email');
        emailElement.text(email);
        author.append(emailElement);
    }
    if (uri) {
        var uriElement = getSimpleElement('uri');
        uriElement.text(uri);
        author.append(uriElement);
    }
    return author;
}


/**
 *
 * Generic Functions
 *
 * */
function getSimpleElementWithText(name, text) {
    var element = getSimpleElement(name);
    element.text(text);
    return element;
}

function getElementWithAttributes(name, attributes) {
    var element = getSimpleElement(name);
    for (var i = 0; i < attributes.length; i++) {
        var attribute = attributes[i];
        if (attribute.name && attribute.value) {
            element.attr(attribute.name, attribute.value);
        }
    }
    return element;
}

function getSimpleElement(name) {
    return $(document.createElementNS(NS_ATOM, name));
//    return $('<' + name + '>');
}

function getSimpleElementWithNameSpace(name, namespace) {
    var element = $(document.createElementNS(namespace, name));
    return element;
}

function serializeToString(domNode) {
    var stringXML = "";
    var elemType = domNode.nodeType;
    switch (elemType) {
        case 1: //element
            stringXML = "<" + domNode.tagName;

            for (var i = 0; i < domNode.attributes.length; i++) {
                stringXML += " " + domNode.attributes[i].name + "=\"" + domNode.attributes[i].value + "\"";
            }

            stringXML += ">";

            for (var i = 0; i < domNode.childNodes.length; i++) {
                stringXML += serializeToString(domNode.childNodes[i]);
            }

            stringXML += "</" + domNode.tagName + ">";
            break;

        case 3: //text node
            stringXML = domNode.nodeValue;
            break;
        case 4: //cdata
            stringXML = "<![CDATA[" + domNode.nodeValue + "";
            break;
        case 7: //processing instruction
            stringXML = "<?" + domNode.nodevalue + "?>";
            break;
        case 8: //comment
            stringXML = "<!--" + domNode.nodevalue + "-->";
            break;
        case 9: //document
            for (var i = 0; i < domNode.childNodes.length; i++) {
                stringXML += serializeToString(domNode.childNodes[i]);
            }
            break;
    }
    return stringXML;
}