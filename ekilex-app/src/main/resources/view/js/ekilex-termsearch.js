// add on click handlers to details buttons in search result table
function initialise() {
	$(document).on("click", ":button[name='detailsBtn']", function() {
		var id = $(this).data('id');
		var isRestoreScrollPos = this.hasAttribute('data-refresh');
		$.get(applicationUrl + 'meaningdetails/' + id).done(function(data) {
			var scrollPos = $('#details_div').scrollTop();
			$('#details_div').replaceWith(data);
			if (isRestoreScrollPos) {
				$('#details_div').scrollTop(scrollPos);
			}
		}).fail(function(data) {
			console.log(data);
			alert('Detailide päring ebaõnnestus, proovige hiljem uuesti.');
		});
	});

	$(document).on('click', '.order-up', function() {
		var clickable = $(this);
		var orderingData = changeItemOrdering(clickable, -1);
		updateTermUserLangWrapup(clickable);
		postJson(applicationUrl + 'modify_ordering', orderingData).done(function(data) {
			refreshDetails();
		});
	});

	$(document).on('click', '.order-down', function() {
		var clickable = $(this);
		var orderingData = changeItemOrdering(clickable, 1);
		updateTermUserLangWrapup(clickable);
		postJson(applicationUrl + 'modify_ordering', orderingData).done(function(data) {
			refreshDetails();
        });
	});

	$(document).on('click', '#show-all-btn', function() {
		$('#fetchAll').val(true);
		$('#fetchAll').closest('form').find('button[type="submit"]').trigger('click');
	});

	$(document).on('click', '[name="term_user_lang_check"]', function() {
		var clickable = $(this);
		var opCode = clickable.closest("[data-op-code]").attr("data-op-code");
		var index = clickable.closest("[data-index]").attr("data-index");
		var selected = clickable.is(":checked");
		var itemData = {
			opCode : opCode,
			selected : selected,
			index : index
		};
		updateTermUserLangWrapup(clickable);
		postJson(applicationUrl + 'modify_item', itemData);
		refreshDetails();
	});

	var detailsButtons = $('#results').find('[name="detailsBtn"]');
	if (detailsButtons.length === 1) {
		detailsButtons.trigger('click');
	}

    var editDlg = $('#editDlg');
	editDlg.find('[name=value]').attr("rows", 4);
    editDlg.off('shown.bs.modal').on('shown.bs.modal', function(e) {
        alignAndFocus(e, editDlg)
    });

    $('#addNewDefinitionDlg').find('[name=value]').attr("rows", 4);
    initMultiValueAddDlg($('#addNewUsageMemberDlg'));
}

function updateTermUserLangWrapup(clickable) {
	var langWrapupArr = clickable.closest('.orderable').find("[data-value]").filter(function() {
		return $(this).find("input[name='term_user_lang_check']").is(":checked");
	}).map(function() {
		return $(this).attr("data-value");
	}).get();
	var languagesWrapupLimit = 4;
	var langWrapup;
	if (langWrapupArr.length > languagesWrapupLimit) {
		langWrapup = langWrapupArr.slice(0, languagesWrapupLimit).join(", ") + " ...";
	} else {
		langWrapup = langWrapupArr.join(", ");
	}
	$("#term_user_lang_wrapup").text(langWrapup);
}

function refreshDetails() {
	var refreshButton = $('#refresh-details');
    refreshButton.trigger('click');
}