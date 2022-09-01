function initializeTermUpdateWord() {
	enableSelectWordBtn();
}

$.fn.enableSelectUpdateWordBtnPlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('click', function() {
			enableSelectWordBtn();
		});
	});
}

function enableSelectWordBtn() {
	if ($('input[name="wordId"]:checked').length === 1) {
		$("#selectWordBtn").removeAttr("disabled");
	}
}