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
