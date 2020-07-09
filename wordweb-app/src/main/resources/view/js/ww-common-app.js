var sessionTimeoutBufferSec = 60;

$(document).ready(function() {

	var sessionTimeoutMs = (sessionTimeoutSec - sessionTimeoutBufferSec) * 1000;
	setTimeout(function() {
		window.location = applicationUrl;
	}, sessionTimeoutMs);

	$('[autofocus]:not(:focus)').eq(0).focus();
	$('.home-page #search').focus();

	// Focus trap sidebar
	$('.header-links .menu-item:last-of-type').on('focus', function(e) {
		if ($("#uiLangMenuButton").val() == "") {
			$("#uiLangMenuButton").focus();
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



$(document).on("click", "button[name='feedbackSendBtn']", function(event) {

	if (feedbackServiceUrl === null) {
		console.debug('Feedback service configuration is missing.');
		alert(messages.fb_service_error);
		return;
	}
	var feedbackForm = $(this).closest('form');

	feedbackForm.addClass('was-validated');
	if (feedbackForm[0].checkValidity() === false) {
		event.preventDefault();
		event.stopPropagation();
		return;
	}

	var dataDiv = $('#feedbackModal').find('[name=dataDiv]');
	var responseDiv = $('#feedbackModal').find('[name=responseDiv]');
	var okMessageElement = responseDiv.find('[name=ok_message]');
	var errorMessageElement = feedbackForm.find('[name=error_message]');
	var okMessage = feedbackForm.find('[name=ok_message]').text();
    var acceptPrivacyStatement = feedbackForm.find('.modal-check');
	$.ajax({
		url: feedbackServiceUrl,
		data: JSON.stringify(feedbackForm.serializeJSON()),
		method: 'POST',
		dataType: 'json',
		contentType: 'application/json'
	}).done(function(data) {
		if (data.status === 'ok') {
			dataDiv.attr('hidden', true);
			responseDiv.attr('hidden', false);
			okMessageElement.text(okMessage);
			okMessageElement.attr('hidden', false);
			acceptPrivacyStatement.trigger('click');
		} else {
			errorMessageElement.attr('hidden', false);
		}
	}).fail(function(data) {
		//dataDiv.attr('hidden', true);
		responseDiv.attr('hidden', false);
		responseDiv.find('.has-error').show();
		//acceptPrivacyStatement.trigger('click');
                                    errorMessageElement.attr('hidden', false);
                                    errorMessageElement.text('Vabandust, saatmine ebaõnnestus. Proovige hiljem uuesti.');
                                    errorMessageElement.css('color', 'red');
	});
});

$(document).on("click", ".modal-check", function() {
	$(this).closest('form').find("button[name='feedbackSendBtn']").prop('disabled', !$(this).prop('checked'));
});

function clearMessages(modalDlg) {
	modalDlg.find('form').removeClass('was-validated');
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
	var fbModal = $(this);
	clearMessages(fbModal);
	fbModal.find('[name=dataDiv]').attr('hidden', false);
	fbModal.find('[name=responseDiv]').attr('hidden', true);
	fbModal.modal('toggle');
	fbModal.off('shown.bs.modal').on('shown.bs.modal', function() {
		fbModal.find('.close').focus()
	});
});
