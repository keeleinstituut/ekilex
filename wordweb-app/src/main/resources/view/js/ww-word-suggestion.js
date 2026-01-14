var wordSuggestionFormValidator = null;

$(document).ready(function() {
	var wordSuggestionForm = document.getElementById("word-suggestion-form");
	if (wordSuggestionForm) {
		wordSuggestionFormValidator = new FormValidator(wordSuggestionForm);
	}
});

$(document).on("click", "#word-suggestion-send-btn", function(event) {

	if (!wordSuggestionFormValidator.validate()) {
		event.preventDefault();
		event.stopPropagation();
		return;
	}

	let wordSuggestionForm = $(this).closest('form');
	let actionUrl = wordSuggestionForm.attr('action');
	let serviceUrl = actionUrl + 'suggestion';

	$.ajax({
		url: serviceUrl,
		data: wordSuggestionForm.serialize(),
		method: "POST"
	}).done((data) => {
		resetMessages();
		if (data.status === "ok") {
			$("#word-suggestion-form-area").addClass('d-none');
			$("#word-suggestion-success-message").removeClass('d-none');
		} else {
			$("#word-suggestion-error-message").text(data.messageValue);
			$("#word-suggestion-error-message").removeClass('d-none');
		}
	});
});

$(document).on('show.bs.modal', '#word-suggestion-modal', function() {
	wordSuggestionFormValidator.reset();
	resetMessages();
	$("#word-suggestion-form-area").removeClass('d-none');
});

function resetMessages() {
	$("#word-suggestion-success-message").addClass('d-none');
	$("#word-suggestion-error-message").text('');
	$("#word-suggestion-error-message").addClass('d-none');
}
