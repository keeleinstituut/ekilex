function initializeTermCreateWordAndMeaning() {
	enableSelectWordBtn();
}

$.fn.createWordClickPlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('click', function() {
			const form = $('#createWordForm');
			if (!checkRequiredFields(form)) {
				return;
			}
			form.submit();
		});
	});
}

$.fn.enableSelectWordBtnPlugin = function() {
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

$.fn.editWordClickPlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('click', function() {
			const form = $('#createWordForm');
			form.find('input[name="clearResults"]').val(true);
			form.submit();
		});
	});
}

$.fn.selectWordClickPlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('click', function() {
			const dataset = $("#createWordForm").find('input[name="dataset"]').val();
			const selectWordForm = $('#selectWordForm');
			selectWordForm.find('input[name="dataset"]').val(dataset);
			selectWordForm.submit();
		});
	});
}