function initialise() {
	$(document).on("click", ":button[name='synDetailsBtn']", function() {
		let id = $(this).data('id');

		$("[id^='syn_select_point_']").hide();
		$("[id^='syn_select_wait_']").hide();
		$("#syn_select_wait_" + id).show();
		$.get(applicationUrl + 'syn_worddetails/' + id).done(function(data) {
			let detailsDiv = $('#syn_details_div');
			detailsDiv.replaceWith(data);
			$("#syn_select_wait_" + id).hide();
			$("#syn_select_point_" + id).show();

			$(document).find('.draggable-synonym').draggable({ revert: "invalid" });

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

	function checkKey(e) {
		var tag = e.target.tagName.toLowerCase();
		if ( tag == 'input' || tag == 'textarea') {
			return;
		}

		let PANEL_KEYCODES = {"49": "1", "50": "2", "51" : "3"};
		let DISABLED_PANEL_CLASS = 'navigate-disabled-panel';
		let NAVIGATE_SELECTED_CLASS = 'navigate-selected';


		e = e || window.event;
		//console.log(e.keyCode);
		// 1 - 3
		if (e.keyCode >= 49 && e.keyCode <= 51) {
			$('.navigate-panel').each(function (e) {
				$(this).addClass(DISABLED_PANEL_CLASS);
				$(this).removeAttr('data-active-panel');
			});

			let activatedDiv = $('div[data-panel-index="' + PANEL_KEYCODES[e.keyCode] + '"]');

			activatedDiv.removeClass(DISABLED_PANEL_CLASS);
			activatedDiv.attr('data-active-panel', true);

			let selectedItem = activatedDiv.find('.navigate-selected');

			if (selectedItem.length == 0) {
				console.log("-- no selected item --");
				let itemToSelect;
				if (e.keyCode == 49) {
					let selectedWordId = $('#syn_details_div').data('id');
					console.log('selected word Id ' + selectedWordId);
					itemToSelect = $('#syn_select_point_' + selectedWordId).closest('.navigate-item');
				} else {
					itemToSelect = activatedDiv.find('[data-navigate-index="0"]');
				}
				itemToSelect.addClass(NAVIGATE_SELECTED_CLASS);
			}

		}
		if (e.keyCode == 27) { //esc
			$('.navigate-panel').each(function (e) {
				$(this).removeClass(DISABLED_PANEL_CLASS);
				$(this).removeAttr('data-active-panel');
				$(this).find('[data-navigate-index]').removeClass(NAVIGATE_SELECTED_CLASS);
			});
		}

		if (e.keyCode == 38 || e.keyCode == 40) { // arrows
			let activeDiv = $('div[data-active-panel]');
			let selectedItem = activeDiv.find('.' + NAVIGATE_SELECTED_CLASS);

			if (selectedItem.length != 0) {
				console.log("-- selected item found --");

				let indexIncrement = (e.keyCode == 40 ? 1 : -1);
				let selectedIndex = parseInt(selectedItem.attr('data-navigate-index'));
				console.log("selected index " + selectedIndex);

				let newIndex = selectedIndex + indexIncrement;
				console.log("new index " + newIndex);
				let newItem = activeDiv.find('[data-navigate-index="' + newIndex + '"]');

				if (newItem.length !=0) {
					console.log("new item found");
					newItem.addClass(NAVIGATE_SELECTED_CLASS);
					selectedItem.removeClass(NAVIGATE_SELECTED_CLASS);
				}
			}
		}

		if (e.keyCode == 13) {
			e.preventDefault();
			let activeDiv = $('div[data-active-panel]');
			let selectedItem = activeDiv.find('.' + NAVIGATE_SELECTED_CLASS);

			let panelIndex = activeDiv.data('panel-index');

			if (panelIndex == "3") {
				selectedItem.removeClass(NAVIGATE_SELECTED_CLASS);
				selectedItem.addClass('navigate-marked');

				activeDiv.addClass(DISABLED_PANEL_CLASS);
				activeDiv.removeAttr('data-active-panel');


				let activatedDiv = $('div[data-panel-index="2"]');

				activatedDiv.removeClass(DISABLED_PANEL_CLASS);
				activatedDiv.attr('data-active-panel', true);

				let wordId = selectedItem.children(':first').attr('data-word-id');
				console.log(' -- - word id ' + wordId);

				activatedDiv.data('navigate-selected-word-id', wordId);

				let selectedLexemeItem = activatedDiv.find('.navigate-selected');

				if (selectedLexemeItem.length == 0) {
					console.log("-- no selected item --");
					let itemToSelect = activatedDiv.find('[data-navigate-index="0"]');
					itemToSelect.addClass(NAVIGATE_SELECTED_CLASS);
				}


			}
		}


	}

	$(document).on('keydown', checkKey);

}


