var FieldOfResearchParser = function() {
	
	var descriptions = {};
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
	
	
	this.loadRdf = function(rdfText) {
		var xml = $.parseXML(rdfText);
		
		var descriptionXml = $('Description', xml);
		expect(descriptionXml.size()).toBeGreaterThan(0);
		
		//console.debug(descriptionXml.get(100));
		descriptionXml.each(function(i,n) {
			var e = $(n);
			var obj = {};
			obj.about = e.attr('rdf:about');
			obj.code = e.find('code').text();
			obj.label = e.find('label').text();
			var getResource = function(i,n) {
				return n.getAttribute('rdf:resource');
			};
			$.each(['broader','narrower','type'], function(i,v) {
				obj[v+'Resources'] = e.find(v).map(getResource)
										.toArray().sort();
				obj[v] = resolverFunction(v+'Resources');
			});
			$.each(obj.typeResources, function(i,v) {
				if (typeIndex[v] == null) {
					typeIndex[v] = [obj];
				} else {
					typeIndex[v].push(obj);
				}
			});
			descriptions[obj.about] = obj;
		});
	};
	
	this.getKnownTypes = function() {
		var types = [];
		for (var type in typeIndex) {
			types.push(type);
		}
		return types;
	};
	
	this.getByType = function(type) {
		return typeIndex[type] || [];
	};
	
};