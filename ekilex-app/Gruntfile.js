module.exports = function (grunt) {
  const browserList = ["opera >= 27", "ff >= 45", "chrome >= 45"];

  var concatFile = "src/main/resources/view/js/main.js";
  grunt.task.registerTask(
    "configureBabel",
    "configures babel options",
    function () {
      config.babel.options.inputSourceMap = grunt.file.readJSON(
        concatFile + ".map",
      );
    },
  );

  var config = {
    clean: {
      html: ["target/classes/view/html"],
      css: ["target/classes/view/css"],
      js: ["target/classes/view/js"],
      assets: ["target/classes/view/"],
    },

    copy: {
      html: {
        expand: true,
        cwd: "src/main/resources/view/html/",
        src: "**",
        dest: "target/classes/view/html/",
      },
      css: {
        expand: true,
        cwd: "src/main/resources/view/css/",
        src: "**",
        dest: "target/classes/view/css/",
      },
      js: {
        expand: true,
        cwd: "src/main/resources/view/js/",
        src: "**",
        dest: "target/classes/view/js/",
      },
      assets: {
        expand: true,
        cwd: "src/main/resources/view/",
        src: "**",
        dest: "target/classes/view/",
      },
    },

    autoprefixer: {
      options: {
        browsers: browserList,
      },
      default_css: {
        src: "src/main/resources/view/css/styles.css",
        dest: "src/main/resources/view/css/styles.css",
      },
    },

    concat: {
      components: {
        options: {
          sourceMap: true,
        },
        src: [
          "src/main/resources/view/js/*.js",
          "!src/main/resources/view/js/jquery-3.7.1.min.js",
          "!src/main/resources/view/js/main.js",
        ],
        dest: concatFile,
      },
    },

    sass: {
      dist: {
        options: {
          style: "compressed",
        },
        files: {
          "src/main/resources/view/css/styles.css":
            "src/main/resources/scss/styles.scss",
        },
      },
    },

    babel: {
      options: {
        presets: [
          [
            "@babel/preset-env",
            {
              targets: {
                esmodules: true,
              },
              useBuiltIns: false,
              modules: false,
              exclude: ["@babel/plugin-transform-regenerator"],
            },
          ],
        ],
        plugins: ["@babel/plugin-proposal-class-properties"],
        comments: false,
        compact: true,
        sourceMap: true,
        inputSourceMap: grunt.file.readJSON(
          "src/main/resources/view/js/main.js.map",
        ),
      },
      dist: {
        files: [
          {
            src: [concatFile],
            dest: "src/main/resources/view/js/main.js",
          },
        ],
      },
    },
    watch: {
      css: {
        files: [
          "src/main/resources/scss/*.scss",
          "src/main/resources/scss/**/*.scss",
        ],
        tasks: ["sass", "autoprefixer", "clean:css", "copy:css"],
      },
      js: {
        files: [
          "src/main/resources/view/js/**/**.js",
          "!src/main/resources/view/js/main.js",
        ],
        tasks: ["concat", "configureBabel", "babel", "clean:js", "copy:js"],
      },
      html: {
        files: ["src/main/resources/view/html/*.html"],
        tasks: ["clean:html", "copy:html"],
      },
      options: {
        spawn: false,
        interrupt: true,
      },
    },
    browserSync: {
      dev: {
        bsFiles: {
          src: [
            "target/classes/view/css/**/*.css",
            "target/classes/view/js/**/*.js",
            "target/classes/view/html/**/*.html",
          ],
        },
        options: {
          watchTask: true,
          proxy: "localhost:5555",
          port: 3000,
          notify: false,
          open: false,
          reloadDelay: 300,
        },
      },
    },
  };

  const defaultTasks = [
    "clean:assets",
    "concat",
    "configureBabel",
    "sass",
    "autoprefixer",
    "babel",
    "copy:assets",
    "browserSync:dev",
    "watch",
  ];
  const deployTasks = [
    "clean:assets",
    "concat",
    "configureBabel",
    "sass",
    "autoprefixer",
    "babel",
    "copy:assets",
  ];

  grunt.initConfig(config);
  require("load-grunt-tasks")(grunt);

  grunt.registerTask("default", defaultTasks);
  grunt.registerTask("deploy", deployTasks);
};
