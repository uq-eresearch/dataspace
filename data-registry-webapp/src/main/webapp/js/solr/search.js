var Manager;
$(document).ready(function() {
    Manager = new AjaxSolr.Manager({
                //TODO change this
//                solrUrl: 'http://evolvingweb.ca/solr/reuters/'
                solrUrl: '/solr/'
            });

    Manager.addWidget(new AjaxSolr.PagerWidget({
                id: 'pager',
                target: '#pager',
                prevLabel: '&lt;',
                nextLabel: '&gt;',
                innerWindow: 1,
                renderHeader: function (perPage, offset, total) {
                    $('#pager-header').html($('<span/>').text('displaying ' + Math.min(total, offset + 1) + ' to ' + Math.min(total, offset + perPage) + ' of ' + total));
                }
            }));

//            Manager.addWidget(new AjaxSolr.AutocompleteWidget({
//                id: 'text',
//                target: '#searching',
//                field: 'allText',
//                fields: ['topics', 'organisations', 'exchanges' ]
//            }));

    Manager.init();

    var params = {
        facet: true,
        'facet.field': [ 'title', 'description' ],
        'qt':'standard',
//                'wt':'standard',
        'facet.limit': 20,
        'facet.mincount': 1,
        'f.topics.facet.limit': 50,
        'f.countryCodes.facet.limit': -1,
        'facet.date.start': '1987-02-26T00:00:00.000Z/DAY',
        'facet.date.end': '1987-10-20T00:00:00.000Z/DAY+1DAY',
        'facet.date.gap': '+1DAY',
        'json.nl': 'map'
    };
    for (var name in params) {
        Manager.store.addByValue(name, params[name]);
    }
});
$.fn.showIf = function (condition) {
    if (condition) {
        return this.show();
    }
    else {
        return this.hide();
    }
};

function doSearch(term) {
    Manager.addWidget(new AjaxSolr.ResultWidget({
                id: 'result',
                target: '#docs'
            }));
    Manager.store.addByValue('q', term);
    Manager.doRequest();
}

function doEntityLookup(type, term) {
    Manager.addWidget(new AjaxSolr.ResultWidget({
                id: 'result',
                target: '#docs',
                afterRequest: function () {
                    $(this.target).empty();
                    for (var i = 0, l = this.manager.response.response.docs.length; i < l; i++) {
                        var doc = this.manager.response.response.docs[i];
                        $(this.target).append(AjaxSolr.theme(type + 'Snippet', doc));
                    }
                }
            }));
    Manager.store.addByValue('q', type + ':' + term);
    Manager.doRequest();
}

function lookup(type, term) {
    var entity = 'text';
    if (type == 'subject') {
        entity = 'subject';
    } else if (type == 'isparticipantin') {
        entity = 'activity';
    } else if (type == 'isoutputof') {
        entity = 'activity';
    } else if (type == 'creator') {
        entity = 'agent';
    } else if (type == 'publisher') {
        entity = 'agent';
    } else if (type == 'hasparticipant') {
        entity = 'agent';
    } else if (type == 'iscollectorof') {
        entity = 'collection';
    } else if (type == 'hasoutput') {
        entity = 'collection';
    } else if (type == 'relation') {
        entity = 'collection';
    } else if (type == 'issupportedby') {
        entity = 'collection';
    } else if (type == 'isaccessedvia') {
        entity = 'service';
    }
    doEntityLookup(entity, term);
}

function selectItemsFromLookup(type) {
    $('p:subject').each();
}