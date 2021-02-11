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
  {
		id: 'asdfdsf',
		label: 'Hardi margendus',
		command: 'asdfdsf',
		element: 'eki-hardi',
		styles: 'color: green; font-size: 21px;',
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

CKEDITOR.plugins.add('ekiLink', {
	icons: icons,
	hidpi: true,
	init: function( editor ) {
		editor.addCommand( 'insertTimestamp', {
      exec: function( editor ) {
        var now = new Date();
        editor.insertHtml( 'The current date and time is: <em>' + now.toString() + '</em>' );
      }
    });
    editor.ui.addButton( 'Timestamp', {
      label: 'Insert Timestamp',
      command: 'insertTimestamp',
      toolbar: 'ekiLink'
    });
	}
});


