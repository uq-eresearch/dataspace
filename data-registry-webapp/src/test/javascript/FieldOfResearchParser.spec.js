describe('FieldOfResearchParser', function() {
	
	var getTestRdf = function() {
		var testRdf = null;
		$.ajax({
			url: '../../src/main/webapp/doc/for.rdf',
			async: false,
			dataType: 'text',
			success: function(data, textStatus, jqXHR) {
				testRdf = data;
			},
			error: function(jqXHR, textStatus, errorThrown) {
				throw errorThrown;
			}
		});
		return testRdf;
	}
	
	it('should be instantiable', function() {
		var parser = new FieldOfResearchParser();
		expect(parser).not.toBe(null);
	});
	
	it('should be able to load RDF and get types', function() {
		var parser = new FieldOfResearchParser();
		
		var rdf = getTestRdf();
		expect(rdf).not.toBeNull();
		parser.loadRdf(rdf);
		
		expect(typeof(parser.getKnownTypes)).toBe('function');
		expect(typeof(parser.getByType)).toBe('function');
		
		expect(parser.getKnownTypes()).not.toEqual([]);
		$.each(parser.getKnownTypes(), function(i,v) {
			expect(parser.getByType(v).length).toBeGreaterThan(0);
		});
	});	
	
	it('should be able to search labels', function() {
		var parser = new FieldOfResearchParser();

		var rdf = getTestRdf();
		expect(rdf).not.toBeNull();
		parser.loadRdf(rdf);
		
		expect(typeof(parser.getByLabel)).toBe('function');
		var results = parser.getByLabel(/water/i);
		expect(results.length).toBe(6);
		
		window.forParser = parser;
	});	
	
	it('should be able to build trees', function() {
		var parser = new FieldOfResearchParser();

		var rdf = getTestRdf();
		expect(rdf).not.toBeNull();
		parser.loadRdf(rdf);
		
		expect(typeof(parser.getByLabel)).toBe('function');
		var results = parser.getByLabel(/economics/i);
		expect(results.length).toBeGreaterThan(0);
		console.debug(results);
		
		var tree = parser.buildTree(results);
		console.debug(tree);
		
		var searchTree = function(tree, label) {
			if (tree == undefined)
				return false;
			var type = null;
			for (var i = 0; i < tree.length; i++) {
				var element = tree[i];
				if (type == null) {
					type = element.type;
				} else {
					expect(element.type).toEqual(type);
				}
				if (element.label == label)
					return true;
				if (searchTree(element.children,label))
					return true;
			}
			return false;
		};
		
		
		for (var i = 0; i < results.length; i++) {
			expect(searchTree(tree, results[i].label)).toBeTruthy();
		}
		
		
		window.forParser = parser;
	});	
	
});


