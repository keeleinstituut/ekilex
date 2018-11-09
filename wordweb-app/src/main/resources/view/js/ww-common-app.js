var sessionTimeoutBufferSec = 60;

$(document).ready(function() {

	var sessionTimeoutMs = (sessionTimeoutSec - sessionTimeoutBufferSec) * 1000;
	setTimeout(function() {
		window.location = applicationUrl;
	}, sessionTimeoutMs);

	$('[data-toggle="tooltip"]').tooltip();
});

$(document).on("click", ".menu-btn", function(e) {
	$(".header-container").toggleClass("show-header");
});

function setActiveMenuItem(itemName) {
	$('.menu-item[data-item-name=' + itemName + ']').addClass('selected');
}
