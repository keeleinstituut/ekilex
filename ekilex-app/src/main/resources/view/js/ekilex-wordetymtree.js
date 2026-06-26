
;(function () {

  const treeCache = {};

  class WordEtymTree {
    constructor(container) {
      this.container = container;
      this.wordId = container.data('word-id');
      this.instance = container.closest('.ekiAccordion__instance');
      this.loaded = false;
      this.observer = null;
      this.init();
    }

    init() {
      if (!this.wordId) {
        return;
      }
      if (this.isOpen()) {
        this.load();
      } else {
        this.watchForOpen();
      }
    }

    isOpen() {
      return this.instance.hasClass('ekiAccordion__instance--active');
    }

    watchForOpen() {
      if (!this.instance.length || typeof MutationObserver === 'undefined') {
        this.load();
        return;
      }
      this.observer = new MutationObserver(() => {
        if (this.isOpen()) {
          this.load();
        }
      });
      this.observer.observe(this.instance[0], { attributes: true, attributeFilter: ['class'] });
    }

    stopWatching() {
      if (this.observer) {
        this.observer.disconnect();
        this.observer = null;
      }
    }

    load() {
      if (this.loaded) {
        return;
      }
      this.loaded = true;
      this.stopWatching();

      const cached = treeCache[this.wordId];
      if (cached) {
        this.render(cached);
        return;
      }

      $.get(applicationUrl + 'wordetymtree/' + this.wordId)
        .done((data) => {
          treeCache[this.wordId] = data;
          this.render(data);
        })
        .fail(() => {
          this.loaded = false;
          this.container.text(messages['lex.wordetym.load.fail']);
        });
    }

    render(data) {
      const element = document.createElement('eki-etym-tree');
      this.container.empty();
      this.container[0].appendChild(element);
      element.messages = messages;
      element.data = data;
    }
  }

  $.fn.wordEtymTree = function () {
    return this.each(function () {
      new WordEtymTree($(this));
    });
  };

})();
