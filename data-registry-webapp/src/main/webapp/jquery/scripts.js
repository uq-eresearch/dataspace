$(document).ready(function() {
    $("#edit-tabs").tabs();
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
    newRow.find('td').eq(2).append(' <a href="#" class="remove-link" id="' + newFieldId + '-remove-link">remove</a>');
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

function styleTables() {
    $(".edit-table > tr:even").css("background-color", "#F4F4F8");
    $(".edit-table > tr:odd").css("background-color", "#d4d4d4");
    $(".lookup-table > tbody > tr:even").css("background-color", "#F4F4F8");
    $(".lookup-table > tbody > tr:odd").css("background-color", "#d4d4d4");
}