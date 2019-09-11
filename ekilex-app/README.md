# EKILEX App front end development
First make sure you have the following installed on your computer
* [Node.js](https://nodejs.org/en/)
* [npm.js](https://www.npmjs.com/)

If you have node and npm installed, then
* using you command line tool (Terminal on OSX or CMD on Windows) navigate to the folder `ekilex-app` 
* enter the command `npm install` to command line

This should install all the necessary tools for front end development.
If the install was successful then run the command

`npm run watch`

Now you can edit all your `.scss` files and `.html` files and they will be automatically copied to the target folder.
**Note:** current setup does not handle adding new files. If for example you add a new `.html` file then you have to restart the `watch` command. 
You do this by first entering `Ctr+C` to your command line and then calling `npm run watch` again. Now nodemonitor will see the new files

## About package.json
package.json holds all the scripts needed for front end development
* **clean-and-copy-html** – as the name suggests, it runs the clean html and copy-html command
* **clean-fonts**, **clean-html**, **clean-img** – prepare folder for new files by first deleting it. This way if a file does not exist in src folder then it won't be in the target folder. Uses **clean-dir** package
* **copy-html**, **copy-img**, **copy-fonts** – Copy html, img or font files to  the target folder so that the Spring server could serve them. Uses **ncp** package
* **css** – command for calling all the .scss commands in sequence
* **css-compile** – compiles **.scss** in to **.css**. Takes one .scss file as input and compiles it directly to the target folder. Uses **node-sass** package
* **css-stylelint** – lints **.scss** files for code style errors. It uses Bootstrap style lint settings. Settings for style lint are in the `.stylelintrc` file
* **css-prefix** - automatically adds all vendor prefixes to css. Uses **postcss** package and **autoprefixer** plugin. It also uses settings that are located in `build/postcss.config.js`. This uses the css file in the target folder as input
* **watch** – starts nodemonitor for **.scss** and **.html** files. If any file is updated then it executes a specified command.
* **watch-css**, **watch-html** – scripts for monitoring folders and files for changes and then executing another script. Used in **watch** script

You can run all these scripts one by one. For example `npm run css-compile` would just run the compile command and create a new .css file in the target folder

## Theming Bootstrap
We use **npm** to add Bootstrap to our project. And we use **.scss** to write styles.
Bootstrap is imported to our styles in the `src/main/resources/view/styles.scss` file.
We use scss variables to override bootstrap default values. Variables are located in the` _variables.scss` partial. 

Read more here [https://getbootstrap.com/docs/4.3/getting-started/theming/](https://getbootstrap.com/docs/4.3/getting-started/theming/)
