function initialise() {
	var NAVIGATE_SELECTED_CLASS = 'navigate-selected';
	var NAVIGATE_DECLINED_CLASS = 'navigate-declined';
	var NAVIGATE_SELECTED_ATTR = 'data-navigate-selected';

	$(document).on("click", ":button[name='manualEditBtn']", function() {
		//TODO refactor
		$('.navigate-panel').each(function (e) {
			//$(this).addClass('navigate-disabled-panel');
			$(this).removeAttr('data-active-panel');
		});

		let activatedDiv = $('div[data-panel-index="3"]');
		activatedDiv.attr('data-active-panel', true);
		itemToSelect = activatedDiv.find('[data-navigate-index="0"]');
		itemToSelect.addClass('navigate-selected');
		itemToSelect.attr(NAVIGATE_SELECTED_ATTR, true);
		itemToSelect.find('button').focus();

		$(this).attr('disabled', true);

	});

	$(document).on("click", ":button[name='synDetailsBtn']", function() {
		let id = $(this).data('id');
		let markedSynWordId = $(document).find('.navigate-marked').children(':first').data('word-id');
		$('#synSearchResultsDiv').find('.navigate-selected').each(function () {$(this).removeClass('navigate-selected active');});
		$('#synSearchResultsDiv').find('[data-navigate-selected]').removeAttr('data-navigate-selected');

		$(this).parent().addClass('navigate-selected active');
		$(this).parent().attr('data-navigate-selected', true);

		$("[id^='syn_select_wait_']").hide();
		$("#syn_select_wait_" + id).show();
		let detailsUrl = applicationUrl + 'syn_worddetails/' + id;
		if (markedSynWordId != undefined) {
			detailsUrl += '?markedSynWordId=' + markedSynWordId;
		}

		$.get(detailsUrl).done(function(data) {
			let detailsDiv = $('#syn_details_div');
			detailsDiv.replaceWith(data);
			$("#syn_select_wait_" + id).hide();
			$('[data-toggle="tooltip"]').tooltip();

			$(document).find('.draggable-synonym').draggable({
				revert: "invalid",
				appendTo:"body",
				containment: "window",
				helper: "clone",
				handle: ".handle"
			});

			$(document).find('.droppable-lexeme').droppable({
				accept: function(draggableDiv) {
					if (draggableDiv.hasClass("draggable-synonym")) {
						let wordId = draggableDiv.data('word-id');
						let existingWord = $(this).find("input.meaning-word-id[value='" + wordId + "']");

						if (!existingWord.length) {
							return true;
						}
					}
				},

				classes: {
					"ui-droppable-active": "ui-state-active",
					"ui-droppable-hover": "ui-state-hover"
				},
				drop: function (event, ui) {
					let meaningId = $(this).data('meaning-id');
					let lexemeId = $(this).data('lexeme-id');
					let wordId = ui.draggable.data('word-id');

					let actionUrl = applicationUrl + 'syn_create_lexeme/' + meaningId + '/' + wordId + '/' + lexemeId;
					let callbackFunc = () => $('#refresh-details').trigger('click');
					doPostRelationChange(actionUrl, callbackFunc);
				}
			});


		}).fail(function(data) {
			console.log(data);
			alert('Detailide päring ebaõnnestus, proovige hiljem uuesti.');
		});
	});

	$(document).on("click", ".rel-status-btn", function() {
		let status = $(this).data('status');
		let id = $(this).data('id');
		let actionUrl = applicationUrl + 'syn_relation_status?id=' + id + '&status=' + status;

		let callbackFunc = () => $('#refresh-details').trigger('click');

		doPostRelationChange(actionUrl, callbackFunc);

	});

	$(document).on('click', '.order-up', function() {
		let orderingData = changeItemOrdering($(this), -1);
		postJson(applicationUrl + 'update_ordering', orderingData);
	});

	$(document).on('click', '.order-down', function() {
		let orderingData = changeItemOrdering($(this), 1);
		postJson(applicationUrl + 'update_ordering', orderingData);
	});

	$(document).on("show.bs.modal", "[id^=addSynRelationDlg_]", function() {
		initAddSynRelationDlg($(this));
	});

	$(document).find('.draggable-synonym').draggable();

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
			let wordId = activeDiv.data('marked-word-id');

			return  navigateItem.find('input.meaning-word-id[value="' + wordId + '"]').length != 0;
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
		let synDetailsVisible = $("#syn_details_div").html() != '';

		if (!synDetailsVisible) {
			return false;
		}

		let currentActiveDiv = $('div[data-active-panel]');
		let currentActivePanelIndex = currentActiveDiv.data('panel-index');

		if (keyCode == 37 || keyCode == 39) {

			if (currentActivePanelIndex != undefined) {
				let currentIndex = parseInt(currentActivePanelIndex);
				return ((currentIndex > 1 && keyCode == 37) || (currentIndex < 3 && keyCode == 39));
			}
		}

		return true;
	}

	function checkKey(e) {
		//TODO refactor all this


		var tag = e.target.tagName.toLowerCase();
		if ( tag == 'input' || tag == 'textarea') {
			return;
		}

		if (($(".modal").data('bs.modal') || {})._isShown) {
			return;
		}

		let currentActiveDiv = $('div[data-active-panel]');
		let currentActivePanelIndex = currentActiveDiv.data('panel-index');
		let currentSelectedItem = currentActiveDiv.find('[data-navigate-selected]');
		let currentSelectedIndex = parseInt(currentSelectedItem.attr('data-navigate-index'));

		e = e || window.event;
		//console.log(e.keyCode);

		if (e.keyCode == 38 || e.keyCode == 40) { // arrows up down

			if (currentSelectedItem.length != 0) {
				let indexIncrement = (e.keyCode == 40 ? 1 : -1);
				let newIndex = currentSelectedIndex + indexIncrement;
				let newItem = currentActiveDiv.find('[data-navigate-index="' + newIndex + '"]');

				if (newItem.length != 0) {
					newItem.addClass(isDisabledItem(currentActiveDiv, newItem) ? NAVIGATE_DECLINED_CLASS : NAVIGATE_SELECTED_CLASS);
					newItem.attr(NAVIGATE_SELECTED_ATTR, true);
					unActivateItem(currentSelectedItem, true);
				}
			}
		}

		// 1 - 3, arrows left-right
		if ((e.keyCode >= 49 && e.keyCode <= 51) || e.keyCode == 37 || e.keyCode == 39) {
			if (isValidPanelChangeKeyPress(e.keyCode)) {
				$('div[data-panel-index]').each(function () {$(this).removeAttr('data-active-panel');});

				let selectedPanelIndex = 1;
				let PANEL_KEYCODES = {"49": "1", "50": "2", "51" : "3"};

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

				let activatedDiv = $('div[data-panel-index="' + selectedPanelIndex + '"]');
				activatedDiv.attr('data-active-panel', true);

				let selectedItem = findSelectedNavigateItem(activatedDiv);
				let isDisabled = isDisabledItem(activatedDiv, selectedItem);

				selectedItem.addClass(isDisabled ? NAVIGATE_DECLINED_CLASS : NAVIGATE_SELECTED_CLASS);
				selectedItem.attr('data-navigate-selected', true);
			}

		}

		if (e.keyCode == 27) { //esc
			$('.navigate-panel').each(function () {
				$(this).removeAttr('data-marked-word-id');
				$(this).removeAttr('data-active-panel');
				$(this).find('[data-navigate-index]').each(function () {
					unActivateItem($(this), true);
				});

				$(this).find('.navigate-marked').removeClass('navigate-marked');

				$(this).find(":button[name='manualEditBtn']").removeAttr('disabled');

				$(document).find('input[name="simpleSearchFilter"]').val('').focus();
			});
		}



		if (e.keyCode == 13) {
			e.preventDefault();

			if (currentActivePanelIndex == "3") {

				currentActiveDiv.find('.navigate-marked').each(function () {$(this).removeClass('navigate-marked');});
				currentSelectedItem.addClass('navigate-marked');
				currentActiveDiv.removeAttr('data-active-panel');
				let wordId = currentSelectedItem.children(':first').attr('data-word-id');

				let activatedDiv = $('div[data-panel-index="2"]');
				activatedDiv.attr('data-active-panel', true);
				activatedDiv.data('marked-word-id', wordId);

				let selectedLexemeItem = findSelectedNavigateItem(activatedDiv);

				let lexemeExists = selectedLexemeItem.find('input.meaning-word-id[value="' + wordId + '"]').length != 0;

				selectedLexemeItem.addClass(lexemeExists ? NAVIGATE_DECLINED_CLASS : NAVIGATE_SELECTED_CLASS);
				selectedLexemeItem.attr(NAVIGATE_SELECTED_ATTR, true);

			} else if (currentActivePanelIndex == "2") {
				if (!currentSelectedItem.hasClass(NAVIGATE_DECLINED_CLASS)) {
					let wordId = currentActiveDiv.data('marked-word-id'); //TODO move to a hidden field ?
					if (wordId != undefined) {

						let lexemeId = currentSelectedItem.data('lexeme-id');
						let meaningId = currentSelectedItem.data('meaning-id');

						let actionUrl = applicationUrl + 'syn_create_lexeme/' + meaningId + '/' + wordId + '/' + lexemeId;
						let callbackFunc = () => $('#refresh-details').trigger('click');

						doPostRelationChange(actionUrl, callbackFunc);

					} else {
						openAlertDlg("Ilmiku tekitamiseks vali paremalt tulbast sõna vajutades 'ENTER'.");
					}

				} else {
					openAlertDlg("Ilmik on juba olemas.");
				}
			} else if (currentActivePanelIndex == "1") {
				$(document).find('.navigate-marked').removeClass('navigate-marked');
				currentSelectedItem.find('button[name="synDetailsBtn"]').trigger('click');
			}
		}
	}

	$(document).on('keydown', checkKey);

	if ($('#synSearchResultsDiv').html() == undefined) {
		$(document).find('input[name="simpleSearchFilter"]').focus();
	}

}

function refreshDetails() {
	var refreshButton = $('#refresh-details');
	refreshButton.trigger('click');
}
