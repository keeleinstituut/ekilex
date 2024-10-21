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
      fileName: "eki-components",
      formats: ["es"],
    },
    outDir: "eki-components",
    minify: true,
    cssMinify: true,
    emptyOutDir: true,
    copyPublicDir: false,
  },
});
