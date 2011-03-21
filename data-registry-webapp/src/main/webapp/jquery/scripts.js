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

function replicate(inputField) {
    var parentTd = $('#' + inputField).parent();
    var numberOfFields = parentTd.children().length;
    var newInputField = $('#' + inputField).clone(true);
    var newFieldId = inputField + numberOfFields;
    newInputField.attr('id', newFieldId);
    parentTd.append(newInputField);
    parentTd.append('<a href="#" class="remove-link" id="' + newFieldId + '-remove-link">remove</a>');
    $('#' + newFieldId + '-remove-link').click(function() {
        $('#' + newFieldId).remove();
        $(this).remove();
    });
    return false;
}

function styleTables() {
    $(".edit-table > tr:even").css("background-color", "#F4F4F8");
    $(".edit-table > tr:odd").css("background-color", "#d4d4d4");
}