// Javascript methods for Ekilex custom editor component and dialogs are using it
function openUsageMemberDlg(elem) {
	let theDlg = $($(elem).data('target'));
	theDlg.find('[name=id]').val($(elem).data('id'));
}

function toggleGroup(dlg, groupName) {
	dlg.find('.value-group').hide();
	dlg.find('[data-id=' + groupName + ']').show();
}

function initUsageMemberDlg(theDlg) {
	theDlg.find('[name=opCode]').off('change').on('change', function(e) {
		toggleGroup(theDlg, $(e.target).val())
	});
	theDlg.find('button[type="submit"]').off('click').on('click', function(e) {
		let selectedGroup = theDlg.find('[name=opCode]').val();
		let selectedEditFld = theDlg.find('[data-id=' + selectedGroup + ']').find('[name=editFld]');
		theDlg.find('[name=value]').val(selectedEditFld.html());
		submitDialog(e, theDlg, 'Andmete lisamine ebaõnnestus.')
	});
	theDlg.off('shown.bs.modal').on('shown.bs.modal', function(e) {
		theDlg.find('.form-control').each(function(indx, item) {
			if ($(item).prop('contenteditable') === 'true') {
				$(item).html(null);
			} else {
				$(item).val(null);
			}
		});
		theDlg.find('select').each(function(indx, item) {
			$(item).val($(item).find('option').first().val());
		});
		toggleGroup(theDlg, theDlg.find('[name=opCode]').val());
		alignAndFocus(e, theDlg);
	});
	theDlg.find('.eki-editor').each(function(indx, item) {
		initEkiEditor($(item));
	});
}

function openEkiEditorAddDlg(elem, callback = $.noop) {
	let addDlg = $($(elem).data('target'));
	addDlg.find('[name=id]').val($(elem).data('id'));
	let modifyFld = addDlg.find('[name=editFld]');
	modifyFld.html(null);
	addDlg.find('button[type="submit"]').off('click').on('click', function(e) {
		addDlg.find('[name=value]').val(modifyFld.html());
		submitDialog(e, addDlg, 'Andmete lisamine ebaõnnestus.', callback)
	});
	addDlg.off('shown.bs.modal').on('shown.bs.modal', function(e) {
		alignAndFocus(e, addDlg)
	});
	let ekiEditorElem = addDlg.find('.eki-editor');
	initEkiEditor(ekiEditorElem);
}

function openEkiEditorDlg(elem) {
	let targetElementName = $(elem).data('target-elem');
	let targetElement = $('[name="' + targetElementName + '"]');
	let editDlg = $($(elem).data('target'));
	let modifyFld = editDlg.find('[name=editFld]');
	modifyFld.html(targetElement.data('value') === undefined ? targetElement.text() : targetElement.data('value'));
	editDlg.find('[name=id]').val(targetElement.data('id'));
	editDlg.find('[name=opCode]').val(targetElement.data('op-code'));
	editDlg.find('button[type="submit"]').off('click').on('click', function(e) {
		editDlg.find('[name=value]').val(modifyFld.html());
		submitDialog(e, editDlg, 'Andmete muutmine ebaõnnestus.')
	});
	editDlg.off('shown.bs.modal').on('shown.bs.modal', function(e) {
		alignAndFocus(e, editDlg)
	});
	let ekiEditorElem = editDlg.find('.eki-editor');
	initEkiEditor(ekiEditorElem);
}

function initEkiEditorDlg(editDlg) {
	let modifyFld = editDlg.find('[name=editFld]');
	modifyFld.html(editDlg.find('[name=value]').val());
	editDlg.find('button[type="submit"]').off('click').on('click', function(e) {
		//TODO why there is always <br> at the end??
		var content = modifyFld.html().replace("<br>", "");
		editDlg.find('[name=value]').val(content);
		submitDialog(e, editDlg, 'Andmete muutmine ebaõnnestus.')
	});
	let ekiEditorElem = editDlg.find('.eki-editor');
	initEkiEditor(ekiEditorElem);
}

function initEkiEditor(ekiEditorElem) {
	let editorElem = ekiEditorElem.find('[name=editFld]');
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
	})
}

function removeTags(editorElem) {
	let cleanedHtml = editorElem.html().replace(/<\/?eki-(.*?)>/g, '');
	editorElem.html(cleanedHtml);
	editorElem.focus();
}

function openEditorMenu(menuElement) {
	menuElement.addClass('show');
	let selectElem = menuElement.find('select');
	selectElem.val(selectElem.find('option').first().val());
	selectElem.focus();
}

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
}

//TODO not working
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
}