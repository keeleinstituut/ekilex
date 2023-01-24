class WordGame {

  constructor() {
    this.paths = {
      data: `${viewPath}/js/wordgame-data.json`,
      template: `${viewPath}/js/wordgame.twig`,
      missingImage: `${viewPath}/images/wordgame-icons/missing.svg`,
      images: `${viewPath}/images`,
    }
  
    this.languages = ['et', 'ru', 'ua'];
    this.languagesWithSound = ['et'];
    this.languageNames = {};
    this.images = []
    this.multiLevelMulticards = ['keha','nägu','jalg','käsi'];
    this.multiCaseMulticards = ['laps', 'vanem', 'vanavanem'];
    this.multiCards = [
      {
        category: "Kehaosad",
        sub_category: "keha",
        word: "keha",
        wordId: 123456,
        word_link: "",
        example_1: "",
        example_2: "",
        example_3: "",
        image_link: "",
        audio_link: "",
        level: "A1",
        A1_A2: "yes",
        A1_B1: "yes",
        order: 1,
        type: 1
      },
      {
        category: "Kehaosad",
        sub_category: "nägu",
        word: "nägu",
        wordId: 123456,
        word_link: "",
        example_1: "",
        example_2: "",
        example_3: "",
        image_link: "",
        audio_link: "",
        level: "A1",
        A1_A2: "yes",
        A1_B1: "yes",
        order: 1,
        type: 1
      },
      {
        category: "Kehaosad",
        sub_category: "jalg",
        word: "jalg",
        wordId: 123456,
        word_link: "",
        example_1: "",
        example_2: "",
        example_3: "",
        image_link: "",
        audio_link: "",
        level: "A1",
        A1_A2: "yes",
        A1_B1: "yes",
        order: 1,
        type: 1
      },
      {
        category: "Kehaosad",
        sub_category: "käsi",
        word: "käsi",
        wordId: 123456,
        word_link: "",
        example_1: "",
        example_2: "",
        example_3: "",
        image_link: "",
        audio_link: "",
        level: "A1",
        A1_A2: "yes",
        A1_B1: "yes",
        order: 1,
        type: 1
      },
      {
        category: "Pereliikmed",
        sub_category: "laps",
        word: "laps",
        wordId: 123456,
        word_link: "",
        example_1: "",
        example_2: "",
        example_3: "",
        image_link: "",
        audio_link: "",
        level: "A1",
        A1_A2: "yes",
        A1_B1: "yes",
        order: 1,
        type: 1
      },
      {
        category: "Pereliikmed",
        sub_category: "vanem",
        word: "vanem",
        wordId: 123456,
        word_link: "",
        example_1: "",
        example_2: "",
        example_3: "",
        image_link: "",
        audio_link: "",
        level: "A1",
        A1_A2: "yes",
        A1_B1: "yes",
        order: 1,
        type: 1
      },
      {
        category: "Pereliikmed",
        sub_category: "vanavanem",
        word: "vanavanem",
        wordId: 123456,
        word_link: "",
        example_1: "",
        example_2: "",
        example_3: "",
        image_link: "",
        audio_link: "",
        level: "A1",
        A1_A2: "yes",
        A1_B1: "yes",
        order: 1,
        type: 1
      }
    ];
  
    this.options = {
      wordgame_lang: undefined,
      active_category: undefined,
      level: undefined,
      text_transform: undefined,
      autoplay: undefined,
    }
  
    this.colMapper = {
      type_1: 'col-12',
      type_2: 'col-lg-3 col-md-6 col-sm-6 col-xs-10 col-10',
      type_3: 'col-12',
    }

    this.sortAlphabets = {
      et: [
        "a",
        "b",
        "c",
        "d",
        "e",
        "f",
        "g",
        "h",
        "i",
        "j",
        "k",
        "l",
        "m",
        "n",
        "o",
        "p",
        "q",
        "r",
        "s",
        "š",
        "z",
        "ž",
        "t",
        "u",
        "v",
        "w",
        "õ",
        "ä",
        "ö",
        "ü",
        "x",
        "y",
      ],
      ru: [
        "а",
        "б",
        "в",
        "г",
        "д",
        "е",
        "ё",
        "ж",
        "з",
        "и",
        "й",
        "к",
        "л",
        "м",
        "н",
        "о",
        "п",
        "р",
        "с",
        "т",
        "у",
        "ф",
        "х",
        "ц",
        "ч",
        "ш",
        "щ",
        "ъ",
        "ы",
        "ь",
        "э",
        "ю",
        "я",
      ],
    };
  }

  loadData(path, contentType) {
    return new Promise((resolve, reject) => {
      fetch(path, {
          method: 'GET',
          headers: {
            'Content-Type': contentType,
          },
        })
        .then(response => response.text())
        .then((data) => {
          if (contentType.match('json')) {
            resolve(JSON.parse(data));
          } else {
            resolve(data);
          }
        });
    });
  }

  getSearchHint(key) {
    const hints = wordGameTranslations.searchHintsList;

    let desktopHint = '...';
    let mobileHint = '...';
    
    if (hints.desktop && hints.desktop[key]) {
      desktopHint = hints.desktop[key];
    }

    if (hints.mobile && hints.mobile[key]) {
      mobileHint = hints.mobile[key];
    }

    if (window.matchMedia('(max-width: 768px)').matches) {
      return mobileHint;
    } else {
      return desktopHint;
    }
  }

  async renderTemplate(full = false, searchError = null) {
    return new Promise((resolve, reject) => {
      const template = Twig.twig({
        data: this.htmlTemplate,
      });

      Twig.extendFilter('slug', (str) => {
        str = str.replace(/^\s+|\s+$/g, '');
        str = str.toLowerCase();

        var from = "àáäâèéëêìíïîòóöôõùúüûñç·/_,:;";
        var to = "aaaaeeeeiiiiooooouuuunc------";
        for (var i = 0, l = from.length; i < l; i++) {
          str = str.replace(new RegExp(from.charAt(i), 'g'), to.charAt(i));
        }

        str = str.replace(/[^a-z0-9 -]/g, '')
          .replace(/\s+/g, '-')
          .replace(/-+/g, '-');

        return str;
      });

      Twig.extendFilter('decode', (str) => {
        return this.decodeHTMLEntities(str);
      });

      Twig.extendFilter('replaceString', (str, value) => {
        return str.replace(/\.*(\[0\])\.*/, value);
      });

      Twig.extendFilter('getSearchHint', (str) => {
        return this.getSearchHint(str);
      });

      const html = template.render({
        data: this.parsedData,
        searchError: searchError,
        categoryTranslations: this.categoryTranslations,
        languageNames: this.languageNames,
        emptySubCategories: this.parsedSubCategories,
        emptyCategories: this.parsedCategories,
        languagesWithSound: this.languagesWithSound,
        options: this.options,
        labels: wordGameTranslations,
        viewPath: viewPath,
      });

      // re-render only necessary stuff
      if (this.main.querySelector('.wordgame__options') && !full) {
        this.main.querySelector('.wordgame__content').innerHTML = $(html).find('.wordgame__content').html();
        this.main.querySelector('.dropdown-game').innerHTML = $(html).find('.dropdown-game').html();
        resolve(true);
        this.bindEvents(this.main.querySelector('.wordgame__content'));
        this.bindEvents(this.main.querySelector('.dropdown-game'));
      } else {
        this.main.innerHTML = html;
        resolve(true);
        this.bindEvents();
      }
    });
  }

  categorizeData() {
    this.parsedData = {};

    this.parsedSubCategories = {};
    this.parsedCategories = {};

    var staticPictureCounter = 0;
    var addedMulticards = [];
    var multicardsToAdd = [];
    
    if (this.options.wordgame_lang === 'et') {
      this.origData = this.origData.concat(this.multiCards);
    }
    this.categoryTranslations = {};
    
    this.origData.forEach((item) => {
      let isOtherLanguage = item.ee_category && item.ee_category.length > 0;

      if (item.type == 1) {

        item.image_link = item.not_interactive === 1 ? item.image_link : '';
        item.category = item.category.trim();
        if (isOtherLanguage) {
          item.ee_category = item.ee_category.trim();
        }
        item.sub_category = item.sub_category.trim();
        item.A1_A2 = "no";
        item.A1_B1 = "no";

        if ((item.sub_category != "" && item.sub_category != null) && !addedMulticards.includes(item.sub_category)) {

          var multiCardTemplate = {
            wordId: 123456,
            word: "Multikaart",
            word_link: "",
            category: `${item.category}`,
            ee_category: `${isOtherLanguage ? item.ee_category : ''}`,
            sub_category: `${item.sub_category}`,
            example_1: "",
            example_2: "",
            example_3: "",
            image_link: `${this.paths.images}/wordgame-multicards/${item.multicard_filename ? item.multicard_filename : this.wordgameSlugify(item.sub_category)}.svg`,
            audio_link: "",
            level: "",
            A1_A2: "yes",
            A1_B1: "yes",
            order: 1,
            type: 1
          };

          if (item.not_interactive === 1) {
            var card = JSON.parse(JSON.stringify(multiCardTemplate));
            card.image_link = item.image_link;
            multicardsToAdd.push(card);
          } else 
          {
            if (this.multiCaseMulticards.includes(item.sub_category.toLowerCase())) {
              var card1 = JSON.parse(JSON.stringify(multiCardTemplate));
              card1.image_link = `${this.paths.images}/wordgame-multicards/${this.wordgameSlugify(item.sub_category)}_L.svg`;
              card1.level = "lower";
              multicardsToAdd.push(card1);
    
              var card2 = JSON.parse(JSON.stringify(multiCardTemplate));
              card2.image_link = `${this.paths.images}/wordgame-multicards/${this.wordgameSlugify(item.sub_category)}_U.svg`;
              card2.level = "upper";
              multicardsToAdd.push(card2);
            }

            if (this.multiLevelMulticards.includes(item.sub_category.toLowerCase())) {
              var card1 = JSON.parse(JSON.stringify(multiCardTemplate));
              card1.image_link = `${this.paths.images}/wordgame-multicards/${this.wordgameSlugify(item.sub_category)}-A.svg`;
              card1.level = "A1";
              card1.A1_B1 = "no"
              multicardsToAdd.push(card1);

              var card2 = JSON.parse(JSON.stringify(multiCardTemplate));
              card2.image_link = `${this.paths.images}/wordgame-multicards/${this.wordgameSlugify(item.sub_category)}-B.svg`;
              card2.level = "B1";
              card2.A1_A2 = "no"
              multicardsToAdd.push(card2);
            }
            if (!this.multiLevelMulticards.includes(item.sub_category.toLowerCase()) && !this.multiCaseMulticards.includes(item.sub_category.toLowerCase())) {
              var card1 = JSON.parse(JSON.stringify(multiCardTemplate));
              card1.level = "A1";
              multicardsToAdd.push(card1);
            }
          }

          addedMulticards.push(item.sub_category);
        }
      }
    });

    if (multicardsToAdd != null) {
      multicardsToAdd.forEach((item) => {
        this.origData.push(item);
      });
    }

    // separating data by category
    this.origData.forEach((item) => {
      let isOtherLanguage = item.ee_category && item.ee_category.length > 0;
      const category = isOtherLanguage ? item.ee_category : item.category;

      if (!this.parsedData[category]) {
        this.parsedData[category] = [];
        this.categoryTranslations[category] = item.category;
      }

      item.examples = [item.example_1, item.example_2, item.example_3].filter(text => text);
      item.cols = this.colMapper[`type_${item.type}`];

      if (!item.order || item.order == "" || item.order == null) {
        item.order = 9999;
      }

      if (item.type == 3 && !item.sub_category) {
        item.sub_category = "wordgame_static_picture" + staticPictureCounter;
        staticPictureCounter++;
      }

      if (!item.level || item.level == "" || item.level == null) {
        item.level = 'A1';
      }

      if (item.A1_A2 == "" || item.A1_A2 == null || item.A1_B1 == "" || item.A1_B1 == null) {
        item.A1_A2 = 'yes';
        item.A1_B1 = 'yes';
  
        if (item.level == "B1") {
          item.A1_A2 = 'no';
        }
      }

      this.parsedData[category].push(item);
    });

    // separating data by subcategory in each category
    Object.keys(this.parsedData).forEach((key) => {
      const items = {};
      this.parsedData[key].forEach((item) => {
        if (!items[item.sub_category]) {
          items[item.sub_category] = [];
        }
        items[item.sub_category].push(item);
      });
      
      this.parsedData[key] = items;

      if (!this.parsedCategories[key]) {
        this.parsedCategories[key] = [];
      }
      
      if (!this.parsedSubCategories[key]) {
        this.parsedSubCategories[key] = [];
      }

      var catHasA = "no";
      var catHasB = "no";
      let subCategoriesToModify = [];

      Object.keys(this.parsedData[key]).forEach((subkey) => {  
        items[subkey] = items[subkey].sort((a, b) => a.order - b.order);

        var hasA = "no";
        var hasB = "no";

        items[subkey].forEach((item2) => {
          if (item2.type !== 1 || item2.image_link.trim() !== null || item2.image_link.trim() !== "") {
            if (item2.type === 3) {
              subCategoriesToModify.push({ key: key, subkey: subkey });
            } else {
              if (item2.A1_A2 == "yes") {
                hasA = "yes";
                catHasA = "yes";
              }
  
              if (item2.A1_B1 == "yes") {
                hasB = "yes";
                catHasB = "yes";
              }
            }
          }
        });

        this.parsedSubCategories[key][subkey] = {
          A1_A2: hasA,
          A1_B1: hasB
        };
      });

      this.parsedCategories[key] = {
        A1_A2: catHasA,
        A1_B1: catHasB
      };

      subCategoriesToModify.forEach((el) => {
        const { A1_A2, A1_B1 } = this.parsedCategories[el.key];
        this.parsedSubCategories[el.key][el.subkey] = { A1_A2, A1_B1 };
      });

      // Add empty cards to fix print layout, prevent empty rows between cards.
      // The bug is related to css "break-inside: avoid;", which is required
      // for cards not to be split in print layout
      Object.keys(this.parsedData[key]).forEach((subkey) => {
        const type2CardsCount = this.parsedData[key][subkey].reduce((acc, curr) => curr.type === 2 ? ++acc : acc, 0);
        const remainderCards = type2CardsCount % 9

        if (type2CardsCount > 9 && remainderCards > 0 && remainderCards < 4) {
          const emptycardsToAdd = 4 - remainderCards;

          for (let i = 0; i < emptycardsToAdd; i++) {
            this.parsedData[key][subkey].push(
            {
                category: key,
                sub_category: subkey,
                word: "empty",
                wordId: 0,
                word_link: "",
                example_1: "",
                example_2: "",
                example_3: "",
                image_link: "",
                audio_link: "",
                level: "A1",
                A1_A2: "yes",
                A1_B1: "yes",
                order: 99999,
                type: 0,
                cols: 'col-lg-3 col-md-6 col-sm-6 col-xs-10 col-10',
            });
          }
        }
      });
    });
  }

  bindEvents(element) {
    const parent = element ? element : this.main;

    this.bindHoverTooltips(parent);

    this.bindSidebarMenuCategories(parent);

    this.bindOptions(parent);

    this.bindCards(parent);

    this.setMenuPlaceholderIcons(parent);

    this.lazyLoad();

    if (!element) {
      this.bindSearch();
      this.bindLanguageSelect();
      this.bindVoiceSearch();
      this.bindCollapseToggle();
    }
  }

  bindCollapseToggle() {
    $('#wordgame-collapse-toggle').click((e) => {
      $('#wordgame-collapse').toggleClass('collapse-open');
    });
  }

  bindVoiceSearch() {
    $("#wordgame-button-start-rec").click(function (e) {
      console.log('aaaaa');
      $('#wordgame-button-start-rec').prop('hidden', 'hidden');
      $('#wordgame-button-stop-rec').prop('hidden', null);
      $('.search-btn').prop('disabled', true);
      startRecording();
    });
    
    $("#wordgame-button-stop-rec").click(function (e) {
      $('#wordgame-button-stop-rec').prop('hidden', 'hidden');
      $('#wordgame-button-start-rec').prop('hidden', null);
      $('.search-btn').prop('disabled', false);
      stopRecording(function (audioBlob) {
        sendToWebSocket(audioBlob);
      });
    });
  }

  bindCards(parent) {
    this.bindFlipcardButtons(parent);

    this.bindFlipcardAudioButtons(parent);

    this.bindMulticard();
  }

  bindOptions(parent) {
    // keeletase
    this.bindLevelFilter(parent);

    // kirjatüüp
    this.bindTextTransformFilter(parent);

    // automaatne hääldamine
    this.bindAutoplaySwitch(parent);
  }

  setMenuPlaceholderIcons(parent) {
    parent.querySelectorAll('.wordgame__menu img, .dropdown-game img').forEach((item) => {
      const src = item.getAttribute('src');
      const tmp = new Image();
      tmp.onerror = () => {
        item.setAttribute('src', this.paths.missingImage);
      };
      tmp.src = src;
    });
  }

  // multikaardi struktuur:
  // multikaardi svg asub div'is klassiga "wordgame-multicard"
  // svg ise on tähistatud kujul "subcategory+svg" ehk subcategory "keha" puhul on svg id'ks "kehasvg"
  // svg sisaldab endas:
  // - data-label'id ja data-circle'id
  // - data-label märgitakse sedasi "data-label=name"
  // - data-circle märgitakse sedasi "data-circle-name" (tähelepanu sellele et = märgi asemel on -)
  // - 'name' peab matchima json'is antud sõnaga
  // svg's peavad olema labelid hierarhiliselt data-circle'de peal, kuna muidu click/hover event lihtsalt ei tööta
  bindMulticard() {
    document.querySelectorAll('.wordgame-multicard-interactive').forEach((multicardElem) => {
      const multicard = multicardElem.children[0];

      if (multicard != null && !multicard.classList.contains('wordgame-multicard-initialized')) {

        if (multicardElem.querySelectorAll('[data-bigger]').length > 0) {
          multicardElem.classList.add('bigger');
        }

        multicard.querySelectorAll('[id]').forEach((item) => {
          const elemId = item.getAttribute('id');


          if (elemId.includes('data-label')) {
            const dataName = elemId.substring(elemId.indexOf('=') + 1);
            const dataCircle = multicard.querySelector(`#data-circle-${dataName}`);

            const highlightColor1 = '#2c6fb6';
            const highlightColor2 = 'white';

            const multicardId = multicard.getAttribute('id').replace('svg', '');

            const wordData = this.origData.find((name) => name.word === dataName);

            if (wordData != null && wordData != "") {
              item.addEventListener('click', (e) => {
                playAudio(wordData.audio_link, function () {
                });
              });
              item.addEventListener('mouseenter', (e) => {
                if (this.options.autoplay == "on") {
                  playAudio(wordData.audio_link, function () {
                  });
                }
              });
            }
            else {
              item.classList.add('wordgame-no-sound');

              if (this.options.debugmissing) {
                item.children[0].setAttribute('fill', 'red');
              }
            }

            if (dataCircle != null) {
              const origColor1 = item.children[0].getAttribute('fill');
              const origColor2 = item.children[1].getAttribute('fill');
              const origOpacity = dataCircle.getAttribute('opacity');

              dataCircle.setAttribute('opacity', '0');
              item.classList.add("wordgame-multicard-label");

              item.addEventListener('mouseenter', (e) => {
                dataCircle.setAttribute('opacity', origOpacity);
                item.children[0].setAttribute('fill', highlightColor1);
                item.children[1].setAttribute('fill', highlightColor2);
              });
              item.addEventListener('mouseleave', (e) => {
                dataCircle.setAttribute('opacity', '0');
                item.children[0].setAttribute('fill', origColor1);
                item.children[1].setAttribute('fill', origColor2);
              });
            }
            else {
              const origColor1 = item.children[0].getAttribute('fill');
              const origColor2 = item.children[1].getAttribute('fill');

              item.classList.add("wordgame-multicard-label");

              item.addEventListener('mouseenter', (e) => {
                item.children[0].setAttribute('fill', highlightColor1);
                item.children[1].setAttribute('fill', highlightColor2);
              });
              item.addEventListener('mouseleave', (e) => {
                item.children[0].setAttribute('fill', origColor1);
                item.children[1].setAttribute('fill', origColor2);
              });
            }
          }
        });

        multicard.classList.add('wordgame-multicard-initialized');
      }
    });
  }

  bindFlipcardAudioButtons(parent) {
    parent.querySelectorAll('[audio-value],[data-words]').forEach((item) => {
      const audioValue = item.getAttribute('audio-value');
      const audioType = item.getAttribute('audio-type');

      if ((audioValue != null && audioValue != "") || (item.getAttribute('data-words') != null && item.getAttribute('data-words') != "")) {
        item.addEventListener('click', (e) => {
          e.preventDefault();
          if (item.getAttribute('data-words')) {

            var definedUrlToAudio = item.getAttribute('data-url-to-audio');
            if (definedUrlToAudio) {
              playAudio(definedUrlToAudio, function () {
              });
              return;
            }

            var data = {
              'words': item.getAttribute('data-words')
            };

            $.post(applicationUrl + 'generate_audio', data).done(function (providedUrlToAudio) {
              item.setAttribute('data-url-to-audio', providedUrlToAudio);
              playAudio(providedUrlToAudio, function () {
              });
            }).fail(function () {
              alert(messages.audio_generation_failure);
            });
          } else {
            playAudio(audioValue, function () {
            });
          }
        });

        item.addEventListener('mouseenter', (e) => {
          if (this.options.autoplay == "on") {
            playAudio(audioValue, function () {
            });
          }
        });
      }
      else {
        if (this.options.debugmissing) {
          item.style.background = "red";
        }
      }
    });
  }

  bindAutoplaySwitch(parent) {
    parent.querySelectorAll('[autoplay-switch]').forEach((item) => {
      item.addEventListener('click', (e) => {
        if (this.options.autoplay === "on") {
          this.options.autoplay = "off";
        }
        else {
          this.options.autoplay = "on";
        }
        this.pushToUrl();
      });
    });
  }

  bindTextTransformFilter(parent) {
    parent.querySelectorAll('[text-transform]').forEach((item) => {
      item.addEventListener('click', (e) => {
        parent.querySelectorAll(`[text-transform=${item.getAttribute('text-transform')}]`).forEach((el) => {
          if (!el.checked) {
            el.checked = true;
          }
        });

        this.options.text_transform = item.getAttribute('text-transform');
        const upperClass = 'wordgame-text-upper';

        document.querySelectorAll('.wordgame__row').forEach((item2) => {
          if (item.getAttribute('text-transform') == "upper") {
            if (!item2.classList.contains(upperClass)) {
              item2.classList.add(upperClass);
            }
          } else {
            if (item2.classList.contains(upperClass)) {
              item2.classList.remove(upperClass);
            }
          }
        });

        var hasMulticard = false;

        Object.keys(this.parsedData[this.options.active_category]).forEach((key) => {
          this.parsedData[this.options.active_category][key].forEach((obj) => {
            if (obj.type == 1) {
              hasMulticard = true;
            }
          });
        });

        this.pushToUrl();

        if (hasMulticard) {
          this.renderTemplate();
        }
      });
    });
  }

  bindFlipcardButtons(parent) {
    parent.querySelectorAll('[toggle="flip"]').forEach((item) => {
      item.addEventListener('click', (e) => {
        e.preventDefault();
        const flipClass = 'card--flipped';
        const parent = item.closest('.wordgame-flippable');
        parent.classList.toggle(flipClass);

        const cardBack = parent.querySelector('.card__back');
        const cardFront = parent.querySelector('.card__face');

        if (parent.classList.contains(flipClass)) {
          cardBack.querySelectorAll('button,a').forEach((elem) => {
            elem.setAttribute("tabindex", "0");
          });

          cardFront.querySelectorAll('button,a').forEach((elem) => {
            elem.setAttribute("tabindex", "-1");
          });

          cardBack.classList.remove('wordgame-card-hidden');
          setTimeout(() => {
            cardBack.classList.remove('wordgame-card-hidden');
            cardFront.classList.add('wordgame-card-hidden');
          }, 200);
        }
        else {
          cardBack.querySelectorAll('button,a').forEach((elem) => {
            elem.setAttribute("tabindex", "-1");
          });

          cardFront.querySelectorAll('button,a').forEach((elem) => {
            elem.setAttribute("tabindex", "0");
          });

          cardFront.classList.remove('wordgame-card-hidden');
          setTimeout(() => {
            cardFront.classList.remove('wordgame-card-hidden');
            cardBack.classList.add('wordgame-card-hidden');
          }, 200);
        }
      });
    });
  }

  bindLevelFilter(parent) {
    parent.querySelectorAll('[sort-id]').forEach((item) => {
      item.addEventListener('click', (e) => {
        parent.querySelectorAll(`[sort-id=${item.getAttribute('sort-id')}]`).forEach((el) => {
          if (!el.checked) {
            el.checked = true;
          }
        });

        this.options.level = item.getAttribute('sort-id');
        this.pushToUrl();
        this.renderTemplate();
      });
    });
  }

  bindSidebarMenuCategories(parent) {
    const categories = parent.querySelectorAll('[data-category]');

    categories.forEach((item) => {

      item.addEventListener('click', (e) => {
        e.preventDefault();

        categories.forEach((cat) => {
          cat.classList.remove('wordgame-active');
          cat.setAttribute('aria-selected', "false");
        });
        item.classList.add('wordgame-active');
        item.setAttribute('aria-selected', "true");

        this.options.active_category = item.getAttribute('data-category');

        if (item.classList.contains('nav-link')) {
          this.main.querySelectorAll('[data-category]').forEach((elem) => {
            if (elem.getAttribute('data-category') == this.options.active_category) {
              elem.classList.add('wordgame-active');
            }
            else if (elem.classList.contains('wordgame-active')) {
              elem.classList.remove('wordgame-active');
            }
          });
        }

        if (this.main.querySelector('.dropdown-game')) {
          this.main.querySelector('.dropdown-game').classList.remove('show');
          this.main.querySelector('.dropdown-game .dropdown-menu').classList.remove('show');
        }
        
        const asyncReRender = async () => {
          await new Promise(r => setTimeout(r, 1));
          this.pushToUrl();
          await this.renderTemplate();

          window.scrollTo(0, 0);
        }

        asyncReRender();
      });
    });
  }

  bindHoverTooltips(parent) {
    let isMobile = window.matchMedia("only screen and (any-hover: none), (pointer: coarse)").matches;

    if (!isMobile) {
      parent.querySelectorAll('[title]').forEach((item) => {
        $(item).tooltip({
          trigger: 'hover'
        });
      });
    }
    $('.tooltip[role="tooltip"]').remove();
  }

  searchCategories(input) {
    let categories = Object.keys(this.categoryTranslations).map((categoryKey) => {
      return {
        category: this.categoryTranslations[categoryKey],
        categoryid: categoryKey
      }
    });

    const items = JSON.parse(JSON.stringify(this.origData));
    const inputLower = input.toLowerCase();

    function CharCompare(a, b, alphabet, index) {
      if (index == a.length || index == b.length) {
        return 0;
      }
      var aChar = alphabet.indexOf(a.toLowerCase().charAt(index));
      var bChar = alphabet.indexOf(b.toLowerCase().charAt(index));
      if (aChar != bChar) {
        return aChar - bChar
      }
      else {
        return CharCompare(a, b, alphabet, index+1)
      }
    }

    function searchWordsArray(splitWords) {
      const inputWords = inputLower.split(' ');

      if (inputWords.length <= 1) {
        for (let i = 0; i < splitWords.length; i++) {
          const word = splitWords[i];
  
          if (word.startsWith(inputLower) || word.endsWith(inputLower)) {
            return true;
          }
        }
      } else if (splitWords.length >= inputWords.length) {        
        for (let i = 0; i < splitWords.length; i++) {
          let matchingParts = 0;

          for (let j = 0; j < inputWords.length; j++) {
            if (i + j > splitWords.length - 1) {
              break;
            }

            const word = splitWords[i + j];
            const inputWord = inputWords[j];

            if (j === 0 && word.endsWith(inputWord)) {
              matchingParts++;
            }
            else if (j < inputWords.length - 1 && inputWord === word) {
              matchingParts++;
            } else if (word.startsWith(inputWord)) {
              matchingParts++;
            } else {
              break;
            }
          }

          if (matchingParts === inputWords.length) {  
            return true;
          }
        }
      }

      return false;
    }

    let resultCategoryObjects = categories.filter(el => {
      const splitWords = el.category.toLowerCase().split(' ');
      return searchWordsArray(splitWords);
    });

    resultCategoryObjects = resultCategoryObjects.sort((a, b) => {
      if (this.sortAlphabets[this.options.wordgame_lang]) {
        return CharCompare(a.category, b.category, this.sortAlphabets[this.options.wordgame_lang], 0);
      } else {
        return a.category.localeCompare(b.category);
      }
    });

    // bring exact string matches to front
    resultCategoryObjects = resultCategoryObjects.sort((a, b) => {
      return (a.category.toLowerCase() === inputLower) ? -1 : 1;
    });



    let resultWordObjects = items.filter(el => {
      const splitWords = el.word.toLowerCase().split(' ');
      const wordLevelCorrect = el.type === 1 ? true : this.options.level === 'algtase' ? el.A1_A2 === 'yes' : el.A1_B1 === 'yes';
      return wordLevelCorrect && searchWordsArray(splitWords);
    }).map((el) => {
      let simplifiedObj = {
        word: el.word,
        category: el.category,
        categoryid: el.ee_category || el.category,
      };

      if (el.type === 1) {
        simplifiedObj.sub_category = el.sub_category;
      }

      return simplifiedObj;
    });

    resultWordObjects = resultWordObjects.sort((a, b) => {
      const aCombined = `${a.word} ${a.category}`;
      const bCombined = `${b.word} ${b.category}`;

      if (this.sortAlphabets[this.options.wordgame_lang]) {
        return CharCompare(aCombined, bCombined, this.sortAlphabets[this.options.wordgame_lang], 0);
      } else {
        return aCombined.localeCompare(bCombined);
      }
    });

    resultWordObjects = resultWordObjects.sort((a, b) => {
      if (a.word.toLowerCase() === inputLower) {
        // bring exact string matches to front
        if (a.word === b.word) {
          if (this.sortAlphabets[this.options.wordgame_lang]) {
            return CharCompare(a.category, b.category, this.sortAlphabets[this.options.wordgame_lang], 0);
          } else {
            return a.category.localeCompare(b.category);
          }
        } else {
          return -1;
        }
      } else {
        const aCombined = `${a.word} ${a.category}`;
        const bCombined = `${b.word} ${b.category}`;
  
        if (this.sortAlphabets[this.options.wordgame_lang]) {
          return CharCompare(aCombined, bCombined, this.sortAlphabets[this.options.wordgame_lang], 0);
        } else {
          return aCombined.localeCompare(bCombined);
        }
      }
    });

    // remove duplicate words that are in same category
    let seenWordObjects = [];
    resultWordObjects = resultWordObjects.filter((el) => {
      if (seenWordObjects.includes(el.word + el.category)) {
        return false;
      } else {
        seenWordObjects.push(el.word + el.category);
        return true;
      }
    });

    // limit the amount of results
    resultCategoryObjects = resultCategoryObjects.slice(0, 5);
    resultWordObjects = resultWordObjects.slice(0, 10);

    let directMatchObject = null;

    if (resultWordObjects.length > 0) { 
      for (let i = 0; i < resultWordObjects.length; i++) {
        const wordObj = resultWordObjects[i];
        
        if (inputLower === wordObj.word.toLowerCase()) {
          directMatchObject = wordObj;
          break;
        }
      }
    }

    if (!directMatchObject && resultCategoryObjects.length > 0) {
      for (let i = 0; i < resultCategoryObjects.length; i++) {
        const categoryObj = resultCategoryObjects[i];
        
        if (inputLower === categoryObj.category.toLowerCase()) {
          directMatchObject = categoryObj;
          break;
        }
      }
    }
    
    const hasResults = resultCategoryObjects.length > 0 || resultWordObjects.length > 0;
    
    return { resultCategoryObjects, resultWordObjects, hasResults, directMatchObject, searchTerm: input };
  }

  bindSearch(parent) {
    const searchElem = document.querySelector('#wordgame-search');
    const searchContainer = searchElem.closest('.wordgame-search');
    const searchClearBtn = document.querySelector('#wordgame-button-close');
    const searchEnterBtn = document.querySelector('#wordgame-button-search');
    const resultsElem = document.querySelector('#wordgame-search-results');
    let currentFocus = -1;

    if (searchElem) {
      const categories = document.querySelectorAll('.wordgame__menu [data-category]');

      const resultOpenHandler = (resultObj) => {
        const { category, word, sub_category, categoryid } = resultObj;

        searchContainer.classList.remove('open');
        searchClearBtn.classList.add('wordgame-hidden');
        searchElem.value = "";
        
        let activeCategoryElem = null;

        categories.forEach((cat) => {
          cat.classList.remove('wordgame-active');
          cat.setAttribute('aria-selected', "false");

          if (cat.getAttribute('data-category') === categoryid) {
            activeCategoryElem = cat;
          }
        });
        
        this.options.active_category = categoryid;

        if (activeCategoryElem) {
          activeCategoryElem.classList.add('wordgame-active');
          activeCategoryElem.setAttribute('aria-selected', "true");
  
          if (activeCategoryElem.classList.contains('nav-link')) {
            this.main.querySelectorAll('[data-category]').forEach((elem) => {
              if (elem.getAttribute('data-category') == this.options.active_category) {
                elem.classList.add('wordgame-active');
              }
              else if (elem.classList.contains('wordgame-active')) {
                elem.classList.remove('wordgame-active');
              }
            });
          }
        }

        this.pushToUrl();

        const asyncReRender = async () => {
          await this.renderTemplate();

          if (word) {
            let scrollToCard = null;
            const cardLabelElems = this.main.querySelectorAll('.wordgame__card__name');

            for (let i = 0; i < cardLabelElems.length; i++) {
              const cardLabelElem = cardLabelElems[i];

              if (cardLabelElem.innerText === word) {
                scrollToCard = cardLabelElem.closest('.wordgame__card');
                break;
              }
            }

            let scrollToSubcategory = null;
            if (!scrollToCard && sub_category) {
              const subcategoryElems = this.main.querySelectorAll('.wordgame__subcategory--header h3');

              for (let i = 0; i < subcategoryElems.length; i++) {
                const subcategoryElem = subcategoryElems[i];

                if (subcategoryElem.innerText === sub_category) {
                  scrollToSubcategory = subcategoryElem.closest('.subcategory-container');
                  break;
                }
              }
            }

            if (scrollToCard || scrollToSubcategory) {
              await new Promise(r => setTimeout(r, 100));

              if (!scrollToCard && scrollToSubcategory) {
                scrollToSubcategory.scrollIntoView({behavior: 'smooth', block: 'center'});

                return;
              }


              scrollToCard.scrollIntoView({behavior: 'smooth', block: 'center'});

              let animationRan = false;

              await new Promise(r => setTimeout(r, 100));

              const highlightAnimation = () => {
                animationRan = true;
                scrollToCard.classList.add('testanimation');
                $(scrollToCard).on('animationend webkitAnimationEnd oAnimationEnd', () => {
                  scrollToCard.classList.remove('testanimation');
                });
              }

              let scrollTimeout = setTimeout(highlightAnimation, 150);

              const handleScrollEvent = (e) => {
                if (!animationRan) {
                  clearTimeout(scrollTimeout);
                  scrollTimeout = setTimeout(highlightAnimation, 150);
                } else {
                  document.removeEventListener('scroll', handleScrollEvent);
                }
              }

              document.addEventListener('scroll', handleScrollEvent);
            } else {
              window.scrollTo(0, 0);
            }
          } else {
            window.scrollTo(0, 0);
          }
        }

        asyncReRender();
      }

      const resultClickHandler = (resultObj) => {    
        return (e) => {
          e.preventDefault();

          resultOpenHandler(resultObj);
        }
      }

      $(searchClearBtn).click(() => {
        searchElem.value = "";
        searchContainer.classList.remove('open');
        searchClearBtn.classList.add('wordgame-hidden');
      });

      const createResultElements = (parentElem, resultObjects, isCategory = false) => {
        const dividerElem = document.createElement('div');
        dividerElem.className = 'wordgame-search__divider';
        dividerElem.innerHTML = `<div>${isCategory ? wordGameTranslations.category.toUpperCase() || 'TEEMA' : wordGameTranslations.word.toUpperCase() || 'SÕNA'}</div><div class="wordgame-search__divider-line"></div>`;
        parentElem.appendChild(dividerElem);

        let resultsListElem = document.createElement('ul');
        resultsListElem.className = "wordgame-search__list";

        for (let i = 0; i < resultObjects.length; i++) {
          const resultObj = resultObjects[i];
          
          let listElem = document.createElement('li');
          listElem.className = "wordgame-search__list-item";

          let resultButton = document.createElement('button');
          resultButton.type = "button";
          resultButton.tabIndex = "-1";
          resultButton.className = "wordgame-search__list-result";
          resultButton.onclick = resultClickHandler(resultObj);

          if (isCategory) {
            resultButton.innerHTML = `<span class="wordgame-search__category">${resultObj.category}</span>`;
          } else {            
            resultButton.innerHTML = `
              <span class="wordgame-search__word">${resultObj.word}</span>
              <span class="wordgame-search__minus">—</span>
              <span class="wordgame-search__category">${resultObj.category}</span>
            `;
          }


          listElem.appendChild(resultButton);

          resultsListElem.appendChild(listElem);
        }
        
        parentElem.appendChild(resultsListElem);
      }
      
      let searchResults = {};

      $(searchElem).on('input', () => {
        const inputValue = searchElem.value;
        currentFocus = -1;

        if (searchClearBtn) {
          if (inputValue.length > 0) {
            searchClearBtn.classList.remove('wordgame-hidden');
          } else {
            searchClearBtn.classList.add('wordgame-hidden');
          }
        }

        if (inputValue.length > 2) {
          searchResults = this.searchCategories(inputValue);
          const { resultCategoryObjects, resultWordObjects, hasResults } = searchResults

          resultsElem.innerHTML = '';

          if (hasResults) {
            if (resultWordObjects.length > 0) {
              createResultElements(resultsElem, resultWordObjects);
            }

            if (resultCategoryObjects.length > 0) {
              createResultElements(resultsElem, resultCategoryObjects, true);
            }
          } else {
            resultsElem.innerHTML = `<div class="wordgame-search__noresults">${wordGameTranslations.searchnoresultshort}</div>`;
          }

          searchContainer.classList.add('open');
        } else {
          searchContainer.classList.remove('open');
          searchResults = {};
        }
      });

      const directSearchHandler = () => {
        const { hasResults, directMatchObject } = searchResults;

        if (hasResults && directMatchObject && directMatchObject.category) {
          resultOpenHandler(directMatchObject);
        } else {
          searchContainer.classList.remove('open');
          searchClearBtn.classList.add('wordgame-hidden');
          this.renderTemplate(false, searchElem.value);
          searchElem.value = "";
        }
      }

      const hideKeyboard = (element) => {
        element.setAttribute('readonly', 'readonly');
        element.setAttribute('disabled', 'true');
        setTimeout(() => {
          element.blur();
          element.removeAttribute('readonly');
          element.removeAttribute('disabled');
        }, 100);
      }
    
      $(searchElem).on('keydown', (e) => {
        const buttonElems = resultsElem.querySelectorAll('.wordgame-search__list-result');

        if (!buttonElems) {
          if (e.key === 'Enter' || e.keyCode === 13) {
            e.preventDefault();
            directSearchHandler();
            hideKeyboard(searchElem);
            currentFocus = -1;
          }

          return;
        }

        const setActive = () => {
          for (let i = 0; i < buttonElems.length; i++) {
            buttonElems[i].classList.remove('wordgame-search__active-item');
          }

          if (currentFocus >= buttonElems.length) {
            currentFocus = 0;
          }
          if (currentFocus < 0) {
            currentFocus = buttonElems.length - 1;
          }

          buttonElems[currentFocus].classList.add('wordgame-search__active-item');
        }

        if (e.key === 'ArrowDown' || e.keyCode === 40) {
          e.preventDefault();
          currentFocus++;
          setActive();
        } else if (e.key === 'ArrowUp' || e.keyCode === 38) {
          e.preventDefault();
          currentFocus--;
          setActive();
        } else if (e.key === 'Enter' || e.keyCode === 13) {
          e.preventDefault();

          if (currentFocus > -1) {
            buttonElems[currentFocus].click();
          } else {
            directSearchHandler();
            hideKeyboard(searchElem);
          }
          currentFocus = -1;
        }
      });

      $(searchEnterBtn).click((e) => {
        directSearchHandler();
      });

      const handleClosingAutocomplete = (className, focusOut = false) => {
        if ((className !== 'wordgame-search__input' && className !== 'wordgame-search__list-result') || focusOut) {
          searchContainer.classList.remove('open');
        }
      }

      document.addEventListener('focus', function (e) {
        handleClosingAutocomplete(e.target.className);
      }, true);

      window.addEventListener('blur', function (e) {
        handleClosingAutocomplete(e.target.className, true);
      });

      $(document).click((e) => {
        handleClosingAutocomplete(e.target.className);
      });
    }
  }

  bindLanguageSelect() {
    const selectElem = document.querySelector('#wordgame-language-select');

    if (selectElem) {
      selectElem.addEventListener('change', (e) => {
        e.preventDefault();

        this.options.wordgame_lang = e.currentTarget.value;

        const asyncReRender = async () => {
          await new Promise(r => setTimeout(r, 1));
          this.pushToUrl();
          this.initialize(true);
          window.scrollTo(0, 0);
        }

        asyncReRender();
      });
    }
  }

  wordgameSlugify (str) {
    str = str.replace(/^\s+|\s+$/g, '');
    str = str.toLowerCase();

    var from = "àáäâèéëêìíïîòóöôõùúüûñç·/_,:;";
    var to = "aaaaeeeeiiiiooooouuuunc------";
    for (var i = 0, l = from.length; i < l; i++) {
      str = str.replace(new RegExp(from.charAt(i), 'g'), to.charAt(i));
    }

    str = str.replace(/[^a-z0-9 -]/g, '')
      .replace(/\s+/g, '-')
      .replace(/-+/g, '-');

    return str;
  }

  lazyLoad() {
    this.images = [];
    document.querySelectorAll('[data-src]').forEach((item) => {
      this.images.push({
        elem: item,
        top: item.getBoundingClientRect().top,
        height: item.getBoundingClientRect().height,
      });
    });
    this.loadImages();
  }

  updateImageAttributes() {
    this.images.forEach((item) => {
      item.top = item.elem.getBoundingClientRect().top;
      item.height = item.elem.getBoundingClientRect().height;
    });
  }

  loadImages(loadAll = false) {
    const windowTop = window.scrollY;
    const windowHeight = window.innerHeight;
    this.images.forEach((item, index) => {
      if (item.top < windowTop + windowHeight || loadAll) {
        const realSrc = item.elem.getAttribute('src');

        if (!realSrc) {
          const src = item.elem.getAttribute('data-src');
          item.elem.setAttribute('src', src);
          item.elem.parentNode.setAttribute('style', `background-image:url(${src})`);
          item.elem.removeAttribute('data-src');
          delete this.images[index];
        }
      }
    });
  }

  pushToUrl() {
    const url = Object.keys(this.options).map((key) => {
      return key ? `${key}=${this.options[key]}` : undefined;
    }).filter(item => item).join('&');
    window.history.replaceState({}, '', `?${url}`);
  }

  retrieveFromUrl() {
    const search = window.location.search.replace('?', '');

    search.split('&').forEach((item) => {
      const splitValues = item.split('=');
      const key = splitValues[0];
      const value = decodeURIComponent(splitValues[1]);
      this.options[key] = value;
    });
  }

  printListener() {
    var thisRef = this;

    var beforePrint = function() {
      thisRef.lazyLoad();
      thisRef.loadImages(true);
    };

    if (window.matchMedia) {
        var isPrinting = window.matchMedia('print');
        try {
          isPrinting.addEventListener('change', function(mql) {
            if (mql.matches) {
              beforePrint();
            } 
          });
        } catch {
          try {
            isPrinting.addListener(function(mql) {
              if (mql.matches) {
                beforePrint();
              } 
            });
          } catch {}
        }
    }

    window.onbeforeprint = beforePrint;
  }

  async initialize(full = false, testJson = false) {
    this.options.wordgame_lang = "et";
    this.retrieveFromUrl();

    this.main = document.querySelector('#wordgame');

    if (this.options.wordgame_lang === 'et') {
      this.origData = await this.loadData(this.paths.data, 'application/json');
    } else {
      let err = false;
      let tempData = {};

      try {
        tempData = await this.loadData(`${viewPath}/js/wordgame-data-${this.options.wordgame_lang}.json`, 'application/json');

        if (tempData.error) {
          err = true;
        }
      } catch (err) {
        err = true;
      }

      if (err) {
        // if fail to load other language then fallback to 'et'
        this.origData = await this.loadData(this.paths.data, 'application/json');
        this.options.wordgame_lang = "et";
        this.pushToUrl();
      } else {
        this.origData = tempData;
      }
    }
    
    this.htmlTemplate = await this.loadData(this.paths.template, 'text/html; charset=UTF-8');

    this.categorizeData();

    // set defaults
    this.options.wordgame_lang = "et";
    this.options.level = "kesktase";
    this.options.text_transform = "lower";
    this.options.autoplay = "off";
    this.options.active_category = Object.keys(this.parsedData)[0];
    this.retrieveFromUrl();
    
    if (testJson !== false) {
      this.origData = testJson;
      this.categorizeData();
    }

    // get language keys & translations for language select
    Object.keys(wordGameTranslations.learningLanguagesList).forEach((langKey) => {
      if (this.languages.includes(langKey.toLowerCase())) {
        this.languageNames[langKey] = wordGameTranslations.learningLanguagesList[langKey];
      }
    });
    
    await this.renderTemplate(full);
    
    window.addEventListener('scroll', () => {
      this.loadImages();
    });
    window.addEventListener('resize', () => {
      this.updateImageAttributes();
      this.loadImages();
    });

    this.printListener();

    if ($('body').find('#wordgame').length !== 0) {
      $('body').addClass('has-wordgame');
    }

    console.log('wordgamejs initialized');
  }

  decodeHTMLEntities(text) {
    var entities = [
      ['&otilde;', 'õ'],
      ['&Otilde;', 'Õ'],
      ['&auml;', 'ä'],
      ['&Auml;', 'Ä'],
      ['&ouml;', 'ö'],
      ['&Ouml;', 'Ö'],
      ['&uuml;', 'ü'],
      ['&Uuml;', 'Ü'],
      ['&scaron;', 'š'],
      ['&Scaron;', 'Š'],
      ['&ocirc;', 'ô'],
      ['&Ocirc;', 'Ô'],
      ['&oacute;', 'ó'],
      ['&Oacute;', 'Ó'],
      ['&ograve;', 'ò'],
      ['&Ograve;', 'Ò'],
      ['&ocirc;', 'ô'],
      ['&Ocirc;', 'Ô'],
      ['&ntilde;', 'ñ'],
      ['&Ntilde;', 'Ñ'],
      ['&quot;', '"'],
      ['&bdquo;', '„'],
      ['&ldquo;', '“'],
      ['&rdquo;', '”'],
      ['&lsquo;', '‘'],
      ['&rsquo;', '’'],
      ['&laquo;', '«'],
      ['raquo', '»'],
      ['&ndash;', '–'],
      ['&gt;', '>'],
      ['&lt;', '<'],
      [' ', ' '],
      ['&alpha;', 'α'],
      ['&Alpha;', 'Α'],
      ['&beta;', 'β'],
      ['&Beta;', 'Β'],
      ['&gamma;', 'γ'],
      ['&Gamma;', 'Γ'],
      ['&delta;', 'δ'],
      ['&Delta;', 'Δ'],
      ['&epsilon;', 'ε'],
      ['&Epsilon;', 'Ε'],
      ['&zeta;', 'ζ'],
      ['&Zeta;', 'Ζ'],
      ['&eta;', 'η'],
      ['&Eta;', 'Η'],
      ['&theta;', 'θ'],
      ['&Theta;', 'Θ'],
      ['&iota;', 'ι'],
      ['&Iota;', 'Ι'],
      ['&kappa;', 'κ'],
      ['&Kappa;', 'Κ'],
      ['&lambda;', 'λ'],
      ['&Lambda;', 'Λ'],
      ['&mu;', 'μ'],
      ['&Mu;', 'Μ'],
      ['&nu;', 'ν'],
      ['&Nu;', 'Ν'],
      ['&xi;', 'ξ'],
      ['&Xi;', 'Ξ'],
      ['&omicron;', 'ο'],
      ['&Omicron;', 'Ο'],
      ['&pi;', 'π'],
      ['&Pi;', 'Π'],
      ['&Rho;', 'ρ'],
      ['&Zeta;', 'Ρ'],
      ['&sigmaf;', 'ς'],
      ['&sigma;', 'σ'],
      ['&Sigma;', 'Σ'],
      ['&tau;', 'τ'],
      ['&Tau;', 'Τ'],
      ['&upsilon;', 'υ'],
      ['&Upsilon;', 'Υ'],
      ['&phi;', 'φ'],
      ['&Phi;', 'Φ'],
      ['&chi;', 'χ'],
      ['&Chi;', 'Χ'],
      ['&psi;', 'ψ'],
      ['&Psi;', 'Ψ'],
      ['&omega;', 'ω'],
      ['&Omega;', 'Ω'],
      ['&hellip;', '…'],
      ['&micro;', 'µ'],
      ['&minus;', '−']
    ];;
  
    for (var i = 0, max = entities.length; i < max; ++i)
      text = text.replace(new RegExp(entities[i][1], 'g'), entities[i][0]);
  
    return text;
  }
}

const wordGameInstance = new WordGame();

// window.onload = function () {
// }

$(document).ready(function () {
  wordGameInstance.initialize();
	initialiseRecording(speechRecognitionServiceUrl);
});