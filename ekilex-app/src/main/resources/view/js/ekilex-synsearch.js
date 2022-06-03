var IS_KEYBOARD_MODE = false;
var NAVIGATE_SELECTED_CLASS = 'keyboard-nav-list-item-active';
var NAVIGATE_DECLINED_CLASS = 'keyboard-nav-declined-item';
var NAVIGATE_SELECTED_ATTR = 'data-navigate-selected';


function initializeSynSearch() {
	let activeSearchResultID;
	var sidebarScrollPosition = {};

	//Enter keyboard edit mode
	$.fn.enableKeyboardModePlugin = function() {
		this.each(function() {
			const button = $(this);
			button.on('click', function() {
				IS_KEYBOARD_MODE = true;
				console.log('IS_KEYBOARD_MODE: ' + IS_KEYBOARD_MODE);
				$('body').addClass('keyboard-edit-mode-active');

				activateSynCandidatesList();

				$(this).attr('disabled', true);
			})
		})
	}
	// $(document).on("click", "#keyboardEditBtn", function() {
	// 	IS_KEYBOARD_MODE = true;
	// 	console.log('IS_KEYBOARD_MODE: ' + IS_KEYBOARD_MODE);
	// 	$('body').addClass('keyboard-edit-mode-active');

	// 	activateSynCandidatesList();

	// 	$(this).attr('disabled', true);

	// });

	// Moved to end of file as a plugin
	// $(document).on("click", "#activeTagCompleteBtn", function() {
	// 	let wordId = $(this).data('word-id');
	// 	let actionUrl = applicationUrl + "update_word_active_tag_complete/" + wordId;
	// 	let callbackFunc = () => refreshSynDetails();
	// 	doPostRelationChange(actionUrl, callbackFunc);
	// });

	$(document).on("click", ":button[name='synDetailsBtn']", function() {

		var savedScrollPositions = getScrollPositions();

		let id = $(this).data('id');
		let markedSynMeaningId = $(document).find('.keyboard-nav-list-item-selected').data('meaning-id');

		$('#synSearchResultsDiv').find('.list-group-item').each(function() {
			$(this).removeClass('keyboard-nav-list-item-active active');
		});
		$('#synSearchResultsDiv').find('[data-navigate-selected]').removeAttr('data-navigate-selected');

		$(this).parent().addClass('active');
		$(this).parent().attr('data-navigate-selected', true);

		$("[id^='syn_select_wait_']").hide();
		$("#syn_select_wait_" + id).show();
		openWaitDlg();

		let detailsUrl = applicationUrl + "syn_worddetails/" + id;
		if (markedSynMeaningId != undefined) {
			detailsUrl += '?markedSynMeaningId=' + markedSynMeaningId;
		}

		sidebarScrollPosition = {
			id: $('#syn-details-area').attr('data-id'),
			pos: $("#synCandidatesListDiv").scrollTop(),
		}

		$.get(detailsUrl).done(function(data) {
			let detailsDiv = $('#syn-details-area');
			detailsDiv.replaceWith(data);
			$('.tooltip').remove();
			closeWaitDlg();
			$("#syn_select_wait_" + id).hide();
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

			/*
			$(document).find('.draggable-syn-rel').draggable({
				revert: "invalid",
				appendTo: "body",
				containment: "window",
				helper: "clone"
			});
			*/

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
			}).trigger('scroll');

			$(document).find('.droppable-lexeme').droppable({
				accept: function(draggableDiv) {
					if ($(this).is('.canAccept')) {
						if (draggableDiv.hasClass("draggable-synonym")) {
							let draggableMeaningId = draggableDiv.data('meaning-id');
							let droppableMeaningId = $(this).data('meaning-id');
							let isSameMeaning = draggableMeaningId === droppableMeaningId;
							let existingMeaning = $(this).find("input.relation-meaning-id[value='" + draggableMeaningId + "']");

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

					let targetMeaningId = $(this).data('meaning-id');
					let wordRelationId = ui.draggable.parent().data('id');
					let sourceMeaningId = ui.draggable.data('meaning-id');

					let actionUrl = applicationUrl + 'syn_create_meaning_relation/' + targetMeaningId + '/' + sourceMeaningId + '/' + wordRelationId;

					openWaitDlg();
					let callbackFunc = () => refreshSynDetails();
					doPostRelationChange(actionUrl, callbackFunc);
				}
			});

			/*
			$(document).find('.droppable-syn-rel').droppable({
				accept: function(draggableDiv) {
					if ($(this).is('.canAccept')) {
						if (draggableDiv.hasClass("draggable-syn-rel")) {
							let draggableSynRelLexemeId = draggableDiv.closest('.droppable-syn-rel').attr('data-lexeme-id');
							let droppableSynRelLexemeId = $(this).closest('.droppable-syn-rel').attr('data-lexeme-id');

							return draggableSynRelLexemeId === droppableSynRelLexemeId;
						}
					}
				},
				classes: {
					"ui-droppable-active": "ui-state-active",
					"ui-droppable-hover": "ui-state-hover"
				},
				drop: function(event, ui) {

					let draggableOrderable = ui.draggable.closest('[data-orderpos]');
					let draggableOrderPos = Number(draggableOrderable.data('orderpos'));
					let orderingBtn = $(this);
					let droppableOrderable = $(this).closest('[data-orderpos]');
					let droppableOrderPos = Number(droppableOrderable.data('orderpos'));
					let posDelta = droppableOrderPos - draggableOrderPos;
					let draggableItem = ui.draggable;
					let orderingData = changeLexemeSynRelationOrdering(draggableItem, posDelta);
					openWaitDlg();
					postJson(applicationUrl + 'update_ordering', orderingData);
					if (orderingBtn.hasClass('do-refresh')) {
						refreshSynDetails();
					}
				}
			});
			*/

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

	// Moved to end of file as a plugin
	// $(document).on("click", ".rel-status-btn", function() {
	// 	let status = $(this).data('status');
	// 	let id = $(this).data('id');
	// 	let actionUrl = applicationUrl + 'syn_relation_status?id=' + id + '&status=' + status;

	// 	let callbackFunc = () => refreshSynDetails();

	// 	doPostRelationChange(actionUrl, callbackFunc);

	// });
	
	// Now in common file as changeItemOrderingPlugin
	// $(document).on('click', '.order-up', function() {
	// 	let orderingBtn = $(this);
	// 	let orderingData = changeItemOrdering(orderingBtn, -1);
	// 	postJson(applicationUrl + 'update_ordering', orderingData);
	// 	if (orderingBtn.hasClass('do-refresh')) {
	// 		refreshSynDetails();
	// 	}
	// });

	// Now in common file as changeItemOrderingPlugin
	// $(document).on('click', '.order-down', function() {
	// 	let orderingBtn = $(this);
	// 	let orderingData = changeItemOrdering(orderingBtn, 1);
	// 	postJson(applicationUrl + 'update_ordering', orderingData);
	// 	if (orderingBtn.hasClass('do-refresh')) {
	// 		refreshSynDetails();
	// 	}
	// });

	$(document).find('.draggable-synonym').draggable();
	$(document).find('.draggable-syn-rel').draggable();

	$(document).on('keydown', handleKeyPress);

	if ($('#synSearchResultsDiv').html() == undefined) {
		$(document).find('input[name="simpleSearchFilter"]').focus();
	}

	let detailButtons = $('#results').find('[name="synDetailsBtn"]');
	if (detailButtons.length === 1) {
		detailButtons.trigger('click');
	}

	// Replaced by activityLogDlgPlugin under common
	// $.fn.wordActivityLogDlgPlugin = function() {
	// 	var el = $(this);
	// 	el.on('show.bs.modal', function(e) {
	// //$(document).on('show.bs.modal', '#wordActivityLogDlg', function(e) {
	// 		let dlg = $(this);
	// 		let link = $(e.relatedTarget);
	// 		let url = link.attr('href');
	// 		dlg.find('.close').focus();
	// 		dlg.find('.modal-body').html(null);
	// 		$.get(url).done(function(data) {
	// 			dlg.find('.modal-body').html(data);
	// 		});
	// 	});
	// }

	// $.fn.pagingInputPlugin = function() {
	// 	const input = $(this);
	// 	input.on('keydown', function (e) {
	// 		if (e.which === 13 || e.keyCode === 13) {
	// 			console.log(e);
	// 			e.preventDefault();
	// 			input.closest('form').find('.paging-submit').click();
	// 		}
	// 	})
	// }
	//$(document).on('keydown', '.paging-input', function(e) {
	// $('#synSearchResultsDiv').on('keydown', '.paging-input', function (e) {
	// 	if (e.which === 13) {
	// 		e.preventDefault();
	// 		console.log(e);
	// 		$('[name="pagingBtn"]').last().trigger('click');
	// 	}
	// });

	// $('#synSearchResultsDiv').on('click', '[name="pagingBtn"]', function () {
	// //$(document).on('click', '[name="pagingBtn"]', function() {
	// 	openWaitDlg();
	// 	let url = applicationUrl + "syn_paging";
	// 	let button = $(this);
	// 	let direction = button.data("direction");
	// 	let form = button.closest('form');
	// 	if (direction === "page") {
	// 		//form.find('input[name="direction"]').val('next');
	// 		//let inputPageValue = $(".paging-input").val().trim();
	// 		//let tulemus = ((inputPageValue - 1) * 50) - 50;
	// 		//form.find('input[name="offset"]').val(tulemus);
	// 		//form.find('input[name="userInputPage"]').val(inputPageValue)
	// 	} else {
	// 		form.find('input[name="direction"]').val(direction);
	// 	}
	// 	$.ajax({
	// 		url: url,
	// 		data: form.serialize(),
	// 		method: 'POST',
	// 	}).done(function (data) {
	// 		closeWaitDlg();
	// 		$('#synSearchResultsDiv').html(data);
	// 		$('#synSearchResultsDiv').parent().scrollTop(0);
	// 		$('#syn-details-area').empty();
	// 		$wpm.bindObjects();
			
	// 	}).fail(function (data) {
	// 		console.log(data);
	// 		closeWaitDlg();
	// 		openAlertDlg('Lehekülje muutmine ebaõnnestus');
	// 	});

	// });
	 detailSearchBtn();  
}

function initAddSynRelationDlg(addDlg) {
	addDlg.find('.form-control').val(null);
	addDlg.find('[data-name=dialogContent]').html(null);

	addDlg.find('button[type="submit"]').off('click').on('click', function(e) {
		e.preventDefault();
		let button = $(this);
		let content = button.html();
		button.html(content + ' <i class="fa fa-spinner fa-spin"></i>');
		let theForm = $(this).closest('form');
		let url = theForm.attr('action') + '?' + theForm.serialize();

		$.get(url).done(function(data) {
			addDlg.find('[data-name=dialogContent]').replaceWith(data);
			let addRelationsBtn = addDlg.find('button[name="addRelationsBtn"]');

			let idsChk = addDlg.find('input[name="ids"]');
			idsChk.on('change', function() {
				addRelationsBtn.prop('disabled', !idsChk.filter(":checked").length);
			});

			addRelationsBtn.off('click').on('click', function(e) {
				e.preventDefault();
				let selectRelationsForm = addRelationsBtn.closest('form');
				if (checkRequiredFields(selectRelationsForm)) {
					let url = applicationUrl + "create_relations/"
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
						openAlertDlg('Kandidaatide lisamine ebaõnnestus');
					});
				}
			});

			addDlg.find('#addSynRelationWord').on('click', function(e) {
				e.preventDefault();
				let button = $(e.target);
				addDlg.find('[name=opCode]').val('create_syn_word');
				let weightValue = $("#weightInput").val();
				addDlg.find('[name=value2]').val(weightValue);

				let theForm = button.closest('form');
				if (checkRequiredFields(theForm)) {
					submitForm(theForm, 'Keelendi lisamine ebaõnnestus.').always(function() {
						refreshSynDetails();
						addDlg.modal('hide');
					});
				}
			});

		}).fail(function(data) {
			console.log(data);
			openAlertDlg('Viga!');
		}).always(function() {
			button.html(content);
		});
	});

	addDlg.off('shown.bs.modal').on('shown.bs.modal', function(e) {
		addDlg.find('.form-control').first().focus();
	});
}

function activateSynCandidatesList() {
	let activatedList = $('#synCandidatesListDiv');
	activatedList.attr('data-active-panel', true).addClass('keyboard-nav-list-active');
	let itemToSelect = activatedList.find('[data-navigate-selected="true"]').length ? activatedList.find('[data-navigate-selected="true"]') : activatedList.find('[data-navigate-index="0"]');
	itemToSelect.addClass('keyboard-nav-list-item-active');
	itemToSelect.attr(NAVIGATE_SELECTED_ATTR, true);
	changeSynonymDefinitionDisplay('show');
}

function doPostRelationChange(actionUrl, callbackFunc) {

	$.post(actionUrl).done(function(data) {
		if (data != '{}') {
			openAlertDlg("Andmete muutmine ebaõnnestus.");
			console.log(data);
		}
		callbackFunc();
	}).fail(function(data) {
		openAlertDlg("Andmete muutmine ebaõnnestus.");
		console.log(data);
	});
}

function isDisabledItem(activeDiv, navigateItem) {
	let panelIndex = activeDiv.attr('data-panel-index');

	if (panelIndex == "2") {
		let meaningId = activeDiv.data('marked-meaning-id');
		return navigateItem.find('input.relation-meaning-id[value="' + meaningId + '"]').length != 0;
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
	let selectedItem = activeDiv.find('[' + NAVIGATE_SELECTED_ATTR + ']');

	if (selectedItem.length == 0) {
		selectedItem = activeDiv.find('[data-navigate-index="0"]');
	}

	return selectedItem;
}

function isValidPanelChangeKeyPress(keyCode) {
	let synDetailsVisible = $("#syn-details-area").html() != '';

	if (!synDetailsVisible) {
		return false;
	}

	let currentActiveList = $('div[data-active-panel]');
	let currentActivePanelIndex = currentActiveList.data('panel-index');

	if (keyCode == 37 || keyCode == 39) {

		if (currentActivePanelIndex != undefined) {
			let currentIndex = parseInt(currentActivePanelIndex);
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

	var tag = e.target.tagName.toLowerCase();
	if (tag == 'input' || tag == 'textarea') {
		return false;
	}

	if (($(".modal").data('bs.modal') || {})._isShown) {
		return false;
	}

	return true;
}

function handleUpOrDownInList(e, currentSelectedItem, currentSelectedIndex, currentActiveList, currentActivePanelIndex) {
	if (currentSelectedItem.length != 0) {
		let indexIncrement = (e.keyCode == 40 ? 1 : -1);
		let newIndex = currentSelectedIndex + indexIncrement;
		let newItem = currentActiveList.find('[data-navigate-index="' + newIndex + '"]');

		if (newItem.length != 0) {
			if (currentActivePanelIndex == "3") {
				changeSynonymDefinitionDisplay('hide');
			}
			newItem.addClass(isDisabledItem(currentActiveList, newItem) ? NAVIGATE_DECLINED_CLASS : NAVIGATE_SELECTED_CLASS);
			newItem.attr(NAVIGATE_SELECTED_ATTR, true);
			unActivateItem(currentSelectedItem, true);
			$(currentActiveList).stop(true);
			$(currentActiveList).scrollTo(newItem, 320, {axis: 'y', offset: -64});
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
		let PANEL_KEYCODES = {"49": "1", "50": "2", "51": "3"};

		let isArrowKey = e.keyCode == 37 || e.keyCode == 39;
		if (isArrowKey) {
			if (currentActivePanelIndex != undefined) {
				selectedPanelIndex = parseInt(currentActivePanelIndex);
				if (e.keyCode == 37 && selectedPanelIndex > 1) {
					selectedPanelIndex--;
				} else if (e.keyCode == 39 && selectedPanelIndex < 3) {
					selectedPanelIndex++;
				}
			}
		} else {
			selectedPanelIndex = PANEL_KEYCODES[e.keyCode];
		}


		unActivateItem(currentSelectedItem, false);

		let activatedList = $('div[data-panel-index="' + selectedPanelIndex + '"]');
		activatedList.attr('data-active-panel', true).addClass('keyboard-nav-list-active');

		let selectedItem = findSelectedNavigateItem(activatedList);
		let isDisabled = isDisabledItem(activatedList, selectedItem);

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
		$(this).removeAttr('data-marked-meaning-id');
		$(this).removeAttr('data-active-panel');
		$(this).removeClass('keyboard-nav-list-active');
		$(this).find('[data-navigate-index]').each(function () {
			unActivateItem($(this), true);
		});
		$('.keyboard-nav-list-item-selected').removeClass('keyboard-nav-list-item-selected');

		$(this).find('.keyboard-nav-list-item-active').removeClass('keyboard-nav-list-item-active');

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
			let meaningId = currentActiveList.data('marked-meaning-id');
			let wordRelationId = $('#synCandidatesListDiv').find('.keyboard-nav-list-item-selected').data('relation-id');
			let sourceMeaningId = $('#synCandidatesListDiv').find('.keyboard-nav-list-item-selected').data('meaning-id');

			if (meaningId != undefined) {
				let targetMeaningId = currentSelectedItem.data('meaning-id');

				let actionUrl = applicationUrl + 'syn_create_meaning_relation/' + targetMeaningId + '/' + sourceMeaningId + '/' + wordRelationId;
				let callbackFunc = () => refreshSynDetails();
				doPostRelationChange(actionUrl, callbackFunc);

			} else {
				openAlertDlg("Ilmiku tekitamiseks vali paremalt tulbast sõna vajutades 'ENTER'.");
			}

		} else {
			openAlertDlg("Ilmik on juba olemas.");
		}
	} else if (currentActivePanelIndex == "1") {
		$(document).find('.keyboard-nav-list-item-selected').removeClass('keyboard-nav-list-item-selected');
		currentSelectedItem.find('button[name="synDetailsBtn"]').trigger('click');

		$('div[data-panel-index="' + 1 + '"]').removeAttr('data-active-panel').removeClass('keyboard-nav-list-active');
		activateSynCandidatesList();

	}
}

function handleKeyPress(e) {
	if (!isValidKeyboardModeKeypress(e)) {
		return;
	}

	let currentActiveList = $('div[data-active-panel]');
	let currentActivePanelIndex = currentActiveList.data('panel-index');
	let currentSelectedItem = currentActiveList.find('[data-navigate-selected]');
	let currentSelectedIndex = parseInt(currentSelectedItem.attr('data-navigate-index'));

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
	let scrollPositions = [];

	$('.keyboard-nav-list').each(function() {
		var scrollTop = $(this).scrollTop();
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
	let selectedWordId = $('#syn-details-area').data('id');
	var refreshButton = $('[name="synDetailsBtn"][data-id="' + selectedWordId + '"]');

	refreshButton.each(function () {
		if ($(this).is(":hidden")) {
			$(this).trigger('click');
		}
	});

	refreshButton.parent().addClass('active');
}

function updateWordSynRelationsStatusDeleted() {

	let wordId = $(this).data('word-id');
	let actionUrl = applicationUrl + 'syn_relation_status/delete?wordId=' + wordId;

	let callbackFunc = () => refreshSynDetails();

	doPostRelationChange(actionUrl, callbackFunc);
}

$.fn.updateTagCompletePlugin = function() {
	return this.each(function() {
		const button = $(this);
		button.on('click', function() {
			let wordId = button.data('word-id');
			let actionUrl = applicationUrl + "update_word_active_tag_complete/" + wordId;
			let callbackFunc = () => refreshSynDetails();
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
			const actionUrl = applicationUrl + 'syn_relation_status?id=' + id + '&status=' + status;
			const callbackFunc = () => refreshSynDetails();
			doPostRelationChange(actionUrl, callbackFunc);
		})
	});
}
