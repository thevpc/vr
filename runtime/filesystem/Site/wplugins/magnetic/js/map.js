jQuery(function ($) {
    //, 
    var longitude = 35.820547;
    var latitude = 10.593090;
    var canvas = "map";
    var user_name = '';
    var user_email = '';
    if (document.getElementById('contactForm:teacher_name')) {
        user_name = document.getElementById('contactForm:teacher_name').value;
    }
    if (document.getElementById('contactForm:teacher_email')) {
        user_email = document.getElementById('contactForm:teacher_email').value;
    }
    function randing_map(canvas, lan, lat) {
        var myLatlng = new google.maps.LatLng(lan, lat);
        var myOptions = {
            zoom: 13,
            center: myLatlng,
            mapTypeId: google.maps.MapTypeId.ROADMAP,
            maxZoom: 20,
            disableDefaultUI: true
        }
        var map = new google.maps.Map(document.getElementById(canvas), myOptions);
        var marker = new google.maps.Marker({
            position: myLatlng,
            map: map,
            icon: "/vr/fs/Site/wplugins/magnetic/img/point.png"
        });
        var styles = [
            {
                featureType: "all",
                stylers: [
                    {saturation: -80}
                ]
            }, {
                featureType: "road.arterial",
                elementType: "geometry",
                stylers: [
                    {hue: "#00ffee"},
                    {saturation: 50}
                ]
            }, {
                featureType: "poi.business",
                elementType: "labels",
                stylers: [
                    {visibility: "off"}
                ]
            }
        ];
        var infowindow = new google.maps.InfoWindow({
            content: "<div class='map_adresse'><div class='map_address'><span class='address'><p>"+user_name+"</p>ENISo, National School of Engineers of Sousse, Tunisia </span><p>BP 264 Sousse Erriadh 4023</p></div> <div class='map_tel'><span class='tel'>Email : </span>"+user_email+"</div><div class='map_tel'><span class='tel'>Phone : </span>(+216) 73 216-73-369-500</div></div>"
        });

        map.setOptions({styles: styles});

        google.maps.event.addListener(marker, 'click', function () {
            infowindow.open(map, marker);
        });
    }
    randing_map(canvas, longitude, latitude);

});