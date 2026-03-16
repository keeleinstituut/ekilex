# EKILEX App front end development

First make sure you have the following installed on your computer

- [Node.js](https://nodejs.org/en/)
- [npm.js](https://www.npmjs.com/)
  NB! If you install Node then you get npm automatically. No need to install npm separately.

If you have node and npm installed, then

- using you command line tool (Terminal on OSX or CMD on Windows) navigate to the folder `ekilex-app`
- enter the command `npm install` to command line

This should install all the necessary tools for front end development.
If the install was successful then run the command

`npm run watch`

Now you can edit all your `.scss` files and `.html` files and they will be automatically copied to the target folder.
**Note:** current setup does not handle adding new files. If for example you add a new `.html` file then you have to restart the `watch` command.
You do this by first entering `Ctr+C` to your command line and then calling `npm run watch` again. Now nodemonitor will see the new files

## About package.json

package.json holds all the scripts needed for front end development

- **build** - compiles styles, copies over html and js files to java resources
- **watch** – uses nodemon to re-build styles and copy over files on changes

## Theming Bootstrap

We use **npm** to add Bootstrap to our project. And we use **.scss** to write styles.
Bootstrap is imported to our styles in the `src/main/resources/scss/styles.scss` file.
We use scss variables to override bootstrap default values. Variables are located in the` _variables.scss` partial.

Read more here [https://getbootstrap.com/docs/4.3/getting-started/theming/](https://getbootstrap.com/docs/4.3/getting-started/theming/)

##Writing new styles
If you want to add new styles then you should do your work in the `src/main/resources/scss/` folder. All styles should be in `.scss` files and imported in to the `styles.scss` file.
**styles.scss** is then compiled in to `styles.css` which is then used in production. No changes should be made to **.css** files as they will be lost when generating styles from **.scss**
