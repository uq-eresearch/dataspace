var map, drawControls;
//$(document).ready(function() {
//    loadScript();
//    getOpenLayersMaps();
//});

function init() {
    map = new OpenLayers.Map('map');
    var osm = new OpenLayers.Layer.OSM();
//    var wmsLayer = new OpenLayers.Layer.WMS("OpenLayers WMS", "http://vmap0.tiles.osgeo.org/wms/vmap0?", {layers: 'basic'});
    var pointLayer = new OpenLayers.Layer.Vector("Point Layer");
    var polygonLayer = new OpenLayers.Layer.Vector("Polygon Layer");

    var gphy = new OpenLayers.Layer.Google(
            "Google Physical",
            {type: google.maps.MapTypeId.TERRAIN}
            // used to be {type: G_PHYSICAL_MAP}
    );

    map.addLayers([gphy, osm, pointLayer, polygonLayer]);
    drawControls = {
        point: new OpenLayers.Control.DrawFeature(pointLayer, OpenLayers.Handler.Point),
        polygon: new OpenLayers.Control.DrawFeature(polygonLayer, OpenLayers.Handler.Polygon)
    };
    for (var key in drawControls) {
        map.addControl(drawControls[key]);
    }


    map.setCenter(new OpenLayers.LonLat(130.32129, -24.25231), 3);
    map.addControl(new OpenLayers.Control.LayerSwitcher());
    map.addControl(new OpenLayers.Control.MousePosition());
    document.getElementById('noneToggle').checked = true;

}

function toggleControl(element) {
    for (key in drawControls) {
        var control = drawControls[key];
        if (element.value == key && element.checked) {
            control.activate();
        } else {
            control.deactivate();
        }
    }
}
//
//function loadScript() {
//    var script = document.createElement("script");
//    script.type = "text/javascript";
//    script.src = "http://maps.google.com/maps/api/js?sensor=false";
//    $('head').append(script);
//    var script2 = document.createElement("script");
//    script2.type = "text/javascript";
//    script2.src = "http://maps.gstatic.com/intl/en_us/mapfiles/api-3/5/3/main.js";
//    $('head').append(script2);
//}
