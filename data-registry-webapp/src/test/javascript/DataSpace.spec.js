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
		it('exposes deleteRecord function', function() {
			expect(typeof(DataSpace.deleteRecord)).toBe("function");
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

    var runTemporalEncodingChecks = function(entry) {
        var newPeriod = $(entry).find('meta[property="http://purl.org/dc/terms/temporal"]').attr('content');
        expect(newPeriod).toMatch('.*start=[0-9]+;.*');
        expect(newPeriod).toMatch('.*end=[0-9]+;.*');
    }

	it('should provide a valid new Activity', function() {
        $('body').append($('<select id="type-combobox"><option value="Project" selected/></select>'));
		var newActivity = DataSpace.getActivityAtom(true,false);
		var newActivityId = $(newActivity).find('id:not(source id)').text();
        $('#type-combobox').detach();
		runNewEntryChecks(newActivity,'activity');
		// TODO: Add some more checks

	});

	it('should provide a valid new Agent', function() {
        $('body').append($('<select id="type-combobox"><option value="Person" selected/></select>'));
		var newAgent = DataSpace.getAgentAtom(true,false);
		runNewEntryChecks(newAgent,'agent');
		// TODO: Add some more checks
        $('#type-combobox').detach();
	});

	it('should provide a valid new Collection', function() {
        $('body').append($('<select id="type-combobox"><option value="Dataset" selected/></select>'));
        $('body').append($('<input name="start-date" id="start-date" value="2000">'));
        $('body').append($('<input name="end-date" id="end-date" value="2010">'));
		var newCollection = DataSpace.getCollectionAtom(true,false);
        $('#type-combobox').detach();
        $('#start-date').detach();
        $('#end-date').detach();
		var newCollectionId = $(newCollection).find('id:not(source id)').text();
		runNewEntryChecks(newCollection,'collection');
        runTemporalEncodingChecks(newCollection);

		// TODO: Add some more checks
        ;
	});

	it('should provide a valid new Service', function() {
        $('body').append($('<select id="type-combobox"><option value="Create" selected/></select>'));
        var newService = DataSpace.getServiceAtom(true,false);
		var newServiceId = $(newService).find('id:not(source id)').text();
        $('#type-combobox').detach();
		runNewEntryChecks(newService,'service');
		// TODO: Add some more checks
	});


});