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
		var pointLayer = new OpenLayers.Layer.Vector("Point Layer");
		var polygonLayer = new OpenLayers.Layer.Vector("Polygon Layer");

		var gphy = new OpenLayers.Layer.GoogleNG({
			name : "Google Physical",
			type : google.maps.MapTypeId.TERRAIN
		});

		map.addLayers([ gphy, osm, pointLayer, polygonLayer ]);

		drawControls = {
			point : new OpenLayers.Control.DrawFeature(pointLayer,
					OpenLayers.Handler.Point, {
						callbacks: {
							done: function(geometry) {
								console.debug(geometry);
							}
						}
					}),
			box : new OpenLayers.Control.DrawFeature(polygonLayer,
					OpenLayers.Handler.Box, {
						callbacks: {
							done: function(boundsOrPixel) {
								if (boundsOrPixel instanceof OpenLayers.Pixel) {
									var pixel = boundsOrPixel;
									console.debug(pixel);
								} else {
									var polygon = boundsOrPixel.toGeometry();
									console.debug(polygon);
								}
							}
						}
					}),
			polygon : new OpenLayers.Control.DrawFeature(polygonLayer,
					OpenLayers.Handler.Polygon, {
						callbacks: {
							done: function(geometry) {
								console.debug(geometry);
							}
						}
					})
		};
		for ( var key in drawControls) {
			map.addControl(drawControls[key]);
		}

		var proj = new OpenLayers.Projection("EPSG:4326");
		var center = new OpenLayers.LonLat(130.32129, -24.25231);
		map.setCenter(center.transform(proj, map.getProjectionObject()), 3);
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
