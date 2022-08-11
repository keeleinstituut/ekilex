function initializeLexJoin() {
	let idsChk = $(document).find('input[name="sourceLexemeIds"]');
	let joinBtn = $("#joinLexemesBtn");

	idsChk.on('change', function() {
		joinBtn.prop('disabled', !idsChk.filter(":checked").length);
	});
};

function joinLexemes() {
	const joinForm = $(this).closest('form');
	const validateJoinUrl = applicationUrl + "validatelexjoin";
	const failMessage = messages["lexjoin.fail"];
	validateAndSubmitJoinForm(validateJoinUrl, joinForm, failMessage);
};