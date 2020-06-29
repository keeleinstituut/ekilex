function initializeWordJoin() {
	let idsChk = $(document).find('input[name="sourceWordIds"]');
	let joinBtn = $("#joinWordsBtn");

	idsChk.on('change', function() {
		joinBtn.prop('disabled', !idsChk.filter(":checked").length);
	});
};