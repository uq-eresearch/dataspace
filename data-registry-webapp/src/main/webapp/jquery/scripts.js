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


function getCollectionAtom() {
    var collection = getAtomEntryElement();
    var id = getSimpleElementWithText('id', 'http://localhost:8080/collections/abc')
    collection.append(id);
    var titleValue = $('#edit-title-text').val();
    var title = getSimpleElementWithText('title', titleValue);
    title.attr('type', 'text');
    collection.append(title);
    var collectionType = $('#collection-type-combobox').val();
    var typeCategory = getCategoryElement('', '', collectionType);
    collection.append(typeCategory);
    var contentValue = $('#content-textarea').val();
    var content = getSimpleElementWithText('content', contentValue);
    content.attr('type', 'text');
    collection.append(content);
    var updated = getSimpleElementWithText('updated', '2010-10-08T05:58:02.781Z');
    collection.append(updated);
    $('#outerhtml').append(collection);
//    return collection;
}

function getAtomEntryElement() {
    var attributes = [
        {name: "xmlns", value:"http://www.w3.org/2005/Atom"},
        {name: "xmlns:app", value:"http://www.w3.org/2007/app"},
        {name: "xmlns:georss", value:"http://www.georss.org/georss/"},
        {name: "xmlns:rdfa", value:"http://www.w3.org/ns/rdfa#"}
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