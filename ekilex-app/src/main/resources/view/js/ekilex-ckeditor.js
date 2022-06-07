CKEDITOR.disableAutoInline = true;
CKEDITOR.config.removeFormatTags = CKEDITOR.config.removeFormatTags + ',eki-stress,eki-foreign,eki-highlight,eki-meta,eki-sub,eki-sup';
CKEDITOR.config.entities = false;
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
CKEDITOR.plugins.addExternal('sourcedialog', '/view/js/sourcedialog/plugin.js');

CKEDITOR.plugins.add('ekiLink', {
	icons: 'ekilink',
	hidpi: true,
	init: function( editor ) {
		editor.addCommand( 'ekilink', {
      exec: function( editor ) {
				const link = new ckLink(editor);
				link.init();
      }
    });
    editor.ui.addButton( 'ekilink', {
      label: 'Eki-link',
      command: 'ekilink',
      toolbar: 'ekiLink'
    });
	}
});

function getSelectedElement( selection, tag) {
	var range = selection.getRanges()[ 0 ],
		element = selection.getSelectedElement();

	// In case of table cell selection, we want to shrink selection from td to a element.
	range.shrink( CKEDITOR.SHRINK_ELEMENT );
	element = range.getEnclosedNode();

	// If selection is inside text, get its parent element (#3437).
	if ( element && element.type === CKEDITOR.NODE_TEXT ) {
		element = element.getParent();
	}

	if ( element && element.type === CKEDITOR.NODE_ELEMENT && element.is(tag) ) {
		return element;
	}
}

CKEDITOR.plugins.add('removeEkilink', {
	icons: 'removeekilink',
	hidpi: true,
	init: function( editor ) {
		editor.addCommand( 'removeEkilink', {
      exec: function( editor ) {
				const range = editor.getSelection().getRanges()[0];
				// Collapsed means there's nothing selected
				if (range.collapsed) {
					const root = editor.document.getBody();
					const newRange = editor.createRange();
					newRange.selectNodeContents(root);
					const rangeEnd = range.endContainer.$.parentNode;
					const newRangeEnd = newRange.getBoundaryNodes().endNode.$.parentNode;

					// Add an empty space inside the editor if the link is the last element
					if (newRangeEnd === rangeEnd) {
						root.appendHtml('&nbsp;');
						newRange.selectNodeContents(root);
						newRange.collapse(false);
						editor.getSelection().selectRanges([newRange]);
					} else {
						// Add an usable empty space after the link
						// Currently doesn't focus the cursor on that space
						const activeElement = $(range.endContainer.$.parentNode);
						activeElement.after('&nbsp;');
					}
				} else {
					try {
					const element = getSelectedElement(editor.getSelection(), 'eki-link');
					if (element.getName() === 'eki-link') {
						const native = $(element.$);
						native.replaceWith(native.text());
					}
					} catch(err) {
						console.log('err', err);
					}
				}
      }
    });
    editor.ui.addButton( 'removeEkilink', {
      label: 'Eki-link',
      command: 'removeEkilink',
      toolbar: 'removeEkilink'
    });
	}
});

class ckLink {

	constructor(editor){
		this.editor = editor;
		this.parent = $(this.editor.element.$).parents('.modal-content:first');
		this.parentTop = this.parent.css('top');
		this.parentHeight = this.parent.outerHeight();
		this.paths = {
			meaning: {
				api: `${applicationUrl}meaning_internal_link_search`,
				link: `${applicationBaseUrl}/lexsearch?id={{id}};`,
				title: 'Sisesta otsitav Tähendus/Mõiste',
			},
			word: {
				api: `${applicationUrl}word_internal_link_search`,
				link: `${applicationBaseUrl}/termsearch?id={{id}};`,
				title: 'Sisesta otsitav Keelend',
			},
		};
		this.activeID = false;
		this.valid = {
			external: true,
			internal: true,
		}
	}

	addTemplate() {
		const template = linkTemplate.replace('{{parentTop}}', this.parentTop);
		this.parent.after(this.linkContent = $(template));

		this.linkContent.css('marginBottom', (parseInt(this.parentTop) + this.parentHeight) - this.linkContent.outerHeight());

		this.outerLink = {
			title: this.linkContent.find('input[name="title"]:first'),
			url: this.linkContent.find('input[name="url"]:first'),
		}

		this.internalLink = {
			title: this.linkContent.find('input[name="internalTitle"]'),
		}

		this.outerLink.title.val(this.editor.getSelection().getSelectedText());
		this.internalLink.title.val(this.editor.getSelection().getSelectedText());

		this.roles = this.linkContent.find('[data-linkType]');
		this.internalTypes = this.linkContent.find('[data-internalType]');
		this.results = this.linkContent.find('.results');
		this.activeType = this.roles.eq(0).attr('data-linkType');
		this.internalSearchButton = this.linkContent.find('[data-role="internalSearchButton"]');
		this.changeInternalType('meaning');
		this.changeLayout('external');
	}

	toggle(state) {
		if (state === 'show') {
			this.addTemplate();
			//this.parent.hide();
			this.parent.addClass('size-zero');
		} else {
			this.linkContent.find('.formItem').removeClass('formItem--error');
			this.linkContent.find('input').val('');
			this.changeInternalType('meaning');
			this.changeLayout('external');
			this.linkContent.remove();
			//this.parent.show();
			this.parent.removeClass('size-zero');
		}
	}

	bindEvents() {
		console.log(this.linkContent);
		this.linkContent.find('[data-role="cancel"]').on('click', (e) => {
			e.preventDefault();
			this.toggle('hide');
		});
		
		this.linkContent.parents('.modal:first').on('click', (e) => {
			if ($(e.target).is('.modal')) {
				this.toggle('hide');
			}
		});
		this.linkContent.find('[data-role="save"]').on('click', (e) => {
			e.preventDefault();
			this.insertLink();
		});
		this.linkContent.find('[data-type]').on('click', (e) => {
			e.preventDefault();
			this.changeLayout($(e.currentTarget).attr('data-type'));
		});

		this.internalTypes.on('click', (e) => {
			e.preventDefault();
			this.changeInternalType($(e.currentTarget).attr('data-internalType'));
		});

		this.linkContent.find('[name="internalSearchValue"]').on('keypress', (e) => {
			const code = e.which || e.keyCode;
			if (code === 13) {
				e.preventDefault();
				this.getSearchResults(this.linkContent.find('[name="internalSearchValue"]').val());
			}
		});

		this.internalSearchButton.on('click', (e) => {
			e.preventDefault();
			this.getSearchResults(this.linkContent.find('[name="internalSearchValue"]').val());
		});
	}

	changeLayout(type) {
		this.activeType = type;
		const buttons = this.linkContent.find('[data-type]');
		buttons.removeClass('active');
		buttons.filter(`[data-type="${type}"]`).not('[data-internalType]').addClass('active');
		this.roles.hide().filter(`[data-linkType="${type}"]`).show();
		this.linkContent.find('.formItem').removeClass('formItem--error');
	}

	changeInternalType(internalType) {
		this.internalType = internalType;
		const buttons = this.linkContent.find('[data-internalType]');
		buttons.removeClass('active');
		buttons.filter(`[data-internalType="${internalType}"]`).addClass('active');
		this.linkContent.find('[data-role="title"]').html(this.paths[internalType].title);
		this.results.empty().hide();
		this.linkContent.find('.formItem').removeClass('formItem--error');
	}

	getSearchResults(value) {
		const data = {
			searchFilter: value,
		};
		this.results.show().html('<div class="loader"><i class="fa fa-3x fa-spinner fa-spin"></i></div>');
		$.ajax({
			url: this.paths[this.internalType].api,
			method: 'POST',
			data: JSON.stringify(data),
			contentType: 'application/json',
			success: (response) => {
				this.results.html(response).show();
				this.results.find('[name="details-btn"]').removeAttr('name');
				this.bindResults();
			}
		})
	}

	bindResults() {
		const buttons = this.results.find('.list-group-item');
		buttons.on('click', (e) => {
			const obj = $(e.currentTarget);
			const id = obj.find('[data-id]').attr('data-id');
			buttons.removeClass('active');
			obj.addClass('active');
			this.activeID = id;
		});
	}

	validateFields() {
		if (this.outerLink.url.val() === '') {
			this.outerLink.url.parents('.formItem:first').addClass('formItem--error');
			this.valid.external = false;
		} else {
			this.valid.external = true;
		}
		if (this.outerLink.title.val() === '') {
			this.outerLink.title.parents('.formItem:first').addClass('formItem--error');
			this.valid.external = false;
		} else {
			this.valid.external = true;
		}
		if (this.internalLink.title.val() === '') {
			this.internalLink.title.parents('.formItem:first').addClass('formItem--error');
			this.valid.internal = false;
		} else {
			this.valid.internal = true;
		}
		
	}

	insertLink() {
		this.validateFields();

		if (this.activeType === 'external') {
			if (!this.valid.external) {
				return false;
			}
			const content = CKEDITOR.dom.element.createFromHtml(`<eki-link href="${this.outerLink.url.val()}" target="ekiLinkTarget">${this.outerLink.title.val()}</eki-link>`);
			this.editor.insertElement(content);
			this.toggle('hide');
		} else {
			if (!this.valid.internal) {
				return false;
			}
			const content = CKEDITOR.dom.element.createFromHtml(`<eki-link id="${this.activeID}" link-type="${this.internalType}">${this.internalLink.title.val()}</eki-link>`);
			console.log(content);
			this.editor.insertElement(content);
			this.toggle('hide');
		}
		// Add a non-breaking space after the link
		this.editor.insertHtml('&nbsp;');
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
		extraPlugins: 'ekiStyles,ekiLink,removeEkilink',
		toolbarGroups: [
			{
				name: "eki-styles",
				groups: ["ekiStyles", 'ekiLink','removeEkilink'],
			},
			{
				name: 'eki-tools',
				groups: ['cleanup', 'undo'],
			},
			{ name: 'mode' },
		],
		extraAllowedContent: 'eki-link[*]',
		removeButtons: 'Underline,Strike,Subscript,Superscript,Anchor,Styles,Specialchar,Italic,Bold'
	});

	

}

const linkTemplate = /*html*/`
<div class="modal-content ekilinkEditor" style="top:{{parentTop}};">
	<div class="modal-header">
		<button class="btn btn-secondary" data-role="cancel">Tagasi</button>
		<button type="button" class="close" aria-label="Close" data-role="close" data-dismiss="modal">
			<span aria-hidden="true">×</span>
		</button>
	</div><!--/modal-header-->
	<div class="modal-body">
		<div class="row">
			<div class="col-4">
				<h3>Vali lingi tüüp</h3>
				<div class="editorLink" data-type="external">Välislink</div>
				<div class="editorLink editorLink--nested">
					<div class="editorLink__parent">Siselink</div>
					<div class="editorLink__child" data-type="internal" data-internalType="meaning">Tähendus/Mõiste</div>
					<div class="editorLink__child" data-type="internal" data-internalType="word">Keelend/Termin</div>
				</div><!--/editorLink-->
			</div><!--/col-4-->
			<div class="col-8 position-relative">
				<div data-linkType="external">
					<div class="formItem">
						<div class="formItem--title">
							Valitud Tekst
						</div><!--/formItem--title-->
						<input type="text" name="title" />
						<div class="formItem__error">Välja täitmine kohustuslik!</div>
					</div><!--/formItem-->
					<div class="formItem">
						<div class="formItem--title">
							Lisa link
						</div><!--/formItem--title-->
						<input type="text" name="url" />
						<div class="formItem__error">Välja täitmine kohustuslik!</div>
					</div><!--/formItem-->
				</div><!--/div-->

				<div data-linkType="internal" style="display: none;">
					<div class="formItem">
						<div class="formItem--title">
							Valitud Tekst
						</div><!--/formItem--title-->
						<input type="text" name="internalTitle" />
						<div class="formItem__error">Välja täitmine kohustuslik!</div>
					</div><!--/formItem-->

					<div class="formItem">
						<div class="formItem--title" data-role="title">
							Sisesta otsitav Keelend
						</div><!--/formItem--title-->
						<div class="formItem--dual">
							<input type="text" name="internalSearchValue" />
							<button class="btn btn-primary" data-role="internalSearchButton">Otsi</button>
						</div><!--/formItem--dual-->
						<div class="formItem__error">Siselingi valimine kohustuslik!</div>
					</div><!--/formItem-->
					<div class="results">
					</div><!--/results-->
				</div><!--/div-->

			</div><!--/col-8-->
		</div><!--/row-->
	</div><!--/modal-body-->
	<div class="modal-footer">
		<button type="button" class="btn btn-default" data-dismiss="modal" data-role="cancel">Katkesta</button>
		<button type="submit" class="btn btn-primary" data-role="save">Valmis</button>
	</div>
</div>
`