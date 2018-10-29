var fButtonKeyCode = 70;
var jButtonKeyCode = 74;
var escButtonKeyCode = 27;

var minimumDelayTime = 250;
var brainlessAnswers = 0;
var gameAnswers = 0;

var lexicalDecisionStartTime;
var lexicalDecisionStopTime;

var lexicDecisGameBatch = [];
var currentLexicDecisGameRow;
var answerLexicDecisGameRow;

$(document).ready(function() {

});

$(document).on("click", "#playGameButton", function(e) {
	$("#lexicalDecisionStartPage").hide();
	$("#lexicalDecisionGamePage").show();
	getLexicDecisGameBatch();
});

function populateLexicDecisGameRow() {

	if (lexicDecisGameBatch.length > 0) {
		currentLexicDecisGameRow = lexicDecisGameBatch.pop();
		$("#suggestedWordValue").text(currentLexicDecisGameRow.suggestedWordValue);
		$("#lexicDecisValidationNotification").hide();
		lexicalDecisionStartTime = new Date().getTime();
	} else {
		getLexicDecisGameBatch();
	}
}

function getLexicDecisGameBatch() {
	$.get(getLexicDecisGameBatchUrl, function(gameRows) {
		lexicDecisGameBatch = gameRows;
		populateLexicDecisGameRow()
	});
}

$(window).keyup(function(e) {

	if (!currentLexicDecisGameRow) {
		return;
	}

	if (e.keyCode == fButtonKeyCode) {

		answerLexicDecisGameRow = Object.assign({}, currentLexicDecisGameRow);
		lexicalDecisionStopTime = new Date().getTime();
		answerLexicDecisGameRow.answer = false;
		answerLexicDecisGameRow.delay = lexicalDecisionStopTime - lexicalDecisionStartTime;
		answerLexicDecisGameRow.correct = currentLexicDecisGameRow.word ? false : true;

		if (answerLexicDecisGameRow.delay > minimumDelayTime) {
			$.post(submitLexicDecisGameRowUrl, answerLexicDecisGameRow);
			gameAnswers++;
			brainlessAnswers = 0;
		} else {
			brainlessAnswers++;
		}

		populateLexicDecisGameRow();

	} else if (e.keyCode == jButtonKeyCode) {

		answerLexicDecisGameRow = Object.assign({}, currentLexicDecisGameRow);
		lexicalDecisionStopTime = new Date().getTime();
		answerLexicDecisGameRow.answer = true;
		answerLexicDecisGameRow.delay = lexicalDecisionStopTime - lexicalDecisionStartTime;
		answerLexicDecisGameRow.correct = currentLexicDecisGameRow.word ? true : false;

		if (answerLexicDecisGameRow.delay > minimumDelayTime) {
			$.post(submitLexicDecisGameRowUrl, answerLexicDecisGameRow);
			gameAnswers++;
			brainlessAnswers = 0;
		} else {
			brainlessAnswers++;
		}

		populateLexicDecisGameRow();

	} else if (e.keyCode == escButtonKeyCode) {

		if (gameAnswers == 0) {
			return;
		}
		$("#lexicDecisExitMode").val("decent");
		$("#lexicalDecisionResultForm").submit();

	} else {

		$("#lexicDecisValidationNotification").show();
	}

	if (brainlessAnswers == 3) {
		$("#lexicDecisExitMode").val("brainless");
		$("#lexicalDecisionResultForm").submit();
	}

});

