$(function () {
  const Inline = Quill.import("blots/inline");
  class BoldBlot extends Inline {
    static blotName = "bold";
    static tagName = "eki-highlight";
  }

  class ItalicBlot extends Inline {
    static blotName = "italic";
    static tagName = "eki-foreign";
  }

  class StressBlot extends Inline {
    static blotName = "stress";
    static tagName = "eki-stress";
  }

  class MetaBlot extends Inline {
    static blotName = "meta";
    static tagName = "eki-meta";
  }

  Quill.register(BoldBlot, true);
  Quill.register(ItalicBlot, true);
  Quill.register(StressBlot, true);
  Quill.register(MetaBlot, true);
});

function toggleFormatVisualState(dlg, editor) {
  const formatButtons = dlg.find("[data-format]");
  formatButtons.removeClass("ql-active");
  const selection = editor.getSelection();
  if (!selection) {
    return;
  }
  const formats = editor.getFormat(selection.index, selection.length);
  formatButtons.each(function () {
    const format = this.getAttribute("data-format");
    if (formats[format]) {
      this.classList.add("ql-active");
    }
  });
}

function initQuillDlg(dlg, options = {}) {
  const container = dlg.find("[data-quill-container]").get(0);
  const toolbar = dlg
    .find(
      `[data-quill-toolbar="${container.getAttribute("data-quill-container")}"]`
    )
    .get(0);

  const editor = new Quill(container, {
    theme: "snow",
    modules: {
      toolbar: {
        container: toolbar,
      },
    },
  });
  dlg.find("[data-format]").on("click", function () {
    const selection = editor.getSelection();
    if (!selection) {
      return;
    }
    const format = this.getAttribute("data-format");
    const currentValue = editor.getFormat(selection.index, selection.length)[
      format
    ];
    editor.format(format, !currentValue);
    toggleFormatVisualState(dlg, editor);
  });
  editor.on("selection-change", function () {
    toggleFormatVisualState(dlg, editor);
  });
  return editor;
}

function setQuillContent(editor, content) {
  editor.root.innerHTML = `<p>${content}</p>`;
}

$.fn.initQuill = function () {
  return this.each(function () {
    const obj = $(this);
    initQuillDlg(obj);
  });
};

$.fn.initMultipleQuillEditorDlgPlugin = function () {
  return this.each(function () {
    const obj = $(this);
    obj.on("show.bs.modal", function () {
      initMultipleQuillEditorDlg(obj);
    });
  });
};

$.fn.initMultipleQuillEditorDlgAndFocusPlugin = function () {
  return this.each(function () {
    const obj = $(this);
    obj.on("show.bs.modal", function (e) {
      initMultipleQuillEditorDlg(obj);
      alignAndFocus(e, obj);
    });
  });
};

function initMultipleQuillEditorDlg(editDlg, editorOptions = {}) {
  // Get all editor fields and store them with their respective value fields
  const editFields = editDlg
    .find("[data-editor-field]")
    .toArray()
    .reduce((acc, editorField) => {
      const editorFieldId = editorField.getAttribute("data-id");
      const valueField = editDlg.find(`[name="${editorFieldId}"]`);
      if (valueField.length) {
        // Editor field will have to be a jquery object for ckeditor
        acc.push({
          valueField,
          container: editDlg,
        });
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
  editFields.forEach(({ valueField, container }) => {
    const editor = initQuillDlg(container, editorOptions);
    setQuillContent(editor, valueField.val());
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
}
