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
	let failMessage = "Tähenduste ühendamine ebaõnnestus";
	validateAndSubmitJoinForm(validateJoinUrl, joinForm, failMessage);
}