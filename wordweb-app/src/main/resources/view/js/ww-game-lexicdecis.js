var fButtonKeyCode = 70;
var jButtonKeyCode = 74;
var escButtonKeyCode = 27;

var answerDisplayDelay = 400;
var brainlessAnswerDelayTreshold = 250;
var brainlessAnswers = 0;
var gameAnswers = 0;

var lexicalDecisionStartTime;
var lexicalDecisionStopTime;

var lexicDecisGameBatch = [];
var currentLexicDecisGameRow;
var answerLexicDecisGameRow;

$(document).ready(function() {

});

function populateLexicDecisGameRow() {

	if (lexicDecisGameBatch.length > 0) {
		currentLexicDecisGameRow = lexicDecisGameBatch.pop();
		$("#suggestedWordWrapper").removeClass();
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

function handleFanswer() {

	$("#lexicDecisValidationNotification").hide();
	answerLexicDecisGameRow = Object.assign({}, currentLexicDecisGameRow);
	lexicalDecisionStopTime = new Date().getTime();
	var isCorrectAnswer = currentLexicDecisGameRow.word ? false : true;
	answerLexicDecisGameRow.answer = false;
	answerLexicDecisGameRow.delay = lexicalDecisionStopTime - lexicalDecisionStartTime;
	answerLexicDecisGameRow.correct = isCorrectAnswer;
}

function handleJanswer() {

	$("#lexicDecisValidationNotification").hide();
	answerLexicDecisGameRow = Object.assign({}, currentLexicDecisGameRow);
	lexicalDecisionStopTime = new Date().getTime();
	var isCorrectAnswer = currentLexicDecisGameRow.word ? true : false;
	answerLexicDecisGameRow.answer = true;
	answerLexicDecisGameRow.delay = lexicalDecisionStopTime - lexicalDecisionStartTime;
	answerLexicDecisGameRow.correct = isCorrectAnswer;
}

function resolveAnswer() {

	if (answerLexicDecisGameRow.delay > brainlessAnswerDelayTreshold) {
		$.post(submitLexicDecisGameRowUrl, answerLexicDecisGameRow);
		gameAnswers++;
		brainlessAnswers = 0;
	} else {
		brainlessAnswers++;
	}
	if (brainlessAnswers == 3) {
		$("#lexicDecisExitMode").val("brainless");
		$("#lexicalDecisionResultForm").submit();
		return;
	}
	if (answerLexicDecisGameRow.correct) {
		$("#suggestedWordWrapper").addClass("bg-success");
	} else {
		$("#suggestedWordWrapper").addClass("bg-danger");
	}
	setTimeout(function() {
		populateLexicDecisGameRow();			
	}, answerDisplayDelay);
}

$(window).keyup(function(e) {

	if (!currentLexicDecisGameRow) {
		return;
	}

	if (e.keyCode == fButtonKeyCode) {

		handleFanswer();
		resolveAnswer();

	} else if (e.keyCode == jButtonKeyCode) {

		handleJanswer();
		resolveAnswer();

	} else if (e.keyCode == escButtonKeyCode) {

		if (gameAnswers == 0) {
			return;
		}
		$("#lexicDecisExitMode").val("decent");
		$("#lexicalDecisionResultForm").submit();

	} else {

		$("#lexicDecisValidationNotification").show();
	}
});

$(document).on("click", "#playGameButton", function(e) {
	$("#lexicalDecisionStartPage").hide();
	$("#lexicalDecisionGamePage").show();
	getLexicDecisGameBatch();
});

$(document).on("click", "#answerFbtn", function(e) {
	handleFanswer();
	resolveAnswer();
});

$(document).on("click", "#answerJbtn", function(e) {
	handleJanswer();
	resolveAnswer();
});
