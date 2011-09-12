var MapEditor = function(jqueryObj) {
	var target = jqueryObj.get(0);
	var map, drawControls, polygonLayer;

	var init = function() {
		/* Close map data popup (which seems to auto-show itself on pan/zoom) */
		var closeMapData = function() {
			if ($('div.olLayerGoogleCopyright').css('display') == 'block') {
				$('div.olLayerGoogleCopyright').css('display', 'none');
			}
		};
		map = new OpenLayers.Map({
			div: target,
			eventListeners: {
                "moveend": closeMapData,
                "zoomend": closeMapData
            }
		});
		var layers = [];
		var osm = new OpenLayers.Layer.OSM();
		polygonLayer = new OpenLayers.Layer.Vector("Region Layer");

		if (typeof(google) != 'undefined') {
			var gphy = new OpenLayers.Layer.Google(
				"Google Physical",
				{
		            type: google.maps.MapTypeId.HYBRID,
		            sphericalMercator: true,
		            numZoomLevels: 20,
					wrapDateLine: true,
					disableDefaultUI: true // hide Map Data window
				}
			);
			map.addLayers([ gphy ]);
		}

		map.addLayers([ osm, polygonLayer ]);
		map.addControl(new OpenLayers.Control.LayerSwitcher());

		recenter();

		// Wait 3s for the map to load, then try to close data window.
		_.delay(closeMapData, 3000);
	};

	var makeEditable = function() {

		var changeFeature = function(geometry) {
			polygonLayer.removeAllFeatures();
			polygonLayer.addFeatures([
				new OpenLayers.Feature.Vector(geometry)
			]);
			recenter();
		};

		var boxHandler = function(boundsOrPixel) {
			if (boundsOrPixel instanceof OpenLayers.Pixel) {
				var pixel = boundsOrPixel;
				var coord = map.getLonLatFromPixel(pixel);
				var point = new OpenLayers.Geometry.Point(
						coord.lon, coord.lat);
				changeFeature(point);
			} else {
				var bounds = boundsOrPixel;
				var p1 = new OpenLayers.Pixel(bounds.left, bounds.top);
				var p2 = new OpenLayers.Pixel(bounds.right, bounds.bottom);
				var coord1 = map.getLonLatFromPixel(p1);
				var coord2 = map.getLonLatFromPixel(p2);
				bounds.left = coord1.lon;
				bounds.top = coord1.lat;
				bounds.right = coord2.lon;
				bounds.bottom = coord2.lat;
				var polygon = bounds.toGeometry();
				changeFeature(polygon);
			}
		};

		drawControls = {
			point : new OpenLayers.Control.DrawFeature(polygonLayer,
					OpenLayers.Handler.Point, {
						callbacks: {
							done: function(geometry) {
								changeFeature(geometry);
							}
						}
					}),
			box : new OpenLayers.Control.DrawFeature(polygonLayer,
					OpenLayers.Handler.Box, {
						callbacks: {
							done: boxHandler
						}
					}),
			polygon : new OpenLayers.Control.DrawFeature(polygonLayer,
					OpenLayers.Handler.Polygon, {
						callbacks: {
							done: function(geometry) {
								changeFeature(geometry);
							}
						}
					})
		};
		_.each(drawControls, function(control) {
			map.addControl(control);
		});

	};

	var parseXML = function(txt) {
		var xmlDoc = null;
		if (window.DOMParser) {
			parser=new DOMParser();
			xmlDoc=parser.parseFromString(txt,"text/xml");
		} else {
			// Internet Explorer
			xmlDoc=new ActiveXObject("Microsoft.XMLDOM");
			xmlDoc.async="false";
			xmlDoc.loadXML(txt);
		}
		return xmlDoc;
	};

	var loadData = function(elementString) {
		// Wrap in element and parse
		var georssFormatter = new OpenLayers.Format.GeoRSS();
		var wrappedTxt = '<entry>'+elementString+'</entry>';
		var root = parseXML(wrappedTxt);
		var feature = georssFormatter.createFeatureFromItem(root);
		// Transform the geometry in relation to the map
		var longlatProj = new OpenLayers.Projection("EPSG:4326");
		feature.geometry.transform(longlatProj, map.getProjectionObject());
		// Add the feature
		polygonLayer.addFeatures([feature]);
		recenter();
		return feature;
	};

	var exportData = function() {
		if (polygonLayer.features.length == 0) {
			return null;
		}
		var feature = polygonLayer.features[0];
		// Transform the geometry back to long/lat
		var longlatProj = new OpenLayers.Projection("EPSG:4326");
		var geometry = feature.geometry.clone();
		geometry.transform(map.getProjectionObject(), longlatProj);
		feature = new OpenLayers.Feature.Vector(geometry);
		var georssFormatter = new OpenLayers.Format.GeoRSS();
		var rssEntry = georssFormatter.createFeatureXML(feature);
		var georss = $(rssEntry).find(':not(title, description)').last().get(0);
		return georss;
	};

	var clearData = function() {
		polygonLayer.removeAllFeatures();
	};

	var recenter = function() {
		var center;
		if (polygonLayer.features.length == 0) {
			var proj = new OpenLayers.Projection("EPSG:4326");
			// Default center with no features
			center = new OpenLayers.LonLat(130.32129, -24.25231);
			map.setCenter(center.transform(proj, map.getProjectionObject()), 3);
		} else {
			// Otherwise shoot for the middle
			var feature = polygonLayer.features[0];
			var point = feature.geometry.getCentroid();
			center = new OpenLayers.LonLat(point.x, point.y);
			map.panTo(center);
		}
	};

	var toggleControl = function(element) {
		_.each(drawControls, function(control, key) {
			if (element.value == key && element.checked) {
				control.activate();
			} else {
				control.deactivate();
			}
		});
	};

	this.makeEditable = makeEditable;
	this.toggleControl = toggleControl;
	this.loadData = loadData;
	this.exportData = exportData;
	this.clearData = clearData;

	init();
};
