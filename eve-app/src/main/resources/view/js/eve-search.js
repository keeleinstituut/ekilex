// add on click handlers to details buttons in search result table
function initialize(urlPrefix) {
    var detailsDivs = $('#results').find('[name="details"]');
    detailsDivs.on('click', function (e) {
        var id = $(e.target).data('id');
        var detailsDiv = $('[name="' + id + '_details"]');
        if (detailsDiv.html() === '') {
            $.get(urlPrefix + 'details/' + id).done(function (data) {
                detailsDiv.replaceWith(data);
            }).fail(function (data) {
                console.log(data);
                alert('Detailide päring ebaõnnestus, proovige hiljem uuesti.');
            });
        } else {
            detailsDiv.toggle();
        }
    });
    if (detailsDivs.length === 1) {
        detailsDivs.trigger('click');
    }

    $('#start-rec-btn').on('click', function (e) {
        $('#start-rec-btn').prop('hidden','hidden');
        $('#stop-rec-btn').prop('hidden', null);
        $('#serch-btn').prop('disabled', true);
        startRecording();
    });

    $('#stop-rec-btn').on('click', function (e) {
        $('#stop-rec-btn').prop('hidden','hidden');
        $('#start-rec-btn').prop('hidden', null);
        $('#serch-btn').prop('disabled', false);
        stopRecording(function(audioBlob) {
            sendToWebSocket(audioBlob);
        });
    })
}

function playSound(soundSource) {
    var music = new Audio(soundSource);
    music.play();
}

var audio_context;
var recorder;
var audio_stream;
var ws;
var speechRecognitionServiceUrl;
/**
 * Patch the APIs for every browser that supports them and check
 * if getUserMedia is supported on the browser.
 */
function initializeRecording(serviceUrl) {
    try {
        speechRecognitionServiceUrl = serviceUrl;
        // Monkeypatch for AudioContext, getUserMedia and URL
        window.AudioContext = window.AudioContext || window.webkitAudioContext;
        navigator.getUserMedia = navigator.getUserMedia || navigator.webkitGetUserMedia || navigator.mozGetUserMedia;
        window.URL = window.URL || window.webkitURL;

        audio_context = new AudioContext;
        console.log('Audio context is ready !');
        console.log('navigator.getUserMedia ' + (navigator.getUserMedia ? 'available.' : 'not present!'));
    } catch (e) {
        alert('No web audio support in this browser!');
    }
}

function startRecording() {
    // Access the Microphone using the navigator.getUserMedia method to obtain a stream
    navigator.getUserMedia({audio: true}, function (stream) {
        // Expose the stream to be accessible globally
        audio_stream = stream;
        var input = audio_context.createMediaStreamSource(stream);
        console.log('Media stream succesfully created');

        recorder = new Recorder(input);
        console.log('Recorder initialised');

        // Start recording !
        recorder && recorder.record();
        console.log('Recording...');
    }, function (e) {
        console.error('No live audio input: ' + e);
    });
}

function stopRecording(callback) {
    // Stop the recorder instance
    recorder && recorder.stop();
    audio_stream.getAudioTracks()[0].stop();
    console.log('Stopped recording.');

    // Use the Recorder Library to export the recorder Audio as a .mp3 file
    // The callback providen in the stop recording method receives the blob
    if(typeof(callback) == "function"){
        recorder && recorder.exportWAV(function (blob) {
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

        ws.onmessage = function (evt) {
            var data = evt.data;
            console.log("Message is received...", data);
            var res = JSON.parse(data);
            if (res.status == 0) {
                if (res.result) {
                    if (res.result.final) {
                        console.log(res.result.hypotheses);
                        console.log(res.result.hypotheses[0].transcript);
                        $('[name="searchFilter"]').val(res.result.hypotheses[0].transcript);
                        $('#serch-btn').trigger('click');
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