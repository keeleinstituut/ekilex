// general demo js
$(function() {
	// show menu in mobile
	$(".menu-btn").click(function(){
	    $(".header-container").toggleClass("show-header");
	});
	// display full info in 3-rd level content
	$(".more-btn, .level-3-panel").click(function(){
	    $(this).parent().toggleClass("expand");
	});
});

// demo js for autocomplete
$( ".search-phrase" ).focus(function() {
	$(".awesomplete ul").removeClass("d-none");
});
$( ".search-phrase" ).focusout(function() {
	$(".awesomplete ul").addClass("d-none");
});


// demo js for interactions between the mobile and desktop modes
$(function() {
	$(".logo").click(function(){
		if ($(".homonym-panel").hasClass( "d-none" )) {
		   $(".content-panel").addClass("d-none d-md-block");
		   $(".homonym-panel").removeClass("d-none d-md-block");
		   $(".search-panel").removeClass("d-none d-md-block");
		}
	});
	$(".homonym-item").click(function(){
	    $(".homonym-item").removeClass("selected last-selected");
	    $(".homonym-item-1").removeClass("animation-target").dequeue();
	    $(this).addClass("selected last-selected");
		if ($(window).width() < 767) {
		   $(".content-panel").removeClass("d-none d-md-block");
		   $(".homonym-panel").addClass("d-none d-md-block");
		   $(".search-panel").addClass("d-none d-md-block");
		}
	});
});
$( window ).resize(function() {
	if ($(window).width() < 768) {
	   $(".homonym-item").removeClass("selected");
		   $(".content-panel").addClass("d-none d-md-block");
		   $(".homonym-panel").removeClass("d-none d-md-block");
		   $(".search-panel").removeClass("d-none d-md-block");
	}
	else {
		$(".last-selected").addClass("selected");
		if (!$(".homonym-item").hasClass("last-selected")) {
			$(".homonym-item-1").addClass("selected last-selected");
		}
	}
});
$( document ).ready(function() {
	if ($(window).width() < 768) {
		$(".content-panel").addClass("d-none d-md-block");
		$(".homonym-item").removeClass("last-selected");
	}
});