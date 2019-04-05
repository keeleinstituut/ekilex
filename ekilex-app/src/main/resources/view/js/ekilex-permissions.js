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

$(document).on("click", ":input[name='userAdminCheck']", function() {
	var userId = $(this).data('id');
	var enable = $(this).is(':checked');
	var setAdminUrl;
	if (enable == true) {
		setAdminUrl = applicationUrl + 'permissions/setadmin/' + userId;
	} else {
		setAdminUrl = applicationUrl + 'permissions/remadmin/' + userId;
	}
	$.get(setAdminUrl).done(function(data) {
		var permissionsArea = $('#permissionsArea');
		permissionsArea.replaceWith(data);
	}).fail(function(data) {
		console.log(data);
		alert('Viga!');
	});
});

function deleteDatasetPermission(datasetPermId) {
	var deleteDatasetPermUrl = applicationUrl + 'permissions/deletedatasetperm/' + datasetPermId;
	$.get(deleteDatasetPermUrl).done(function(data) {
		var permissionsArea = $('#permissionsArea');
		permissionsArea.replaceWith(data);
	}).fail(function(data) {
		console.log(data);
		alert('Viga!');
	});
}

function openAddDatasetPermissionDlg(elem) {
	var userId = $(elem).data('id');
	var addDlg = $($(elem).data('target'));
	$("#datasetPermUserId").val(userId);
}