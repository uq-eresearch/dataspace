var map, drawControls;
function init() {
    map = new OpenLayers.Map('map');

    var wmsLayer = new OpenLayers.Layer.WMS("OpenLayers WMS", "http://vmap0.tiles.osgeo.org/wms/vmap0?", {layers: 'basic'});

    var pointLayer = new OpenLayers.Layer.Vector("Point Layer");
    var polygonLayer = new OpenLayers.Layer.Vector("Polygon Layer");

    map.addLayers([wmsLayer, pointLayer, polygonLayer]);
    map.addControl(new OpenLayers.Control.LayerSwitcher());
    map.addControl(new OpenLayers.Control.MousePosition());

    drawControls = {
        point: new OpenLayers.Control.DrawFeature(pointLayer,
                OpenLayers.Handler.Point),
        polygon: new OpenLayers.Control.DrawFeature(polygonLayer,
                OpenLayers.Handler.Polygon)
    };

    for (var key in drawControls) {
        map.addControl(drawControls[key]);
    }

    map.setCenter(new OpenLayers.LonLat(0, 0), 3);

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