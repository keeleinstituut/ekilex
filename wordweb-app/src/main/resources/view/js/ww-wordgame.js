class WordGame {

  constructor() {
    this.paths = {
      // data: 'word-game.json',
      // data: '/view/js/real-data-naide.json',
      data: '/view/js/wordgame-data.json',
      template: '/view/js/wordgame.twig',
      missingImage: '/view/images/wordgame-icons/missing.svg',
    }
  
    this.images = []
  
    this.options = {
      active_category: undefined,
      level: undefined,
      text_transform: undefined,
      autoplay: undefined,
    }
  
    this.colMapper = {
      type_1: 'col-12',
      // type_2: 'col-xl-3 col-lg-4 col-md-6 col-sm-6 col-xs-12',
      type_2: 'col-lg-3 col-md-6 col-sm-6 col-xs-9 col-9',
      type_2_2: 'col-lg-4 col-md-6 col-sm-6 col-xs-12',
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
        options: this.options,
        labels: wordGameTranslations,
      });

      // console.log(wordGameTranslations);

      // enamasti renderdab uuesti ainult content ala
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

      // this.main.innerHTML = html;
      // this.bindEvents();

    });
  }

  categorizeData() {
    this.parsedData = {};

    var staticPictureCounter = 0;

    // data kategooriate järgi kokku panemine
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

      // item level convertimine, juhul kui datas pole märgitud, kus kategoorias antud itemit kuvada tahetakse
      if (!item.level || item.level == "" || item.level == null) {
        item.level = 'A1';
      }

      item.A1_A2 = 'yes';
      item.A1_B1 = 'yes';

      if (item.level == "B1") {
        item.A1_A2 = 'no';
      }

      this.parsedData[category].push(item);
    });

    // igas kategoorias data subkategooriatesse kokku panemine
    Object.keys(this.parsedData).forEach((key) => {
      const items = {};
      this.parsedData[key].forEach((item) => {
        if (!items[item.sub_category]) {
          items[item.sub_category] = [];
        }
        items[item.sub_category].push(item);
      });

      this.parsedData[key].forEach((item) => {
        items[item.sub_category] = items[item.sub_category].sort((a, b) => a.order - b.order);

        if(items[item.sub_category].length <= 3) {
          var hasOnlyType2 = true;

          items[item.sub_category].forEach((item2) => {
            if (item2.type != 2) {
              hasOnlyType2 = false;
            }
          });

          // kui alla 4 asja siis suuremad col'id
          // if (hasOnlyType2) {
          //   items[item.sub_category].forEach((item2) => {
          //     item2.cols = this.colMapper['type_2_2'];
          //   });
          // }
        }
      });

      this.parsedData[key] = items;
    });

    console.log(this.parsedData);
  }

  bindEvents(element) {
    const parent = element ? element : this.main;

    parent.querySelectorAll('[title]').forEach((item) => {
      $(item).tooltip({
        trigger: 'hover'
      });
    });
    $('.tooltip[role="tooltip"]').remove();

    // menüü
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

        // console.log(categories);

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

    // keeletase
    parent.querySelectorAll('[sort-id]').forEach((item) => {
      item.addEventListener('click', (e) => {
        this.options.level = item.getAttribute('sort-id');
        this.pushToUrl();
        this.renderTemplate();
      });
    });

    // flipkaart
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
            elem.setAttribute("tabindex","0");
          });

          cardFront.querySelectorAll('button,a').forEach((elem) => {
            elem.setAttribute("tabindex","-1");
          });

          cardBack.classList.remove('wordgame-hidden');
          setTimeout(() => {
            cardBack.classList.remove('wordgame-hidden');
            cardFront.classList.add('wordgame-hidden');
          }, 200);
        }
        else {
          cardBack.querySelectorAll('button,a').forEach((elem) => {
            elem.setAttribute("tabindex","-1");
          });

          cardFront.querySelectorAll('button,a').forEach((elem) => {
            elem.setAttribute("tabindex","0");
          });

          cardFront.classList.remove('wordgame-hidden');
          setTimeout(() => {
            cardFront.classList.remove('wordgame-hidden');
            cardBack.classList.add('wordgame-hidden');
          }, 200);
        }
      });
    });

    // kirjatüüp
    parent.querySelectorAll('[text-transform]').forEach((item) => {
      item.addEventListener('click', (e) => {
        this.options.text_transform = item.getAttribute('text-transform');
        const upperClass = 'text-upper';

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

        this.pushToUrl();
      });
    });

    // automaatne hääldamine
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

    // flip kaartide audio nupud
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
            // https://sonaveeb.ee/
            $.post(applicationUrl + 'generate_audio', data).done(function (providedUrlToAudio) {
              item.setAttribute('data-url-to-audio', providedUrlToAudio);
              playAudio(providedUrlToAudio, function () {

              });
            }).fail(function () {
              alert(messages.audio_generation_failure);
            })
          } else {
            playAudio(audioValue, function () {
            
            });
          }
          console.log(`audio value: ${audioValue} type: ${audioType}`);
          //play audio
        });

        item.addEventListener('mouseenter', (e) => {
          if (this.options.autoplay == "on") {
            playAudio(audioValue, function () {

            });
            console.log(`audio value: ${audioValue} type: ${audioType}`);
          }

        });
      }
      else {
        // debug. visuaalselt näitab ära, mis sõnadel puudub audio
        if (this.options.debugmissing) {
          item.style.background = "red";
        }
      }
    });
    
    // multikaardi struktuur:
    // multikaardi svg asub div'is klassiga "wordgame-multicard"
    // svg ise on tähistatud kujul "subcategory+svg" ehk subcategory "keha" puhul on svg id'ks "kehasvg"
    // svg sisaldab endas:
    // - data-label'id ja data-circle'id
    // - data-label märgitakse sedasi "data-label=name"
    // - data-circle märgitakse sedasi "data-circle-name" (tähelepanu sellele et = märgi asemel on -)
    // - 'name' peab matchima json'is antud sõnaga
    // svg's peavad olema labelid hierarhiliselt data-circle'de peal, kuna muidu click/hover event lihtsalt ei tööta

    // tegeleb multikaartidega
    document.querySelectorAll('.wordgame-multicard').forEach((multicardElem) => {
      // const multicard = document.querySelector('#kehaosadsvg');
      const multicard = multicardElem.children[0];

      if (multicard != null && !multicard.classList.contains('wordgame-multicard-initialized')) {
        multicard.querySelectorAll('[id]').forEach((item) => {
          const elemId = item.getAttribute('id');

          
          if (elemId.includes('data-label')) {
            const dataName = elemId.substring(elemId.indexOf('=') + 1);
            const dataCircle = multicard.querySelector(`#data-circle-${dataName}`);
            
            const highlightColor1 = '#2c6fb6';
            const highlightColor2 = 'white';

            const multicardId = multicard.getAttribute('id').replace('svg','');
            
            const wordData = this.parsedData[this.options.active_category][multicardId].find((name) => name.word === dataName);

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

              // debug. visuaalselt näitab ära, mis sõnadel puudub audio
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

    // puuduva ikooni puhul placeholderi kuvamine
    parent.querySelectorAll('.wordgame__menu img, .dropdown-game img').forEach((item) => {
      const src = item.getAttribute('src');
      const tmp = new Image();
      tmp.onerror = () => {
        item.setAttribute('src', this.paths.missingImage);
      };
      tmp.src = src;
    });

    this.lazyLoad();

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

  loadImages() {
    const windowTop = window.scrollY;
    const windowHeight = window.innerHeight;
    this.images.forEach((item, index) => {
      if (item.top < windowTop + windowHeight) {
        const src = item.elem.getAttribute('data-src');
        item.elem.setAttribute('src', src);
        item.elem.parentNode.setAttribute('style', `background-image:url(${src})`);
        item.elem.removeAttribute('data-src');
        delete this.images[index];
      }
    });
  }

  pushToUrl() {
    // console.log(this.options);
    const url = Object.keys(this.options).map((key) => {
      // console.log(key);
      return key ? `${key}=${this.options[key]}` : undefined;
    }).filter(item => item).join('&');
    window.history.replaceState({}, '', `?${url}`);
  }

  retrieveFromUrl() {
    const search = window.location.search.replace('?', '');
    // console.log(search);
    search.split('&').forEach((item) => {
      const splitValues = item.split('=');
      const key = splitValues[0];
      const value = decodeURIComponent(splitValues[1]);
      this.options[key] = value;
    });
    // console.log(this.options);
  }

  async initialize() {
    this.main = document.querySelector('#wordgame');
    this.origData = await this.loadData(this.paths.data, 'application/json');
    this.htmlTemplate = await this.loadData(this.paths.template, 'text/html; charset=UTF-8');
    this.categorizeData();
    this.options.active_category = Object.keys(this.parsedData)[0];
    this.options.level = "algtase";
    this.options.text_transform = "lower";
    this.options.autoplay = "off";
    this.retrieveFromUrl();

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

    console.log('initialized');
  }

}

window.onload = function () {
  const game = new WordGame();
  game.initialize();
}

function decodeHTMLEntities(text) {
  var entities = decodeData;

  for (var i = 0, max = entities.length; i < max; ++i)
    text = text.replace(new RegExp(entities[i][1], 'g'), entities[i][0]);

  return text;
}

const decodeData = [
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
];