//var COUNTDOWN_DATE='02/19/2012 10:1 AM';
if (COUNTDOWN_DATE) {
    if (!COUNTDOWN_TEXT) {
        COUNTDOWN_TEXT='Event in %1';
    }
    if(COUNTDOWN_TEXT.indexOf('%1')<0){
        COUNTDOWN_TEXT=COUNTDOWN_TEXT+' %1';
    }
    var end = new Date(COUNTDOWN_DATE);

    var _second = 1000;
    var _minute = _second * 60;
    var _hour = _minute * 60;
    var _day = _hour * 24;
    var timer;

    function showRemaining() {
        var now = new Date();
        var distance = end - now;
        if (distance < 0) {

            clearInterval(timer);
            document.getElementById('countdown').innerHTML = '';

            return;
        }
        var days = Math.floor(distance / _day);
        var hours = Math.floor((distance % _day) / _hour);
        var minutes = Math.floor((distance % _hour) / _minute);
        var seconds = Math.floor((distance % _minute) / _second);
        var str=days + 'days ';
        str+= hours + 'hrs ';
        str+= minutes + 'mins ';
        str+= seconds + 'secs';
        document.getElementById('countdown').innerHTML = COUNTDOWN_TEXT.replace('%1',str);
    }
    timer = setInterval(showRemaining, 1000);
}
