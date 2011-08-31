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

	describe('public functions', function() {
		it('exposes prepareFields function', function() {
			expect(typeof(DataSpace.prepareFields)).toBe("function");
		});
		it('exposes ingestRecord function', function() {
			expect(typeof(DataSpace.ingestRecord)).toBe("function");
		});
		it('exposes replicateSimpleField function', function() {
			expect(typeof(DataSpace.replicateSimpleField)).toBe("function");
		});
		it('exposes addKeyword function', function() {
			expect(typeof(DataSpace.addKeyword)).toBe("function");
		});
		it('exposes createLookupDialog function', function() {
			expect(typeof(DataSpace.createLookupDialog)).toBe("function");
		});
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

	it('should provide new entry IDs for this host', function() {
		expect(DataSpace.getNewEntryId('collection')).toContain(
				window.location.protocol + "//" + window.location.host
		);
	});

	var runNewEntryChecks = function(entry, type) {
		// New IDs should contain "ignore" by default
		var newId = $(entry).find('id:not(source id)').text();
		expect(newId).toContain('ignore');
		// Updated should be current
		var newUpdated = $(entry).find('updated').text();
		var now = new Date();
		expect(newUpdated).toMatch('^'+now.getUTCFullYear());

		var newType = $(entry).find('link[rel="http://www.w3.org/1999/02/22-rdf-syntax-ns#type"]');
		expect(newType.attr('href')).toMatch('^http://.+');
		expect(newType.attr('title')).toMatch('[A-Z][a-z]+');

		// TODO: Add some more checks
		//console.debug(entry);
	}

	it('should provide a valid new Activity', function() {
		var newActivity = DataSpace.getActivityAtom(true,false);
		var newActivityId = $(newActivity).find('id:not(source id)').text();
		runNewEntryChecks(newActivity,'activity');
		// TODO: Add some more checks
	});

	it('should provide a valid new Agent', function() {
		var newAgent = DataSpace.getAgentAtom(true,false);
		runNewEntryChecks(newAgent,'agent');
		// TODO: Add some more checks
	});

	it('should provide a valid new Collection', function() {
		var newCollection = DataSpace.getCollectionAtom(true,false);
		var newCollectionId = $(newCollection).find('id:not(source id)').text();
		runNewEntryChecks(newCollection,'collection');
		// TODO: Add some more checks
	});

	it('should provide a valid new Service', function() {
		var newService = DataSpace.getServiceAtom(true,false);
		var newServiceId = $(newService).find('id:not(source id)').text();
		runNewEntryChecks(newService,'service');
		// TODO: Add some more checks
	});


});