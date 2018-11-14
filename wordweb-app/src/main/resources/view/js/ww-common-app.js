var sessionTimeoutBufferSec = 60;

$(document).ready(function() {

	var sessionTimeoutMs = (sessionTimeoutSec - sessionTimeoutBufferSec) * 1000;
	setTimeout(function() {
		window.location = applicationUrl;
	}, sessionTimeoutMs);

	$('[data-toggle="tooltip"]').tooltip();

	$(".form-email").on('input', function() {
		var $errorMail = $(".alert-mail");
		var input = $(this);
		var re = /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/;
		var is_email = re.test(input.val());
		if (is_email) {
			input.siblings(".errors").find($errorMail).removeClass("error-show");
		} else {
			input.siblings(".errors").find($errorMail).addClass("error-show");
		}
	});

});

$(document).on("click", ".menu-btn", function(e) {
	$(".header-container").toggleClass("show-header");
});

function setActiveMenuItem(itemName) {
	$('.menu-item[data-item-name=' + itemName + ']').addClass('selected');
}

$(document).on("click", "button[name='feedbackSendBtn']", function() {
	var $error = $(".alert-danger");
	if ($("input[name='sender']").val() == "") {
		$("input[name='sender']").siblings(".errors").find($error).addClass("error-show");
	} else {
		$("input[name='sender']").siblings(".errors").find($error).removeClass("error-show");
	}
	;
	if ($("input[name='email']").val() == "") {
		$("input[name='email']").siblings(".errors").find($error).addClass("error-show");
	} else {
		$("input[name='email']").siblings(".errors").find($error).removeClass("error-show");
	}
	;
	if ($("input[name='word']").val() == "") {
		$("input[name='word']").siblings(".errors").find($error).addClass("error-show");
	} else {
		$("input[name='word']").siblings(".errors").find($error).removeClass("error-show");
	}
	;

	if (feedbackServiceUrl === null) {
		console.debug('Feedback service configuration is missing.');
		alert(messages.fb_service_error);
		return;
	}
	var feedbackForm = $(this).closest('form');
	var okMessage = feedbackForm.find('[name=ok_message]').text();
	var errorMessage = feedbackForm.find('[name=error_message]').text();
	return $.ajax({
		url : feedbackServiceUrl,
		data : feedbackForm.serialize(),
		method : 'POST'
	}).done(function(data) {
		console.log(data);
		var answer = JSON.parse(data);
		if (answer.status === 'ok') {
			alert(okMessage);
		} else {
			alert(errorMessage);
		}
	}).fail(function(data) {
		$('.has-error').show();
	});
});

$(document).on("click", "#customRadioInline1", function() {
	$('#feedWord').addClass('show-section');
	$('.alert').removeClass('error-show');
	$('#feedComment').removeClass('show-section');
});

$(document).on("click", "#customRadioInline2", function() {
	$('#feedWord').removeClass('show-section');
	$('#feedComment').addClass('show-section');
	$('.alert').removeClass('error-show');
});
