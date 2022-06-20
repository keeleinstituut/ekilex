$.fn.editTag = function() {
	const main = $(this);
	main.on('click', function(e) {
		e.preventDefault();
		const tagName = main.data('tag-name');
		const tagType = main.data('tag-type');
		const tagOrder = main.data('tag-order');
		const setAutomatically = main.data('tag-set-automatically');
		const removeToComplete = main.data('tag-remove-to-complete');

		const editTagDlg = $("#editTagDlg");
		const editTagForm = editTagDlg.find('form');
		editTagForm.find('input[name="currentTagName"]').val(tagName);
		editTagForm.find('input[name="tagName"]').val(tagName);
		editTagForm.find('span[name="tagType"]').text(tagType);
		editTagForm.find('input[name="tagOrder"]').val(tagOrder);
		editTagForm.find('input[name="setAutomatically"]').prop('checked', setAutomatically);
		editTagForm.find('input[name="removeToComplete"]').prop('checked', removeToComplete);

		editTagDlg.modal('show');
	});
};

$.fn.saveTag = function() {
	const main = $(this);
	main.on('click', function(e) {
		e.preventDefault();
		const tagForm = main.closest('form');
		const isValid = checkRequiredFields(tagForm);
		if (isValid) {
			$.ajax({
				url: tagForm.attr('action'),
				data: tagForm.serialize(),
				method: 'POST',
			}).done(function(response) {
				if (response === "OK") {
					location.reload();
				} else {
					console.log(response);
					openAlertDlg("Salvestamine ebaõnnestus. Kontrolli, kas sellise nimega silt on juba olemas");
				}
			}).fail(function(data) {
				console.log(data);
				openAlertDlg(messages["common.error"]);
			});
		}
	});
};

$.fn.tagDeleteConfirm = function() {
	const tag = $(this);
	let title;
	if (tag.data('tag-used')) {
		title = 'Ettevaatust, silt on kasutusel. Kas oled kindel, et soovid kustutada?';
	} else {
		title = 'Kinnita sildi kustutamine';
	}
	tag.confirmation({
		btnOkLabel : 'Jah',
		btnCancelLabel : 'Ei',
		title : title,
		onConfirm : function() {
			openWaitDlg();
			deleteTag(tag.data('tag-name'));
		}
	});
};

function deleteTag(tagName) {
	$.ajax({
		url: applicationUrl + "delete_tag",
		data: {tagName: tagName},
		method: 'POST',
	}).done(function() {
		location.reload();
	}).fail(function(data) {
		console.log(data);
		openAlertDlg(messages["common.error"]);
	});
}