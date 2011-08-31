describe("MapEditor", function() {

	it('should be a prototype/class object', function() {
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
	});


});