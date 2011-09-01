describe("MapEditor", function() {

	it('is a prototype/class object', function() {
		expect(typeof(MapEditor)).toBe("function");
	});

	describe('public functions', function() {
		var mapEditor = new MapEditor($('<div/>'));

		it('exposes init function', function() {
			expect(typeof(mapEditor.init)).toBe("function");
		});
		it('exposes toggleControl function', function() {
			expect(typeof(mapEditor.toggleControl)).toBe("function");
		});
		it('exposes loadData function', function() {
			expect(typeof(mapEditor.loadData)).toBe("function");
		});
	});

	it('can import and export feature XML', function() {
		var testDiv = $('<div/>');
		// OpenLayers doesn't work on elements unattached to the DOM.
		$('body').append(testDiv);
		var mapEditor = new MapEditor(testDiv);
		mapEditor.init();
		// Import
		var initialData = '<georss:polygon xmlns="http://www.w3.org/2005/Atom" xmlns:georss="http://www.georss.org/georss" xmlns:rdfa="http://www.w3.org/ns/rdfa#">-10.5 140 -17 140 -17 145.5 -10.5 145.5 -10.5 140</georss:polygon>';
		var feature = mapEditor.loadData(initialData);
		expect(feature.geometry).not.toBeNull();
		// Export
		var element = mapEditor.exportData();
		expect(element.nodeName).toEqual('POLYGON');
		// Remove test div
		testDiv.remove();
	});


});