// Javascript methods for Ekilex custom editor

function openEkiEditorDlg(elem) {
    let targetName = $(elem).data('target-elem');
    let targetElement = $('[name="' + targetName + '"]');
    let editDlg = $($(elem).data('target'));
    let modifyFld = editDlg.find('[name=editFld]');
    modifyFld.html(targetElement.data('value') === undefined ? targetElement.text() : targetElement.data('value'));
    editDlg.find('[name=id]').val(targetElement.data('id'));
    editDlg.find('[name=opCode]').val(targetElement.data('op-code'));
    editDlg.find('button[type="submit"]').off('click').on('click', function(e) {
        editDlg.find('[name=value]').val(modifyFld.html());
        submitDialog(e, editDlg, 'Andmete muutmine ebaõnnestus.')
    });
    initEkiEditor(editDlg);
}

function initEkiEditor(editDlg) {
    let editorElem = editDlg.find('[name=editFld]');
    let menuElement = editDlg.find('.eki-editor-menu');
    editorElem.off('keydown').on('keydown', function (e) {
        let isEditorCommand = e.ctrlKey === true && (e.key === 'm' || e.key === 'n');
        if (isEditorCommand) {
            if (e.key === 'm') {
                menuElement.addClass('show');
                let selectElem = menuElement.find('select');
                selectElem.val(selectElem.find('option').first().val());
                selectElem.focus();
            } else if (e.key === 'n') {
                let cleanedHtml = editorElem.html().replace(/<\/?eki-elem-(.*?)>/g, '');
                editorElem.html(cleanedHtml);
            }
            e.preventDefault();
            e.stopPropagation();
        }
    });
    menuElement.off('keydown').on('keydown', function (e) {
        if ((e.key === 'Escape' || e.key === 'Enter') && menuElement.hasClass('show')) {
            e.preventDefault();
            e.stopPropagation();
            menuElement.removeClass('show');
            editorElem.focus();
            if (e.key === 'Enter') {
                let ekiTag = menuElement.find('option:selected').val();
                addNode(ekiTag);
            }
        }
    });
    menuElement.off('dblclick').on('dblclick', function (e) {
        if (menuElement.hasClass('show')) {
            e.preventDefault();
            e.stopPropagation();
            menuElement.removeClass('show');
            editorElem.focus();
            let ekiTag = menuElement.find('option:selected').val();
            addNode(ekiTag);
        }
    });
}

function addNode(ekiTag) {
    let sel = window.getSelection();
    if (sel.rangeCount) {
        let selectedRange = sel.getRangeAt(0);
        let ekiNode = document.createElement(ekiTag);
        selectedRange.surroundContents(ekiNode);
    }
}
