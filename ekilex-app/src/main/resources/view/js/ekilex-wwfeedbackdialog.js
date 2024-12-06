$(function(){
	$.fn.addFeedbackCommentBtn = function() {
		const main = $(this);
		main.on('click', function(e) {
			e.preventDefault();
			const form = main.closest('form');
			const formUrl = form.attr('action');
			const data = form.serializeJSON();
			const dataStr = JSON.stringify(form.serializeJSON());
			const ekiCommentsArea = $("#ekiCommentsArea_" + data.feedbackId);
			$.ajax({
				url: formUrl,
				data: dataStr,
				method: 'POST',
				contentType: 'application/json'
			}).done(function(data) {
				ekiCommentsArea.replaceWith(data);
				form.find("textarea").val("");
				$wpm.bindObjects();
			}).fail(function(data) {
				console.log(data);
				alert("Kommentaari lisamine ebaõnnestus");
			});
		});
	}
});

function deleteFeedback(feedbackId) {
	const deleteFeedbackUrl = `${applicationUrl}wwfeedback/deletefeedback/${feedbackId}`;
	$.get(deleteFeedbackUrl)
	.done(function(data) {})
	.fail(function(data) {
		console.log(data);
		alert('Viga!');
	});
};
