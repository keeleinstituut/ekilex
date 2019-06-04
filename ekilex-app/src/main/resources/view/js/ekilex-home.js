
function initialise() {
	$(document).on("change", "#changeRoleSelect", function(e) {
		let selectedValue = $(this).find('option:selected');

		let isAdminField = $(this).closest('form').find('[name="isAdmin"]');
		let isAdminSelected = selectedValue.attr('data-admin') === 'true';
		isAdminField.val(isAdminSelected);

		this.form.submit();
	});
}
