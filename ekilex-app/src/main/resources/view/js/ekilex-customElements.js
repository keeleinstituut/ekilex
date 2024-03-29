class ekiMedia extends HTMLElement {
	static get observedAttributes() {
		return ['src', 'alt'];
	}

	constructor() {
		super();
		const shadowRoot = this.attachShadow({mode: 'closed'});

		this.img = document.createElement('img');

		const style = document.createElement('style');
		// :host refers to the eki-media element
		style.textContent = `
		:host {
			cursor: default;
			display: inline-block;
			height: 20px;
			max-height: 50px;
		}

		:host(.eki-selected) img {
			outline: solid 1px blue;
		}

		img {
			width: 100%;
			height: 100%;
			max-height: inherit;
			object-fit: cover;
		}
		`;

		shadowRoot.appendChild(style);
		shadowRoot.appendChild(this.img);
	}

	attributeChangedCallback(name, oldValue, newValue) {
		// Only reassign if the value actually changed
		if (oldValue !== newValue) {
			this.img[name] = newValue;
		}
	}
}

customElements.define('eki-media', ekiMedia);

// CKEditor content is shown inside an iframe when the editor is open,
// which means custom elements defined on the main page are not available and need to be defined inside the iframe
function registerEkiMedia(document) {
	const head = document.find('head').first();
	// Only define the new element if it doesn't already exist
	const script = `
	<script>
	if (!customElements.get('eki-media')) {
		class ekiMedia extends HTMLElement {
			static get observedAttributes() {
				return ['src', 'alt'];
			}
		
			constructor() {
				super();
				const shadowRoot = this.attachShadow({mode: 'closed'});
		
				this.img = document.createElement('img');
		
				const style = document.createElement('style');
				// :host refers to the eki-media element
				style.textContent = \`
				:host {
					cursor: default;
					display: inline-block;
					height: 20px;
					max-height: 50px;
				}
		
				:host(.eki-selected) img {
					outline: solid 1px blue;
				}
		
				img {
					width: 100%;
					height: 100%;
					max-height: inherit;
					object-fit: cover;
				}
				\`;
		
				shadowRoot.appendChild(style);
				shadowRoot.appendChild(this.img);
			}
		
			attributeChangedCallback(name, oldValue, newValue) {
				// Only reassign if the value actually changed
				if (oldValue !== newValue) {
					this.img[name] = newValue;
				}
			}
		}
		
		customElements.define('eki-media', ekiMedia);
	}
	<\/script>`;
	// Add the new script tag to the head of the iframe
	head.append(script);
}