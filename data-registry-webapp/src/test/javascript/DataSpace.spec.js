describe("DataSpace", function() {
	
	it('should be a singleton object', function() {
		expect(typeof(DataSpace)).toBe("object");
	
	});
	
	it('exposes prepareFields function', function() {
		expect(typeof(DataSpace.prepareFields)).toBe("function");
	});
	
});