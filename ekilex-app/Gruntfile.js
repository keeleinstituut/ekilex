module.exports = function (grunt) {

	grunt.loadNpmTasks('grunt-contrib-watch');
	grunt.loadNpmTasks('grunt-contrib-concat');
	grunt.loadNpmTasks('grunt-contrib-connect');
	grunt.loadNpmTasks('grunt-px-to-rem');
	grunt.loadNpmTasks('grunt-autoprefixer');
	grunt.loadNpmTasks('grunt-contrib-sass');
	grunt.loadNpmTasks('grunt-babel');
	grunt.loadNpmTasks('grunt-contrib-clean');
	grunt.loadNpmTasks('grunt-contrib-copy');

	const browserList = ['opera >= 27', 'ff >= 45', 'chrome >= 45', 'ie >= 11'];

	grunt.initConfig({

		clean: {
			html: ['target/classes/view/html'],
			css: ['target/classes/view/css'],
			js: ['target/classes/view/js'],
			assets: ['target/classes/view/']
		},

		copy: {
			html: {
				expand: true,
				cwd: 'src/main/resources/view/html/',
				src: '**',
				dest: 'target/classes/view/html/',
			},
			css: {
				expand: true,
				cwd: 'src/main/resources/view/css/',
				src: '**',
				dest: 'target/classes/view/css/',
			},
			js: {
				expand: true,
				cwd: 'src/main/resources/view/js/',
				src: '**',
				dest: 'target/classes/view/js/',
			},
			assets: {
				expand: true,
				cwd: 'src/main/resources/view/',
				src: '**',
				dest: 'target/classes/view/',
			},
		},

		autoprefixer: {
			options: {	
				browsers: browserList,
			},
			default_css: {
				src: "src/main/resources/view/css/styles.css",
				dest: "src/main/resources/view/css/styles.css"
			},
		},

		px_to_rem: {
			dist: {
				options: {
					base: 16,
					fallback: false,
					fallback_existing_rem: false,
					ignore: [],
					map: false
				},
				files: {
					'src/main/resources/view/css/styles.css': ['src/main/resources/view/css/styles.css']
				}
			}
		},

		concat: {
			components: {
				src: [
					'src/main/resources/view/js/*.js',
					'!src/main/resources/view/js/_jquery-3.2.1.js',
					'!src/main/resources/view/js/main.js',
					
				],
				dest: 'src/main/resources/view/js/main.js'
			},
		},

		sass: {
			dist: {
				options: {
					style: 'compressed'
				},
				files: {
					'src/main/resources/view/css/styles.css': 'src/main/resources/scss/styles.scss'
				}
			}
		},

		babel: {
			options: {
				presets: ['@babel/preset-env'],
				comments: false,
				compact: true,
			},
			dist: {
				files: {
					'src/main/resources/view/js/main.js': 'src/main/resources/view/js/main.js',
				}
			}
		},
		watch: {
			css: {
				files: ["src/main/resources/scss/*.scss"],
				tasks: ['sass', 'px_to_rem', 'autoprefixer', 'clean:css', 'copy:css']
			},
			js: {
				files: ['src/main/resources/view/js/**/**.js', '!src/main/resources/view/js/main.js'],
				tasks: ['concat', 'babel', 'clean:js', 'copy:js'],
				options: {
					nospawn: true
				}
			},
			html: {
				files: ['src/main/resources/view/html/*.html'],
				tasks: ['clean:html', 'copy:html'],
				options: {
					nospawn: true
				}
			}
		}

	});

	var tasks = {
		'clean:assets': true,
		concat: true,
		sass: true,
		px_to_rem: true,
		autoprefixer: true,
		babel: true,
		'copy:assets': true,
		watch: true
	};

	var tasksArray = new Array();

	for (var i in tasks) {
		if (tasks[i]) {
			tasksArray.push(i);
		}
	}

	var deployTasks = { ...tasks };
	delete deployTasks.watch;

	var deployTasksArray = new Array();

	for (var i in deployTasks) {
		if (deployTasks[i]) {
			deployTasksArray.push(i);
		}
	}


	grunt.registerTask('default', tasksArray);
	grunt.registerTask('deploy', deployTasksArray);

};