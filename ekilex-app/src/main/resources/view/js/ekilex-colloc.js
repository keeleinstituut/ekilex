$.fn.lexemeCollocMemberGroupOrderPlugin = function() {
	return this.each(function() {
		const btn = $(this);
		btn.on('click', function() {
			const collocLexemeId = btn.attr("data-colloc-lexeme-id");
			const memberLexemeId = btn.attr("data-member-lexeme-id");
			const direction = btn.attr("data-direction");
			const successCallback = btn.attr("data-callback");
			const successCallbackFunc = createCallback(successCallback);
			const data = {
				collocLexemeId: collocLexemeId,
				memberLexemeId: memberLexemeId,
				direction: direction
			};
			postJson(applicationUrl + 'update_colloc_member_group_order', data).done(function() {
				successCallbackFunc();
			});
		})
	})
}

$.fn.collocMemberOrderPlugin = function() {
	return this.each(function() {
		const btn = $(this);
		btn.on('click', function() {
			const collocLexemeId = btn.attr("data-colloc-lexeme-id");
			const memberLexemeId = btn.attr("data-member-lexeme-id");
			const direction = btn.attr("data-direction");
			const successCallback = btn.attr("data-callback");
			const successCallbackFunc = createCallback(successCallback);
			const data = {
				collocLexemeId: collocLexemeId,
				memberLexemeId: memberLexemeId,
				direction: direction
			};
			postJson(applicationUrl + 'update_colloc_member_order', data).done(function() {
				successCallbackFunc();
			});
		})
	})
}

$.fn.collocMemberMovePlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('click', function() {
			const collocMemberMoveForm = obj.closest('form');
			const collocMemberMoveModal = collocMemberMoveForm.closest('.modal');
			const actionUrl = collocMemberMoveForm.attr('action');
			const successCallback = collocMemberMoveModal.attr("data-callback");
			let successCallbackFunc = createCallback(successCallback);

			let collocLexemeIdArr = [];
			$.each($("input[name='collocLexemeIds']:checked"), function() {
				collocLexemeIdArr.push($(this).val());
			});
			let collocLexemeIds = collocLexemeIdArr.join(",");
			collocMemberMoveForm.find('input[name="collocLexemeIds"]').val(collocLexemeIds);

			$.ajax({
				url: actionUrl,
				data: collocMemberMoveForm.serialize(),
				method: 'POST'
			}).done(function() {
				collocMemberMoveModal.modal('hide');
				successCallbackFunc();
			}).fail(function(data) {
				console.log(data);
				openAlertDlg(messages["common.error"]);
			});
		})
	});
}


$.fn.collocPosGroupTogglePlugin = function() {
	return this.each(function() {
		const toggle = this;
		let state = toggle.getAttribute('aria-checked') === 'true';
		toggle.addEventListener('click', () => {
			state = !state;
			toggle.setAttribute('aria-checked', state);
			const posGroup = toggle.closest('[data-colloc-pos-group]')?.getAttribute('data-colloc-pos-group');
			if (!posGroup) {
				console.warn('Could not find colloc pos group value');
				return;
			}
			const checkboxes = document.querySelectorAll(`[data-colloc-rel-group-for='${posGroup}'] input[type='checkbox']`);
			for (const checkbox of checkboxes) {
				if (state) {
					checkbox.classList?.remove('d-none');
				} else {
					checkbox.classList?.add('d-none');
				}
			}
		});
	});
}

$.fn.collocCheckboxContainerTogglePlugin = function() {
	return this.each(function() {
		const toggle = this;
		let state = toggle.getAttribute('aria-checked') === 'true';
		toggle.addEventListener('click', () => {
			state = !state;
			toggle.setAttribute('aria-checked', state);
			const checkboxes = toggle.closest('[data-colloc-checkbox-container]')?.querySelectorAll("input[type='checkbox']");
			for (const checkbox of checkboxes) {
				if (state) {
					checkbox.classList?.remove('d-none');
				} else {
					checkbox.classList?.add('d-none');
				}
			}
		});
	});
}

$.fn.collocRelGroupTogglePlugin = function() {
	return this.each(function() {
		const mainCheckbox = this;
		const relGroupCode = mainCheckbox?.getAttribute('data-colloc-rel-group');
		if (!relGroupCode) {
			console.warn('Could not find rel group code');
			return;
		}
		const relatedCheckboxesTable = document.querySelector(`table[data-colloc-rel-group='${relGroupCode}']`);
		if (!relatedCheckboxesTable) {
			console.warn('Could not find colloc rel checkboxes table');
			return;
		}
		const checkboxes = Array.from(relatedCheckboxesTable.querySelectorAll("input[type='checkbox']"));
		mainCheckbox.checked = checkboxes.every(checkbox => checkbox.checked);
		relatedCheckboxesTable.addEventListener('click', () => {
			let checkedCount = 0;
			checkboxes.forEach(checkbox => {
				if (checkbox.checked) {
					checkedCount++;
				}
			});
			if (checkedCount === checkboxes.length) {
				mainCheckbox.checked = true;
			} else {
				mainCheckbox.checked = false;
			}
		});

		mainCheckbox.addEventListener('click', () => {
			checkboxes.forEach(checkbox => {
				checkbox.checked = mainCheckbox.checked;
			});
		});
	});
}

$.fn.collocMemberFormSearchPlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('submit', function(e) {
			e.preventDefault();

			const collocMemberSearchForm = obj;
			const actionUrl = collocMemberSearchForm.attr('action');
			const formValue = collocMemberSearchForm.find('input[name="formValue"]').val();

			if (formValue.length === 0) {
				return;
			}
			openWaitDlg();

			$.ajax({
				url: actionUrl,
				data: collocMemberSearchForm.serialize(),
				method: 'POST'
			}).done(function(data) {
				closeWaitDlg();
				$("button[name='collocMemberSaveBtn']").prop('disabled', false);
				$('#add_colloc_member_section').html(data);
				initCollapse('#add_colloc_member_section');
			}).fail(function(data) {
				console.log(data);
				closeWaitDlg();
				openAlertDlg(messages["common.error"]);
			});
		})
	});
}

$.fn.collocMemberSavePlugin = function() {
	return this.each(function() {
		const obj = $(this);
		obj.on('click', function(e) {
			e.preventDefault();

			const collocMemberSaveModal = obj.closest('.modal');
			const collocMemberSaveForm = collocMemberSaveModal.find('form[name="collocMemberSaveForm"]');
			const successCallback = collocMemberSaveModal.attr("data-callback");
			let successCallbackFunc = createCallback(successCallback);
			const actionUrl = collocMemberSaveForm.attr('action');

			$.ajax({
				url: actionUrl,
				data: collocMemberSaveForm.serialize(),
				method: 'POST'
			}).done(function(data) {
				if (data.status == 'OK') {
					collocMemberSaveModal.modal('hide');
					successCallbackFunc();
					openMessageDlg(data.message);
				} else if (data.status == 'INVALID') {
					openAlertDlg(data.message);
				}
			}).fail(function(data) {
				console.log(data);
				openAlertDlg(messages["common.error"]);
			});
		})
	});
}

$.fn.initCollocMemberUpdatePlugin = function() {
	return this.each(function() {
		const dlg = $(this);
		dlg.on('shown.bs.modal', function() {

			const collocMemberMeaningSearchForm = dlg.find('form[name="collocMemberMeaningSearchForm"]');
			const collocMemberId = collocMemberMeaningSearchForm.find('input[name="id"]').val();
			const actionUrl = collocMemberMeaningSearchForm.attr('action');
			openWaitDlg();

			$.ajax({
				url: actionUrl,
				data: collocMemberMeaningSearchForm.serialize(),
				method: 'POST'
			}).done(function(data) {
				closeWaitDlg();
				$("button[name='collocMemberSaveBtn']").prop('disabled', false);
				$('#edit_colloc_member_section_' + collocMemberId).html(data);
				initCollapse('#edit_colloc_member_section_' + collocMemberId);
			}).fail(function(data) {
				console.log(data);
				closeWaitDlg();
				openAlertDlg(messages["common.error"]);
			});
		});
	});
}

function initCollapse(containerSelector) {
	const wrappedContainer = $(containerSelector);
	wrappedContainer.find('input[data-toggle="custom-collapse"]').on('click', function(e) {
		if (e.target.getAttribute('aria-expanded') === 'true') {
			return;
		}
		wrappedContainer.find('input[data-toggle="custom-collapse"]').not(e.target).attr('aria-expanded', 'false');
		wrappedContainer.find('.collapse').removeClass('show');
		const target = e.target.getAttribute('data-target');
		$(target).addClass('show');
		e.target.setAttribute('aria-expanded', 'true');
	});
}
