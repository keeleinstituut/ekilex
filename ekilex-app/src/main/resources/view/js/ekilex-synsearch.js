let IS_KEYBOARD_MODE = false;
const NAVIGATE_SELECTED_CLASS = 'keyboard-nav-list-item-active';
const NAVIGATE_DECLINED_CLASS = 'keyboard-nav-declined-item';
const NAVIGATE_SELECTED_ATTR = 'data-navigate-selected';


function initializeSynSearch() {
	let activeSearchResultID;
	let sidebarScrollPosition = {};

	//Enter keyboard edit mode
	$.fn.enableKeyboardModePlugin = function() {
		this.each(function() {
			const button = $(this);
			button.on('click', function() {
				IS_KEYBOARD_MODE = true;
				console.log('IS_KEYBOARD_MODE: ' + IS_KEYBOARD_MODE);
				$('body').addClass('keyboard-edit-mode-active');

				activateSynCandidatesList();

			 obj.attr('disabled', true);
			});
		})
	}

	$(document).on("click", ":button[name='synDetailsBtn']", function() {
		const button = $(this);
		let savedScrollPositions = getScrollPositions();

		const id = button.data('id');
		let markedSynMeaningId = $(document).find('.keyboard-nav-list-item-selected').data('meaning-id');

		$('#synSearchResultsDiv').find('.list-group-item').each(function() {
			button.removeClass('keyboard-nav-list-item-active active');
		});
		$('#synSearchResultsDiv').find('[data-navigate-selected]').removeAttr('data-navigate-selected');

		button.parent().addClass('active');
		button.parent().attr('data-navigate-selected', true);

		$("[id^='syn_select_wait_']").hide();
		$(`#syn_select_wait_${id}`).show();
		openWaitDlg();

		let detailsUrl = `${applicationUrl}syn_worddetails/${id}`;
		if (markedSynMeaningId != undefined) {
			detailsUrl += `?markedSynMeaningId=${markedSynMeaningId}`;
		}

		sidebarScrollPosition = {
			id: $('#syn-details-area').attr('data-id'),
			pos: $("#synCandidatesListDiv").scrollTop(),
		}

		$.get(detailsUrl).done(function(data) {
			let detailsDivParent = $('#syn-details-area').parent();
			detailsDivParent.html(data);
			$('.tooltip').remove();
			closeWaitDlg();
			$(`#syn_select_wait_${id}`).hide();
			$('[data-toggle="tooltip"]').tooltip({trigger: 'hover'});

			$wpm.bindObjects();

			if ($('#syn-details-area').attr('data-id') === sidebarScrollPosition.id) {
				$('#synCandidatesListDiv').scrollTop(sidebarScrollPosition.pos);
			}

			$('.syn-stats-popover').popover({
				template: '<div class="popover popover-inverted synonym-statistics-popover" role="tooltip"><div class="arrow"></div><div class="popover-head"><h3 class="popover-header"></h3></div><div class="popover-body"></div></div>',
				placement: 'top',
				content: function() {
					// Get the content from the hidden sibling.
					return $(this).siblings('.syn-stats-content').html();
				}
			});

			$(document).find('.draggable-synonym').draggable({
				revert: "invalid",
				appendTo: "body",
				containment: "window",
				helper: "clone"
			});

			let scrollDebounce;
			$('.lexeme-list').off('scroll.droppable').on('scroll.droppable', function(){
				const main = $(this);
				clearTimeout(scrollDebounce);
				scrollDebounce = setTimeout(function(){
					const elements = main.find('.lexeme-list-item');
					const scrollTop = main.scrollTop();
					const mainHeight = main.height();
					const topPos = main.offset().top + scrollTop;
					elements.each(function(){
						const obj = $(this);
						const objTop = obj.offset().top + scrollTop;
						if (objTop + 100 > topPos && objTop - 100 < topPos + mainHeight) {
							obj.addClass('canAccept');
							obj.find('.droppable-lexeme, .droppable-syn-rel').addClass('canAccept');
						} else {
							obj.removeClass('canAccept');
							obj.find('.droppable-lexeme, .droppable-syn-rel').removeClass('canAccept');
						}
					});
				}, 150);
			}).scroll();

			$(document).find('.droppable-lexeme').droppable({
				accept: function(draggableDiv) {
					const $this = $(this);
					if ($this.is('.canAccept')) {
						if (draggableDiv.hasClass("draggable-synonym")) {
							const draggableMeaningId = draggableDiv.data('meaning-id');
							const droppableMeaningId = $this.data('meaning-id');
							const isSameMeaning = draggableMeaningId === droppableMeaningId;
							const existingMeaning = $this.find(`input.relation-meaning-id[value="${draggableMeaningId}"]`);

							if (!existingMeaning.length && !isSameMeaning) {
								return true;
							}
						}
					}
				},
				classes: {
					"ui-droppable-active": "ui-state-active",
					"ui-droppable-hover": "ui-state-hover"
				},
				drop: function(event, ui) {

					const targetMeaningId = $(this).data('meaning-id');
					const wordRelationId = ui.draggable.parent().data('id');
					const sourceMeaningId = ui.draggable.data('meaning-id');

					const actionUrl = `${applicationUrl}syn_create_meaning_relation/${targetMeaningId}/${sourceMeaningId}/${wordRelationId}`;

					openWaitDlg();
					const callbackFunc = () => refreshSynDetails();
					doPostRelationChange(actionUrl, callbackFunc);
				}
			});

			if (IS_KEYBOARD_MODE) {
				activateSynCandidatesList();
			}

			//KEEP TRACK OF WHAT WAS THE LAST SEARCH RESULT DISPLAYED
			if (activeSearchResultID !== id) {
				activeSearchResultID = id;
			}
			//IF AN ALLREADY ACTIVE DETAILS VIEW WAS SELECTED KEEP THE SCROLLPOSITIONS
			else if (activeSearchResultID === id) {
				setScrollPositions(savedScrollPositions);
			}

		}).fail(function(data) {
			console.log(data);
			alert('Detailide päring ebaõnnestus, proovige hiljem uuesti.');
		});
	});

	$(document).find('.draggable-synonym').draggable();
	$(document).find('.draggable-syn-rel').draggable();

	$(document).on('keydown', handleKeyPress);

	if ($('#synSearchResultsDiv').html() == undefined) {
		$(document).find('input[name="simpleSearchFilter"]').focus();
	}

	const detailButtons = $('#results').find('[name="synDetailsBtn"]');
	if (detailButtons.length === 1) {
		detailButtons.click();
	}
	detailSearchBtn();  
}

function initAddSynRelationDlg(addDlg) {
	addDlg.find('.form-control').val(null);
	addDlg.find('[data-name=dialogContent]').html(null);

	addDlg.find('button[type="submit"]').off('click').on('click', function(e) {
		e.preventDefault();
		const button = $(this);
		const content = button.html();
		button.html(content + ' <i class="fa fa-spinner fa-spin"></i>');
		const form = button.closest('form');
		const url = form.attr('action') + '?' + form.serialize();

		$.get(url).done(function(data) {
			addDlg.find('[data-name=dialogContent]').replaceWith(data);
			const addRelationsBtn = addDlg.find('button[name="addRelationsBtn"]');

			const idsChk = addDlg.find('input[name="ids"]');
			idsChk.on('change', function() {
				addRelationsBtn.prop('disabled', !idsChk.filter(":checked").length);
			});

			addRelationsBtn.off('click').on('click', function(e) {
				e.preventDefault();
				const selectRelationsForm = addRelationsBtn.closest('form');
				if (checkRequiredFields(selectRelationsForm)) {
					const url = applicationUrl + "create_relations/"
					$.ajax({
						url: url,
						data: selectRelationsForm.serialize(),
						method: 'POST',
					}).done(function() {
						addDlg.modal('hide');
						refreshSynDetails();
					}).fail(function(data) {
						addDlg.modal('hide');
						console.log(data);
						openAlertDlg(messages["common.error"]);
					});
				}
			});

			addDlg.find('#addSynRelationWord').on('click', function(e) {
				e.preventDefault();
				const button = $(e.target);
				addDlg.find('[name=opCode]').val('create_syn_word');
				const weightValue = $("#weightInput").val();
				addDlg.find('[name=value2]').val(weightValue);

				const form = button.closest('form');
				if (checkRequiredFields(form)) {
					submitForm(form, messages["syn.add.syn.relation.word.fail"]).always(function() {
						refreshSynDetails();
						addDlg.modal('hide');
					});
				}
			});

		}).fail(function(data) {
			console.log(data);
			openAlertDlg(messages["common.error"]);
		}).always(function() {
			button.html(content);
		});
	});

	addDlg.off('shown.bs.modal').on('shown.bs.modal', function(e) {
		addDlg.find('.form-control').first().focus();
	});
}

function activateSynCandidatesList() {
	const activatedList = $('#synCandidatesListDiv');
	activatedList.attr('data-active-panel', true).addClass('keyboard-nav-list-active');
	const itemToSelect = activatedList.find('[data-navigate-selected="true"]').length ? activatedList.find('[data-navigate-selected="true"]') : activatedList.find('[data-navigate-index="0"]');
	itemToSelect.addClass('keyboard-nav-list-item-active');
	itemToSelect.attr(NAVIGATE_SELECTED_ATTR, true);
	changeSynonymDefinitionDisplay('show');
}

function doPostRelationChange(actionUrl, callbackFunc) {

	$.post(actionUrl).done(function(data) {
		if (data != '{}') {
			openAlertDlg(messages["common.error"]);
			console.log(data);
		}
		callbackFunc();
	}).fail(function(data) {
		openAlertDlg(messages["common.error"]);
		console.log(data);
	});
}

function isDisabledItem(activeDiv, navigateItem) {
	const panelIndex = activeDiv.attr('data-panel-index');

	if (panelIndex == "2") {
		const meaningId = activeDiv.data('marked-meaning-id');
		return navigateItem.find(`input.relation-meaning-id[value="${meaningId}"]`).length != 0;
	}
	return false;
}

function unActivateItem(selectedItem, unSelect) {

	if (selectedItem != undefined) {
		selectedItem.removeClass(NAVIGATE_SELECTED_CLASS);
		selectedItem.removeClass(NAVIGATE_DECLINED_CLASS);

		if (unSelect) {
			selectedItem.removeAttr(NAVIGATE_SELECTED_ATTR);
		}
	}
}

function findSelectedNavigateItem(activeDiv) {
	let selectedItem = activeDiv.find(`${NAVIGATE_SELECTED_ATTR}`);

	if (selectedItem.length == 0) {
		selectedItem = activeDiv.find('[data-navigate-index="0"]');
	}

	return selectedItem;
}

function isValidPanelChangeKeyPress(keyCode) {
	const synDetailsVisible = $("#syn-details-area").html() !== '';

	if (!synDetailsVisible) {
		return false;
	}

	const currentActiveList = $('div[data-active-panel]');
	const currentActivePanelIndex = currentActiveList.data('panel-index');

	if (keyCode == 37 || keyCode == 39) {

		if (currentActivePanelIndex != undefined) {
			const currentIndex = parseInt(currentActivePanelIndex);
			return ((currentIndex > 1 && keyCode == 37) || (currentIndex < 3 && keyCode == 39));
		}
	}

	return true;
}

function isValidKeyboardModeKeypress(e) {
	if (IS_KEYBOARD_MODE == false) {
		console.log("KEYBOARD MODE NOT ENABLED");
		return false;
	}

	const tag = e.target.tagName.toLowerCase();
	if (tag == 'input' || tag == 'textarea') {
		return false;
	}

	if ($(".modal").data('bs.modal')?._isShown) {
		return false;
	}

	return true;
}

function handleUpOrDownInList(e, currentSelectedItem, currentSelectedIndex, currentActiveList, currentActivePanelIndex) {
	if (currentSelectedItem.length != 0) {
		const indexIncrement = (e.keyCode == 40 ? 1 : -1);
		const newIndex = currentSelectedIndex + indexIncrement;
		const newItem = currentActiveList.find(`[data-navigate-index="${newIndex}"]`);

		if (newItem.length != 0) {
			if (currentActivePanelIndex == "3") {
				changeSynonymDefinitionDisplay('hide');
			}
			const $currentActiveList = $(currentActiveList);
			newItem.addClass(isDisabledItem(currentActiveList, newItem) ? NAVIGATE_DECLINED_CLASS : NAVIGATE_SELECTED_CLASS);
			newItem.attr(NAVIGATE_SELECTED_ATTR, true);
			unActivateItem(currentSelectedItem, true);
			$currentActiveList.stop(true);
			$currentActiveList.scrollTo(newItem, 320, {axis: 'y', offset: -64});
			if (currentActivePanelIndex == "3") {
				changeSynonymDefinitionDisplay('show');
			}
		}
	}
}

function handleListChange(e, currentActivePanelIndex, currentSelectedItem) {
	if (isValidPanelChangeKeyPress(e.keyCode)) {

		$('div[data-panel-index]').each(function() {
			$(this).removeAttr('data-active-panel').removeClass('keyboard-nav-list-active');
		});
		if (currentActivePanelIndex == "3") {
			changeSynonymDefinitionDisplay('hide');
		}

		let selectedPanelIndex = 1;
		const PANEL_KEYCODES = {"49": "1", "50": "2", "51": "3"};

		const isArrowKey = e.keyCode === 37 || e.keyCode === 39;
		if (isArrowKey) {
			if (currentActivePanelIndex != undefined) {
				selectedPanelIndex = parseInt(currentActivePanelIndex);
				if (e.keyCode === 37 && selectedPanelIndex > 1) {
					selectedPanelIndex--;
				} else if (e.keyCode === 39 && selectedPanelIndex < 3) {
					selectedPanelIndex++;
				}
			}
		} else {
			selectedPanelIndex = PANEL_KEYCODES[e.keyCode];
		}


		unActivateItem(currentSelectedItem, false);

		const activatedList = $('div[data-panel-index="' + selectedPanelIndex + '"]');
		activatedList.attr('data-active-panel', true).addClass('keyboard-nav-list-active');

		const selectedItem = findSelectedNavigateItem(activatedList);
		const isDisabled = isDisabledItem(activatedList, selectedItem);

		selectedItem.addClass(isDisabled ? NAVIGATE_DECLINED_CLASS : NAVIGATE_SELECTED_CLASS);
		selectedItem.attr('data-navigate-selected', true);
		if (selectedPanelIndex == "3") {
			changeSynonymDefinitionDisplay('show');
		}
	}
}

function handleEscapeKeyPress(currentActivePanelIndex) {
	if (currentActivePanelIndex == "3") {
		changeSynonymDefinitionDisplay('hide');
	}
	$('.keyboard-nav-list').each(function() {
		const navItem = $(this);
		navItem.removeAttr('data-marked-meaning-id');
		navItem.removeAttr('data-active-panel');
		navItem.removeClass('keyboard-nav-list-active');
		navItem.find('[data-navigate-index]').each(function () {
			unActivateItem(navItem, true);
		});
		$('.keyboard-nav-list-item-selected').removeClass('keyboard-nav-list-item-selected');

		navItem.find('.keyboard-nav-list-item-active').removeClass('keyboard-nav-list-item-active');

		$("#keyboardEditBtn").removeAttr('disabled');

		$(document).find('input[name="simpleSearchFilter"]').val('').focus();
	});
	$('body').removeClass('keyboard-edit-mode-active');
	IS_KEYBOARD_MODE = false;
}

function handleEnterKeyPress(e, currentActivePanelIndex, currentSelectedItem, currentActiveList) {
	e.preventDefault();
	//IF SYNONYM LIST IS ACTIVE AND USER PRESSES ENTER
	if (currentActivePanelIndex == "3") {
		changeSynonymDefinitionDisplay('hide');
		currentActiveList.removeClass('keyboard-nav-list-active').removeAttr('data-active-panel').find('.keyboard-nav-list-item-selected').each(function() {
			$(this).removeClass('keyboard-nav-list-item-selected');
		});
		currentSelectedItem.addClass('keyboard-nav-list-item-selected');

		unActivateItem(currentSelectedItem, false);
		let meaningId = currentSelectedItem.attr('data-meaning-id');

		let activatedList = $('div[data-panel-index="' + 2 + '"]');
		activatedList.attr('data-active-panel', true).addClass('keyboard-nav-list-active');
		activatedList.data('marked-meaning-id', meaningId);

		let selectedLexemeItem = findSelectedNavigateItem(activatedList);

		let lexemeExists = selectedLexemeItem.find('input.relation-meaning-id[value="' + meaningId + '"]').length != 0;

		selectedLexemeItem.addClass(lexemeExists ? NAVIGATE_DECLINED_CLASS : NAVIGATE_SELECTED_CLASS);
		selectedLexemeItem.attr(NAVIGATE_SELECTED_ATTR, true);

	} else if (currentActivePanelIndex == "2") {
		if (!currentSelectedItem.hasClass(NAVIGATE_DECLINED_CLASS)) {
			const meaningId = currentActiveList.data('marked-meaning-id');
			const wordRelationId = $('#synCandidatesListDiv').find('.keyboard-nav-list-item-selected').data('relation-id');
			const sourceMeaningId = $('#synCandidatesListDiv').find('.keyboard-nav-list-item-selected').data('meaning-id');

			if (meaningId != undefined) {
				const targetMeaningId = currentSelectedItem.data('meaning-id');

				const actionUrl = `${applicationUrl}syn_create_meaning_relation/${targetMeaningId}/${sourceMeaningId}/${wordRelationId}`;
				const callbackFunc = () => refreshSynDetails();
				doPostRelationChange(actionUrl, callbackFunc);

			} else {
				openAlertDlg(messages["syn.press.enter.for.lexeme"]);
			}

		} else {
			openAlertDlg(messages["syn.lexeme.exists"]);
		}
	} else if (currentActivePanelIndex == "1") {
		$(document).find('.keyboard-nav-list-item-selected').removeClass('keyboard-nav-list-item-selected');
		currentSelectedItem.find('button[name="synDetailsBtn"]').click();

		$('div[data-panel-index="1"]').removeAttr('data-active-panel').removeClass('keyboard-nav-list-active');
		activateSynCandidatesList();

	}
}

function handleKeyPress(e) {
	if (!isValidKeyboardModeKeypress(e)) {
		return;
	}

	const currentActiveList = $('div[data-active-panel]');
	const currentActivePanelIndex = currentActiveList.data('panel-index');
	const currentSelectedItem = currentActiveList.find('[data-navigate-selected]');
	const currentSelectedIndex = parseInt(currentSelectedItem.attr('data-navigate-index'));

	e = e || window.event;

	if (e.keyCode == 38 || e.keyCode == 40) { // arrows up down
		handleUpOrDownInList(e, currentSelectedItem, currentSelectedIndex, currentActiveList, currentActivePanelIndex);
	}

	// 1 - 3, arrows left-right
	if ((e.keyCode >= 49 && e.keyCode <= 51) || e.keyCode == 37 || e.keyCode == 39) {
		handleListChange(e, currentActivePanelIndex, currentSelectedItem);
	}

	if (e.keyCode == 27) {
		handleEscapeKeyPress(currentActivePanelIndex);
	}

	if (e.keyCode == 13) {
		handleEnterKeyPress(e, currentActivePanelIndex, currentSelectedItem, currentActiveList);
	}
}

function getScrollPositions() {
	const scrollPositions = [];

	$('.keyboard-nav-list').each(function() {
		const scrollTop = $(this).scrollTop();
		scrollPositions.push(scrollTop);
	})
	return scrollPositions;
}

function setScrollPositions(positions) {
	$('.keyboard-nav-list').each(function(i) {
		$(this).scrollTop(positions[i]);
	})
}

function changeSynonymDefinitionDisplay(displayOption = 'toggle') {
	$('.tooltip').remove();
	$('.keyboard-nav-list-item-active .list-item-value').tooltip(displayOption);
}

function refreshSynDetails() {
	const selectedWordId = $('#syn-details-area').data('id');
	const refreshButton = $(`[name="synDetailsBtn"][data-id="${selectedWordId}"]`).first();

	refreshButton.click();

	refreshButton.parent().addClass('active');
}

function updateWordSynRelationsStatusDeleted() {

	const wordId = $(this).data('word-id');
	const actionUrl = `${applicationUrl}syn_relation_status/delete?wordId=${wordId}`;
	const callbackFunc = () => refreshSynDetails();

	doPostRelationChange(actionUrl, callbackFunc);
}

$.fn.updateSynTagCompletePlugin = function() {
	return this.each(function() {
		const button = $(this);
		button.on('click', function() {
			const wordId = button.data('word-id');
			const actionUrl = `${applicationUrl}update_word_active_tag_complete/${wordId}`;
			const callbackFunc = () => refreshSynDetails();
			doPostRelationChange(actionUrl, callbackFunc);
		})
	})
}

$.fn.changeSynRelationPlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('click', function() {
			const status = obj.data('status');
			const id = obj.data('id');
			const actionUrl = `${applicationUrl}syn_relation_status?id=${id}&status=${status}`;
			const callbackFunc = () => refreshSynDetails();
			doPostRelationChange(actionUrl, callbackFunc);
		})
	});
}
