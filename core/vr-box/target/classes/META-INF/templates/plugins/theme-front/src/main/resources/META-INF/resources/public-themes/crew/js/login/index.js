$('.tooltip').hide();

$('.form-input').focus(function() {
   $('.tooltip').fadeOut(250);
   $("."+$(this).attr('tooltip-class')).fadeIn(500);
});

$('.form-input').blur(function() {
   $('.tooltip').fadeOut(250);
});

$('.login-button').click(function (event) {
  event.preventDefault();
  // or use return false;
});

$( ".login-button" ).click(function() {
    if (  $( '.login-form' ).css( "transform" ) == 'none' ){
        $('.login-form').css("transform","rotateY(-180deg)");
	      $('.loading').css("transform","rotateY(0deg)");
      	var delay=600;
        setTimeout(function(){
          $('.loading-spinner-large').css("display","block");
          $('.loading-spinner-small').css("display","block");
        }, delay);
    } else {
        $('.login-form').css("transform","" );
    }
});