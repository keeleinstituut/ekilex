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

			$(document).find('.droppable-lexeme').droppable(
			{
				accept:".draggable-synonym",
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

					//let callbackFunc = () => $('#refresh-details').trigger('click');
					//
					// $(this)
					// 	.addClass("ui-state-highlight")
					// 	.find("p")
					// 	.html("Dropped!");
				}
			}
			);

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


}
