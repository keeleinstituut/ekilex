$(document).ready(function() {
	focusSearchInput();
});

function focusSearchInput() {
	const searchInput = document.getElementById('searchInput');
	searchInput.focus();
	const textLength = searchInput.value?.length ?? 0;
	// Set text cursor to end, goes to beginning by default
	searchInput.setSelectionRange(textLength, textLength);
}

$(document).on('click', 'ext-link', function() {
	const link = $(this);
	const href = link.attr('href');
	if (href) {
		const target = link.attr('target');
		if (href.startsWith('https://')) {
			window.open(href, target);
		} else {
			window.open(`https://${href}`, target);
		}
	} else {
		openAlertDlg(messages["common.broken.link"]);
	}
});
