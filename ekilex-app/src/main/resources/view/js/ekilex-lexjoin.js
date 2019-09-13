function initialise() {
	let idsChk = $(document).find('input[name="sourceLexemeIds"]');
	let joinBtn = $("#joinLexemesBtn");

	idsChk.on('change', function() {
		joinBtn.prop('disabled', !idsChk.filter(":checked").length);
	});
}