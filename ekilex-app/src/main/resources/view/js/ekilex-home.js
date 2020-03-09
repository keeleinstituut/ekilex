function initialise() {
	$(document).on("change", "#changeRoleSelect", function(e) {
		this.form.submit();
	});

	$(document).on("change", "#changeLayerSelect", function(e) {
		this.form.submit();
	});
}
