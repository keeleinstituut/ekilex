class panelResize {
  constructor(handle) {
    this.handle = handle;
    this.panel = handle.parents('[id*="toolsColumn"]');
    this.siblingPanel = this.panel.parent().find('[id*="contentColumn"]');
  }

  bindEvents() {
    this.handle.on('mousedown.paneResize', (e) => {
      e.stopPropagation();
      e.preventDefault();
      
      const panelWidth = this.panel.css('width') ? parseInt(this.panel.css('width')) : this.panel.width();
      const dragStart = e.pageX;

      $(document).on('mousemove.paneResize', (e) => {
        e.preventDefault();
        const currentPos = e.pageX;
        const difference = currentPos - dragStart;
        let newWidth = panelWidth - difference;
        this.siblingPanel.parent().css('flexWrap', 'noWrap');
        this.siblingPanel.css({
          flex: `unset`,
          flexGrow: '1',
          flexShrink: '1',
          maxWidth: '100%',
          width: '100%',
        });

        this.panel.css({
          flex: `unset`,
          flexGrow: '1',
          flexShrink: '0',
          width: `${newWidth}px`,
          maxWidth: '100%',
        });
      });

      $(document).on('mouseup.paneResize', (e) => {
        $(document).off('mousemove.paneResize mouseup.paneResize');
      });

    });
  }
  initialize() {
    this.bindEvents();
  }
}

$.fn.panelResize = function() {
  $(this).each(function(){
    const instance = new panelResize($(this));
    instance.initialize();
  });
}