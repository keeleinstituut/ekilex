
function fetchDetails(wordId, word) {
    var detailsDiv = $('.word-details');
	$.get(applicationUrl + 'worddetails/' + wordId).done(function (data) {
		detailsDiv.replaceWith(data);
        // these need to be present after each fetchDetails//
        $('.word-details [data-toggle="tooltip"]').tooltip();
        ////
        fetchCorpSentences(word);
    }).fail(function (data) {
		console.log(data);
		alert('Detailide päring ebaõnnestus, proovige hiljem uuesti.');
	})
}

function fetchCorpSentences(sentence) {
    var corpDiv = $('[name="korp"]');
    $.get(applicationUrl + 'korp/' + sentence).done(function (data) {
        corpDiv.replaceWith(data);
    }).fail(function (data) {
        console.log(data);
    })
}

function playSound(soundSource) {
    var music = new Audio(soundSource);
    music.play();
}

function generateVoiceAndPlay(e) {
    var elem = $(e);
    if (elem.data('url-to-sound') != undefined) {
        playSound(elem.data('urlToSound'));
        return;
    }
    var content = elem.html();
    elem.html(content + ' <i class="fa fa-spinner fa-spin"></i>');
    $.post(applicationUrl + 'generate_voice', {'words': elem.data('words')}).done(function(urlToSound) {
        elem.data('url-to-sound', urlToSound);
        elem.html(content);
        playSound(urlToSound);
    }).fail(function() {
        elem.html(content);
        console.log(data);
        alert("Heli genereerise teenus hetkel ei toimi, proovige palun hiljem uuesti.");
    })
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
function initialiseRecording(serviceUrl) {
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
                        $('.search-phrase').val(res.result.hypotheses[0].transcript);
                        $('.search-btn').trigger('click');
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

function initialisePage() {
    $(".menu-btn").click(function(){
        $(".header-links").toggleClass("d-none d-md-block");
    });
    // $(".search-phrase").focus(function() {
    //     $(".awesomplete ul").removeClass("d-none");
    // });
    // $( ".search-phrase" ).focusout(function() {
    //     $(".awesomplete ul").addClass("d-none");
    // });
    $(document).on("click", ".more-btn", function() {
        $(this).parent().toggleClass("expand");
        $(".additional-meta, .dictionary-source, .dependence:not(:first-child)").toggleClass("fade-target");
    });

    // demo js for interactions between the mobile and desktop modes
    $(".logo").click(function(){
        if ($(".homonym-panel").hasClass( "d-none" )) {
            $(".content-panel").addClass("d-none d-md-block");
            $(".homonym-panel").removeClass("d-none d-md-block");
            $(".search-panel").removeClass("d-none d-md-block");
        }
    });
    $(".homonym-item").click(function(){
        $(".homonym-item").removeClass("selected last-selected");
        $(".homonym-item:first").removeClass("animation-target").dequeue();
        $(this).addClass("selected last-selected");
        if ($(window).width() < 767) {
            $(".content-panel").removeClass("d-none d-md-block");
            $(".homonym-panel").addClass("d-none d-md-block");
            $(".search-panel").addClass("d-none d-md-block");
        }
        if ($(window).width() > 766) {
          $('.homonym-list').animate({
              scrollLeft: $('.homonym-item.selected').position().left - $('.search-panel').offset().left + 10 + $('.homonym-list').scrollLeft()
            },
            200);
        }



    });

    $(window).resize(function() {
        if ($(window).width() < 768) {
            $(".homonym-item").removeClass("selected");
            $(".content-panel").addClass("d-none d-md-block");
            $(".homonym-panel").removeClass("d-none d-md-block");
            $(".search-panel").removeClass("d-none d-md-block");
        }
        else {
            $(".last-selected").addClass("selected");
            if (!$(".homonym-item").hasClass("last-selected")) {
                $(".homonym-item:first").addClass("selected last-selected");
            }
        }
    });

    $('#start-rec-btn').on('click', function (e) {
        $('#start-rec-btn').prop('hidden','hidden');
        $('#stop-rec-btn').prop('hidden', null);
        $('.search-btn').prop('disabled', true);
        startRecording();
    });

    $('#stop-rec-btn').on('click', function (e) {
        $('#stop-rec-btn').prop('hidden','hidden');
        $('#start-rec-btn').prop('hidden', null);
        $('.search-btn').prop('disabled', false);
        stopRecording(function(audioBlob) {
            sendToWebSocket(audioBlob);
        });
    });

    $('.clear-btn').on('click', function (e) {
        $('.search-phrase').val(null);
        $('.search-btn').trigger('click');
    });

    $(document).ready(function() {
        $(".homonym-item:first")
            .delay(1250).queue(function(){})
            .trigger('click');
        $(".homonym-item:first").addClass("animation-target");
        $('[data-toggle="tooltip"]').tooltip();
    });
}