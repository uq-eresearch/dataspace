describe('SkosParser', function() {
	
	var getTestRdf = function(callback) {
		$.ajax({
			url: '../../src/test/javascript/SkosParser_test.rdf',
			dataType: 'text',
			success: function(data, textStatus, jqXHR) {
				callback(data);
			},
			error: function(jqXHR, textStatus, errorThrown) {
				throw errorThrown;
			}
		});
	}
	
	it('should be instantiable', function() {
		var parser = new SkosParser();
		expect(parser).not.toBe(null);
	});
	
	it('should be run from a browser with CORS', function() {
		$.ajax({
			url: 'http://ipv4.0-9.fi/',
			dataType: 'text',
			success: function(data, textStatus, jqXHR) {
				expect(data).toMatch('^\d+\.\d+\.\d+\.\d+$');
			},
			error: function(jqXHR, textStatus, errorThrown) {
				throw errorThrown;
			}
		});
	});
	
	it('should be able to load RDF', function() {
		var parser = new SkosParser();
		getTestRdf(function(rdf) {
			parser.loadRdf(rdf);
			expect(parser.getKnownTypes().length).toBeGreaterThan(0);
			$.each(parser.getKnownTypes(), function(i,v) {
				console.debug(v);
				console.debug(parser.getByType(v).length);
				expect(parser.getByType(v).length).toBeGreaterThan(0);
			});
		})
		window.skosParser = parser;
	});	
	
});


