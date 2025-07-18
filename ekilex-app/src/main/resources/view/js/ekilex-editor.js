// Javascript methods for Ekilex custom editor component and dialogs are using it

function initUsageMemberDlg(dlg) {
	let usageMemberType = $('#usageMemberTypeSelect').find(':selected').val();
	toggleUsageMemberAdditionalFields(dlg, usageMemberType);

	dlg.find('[name=opCode]').off('change').on('change', function(e) {
		let usageMemberType = $(e.target).val();
		toggleUsageMemberAdditionalFields(dlg, usageMemberType);
	});
}

function toggleUsageMemberAdditionalFields(dlg, usageMemberType) {
	dlg.find('.usage-member-additional-fields').hide();
	dlg.find('[data-id=' + usageMemberType + ']').show();
}

function initEkiEditorDlg(editDlg, editorOptions) {
	let editFld = editDlg.find('[data-id="editFld"]');
	let valueInput = editDlg.find('[name=value]');
	let footer = editDlg.find('.modal-footer');
	let cancelBtn = footer.find('[data-dismiss=modal]');
	let errorText = messages["editor.error.add.note"];
	let errorTemplate = '<span class="error-text">' + errorText + '</span>';
	editFld.val(valueInput.val());

	initCkEditor(editFld, editorOptions);

	cancelBtn.off('click').on('click', function() {
		if (errorTemplate) {
			footer.find('.error-text').remove();
		};
	});

	editDlg.find('button[type="submit"]').off('click').on('click', function(e) {
		if (editFld.val()) {
			let editFldValue = editFld.val();
			editFldValue = cleanEkiEditorValue(editFldValue);
			valueInput.val(editFldValue);
			footer.find('.error-text').remove();
			editFld.removeClass('is-invalid');
			submitDialog(e, editDlg, messages["common.data.update.error"]);
		} else {
			e.preventDefault();
			editFld.addClass('is-invalid');
			footer.prepend(errorTemplate);
		}
	});
};


function initMultipleEkiEditorDlg(editDlg, editorOptions) {
  // Get all editor fields and store them with their respective value fields
  const editFields = editDlg
    .find("[data-editor-field]")
    .toArray()
    .reduce((acc, editorField) => {
      const editorFieldId = editorField.getAttribute("data-id");
      const valueField = editDlg.find(`[name="${editorFieldId}"]`);
      if (valueField.length) {
        // Editor field will have to be a jquery object for ckeditor
        acc.push({ editorField: $(editorField), valueField });
      } else {
        console.error(
          `Could not find a matching value field for ${editorFieldId}`
        );
      }
      return acc;
    }, []);
  let footer = editDlg.find(".modal-footer");
  let cancelBtn = footer.find("[data-dismiss=modal]");
  let errorText = messages["editor.error.add.note"];
  let errorTemplate = '<span class="error-text">' + errorText + "</span>";
  // Init ckeditor for each field
  editFields.forEach(({ editorField, valueField }) => {
    editorField.val(valueField.val());
    initCkEditor(editorField, editorOptions);
  });

  cancelBtn.off("click").on("click", function () {
    if (errorTemplate) {
      footer.find(".error-text").remove();
    }
  });

  editDlg
    .find('button[type="submit"]')
    .off("click")
    .on("click", function (e) {
      const areValuesFilled = editFields.every(({ editorField }) => {
        if (editorField.val() || editorField.data("optional")) {
          editorField.removeClass("is-invalid");
          return true;
        } else {
          editorField.addClass("is-invalid");
          return false;
        }
      });
      if (areValuesFilled) {
        editFields.forEach(({ editorField, valueField }) => {
          const cleanedValue = cleanEkiEditorValue(editorField.val());
          valueField.val(cleanedValue);
        });
        footer.find(".error-text").remove();
        submitDialog(e, editDlg, messages["common.data.update.error"]);
      } else {
        e.preventDefault();
        footer.prepend(errorTemplate);
      }
    });
};

function cleanEkiEditorValue(editFldValue) {

	// Remove empty values, eki-selected class and empty elements.
	return editFldValue
		.replace("<br>", "")
		.replaceAll("&nbsp;", " ")
		.replaceAll('class="eki-selected"', '')
		.replace(/<[^/>][^>]*><\/[^>]+>/gm, '');
}
