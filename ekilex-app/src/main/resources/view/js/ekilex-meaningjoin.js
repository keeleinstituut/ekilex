function initializeMeaningJoin() {
	let idsChk = $(document).find('input[name="sourceMeaningIds"]');
	let joinBtn = $("#joinMeaningsBtn");

	idsChk.on('change', function() {
		joinBtn.prop('disabled', !idsChk.filter(":checked").length);
	});

	var detailsDiv = $("#meaning-details-area");
	decorateSourceLinks(detailsDiv);
};

function joinMeanings() {
	let joinForm = $(this).closest('form');
	let validateJoinUrl = applicationUrl + "validatemeaningjoin";
	let failMessage = "Tähenduste ühendamine ebaõnnestus";
	validateAndSubmitJoinForm(validateJoinUrl, joinForm, failMessage);
};