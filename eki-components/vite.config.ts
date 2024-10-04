import { resolve } from "path";
import { defineConfig } from "vite";
import { svelte } from "@sveltejs/vite-plugin-svelte";

// https://vitejs.dev/config/
export default defineConfig({
  plugins: [
    svelte({
      compilerOptions: {
        customElement: true,
      },
    }),
  ],
  build: {
    lib: {
      entry: resolve(__dirname, "src/library.ts"),
      name: "eki-components",
      fileName: (format) => `eki-components.${format}.js`,
      formats: ["es"],
    },
    outDir: "eki-components",
  },
});
