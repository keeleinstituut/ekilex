$(function() {

	$.fn.initAddMeaningMediaPlugin = function() {
		return this.each(function() {
			const modal = $(this);

			modal.on('show.bs.modal', function(e) {
				resetModal();
				initGenericTextAddDlg(modal);
				alignAndFocus(e, modal);
			});

			const fileInput = modal.find('[data-name=meaning-media-file-input]');
			const chooseFileBtn = modal.find('[data-name=meaning-media-choose-file-btn]');
			const chooseFileDiv = chooseFileBtn.parent();
			const thumbnailDiv = modal.find('[data-name=meaning-media-thumbnail-div]');
			const thumbnail = modal.find('[data-name=meaning-media-upload-thumbnail]');
			const deleteButtons = modal.find('[data-name=meaning-media-delete-buttons]');
			const urlInput = modal.find('input[name=url]');
			const objectFilenameInput = modal.find('input[name=objectFilename]');
			const uploadErrorBlock = modal.find('[data-id=meaning-media-upload-error]');
			let objectFilename = null;

			function showUploadError(errorMessage) {
				uploadErrorBlock.text(errorMessage).removeClass('d-none');
			}

			function hideUploadError() {
				uploadErrorBlock.text('').addClass('d-none');
			}

			function resetModal() {
				objectFilename = null;
				urlInput.val('').prop('readonly', false);
				objectFilenameInput.val('');
				hideUploadError();
				chooseFileDiv.show();
				thumbnailDiv.hide();
				thumbnail.attr('src', '');
				deleteButtons.hide();
				fileInput.val('');
			}

			function replaceMediaFile() {
				deleteMediaFile(true);
			}

			function deleteMediaFile(isFileReplace) {
				if (!objectFilename) {
					resetModal();
					return;
				}

				$.ajax({
					url: applicationUrl + 'delete_media_file',
					method: 'POST',
					data: {objectFilename: objectFilename}
				}).done(function(response) {
					if (response.status === "OK") {
						resetModal();
						if (isFileReplace) {
							fileInput[0].click();
						}
					} else if (response.status === "ERROR") {
						showUploadError(response.message);
					}
				}).fail(function(data) {
					console.log(data);
					showUploadError(messages["common.error"]);
				});
			}

			chooseFileBtn.off('click').on('click', function() {
				fileInput[0].click();
			});

			fileInput.off('change').on('change', function() {
				const file = this.files && this.files[0];
				if (!file) {
					return;
				}
				const formData = new FormData();
				formData.append('file', file);

				$.ajax({
					url: applicationUrl + 'upload_media_file',
					method: 'POST',
					data: formData,
					processData: false,
					contentType: false
				}).done(function(response) {
					if (response.status === "OK") {
						objectFilename = response.objectFilename;
						objectFilenameInput.val(objectFilename);
						urlInput.val(response.url).prop('readonly', true);
						thumbnail.attr('src', response.url);

						chooseFileDiv.hide();
						thumbnailDiv.show();
						deleteButtons.show();
						hideUploadError();
					} else if (response.status === "ERROR") {
						console.log(response.detailMessage);
						showUploadError(response.message);
					}
				}).fail(function(data) {
					console.log(data);
					showUploadError(messages["common.error"]);
				});
				fileInput.val('');
			});

			modal.find('[data-name=meaning-media-delete-file-btn]').off('click').on('click', function() {
				deleteMediaFile();
			});

			modal.find('[data-name=meaning-media-replace-file-btn]').off('click').on('click', function() {
				replaceMediaFile();
			});
		});
	}

	$.fn.initEditMeaningMediaPlugin = function() {
		return this.each(function() {
			const modal = $(this);
			modal.on('show.bs.modal', function(e) {
				initGenericTextEditDlg(modal);
				alignAndFocus(e, modal);

				const urlInput = modal.find('input[name=url]');
				const isObjectFilename = modal.attr('data-is-object-filename') === 'true';
				if (isObjectFilename) {
					urlInput.prop('readonly', true);
				} else {
					urlInput.prop('readonly', false);
				}
			});
		});
	}

});