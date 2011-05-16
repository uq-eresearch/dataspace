(function ($) {

    AjaxSolr.theme.prototype.result = function (doc, snippet) {
        var output = '<div><h2>' + doc.title + '</h2>';
//        if (doc.label && !doc.title) {
//            output = '<div><h2>' + doc.label + '</h2>';
//        }
        output += '<p id="links_' + doc.id + '" class="links"></p>';
        output += '<p>' + snippet + '</p></div>';
        return output;
    };

    AjaxSolr.theme.prototype.snippet = function (doc) {
        var output = '';
        if (doc.description.length > 300) {
            output += doc.description.substring(0, 300);
//    output += '<span style="display:none;">' + doc.text.substring(300);
            output += '</span> <a href="#" class="more">more</a>';
        }
        else {
            output += doc.description;
        }
        return output;
    };
//
//    AjaxSolr.theme.prototype.subjectResult = function (doc, snippet) {
//        var output = '<div><h2>' + doc.title + '</h2>';
////        if (doc.label && !doc.title) {
////            output = '<div><h2>' + doc.label + '</h2>';
////        }
//        output += '<p id="links_' + doc.id + '" class="links"></p>';
//        output += '<p>' + snippet + '</p></div>';
//        return output;
//    };

    AjaxSolr.theme.prototype.subjectSnippet = function (doc) {
        return '<p><input type="checkbox" id="' + doc.term + '"/> <a target="_blank" href="' + doc.term + '" title="' + doc.label + '">' + doc.label + '</a></p>';
    };

    AjaxSolr.theme.prototype.tag = function (value, weight, handler) {
        return $('<a href="#" class="tagcloud_item"/>').text(value).addClass('tagcloud_size_' + weight).click(handler);
    };

    AjaxSolr.theme.prototype.facet_link = function (value, handler) {
        return $('<a href="#"/>').text(value).click(handler);
    };

    AjaxSolr.theme.prototype.no_items_found = function () {
        return 'no items found in current selection';
    };

})(jQuery);
