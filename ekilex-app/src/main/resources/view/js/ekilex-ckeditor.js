CKEDITOR.config.removeFormatTags = CKEDITOR.config.removeFormatTags + ',eki-stress,eki-foreign,eki-highlight,eki-meta,eki-sub,eki-sup';
var buttons = [
	{
		id: 'stress',
		label: 'Rõhk',
		command: 'stress',
		element: 'eki-stress',
		styles: 'font-weight: bold; color: #EC0138;',
	},
	{
		id: 'foreign',
		label: 'Tsitaat',
		command: 'foreign',
		element: 'eki-foreign',
		styles: 'color: #00874f; font-style: italic;',
	}, 
	{
		id: 'highlight',
		label: 'Esiletõstetud',
		command: 'highlight',
		element: 'eki-highlight',
		styles: 'font-weight:bold;',
	},
	{
		id: 'meta',
		label: 'Metatähistus',
		command: 'meta',
		element: 'eki-meta',
		styles: 'color: #00874f; font-variant: small-caps; font-stretch: expanded;',
	},
	{
		id: 'ekisub',
		label: 'Alaindeks',
		command: 'sub',
		element: 'eki-sub',
		styles: 'vertical-align: sub; font-size: 70%;',
	},
	{
		id: 'ekisup',
		label: 'Ülaindeks',
		command: 'sup',
		element: 'eki-sup',
		styles: 'vertical-align: super; font-size: 70%;',
	},
];

var icons = Array(buttons.length).fill('.').join(',');

function buttonBinding(editor) {
	var order = 0;
	
	var addButtonCommand = function( buttonName, buttonLabel, commandName, styleDefiniton ) {

			if ( !styleDefiniton ) { return };

			var style = new CKEDITOR.style( styleDefiniton );
			var forms = contentForms[ commandName ];

			forms.unshift( style );

			editor.attachStyleStateChange( style, function( state ) {
				!editor.readOnly && editor.getCommand( commandName ).setState( state );
			});

			editor.addCommand( commandName, new CKEDITOR.styleCommand( style, {
				contentForms: forms
			}));

			if ( editor.ui.addButton ) {
				editor.ui.addButton( buttonName, {
					label: buttonLabel,
					command: commandName,
					toolbar: 'ekiStyles,' + ( order += 10 )
				});
			}

		};

	var contentForms = {};
	buttons.forEach(function(item){
		CKEDITOR.addCss(item.element+'{'+item.styles+'}');
		CKEDITOR.config['coreStyles_'+item.command] = { element: item.element };
		contentForms[item.command] = [item.element];
		addButtonCommand(item.id, item.label, item.command, editor.config['coreStyles_'+item.command] );
	});
}

CKEDITOR.plugins.add('ekiStyles', {
	icons: icons,
	hidpi: true,
	init: function( editor ) {
		buttonBinding(editor);
	}
});

CKEDITOR.addCss('eki-link{color:blue; text-decoration: underline;}');

CKEDITOR.plugins.add('ekiLink', {
	icons: icons,
	hidpi: true,
	init: function( editor ) {
		editor.addCommand( 'insertTimestamp', {
      exec: function( editor ) {
				const link = new ckLink(editor);
				link.init();
      }
    });
    editor.ui.addButton( 'Timestamp', {
      label: 'Insert Timestamp',
      command: 'insertTimestamp',
      toolbar: 'ekiLink'
    });
	}
});

class ckLink {

	constructor(editor){
		this.editor = editor;
		this.parent = $(this.editor.element.$).parents('.modal-content:first');
		this.parentTop = this.parent.css('top');
		this.parentHeight = this.parent.outerHeight();
	}

	addTemplate() {
		this.parent.after(this.linkContent = $(`
			<div class="modal-content" style="top:${this.parentTop};">
				<div class="modal-header">
					<button class="btn btn-secondary" data-role="cancel">Tagasi</button>
					<button type="button" class="close" aria-label="Close" data-role="close" data-dismiss="modal">
						<span aria-hidden="true">×</span>
					</button>
				</div><!--/modal-header-->
				<div class="modal-body">
					<div class="formItem">
						<div class="formItem--title">
							Valitud Tekst
						</div><!--/formItem--title-->
						<input type="text" name="title" />
					</div><!--/formItem-->
					<div class="formItem">
						<div class="formItem--title">
							Lisa link
						</div><!--/formItem--title-->
						<input type="text" name="url" />
					</div><!--/formItem-->
				</div><!--/modal-body-->
				<div class="modal-footer">
					<button type="button" class="btn btn-default" data-dismiss="modal" data-role="cancel">Katkesta</button>
					<button type="submit" class="btn btn-primary" data-role="save">Valmis</button>
				</div>
			</div>
		`));

		console.log(this.parentTop);
		this.linkContent.css('marginBottom', (parseInt(this.parentTop) + this.parentHeight) - this.linkContent.outerHeight());

		this.outerLink = {
			title: this.linkContent.find('input[name="title"]:first'),
			url: this.linkContent.find('input[name="url"]:first'),
		}
	}

	toggle(state) {
		if (state === 'show') {
			this.addTemplate();
			this.parent.hide();
		} else {
			this.linkContent.remove();
			this.parent.show();
		}
	}

	bindEvents() {
		this.linkContent.find('[data-role="cancel"]').on('click', (e) => {
			e.preventDefault();
			this.toggle('hide');
		});
		this.linkContent.find('[data-role="save"]').on('click', (e) => {
			e.preventDefault();
			this.insertLink();
		});

	}

	insertLink() {
		const content = CKEDITOR.dom.element.createFromHtml(`<eki-link href="${this.outerLink.url.val()}" target="_blank">${this.outerLink.title.val()}</eki-link>`);
		this.editor.insertElement(content);
		this.toggle('hide');
	}

	init() {
		this.toggle('show');
		this.bindEvents();
	}
}

function initCkEditor(elem) {
	elem.ckeditor(function( textarea ) {
		// Callback function code.
	}, {
		enterMode: CKEDITOR.ENTER_BR,
		extraPlugins: 'ekiStyles,ekiLink',
		toolbarGroups: [
			{
				name: "eki-styles",
				groups: ["ekiStyles", 'ekiLink'],
			},
			{
				name: 'eki-tools',
				groups: ['cleanup', 'undo'],
			}
		],
		removeButtons: 'Underline,Strike,Subscript,Superscript,Anchor,Styles,Specialchar,Italic,Bold'
	});

	

}

