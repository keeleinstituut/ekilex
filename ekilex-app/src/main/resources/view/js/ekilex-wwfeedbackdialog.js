$(function() {
	$.fn.feedbackActionBtn = function() {
		const main = $(this);
		main.on('click', function(e) {
			e.preventDefault();
			const actionModal = main.closest('.modal');
			const actionForm = main.closest('form');
			const actionUrl = actionForm.attr('action');
			const feedbackLogId = actionForm.find("input[name='feedbackLogId']").val();

			const isValid = checkRequiredFields(actionForm);
			if (!isValid) {
				return;
			}

			$.ajax({
				url: actionUrl,
				data: actionForm.serialize(),
				method: 'POST'
			}).done(function(data) {
				actionModal.modal('hide');
				$("#feedbackLogRow_" + feedbackLogId).replaceWith(data);
				actionForm[0].reset();
				$wpm.bindObjects();
			}).fail(function(data) {
				actionModal.modal('hide');
				console.log(data);
				alert("Salvestamine ebaõnnestus");
			});
		});
	}
});


function deleteFeedback(feedbackId) {
	const deleteFeedbackUrl = `${applicationUrl}wwfeedback/deletefeedback/${feedbackId}`;
	$.get(deleteFeedbackUrl)
		.done(function(data) { })
		.fail(function(data) {
			console.log(data);
			alert('Viga!');
		});
};
