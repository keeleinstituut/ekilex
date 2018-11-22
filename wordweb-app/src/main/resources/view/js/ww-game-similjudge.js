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

		//TODO remove previous answer correctness notification
		//$("#suggestedWordWrapper").removeClass();

		$("#synonymPair1Word1").text(currentGameRow.wordPair1.word1);
		$("#synonymPair1Word2").text(currentGameRow.wordPair1.word2);
		$("#synonymPair2Word1").text(currentGameRow.wordPair2.word1);
		$("#synonymPair2Word2").text(currentGameRow.wordPair2.word2);
		$("#gameValidationNotification").hide();
		gameRowStartTime = new Date().getTime();
	} else if (gameAnswers == 0) {
		getGameBatch();
	} else {
		handleEsc();
	}
}

function getGameBatch() {
	var getSimilJudgeGameBatchUrlUrlWithParams = getSimilJudgeGameBatchUrl + "/" + gameKey;
	$.get(getSimilJudgeGameBatchUrlUrlWithParams, function(gameRows) {
		if (gameRows.length > 0) {
			gameBatch = gameRows;
			populateGameRow()
		}
	});
}

function handleAnswerF() {

	$("#gameValidationNotification").hide();
	answerGameRow = Object.assign({}, currentGameRow);
	gameRowStopTime = new Date().getTime();
	answerGameRow.answerPair1 = true;
	answerGameRow.answerPair2 = false;
	answerGameRow.delay = gameRowStopTime - gameRowStartTime;
	answerGameRow.correct = currentGameRow.wordPair1.synonym;
}

function handleAnswerJ() {

	$("#gameValidationNotification").hide();
	answerGameRow = Object.assign({}, currentGameRow);
	gameRowStopTime = new Date().getTime();
	answerGameRow.answerPair1 = false;
	answerGameRow.answerPair2 = true;
	answerGameRow.delay = gameRowStopTime - gameRowStartTime;
	answerGameRow.correct = currentGameRow.wordPair2.synonym;
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
		var answerGameRowSer = JSON.stringify(answerGameRow);
		$.ajax({
			url : submitSimilJudgeGameRowUrl,
			type : "POST",
			dataType : "json",
			contentType : "application/json",
			data : answerGameRowSer
		});
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
	/*
	 * TODO notify about answer correctness
	 * 
	if (answerGameRow.correct) {
		$("#suggestedWordWrapper").addClass("bg-success");
	} else {
		$("#suggestedWordWrapper").addClass("bg-danger");
	}
	*/
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
