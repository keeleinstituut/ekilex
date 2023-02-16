$(function() {

	$.fn.userEnableCheck = function() {
		const main = $(this);
		main.on('click', function(e) {
			e.preventDefault();
			openWaitDlg();
			const userId = main.data('id');
			const checked = main.is(':checked');
			let userEnableUrl;
			if (checked) {
				userEnableUrl = `${applicationUrl}permissions/enable/${userId}`;
			} else {
				userEnableUrl = `${applicationUrl}permissions/disable/${userId}`;
			}
			submitUrlAndUpdatePermissionsData(userEnableUrl);
		});
	}

	$.fn.userApiCrudCheck = function() {
		const main = $(this);
		main.on('click', function(e) {
			e.preventDefault();
			openWaitDlg();
			const userId = main.data('id');
			const checked = main.is(':checked');
			let userEnableUrl;
			if (checked) {
				userEnableUrl = `${applicationUrl}permissions/setapicrud/${userId}`;
			} else {
				userEnableUrl = `${applicationUrl}permissions/remapicrud/${userId}`;
			}
			submitUrlAndUpdatePermissionsData(userEnableUrl);
		});
	}

	$.fn.userAdminCheck = function() {
		const main = $(this);
		main.on('click', function(e) {
			e.preventDefault();
			openWaitDlg();
			const userId = main.data('id');
			const checked = main.is(':checked');
			let setAdminUrl;
			if (checked) {
				setAdminUrl = `${applicationUrl}permissions/setadmin/${userId}`;
			} else {
				setAdminUrl = `${applicationUrl}permissions/remadmin/${userId}`;
			}
			submitUrlAndUpdatePermissionsData(setAdminUrl);
		});
	}

	$.fn.userMasterCheck = function() {
		const main = $(this);
		main.on('click', function(e) {
			e.preventDefault();
			openWaitDlg();
			const userId = main.data('id');
			const checked = main.is(':checked');
			let setMasterUrl;
			if (checked) {
				setMasterUrl = `${applicationUrl}permissions/setmaster/${userId}`;
			} else {
				setMasterUrl = `${applicationUrl}permissions/remmaster/${userId}`;
			}
			submitUrlAndUpdatePermissionsData(setMasterUrl);
		});
	}

	$.fn.permDatasetCodeSelect = function() {
		const main = $(this);
		main.on('change', function(e) {
			e.preventDefault();
			const datasetCode = main.val();
			const $languages = main.closest('form').find('[name="authLang"]');
			const getDatasetLanguagesUrl = `${applicationUrl}permissions/dataset_languages/${datasetCode}`;
			$.get(getDatasetLanguagesUrl).done(function(response) {
				$languages.empty();
				$languages.append($("<option></option>")
					.attr("value", '')
					.attr("selected", '')
					.text(messages["common.all.languages"]));
				const datasetLanguages = JSON.parse(response);
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
		const main = $(this);
		main.on('click', function(e) {
			e.preventDefault();
			const form = main.closest('form');
			const modalId = main.closest('.modal').attr('id');
			const isValid = checkRequiredFields(form);
			if (isValid) {
				openWaitDlg();
				$.ajax({
					url: form.attr('action'),
					data: form.serialize(),
					method: 'POST',
				}).done(function(data) {
					$(`#${modalId}`).modal('hide');
					$('#permissionsArea').replaceWith(data);
					$wpm.bindObjects();
				}).fail(function(data) {
					$(`#${modalId}`).modal('hide');
					console.log(data);
					openAlertDlg(messages["common.error"]);
				}).always(function() {
					closeWaitDlg();
				});
			}
		});
	}

	$.fn.editPermReviewCommentPlugin = function() {
		const main = $(this);
		main.on('click', function(e) {
			e.preventDefault();
			openWaitDlg();
			const form = main.closest('form');
			const modalId = main.closest('.modal').attr('id');
			$.ajax({
				url: form.attr('action'),
				data: form.serialize(),
				method: 'POST',
			}).done(function(data) {
				$(`#${modalId}`).modal('hide');
				$('#permissionsArea').replaceWith(data);
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
		const main = $(this);
		main.on('click', function(e) {
			e.preventDefault();
			openWaitDlg();
			const userId = main.data('id');
			const deleteReviewCommentUrl = `${applicationUrl}permissions/deletereviewcomment/${userId}`;
			submitUrlAndUpdatePermissionsData(deleteReviewCommentUrl);
		});
	}
});

function deleteDatasetPermission(datasetPermId) {
	openWaitDlg();
	const deleteDatasetPermUrl = `${applicationUrl}permissions/deletedatasetperm/${datasetPermId}`;
	submitUrlAndUpdatePermissionsData(deleteDatasetPermUrl);
};

function sendPermissionsEmail(userEmail) {
	const sendPermissionsEmailUrl = `${applicationUrl}permissions/sendpermissionsemail/${userEmail}`;
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

function rejectApplication(applicationId) {
	openWaitDlg();
	const rejectApplicationUrl = `${applicationUrl}permissions/rejectapplication/${applicationId}`;
	submitUrlAndUpdatePermissionsData(rejectApplicationUrl);
}

function submitUrlAndUpdatePermissionsData(url) {
	$.get(url).done(function(data) {
		$('#permissionsArea').replaceWith(data);
		$wpm.bindObjects();
	}).fail(function(data) {
		console.log(data);
		openAlertDlg(messages["common.error"]);
	}).always(function() {
		closeWaitDlg();
	});
}