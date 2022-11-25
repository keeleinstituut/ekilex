class WordGame {

  constructor() {
    this.paths = {
      data: `${viewPath}/js/wordgame-data.json`,
      template: `${viewPath}/js/wordgame.twig`,
      missingImage: `${viewPath}/images/wordgame-icons/missing.svg`,
      images: `${viewPath}/images`,
    }
  
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
      lang: undefined,
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

  async renderTemplate() {
    return new Promise((resolve, reject) => {
      const template = Twig.twig({
        data: this.htmlTemplate,
      });
      Twig.extendFilter('slug', function (str) {
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

      Twig.extendFilter('decode', function (str) {
        return decodeHTMLEntities(str);
      });
      const html = template.render({
        data: this.parsedData,
        emptySubCategories: this.parsedSubCategories,
        emptyCategories: this.parsedCategories,
        options: this.options,
        labels: wordGameTranslations,
        viewPath: viewPath,
      });

      // re-render only necessary stuff
      if (this.main.querySelector('.wordgame__options')) {
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
    
    this.origData = this.origData.concat(this.multiCards);
    
    this.origData.forEach((item) => {
      if (item.type == 1) {

        item.image_link = item.not_interactive === 1 ? item.image_link : '';
        item.category = item.category.trim();
        item.sub_category = item.sub_category.trim();
        item.A1_A2 = "no";
        item.A1_B1 = "no";

        if ((item.sub_category != "" && item.sub_category != null) && !addedMulticards.includes(item.sub_category)) {

          var multiCardTemplate = {
            wordId: 123456,
            word: "Multikaart",
            word_link: "",
            category: `${item.category}`,
            sub_category: `${item.sub_category}`,
            example_1: "",
            example_2: "",
            example_3: "",
            image_link: `${this.paths.images}/wordgame-multicards/${this.wordgameSlugify(item.sub_category)}.svg`,
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
      const category = item.category;
      if (!this.parsedData[category]) {
        this.parsedData[category] = [];
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

      Object.keys(this.parsedData[key]).forEach((subkey) => {  
        items[subkey] = items[subkey].sort((a, b) => a.order - b.order);

        var hasA = "no";
        var hasB = "no";

        items[subkey].forEach((item2) => {
          if (item2.type != 1 || item2.image_link.trim() != null || item2.image_link.trim() != "") {
            if (item2.A1_A2 == "yes") {
              hasA = "yes";
              catHasA = "yes";
            }

            if (item2.A1_B1 == "yes") {
              hasB = "yes";
              catHasB = "yes";
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
        }
        this.pushToUrl();
        this.renderTemplate();

        window.scrollTo(0, 0);
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

  async initialize() {
    this.main = document.querySelector('#wordgame');
    this.origData = await this.loadData(this.paths.data, 'application/json');
    this.htmlTemplate = await this.loadData(this.paths.template, 'text/html; charset=UTF-8');
    this.categorizeData();
    this.options.active_category = Object.keys(this.parsedData)[0];
    this.options.lang = "et";
    this.options.level = "kesktase";
    this.options.text_transform = "lower";
    this.options.autoplay = "off";
    this.retrieveFromUrl();
    this.printListener();

    if (this.options.json) {
      this.origData = await this.loadData(this.options.json, 'application/json');
      this.categorizeData();
    }

    await this.renderTemplate();
    
    window.addEventListener('scroll', () => {
      this.loadImages();
    });
    window.addEventListener('resize', () => {
      this.updateImageAttributes();
      this.loadImages();
    });

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

window.onload = function () {
  const game = new WordGame();
  game.initialize();
}
