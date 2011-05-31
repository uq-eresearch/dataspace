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
            {type: google.maps.MapTypeId.HYBRID, sphericalMercator: true, numZoomLevels: 20}
            // used to be {type: G_PHYSICAL_MAP}
    );
    map.addLayer(gphy);

    var point_style = OpenLayers.Util.extend({}, OpenLayers.Feature.Vector.style['default']);
    point_style.strokeColor = "#cc33ff";
    point_style.fillColor = "#cc33ff";
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
            var lat = parseFloat(pnt[0]);
            var long = parseFloat(pnt[1]);
            var point = new OpenLayers.Geometry.Point(long, lat).transform(WGS84, map.getProjectionObject());
            var pointFeature = new OpenLayers.Feature.Vector(point, null, point_style);
            vectorLayer.addFeatures([pointFeature]);
        }
    });

    $(".georss-polygon").each(function() {
        var polygonStr = $(this).val();
        if (polygonStr && polygonStr != "") {
            var latsLongs = polygonStr.split(" ");
            var pointList = [];
            for (var index = 0; index < latsLongs.length - 1; index = index + 2) {
                var lat = parseFloat(latsLongs[index]);
                var long = parseFloat(latsLongs[index + 1]);
                var newPoint = new OpenLayers.Geometry.Point(long, lat).transform(WGS84, map.getProjectionObject());
                pointList.push(newPoint);
            }
            pointList.push(pointList[0]);
            var linearRing = new OpenLayers.Geometry.LinearRing(pointList);
            polygonFeature = new OpenLayers.Feature.Vector(new OpenLayers.Geometry.Polygon([linearRing]));
            vectorLayer.addFeatures([polygonFeature]);
        }
    });

    map.addLayer(vectorLayer);
    map.addControl(new OpenLayers.Control.MousePosition());
    if (!map.getCenter()) {
        map.zoomToExtent(vectorLayer.getDataExtent());
        map.zoomOut(1);
    }
}
