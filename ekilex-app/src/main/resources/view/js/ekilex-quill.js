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

  class EkiSubBlot extends Inline {
    static blotName = "eki-sub";
    static tagName = "eki-sub";
  }

  class EkiSupBlot extends Inline {
    static blotName = "eki-sup";
    static tagName = "eki-sup";
  }

  class EkiLinkBlot extends Inline {
    static blotName = "ekilink";
    static tagName = "eki-link";

    static create(value) {
      const node = super.create();
      node.setAttribute("data-link-id", value.linkId);
      node.setAttribute("data-link-type", value.linkType);
      return node;
    }

    static formats(node) {
      return {
        linkId: node.getAttribute("data-link-id"),
        linkType: node.getAttribute("data-link-type"),
      };
    }
  }

  class ExtLinkBlot extends Inline {
    static blotName = "extlink";
    static tagName = "ext-link";

    static create(value) {
      const node = super.create();
      node.setAttribute("href", value.href);
      node.setAttribute("target", "ext-link");
      return node;
    }

    static formats(node) {
      return {
        href: node.getAttribute("href"),
      };
    }
  }

  const InlineEmbed = Quill.import("blots/embed");
  class EkiMediaBlot extends InlineEmbed {
    static blotName = "eki-media";
    static tagName = "eki-media";

    static create(value) {
      const node = super.create();
      node.setAttribute("src", value.src);
      node.setAttribute("alt", value.alt || "");
      node.setAttribute("contenteditable", "false");
      return node;
    }

    static value(node) {
      return {
        src: node.getAttribute("src"),
        alt: node.getAttribute("alt"),
      };
    }
  }

  Quill.register(BoldBlot, true);
  Quill.register(ItalicBlot, true);
  Quill.register(StressBlot, true);
  Quill.register(MetaBlot, true);
  Quill.register(EkiSubBlot, true);
  Quill.register(EkiSupBlot, true);
  Quill.register(EkiLinkBlot, true);
  Quill.register(ExtLinkBlot, true);
  Quill.register(EkiMediaBlot, true);
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
    const format = this.getAttribute("data-format");
    // Link button opens the link modal instead of toggling format
    if (format === "link") {
      const quillLink = new QuillLink(editor, dlg);
      quillLink.init();
      return;
    }
    // Remove link button clears ekilink/extlink format from selection
    if (format === "remove-link") {
      removeQuillLink(editor);
      return;
    }
    if (format === "eki-media") {
      const quillMedia = new QuillMedia(editor, dlg);
      quillMedia.init();
      return;
    }
    // Remove selected media
    if (format === "remove-media") {
      removeQuillMedia(editor);
      return;
    }
    if (format === "undo") {
      editor.history.undo();
      return;
    }
    if (format === "redo") {
      editor.history.redo();
      return;
    }
    if (format === "source") {
      toggleSourceView(editor, dlg);
      return;
    }
    // Remove all formatting from selection, or move cursor out of current format
    if (format === "remove-format") {
      const selection = editor.getSelection();
      if (selection && selection.length > 0) {
        editor.removeFormat(selection.index, selection.length);
      } else if (selection) {
        // Insert space and move cursor there to break out of formatting
        const index = selection.index;
        editor.insertText(index, " ");
        editor.removeFormat(index, 1);
        editor.setSelection(index + 1, 0);
      }
      return;
    }
    const selection = editor.getSelection();
    if (!selection) {
      return;
    }
    const currentValue = editor.getFormat(selection.index, selection.length)[
      format
    ];
    editor.format(format, !currentValue);
    toggleFormatVisualState(dlg, editor);
  });
  editor.on("selection-change", function () {
    toggleFormatVisualState(dlg, editor);
  });
  // Enable click-to-select for eki-media elements
  initQuillMediaSelection(editor);
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

class QuillLink {
  constructor(editor, dlg) {
    this.editor = editor;
    this.dlg = dlg;
    this.parent = dlg.find(".modal-content:first");
    this.parentTop = this.parent.css("top");
    this.parentHeight = this.parent.outerHeight();
    this.paths = {
      meaning: {
        api: `${applicationUrl}meaning_internal_link_search`,
        title: "Sisesta otsitav Tähendus/Mõiste",
      },
      word: {
        api: `${applicationUrl}word_internal_link_search`,
        title: "Sisesta otsitav Keelend",
      },
    };
    this.activeID = false;
    this.valid = {
      external: true,
      internal: true,
    };
    // Store selection before opening modal
    this.savedSelection = this.editor.getSelection();
  }

  addTemplate() {
    const template = quillLinkTemplate.replace("{{parentTop}}", this.parentTop);
    this.parent.after((this.linkContent = $(template)));
    this.linkContent.css(
      "marginBottom",
      parseInt(this.parentTop) + this.parentHeight - this.linkContent.outerHeight()
    );

    this.outerLink = {
      title: this.linkContent.find('input[name="title"]:first'),
      url: this.linkContent.find('input[name="url"]:first'),
    };

    this.internalLink = {
      title: this.linkContent.find('input[name="internalTitle"]'),
    };

    const selectedText = this.savedSelection
      ? this.editor.getText(this.savedSelection.index, this.savedSelection.length)
      : "";
    this.outerLink.title.val(selectedText);
    this.internalLink.title.val(selectedText);

    this.roles = this.linkContent.find("[data-linkType]");
    this.internalTypes = this.linkContent.find("[data-internalType]");
    this.results = this.linkContent.find(".results");
    this.activeType = this.roles.eq(0).attr("data-linkType");
    this.internalSearchButton = this.linkContent.find('[data-role="internalSearchButton"]');
    this.changeInternalType("meaning");
    this.changeLayout("external");
  }

  toggle(state) {
    if (state === "show") {
      this.addTemplate();
      this.parent.addClass("size-zero");
    } else {
      this.linkContent.find(".formItem").removeClass("formItem--error");
      this.linkContent.find("input").val("");
      this.changeInternalType("meaning");
      this.changeLayout("external");
      this.linkContent.remove();
      this.parent.removeClass("size-zero");
    }
  }

  bindEvents() {
    this.linkContent.find('[data-role="cancel"]').on("click", (e) => {
      e.preventDefault();
      this.toggle("hide");
    });

    this.linkContent.parents(".modal:first").on("click.quillLink", (e) => {
      if ($(e.target).is(".modal")) {
        this.toggle("hide");
        this.linkContent.parents(".modal:first").off("click.quillLink");
      }
    });

    this.linkContent.find('[data-role="save"]').on("click", (e) => {
      e.preventDefault();
      this.insertLink();
    });

    this.linkContent.find("[data-type]").on("click", (e) => {
      e.preventDefault();
      this.changeLayout($(e.currentTarget).attr("data-type"));
    });

    this.internalTypes.on("click", (e) => {
      e.preventDefault();
      this.changeInternalType($(e.currentTarget).attr("data-internalType"));
    });

    this.linkContent.find('[name="internalSearchValue"]').on("keypress", (e) => {
      const code = e.which || e.keyCode;
      if (code === 13) {
        e.preventDefault();
        this.getSearchResults(this.linkContent.find('[name="internalSearchValue"]').val());
      }
    });

    this.internalSearchButton.on("click", (e) => {
      e.preventDefault();
      this.getSearchResults(this.linkContent.find('[name="internalSearchValue"]').val());
    });
  }

  changeLayout(type) {
    this.activeType = type;
    const buttons = this.linkContent.find("[data-type]");
    buttons.removeClass("active");
    buttons.filter(`[data-type="${type}"]`).not("[data-internalType]").addClass("active");
    this.roles.hide().filter(`[data-linkType="${type}"]`).show();
    this.linkContent.find(".formItem").removeClass("formItem--error");
  }

  changeInternalType(internalType) {
    this.internalType = internalType;
    const buttons = this.linkContent.find("[data-internalType]");
    buttons.removeClass("active");
    buttons.filter(`[data-internalType="${internalType}"]`).addClass("active");
    this.linkContent.find('[data-role="title"]').html(this.paths[internalType].title);
    this.results.empty().hide();
    this.linkContent.find(".formItem").removeClass("formItem--error");
  }

  getSearchResults(value) {
    const data = {
      searchFilter: value,
    };
    this.results.show().html('<div class="loader"><i class="fa fa-3x fa-spinner fa-spin"></i></div>');
    $.ajax({
      url: this.paths[this.internalType].api,
      method: "POST",
      data: JSON.stringify(data),
      contentType: "application/json",
      success: (response) => {
        this.results.html(response).show();
        this.results.find('[name="details-btn"]').removeAttr("name");
        this.bindResults();
      },
    });
  }

  bindResults() {
    const buttons = this.results.find(".list-group-item");
    buttons.on("click", (e) => {
      const obj = $(e.currentTarget);
      const id = obj.find("[data-id]").attr("data-id");
      buttons.removeClass("active");
      obj.addClass("active");
      this.activeID = id;
    });
  }

  validateFields() {
    this.valid.external = true;
    this.valid.internal = true;

    if (this.outerLink.url.val() === "") {
      this.outerLink.url.parents(".formItem:first").addClass("formItem--error");
      this.valid.external = false;
    }
    if (this.outerLink.title.val() === "") {
      this.outerLink.title.parents(".formItem:first").addClass("formItem--error");
      this.valid.external = false;
    }
    if (this.internalLink.title.val() === "") {
      this.internalLink.title.parents(".formItem:first").addClass("formItem--error");
      this.valid.internal = false;
    }
    if (!this.activeID) {
      this.valid.internal = false;
    }
  }

  insertLink() {
    this.validateFields();
    const index = this.savedSelection ? this.savedSelection.index : this.editor.getLength() - 1;
    const length = this.savedSelection ? this.savedSelection.length : 0;

    if (this.activeType === "external") {
      if (!this.valid.external) {
        return false;
      }
      if (length > 0) {
        this.editor.deleteText(index, length);
      }
      const linkText = this.outerLink.title.val();
      this.editor.insertText(index, linkText, "extlink", { href: this.outerLink.url.val() });
      // Insert space without format and position cursor after it
      const spaceIndex = index + linkText.length;
      this.editor.insertText(spaceIndex, " ", { extlink: false });
      this.editor.setSelection(spaceIndex + 1, 0);
      this.editor.format("extlink", false);
      this.toggle("hide");
    } else {
      if (!this.valid.internal) {
        return false;
      }
      if (length > 0) {
        this.editor.deleteText(index, length);
      }
      const linkText = this.internalLink.title.val();
      this.editor.insertText(index, linkText, "ekilink", {
        linkId: this.activeID,
        linkType: this.internalType,
      });
      // Insert space without format and position cursor after it
      const spaceIndex = index + linkText.length;
      this.editor.insertText(spaceIndex, " ", { ekilink: false });
      this.editor.setSelection(spaceIndex + 1, 0);
      this.editor.format("ekilink", false);
      this.toggle("hide");
    }
  }

  init() {
    this.toggle("show");
    this.bindEvents();
  }
}

const quillLinkTemplate = /*html*/ `
<div class="modal-content ekilinkEditor" style="top:{{parentTop}};">
  <div class="modal-header">
    <button class="btn btn-secondary" data-role="cancel">Tagasi</button>
    <button type="button" class="close" aria-label="Close" data-role="close" data-dismiss="modal">
      <span aria-hidden="true">×</span>
    </button>
  </div>
  <div class="modal-body">
    <div class="row">
      <div class="col-4">
        <h3>Vali lingi tüüp</h3>
        <div class="editorLink" data-type="external">Välislink</div>
        <div class="editorLink editorLink--nested">
          <div class="editorLink__parent">Siselink</div>
          <div class="editorLink__child" data-type="internal" data-internalType="meaning">Tähendus/Mõiste</div>
          <div class="editorLink__child" data-type="internal" data-internalType="word">Keelend/Termin</div>
        </div>
      </div>
      <div class="col-8 position-relative">
        <div data-linkType="external">
          <div class="formItem">
            <div class="formItem--title">Valitud Tekst</div>
            <input type="text" name="title" />
            <div class="formItem__error">Välja täitmine kohustuslik!</div>
          </div>
          <div class="formItem">
            <div class="formItem--title">Lisa link</div>
            <input type="text" name="url" />
            <div class="formItem__error">Välja täitmine kohustuslik!</div>
          </div>
        </div>
        <div data-linkType="internal" style="display: none;">
          <div class="formItem">
            <div class="formItem--title">Valitud Tekst</div>
            <input type="text" name="internalTitle" />
            <div class="formItem__error">Välja täitmine kohustuslik!</div>
          </div>
          <div class="formItem">
            <div class="formItem--title" data-role="title">Sisesta otsitav Keelend</div>
            <div class="formItem--dual">
              <input type="text" name="internalSearchValue" />
              <button class="btn btn-primary" data-role="internalSearchButton">Otsi</button>
            </div>
            <div class="formItem__error">Siselingi valimine kohustuslik!</div>
          </div>
          <div class="results"></div>
        </div>
      </div>
    </div>
  </div>
  <div class="modal-footer">
    <button type="button" class="btn btn-default" data-dismiss="modal" data-role="cancel">Katkesta</button>
    <button type="submit" class="btn btn-primary" data-role="save">Valmis</button>
  </div>
</div>
`;

function removeQuillLink(editor) {
  const selection = editor.getSelection();
  if (!selection) {
    return;
  }
  
  const formats = editor.getFormat(selection.index, selection.length);
  
  // Check if we're in a link
  if (formats.ekilink || formats.extlink) {
    // Find the end of the current format by scanning forward
    let endIndex = selection.index;
    const editorLength = editor.getLength();
    
    while (endIndex < editorLength) {
      const charFormat = editor.getFormat(endIndex, 1);
      if (!charFormat.ekilink && !charFormat.extlink) {
        break;
      }
      endIndex++;
    }
    
    // Move cursor to end of link first, then clear format before inserting space
    editor.setSelection(endIndex, 0);
    editor.format("ekilink", false);
    editor.format("extlink", false);
    editor.insertText(endIndex, " ");
    editor.setSelection(endIndex + 1, 0);
  }
}

class QuillMedia {
  constructor(editor, dlg) {
    this.editor = editor;
    this.dlg = dlg;
    this.parent = dlg.find(".modal-content:first");
    this.parentTop = this.parent.css("top");
    this.parentHeight = this.parent.outerHeight();
    this.isValid = true;
  }

  addTemplate() {
    const template = quillMediaTemplate.replace("{{parentTop}}", this.parentTop);
    this.mediaContent = $(template);
    this.url = this.mediaContent.find('input[name="url"]');
    this.parent.after(this.mediaContent);
  }

  toggleWindow(state) {
    if (state === "show") {
      this.addTemplate();
      this.parent.addClass("size-zero");
    } else {
      this.mediaContent.find(".formItem").removeClass("formItem--error");
      this.mediaContent.find("input").val("");
      this.mediaContent.remove();
      this.parent.removeClass("size-zero");
    }
  }

  validateField() {
    if (this.url.val() === "") {
      this.url.parents(".formItem:first").addClass("formItem--error");
      this.isValid = false;
    } else {
      this.isValid = true;
    }
  }

  insertMedia() {
    this.validateField();
    if (!this.isValid) {
      return;
    }

    const selection = this.editor.getSelection();
    const index = selection ? selection.index : this.editor.getLength() - 1;

    this.editor.insertEmbed(index, "eki-media", { src: this.url.val(), alt: "" });
    this.editor.setSelection(index + 1, 0);
    this.toggleWindow("hide");
  }

  bindEvents() {
    this.mediaContent.find('[data-role="cancel"]').on("click", (e) => {
      e.preventDefault();
      this.toggleWindow("hide");
    });

    this.mediaContent.parents(".modal:first").on("click.quillMedia", (e) => {
      if ($(e.target).is(".modal")) {
        this.toggleWindow("hide");
        this.mediaContent.parents(".modal:first").off("click.quillMedia");
      }
    });

    this.mediaContent.find('[data-role="save"]').on("click", (e) => {
      e.preventDefault();
      this.insertMedia();
    });
  }

  init() {
    this.toggleWindow("show");
    this.bindEvents();
  }
}

function removeQuillMedia(editor) {
  const editorRoot = $(editor.root);
  editorRoot.find("eki-media.eki-selected").remove();
}

function initQuillMediaSelection(editor) {
  const editorRoot = $(editor.root);
  editorRoot.on("click", "eki-media", function (e) {
    $(e.currentTarget).toggleClass("eki-selected");
  });
}

const quillMediaTemplate = /*html*/ `
<div class="modal-content ekimediaEditor" style="top:{{parentTop}};">
  <div class="modal-header">
    <button class="btn btn-secondary" data-role="cancel">Tagasi</button>
    <button type="button" class="close" aria-label="Close" data-role="close" data-dismiss="modal">
      <span aria-hidden="true">×</span>
    </button>
  </div>
  <div class="modal-body">
    <div class="row">
      <div class="col-12 position-relative">
        <div>
          <div class="formItem">
            <div class="formItem--title">Lisa pildi aadress</div>
            <input type="text" name="url" />
            <div class="formItem__error">Välja täitmine kohustuslik!</div>
          </div>
        </div>
      </div>
    </div>
  </div>
  <div class="modal-footer">
    <button type="button" class="btn btn-default" data-dismiss="modal" data-role="cancel">Katkesta</button>
    <button type="submit" class="btn btn-primary" data-role="save">Valmis</button>
  </div>
</div>
`;

function toggleSourceView(editor, dlg) {
  const container = $(editor.root).parent();
  let sourceArea = container.find(".ql-source-area");
  
  if (sourceArea.length) {
    // Switch back to WYSIWYG mode
    let html = sourceArea.val();
    // Wrap content in p tag for Quill if not already wrapped
    if (!html.startsWith("<p>")) {
      html = "<p>" + html + "</p>";
    }
    editor.root.innerHTML = html;
    sourceArea.remove();
    $(editor.root).show();
    dlg.find('[data-format="source"]').removeClass("ql-active");
  } else {
    // Switch to source mode
    let html = editor.root.innerHTML;
    // Strip p tags for cleaner source view
    html = html.replace(/<p>/g, "").replace(/<\/p>/g, "");
    const containerHeight = container.height();
    sourceArea = $('<textarea class="ql-source-area"></textarea>');
    sourceArea.val(html);
    sourceArea.css({
      width: "100%",
      height: containerHeight + "px",
      minHeight: "200px",
      fontFamily: "monospace",
      fontSize: "12px",
      border: "none",
      padding: "10px",
      resize: "none",
      outline: "none",
      boxSizing: "border-box",
    });
    $(editor.root).hide().after(sourceArea);
    dlg.find('[data-format="source"]').addClass("ql-active");
  }
}
