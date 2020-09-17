class panelBreadcrumbs {
  constructor(obj) {
    this.main = obj;
    this.panel = this.main.parents('[data-rel="details-area"]:first');
  }

  getData() {
    const breadCrumbs = this.panel.attr('data-breadcrumbs') ? JSON.parse(this.panel.attr('data-breadcrumbs')) : [];
    const output = breadCrumbs.map((value, index) => {
      return `<li><button href="javascript:void(0);" data-index="${index}" data-id="${value.id}" data-contextmenu:compare="Ava uues paneelis" data-behaviour="replace" data-plugin="contextmenu">${value.word}</button></li>`;
    }).join('');

    this.main.find('ul').html(output);
    this.main[0].scrollLeft = 999999999;
    this.bindEvents();
  }

  bindEvents() {
    $wpm.bindObjects(this.main);

    const breadCrumbs = this.panel.attr('data-breadcrumbs') ? JSON.parse(this.panel.attr('data-breadcrumbs')) : [];

    this.main.find('button').on('click', (e) => {
      const wordId = $(e.target).data('id');
      const behaviour = $(e.target).data('behaviour') || false;
      const lastWordId = behaviour === 'replace' ? $(e.target).parents('#word-details-area:first').attr('data-id') : false;
      const index = $(e.target).attr('data-index');
      
      $(e.target).parents('#word-details-area:first').attr('data-breadcrumbs', JSON.stringify(breadCrumbs.slice(0, index)));
      loadWordDetails(wordId, behaviour, lastWordId);
    });

  }

  initialize() {
    this.getData();
  }
}

$.fn.panelBreadcrumbs = function() {
  $(this).each(function(){
    const instance = new panelBreadcrumbs($(this));
    instance.initialize();
  });
}