describe("DataSpace", function() {
	
	function MockRecord() {
		this.arg = null;
		this.append = function(arg) {
			this.arg = arg;
		};
	}
	
	it('should be a singleton object', function() {
		expect(typeof(DataSpace)).toBe("object");
	
	});
	
	it('exposes prepareFields function', function() {
		expect(typeof(DataSpace.prepareFields)).toBe("function");
	});
	
	it('should set the publish flag correctly', function() {
		$.each([[true,"no"],[false, "yes"]], function(i,testArgs) {
			var record = new MockRecord;
			DataSpace.setPublished(record, testArgs[0]);
			// Should append an jQuery instance
			expect(record.arg instanceof jQuery).toBe(true);
			// Expect a single element
			expect(record.arg.length).toBe(1);
			expect(record.arg[0] instanceof Element).toBe(true);
			var element = record.arg[0];
			// Expect:
			// <app:control>
			//   <app:draft>{testArgs[1]}</app:draft>
			// </app:control>
			expect(element.nodeName).toEqual("app:control");
			expect(element.childNodes.length).toEqual(1);
			expect(element.firstChild.nodeName).toEqual("app:draft");
			// Expect text
			expect(element.firstChild.firstChild.data).toEqual(testArgs[1]);
		});
	});
	
	
	it('should provide a valid new Collection', function() {
		var collection = DataSpace.getCollectionAtom(true,false);
		// TODO: Add some checks
		//console.debug(collection);
	});
	
});