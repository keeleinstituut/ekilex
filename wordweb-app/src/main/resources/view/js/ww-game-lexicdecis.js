var fButtonKeyCode = 70;
var jButtonKeyCode = 74;
var escButtonKeyCode = 27;

var answerDisplayDelay = 400;
var brainlessAnswerDelayTreshold = 250;
var brainlessAnswers = 0;
var gameAnswers = 0;

var gameRowStartTime;
var gameRowStopTime;

var gameBatch = [];
var currentGameRow;
var answerGameRow;

$(document).ready(function() {

});

function populateGameRow() {

	if (gameBatch.length > 0) {
		currentGameRow = gameBatch.pop();
		$("#suggestedWordWrapper").removeClass();
		$("#suggestedWordValue").text(currentGameRow.suggestedWordValue);
		$("#gameValidationNotification").hide();
		gameRowStartTime = new Date().getTime();
	} else if (gameAnswers == 0) {
		getGameBatch();
	} else {
		handleEsc();
	}
}

function getGameBatch() {
	$.get(getLexicDecisGameBatchUrl, function(gameRows) {
		gameBatch = gameRows;
		populateGameRow()
	});
}

function handleAnswerF() {

	$("#gameValidationNotification").hide();
	answerGameRow = Object.assign({}, currentGameRow);
	gameRowStopTime = new Date().getTime();
	var isCorrectAnswer = currentGameRow.word ? false : true;
	answerGameRow.answer = false;
	answerGameRow.delay = gameRowStopTime - gameRowStartTime;
	answerGameRow.correct = isCorrectAnswer;
}

function handleAnswerJ() {

	$("#gameValidationNotification").hide();
	answerGameRow = Object.assign({}, currentGameRow);
	gameRowStopTime = new Date().getTime();
	var isCorrectAnswer = currentGameRow.word ? true : false;
	answerGameRow.answer = true;
	answerGameRow.delay = gameRowStopTime - gameRowStartTime;
	answerGameRow.correct = isCorrectAnswer;
}

function handleEsc() {

	if (gameAnswers == 0) {
		return;
	}
	$("#gameExitMode").val("decent");
	$("#gameResultForm").submit();
}

function resolveAnswer() {

	if (answerGameRow.delay > brainlessAnswerDelayTreshold) {
		$.post(submitLexicDecisGameRowUrl, answerGameRow);
		gameAnswers++;
		brainlessAnswers = 0;
	} else {
		brainlessAnswers++;
	}
	if (brainlessAnswers == 3) {
		$("#gameExitMode").val("brainless");
		$("#gameResultForm").submit();
		return;
	}
	if (answerGameRow.correct) {
		$("#suggestedWordWrapper").addClass("bg-success");
	} else {
		$("#suggestedWordWrapper").addClass("bg-danger");
	}
	setTimeout(function() {
		populateGameRow();
	}, answerDisplayDelay);
}

$(window).keyup(function(e) {

	if (!currentGameRow) {
		return;
	}

	if (e.keyCode == fButtonKeyCode) {
		handleAnswerF();
		resolveAnswer();
	} else if (e.keyCode == jButtonKeyCode) {
		handleAnswerJ();
		resolveAnswer();
	} else if (e.keyCode == escButtonKeyCode) {
		handleEsc();
	} else {
		$("#gameValidationNotification").show();
	}
});

$(document).on("click", "#playGameButton", function(e) {
	$("#gameStartPage").hide();
	$("#gamePage").show();
	getGameBatch();
});

$(document).on("click", "#answerFbtn", function(e) {
	handleAnswerF();
	resolveAnswer();
});

$(document).on("click", "#answerJbtn", function(e) {
	handleAnswerJ();
	resolveAnswer();
});

$(document).on("click", "#escBtn", function(e) {
	handleEsc();
});
