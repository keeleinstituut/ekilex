$(document).ready(function() {
	initialiseRecording(speechRecognitionServiceUrl);
});

function playAudio(audioUrl, onEndedCallback) {
	var music = new Audio(audioUrl);
	music.onended = onEndedCallback;
	music.play();
}

function getAndPlayAudio(e) {
	var elem = $(e);
	if (elem.data('is-playing') !== undefined) {
		return;
	}
	elem.data('is-playing', '');

	var content;
	var buttonSpeaker = elem.find('.btn-speaker').addBack('.btn-speaker');
	if (buttonSpeaker.length !== 0) {
		content = buttonSpeaker.html();
		buttonSpeaker.html('<i class="fa fa-spinner fa-spin"></i>');
	}
	var onEndCallback = function() {
		elem.removeData('is-playing');
		if (buttonSpeaker.length !== 0) {
			buttonSpeaker.html(content);
		}
	};

	var definedAudioUrl = elem.data('audio-url');
	if (definedAudioUrl !== undefined) {
		playAudio(definedAudioUrl, onEndCallback);
		return;
	}

	var data = {
		'text': elem.data('text'),
		'serviceId': elem.data('service-id')
	};
	$.post(applicationUrl + 'audio-link', data)
		.done(function(providedAudioUrl) {
			if (providedAudioUrl) {
				elem.data('audio-url', providedAudioUrl);
				playAudio(providedAudioUrl, onEndCallback);
			} else {
				onEndCallback();
			}
		}).fail(function() {
			onEndCallback();
			alert(messages.audio_generation_failure);
		})
}

var audio_context;
var recorder;
var audio_stream;
var ws;
var speechRecognitionServiceUrl;
/**
 * Patch the APIs for every browser that supports them and check if getUserMedia
 * is supported on the browser.
 */
function initialiseRecording(serviceUrl) {
	try {
		speechRecognitionServiceUrl = serviceUrl;
		// Monkeypatch for AudioContext, getUserMedia and URL
		window.AudioContext = window.AudioContext || window.webkitAudioContext;
		navigator.getUserMedia = navigator.getUserMedia || navigator.webkitGetUserMedia || navigator.mozGetUserMedia;
		window.URL = window.URL || window.webkitURL;

		audio_context = new AudioContext;
		var isUserMediaPresent = navigator.getUserMedia ? 'available.' : 'not present!';
		console.log('Audio context is ready !');
		console.log('navigator.getUserMedia ' + isUserMediaPresent);
	} catch (e) {
		console.log('No web audio support in this browser!');
	}
}

function startRecording() {
	// Access the Microphone using the navigator.getUserMedia method to obtain a stream
	navigator.getUserMedia({
		audio: true
	}, function(stream) {
		// Expose the stream to be accessible globally
		audio_stream = stream;
		var input = audio_context.createMediaStreamSource(stream);
		console.log('Media stream succesfully created');

		recorder = new Recorder(input);
		console.log('Recorder initialised');

		// Start recording !
		recorder && recorder.record();
		console.log('Recording...');
	}, function(e) {
		console.error('No live audio input: ' + e);
	});
}

function stopRecording(callback) {
	// Stop the recorder instance
	recorder && recorder.stop();

	try {
		audio_stream.getAudioTracks()[0].stop();
	} catch (e) {
		console.log('There is nothing to stop?');
	}

	console.log('Stopped recording.');

	// Use the Recorder Library to export the recorder Audio as a .mp3 file
	// The callback providen in the stop recording method receives the blob
	if (typeof (callback) == "function") {
		recorder && recorder.exportWAV(function(blob) {
			callback(blob);
			// Clear the Recorder to start again !
			recorder.clear();
		}, ("audio/mpeg"));
	}
}

function sendToWebSocket(audioBlob) {
	if ("WebSocket" in window) {
		if (ws) {
			ws.close();
			ws = null;
		}
		// Let us open a web socket
		// ws = new WebSocket("ws://localhost:9090/client/ws/speech");
		ws = new WebSocket(speechRecognitionServiceUrl);
		ws.onopen = function() {
			// Web Socket is connected, send data using send()
			ws.send(audioBlob);
			ws.send("EOS");
			console.log("Message is sent...");
		};

		ws.onmessage = function(evt) {
			var data = evt.data;
			console.log("Message is received...", data);
			var res = JSON.parse(data);
			if (res.status == 0) {
				if (res.result) {
					if (res.result.final) {
						$('.search-phrase').val(res.result.hypotheses[0].transcript);
						$('.search-btn, .wordgame-search__button-search').trigger('click');
					}
				}
			}
		};

		ws.onclose = function(e) {
			var code = e.code;
			var reason = e.reason;
			var wasClean = e.wasClean;
			console.log(e.code + "/" + e.reason + "/" + e.wasClean);
			ws = null;
		};

		ws.onerror = function(e) {
			var data = e.data;
			console.log("Error ", data);
		};

		window.onbeforeunload = function(event) {
			socket.close();
		};
	} else {
		console.log("WebSocket NOT supported by your Browser!");
	}
}

$(document).on("click", "#start-rec-btn", function(e) {
	$('#start-rec-btn').prop('hidden', 'hidden');
	$('#stop-rec-btn').prop('hidden', null);
	$('.search-btn').prop('disabled', true);
	startRecording();
});

$(document).on("click", "#stop-rec-btn", function(e) {
	$('#stop-rec-btn').prop('hidden', 'hidden');
	$('#start-rec-btn').prop('hidden', null);
	$('.search-btn').prop('disabled', false);
	stopRecording(function(audioBlob) {
		sendToWebSocket(audioBlob);
	});
});
