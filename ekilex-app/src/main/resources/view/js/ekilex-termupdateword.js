function initializeTermUpdateWord() {
	enableSelectWordForUpdateBtn();
}

$.fn.enableSelectUpdateWordBtnPlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('click', function() {
			enableSelectWordForUpdateBtn();
		});
	});
}

function enableSelectWordForUpdateBtn() {
	if ($('input[name="wordId"]:checked').length === 1) {
		$("#selectWordBtn").prop("disabled", false);
	}
}