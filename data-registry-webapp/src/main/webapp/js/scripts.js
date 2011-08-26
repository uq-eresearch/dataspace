/* Avoid errors due to debugging left in */
if (typeof (window.console) == 'undefined') {
	window.console = function() {
		var noop = function() {
		};
		this.debug = noop;
	};
}

var DataSpace = (function() {
	var instance = {};

	/**
	 * Constants
	 */
	var UQ_REGISTRY_URI_PREFIX = window.location.protocol + '//'
			+ window.location.host + '/';
	var PERSISTENT_URL = "http://purl.org/";
	var NS_FOAF = "http://xmlns.com/foaf/0.1/";
	var NS_ANDS = "http://www.ands.org.au/ontologies/ns/0.1/VITRO-ANDS.owl#";
	var NS_DC = PERSISTENT_URL + "dc/terms/";
	var NS_DCMITYPE = PERSISTENT_URL + "dc/dcmitype/";
	var NS_CLD = PERSISTENT_URL + "cld/terms/";
	var NS_ANZSRC = PERSISTENT_URL + "asc/1297.0/2008/";
	var NS_VIVO = "http://vivoweb.org/ontology/core#";
	var NS_ORE = "http://www.openarchives.org/ore/terms/";
	var NS_GEORSS = "http://www.georss.org/georss/";
	var NS_RDFA = "http://www.w3.org/ns/rdfa#";
	var NS_EFS = "http://www.e-framework.org/Contributions/ServiceGenres/";
	var NS_ATOM = "http://www.w3.org/2005/Atom";
	var NS_APP = "http://www.w3.org/2007/app";
	var REL_ACCESS_RIGHTS = NS_DC + "accessRights";
	var REL_ALTERNATE = "alternate";
	var REL_CREATOR = NS_DC + "creator";
	var REL_DESCRIBES = NS_ORE + "describes";
	var REL_HAS_PARTICIPANT = NS_ANDS + "hasParticipant";
	var REL_HAS_OUTPUT = NS_ANDS + "hasOutput";
	var REL_IS_MANAGER_OF = NS_ANDS + "isManagerOf";
	var REL_IS_ACCESSED_VIA = NS_CLD + "isAccessedVia";
	var REL_MADE = NS_FOAF + "made";
	var REL_MBOX = NS_FOAF + "mbox";
	var REL_IS_DESCRIBED_BY = NS_ORE + "isDescribedBy";
	var REL_IS_LOCATED_AT = NS_CLD + "isLocatedAt";
	var REL_IS_OUTPUT_OF = NS_ANDS + "isOutputOf";
	var REL_IS_REFERENCED_BY = NS_DC + "isReferencedBy";
	var REL_CURRENT_PROJECT = NS_FOAF + "currentProject";
	var REL_IS_SUPPORTED_BY = NS_ANDS + "isSupportedBy";
	var REL_LATEST_VERSION = "latest-version";
	var REL_PAGE = NS_FOAF + "page";
	var REL_PREDECESSOR_VERSION = "predecessor-version";
	var REL_PUBLISHER = NS_DC + "publisher";
	var REL_RELATED = "related";
	var REL_SELF = "self";
	var REL_LICENSE = "license";
	var REL_SUCCESSOR_VERSION = "successor-version";
	var REL_TEMPORAL = NS_DC + "temporal";
	var REL_TYPE = 'http://www.w3.org/1999/02/22-rdf-syntax-ns#type';
	var REL_ALTERNATIVE = NS_DC + "alternative";
	var REL_SPATIAL = NS_DC + "spatial";
	var REL_VIA = "via";
	var TERM_ANDS_GROUP = "The University of Queensland";
	var TERM_ACTIVITY = NS_FOAF + "Project";
	var TERM_COLLECTION = NS_DCMITYPE + "Collection";
	var TERM_AGENT_AS_GROUP = NS_FOAF + "Group";
	var TERM_AGENT_AS_AGENT = NS_FOAF + "Agent";
	var TERM_SERVICE = NS_VIVO + "Service";
	var LABEL_KEYWORD = "keyword";
	var SCHEME_DCMITYPE = NS_DCMITYPE;
	var SCHEME_KEYWORD = UQ_REGISTRY_URI_PREFIX + "keyword";
	var SCHEME_ANZSRC_FOR = NS_ANZSRC + "for";
	var SCHEME_ANZSRC_SEO = NS_ANZSRC + "seo";
	var SCHEME_ANZSRC_TOA = NS_ANZSRC + "toa";
	var PROPERTY_TITLE = NS_FOAF + "title";
	var PROPERTY_GIVEN_NAME = NS_FOAF + "givenName";
	var PROPERTY_FAMILY_NAME = NS_FOAF + "familyName";

	var prepareFields = function() {
		$("#edit-tabs").tabs();
		$('.date-picker').datepicker();
		$('#search-entry').focus(function() {
			if (this.value == this.defaultValue) {
				this.value = '';
			}
			if (this.value != this.defaultValue) {
				this.select();
			}
		});
		$('#search-entry').blur(function() {
			if ($.trim(this.value) == '') {
				this.value = (this.defaultValue || '');
			}
		});
		// Page form validates, but never submits itself
		$('#page-form').validate({
			submitHandler : function(form) {
				return false;
			}
		});
	};

	var ingestRecord = function(url, type, isNew, isPublished) {
		// Trigger validation (which shouldn't trigger a submit
		$('#page-form').submit();
		// Abort if invalid
		if (!$('#page-form').valid()) {
			// TODO: Notify user of errors
			return false;
		}
		var actionText = isNew ? 'create' : 'edit';
		if (confirm("Are you sure you want to " + actionText + " this record?")) {
			var record = null;
			if (type == 'activity') {
				record = getActivityAtom(isNew, isPublished);
			} else if (type == 'agent') {
				record = getAgentAtom(isNew, isPublished);
			} else if (type == 'collection') {
				record = getCollectionAtom(isNew, isPublished);
			} else if (type == 'service') {
				record = getServiceAtom(isNew, isPublished);
			}
			var method = 'PUT';
			if (isNew) {
				method = 'POST';
			}
			$.ajax({
				type : method,
				url : url,
				data : serializeToString(record.context),
				contentType : "application/atom+xml",
				processData : false,
				dataType : 'xml',
				success : function(data, textStatus, XMLHttpRequest) {
					var loc = XMLHttpRequest.getResponseHeader('Location');
					window.location.href = loc;
				},
				error : function(XMLHttpRequest, textStatus, errorThrown) {
					$('#ingest-error-msg').html(
							'<span style="color:red;">' + textStatus
									+ '</span>');
				}
			});
		}
		return false;
	};

	var deleteRecord = function(url) {
		if (confirm("Are you sure you want to delete this record?")) {
			$.ajax({
				type : 'DELETE',
				url : url,
				success : function(data) {
					location.reload();
				},
				error : function(xhr, textStatus, errorThrown) {
					alert("Could not deleted record");
				}
			});
		}
		return false;
	};

	var getRemoveLink = function(clickHandler) {
		var link = $(document.createElement('a'));
		return link.attr('href', '#').attr('class', 'remove-link').text('x')
				.click(clickHandler);
	};

	var replicateSimpleField = function(inputField) {
		var parentWrapper = $('#' + inputField).parent();
		var wrapperType = parentWrapper.get(0).nodeName;
		var newWrapper = $(document.createElement(wrapperType));
		var numberOfFields = parentWrapper.parent().find(wrapperType).length;
		var newInputField = $('#' + inputField).clone(true);
		var newFieldId = inputField + '-' + numberOfFields.toString();
		newInputField.attr('id', newFieldId);
		newWrapper.append(newInputField);
		parentWrapper.siblings().last().before(newWrapper);
		newInputField.val("");
		newWrapper.append(getRemoveLink(function() {
			$(this).parent().remove();
			return false;
		}));
		return false;
	};

	var insertPublicationFields = function(addLink) {
		var wrapper = $('<dd/>');
		var publicationTitleInput = $('<input type="text"/>').attr('name',
				'publication-title').attr('title', 'Publication Title').attr(
				'minlength', 2).addClass('required').addClass('defaultInvalid');
		wrapper.append(publicationTitleInput);
		var publicationUrlInput = $('<input type="text"/>').attr('name',
				'publication-url').attr('title', 'Publication URL').addClass(
				'required').addClass('defaultInvalid').addClass('url').val(
				'http://');
		wrapper.append(publicationUrlInput);
		wrapper.append(getRemoveLink(function() {
			$(this).parent().remove();
			return false;
		}));
		addLink.parent().before(wrapper);
		prepareFields();
		return false;
	};

	var addKeyword = function(inputFieldId) {
		var inputField = $('#' + inputFieldId);
		var keyword = inputField.val();
		if (keyword) {
			var keywordText = _.template(
					'<dd>'
					+ '<span class="keyword"><%=keyword%></span>'
					+ '<a class="remove-keyword"'
					+ ' href="#" title="Remove Keyword"'
					+ ' onclick="$(this).parent().remove(); return false;">x</a>'
					+ '</dd>', {
				keyword : keyword
			});
			inputField.parent()
					.before(keywordText);
			inputField.val('');
			styleTables();
		}
	};

	var showLookupDialog = function(type) {
		$('#lookup-type').val(type);
		$('#lookup-div')
				.dialog(
						{
							modal : true,
							open : function() {
								$('#query').val('');
								$('#docs').html('');
								$('#pager').html('');
								$('#pager-header').html('');
								$(this).css('display', '');

								var selectHandler = function() {
									var objs = $('#docs input:checked').map(
											function(i, n) {
												var obj = $(n).parent().find(
														'a');
												return {
													title : obj.text(),
													uri : obj.attr('href')
												};
											});
									var elements = $(objs)
											.map(
													function(i, obj) {
														var wrapper = $('<dd/>');
														var element = $('<a/>');
														element
																.attr(
																		'class',
																		type
																				+ '-value');
														element.attr('href',
																obj.uri);
														element.text(obj.title);
														element
																.click(function() {
																	window
																			.open(
																					element
																							.attr('href'),
																					'_blank');
																	return false;
																});
														wrapper.append(element);
														var removeLink = getRemoveLink(function() {
															$(this).parent()
																	.remove();
															return false;
														});
														wrapper
																.append(removeLink);
														return wrapper;
													});
									elements.each(function(i, n) {
										$('#add-' + type + '-link').parent()
												.before(n);
									});
									$('#lookup-div').dialog('close');
								};
								$('#lookup-select').unbind('click',
										selectHandler).bind('click',
										selectHandler);
							},
							height : 400,
							width : 600,
							title : 'Lookup'
						});
	};

	var getTree = function(searchTerm, parser) {
		var results = parser.getByLabel(searchTerm);
		var tree = parser.buildTree(results);

		var titleTemplate = _.template('<span class="result">'
				+ '<input type="hidden" name="result" value="<%=about%>"/>'
				+ '<%=code%> - <%=label%>' + '</span>');

		var mapFunc = function(i, n) {
			var hasChildren = (n.children && n.children.length > 0);
			return {
				title : titleTemplate(n),
				data : {
					code : n.code,
					jstree : {
						opened : false
					/* hasChildren */}
				},
				children : hasChildren ? $(n.children).map(mapFunc).toArray()
						: null
			};
		};

		return $(tree).map(mapFunc).toArray();

	};

	var createAnzsrcoLookup = function(field) {
		var target = $('#'+field);
		var lookupDialog = target.find('.dialog-window');
		var parser = target.prop('anzsrcoParser');
		var newLink = target.find('.new-link');
		var insertBefore = newLink.parent();

		var openHandler = function() {
			var queryField = lookupDialog.find(':input[name="query"]');
			var resultDisplay = lookupDialog.find('.results');
			var searchButton = lookupDialog
					.find(':button[name="search"]');
			var selectButton = lookupDialog
					.find(':button[name="select"]');

			// Initially load everything
			getAnzsrcoSelector(resultDisplay, getTree(/.*/,
					parser));

			var searchHandler = function() {
				// Case-insensitive regex
				var query = new RegExp(queryField.val(), 'i');
				var results = getTree(query, parser);
				getFieldOfResearchSelector(resultDisplay, results);
			};
			searchButton.unbind('click.lookup').bind(
					'click.lookup', searchHandler);

			var selectHandler = function() {
				var selected = lookupDialog.find('.jstree')
						.jstree('get_selected');
				var objs = selected.map(function(i, n) {
					var obj = $(n).find('.result');
					return {
						title : obj.text(),
						uri : obj.find('input').val()
					};
				});
				var elements = $(objs).map(
					function(i, obj) {
						var wrapper = $('<dd/>');
						var element = $('<a/>');
						element.attr('class', 'field-value');
						element.attr('href', obj.uri);
						element.text(obj.title);
						element.click(function() {
							window.open(element
									.attr('href'),
									'_blank');
							return false;
						});
						wrapper.append(element);
						var removeLink = getRemoveLink(function() {
							$(this).parent().remove();
							return false;
						});
						wrapper.append(removeLink);
						return wrapper;
					});
				elements.each(function(i, n) {
					insertBefore.before(n);
				});
				lookupDialog.dialog('close');
			};
			selectButton.unbind('click.lookup').bind(
					'click.lookup', selectHandler);
		};

		lookupDialog.dialog({
			autoOpen: false,
			modal : true,
			open : openHandler,
			height : 400,
			width : 600,
			title : 'Lookup'
		});

		newLink.click(function() {
			lookupDialog.dialog('open');
			return false;
		});

	};

	var getAnzsrcoSelector = function(targetElement, tree) {
		targetElement = $(targetElement);
		targetElement.children().remove();

		var expandAllLink = $('<a href="#">Expand All</a>');
		var closeAllLink = $('<a href="#">Close All</a>');
		targetElement.append(expandAllLink, closeAllLink);

		var treeDiv = $('<div style="height: 100%; width: 100%;"/>');
		treeDiv.css('overflow', 'scroll').css('height', '250px').css('width',
				'550px');
		targetElement.append(treeDiv);
		treeDiv.jstree({
			json : {
				data : tree,
				progressive_render : true
			},
			themes : {
				theme : 'apple'
			},
			plugins : [ 'core', 'themes', 'json', 'ui' ]
		});

		expandAllLink.click(function() {
			treeDiv.jstree('open_all');
			return false;
		});
		closeAllLink.click(function() {
			treeDiv.jstree('close_all');
			return false;
		});

		return treeDiv;
	};

	var styleTables = function() {
		$(".edit-table > tr:even").css("background-color", "#F4F4F8");
		$(".edit-table > tr:odd").css("background-color", "#d4d4d4");
		$(".lookup-table > tbody > tr:even").css("background-color", "#F4F4F8");
		$(".lookup-table > tbody > tr:odd").css("background-color", "#d4d4d4");
	};

	var setRecordType = function() {
		var hiddenField = $("#record-type");
		if (hiddenField) {
			var recordType = hiddenField.val();
			$('#type-combobox').val(recordType);
		}
	};

	var setLicenseType = function() {
		var hiddenField = $("#licence-type");
		if (hiddenField) {
			var licenceType = hiddenField.val();
			$('#licence-type-combobox').val(licenceType);
		}
	};

	/**
	 *
	 *
	 * Functional javascript
	 *
	 */

	var getActivityAtom = function(isNew, isPublished) {
		var record = getAtomEntryElement();

		// id
		var id = getSimpleElementWithText('id', getNewEntryId('activities'));
		if (!isNew) {
			id = getSimpleElementWithText('id', $('#page-text').val());
		}
		record.append(id);

		// type
		var typeRelation = getLinkElement('http://xmlns.com/foaf/0.1/Project',
				REL_TYPE, 'Project');
		record.append(typeRelation);
		var recordType = $('#type-combobox').val();
		if (recordType) {
			var typeCategory = getCategoryElement(NS_DCMITYPE, NS_FOAF
					+ recordType, recordType);
			record.append(typeCategory);
		}
		// title
		var titleValue = $('#edit-title-text').val();
		if (titleValue) {
			var title = getSimpleElementWithText('title', titleValue);
			title.attr('type', 'text');
			record.append(title);
		}

		// Alternative titles
		addAlternativeTitles(record);

		var contentValue = $('#content-textarea').val();
		if (contentValue) {
			var content = getSimpleElementWithText('content', contentValue);
			content.attr('type', 'text');
			record.append(content);
		}

		// pages
		addPages(record);

		// add TOA
		addTOA(record);

		// keywords
		addKeywordToCollection(record);

		// updated, format: '2010-10-08T05:58:02.781Z'
		var updated = getSimpleElementWithText('updated', getCurrentDateTime());
		record.append(updated);

		// add source
		addSource(record);

		setPublished(record, isPublished);

		return record;
	};

	var getAgentAtom = function(isNew, isPublished) {
		var record = getAtomEntryElement();

		// id
		var id = getSimpleElementWithText('id', getNewEntryId('agents'));
		if (!isNew) {
			id = getSimpleElementWithText('id', $('#page-text').val());
		}
		record.append(id);

		// type
		var typeRelation = getLinkElement('http://xmlns.com/foaf/0.1/Person',
				REL_TYPE, 'Person');
		record.append(typeRelation);
		var recordType = $('#type-combobox').val();
		if (recordType) {
			var typeCategory = getCategoryElement(NS_DCMITYPE, NS_FOAF
					+ recordType, recordType);
			record.append(typeCategory);
		}
		// title
		var titleValue = $('#edit-title-text').val();
		if (titleValue) {
			var title = getSimpleElementWithText('title', titleValue);
			title.attr('type', 'text');
			record.append(title);
		}

		// Alternative titles
		addAlternativeTitles(record);

		var contentValue = $('#content-textarea').val();
		if (contentValue) {
			var content = getSimpleElementWithText('content', contentValue);
			content.attr('type', 'text');
			record.append(content);
		}

		// Emails
		addEmails(record);

		// pages
		addPages(record);

		// add TOA
		addTOA(record);

		// keywords
		addKeywordToCollection(record);

		// updated, format: '2010-10-08T05:58:02.781Z'
		var updated = getSimpleElementWithText('updated', getCurrentDateTime());
		record.append(updated);

		// add source
		addSource(record);

		setPublished(record, isPublished);

		return record;
	};

	var getCollectionAtom = function(isNew, isPublished) {
		var record = getAtomEntryElement();

		// id
		var id = getSimpleElementWithText('id', getNewEntryId('collections'));
		if (!isNew) {
			id = getSimpleElementWithText('id', $('#page-text').val());
		}
		record.append(id);

		// type
		var typeRelation = getLinkElement(
				'http://purl.org/dc/dcmitype/Collection', REL_TYPE,
				'Collection');
		record.append(typeRelation);
		var recordType = $('#type-combobox').val();
		if (recordType) {
			var typeCategory = getCategoryElement(NS_DCMITYPE, NS_DCMITYPE
					+ recordType, recordType);
			record.append(typeCategory);
		}
		// title
		var titleValue = $('#edit-title-text').val();
		if (titleValue) {
			var title = getSimpleElementWithText('title', titleValue);
			title.attr('type', 'text');
			record.append(title);
		}

		// Alternative titles
		addAlternativeTitles(record);

		var contentValue = $('#content-textarea').val();
		if (contentValue) {
			var content = getSimpleElementWithText('content', contentValue);
			content.attr('type', 'text');
			record.append(content);
		}
		// pages
		addPages(record);

		// add authors
		addAuthors(record);

		// add publishers
		addPublishers(record);

		addOutputOf(record);

		// add FOR & SEO codes
		addFieldOfResearch(record);
		addSocioEconomicImpact(record);

		// add TOA
		addTOA(record);

		// keywords
		addKeywordToCollection(record);

		// temporal
		addTemporal(record);

		// spatial
		addSpatial(record);

		// publications
		addPublications(record);

		// rights
		var rightsValue = $('#rights-textarea').val();
		if (rightsValue) {
			var rights = getSimpleElementWithText('rights', rightsValue);
			record.append(rights);
		}
		// access rights
		addAccessRights(record);

		addLicense(record);

		// updated, format: '2010-10-08T05:58:02.781Z'
		var updated = getSimpleElementWithText('updated', getCurrentDateTime());
		record.append(updated);

		// add source
		addSource(record);

		setPublished(record, isPublished);

		return record;
	};

	var getServiceAtom = function(isNew, isPublished) {
		var record = getAtomEntryElement();

		// id
		var id = getSimpleElementWithText('id', getNewEntryId('services'));
		if (!isNew) {
			id = getSimpleElementWithText('id', $('#page-text').val());
		}
		record.append(id);

		// type
		var typeRelation = getLinkElement(
				'http://www.e-framework.org/Contributions/ServiceGenres/Report',
				REL_TYPE, 'Report');
		record.append(typeRelation);
		var recordType = $('#type-combobox').val();
		if (recordType) {
			var typeCategory = getCategoryElement(NS_DCMITYPE, NS_EFS
					+ recordType, recordType);
			record.append(typeCategory);
		}
		// title
		var titleValue = $('#edit-title-text').val();
		if (titleValue) {
			var title = getSimpleElementWithText('title', titleValue);
			title.attr('type', 'text');
			record.append(title);
		}

		// Alternative titles
		addAlternativeTitles(record);

		var contentValue = $('#content-textarea').val();
		if (contentValue) {
			var content = getSimpleElementWithText('content', contentValue);
			content.attr('type', 'text');
			record.append(content);
		}

		// pages
		addPages(record);

		// updated, format: '2010-10-08T05:58:02.781Z'
		var updated = getSimpleElementWithText('updated', getCurrentDateTime());
		record.append(updated);

		// add source
		addSource(record);

		setPublished(record, isPublished);

		return record;
	};

	/**
	 *
	 * Specific functions
	 *
	 */

	var addAccessRights = function(record) {
		var accessRights = getSimpleElementWithNameSpace('rdfa:meta', NS_RDFA);
		var val = $('#access-rights-textarea').val();
		if (val) {
			var attributes = [ {
				name : 'property',
				value : REL_ACCESS_RIGHTS
			}, {
				name : 'content',
				value : val
			} ];
			_.each(attributes, function(attribute) {
				accessRights.attr(attribute.name, attribute.value);
			});
			record.append(accessRights);
		}
	};

	var addLicense = function(record) {
		var license = $('#licence-type-combobox');
		if ($(license).val() != 'none') {
			var licenseLink = getLinkElement($(license).val(), REL_LICENSE, $(
					license).text());
			licenseLink.attr('type', 'application/rdf+xml');
			record.append(licenseLink);
		}
	};

	var addAlternativeTitles = function(record) {
		$('input[id|="alternative-title-text"]').each(
				function() {
					var content = $.trim($(this).val());
					// Don't add blank alternative values
					if (content == "") {
						return;
					}
					var alternativeTitle = getSimpleElementWithNameSpace(
							'rdfa:meta', NS_RDFA);
					var attributes = [ {
						name : 'property',
						value : REL_ALTERNATIVE
					}, {
						name : 'content',
						value : $(this).val()
					} ];
					_.each(attributes,function(attribute) {
						alternativeTitle.attr(attribute.name, attribute.value);
					});
					record.append(alternativeTitle);
				});
	};

	var addPages = function(record) {
		$('input[id|="page-text"]').each(function() {
			var href = $(this).val();
			if (href) {
				var linkElement = getLinkElement(href, REL_PAGE);
				record.append(linkElement);
			}
		});
	};

	var addEmails = function(record) {
		$('input[id|="email-text"]').each(function() {
			var href = $(this).val();
			if (href) {
				var linkElement = getLinkElement(href, REL_MBOX);
				record.append(linkElement);
			}
		});
	};

	var addAuthors = function(record) {
		// TODO authros needs to be retrieved from the UI
		var author = getAuthorElement('Dr Hamish Campbell',
				'hamish.campbell@uq.edu.au');
		record.append(author);
	};

	var addPublishers = function(record) {
		// TODO publishers needs to be retrieved from the UI
		var publisher = getLinkElement(UQ_REGISTRY_URI_PREFIX + 'agents/1',
				REL_PUBLISHER, 'Abdul Alabri');
		record.append(publisher);
	};

	var addOutputOf = function(record) {
		// TODO publishers needs to be retrieved from the UI
		var activity = getLinkElement(UQ_REGISTRY_URI_PREFIX + 'activities/1',
				REL_IS_OUTPUT_OF);
		record.append(activity);
	};

	var addAccessedVia = function(record) {
		// TODO publishers needs to be retrieved from the UI
		var service = getLinkElement(UQ_REGISTRY_URI_PREFIX + 'services/1',
				REL_IS_ACCESSED_VIA);
		record.append(service);
	};

	var addPublications = function(record) {
		$('input[id|="publication-title"]').each(
				function() {
					var title = $(this).val();
					var href = $(this).parent().parent().find(
							'input[id|="publication-url"]').eq(0).val();
					if (href) {
						var linkElement = getLinkElement(href,
								REL_IS_REFERENCED_BY, title);
						record.append(linkElement);
					}
				});
	};


	var anzsrcoAddHandler = function(field,scheme) {
		return function(record) {
			$('#'+field+' a[class="field-value"]').each(
				function(i, v) {
					var category = getCategoryElement(scheme, v
							.getAttribute('href'), $(v).text());
					record.append(category);
				});
		};
	};

	var addFieldOfResearch = anzsrcoAddHandler(
			'field-of-research', SCHEME_ANZSRC_FOR);

	var addSocioEconomicImpact = anzsrcoAddHandler(
			'socio-economic-impact', SCHEME_ANZSRC_SEO);

	var addTOA = function(record) {
		$('input[name="type-of-activity"]:checked').each(
				function() {
					var category = getCategoryElement(SCHEME_ANZSRC_TOA,
							SCHEME_ANZSRC_TOA + '/#' + $(this).val(), $(this)
									.val());
					record.append(category);
				});
	};

	var addKeywordToCollection = function(record) {
		$('dl[id="keywords-list"] span.keyword').each(function() {
			var keyword = $(this).clone();
			var category = getCategoryElement(null, $(keyword).text(), null);
			record.append(category);
		});
	};

	var addTemporal = function(record) {
		var termporal = getSimpleElementWithNameSpace('rdfa:meta', NS_RDFA);
		var startDate = $('input[id|="start-date"]').val();
		var endDate = $('input[id|="end-date"]').val();
		var attributes = [];
		var content = '';
		if (startDate) {
			content = content + 'start=' + startDate + ' ';
		}
		if (endDate) {
			content = content + 'end=' + endDate;
		}
		if (content != '') {
			attributes[0] = {
				name : 'property',
				value : REL_TEMPORAL
			};
			attributes[1] = {
				name : 'content',
				value : $.trim(content)
			};
			_.each(attributes, function(attribute) {
				termporal.attr(attribute.name, attribute.value);
			});
			record.append(termporal);
		}
	};

	var addSpatial = function(record) {
		$('input[id|="location-name"]').each(
				function() {
					var title = $(this).val();
					var href = 'http://sws.geonames.org/';
					if (title) {
						var linkElement = getLinkElement(href + title,
								REL_SPATIAL, title);
						record.append(linkElement);
					}
				});
	};

	var addSource = function(record) {
		var source = getSimpleElement('source');
		var id = getSimpleElementWithText('id', 'http://dataspace.uq.edu.au');
		source.append(id);
		var title = getSimpleElementWithText('title',
				'The University of Queensland Data Collections Registry');
		title.attr('type', 'text');
		source.append(title);

		var author = getAuthorElement('Abdul Alabri', 'a.alabri@uq.edu.au');
		source.append(author);

		record.append(source);
	};

	var setPublished = function(record, isPublished) {
		var appElement = getSimpleElementWithNameSpace('app:control', NS_APP);
		var draftElement = getSimpleElementWithNameSpace('app:draft', NS_APP);
		if (isPublished) {
			draftElement.text('no');
		} else {
			draftElement.text('yes');
		}
		appElement.append(draftElement);
		record.append(appElement);
	};

	var getNewEntryId = function(type) {
		return UQ_REGISTRY_URI_PREFIX + type + '/ignore';
	};

	var getLinkElement = function(href, rel, title) {
		var attributes = [];
		if (href) {
			attributes.push({
				name : 'href',
				value : href
			});
		}
		if (rel) {
			attributes.push({
				name : 'rel',
				value : rel
			});
		}
		if (title) {
			attributes.push({
				name : 'title',
				value : title
			});
		}
		var link = getElementWithAttributes('link', attributes);
		return link;
	};

	var getAtomEntryElement = function() {
		var attributes = [ {
			name : "xmlns",
			value : NS_ATOM
		}, {
			name : "xmlns:app",
			value : NS_APP
		}, {
			name : "xmlns:georss",
			value : NS_GEORSS
		}, {
			name : "xmlns:rdfa",
			value : NS_RDFA
		} ];
		var entry = getElementWithAttributes("entry", attributes);
		return entry;
	};

	var getCategoryElement = function(scheme, term, label) {
		var attributes = [];
		attributes[0] = {
			name : 'scheme',
			value : scheme
		};
		attributes[1] = {
			name : 'term',
			value : term
		};
		attributes[2] = {
			name : 'label',
			value : label
		};
		var category = getElementWithAttributes('category', attributes);
		return category;
	};

	var getAuthorElement = function(name, email, uri) {
		var author = getSimpleElement('author');
		if (name) {
			var nameElement = getSimpleElement('name');
			nameElement.text(name);
			author.append(nameElement);
		}
		if (email) {
			var emailElement = getSimpleElement('email');
			emailElement.text(email);
			author.append(emailElement);
		}
		if (uri) {
			var uriElement = getSimpleElement('uri');
			uriElement.text(uri);
			author.append(uriElement);
		}
		return author;
	};

	/**
	 *
	 * Generic Functions
	 *
	 */
	var getSimpleElementWithText = function(name, text) {
		var element = getSimpleElement(name);
		element.text(text);
		return element;

	};

	var getElementWithAttributes = function(name, attributes) {
		var element = getSimpleElement(name);
		_.each(attributes, function(attribute) {
			if (attribute.name && attribute.value) {
				element.attr(attribute.name, attribute.value);
			}
		});
		return element;
	};

	var getSimpleElement = function(name) {
		return $(document.createElementNS(NS_ATOM, name));
		// return $('<' + name + '>');
	};

	var getSimpleElementWithNameSpace = function(name, namespace) {
		var element = $(document.createElementNS(namespace, name));
		return element;
	};

	// From:
	// http://stackoverflow.com/questions/43455/how-do-i-serialize-a-dom-to-xml-text-using-javascript-in-a-cross-browser-way/43468#43468
	var serializeToString = function(xmlNode) {
		try {
			// Gecko- and Webkit-based browsers (Firefox, Chrome), Opera.
			return (new XMLSerializer()).serializeToString(xmlNode);
		} catch (e) {
			try {
				// Internet Explorer.
				return xmlNode.xml;
			} catch (e2) {
				// Other browsers without XML Serializer
				alert('Xmlserializer not supported');
			}
		}
		return false;
	};

	/*
	 * Sourced from:
	 * https://developer.mozilla.org/en/JavaScript/Reference/Global_Objects/Date#Example.3a_ISO_8601_formatted_dates
	 */
	var isoDateString = function(d) {
		function pad(n) {
			return n < 10 ? '0' + n : n;
		}
		return d.getUTCFullYear() + '-' + pad(d.getUTCMonth() + 1) + '-'
				+ pad(d.getUTCDate()) + 'T' + pad(d.getUTCHours()) + ':'
				+ pad(d.getUTCMinutes()) + ':' + pad(d.getUTCSeconds()) + 'Z';
	};

	var getCurrentDateTime = function() {
		return isoDateString(new Date());
	};

	/* Public Functions */
	instance.prepareFields = prepareFields;
	instance.styleTables = styleTables;
	instance.setRecordType = setRecordType;
	instance.setLicenseType = setLicenseType;
	instance.showLookupDialog = showLookupDialog;
	instance.ingestRecord = ingestRecord;
	instance.replicateSimpleField = replicateSimpleField;
	instance.insertPublicationFields = insertPublicationFields;
	instance.getTree = getTree;
	instance.createAnzsrcoLookup = createAnzsrcoLookup;
	instance.addKeyword = addKeyword;

	instance.getActivityAtom = getActivityAtom;
	instance.getAgentAtom = getAgentAtom;
	instance.getCollectionAtom = getCollectionAtom;
	instance.getServiceAtom = getServiceAtom;

	instance.getNewEntryId = getNewEntryId;
	instance.setPublished = setPublished;

	return instance;
}());

$(document).ready(function() {
	DataSpace.prepareFields();
	DataSpace.styleTables();
	DataSpace.setRecordType();
	DataSpace.setLicenseType();
});
