var FieldOfResearchParser = function() {
	
	var schemeRegex = /^http\:\/\/purl.org\/asc\/[\d\.]+\/\d+\/for\//;
	
	var descriptions = {};
	
	var labelIndex = {};
	var typeIndex = {};	
	
	var resolverFunction = function(field) {
		return function() {
			var resolvedObjs = [];
			$.each(this[field],function(i,n) {
				var obj = descriptions[n];
				if (obj != null)
					resolvedObjs.push(obj);
			});
			return resolvedObjs;
		}
	}
	
	var createIndexEntry = function(index,key,value) {
		if (index[key] == null) {
			index[key] = [value];
		} else {
			index[key].push(value);
		}
	};
	
	var getIndexKeys = function(index) {
		var keys = [];
		for (var key in index) {
			keys.push(key);
		}
		return keys.sort();
	};
	
	var getByIndex = function(index,key) {
		if (key instanceof RegExp) {
			var matches = [];
			var matchingKeys = [];
			for (k in index) {
				if (k.match(key))
					matchingKeys.push(k);
			}
			for (var i=0; i < matchingKeys.length; i++) {
				k = matchingKeys[i];
				matches = matches.concat(index[k]);
			}
			return matches;
		} else {
			return index[key] || [];
		}
	};
	
	this.loadRdf = function(rdfText) {
		var xml = $.parseXML(rdfText);
		
		// Note: we have jquery.xmlns.js loaded to help us here
		var descriptionXml = $('*|Description', xml);

		descriptionXml.each(function(i,n) {
			var e = $(n);
			var scheme = e.find('inScheme').attr('rdf\:resource');
			if (typeof(scheme) == 'undefined' || !scheme.match(schemeRegex))
				return;
			var obj = {};
			obj.about = e.attr('rdf:about');
			obj.code = e.find('code').text();
			obj.label = e.find('label').text().trim();
			obj.clone = function() { return jQuery.extend({}, this)};
			var getResource = function(i,n) {
				return n.getAttribute('rdf:resource');
			};
			$.each(['broader','narrower'], function(i,v) {
				obj[v+'Ids'] = e.find(v).map(getResource)
										.toArray().sort();
				obj[v] = resolverFunction(v+'Ids');
			});
			$.each(e.find('type'), function(i,v) {
				var resource = getResource(i,v);
				if (!resource.match(schemeRegex))
					return;
				obj.type = resource;
			});
			
			descriptions[obj.about] = obj;
			createIndexEntry(labelIndex, obj.label, obj);
			createIndexEntry(typeIndex, obj.type, obj);
		});
	};
	
	this.getKnownLabels = function() { return getIndexKeys(labelIndex) };
	this.getKnownTypes = function() { return getIndexKeys(typeIndex) };

	this.getByLabel = function(label) { return getByIndex(labelIndex, label) };
	this.getByType = function(type) { return getByIndex(typeIndex, type) };
	
	this.buildTree = function(descriptions) {
		var treeDescriptions = {};
		var rootDescriptions = {};
		for (var i=0; i < descriptions.length; i++) {
			var obj = descriptions[i];
			treeDescriptions[obj.about] = obj.clone();
			var broader = obj;
			while (broader.broader().length != 0) {
				broader = (broader.broader())[0];
				if (typeof(treeDescriptions[broader.about]) == 'undefined')
					treeDescriptions[broader.about] = broader.clone();
			}
			rootDescriptions[broader.about] = treeDescriptions[broader.about];
		}
		for (var i in treeDescriptions) {
			var d = treeDescriptions[i];
			for (var j = 0; j < d.narrowerIds.length; j++) {
				var childId = d.narrowerIds[j];
				if (typeof(treeDescriptions[childId]) != 'undefined') {
					if (typeof(d.children) == 'undefined') {
						d.children = [treeDescriptions[childId]]
					} else {
						d.children.push(treeDescriptions[childId]);
					}
				}
			}
		}
		var results = [];
		for (var i in rootDescriptions) {
			results.push(rootDescriptions[i]);
		}
		return results;
	}
	
	
	
	
};