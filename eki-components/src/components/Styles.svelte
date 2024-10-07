<svelte:options
  customElement={{
    tag: "eki-styles",
    extend: (customElementConstructor) =>
      class extends customElementConstructor {
        constructor() {
          super();
        }
        connectedCallback() {
          super.connectedCallback();
          const sheet = new CSSStyleSheet();
          sheet.replaceSync(stylesImport);
          // Set our tailwind styles as a window var so other components can gain access to it
          if (globalThis?.window) {
            window.ekiStylesheet = sheet;
          }
        }
      },
  }}
/>

<script>
  import stylesImport from "../app.css?inline";
</script>
