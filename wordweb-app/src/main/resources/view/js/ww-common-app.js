var sessionTimeoutBufferSec = 60;

$(document).ready(function() {

	var sessionTimeoutMs = (sessionTimeoutSec - sessionTimeoutBufferSec) * 1000;
	setTimeout(function() {
		window.location = applicationUrl;
	}, sessionTimeoutMs);

	$('[data-toggle="tooltip"]').tooltip();
});

$(document).on("click", ".menu-btn", function(e) {
	$(".header-container").toggleClass("show-header");
});

function setActiveMenuItem(itemName) {
	$('.menu-item[data-item-name=' + itemName + ']').addClass('selected');
}





$(document).on("click", "button[name='feedbackSendBtn']", function () {



  var $error = $(".alert-danger");


    // if ($("input[name='sender'], input[value='email'], input[name='word']").val() == "") {


    // if ($("input[name='sender']").val() == "") {
    //   $(this).closest($error).removeClass("hide").text("No value");
    //   console.log('aaaaa');
    // }    else if ($("input[name='email']").val() == "") {
    //   $(this).closest($error).removeClass("hide").text("No value");
    //   console.log('aaaaa');
    // }     if ($("input[name='word']").val() == "") {
    //   $(this).closest($error).removeClass("hide").text("No value");
    //   console.log('aaaaa');
    // }
    //  else {
    //   $error.addClass("hide");
    //   console.log('bbbbb');
    // }


    if (feedbackServiceUrl === null) {
        console.debug('Feedback service configuration is missing.');
        alert(messages.fb_service_error);
        return;
    }
    var feedbackForm = $(this).closest('form');
    var okMessage = feedbackForm.find('[name=ok_message]').text();
    var errorMessage = feedbackForm.find('[name=error_message]').text();
    return $.ajax({
        url: feedbackServiceUrl,
        data: feedbackForm.serialize(),
        method: 'POST'
    }).done(function(data) {
    	console.log(data);
    	var answer = JSON.parse(data);
    	if (answer.status === 'ok') {
           alert(okMessage);
       } else {
           alert(errorMessage);
       }
   }).fail(function (data) {
        $('.has-error').show();
});
});


$(document).on("click", "#customRadioInline1", function () {
    $('#feedWord').addClass('show-section');
    $('#feedComment').removeClass('show-section');
});

$(document).on("click", "#customRadioInline2", function () {
    $('#feedWord').removeClass('show-section');
    $('#feedComment').addClass('show-section');
});

