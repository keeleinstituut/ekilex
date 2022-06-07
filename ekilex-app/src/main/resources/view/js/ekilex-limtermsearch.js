$.fn.duplicateLimTermMeaningPlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('click', function() {
			const url = applicationUrl + 'duplicatemeaning/' + obj.data('meaning-id');
			$.post(url).done(function(response) {
				if (response.status === "OK") {
					openMessageDlg(response.message);
					const duplicateMeaningId = response.id;
					setTimeout(function() {
						window.location = applicationUrl + 'limtermmeaningback/' + duplicateMeaningId;
					}, 1500);
				} else {
					openAlertDlg(response.message);
				}
			}).fail(function(data) {
				openAlertDlg("Mõiste dubleerimine ebaõnnestus");
				console.log(data);
			});
		});
	});
}

// $(document).on('click', '#duplicateLimTermMeaningBtn', function() {
// 	let url = applicationUrl + 'duplicatemeaning/' + $(this).data('meaning-id');
// 	$.post(url).done(function(data) {
// 		let response = JSON.parse(data);
// 		if (response.status === 'ok') {
// 			openMessageDlg(response.message);
// 			let duplicateMeaningId = response.duplicateMeaningId;
// 			setTimeout(function() {
// 				window.location = applicationUrl + 'limtermmeaningback/' + duplicateMeaningId;
// 			}, 1500);
// 		} else {
// 			openAlertDlg(response.message);
// 		}
// 	}).fail(function(data) {
// 		openAlertDlg("Mõiste dubleerimine ebaõnnestus");
// 		console.log(data);
// 	});
// });

function initNewLimTermWordDlg() {
	let newWordDlg = $('#newLimTermWordDlg');
	newWordDlg.on('shown.bs.modal', function(e) {
		newWordDlg.find('.form-control').first().focus();
		newWordDlg.find('.form-control').each(function() {
			$(this).removeClass('is-invalid');
		});
		let searchValue = $("input[name='simpleSearchFilter']").val() || '';
		if (!searchValue.includes('*') && !searchValue.includes('?')) {
			newWordDlg.find('[name=wordValue]').val(searchValue);
		} else {
			newWordDlg.find('[name=wordValue]').val(null);
		}
		let meaningId = $(e.relatedTarget).data('meaning-id');
		$('[name=meaningId]').val(meaningId);
	});

	newWordDlg.find('.form-control').on('change', function() {
		if ($(this).val()) {
			$(this).removeClass('is-invalid');
		} else {
			$(this).addClass('is-invalid');
		}
	});
	// $(document).on("click", "#limTermWordFormSubmitBtn", function() {
	// 	var addWordForm = $("#addLimTermWordForm");
	// 	var isValid = checkRequiredFields(addWordForm);
	// 	if (!isValid) {
	// 		return;
	// 	}
	// 	addWordForm.submit();
	// });
}

$.fn.limTermWordFormSubmitPlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('click', function() {
			const addWordForm = obj.closest('#addLimTermWordForm');
			const isValid = checkRequiredFields(addWordForm);
			if (!isValid) {
				return;
			}
			addWordForm.submit();
		});
	});
}