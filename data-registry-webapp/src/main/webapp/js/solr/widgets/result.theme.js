(function ($) {

    AjaxSolr.theme.prototype.result = function (doc, snippet) {
        var output = "";
        if (doc.title) {
            output = '<div class="result-record"><a href="/' + getPath(doc) + '/' + fromDecimalToOtherBase(31, doc.atomicnumber) + '">' + doc.title + '</a>';
            output += '<p>' + snippet + '</p></div>';
        }
        return output;
    };

    AjaxSolr.theme.prototype.snippet = function (doc) {
        var output = '';
        if (doc.description) {
            if (doc.description.length > 300) {
                output += doc.description.substring(0, 300);
                output += '</span>... <a href="/' + getPath(doc) + '/' + fromDecimalToOtherBase(31, doc.atomicnumber) + '">more</a>';
            }
            else {
                output += doc.description;
            }
        }
        return output;
    };

    AjaxSolr.theme.prototype.subjectLookup = function (doc) {
        if (doc.isdefinedby.indexOf('anzsrc/seo') != -1) {
            return '<p class="subject"><input type="checkbox" name="subject"/> SEO: <a target="_blank" class="' + doc.isdefinedby + '" href="' + doc.term + '" title="' + doc.label + '">' + doc.label + '</a></p>';
        } else if (doc.isdefinedby.indexOf('anzsrc/for') != -1) {
            return '<p class="subject"><input type="checkbox"  name="subject"/> FOR: <a target="_blank" class="' + doc.isdefinedby + '" href="' + doc.term + '" title="' + doc.label + '">' + doc.label + '</a></p>';
        }
    };

    AjaxSolr.theme.prototype.activityLookup = function (doc) {
        return '<p><input type="checkbox" id="' + doc.atomicnumber + '"/>' + doc.activitytitle + '</p>';
    };

    AjaxSolr.theme.prototype.agentLookup = function (doc) {
        return '<p><input type="checkbox" id="' + doc.atomicnumber + '"/>' + doc.agenttitle + '</p>';
    };
    AjaxSolr.theme.prototype.collectionLookup = function (doc) {
        return '<p><input type="checkbox" id="' + doc.atomicnumber + '"/>' + doc.collectiontitle + '</p>';
    };
    AjaxSolr.theme.prototype.serviceLookup = function (doc) {
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

    AjaxSolr.theme.prototype.activityFeed = function (doc) {
        return '<p><a href="">' + doc.activitytitle + '</a></p>';
    };

    AjaxSolr.theme.prototype.agentFeed = function (doc) {
        return '<p><a href="">' + doc.agenttitle + '</a></p>';
    };

    AjaxSolr.theme.prototype.collectionFeed = function (doc) {
        return '<p><a href="">' + doc.newcollectiontitle + '</a> created on ' + doc.newcollectioncreated + '</p>';
    };

    AjaxSolr.theme.prototype.serviceFeed = function (doc) {
        return '<p><a href="">' + doc.servicetitle + '</a></p>';
    };

})(jQuery);

var BASE_CHARACTERS = "0123456789bcdfghjklmnpqrstvwxyz";
function fromDecimalToOtherBase(base, decimalNumber) {
    var tempVal = decimalNumber == 0 ? "0" : "";
    var mod = 0;
    while (decimalNumber != 0) {
        mod = decimalNumber % base;
        tempVal = BASE_CHARACTERS.substring(mod, mod + 1) + tempVal;
        decimalNumber = decimalNumber / base;
        decimalNumber = parseInt(decimalNumber);
    }
    return tempVal + '';
}

function fromOtherBaseToDecimal(base, number) {
    var iterator = number.length();
    var returnValue = 0;
    var multiplier = 1;

    while (iterator > 0) {
        returnValue = returnValue + (BASE_CHARACTERS.indexOf(number.substring(iterator - 1, iterator)) * multiplier);
        multiplier = multiplier * base;
        --iterator;
    }
    return returnValue;
}

function getPath(doc) {
    if (doc.type && (doc.type.toLowerCase() == "collection" || doc.type.toLowerCase() == "dataset")) {
        return "collections"
    } else if (doc.type && (doc.type.toLowerCase() == "person" || doc.type.toLowerCase() == "group")) {

    } else if (doc.type && (doc.type.toLowerCase() == "program" || doc.type == "project")) {
        return "activities"
    } else {
        return "services"
    }
}
