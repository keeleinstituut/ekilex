import stylesImport from "../app.css?inline";
const sheet = new CSSStyleSheet();
sheet.replaceSync(stylesImport);

export const ekiComponentBase = (
  customElementConstructor: CustomElementConstructor
) =>
  class extends customElementConstructor {
    component = this;
    connectedCallback() {
      super.connectedCallback();
      // Add these shared styles to component's shadow dom
      if (this.shadowRoot?.adoptedStyleSheets && sheet) {
        this.shadowRoot.adoptedStyleSheets = [sheet];
      }
    }
  };
