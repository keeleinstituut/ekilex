function initializeLexJoin() {
	let idsChk = $(document).find('input[name="sourceLexemeIds"]');
	let joinBtn = $("#joinLexemesBtn");

	idsChk.on('change', function() {
		joinBtn.prop('disabled', !idsChk.filter(":checked").length);
	});
};

function joinLexemes() {
	const joinForm = $(this).closest('form');
	const lexjoinvalidateUrl = applicationUrl + "lexjoinvalidate";
	const failMessage = messages["lexjoin.fail"];
	validateAndSubmitJoinForm(lexjoinvalidateUrl, joinForm, failMessage);
};