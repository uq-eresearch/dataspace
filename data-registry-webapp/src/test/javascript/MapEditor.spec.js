describe("MapEditor", function() {
	var mapEditor, testDiv;

	beforeEach(function () {
		testDiv = $('<div style="height: 300px; width:300px"/>');
		// OpenLayers doesn't work on elements unattached to the DOM.
		$('body').append(testDiv);
		mapEditor = new MapEditor(testDiv);
	});

	it('is a prototype/class object', function() {
		expect(typeof(MapEditor)).toBe("function");
	});

	describe('public functions', function() {

		it('exposes makeEditable function', function() {
			expect(typeof(mapEditor.makeEditable)).toBe("function");
		});
		it('exposes toggleControl function', function() {
			expect(typeof(mapEditor.toggleControl)).toBe("function");
		});
		it('exposes loadData function', function() {
			expect(typeof(mapEditor.loadData)).toBe("function");
		});
		it('exposes exportData function', function() {
			expect(typeof(mapEditor.exportData)).toBe("function");
		});
		it('exposes clearData function', function() {
			expect(typeof(mapEditor.clearData)).toBe("function");
		});
		it('exposes getViewingCircle function', function() {
			expect(typeof(mapEditor.getViewingCircle)).toBe("function");
		});
	});

	it('can import and export features', function() {

		// Import
		runs(function() {
			var initialData = '<georss:polygon xmlns="http://www.w3.org/2005/Atom" xmlns:georss="http://www.georss.org/georss" xmlns:rdfa="http://www.w3.org/ns/rdfa#">-10.5 140 -17 140 -17 145.5 -10.5 145.5 -10.5 140</georss:polygon>';
			var feature = mapEditor.loadData(initialData);
			expect(feature.geometry).not.toBeNull();
		});

		// Export
		runs(function() {
			var element = mapEditor.exportData();
			expect(element).not.toBe(null);
			expect(element.nodeName).toEqual('georss:polygon');
		});

		// Clear
		runs(function() {
			mapEditor.clearData();
			var element = mapEditor.exportData();
			expect(element).toBe(null);
		});

	});

	it('provides a viewing circle with lat/long/radius', function() {
		var eventFired = false;
		// Import
		runs(function() {
			var initialData = '<georss:polygon xmlns="http://www.w3.org/2005/Atom" xmlns:georss="http://www.georss.org/georss" xmlns:rdfa="http://www.w3.org/ns/rdfa#">-10.5 140 -17 140 -17 145.5 -10.5 145.5 -10.5 140</georss:polygon>';
			var feature = mapEditor.loadData(initialData);
			expect(feature.geometry).not.toBeNull();
		});
		runs(function() {
			var viewingCircle = mapEditor.getViewingCircle();
			expect(viewingCircle).not.toBeNull();
			expect(viewingCircle.lat).toBe(-24.25231);
			expect(viewingCircle.lon).toBe(130.32129);
			expect(viewingCircle.radius).toBe(2935.1818856249997);
		});
	});

	afterEach(function() {
		// Remove test div
		testDiv.remove();
	});
});