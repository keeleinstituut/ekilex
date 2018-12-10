var sessionTimeoutBufferSec = 60;

$(document).ready(function() {

	var sessionTimeoutMs = (sessionTimeoutSec - sessionTimeoutBufferSec) * 1000;
	setTimeout(function() {
		window.location = applicationUrl;
	}, sessionTimeoutMs);

	//TODO causes # at url - can't allow that
	//parent.location.hash = ''; // Clear Hash

	$(".form-email").on('input', function() {
		var input = $(this);
		var re = /^[a-zA-Z0-9.!#$%&'*+/=?^_`{|}~-]+@[a-zA-Z0-9-]+(?:\.[a-zA-Z0-9-]+)*$/;
		var is_email = re.test(input.val());
		if (is_email) {
			input.siblings(".errors").find(".alert-mail").removeClass("error-show");
		} else {
			input.siblings(".errors").find(".alert-mail").addClass("error-show");
		}
	});

	$('[autofocus]:not(:focus)').eq(0).focus();
	$('.home-page #search').focus();

	// Focus trap modal
	$('#feedbackModal .btn:last-of-type').on('focus', function(e) {
		if ($("#feedbackModal").val() == "") {
			$("#feedbackModal .icon-close").focus();
		}
	});

	// Focus trap sidebar
	$('.header-links .menu-item:last-of-type').on('focus', function(e) {
		if ($("#langDropdownMenuButton").val() == "") {
			$("#langDropdownMenuButton").focus();
		}
	});

	$('[data-toggle="tooltip"]').tooltip();
});

$(document).on("click", ".menu-btn", function(e) {
	$(".header-container").toggleClass("show-header");
});

function setActiveMenuItem(itemName) {
	$('.menu-item[data-item-name=' + itemName + ']').addClass('selected');
}

function validateRequiredFormField(form, fieldName) {
	var fieldElement = form.find("input[name=" + fieldName + "]");
	if (fieldElement.val() == "") {
		fieldElement.siblings(".errors").find(".alert-danger").addClass("error-show");
	} else {
		fieldElement.siblings(".errors").find(".alert-danger").removeClass("error-show");
	}
}

function isValidFeedbackForm(fbForm) {
	validateRequiredFormField(fbForm, 'sender');
	validateRequiredFormField(fbForm, 'email');
	validateRequiredFormField(fbForm, 'word');
	return fbForm.find(".error-show").length == 0;
}

$(document).on("click", "button[name='feedbackSendBtn']", function() {
	if (feedbackServiceUrl === null) {
		console.debug('Feedback service configuration is missing.');
		alert(messages.fb_service_error);
		return;
	}
	var feedbackForm = $(this).closest('form');
	if (!isValidFeedbackForm(feedbackForm)) {
		return;
	}
	var okMessageElement = feedbackForm.find('[name=ok_message]');
	var errorMessageElement = feedbackForm.find('[name=error_message]');
	$.ajax({
		url : feedbackServiceUrl,
		data : feedbackForm.serialize(),
		method : 'POST'
	}).done(function(data) {
		console.log(data);
		var answer = JSON.parse(data);
		if (answer.status === 'ok') {
			okMessageElement.attr('hidden', false);
		} else {
			errorMessageElement.attr('hidden', false);
		}
	}).fail(function(data) {
		feedbackForm.find('.has-error').show();
	});
});

$(document).on("click", ".modal-check", function() {
	$(this).closest('form').find("button[name='feedbackSendBtn']").toggleClass('disabled');
});

function clearMessages(modalDlg) {
	modalDlg.find('.alert').removeClass('error-show');
	modalDlg.find('[name=ok_message]').attr('hidden', true);
	modalDlg.find('[name=error_message]').attr('hidden', true);
}

$(document).on("click", "#feedbackWordRadio", function() {
	$('#feedWord').addClass('show-section');
	$('#feedComment').removeClass('show-section');
	clearMessages($(this).closest('.modal-dialog'));
});

$(document).on("click", "#feedbackCommentRadio", function() {
	$('#feedWord').removeClass('show-section');
	$('#feedComment').addClass('show-section');
	clearMessages($(this).closest('.modal-dialog'));
});

$(document).on('show.bs.modal', '#feedbackModal', function() {
	clearMessages($(this));

	$('#feedbackModal').modal('toggle');
	$('#feedbackModal').on('shown.bs.modal', function() {
		$('#feedbackModal .close').focus()
	});

});
