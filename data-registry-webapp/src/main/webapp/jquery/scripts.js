$(document).ready(function() {
    $("#edit-tabs").tabs();
    $('.date-picker').datepicker();
    getLoginLink();
    styleTables();
});
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

function doLookup() {
    $('#lookup-div').dialog({
        modal: true,
        open: function() {
            $(this).style.display = '';
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

var UQ_REGISTRY_URI_PREFIX = 'http://localhost:8080/';
var PERSISTENT_URL = "http://purl.org/";
/**
 * Namespaces
 */
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

/**
 * rel attribute types
 */
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


/**
 * term attributes
 */
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

function getCollectionAtom() {
    var collection = getAtomEntryElement();

    //id
    var id = getSimpleElementWithText('id', UQ_REGISTRY_URI_PREFIX + 'collections/abc');
    collection.append(id);

    //type
    var collectionType = $('#collection-type-combobox').val();
    var typeCategory = getCategoryElement(NS_DCMITYPE, NS_DCMITYPE + collectionType, collectionType);
    collection.append(typeCategory);

    //title
    var titleValue = $('#edit-title-text').val();
    var title = getSimpleElementWithText('title', titleValue);
    title.attr('type', 'text');
    collection.append(title);

    //Alternative titles
    addAlternativeTitles(collection);

    var contentValue = $('#content-textarea').val();
    var content = getSimpleElementWithText('content', contentValue);
    content.attr('type', 'text');
    collection.append(content);

    //pages
    addPages(collection);

    //rights
    var rightsValue = $('#rights-textarea').val();
    var rights = getSimpleElementWithText('rights', rightsValue);
    collection.append(rights);

    //access rights
    addAccessRights(collection);

    var updated = getSimpleElementWithText('updated', '2010-10-08T05:58:02.781Z');
    collection.append(updated);

    $('#outerhtml').append(collection);
//    return collection;
}

/**
 *
 * Specific functions
 *
 */

function addAccessRights(record) {
    var accessRights = getSimpleElementWithNameSpace('rdfa:meta', NS_RDFA);
    var attributes = [
        {name: 'property', value: REL_ACCESS_RIGHTS },
        {name: 'content', value:$('#access-rights-textarea').val() }
    ];
    for each (var attribute in attributes) {
        accessRights.attr(attribute.name, attribute.value);
    }
    record.append(accessRights);

}

function addAlternativeTitles(record) {
    $('input[id|="alternative-title-text"]').each(function () {
        var alternativeTitle = getSimpleElementWithNameSpace('rdfa:meta', NS_RDFA);
        var attributes = [
            {name: 'property', value: REL_ALTERNATIVE },
            {name: 'content', value:$(this).val() }
        ];
        for each (var attribute in attributes) {
            alternativeTitle.attr(attribute.name, attribute.value);
        }
        record.append(alternativeTitle);
    });
}

function addPages(record) {
    $('input[id|="page-text"]').each(function () {
        var linkElement = getLinkElement($(this).val(), REL_PAGE);
        record.append(linkElement);
    });
}

function getAtomEntryElement() {
    var attributes = [
        {name: "xmlns", value:"http://www.w3.org/2005/Atom"},
        {name: "xmlns:app", value:"http://www.w3.org/2007/app"},
        {name: "xmlns:georss", value:NS_GEORSS},
        {name: "xmlns:rdfa", value:NS_RDFA}
    ];
    var entry = getElementWithAttributes("entry", attributes);
    return entry;
}

function getLinkElement(href, rel, title) {
    var attributes = [
        {name:'href',value: href},
        {name:'rel',value: rel},
        {name:'title',value: title}
    ];
    var link = getElementWithAttributes('link', attributes);
    return link;
}

function getCategoryElement(scheme, term, label) {
    var attributes = [
        {name:'scheme',value: scheme},
        {name:'term',value: term},
        {name:'label',value: label}
    ];
    var category = getElementWithAttributes('category', attributes);
    return category;
}

function getAuthorElement(name, email, uri) {
    var author = getSimpleElement('author');
    var nameElement = getSimpleElement('name');
    nameElement.text(name);
    author.append(nameElement);
    var emailElement = getSimpleElement('email');
    emailElement.text(email);
    author.append(emailElement);
    var uriElement = getSimpleElement('uri');
    uriElement.text(uri);
    author.append(uriElement);
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
    for each (var attribute in attributes) {
        element.attr(attribute.name, attribute.value);
    }
    return element;
}

function getSimpleElement(name) {
    return $('<' + name + '>');
}

function getSimpleElementWithNameSpace(name, namespace) {
    var element = $(document.createElementNS(namespace, name));
    return element;
}