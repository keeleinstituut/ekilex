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
      if (!globalThis?.ekiStylesheet) {
        // Instantiating the styles component is enough to provide styles to the window
        document.createElement("eki-styles");
      }
      // Get reference to shared styles
      const sheet = globalThis?.ekiStylesheet;
      // Add these shared styles to component's shadow dom
      if (this.shadowRoot?.adoptedStyleSheets && sheet) {
        this.shadowRoot.adoptedStyleSheets = [sheet];
      }
    }
  };
