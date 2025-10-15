let keys = 'kioskboard-keys-english.json'; /* default keys */
let langChange = false; /*language keyboard switch box  on/off, if off disable keyboard*/

let keyboardBtn = '#keyboard-search-btn'; /*keyboard switch btn*/
let langBox = '#keyboard-lang-search '; /*language switch box - est to eng*/
let elementWhere = '#search'; /*input element where keyboard will be */
let allKeyboardBtns = '.keyboard-search'; /*language change and keyboard on off */

$(document).ready(function () {
  let urlWordweb = viewPathWordweb ?? "";
  let urlForKeys = urlWordweb + '/js/kiosk-board/dist/';  /* location folder for keyboard keys */

  // Initialize KioskBoard (default/all options)
  KioskBoard.init({

    /*!
    * Required
    * An Array of Objects has to be defined for the custom keys. Hint: Each object creates a row element (HTML) on the keyboard.
    * e.g. [{"key":"value"}, {"key":"value"}] => [{"0":"A","1":"B","2":"C"}, {"0":"D","1":"E","2":"F"}]
    */
    keysArrayOfObjects: null,

    /*!
    * Required only if "keysArrayOfObjects" is "null".
    * The path of the "kioskboard-keys-${langugage}.json" file must be set to the "keysJsonUrl" option. (XMLHttpRequest to get the keys from JSON file.)
    * e.g. '/Content/Plugins/KioskBoard/dist/kioskboard-keys-english.json'
    */
    keysJsonUrl: urlForKeys + keys,

    /*
    * Optional: An Array of Strings can be set to override the built-in special characters.
    * e.g. ["#", "€", "%", "+", "-", "*"]
    */
    keysSpecialCharsArrayOfStrings: null,

    /*
    * Optional: An Array of Numbers can be set to override the built-in numpad keys. (From 0 to 9, in any order.)
    * e.g. [1, 2, 3, 4, 5, 6, 7, 8, 9, 0]
    */
    keysNumpadArrayOfNumbers: null,

    // Optional: (Other Options)

    // Language Code (ISO 639-1) for custom keys (for language support) => e.g. "de" || "en" || "fr" || "hu" || "tr" etc...
    language: 'en',

    // The theme of keyboard => "light" || "dark" || "flat" || "material" || "oldschool"
    theme: 'light',

    // Scrolls the document to the top or bottom(by the placement option) of the input/textarea element. Prevented when "false"
    autoScroll: true,

    // Uppercase or lowercase to start. Uppercased when "true"
    capsLockActive: true,

    /*
    * Allow or prevent real/physical keyboard usage. Prevented when "false"
    * In addition, the "allowMobileKeyboard" option must be "true" as well, if the real/physical keyboard has wanted to be used.
    */
    allowRealKeyboard: false,

    // Allow or prevent mobile keyboard usage. Prevented when "false"
    allowMobileKeyboard: false,

    // CSS animations for opening or closing the keyboard
    cssAnimations: true,

    // CSS animations duration as millisecond
    cssAnimationsDuration: 360,

    // CSS animations style for opening or closing the keyboard => "slide" || "fade"
    cssAnimationsStyle: 'slide',

    // Enable or Disable Spacebar functionality on the keyboard. The Spacebar will be passive when "false"
    keysAllowSpacebar: true,

    // Text of the space key (Spacebar). Without text => " "
    keysSpacebarText: 'Space',

    // Font family of the keys
    keysFontFamily: 'sans-serif',

    // Font size of the keys
    keysFontSize: '22px',

    // Font weight of the keys
    keysFontWeight: 'normal',

    // Size of the icon keys
    keysIconSize: '25px',

    // Text of the Enter key (Enter/Return). Without text => " "
    keysEnterText: 'Enter',

    // The callback function of the Enter key. This function will be called when the enter key has been clicked.
    keysEnterCallback: undefined,

    // The Enter key can close and remove the keyboard. Prevented when "false"
    keysEnterCanClose: true,
  });

  function keyboardOptionsCustom(keys){

    return {
     /*!
      * Required only if "keysArrayOfObjects" is "null".
      * The path of the "kioskboard-keys-${langugage}.json" file must be set to the "keysJsonUrl" option. (XMLHttpRequest to get the keys from JSON file.)
      * e.g. '/Content/Plugins/KioskBoard/dist/kioskboard-keys-english.json'
      */
      keysJsonUrl: urlForKeys + keys,
    
       // The theme of keyboard => "light" || "dark" || "flat" || "material" || "oldschool"
      theme: 'light',

      // Uppercase or lowercase to start. Uppercased when "true"
      capsLockActive: false,

      /*
      * Allow or prevent real/physical keyboard usage. Prevented when "false"
      * In addition, the "allowMobileKeyboard" option must be "true" as well, if the real/physical keyboard has wanted to be used.
      */
      allowRealKeyboard: true,

      // Allow or prevent mobile keyboard usage. Prevented when "false"
      allowMobileKeyboard: true,

    };
  }

  function keyboardLang() { // select correct lang keys
    let lang = $(langBox + '#selected-language').attr('data-value');
    if (lang === 'estonia') {
      keys = 'kioskboard-keys-estonia.json';
    } else if (lang === 'russian') {
      keys = 'kioskboard-keys-russian.json';
    } else if (lang === 'english') {
      keys = 'kioskboard-keys-english.json';
    }
  }

  function keyboardCallOut() {    // Open keyboard
    setTimeout(function () {
      window.document.querySelector(elementWhere).dispatchEvent(new Event('focus'));
    }, 500);
  }

  $(document).on('click','.kioskboard-key-enter', function () {
    clickSearchIfInputExists();
  });

  function keyboardRun() {
    keyboardLang();
    KioskBoard.customMerge(elementWhere, keyboardOptionsCustom(keys)); /* call out for KioskBoard with input element and options */
    keyboardCallOut(); // Open keyboard
  }

  /* remove keyboard on mobile view*/
  $(window).on('resize', function () {
    let mobileView = window.matchMedia('(max-width: 1024px)');

    if (mobileView.matches) {
      window.document.body.click();
      $(langBox).hide();
      $(keyboardBtn).removeClass("active").attr('aria-pressed', false).hide();
      $(allKeyboardBtns).removeClass('lang-open');
    } else {
      $(keyboardBtn).show();
    }
  });
  $(window).trigger('resize');


  /* if button is pressed then keyboard is allowed to run. */
  $(document).on('click', keyboardBtn, function () {
    let obj = $(this);
    if (obj.hasClass("active")) {
      langChange = true;
      keyboardRun();
      $(allKeyboardBtns).addClass('lang-open');
      $(langBox).fadeIn( "slow" );
    } else {
      langChange = false;
      KioskBoard.kill(elementWhere); /* remove keyboard */
      $(langBox).fadeOut( "slow" );
      $(allKeyboardBtns).removeClass('lang-open');
    }
  });

  /* close keyboard manually. To fix keyboard is already opened and then keyboardCallOut() does not work */
  $(langBox).on('show.bs.dropdown', function () {
    window.document.body.click();
  });

  // language change
  $(document).on('click',  langBox + '.dropdown-menu button', function (event) {
    let replace = langBox + '#selected-language';
    event.preventDefault();
    /*changes to user selected value*/
    $(replace).text($(this).text());
    $(replace).attr('data-value', $(this).attr('data-value'));

    /* check if keyboard is allowed to run */
    if (langChange) {
      keyboardRun();
    }
  });
});
