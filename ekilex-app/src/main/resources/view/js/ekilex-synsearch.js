function initialise() {
	var KEYBOARD_MODE = false;
	var NAVIGATE_SELECTED_CLASS = 'keyboard-nav-list-item-active';
	var NAVIGATE_DECLINED_CLASS = 'keyboard-nav-declined-item';
	var NAVIGATE_SELECTED_ATTR = 'data-navigate-selected';

	//Enter keyboard edit mode
	$(document).on("click", "#keyboardEditBtn", function() {
		KEYBOARD_MODE = true;
		console.log('KEYBOARD_MODE: '+KEYBOARD_MODE);
		$('body').addClass('keyboard-edit-mode-active');
		//TODO refactor
		$('.keyboard-nav-list').each(function (e) {
			$(this).removeAttr('data-active-panel').removeClass('keyboard-nav-list-active');
		});
		activateList(3);

		$(this).attr('disabled', true);

	});

	function activateList(listIndx, itemIndx=0){
		let activatedList = $('div[data-panel-index="'+listIndx+'"]');
		activatedList.attr('data-active-panel', true).addClass('keyboard-nav-list-active');
		itemToSelect = activatedList.find('[data-navigate-selected="true"]').length ? activatedList.find('[data-navigate-selected="true"]') : activatedList.find('[data-navigate-index="'+itemIndx+'"]');
		itemToSelect.addClass('keyboard-nav-list-item-active');
		itemToSelect.attr(NAVIGATE_SELECTED_ATTR, true);
	}

	$(document).on("click", ".popover-close-btn" , function(){
		$(this).parents(".popover").popover('hide');
	});

	$(document).on("click", ":button[name='synDetailsBtn']", function() {
		let id = $(this).data('id');
		let markedSynWordId = $(document).find('.keyboard-nav-list-item-selected').children(':first').data('word-id');
		$('#synSearchResultsDiv').find('.list-group-item').each(function () {$(this).removeClass('keyboard-nav-list-item-active active');});
		$('#synSearchResultsDiv').find('[data-navigate-selected]').removeAttr('data-navigate-selected');

		$(this).parent().addClass('active');
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

			$('.syn-stats-popover').popover({
				template:'<div class="popover popover-inverted synonym-statistics-popover" role="tooltip"><div class="arrow"></div><div class="popover-head"><h3 class="popover-header" ></h3><button type="button" class="bnt btn-sm btn-outline-light border-0  popover-close-btn"><i class="fa fa-close" aria-hidden="true"></i></button></div><div class="popover-body"></div></div>',
				placement:'top',
				content: function () {
					// Get the content from the hidden sibling.
					return $(this).siblings('.syn-stats-content').html();
				}
			});

			$(document).find('.draggable-synonym').draggable({
				revert: "invalid",
				appendTo:"body",
				containment: "window",
				helper: "clone"
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
					let relationId = ui.draggable.parent().data('id');
					console.log('relation id ' + relationId)
					let meaningId = $(this).data('meaning-id');
					let lexemeId = $(this).data('lexeme-id');
					let wordId = ui.draggable.data('word-id');

					let actionUrl = applicationUrl + 'syn_create_lexeme/' + meaningId + '/' + wordId + '/' + lexemeId + '/' + relationId;

					let callbackFunc = () => $('#refresh-details').trigger('click');
					doPostRelationChange(actionUrl, callbackFunc);
				}
			});

			if(KEYBOARD_MODE){
				activateList(3);
			}

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

	function checkKey(e) {
		if (KEYBOARD_MODE == false){
			console.log("KEYBOARD MODE NOT ENABLED");
			return;
		}

		//TODO refactor all this


		var tag = e.target.tagName.toLowerCase();
		if ( tag == 'input' || tag == 'textarea') {
			return;
		}

		if (($(".modal").data('bs.modal') || {})._isShown) {
			return;
		}

		let currentActiveList = $('div[data-active-panel]');
		let currentActivePanelIndex = currentActiveList.data('panel-index');
		let currentSelectedItem = currentActiveList.find('[data-navigate-selected]');
		let currentSelectedIndex = parseInt(currentSelectedItem.attr('data-navigate-index'));

		e = e || window.event;
		console.log(e.keyCode);

		if (e.keyCode == 38 || e.keyCode == 40) { // arrows up down

			if (currentSelectedItem.length != 0) {
				console.log('currentSelectedITem exists');
				let indexIncrement = (e.keyCode == 40 ? 1 : -1);
				let newIndex = currentSelectedIndex + indexIncrement;
				let newItem = currentActiveList.find('[data-navigate-index="' + newIndex + '"]');

				if (newItem.length != 0) {
					console.log('navItem exists');
					newItem.addClass(isDisabledItem(currentActiveList, newItem) ? NAVIGATE_DECLINED_CLASS : NAVIGATE_SELECTED_CLASS);
					newItem.attr(NAVIGATE_SELECTED_ATTR, true);
					unActivateItem(currentSelectedItem, true);
					$(currentActiveList).stop(true);
					$(currentActiveList).scrollTo(newItem,320,{axis:'y'},{offset:-16},{queue:false});
				}
			}
		}

		// 1 - 3, arrows left-right
		if ((e.keyCode >= 49 && e.keyCode <= 51) || e.keyCode == 37 || e.keyCode == 39) {
			if (isValidPanelChangeKeyPress(e.keyCode)) {
				$('div[data-panel-index]').each(function () {$(this).removeAttr('data-active-panel').removeClass('keyboard-nav-list-active');});

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

				let activatedList = $('div[data-panel-index="' + selectedPanelIndex + '"]');
				activatedList.attr('data-active-panel', true).addClass('keyboard-nav-list-active');

				let selectedItem = findSelectedNavigateItem(activatedList);
				let isDisabled = isDisabledItem(activatedList, selectedItem);

				selectedItem.addClass(isDisabled ? NAVIGATE_DECLINED_CLASS : NAVIGATE_SELECTED_CLASS);
				selectedItem.attr('data-navigate-selected', true);
			}

		}

		//Esc key
		if (e.keyCode == 27) {
			$('.keyboard-nav-list').each(function () {
				$(this).removeAttr('data-marked-word-id');
				$(this).removeAttr('data-marked-relation-id'); //TODO refactor
				$(this).removeAttr('data-active-panel');
				$(this).find('[data-navigate-index]').each(function () {
					unActivateItem($(this), true);
				});
				$('.keyboard-nav-list-item-selected').removeClass('keyboard-nav-list-item-selected');

				$(this).find('.keyboard-nav-list-item-active').removeClass('keyboard-nav-list-item-active');

				$("#keyboardEditBtn").removeAttr('disabled');

				$(document).find('input[name="simpleSearchFilter"]').val('').focus();
			});
			$('body').removeClass('keyboard-edit-mode-active');
			KEYBOARD_MODE = false;
		}

		//Enter key
		if (e.keyCode == 13) {
			e.preventDefault();
			//IF SYNONYM LIST IS ACTIVE AND USER PRESSES ENTER
			if (currentActivePanelIndex == "3") {

				currentActiveList.removeClass('keyboard-nav-list-active').removeAttr('data-active-panel').find('.keyboard-nav-list-item-selected').each(function () {$(this).removeClass('.keyboard-nav-list-item-selected');});
				currentSelectedItem.addClass('keyboard-nav-list-item-selected');

				unActivateItem(currentSelectedItem, false);
				//TODO refactor
				let wordId = currentSelectedItem.children(':first').attr('data-word-id');
				let relationId = currentSelectedItem.attr('data-id');

				let activatedList = $('div[data-panel-index="' + 2 + '"]');
				activatedList.attr('data-active-panel', true).addClass('keyboard-nav-list-active');

				activatedList.data('marked-word-id', wordId);
				activatedList.data('marked-relation-id', relationId);

				let selectedLexemeItem = findSelectedNavigateItem(activatedList);

				let lexemeExists = selectedLexemeItem.find('input.meaning-word-id[value="' + wordId + '"]').length != 0;

				selectedLexemeItem.addClass(lexemeExists ? NAVIGATE_DECLINED_CLASS : NAVIGATE_SELECTED_CLASS);
				selectedLexemeItem.attr(NAVIGATE_SELECTED_ATTR, true);

			} else if (currentActivePanelIndex == "2") {
				if (!currentSelectedItem.hasClass(NAVIGATE_DECLINED_CLASS)) {
					let wordId = currentActiveList.data('marked-word-id'); //TODO move to a hidden field ? - add a marked attribute to the marked element
					let relationId = currentActiveList.data('marked-relation-id'); //TODO Refactor

					//IF
					if (wordId != undefined) {

						let lexemeId = currentSelectedItem.data('lexeme-id');
						let meaningId = currentSelectedItem.data('meaning-id');

						//TODO - test
						let actionUrl = applicationUrl + 'syn_create_lexeme/' + meaningId + '/' + wordId + '/' + lexemeId + '/' + relationId;
						let callbackFunc = () => $('#refresh-details').trigger('click');

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
