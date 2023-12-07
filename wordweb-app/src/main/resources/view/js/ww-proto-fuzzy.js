function initFuzzyPage() {

	$("#fuzzy-infixlev-inp").autocomplete({
		source: function(request, response) {
			$.ajax({
				url: fuzzySearchByAlgoUrl,
				data: { wordCrit: request.term, algo: 'infixlev' },
				success: function(data) {
					response(data.words);
					$("#fuzzy-infixlev-log").text(data.logMessage)
				}
			});
		},
		minLength: 3
	});

	$("#fuzzy-trigramsim-inp").autocomplete({
		source: function(request, response) {
			$.ajax({
				url: fuzzySearchByAlgoUrl,
				data: { wordCrit: request.term, algo: 'trigramsim' },
				success: function(data) {
					response(data.words);
					$("#fuzzy-trigramsim-log").text(data.logMessage)
				}
			});
		},
		minLength: 3
	});

	$("#fuzzy-levenshteindist-inp").autocomplete({
		source: function(request, response) {
			$.ajax({
				url: fuzzySearchByAlgoUrl,
				data: { wordCrit: request.term, algo: 'levenshteindist' },
				success: function(data) {
					response(data.words);
					$("#fuzzy-levenshteindist-log").text(data.logMessage)
				}
			});
		},
		minLength: 3
	});

	$("#fuzzy-levenshteinlessdist-inp").autocomplete({
		source: function(request, response) {
			$.ajax({
				url: fuzzySearchByAlgoUrl,
				data: { wordCrit: request.term, algo: 'levenshteinlessdist' },
				success: function(data) {
					response(data.words);
					$("#fuzzy-levenshteinlessdist-log").text(data.logMessage)
				}
			});
		},
		minLength: 3
	});

	$("#fuzzy-metaphonesim-inp").autocomplete({
		source: function(request, response) {
			$.ajax({
				url: fuzzySearchByAlgoUrl,
				data: { wordCrit: request.term, algo: 'metaphonesim' },
				success: function(data) {
					response(data.words);
					$("#fuzzy-metaphonesim-log").text(data.logMessage)
				}
			});
		},
		minLength: 3
	});

	$("#fuzzy-daitchmokotoffsim-inp").autocomplete({
		source: function(request, response) {
			$.ajax({
				url: fuzzySearchByAlgoUrl,
				data: { wordCrit: request.term, algo: 'daitchmokotoffsim' },
				success: function(data) {
					response(data.words);
					$("#fuzzy-daitchmokotoffsim-log").text(data.logMessage)
				}
			});
		},
		minLength: 3
	});
}