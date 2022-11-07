# EKILEX App front end development
First make sure you have the following installed on your computer
* [Node.js](https://nodejs.org/en/)
* [npm.js](https://www.npmjs.com/)
* [grunt-cli] npm install -g grunt-cli
NB! If you install Node then you get npm automatically. No need to install npm separately.

If you have node and npm installed, then
* using you command line tool (Terminal on OSX or CMD on Windows) navigate to the folder `ekilex-app` 
* enter the command `npm install` to command line

This should install all the necessary tools for front end development.
If the install was successful then run the command

`grunt`

Now you can edit all your `.scss` files and `.html` files and they will be automatically copied to the target folder.
**Note:** current setup does not handle adding new files. If for example you add a new `.html` file then you have to restart the `grunt` command. 
You do this by first entering `Ctr+C` to your command line and then calling `grunt` again. Now grunt-cli will see the new files

## Theming Bootstrap
We use **npm** to add Bootstrap to our project. And we use **.scss** to write styles.
Bootstrap is imported to our styles in the `src/main/resources/scss/styles.scss` file.
We use scss variables to override bootstrap default values. Variables are located in the` _variables.scss` partial. 

Read more here [https://getbootstrap.com/docs/4.3/getting-started/theming/](https://getbootstrap.com/docs/4.3/getting-started/theming/)

## Writing new styles
If you want to add new styles then you should do your work in the `src/main/resources/scss/` folder. All styles should be in `.scss` files and imported in to the `styles.scss` file.
**styles.scss** is then compiled in to `styles.css` which is then used in production. No changes should be made to **.css** files as they will be lost when generating styles from **.scss**

## Running developer server
* Start a tunnel between test database and your computer `ssh username@ekitest.tripledev.ee -L 5433:127.0.0.1:5432 -N` - run it at the root folder of the project. If there is no output from the command then it means everything is working. Otherwise it will output errors.
* Install Maven application (at root folder) - `mvn clean install -D skipTests`
* Start Ekilex server (at ekilex-app folder) - `mvn spring-boot:run -D spring-boot.run.profiles=dev`