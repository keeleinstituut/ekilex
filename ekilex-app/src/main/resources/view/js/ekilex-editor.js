// Javascript methods for Ekilex custom editor

function initEkiEditor(editDlg) {
    let editorElem = editDlg.find('[name=value]');
    let previewElement = editDlg.find('[name=preview]');
    let menuElement = editDlg.find('.eki-editor-menu');
    editorElem.off('keydown').on('keydown', function (e) {
        let isEditorCommand = e.ctrlKey === true && (e.key === 'm' || e.key === 'n');
        if (isEditorCommand) {
            let isTextSelected = e.target.selectionStart !== e.target.selectionEnd;
            if (isTextSelected) {
                if (e.key === 'm') {
                    menuElement.addClass('show');
                    let selectElem = menuElement.find('select');
                    selectElem.val(selectElem.find('option').first().val());
                    selectElem.focus();
                    e.preventDefault();
                    e.stopPropagation();
                } else if (e.key === 'n') {
                    let preSelect = e.target.value.substring(0, e.target.selectionStart);
                    let selectedText = e.target.value.substring(e.target.selectionStart, e.target.selectionEnd);
                    let postSelect = e.target.value.substring(e.target.selectionEnd);
                    selectedText = selectedText.replace(/<\/?eki-elem-(.*?)>/g, '');
                    e.target.value = preSelect + selectedText + postSelect;
                    editorElem.trigger('input');
                }
                console.log(e);
            }
        }
    });
    menuElement.off('keydown').on('keydown', function (e) {
        if ((e.key === 'Escape' || e.key === 'Enter') && menuElement.hasClass('show')) {
            menuElement.removeClass('show');
            e.preventDefault();
            e.stopPropagation();
            editorElem.focus();
            if (e.key === 'Enter') {
                let ekiTag = menuElement.find('option:selected').val();
                addTag(editorElem, '<' + ekiTag + '>', '</' + ekiTag + '>');
            }
        }
    });
    menuElement.off('dblclick').on('dblclick', function (e) {
        if (menuElement.hasClass('show')) {
            menuElement.removeClass('show');
            e.preventDefault();
            e.stopPropagation();
            editorElem.focus();
            let ekiTag = menuElement.find('option:selected').val();
            addTag(editorElem, '<' + ekiTag + '>', '</' + ekiTag + '>');
        }
    });
    if (previewElement !== undefined) {
        previewElement.html(editorElem.val());
        editorElem.off('input').on('input', function() {
            previewElement.html(editorElem.val());
        });
    }
}

function addTag(editorElement, startTag, endTag) {
    let selectionStart = editorElement.prop('selectionStart');
    let selectionEnd = editorElement.prop('selectionEnd');
    let isTextSelected = selectionStart !== selectionEnd;
    if (isTextSelected) {
        let preSelect = editorElement.val().substring(0, selectionStart);
        let selectedText = editorElement.val().substring(selectionStart, selectionEnd);
        let postSelect = editorElement.val().substring(selectionEnd);
        editorElement.val(preSelect + startTag + selectedText + endTag + postSelect);
        editorElement.trigger('input');
    }
}
