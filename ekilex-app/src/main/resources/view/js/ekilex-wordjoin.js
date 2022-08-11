function initializeWordJoin() {
	const idsChk = $(document).find('input[name="sourceWordIds"]');
	const joinBtn = $("#joinWordsBtn");

	idsChk.on('change', function() {
		joinBtn.prop('disabled', !idsChk.filter(":checked").length);
	});
};