class EkiAccordion {
  constructor(element) {

    this.selectors = {
      elements: '.ekiAccordion__instance',
      toggles: '.ekiAccordion__title',
      active: '.ekiAccordion__instance--active',
      wrapper: '#details-area',
      content: '.ekiAccordion__content',
    };

    this.id = Math.random().toString().substr(2);
    this.resizeDebounce = 300;

    this.main = element;
    this.elements = this.main.find(this.selectors.elements);
    this.main.attr('id', `ekiAccordion-${this.id}`);
    this.debounce;
  }

  getStorageData() {
    return Cookies.get('accordionState') ? JSON.parse(Cookies.get('accordionState')) : {};
  }
  saveState(slug, state) {
    const cookies = this.getStorageData();
    cookies[slug] = state;
    Cookies.set('accordionState', JSON.stringify(cookies));
  }

  toggleElement(target) {
    const id = target.attr('id');
    const idParts = id.split('-');
    const slug = idParts.slice(0, idParts.length-1).join('-');
    target.toggleClass(this.selectors.active.substr(1));
    const active = target.is(this.selectors.active);
    this.saveState(slug, active);

    this.toggleDataExistsBadge(target);
    let targetInnerInstances = target.find('.ekiAccordion__instance');
    targetInnerInstances.each(function () {
      this.toggleDataExistsBadge($(this));
    });
  }

  bindElements() {
    const cookies = this.getStorageData();
    this.elements.each((index, element) => {
      const slug = slugify($(element).find(this.selectors.toggles).text());
      $(element).attr('id', `${slug}-${this.id}`);
      if (cookies[slug]) {
        $(element).addClass(this.selectors.active.substr(1));
      }
    });

    this.elements.find(this.selectors.toggles).on('click', (e) => {
      if ($(e.target).is(this.selectors.toggles)) {
        e.preventDefault();
        this.toggleElement($(e.target).parents(`${this.selectors.elements}:first`));
      }
    });
  }

  handleResize() {
    const element = $(`#ekiAccordion-${this.id}:first`);
    if (element.length) {
      if (!element.closest('div.card').length) {
        this.calculateParameters();
      }
    } else {
      this.unbindResize();
    }
  }

  calculateParameters() {
    let titlesHeight = 0;
    const wrapper = $(`${this.selectors.wrapper}:first`);

    this.elements.find(this.selectors.toggles).each((index, element) => {
      const obj = $(element);
      titlesHeight+= obj.outerHeight();
    });

    const usableHeight = wrapper.outerHeight() - (this.main.offset().top - wrapper.offset().top) - titlesHeight;

    this.main.find(this.selectors.content).css({
      maxHeight: `${usableHeight-16}px`,
    });
  }

  unbindResize() {
    $(window).off(`resize.${this.id}`);
  }

  bindResize() {
    $(window).on(`resize.${this.id}`, () => {
      clearTimeout(this.debounce);
      this.debounce = setTimeout(() => {
        this.handleResize();
      }, this.resizeDebounce);
    });
    this.handleResize();
  }

  toggleDataExistsBadge(target) {
    let isActive = target.hasClass("ekiAccordion__instance--active");
    let dataExistsBadge = target.find('.badge-data-exists');
    if (isActive) {
      dataExistsBadge.hide();
    } else {
      dataExistsBadge.show();
    }
  }

  initialize() {
    this.bindElements();
    this.bindResize();
  }
}

/*
  jQuery plugin wrapper for EkiAccordion class.
  Can be reused accross the application;
    $(element).accordion();
*/

$.fn.ekiAccordion = function() {
  $(this).each(function(){
    const instance = new EkiAccordion($(this));
    instance.initialize();
  });
}