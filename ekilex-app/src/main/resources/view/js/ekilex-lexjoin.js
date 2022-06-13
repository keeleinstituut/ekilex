function initializeLexJoin() {
	let idsChk = $(document).find('input[name="sourceLexemeIds"]');
	let joinBtn = $("#joinLexemesBtn");

	idsChk.on('change', function() {
		joinBtn.prop('disabled', !idsChk.filter(":checked").length);
	});
};

function joinLexemes() {
	let joinForm = $(this).closest('form');
	let validateJoinUrl = applicationUrl + "validatelexjoin";
	let failMessage = /*[[#{lexjoin.fail}]]*/'Tähenduste ühendamine ebaõnnestus';
	validateAndSubmitJoinForm(validateJoinUrl, joinForm, failMessage);
};