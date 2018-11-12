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

$(document).on("click", "button[name='feedbackSendBtn']", function () {
    if (feedbackServiceUrl === null) {
        console.debug('Feedback service configuration is missing.');
        alert(messages.fb_service_error);
        return;
    }
	var feedbackForm = $(this).closest('form');
	var okMessage = feedbackForm.find('[name=ok_message]').text();
	var errorMessage = feedbackForm.find('[name=error_message]').text();
    return $.ajax({
        url: feedbackServiceUrl,
        data: feedbackForm.serialize(),
        method: 'POST'
    }).done(function(data) {
    	console.log(data);
    	var answer = JSON.parse(data);
    	if (answer.status === 'ok') {
    	    alert(okMessage);
        } else {
    	    alert(errorMessage);
        }
    }).fail(function (data) {
        console.log(data);
        alert(messages.fb_technical_error);
    });
});
