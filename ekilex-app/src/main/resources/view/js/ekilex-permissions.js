$(function() {

	$.fn.userEnableCheck = function() {
		var main = $(this);
		main.on('click', function(e) {
			e.preventDefault();
			openWaitDlg();
			var userId = main.data('id');
			var checked = main.is(':checked');
			var userEnableUrl;
			if (checked == true) {
				userEnableUrl = applicationUrl + 'permissions/enable/' + userId;
			} else {
				userEnableUrl = applicationUrl + 'permissions/disable/' + userId;
			}
			$.get(userEnableUrl).done(function(data) {
				var permissionsArea = $('#permissionsArea');
				permissionsArea.replaceWith(data);
				$wpm.bindObjects();
			}).fail(function(data) {
				console.log(data);
				openAlertDlg(messages["common.error"]);
			}).always(function() {
				closeWaitDlg();
			});
		});
	}

	$.fn.userApiCrudCheck = function() {
		var main = $(this);
		main.on('click', function(e) {
			e.preventDefault();
			openWaitDlg();
			var userId = main.data('id');
			var checked = main.is(':checked');
			var userEnableUrl;
			if (checked == true) {
				userEnableUrl = applicationUrl + 'permissions/setapicrud/' + userId;
			} else {
				userEnableUrl = applicationUrl + 'permissions/remapicrud/' + userId;
			}
			$.get(userEnableUrl).done(function(data) {
				var permissionsArea = $('#permissionsArea');
				permissionsArea.replaceWith(data);
				$wpm.bindObjects();
			}).fail(function(data) {
				console.log(data);
				openAlertDlg(messages["common.error"]);
			}).always(function() {
				closeWaitDlg();
			});
		});
	}

	$.fn.userAdminCheck = function() {
		var main = $(this);
		main.on('click', function(e) {
			e.preventDefault();
			openWaitDlg();
			var userId = main.data('id');
			var checked = main.is(':checked');
			var setAdminUrl;
			if (checked == true) {
				setAdminUrl = applicationUrl + 'permissions/setadmin/' + userId;
			} else {
				setAdminUrl = applicationUrl + 'permissions/remadmin/' + userId;
			}
			$.get(setAdminUrl).done(function(data) {
				var permissionsArea = $('#permissionsArea');
				permissionsArea.replaceWith(data);
				$wpm.bindObjects();
			}).fail(function(data) {
				console.log(data);
				openAlertDlg(messages["common.error"]);
			}).always(function() {
				closeWaitDlg();
			});
		});
	}

	$.fn.userMasterCheck = function() {
		var main = $(this);
		main.on('click', function(e) {
			e.preventDefault();
			openWaitDlg();
			var userId = main.data('id');
			var checked = main.is(':checked');
			var setMasterUrl;
			if (checked == true) {
				setMasterUrl = applicationUrl + 'permissions/setmaster/' + userId;
			} else {
				setMasterUrl = applicationUrl + 'permissions/remmaster/' + userId;
			}
			$.get(setMasterUrl).done(function(data) {
				var permissionsArea = $('#permissionsArea');
				permissionsArea.replaceWith(data);
				$wpm.bindObjects();
			}).fail(function(data) {
				console.log(data);
				openAlertDlg(messages["common.error"]);
			}).always(function() {
				closeWaitDlg();
			});
		});
	}

	$.fn.applicationReviewedCheck = function() {
		var main = $(this);
		main.on('click', function(e) {
			e.preventDefault();
			openWaitDlg();
			var applicationId = main.data('application-id');
			var checked = main.is(':checked');
			var setReviewedUrl;
			if (checked == true) {
				setReviewedUrl = applicationUrl + 'permissions/setapplicationreviewed/' + applicationId;
			} else {
				setReviewedUrl = applicationUrl + 'permissions/remapplicationreviewed/' + applicationId;
			}
			$.get(setReviewedUrl).done(function(data) {
				var permissionsArea = $('#permissionsArea');
				permissionsArea.replaceWith(data);
				$wpm.bindObjects();
			}).fail(function(data) {
				console.log(data);
				openAlertDlg(messages["common.error"]);
			}).always(function() {
				closeWaitDlg();
			});
		});
	}

	$.fn.permDatasetCodeSelect = function() {
		var main = $(this);
		main.on('change', function(e) {
			e.preventDefault();
			var datasetCode = main.val();
			var $languages = main.closest('form').find('[name="authLang"]');
			let getDatasetLanguagesUrl = applicationUrl + 'permissions/dataset_languages/' + datasetCode;
			$.get(getDatasetLanguagesUrl).done(function(response) {
				$languages.empty();
				$languages.append($("<option value=''></option>"));
				var datasetLanguages = JSON.parse(response);
				$.each(datasetLanguages, function(index, language) {
					$languages.append($("<option></option>")
						.attr("value", language.code)
						.text(language.value));
				});
				$wpm.bindObjects();
			}).fail(function(response) {
				console.log(response);
				openAlertDlg(messages["common.error"]);
			});
		});
	}

	$.fn.addDatasetPermPlugin = function() {
		var main = $(this);
		main.on('click', function(e) {
			e.preventDefault();
			openWaitDlg();
			var form = main.closest('form');
			var modalId = main.closest('.modal').attr('id');
			$.ajax({
				url: form.attr('action'),
				data: form.serialize(),
				method: 'POST',
			}).done(function(data) {
				$('#' + modalId).modal('hide');
				var permissionsArea = $('#permissionsArea');
				permissionsArea.replaceWith(data);
				$wpm.bindObjects();
			}).fail(function(data) {
				$('#' + modalId).modal('hide');
				console.log(data);
				openAlertDlg(messages["common.error"]);
			}).always(function() {
				closeWaitDlg();
			});
		});
	}

	$.fn.editPermReviewCommentPlugin = function() {
		var main = $(this);
		main.on('click', function(e) {
			e.preventDefault();
			openWaitDlg();
			var form = main.closest('form');
			var modalId = main.closest('.modal').attr('id');
			$.ajax({
				url: form.attr('action'),
				data: form.serialize(),
				method: 'POST',
			}).done(function(data) {
				$('#' + modalId).modal('hide');
				var permissionsArea = $('#permissionsArea');
				permissionsArea.replaceWith(data);
				$wpm.bindObjects();
			}).fail(function(data) {
				$('#' + modalId).modal('hide');
				console.log(data);
				openAlertDlg(messages["common.error"]);
			}).always(function() {
				closeWaitDlg();
			});
		});
	}

	$.fn.deletePermReviewCommentPlugin = function() {
		var main = $(this);
		main.on('click', function(e) {
			e.preventDefault();
			openWaitDlg();
			var userId = main.data('id');
			var deleteReviewCommentUrl = applicationUrl + 'permissions/deletereviewcomment/' + userId;
			$.get(deleteReviewCommentUrl).done(function(data) {
				var permissionsArea = $('#permissionsArea');
				permissionsArea.replaceWith(data);
				$wpm.bindObjects();
			}).fail(function(data) {
				console.log(data);
				openAlertDlg(messages["common.error"]);
			}).always(function() {
				closeWaitDlg();
			});
		});
	}
});

function deleteDatasetPermission(datasetPermId) {
	openWaitDlg();
	var deleteDatasetPermUrl = applicationUrl + 'permissions/deletedatasetperm/' + datasetPermId;
	$.get(deleteDatasetPermUrl).done(function(data) {
		var permissionsArea = $('#permissionsArea');
		permissionsArea.replaceWith(data);
		$wpm.bindObjects();
	}).fail(function(data) {
		console.log(data);
		openAlertDlg(messages["common.error"]);
	}).always(function() {
		closeWaitDlg();
	});
};

function sendPermissionsEmail(userEmail) {
	let sendPermissionsEmailUrl = applicationUrl + 'permissions/sendpermissionsemail/' + userEmail;
	$.get(sendPermissionsEmailUrl).done(function(response) {
		if (response === "OK") {
			openMessageDlg(messages["permissions.email.send.success"]);
		} else {
			console.log(response);
			openAlertDlg(messages["permissions.email.send.fail"]);
		}
	}).fail(function(response) {
		console.log(response);
		openAlertDlg(messages["common.error"]);
	});
};