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
        if (doc.isdefinedby.indexOf('anzsrc/seo') != -1) {
            return '<p class="subject"><input type="checkbox" name="subject"/> SEO: <a target="_blank" class="' + doc.isdefinedby + '" href="' + doc.term + '" title="' + doc.label + '">' + doc.label + '</a></p>';
        } else if (doc.isdefinedby.indexOf('anzsrc/for') != -1) {
            return '<p class="subject"><input type="checkbox"  name="subject"/> FOR: <a target="_blank" class="' + doc.isdefinedby + '" href="' + doc.term + '" title="' + doc.label + '">' + doc.label + '</a></p>';
        } else {
            return '<p class="subject"><input type="checkbox"  name="subject"/> TOA: <a target="_blank" class="' + doc.isdefinedby + '"href="' + doc.term + '" title="' + doc.label + '">' + doc.label + '</a></p>';
        }
    };

    AjaxSolr.theme.prototype.activitySnippet = function (doc) {
        return '<p><input type="checkbox" id="' + doc.atomicnumber + '"/>' + doc.activitytitle + '</p>';
    };
    AjaxSolr.theme.prototype.agentSnippet = function (doc) {
        return '<p><input type="checkbox" id="' + doc.atomicnumber + '"/>' + doc.agenttitle + '</p>';
    };
    AjaxSolr.theme.prototype.collectionSnippet = function (doc) {
        return '<p><input type="checkbox" id="' + doc.atomicnumber + '"/>' + doc.collectiontitle + '</p>';
    };
    AjaxSolr.theme.prototype.serviceSnippet = function (doc) {
        return '<p><input type="checkbox" id="' + doc.atomicnumber + '"/>' + doc.servicetitle + '</p>';
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
