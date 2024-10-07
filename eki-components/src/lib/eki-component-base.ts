export const ekiComponentBase = (
  customElementConstructor: CustomElementConstructor
) =>
  class extends customElementConstructor {
    component = this;
    constructor() {
      super();
    }
    connectedCallback() {
      super.connectedCallback();
      if (globalThis?.window) {
        // Get reference to shared styles
        const sheet = (window as any).ekiStylesheet;
        // Add these shared styles to component's shadow dom
        if (this.shadowRoot?.adoptedStyleSheets && sheet) {
          this.shadowRoot.adoptedStyleSheets = [sheet];
        }
      }
    }
  };
