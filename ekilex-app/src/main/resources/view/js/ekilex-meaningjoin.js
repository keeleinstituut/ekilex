function initialise() {
	let idsChk = $(document).find('input[name="sourceMeaningIds"]');
	let joinBtn = $("#joinMeaningsBtn");

	idsChk.on('change', function() {
		joinBtn.prop('disabled', !idsChk.filter(":checked").length);
	});
}

function joinMeanings() {
	let joinForm = $(this).closest('form');
	let validateJoinUrl = applicationUrl + "validatemeaningjoin";

	$.ajax({
		url: validateJoinUrl,
		data: joinForm.serialize(),
		method: 'POST',
	}).done(function(data) {
		let response = JSON.parse(data);
		if (response.status === 'valid') {
			joinForm.submit();
		} else if (response.status === 'invalid') {
			openAlertDlg(response.message);
		} else {
			openAlertDlg("Tähenduste ühendamine ebaõnnestus");
		}
	}).fail(function(data) {
		console.log(data);
		openAlertDlg('Tähenduste ühendamine ebaõnnestus');
	});
}