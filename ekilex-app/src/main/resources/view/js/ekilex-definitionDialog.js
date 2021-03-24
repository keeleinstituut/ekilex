
class DefinitionDialog {
  constructor(element) {
    this.button = element;
    this.modalElement = $('#definitionDialog');
  }

  openDialog() {
    this.modalElement.modal('show');
    this.bindEvents();
  }

  closeModal() {
    this.modalElement.modal('hide');
  }

  bindEvents() {
    this.modalElement.find('[data-role="close"]').off('click.definitionDialog').on('click.definitionDialog', (e) => {
      e.preventDefault();
      this.closeModal();
    });
    this.modalElement.find('[data-role="save"]').off('click.definitionDialog').on('click.definitionDialog', (e) => {
      e.preventDefault();
      alert('Saving all the beautiful data!');
    });
  }

  initEditor() {
    
  }

  initialize() {
    this.initEditor();
    this.button.on('click.definitionDialog', (e) => {
      e.preventDefault();
      this.openDialog();
    });
  }
}

$.fn.definitionDialog = function() {
  $(this).each(function(){
    const instance = new DefinitionDialog($(this));
    instance.initialize();
  });
}