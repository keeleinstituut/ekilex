function initializeTermCreateWordAndMeaning() {
	enableSelectWordBtn();
}

$.fn.createWordClickPlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('click', function() {
			const form = $('#createWordForm');
			const message = messages['term.add.term']
			if (!checkRequiredFields(form, message)) {
				return;
			}
			form.trigger('submit');
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
		$("#selectWordBtn").prop("disabled", false);
	}
}

$.fn.editWordClickPlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('click', function() {
			const form = $('#createWordForm');
			form.find('input[name="clearResults"]').val(true);
			form.trigger('submit');
		});
	});
}

$.fn.initCreateWordPlugin = function() {
	return this.each(function() {
		const btn = $(this);
		btn.on('click', function() {
			const form = btn.closest('form');
			const backUri = getTermSearchBackUri();
			form.find('input[name="backUri"]').val(backUri);
			form.trigger('submit');
		});
	});
}