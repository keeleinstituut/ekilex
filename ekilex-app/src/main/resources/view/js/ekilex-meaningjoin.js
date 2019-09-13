function initialise() {
	let idsChk = $(document).find('input[name="sourceMeaningIds"]');
	let joinBtn = $("#joinMeaningsBtn");

	idsChk.on('change', function() {
		joinBtn.prop('disabled', !idsChk.filter(":checked").length);
	});
}