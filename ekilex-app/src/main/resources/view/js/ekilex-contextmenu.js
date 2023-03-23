const ContextMenuInstances = [];

class ContextMenu {

  constructor(element) {
    this.element = element;
    this.options;
    this.id = `contextmenu-${Math.random().toString().substr(2)}`;
    this.elementId = element.attr('data-id');
    this.clickEevent = element.attr('data-leftClick') ? 'click' : 'contextmenu';
    this.menu;
    this.cursorPosition;
    this.destroyEvents = [
      'click',
      'contextmenu',
      'mousewheel',
      'blur',
    ];
  }

  bindEvents() {
    this.element.on(this.clickEevent, (e) => {
      e.preventDefault();
      e.stopImmediatePropagation();
      this.cursorPosition = {
        x: e.originalEvent.pageX,
        y: e.originalEvent.pageY,
      };
      this.showMenu();
    });
  }

  getOptions() {
    const data = [];
    const attributes = this.element[0].attributes;
    Object.keys(attributes).forEach((item) => {
      const key = attributes[item].name;
      const value = attributes[item].value;
      if (key.match(':')) {
        const task = key.split(':')[1];
        data.push({
          task,
          value,
        });
      }
    });
    this.options = data;
  }

  bindMenuEvents() {

    const eventsString = this.destroyEvents.map(item => `${item}.${this.id}`).join(' ');

    $(document).on(eventsString, (e) => {
      this.destroyMenu();
    });

    $(window).on(`resize.${this.id}`, (e) => {
      this.destroyMenu();
    });

    this.menu.find('button').on('click', (e) => {
      e.stopImmediatePropagation();
      this.handleContextMenuEvent($(e.target).attr('data-task'));
      this.destroyMenu();
    });

    ContextMenuInstances.push(this.id);

  }

  handleContextMenuEvent(task) {
    const taskName = task.charAt(0).toUpperCase() + task.slice(1);
    try {
      this[`on${taskName}`]();
    } catch(err) {
      console.log(`on${taskName} method doesnt exist!`);
    };
  }

  removeInstance(from) {
    const to = from + 1;
    const rest = ContextMenuInstances.slice((to || from) + 1 || ContextMenuInstances.length);
    ContextMenuInstances.length = from < 0 ? ContextMenuInstances.length + from : from;
    return ContextMenuInstances.push.apply(ContextMenuInstances, rest);
  }

  destroyOthers() {
    ContextMenuInstances.forEach((item) => {
      $(document).trigger(`contextmenu.${item}`);
    });
  }

  destroyMenu() {
    const eventsString = this.destroyEvents.map(item => `${item}.${this.id}`).join(' ');
    $(document).off(eventsString);
    $(window).off(`resize.${this.id}`);
    if (this.menu) {
      this.menu.remove();
      this.menu = undefined;
    }
    if (ContextMenuInstances.indexOf(this.id) !== -1) {
      this.removeInstance(ContextMenuInstances.indexOf(this.id));
    }
    this.destroyOthers();
  }

  showMenu() {
    this.destroyMenu();
    this.getOptions();

    let optionsHtml = '';
    this.options.forEach((item) => {
      optionsHtml+= `<button data-task="${item.task}">${item.value}</button>`;
    });

    const html = `<div class="contextmenu" id="${this.id}">${optionsHtml}</div>`;
    $('body').append(this.menu = $(html));
    this.positionMenu();
    this.bindMenuEvents();
  }

  positionMenu() {

    let left; let top;

    if (this.clickEevent === 'contextmenu') {
      left = this.cursorPosition.x + 10;
      const menuWidth = this.menu.outerWidth();
      const windowWidth = $(window).width();
      if (left + menuWidth >= windowWidth) {
        left = this.cursorPosition.x - menuWidth - 10;
      } 
      top = this.cursorPosition.y;
    } else if (this.clickEevent === 'click') {
      left = this.element.offset().left + this.element.outerWidth() - this.menu.outerWidth();
      top = this.element.offset().top + this.element.outerHeight() + 8;
      
    }

    this.menu.css({
      top,
      left,
    });
  }

  initialize() {
    this.bindEvents();
  }

  /*
    <button
      data-plugin="contextmenu"
      data-id="9191919191919191919"
      data-contextmenu:join="Ühenda"
      data-contextmenu:rearrange="Tõsta ümber / Muuda järjekorda"
      data-contextmenu:move="Tõsta uude (term)mõistesse/ (leks)homonüümi"
      data-contextmenu:share="Jaga linki"
      data-contextmenu:edit="Muuda"
      data-contextmenu:delete="Kustuta">Context test</button>

    custom contextmenu events
    data-contextmenu:rearrange -> onRearrange
    data-contextmenu:move -> onMove
    data-contextmenu:join -> onJoin
    etc..
  */

  onMove() {
    console.log(this.elementId, 'Beep boop.. moving');
  }

  onJoin() {
    console.log(this.elementId, 'Beep boop.. joining');
  }

  onRearrange() {
    console.log(this.elementId, 'Beep boop.. arranging');
  }

  onShare() {
    console.log(this.elementId, 'Beep boop.. sharing');
  }

  onEdit() {
    console.log(this.elementId, 'Beep boop.. editing');
  }

  onDelete() {
    console.log(this.elementId, 'Beep boop.. deleting');
  }

  onCompare() {
    loadDetails(this.elementId, 'compare');
  }

  onClosepanel() {
    let detailsDiv = this?.element?.closest(`#details-area[data-id="${this.elementId}"]`);
    const detailsDivParent = detailsDiv?.parent();
    const detailsDivIndex = detailsDivParent?.children()?.index(detailsDiv);
    // Remove data for the panel that is about to be removed
    PanelBreadcrumbs.removeDataByIndex(detailsDivIndex);
    if (!detailsDiv?.length) {
      detailsDiv = $(`#details-area[data-id="${this.elementId}"]`);
    }
    detailsDiv.animate({
      opacity: 0,
    }, 250, function() {
      $(this).remove();
      $(window).trigger('update:wordId');
    });
    $(`#word-result-${this.elementId}`).removeClass('active');
    const button = $(`#word-result-${this.elementId}`).find('button');
    button.removeAttr('data-contextmenu:closePanel');
    button.attr('data-contextmenu:compare', 'Ava uues paneelis');
  }

  onSharelink() {
    const url = `${window.location.href.split('?')[0]}?wordId=${this.elementId}`;
    window.open(url, '_blank');
  }

  onMultidelete() {
    const values = JSON.parse(this.element.attr('data-multiSelectValues')).map(item => parseInt(item));
    console.log(values)
  }
}

$.fn.contextmenu = function() {
  $(this).each(function(){
    const instance = new ContextMenu($(this));
    instance.initialize();
  });
}