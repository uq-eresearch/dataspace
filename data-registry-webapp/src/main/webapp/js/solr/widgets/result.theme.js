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
                output += '<span class="result-entity-type">' + getEntityType(doc) + '</span>: ' + doc.description.substring(0, 300);
                output += '</span>... <a href="/' + getPath(doc) + '/' + fromDecimalToOtherBase(31, doc.atomicnumber) + '">more</a>';
            } else {
                output += '<span class="result-entity-type">' + getEntityType(doc) + '</span>: ' + doc.description;
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

    var genericLookup = function (doc) {
        return $.mustache('<p><input type="checkbox" id="lookup-{{atomicnumber}}" name="lookup" value="{{atomicnumber}}" /><a href="{{uri}}" title="{{description}}" onclick="window.open(this.href, \'_blank\'); return false;">{{title}}</a></p>', doc);
    };
    
    AjaxSolr.theme.prototype.projectLookup = function(doc) { 
    	var uriPrefix = $.mustache('{{protocol}}//{{host}}/collections',window.location);
    	doc.uri = uriPrefix+'/'+doc.atomicnumber;
    	return genericLookup(doc, uriPrefix);
    };

    AjaxSolr.theme.prototype.personLookup = function(doc) { 
    	var uriPrefix = $.mustache('{{protocol}}//{{host}}/agents',window.location);
    	doc.uri = uriPrefix+'/'+doc.atomicnumber;
    	return genericLookup(doc);
    };
    
    AjaxSolr.theme.prototype.collectionLookup = function(doc) { 
    	var uriPrefix = $.mustache('{{protocol}}//{{host}}/collections',window.location);
    	doc.uri = uriPrefix+'/'+doc.atomicnumber;
    	return genericLookup(doc);
    };
    
    AjaxSolr.theme.prototype.reportLookup = function(doc) { 
    	var uriPrefix = $.mustache('{{protocol}}//{{host}}/reports',window.location);
    	doc.uri = uriPrefix+'/'+doc.atomicnumber;
    	return genericLookup(doc);
    };

    AjaxSolr.theme.prototype.tag = function (value, weight, handler) {
        return $('<a href="/search?q=' + value + '" class="tagcloud_item"/>').text(value).addClass('tagcloud_size_' + weight);
    };

    AjaxSolr.theme.prototype.facet_link = function (value, handler) {
        return $('<a href="#"/>').text(value).click(handler);
    };

    AjaxSolr.theme.prototype.no_items_found = function () {
        return 'no items found in current selection';
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
        return "agents"
    } else if (doc.type && (doc.type.toLowerCase() == "program" || doc.type.toLowerCase() == "project")) {
        return "activities"
    } else {
        return "services"
    }
}

function getEntityType(doc) {
    if (doc.type && (doc.type.toLowerCase() == "collection" || doc.type.toLowerCase() == "dataset")) {
        return "Collection"
    } else if (doc.type && (doc.type.toLowerCase() == "person" || doc.type.toLowerCase() == "group")) {
        return "Agent"
    } else if (doc.type && (doc.type.toLowerCase() == "program" || doc.type.toLowerCase() == "project")) {
        return "Activity"
    } else {
        return "Service"
    }
}
