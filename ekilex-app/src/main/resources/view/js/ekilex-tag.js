$.fn.editTag = function() {
	var main = $(this);
	main.on('click', function(e) {
		e.preventDefault();
		let tagName = main.data('tag-name');
		let tagType = main.data('tag-type');
		let tagOrder = main.data('tag-order');
		let setAutomatically = main.data('tag-set-automatically');
		let removeToComplete = main.data('tag-remove-to-complete');

		let editTagDlg = $("#editTagDlg");
		let editTagForm = editTagDlg.find('form');
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
	var main = $(this);
	main.on('click', function(e) {
		e.preventDefault();
		let tagForm = main.closest('form');
		let isValid = checkRequiredFields(tagForm);
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
	let isUsed = $(this).data('tag-used');
	let title;
	if (isUsed) {
		title = 'Ettevaatust, silt on kasutusel. Kas oled kindel, et soovid kustutada?';
	} else {
		title = 'Kinnita sildi kustutamine';
	}
	$(this).confirmation({
		btnOkLabel : 'Jah',
		btnCancelLabel : 'Ei',
		title : title,
		onConfirm : function() {
			openWaitDlg();
			let tagName = $(this).data('tag-name');
			deleteTag(tagName);
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