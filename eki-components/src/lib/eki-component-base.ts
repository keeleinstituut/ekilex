import stylesImport from "../app.css?inline";
const sheet = new CSSStyleSheet();
sheet.replaceSync(stylesImport);

type CustomElementCtor = new () => HTMLElement & { connectedCallback?(): void };

export const ekiComponentBase = (
  customElementConstructor: CustomElementCtor
) =>
  class extends customElementConstructor {
    connectedCallback() {
      super.connectedCallback?.();
      if (this.shadowRoot?.adoptedStyleSheets && sheet) {
        this.shadowRoot.adoptedStyleSheets = [sheet];
      }
    }
  };
