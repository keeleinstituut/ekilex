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

function initEkiEditorDlg(editDlg) {
	let modifyFld = editDlg.find('[data-id="editFld"]');
	modifyFld.val(editDlg.find('[name=value]').val());

	const complexityBtns = editDlg.find('[name="complexity"]');
	if (complexityBtns.filter(':checked').length === 0){
		complexityBtns.eq(complexityBtns.length-1).prop('checked', true);
	}
	initCkEditor(modifyFld);

	editDlg.find('button[type="submit"]').off('click').on('click', function(e) {
		if (modifyFld.val()) {
			let content = modifyFld.val();
			content = content.replace("<br>", "").replaceAll("&nbsp;", " ").replaceAll('class="eki-selected"', '');
			editDlg.find('[name=value]').val(content);
			modifyFld.removeClass('is-invalid');
			submitDialog(e, editDlg, messages["common.data.update.error"]);
		} else {
			e.preventDefault();
			modifyFld.addClass('is-invalid');
		}
	});
};

function initEkiEditor(ekiEditorElem) {
	let editorElem = ekiEditorElem.find('[data-name=editFld]');
	let menuElement = ekiEditorElem.find('.eki-editor-menu');
	editorElem.off('keydown').on('keydown', function(e) {
		let isEditorCommand = e.ctrlKey === true && (e.key === 'm' || e.key === 'n');
		if (isEditorCommand) {
			if (e.key === 'm') {
				openEditorMenu(menuElement);
			} else if (e.key === 'n') {
				removeEkiTag(editorElem);
			}
			e.preventDefault();
			e.stopPropagation();
		}
	});
	menuElement.off('keydown').on('keydown', function(e) {
		if ((e.key === 'Escape' || e.key === 'Enter') && menuElement.hasClass('show')) {
			editorElem.focus();
			if (e.key === 'Enter') {
				let ekiTag = menuElement.find('option:selected').val();
				addNode(ekiTag);
			}
			e.preventDefault();
			e.stopPropagation();
		}
	});
	menuElement.off('click').on('click', function(e) {
		if (menuElement.hasClass('show')) {
			editorElem.focus();
			let ekiTag = menuElement.find('option:selected').val();
			addNode(ekiTag);
			e.preventDefault();
			e.stopPropagation();
		}
	});
	menuElement.off('focusout').on('focusout', function() {
		menuElement.removeClass('show');
	});
	let menuBtn = ekiEditorElem.find('[data-btn-menu]');
	menuBtn.off('click').on('click', function() {
		openEditorMenu(menuElement)
	});
	let removeBtn = ekiEditorElem.find('[data-btn-remove]');
	removeBtn.off('click').on('click', function() {
		removeEkiTag(editorElem)
	});

	editorElem.on('input', function() {
		if ($(this).html()) {
			$(this).removeClass('is-invalid');
		}
	})
};

function removeTags(editorElem) {
	let cleanedHtml = editorElem.html().replace(/<\/?eki-(.*?)>/g, '');
	editorElem.html(cleanedHtml);
	editorElem.focus();
};

function openEditorMenu(menuElement) {
	menuElement.addClass('show');
	let selectElem = menuElement.find('select');
	selectElem.val(selectElem.find('option').first().val());
	selectElem.focus();
};

function addNode(ekiTag) {
	let sel = window.getSelection();
	if (sel.rangeCount) {
		let selectedRange = sel.getRangeAt(0);
		if (selectedRange.collapsed === false) {
			let ekiNode = document.createElement(ekiTag);
			try {
				selectedRange.surroundContents(ekiNode);
			} catch (err) {
				alert('Vigaselt valitud tekst');
			}
		}
	}
};

function removeEkiTag(editorElem) {
	let sel = window.getSelection();
	if (sel.rangeCount) {
		let selectedRange = sel.getRangeAt(0);
		if (selectedRange.collapsed === false && selectedRange.startContainer.parentNode.nodeName.startsWith('EKI-')) {
			let ekiNode = selectedRange.startContainer.parentNode;
			let parentNodeOfekiNode = ekiNode.parentNode;
			while (ekiNode.firstChild) {
				parentNodeOfekiNode.insertBefore(ekiNode.firstChild, ekiNode);
			}
			parentNodeOfekiNode.removeChild(ekiNode);
		}
	}
	editorElem.focus();
};

$(function(){
	$(document).on("paste", "[data-name=editFld]", function(e) {
		e.preventDefault();
		let text = e.originalEvent.clipboardData.getData('text/plain');
		document.execCommand("insertHTML", false, text);
	});
});
