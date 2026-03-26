$(function () {
  function getMediaElements(modal) {
    const urlPanel = modal.find('[data-segment-panel="url"]');
    return {
      objectFilenameInput: modal.find(
        'input[type="hidden"][name=objectFilename]',
      ),
      hiddenUrlInput: modal.find('input[type="hidden"][name=url]'),
      visibleUrlInput: urlPanel.find(".input-text-new"),
      thumbnail: modal.find("[data-media-thumbnail]"),
      uploadErrorBlock: modal.find("[data-error-container]"),
      segmentContainer: modal.find("[data-segment-container]"),
    };
  }

  function initMediaSegmentHandler(elements, mediaInputApi) {
    elements.segmentContainer
      .off("segment:change.media")
      .on("segment:change.media", function (e, value) {
        if (value === "url") {
          elements.objectFilenameInput.val("");
          if (mediaInputApi) mediaInputApi.reset();
          if (elements.visibleUrlInput.val()) {
            elements.hiddenUrlInput.val(elements.visibleUrlInput.val());
            elements.thumbnail.attr("src", elements.visibleUrlInput.val());
            elements.thumbnail.removeClass("d-none");
          } else {
            elements.hiddenUrlInput.val("");
            elements.thumbnail.addClass("d-none");
            elements.thumbnail.attr("src", "");
          }
        }
        if (value === "file") {
          elements.visibleUrlInput.val("");
          elements.hiddenUrlInput.val("");
          if (elements.objectFilenameInput.val()) {
            elements.thumbnail.removeClass("d-none");
          } else {
            elements.thumbnail.addClass("d-none");
            elements.thumbnail.attr("src", "");
          }
        }
      });

    elements.visibleUrlInput
      .off("input.mediaUrl")
      .on("input.mediaUrl", function () {
        const val = $(this).val();
        elements.hiddenUrlInput.val(val);
        if (val) {
          elements.thumbnail.attr("src", val);
          elements.thumbnail.removeClass("d-none");
        } else {
          elements.thumbnail.attr("src", "");
          elements.thumbnail.addClass("d-none");
        }
      });
  }

  function deleteMediaFile(objectFilename) {
    $.ajax({
      url: applicationUrl + "delete_media_file",
      method: "POST",
      data: { objectFilename: objectFilename },
    }).fail(function (data) {
      console.error(data);
    });
  }

  function createMediaUploadCallbacks(elements, mediaInputApi) {
    return function () {
      const file = this.files?.[0];
      if (!file) return;

      if (elements.objectFilenameInput.val()) {
        deleteMediaFile(elements.objectFilenameInput.val());
      }

      const formData = new FormData();
      formData.append("file", file);

      $.ajax({
        url: applicationUrl + "upload_media_file",
        method: "POST",
        data: formData,
        processData: false,
        contentType: false,
      })
        .done(function (response) {
          if (response.status === "OK") {
            elements.objectFilenameInput.val(response.objectFilename);
            elements.hiddenUrlInput.val(response.url);
            mediaInputApi.setFileName(response.objectFilename);
            elements.thumbnail.attr("src", response.url);
            elements.thumbnail.removeClass("d-none");
            elements.uploadErrorBlock.text("");
          } else if (response.status === "ERROR") {
            console.log(response.detailMessage);
            elements.uploadErrorBlock.text(response.message);
          }
        })
        .fail(function (data) {
          console.log(data);
          elements.uploadErrorBlock.text(messages["common.error"]);
        });

      mediaInputApi.fileInput.val("");
    };
  }

  $.fn.initAddMeaningMediaPlugin = function () {
    return this.each(function () {
      const modal = $(this);

      modal.on("show.bs.modal", function (e) {
        const elements = getMediaElements(modal);

        // Reset form state
        elements.objectFilenameInput.val("");
        elements.hiddenUrlInput.val("");
        elements.visibleUrlInput.val("");
        elements.thumbnail.attr("src", "");
        elements.uploadErrorBlock.text("").addClass("d-none");

        // Reset to file mode
        const fileRadio = modal.find(
          'input[name="mediaSourceType"][value="file"]',
        );
        fileRadio.prop("checked", true);

        // Init generic components
        initSegmentedSwitch(modal);
        const mediaInputApi = initMediaInput(modal);
        if (mediaInputApi) {
          mediaInputApi.reset();
          mediaInputApi.fileInput
            .off("change.mediaUpload")
            .on(
              "change.mediaUpload",
              createMediaUploadCallbacks(elements, mediaInputApi),
            );
        }

        initMediaSegmentHandler(elements, mediaInputApi);

        initGenericTextAddDlg(modal);
        alignAndFocus(e, modal);
      });
    });
  };

  $.fn.initEditMeaningMediaPlugin = function () {
    return this.each(function () {
      const modal = $(this);

      modal.on("show.bs.modal", function (e) {
        const els = getMediaElements(modal);

        // Init generic components
        initSegmentedSwitch(modal);
        const mediaInputApi = initMediaInput(modal);
        if (mediaInputApi) {
          mediaInputApi.fileInput
            .off("change.mediaUpload")
            .on(
              "change.mediaUpload",
              createMediaUploadCallbacks(els, mediaInputApi),
            );
        }

        // Media-specific segment + URL handlers
        initMediaSegmentHandler(els, mediaInputApi);

        initGenericTextEditDlg(modal);
        alignAndFocus(e, modal);
      });
    });
  };
});
