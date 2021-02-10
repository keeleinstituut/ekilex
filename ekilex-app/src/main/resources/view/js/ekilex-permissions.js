$(function(){

	$(document).on("click", ":input[name='userEnableCheck']", function() {
		var userId = $(this).data('id');
		var checked = $(this).is(':checked');
		var userEnableUrl;
		if (checked == true) {
			userEnableUrl = applicationUrl + 'permissions/enable/' + userId;
		} else {
			userEnableUrl = applicationUrl + 'permissions/disable/' + userId;
		}
		$.get(userEnableUrl).done(function(data) {
			var permissionsArea = $('#permissionsArea');
			permissionsArea.replaceWith(data);
		}).fail(function(data) {
			console.log(data);
			openAlertDlg('Viga!');
		});
	});

	$(document).on("click", ":input[name='userApiCrudCheck']", function() {
		var userId = $(this).data('id');
		var checked = $(this).is(':checked');
		var userEnableUrl;
		if (checked == true) {
			userEnableUrl = applicationUrl + 'permissions/setapicrud/' + userId;
		} else {
			userEnableUrl = applicationUrl + 'permissions/remapicrud/' + userId;
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
		var checked = $(this).is(':checked');
		var setAdminUrl;
		if (checked == true) {
			setAdminUrl = applicationUrl + 'permissions/setadmin/' + userId;
		} else {
			setAdminUrl = applicationUrl + 'permissions/remadmin/' + userId;
		}
		$.get(setAdminUrl).done(function(data) {
			var permissionsArea = $('#permissionsArea');
			permissionsArea.replaceWith(data);
		}).fail(function(data) {
			console.log(data);
			openAlertDlg('Viga!');
		});
	});
	
	$(document).on("click", ":input[name='userMasterCheck']", function() {
		var userId = $(this).data('id');
		var checked = $(this).is(':checked');
		var setMasterUrl;
		if (checked == true) {
			setMasterUrl = applicationUrl + 'permissions/setmaster/' + userId;
		} else {
			setMasterUrl = applicationUrl + 'permissions/remmaster/' + userId;
		}
		$.get(setMasterUrl).done(function(data) {
			var permissionsArea = $('#permissionsArea');
			permissionsArea.replaceWith(data);
		}).fail(function(data) {
			console.log(data);
			openAlertDlg('Viga!');
		});
	});
	
	$(document).on("click", ":input[name='userReviewedCheck']", function() {
		var userId = $(this).data('id');
		var checked = $(this).is(':checked');
		var setReviewedUrl;
		if (checked == true) {
			setReviewedUrl = applicationUrl + 'permissions/setreviewed/' + userId;
		} else {
			setReviewedUrl = applicationUrl + 'permissions/remreviewed/' + userId;
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
					.attr("value", language.code)
					.text(language.value));
			});
	
		}).fail(function (response) {
			console.log(response);
			openAlertDlg("Andmekogu keelte päring ebaõnnestus");
		});
	});
});

function deleteDatasetPermission(datasetPermId) {
	var deleteDatasetPermUrl = applicationUrl + 'permissions/deletedatasetperm/' + datasetPermId;
	$.get(deleteDatasetPermUrl).done(function(data) {
		var permissionsArea = $('#permissionsArea');
		permissionsArea.replaceWith(data);
	}).fail(function(data) {
		console.log(data);
		openAlertDlg('Viga!');
	});
};

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
};