var MapEditor = function(jqueryObj) {
	var target = jqueryObj.get(0);
	var map, drawControls;

	var init = function() {
		map = new OpenLayers.Map({div: target});
		var osm = new OpenLayers.Layer.OSM();
		var wmsLayer = new OpenLayers.Layer.WMS("OpenLayers WMS",
				"http://vmap0.tiles.osgeo.org/wms/vmap0?", {
					layers : 'basic',
					attribution : 'Provided by OSGeo'
				});
		var polygonLayer = new OpenLayers.Layer.Vector("Polygon Layer");
		var longlatProj = new OpenLayers.Projection("EPSG:4326");

		var gphy = new OpenLayers.Layer.GoogleNG({
			name : "Google Physical",
			type : google.maps.MapTypeId.TERRAIN
		});

		map.addLayers([ gphy, osm, polygonLayer ]);

		var changeFeature = function(geometry) {
			polygonLayer.removeAllFeatures();
			polygonLayer.addFeatures([
				new OpenLayers.Feature.Vector(geometry)
			]);
			var formatter = new OpenLayers.Format.GeoRSS();
			var features = _.map(polygonLayer.features, function(feature) {
				var geometry = feature.geometry.clone();
				geometry.transform(map.getProjectionObject(), longlatProj);
				return new OpenLayers.Feature.Vector(geometry);
			});
			var dom = $(formatter.createFeatureXML(features[0])).find(':not(title, description)').first();
			console.debug(dom);
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
							done: function(boundsOrPixel) {
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
							}
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
		for ( var key in drawControls) {
			map.addControl(drawControls[key]);
		}

		var center = new OpenLayers.LonLat(130.32129, -24.25231);
		map.setCenter(center.transform(longlatProj, map.getProjectionObject()), 3);
		map.addControl(new OpenLayers.Control.LayerSwitcher());
		polygonLayer.addFeatures([drawPolygon(map)]);

	};

	var toggleControl = function(element) {
		for (key in drawControls) {
			var control = drawControls[key];
			if (element.value == key && element.checked) {
				control.activate();
			} else {
				control.deactivate();
			}
		}
	};

	var drawPolygon = function(map) {
		var pointList = [];

		var proj = new OpenLayers.Projection("EPSG:4326");
		var point = map.getCenter().transform(map.getProjectionObject(),proj);

		for ( var p = 0; p < 6; ++p) {
			var a = p * (2 * Math.PI) / 7;
			var r = Math.random(1) + 1;
			var x = point.lon + (r * Math.sin(a));
			var y = point.lat + (r * Math.cos(a));
			var newPoint = new OpenLayers.Geometry.Point(x, y);
			newPoint = newPoint.transform(proj, map.getProjectionObject());
			pointList.push(newPoint);
		}
		pointList.push(pointList[0]);

		var linearRing = new OpenLayers.Geometry.LinearRing(pointList);
		var polygonFeature = new OpenLayers.Feature.Vector(
				new OpenLayers.Geometry.Polygon([ linearRing ]));
		return polygonFeature;
	};

	this.init = init;
	this.toggleControl = toggleControl;

};
