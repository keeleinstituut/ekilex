function initializePasswordRecovery() {
	$(document).on("click", "#passRecoveryBtn", function () {
		let passRecoveryForm = $("#passRecoveryForm");
		let code = $(this).data('code');
		let action = applicationUrl + "passwordrecovery/" + code;
		passRecoveryForm.attr("action", action);
		passRecoveryForm.submit();
	});
};