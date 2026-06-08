import esbuild from "esbuild";
import { sassPlugin } from "esbuild-sass-plugin";
import fs from "fs";
import path from "path";

const directories = {
  ".html": ["src/main/resources/view/html", "target/classes/view/html"],
  ".js": ["src/main/resources/view/js", "target/classes/view/js"],
  ".css": ["src/main/resources/view/css", "target/classes/view/css"],
};

const copyHtmlAndJsPlugin = () => ({
  name: "copy-html-and-js-plugin",
  setup(
    /**
     * @type {import('esbuild').PluginBuild} build
     */ build
  ) {
    build.onEnd(async () => {
      Object.entries(directories).forEach(
        ([fileExtension, [sourceDir, targetDir]]) => {
          // Clean target directory of the files that will be copied
          if (fs.existsSync(targetDir)) {
            fs.readdirSync(targetDir).forEach((file) => {
              if (file.endsWith(fileExtension)) {
                fs.unlinkSync(path.join(targetDir, file));
              }
            });
          }
          // Create target directory if it does not exist
          fs.mkdirSync(targetDir, { recursive: true });

          fs.readdirSync(sourceDir).forEach((file) => {
            if (file.endsWith(fileExtension)) {
              const srcFile = path.join(sourceDir, file);
              const destFile = path.join(targetDir, file);
              fs.cpSync(srcFile, destFile);
            }
          });
        }
      );
      console.log("✅ Build complete!");
    });
  },
});

/**
 * @type {import('esbuild').BuildOptions}
 */
const buildOptions = {
  entryPoints: ["src/main/resources/scss/styles.scss"],
  outfile: "src/main/resources/view/css/styles.css",
  bundle: true,
  // These files are automatically copied over by java anyways
  external: [
    "*.png",
    "*.svg",
    "*.ttf",
    "*?z7bx2c",
    "*?z7bx2c#material-icons",
    "*.woff",
    "*#OpenSans",
    "*.woff2",
    "*#iefix",
    "*.eot",
    "*#fontawesome",
  ],
  plugins: [
    sassPlugin({
      silenceDeprecations: [
        "import",
        "slash-div",
        "color-functions",
        "global-builtin",
        "mixed-decls",
      ],
    }),
    copyHtmlAndJsPlugin(),
  ],
};

const isWatch = process.argv.includes("--watch");

if (isWatch) {
  const context = await esbuild.context(buildOptions);
  await context.watch();
  console.log("👀 Watching for changes...");

  // esbuild's watcher only tracks files in the build graph (the SCSS entry and
  // its imports). The HTML and JS assets are copied by the plugin above rather
  // than bundled, so they fall outside that graph — watch their source
  // directories directly and trigger a rebuild (which re-runs the copy) on change.
  let rebuildTimer;
  const scheduleRebuild = () => {
    clearTimeout(rebuildTimer);
    rebuildTimer = setTimeout(() => {
      context.rebuild().catch((error) => console.error(error));
    }, 100);
  };
  [directories[".html"][0], directories[".js"][0]].forEach((sourceDir) => {
    fs.watch(sourceDir, { recursive: true }, scheduleRebuild);
  });
} else {
  await esbuild.build(buildOptions);
}
