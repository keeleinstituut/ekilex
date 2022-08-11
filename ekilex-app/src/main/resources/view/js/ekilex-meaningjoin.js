function initializeMeaningJoin() {
	const idsChk = $(document).find('input[name="sourceMeaningIds"]');
	const joinBtn = $("#joinMeaningsBtn");

	idsChk.on('change', function() {
		joinBtn.prop('disabled', !idsChk.filter(":checked").length);
	});

	const detailsDiv = $("#details-area");
	decorateSourceLinks(detailsDiv);
};

function joinMeanings() {
	const joinForm = $(this).closest('form');
	const validateJoinUrl = applicationUrl + "validatemeaningjoin";
	const failMessage = messages["meaningjoin.fail"];
	validateAndSubmitJoinForm(validateJoinUrl, joinForm, failMessage);
};