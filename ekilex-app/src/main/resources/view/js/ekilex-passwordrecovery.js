// function initializePasswordRecovery() {
// 	$(document).on("click", "#passRecoveryBtn", function () {
// 		let passRecoveryForm = $("#passRecoveryForm");
// 		let code = $(this).data('code');
// 		let action = applicationUrl + "passwordrecovery/" + code;
// 		passRecoveryForm.attr("action", action);
// 		passRecoveryForm.submit();
// 	});
// };

$.fn.passwordRecoveryPlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('click', function() {
			const passRecoveryForm = obj.closest("#passRecoveryForm");
			const code = obj.data('code');
			const action = applicationUrl + "passwordrecovery/" + code;
			passRecoveryForm.attr("action", action);
			passRecoveryForm.submit();
		});
	});
}