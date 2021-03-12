$(function(){
	$.fn.addFeedbackCommentBtn = function() {
		var main = $(this);
		main.on('click', function(e) {
			e.preventDefault();
			var theForm = main.closest('form');
			var formUrl = theForm.attr('action');
			var data = theForm.serializeJSON();
			var dataStr = JSON.stringify(theForm.serializeJSON());
			var ekiCommentsArea = $("#ekiCommentsArea_" + data.feedbackId);
			$.ajax({
				url: formUrl,
				data: dataStr,
				method: 'POST',
				contentType: 'application/json'
			}).done(function(data) {
				ekiCommentsArea.replaceWith(data);
				theForm.find("textarea").val("");
				$wpm.bindObjects();
			}).fail(function(data) {
				console.log(data);
				alert("Kommentaari lisamine ebaõnnestus");
			});
		});
	}
});

function deleteFeedback(feedbackId) {
	var deleteFeedbackUrl = applicationUrl + 'wwfeedback/deletefeedback/' + feedbackId;
	$.get(deleteFeedbackUrl).done(function(data) {
		var ekiCommentsArea = $("#ekiCommentsArea_" + feedbackId);
		ekiCommentsArea.replaceWith(data);
	}).fail(function(data) {
		console.log(data);
		alert('Viga!');
	});
};
