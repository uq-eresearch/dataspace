var shape;
function initialize() {
    var mapDiv = document.getElementById('map-canvas');
    var map = new google.maps.Map(mapDiv, {
        center: new google.maps.LatLng(24.886436490787712, -70.2685546875),
        zoom: 4,
        mapTypeId: google.maps.MapTypeId.ROADMAP
    });

    shape = new google.maps.Polygon({
        strokeColor: '#ff0000',
        strokeOpacity: 0.8,
        strokeWeight: 2,
        fillColor: '#ff0000',
        fillOpacity: 0.35
    });

    shape.setMap(map);

    google.maps.event.addListener(map, 'click', addPoint);
}

function addPoint(e) {
    var vertices = shape.getPath();

    vertices.push(e.latLng);
}
