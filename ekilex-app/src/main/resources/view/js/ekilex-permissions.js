$(document).on("click", ":input[name='userEnableCheck']", function() {
	var userId = $(this).data('id');
	var enable = $(this).is(':checked');
	var userEnableUrl;
	if (enable == true) {
		userEnableUrl = applicationUrl + 'permissions/enable/' + userId;
	} else {
		userEnableUrl = applicationUrl + 'permissions/disable/' + userId;
	}
	$.get(userEnableUrl).done(function(data) {
		var permissionsArea = $('#permissionsArea');
		permissionsArea.replaceWith(data);
	}).fail(function(data) {
		console.log(data);
		alert('Viga!');
	});
});