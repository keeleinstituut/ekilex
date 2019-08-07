$(document).on("click", ":input[name='userEnableCheck']", function() {
	var userId = $(this).data('id');
	var enable = $(this).is(':checked');
	var orderBy = $(this).data('order-by');
	var userEnableUrl;
	if (enable == true) {
		userEnableUrl = applicationUrl + 'permissions/enable/' + userId + '/' + orderBy;
	} else {
		userEnableUrl = applicationUrl + 'permissions/disable/' + userId + '/' + orderBy;
	}
	$.get(userEnableUrl).done(function(data) {
		var permissionsArea = $('#permissionsArea');
		permissionsArea.replaceWith(data);
	}).fail(function(data) {
		console.log(data);
		openAlertDlg('Viga!');
	});
});

$(document).on("click", ":input[name='userAdminCheck']", function() {
	var userId = $(this).data('id');
	var enable = $(this).is(':checked');
	var orderBy = $(this).data('order-by');
	var setAdminUrl;
	if (enable == true) {
		setAdminUrl = applicationUrl + 'permissions/setadmin/' + userId + '/' + orderBy;
	} else {
		setAdminUrl = applicationUrl + 'permissions/remadmin/' + userId + '/' + orderBy;
	}
	$.get(setAdminUrl).done(function(data) {
		var permissionsArea = $('#permissionsArea');
		permissionsArea.replaceWith(data);
	}).fail(function(data) {
		console.log(data);
		openAlertDlg('Viga!');
	});
});

$(document).on("click", ":input[name='userReviewedCheck']", function() {
	var userId = $(this).data('id');
	var reviewed = $(this).is(':checked');
	var orderBy = $(this).data('order-by');
	var setReviewedUrl;
	if (reviewed == true) {
		setReviewedUrl = applicationUrl + 'permissions/setreviewed/' + userId + '/' + orderBy;
	} else {
		setReviewedUrl = applicationUrl + 'permissions/remreviewed/' + userId + '/' + orderBy;
	}
	$.get(setReviewedUrl).done(function(data) {
		var permissionsArea = $('#permissionsArea');
		permissionsArea.replaceWith(data);
	}).fail(function(data) {
		console.log(data);
		openAlertDlg('Viga!');
	});
});

$(document).on("change", ".perm-dataset-code", function() {
	var datasetCode = $(this).val();
	var $languages = $(this).closest('form').find('[name="authLang"]');

	let getDatasetLanguagesUrl = applicationUrl + 'permissions/dataset_languages/' + datasetCode;
	$.get(getDatasetLanguagesUrl).done(function (response) {
		$languages.empty();
		$languages.append($("<option value=''></option>"));
		var datasetLanguages = JSON.parse(response);
		$.each(datasetLanguages, function (index, language) {
			$languages.append($("<option></option>")
				.attr("value", language.code).text(language.value));
		});

	}).fail(function (response) {
		console.log(response);
		openAlertDlg("Andmekogu keelte päring ebaõnnestus");
	});
});

function deleteDatasetPermission(datasetPermId, orderBy) {
	var deleteDatasetPermUrl = applicationUrl + 'permissions/deletedatasetperm/' + datasetPermId + '/' + orderBy;
	$.get(deleteDatasetPermUrl).done(function(data) {
		var permissionsArea = $('#permissionsArea');
		permissionsArea.replaceWith(data);
	}).fail(function(data) {
		console.log(data);
		openAlertDlg('Viga!');
	});
}

function sendPermissionsEmail(userEmail) {
	let sendPermissionsEmailUrl = applicationUrl + 'permissions/sendpermissionsemail/' + userEmail;
	$.get(sendPermissionsEmailUrl).done(function (response) {
		if (response === "OK") {
			openMessageDlg("Kiri saadetud");
		} else {
			console.log(response);
			openAlertDlg("Kirja saatmine ebaõnnestus");
		}
	}).fail(function (response) {
		console.log(response);
		openAlertDlg("Kirja saatmine ebaõnnestus");
	});
}