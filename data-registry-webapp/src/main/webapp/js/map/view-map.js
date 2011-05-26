OpenLayers.IMAGE_RELOAD_ATTEMPTS = 3;
function init() {
    $('#map').text("");
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

    $(".georss-point").each(function() {
        var pointStr = $(this).val();
        if (pointStr && pointStr != "") {
            var pnt = pointStr.split(" ");
            var point = new OpenLayers.Geometry.Point(pnt[0], pnt[1]).transform(WGS84, map.getProjectionObject());
            var pointFeature = new OpenLayers.Feature.Vector(point, null, style_blue);
            vectorLayer.addFeatures([pointFeature]);
        }
    });

    $(".georss-polygon").each(function() {
        var polygonStr = $(this).val();
        if (polygonStr && polygonStr != "") {
            var latsLongs = polygonStr.split(" ");
            var pointList = [];
            for (var index = 0; index < latsLongs.length - 1; index = index + 2) {
                var x = parseFloat(latsLongs[index]);
                var y = parseFloat(latsLongs[index + 1]);
                var newPoint = new OpenLayers.Geometry.Point(x, y).transform(WGS84, map.getProjectionObject());
                pointList.push(newPoint);
            }
            pointList.push(pointList[0]);

            var linearRing = new OpenLayers.Geometry.LinearRing(pointList);
            polygonFeature = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Polygon([linearRing]));
            vectorLayer.addFeatures([polygonFeature]);
        }
    });

    map.addLayer(vectorLayer);
    map.addControl(new OpenLayers.Control.LayerSwitcher());
    map.addControl(new OpenLayers.Control.MousePosition());

    if (!map.getCenter()) {
        map.zoomToMaxExtent()
    }
}
