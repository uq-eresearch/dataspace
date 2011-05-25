OpenLayers.IMAGE_RELOAD_ATTEMPTS = 3;
function init() {
    var map, vectorLayer,polygonFeature;
    var WGS84_google_mercator = new OpenLayers.Projection("EPSG:900913");
    var WGS84 = new OpenLayers.Projection("EPSG:4326");
    var maxExtent = new OpenLayers.Bounds(-20037508, -20037508, 20037508, 20037508),
            restrictedExtent = maxExtent.clone(),
            maxResolution = 156543.0339;

    var options = {
        projection: WGS84_google_mercator,
        displayProjection: WGS84,
        units: "m",
        numZoomLevels: 18,
        maxResolution: maxResolution,
        maxExtent: maxExtent,
        restrictedExtent: restrictedExtent
    };
    map = new OpenLayers.Map('map', options);

    var gphy = new OpenLayers.Layer.Google(
            "Google Physical",
            {sphericalMercator: true}
            // used to be {type: G_PHYSICAL_MAP}
    );
    map.addLayer(gphy);

//    var layer = new OpenLayers.Layer.WMS("OpenLayers WMS",
//            "http://vmap0.tiles.osgeo.org/wms/vmap0", {layers: 'basic'});
//    map.addLayer(layer);

    var style_blue = OpenLayers.Util.extend({}, OpenLayers.Feature.Vector.style['default']);
    style_blue.strokeColor = "blue";
    style_blue.fillColor = "blue";
    var style_green = {
        strokeColor: "#339933",
        strokeOpacity: 1,
        strokeWidth: 3,
        pointRadius: 6,
        pointerEvents: "visiblePainted"
    };

    vectorLayer = new OpenLayers.Layer.Vector("Simple Geometry", {projection: WGS84});

    // create a point feature
    var point = new OpenLayers.Geometry.Point(-110, 45);
    var pointFeature = new OpenLayers.Feature.Vector(point, null, style_blue);

    // create a line feature from a list of points
    var pointList = [];
    var newPoint = point;
    for (var p = 0; p < 5; ++p) {
        newPoint = new OpenLayers.Geometry.Point(newPoint.x + Math.random(1),
                newPoint.y + Math.random(1)).transform(WGS84, map.getProjectionObject());
        pointList.push(newPoint);
    }
    var lineFeature = new OpenLayers.Feature.Vector(
            new OpenLayers.Geometry.LineString(pointList), null, style_green);

    // create a polygon feature from a linear ring of points
    var pointList = [];
    for (var p = 0; p < 6; ++p) {
        var a = p * (2 * Math.PI) / 7;
        var r = Math.random(1) + 1;
        var newPoint = new OpenLayers.Geometry.Point(point.x + (r * Math.cos(a)),
                point.y + (r * Math.sin(a))).transform(WGS84, map.getProjectionObject());
        pointList.push(newPoint);
    }
    pointList.push(pointList[0]);

    var linearRing = new OpenLayers.Geometry.LinearRing(pointList);
    polygonFeature = new OpenLayers.Feature.Vector(
            new OpenLayers.Geometry.Polygon([linearRing]));
    vectorLayer.addFeatures([pointFeature, lineFeature, polygonFeature]);

    map.addLayer(vectorLayer);
    map.addControl(new OpenLayers.Control.LayerSwitcher());
    map.addControl(new OpenLayers.Control.MousePosition());

    if (!map.getCenter()) {
        map.zoomToMaxExtent()
    }

}
