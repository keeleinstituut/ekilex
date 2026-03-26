$(function() {
	function getImageElements(modal) {
		const urlPanel = modal.find('[data-segment-panel="url"]');
		return {
			objectFilenameInput: modal.find('input[type="hidden"][name=objectFilename]'),
			hiddenUrlInput: modal.find('input[type="hidden"][name=url]'),
			visibleUrlInput: urlPanel.find('.input-text-new'),
			thumbnailDiv: modal.find('[data-name=meaning-media-thumbnail-div]'),
			thumbnail: modal.find('[data-name=meaning-media-upload-thumbnail]'),
			uploadErrorBlock: modal.find('[data-id=meaning-media-upload-error]'),
			container: modal.find('[data-segment-container]')
		};
	}

	function initImageSegmentHandler(els, mediaInputApi) {
		els.container.off('segment:change.image').on('segment:change.image', function(e, value) {
			if (value === 'url') {
				els.objectFilenameInput.val('');
				if (mediaInputApi) mediaInputApi.reset();
				if (els.visibleUrlInput.val()) {
					els.hiddenUrlInput.val(els.visibleUrlInput.val());
					els.thumbnail.attr('src', els.visibleUrlInput.val());
					els.thumbnailDiv.show();
				} else {
					els.hiddenUrlInput.val('');
					els.thumbnailDiv.hide();
					els.thumbnail.attr('src', '');
				}
			}
			if (value === 'file') {
				els.visibleUrlInput.val('');
				els.hiddenUrlInput.val('');
				if (els.objectFilenameInput.val()) {
					els.thumbnailDiv.show();
				} else {
					els.thumbnailDiv.hide();
					els.thumbnail.attr('src', '');
				}
			}
		});

		els.visibleUrlInput.off('input.imageUrl').on('input.imageUrl', function() {
			const val = $(this).val();
			els.hiddenUrlInput.val(val);
			if (val) {
				els.thumbnail.attr('src', val);
				els.thumbnailDiv.show();
			} else {
				els.thumbnail.attr('src', '');
				els.thumbnailDiv.hide();
			}
		});
	}

	function createImageUploadCallbacks(els, mediaInputApi) {
		return function() {
			const file = this.files && this.files[0];
			if (!file) return;

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
					els.objectFilenameInput.val(response.objectFilename);
					els.hiddenUrlInput.val(response.url);
					mediaInputApi.setFileName(response.objectFilename);
					els.thumbnail.attr('src', response.url);
					els.thumbnailDiv.show();
					els.uploadErrorBlock.text('').addClass('d-none');
				} else if (response.status === "ERROR") {
					console.log(response.detailMessage);
					els.uploadErrorBlock.text(response.message).removeClass('d-none');
				}
			}).fail(function(data) {
				console.log(data);
				els.uploadErrorBlock.text(messages["common.error"]).removeClass('d-none');
			});

			mediaInputApi.fileInput.val('');
		};
	}

	$.fn.initAddMeaningImagePlugin = function() {
		return this.each(function() {
			const modal = $(this);

			modal.on('show.bs.modal', function(e) {
				const els = getImageElements(modal);

				// Reset form state
				els.objectFilenameInput.val('');
				els.hiddenUrlInput.val('');
				els.visibleUrlInput.val('');
				els.thumbnailDiv.hide();
				els.thumbnail.attr('src', '');
				els.uploadErrorBlock.text('').addClass('d-none');

				// Reset to file mode
				const fileRadio = modal.find('input[name="imageSourceType"][value="file"]');
				fileRadio.prop('checked', true);
				modal.find('input[name="imageSourceType"]').attr('tabindex', '-1');
				fileRadio.attr('tabindex', '0');

				// Init generic components
				initSegmentedSwitch(modal);
				const mediaInputApi = initMediaInput(modal);
				if (mediaInputApi) {
					mediaInputApi.reset();
					mediaInputApi.fileInput.off('change.imageUpload').on('change.imageUpload',
						createImageUploadCallbacks(els, mediaInputApi));
				}

				// Image-specific segment + URL handlers
				initImageSegmentHandler(els, mediaInputApi);

				initGenericTextAddDlg(modal);
				alignAndFocus(e, modal);
			});
		});
	}

	$.fn.initEditMeaningImagePlugin = function() {
		return this.each(function() {
			const modal = $(this);

			modal.on('show.bs.modal', function(e) {
				const els = getImageElements(modal);

				// Init generic components
				initSegmentedSwitch(modal);
				const mediaInputApi = initMediaInput(modal);
				if (mediaInputApi) {
					mediaInputApi.fileInput.off('change.imageUpload').on('change.imageUpload',
						createImageUploadCallbacks(els, mediaInputApi));
				}

				// Image-specific segment + URL handlers
				initImageSegmentHandler(els, mediaInputApi);

				initGenericTextEditDlg(modal);
				alignAndFocus(e, modal);
			});
		});
	}

	$.fn.initAddMeaningMediaPlugin = function() {
		return this.each(function() {
			const modal = $(this);

			modal.on('show.bs.modal', function(e) {
				resetMediaModal();
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

			function resetMediaModal() {
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
					resetMediaModal();
					return;
				}

				$.ajax({
					url: applicationUrl + 'delete_media_file',
					method: 'POST',
					data: {objectFilename: objectFilename}
				}).done(function(response) {
					if (response.status === "OK") {
						resetMediaModal();
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