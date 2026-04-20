# WordWeb App front end development

First make sure you have the following installed on your computer

- [Node.js](https://nodejs.org/en/)
- [npm.js](https://www.npmjs.com/)
  NB! If you install Node then you get npm automatically. No need to install npm separately.

If you have node and npm installed, then

- navigate to the folder `wordweb-app` in your terminal
- run `npm install`

## Running developer server
* Start a tunnel between test database and your computer `ssh username@ekitest.tripledev.ee -L 5433:127.0.0.1:5432 -N` - run it at the root folder of the project. If there is no output from the command then it means everything is working. Otherwise it will output errors.
* Install Maven application (at root folder) - `mvn clean install -D skipTests`
* Add application-dev.properties file to src/main/resources with this content and tweak as necessary:
```
spring.datasource.username=wordweb
spring.datasource.url=jdbc:postgresql://localhost:5433/wordweb
server.port=5577
```  
* Start WordWeb server (in wordweb-app folder) - `mvn spring-boot:run -D spring-boot.run.profiles=dev`

## Scripts
- **build** - compiles styles, copies over html and js files to java resources, uses Esbuild
- **watch** – uses nodemon to re-build styles and copy over files on changes

## Styles
Styles are written in .scss files and later compiled into a single .css file, which should not be manually modified.
### TODO
* get rid of excess color shades, can be found in the _variables.scss file
* replace all legacy icons with material symbols
### DO
* use css variables for colors etc, do not use scss variables and especially avoid hardcoded colors (edge case okay like a specific box-shadow, though if designer uses a box-shadow that has a name, you can probably add it to variables)
* use BEM naming convention when creating new pages / components
* keep mobile view in mind
### Theming Bootstrap
We use **npm** to add Bootstrap to our project. And we use **.scss** to write styles.
Bootstrap is imported to our styles in the `src/main/resources/scss/styles.scss` file.
We use scss variables to override bootstrap default values. Variables are located in the` _variables.scss` partial.
Read more here [https://getbootstrap.com/docs/4.3/getting-started/theming/](https://getbootstrap.com/docs/4.3/getting-started/theming/)

## Js
Js logic is spread out across different files with the intention to group them by domain/functionality, but common files also contain a lot of logic
Js files are imported where needed and do not get bundled into one
Some js files are downloaded versions of 3rd party libs to avoid relying on a CDN
Code is written in both plain js and jQuery
You will need to use jQuery when interacting with Bootstrap, like modal events etc

### TODO
* Switch to dialog element to reduce dependency on Bootstrap

### eki-components
Common lib used by both ekilex-app and wordweb-app, currently only contains a toast web component

## Js guidelines
### Try to
* keep your logic generic where possible, if making a popup or something, do it in a way that it could be reused
* prioritize using plain js to avoid increasing jQuery code
* keep aria attributes and general accessibility in mind when creating functionality
* refactor legacy code if your task requires interacting with it anyways
* handle failure cases like target element not found etc
* avoid using global scope, add event listeners where they're needed etc

### DO
* use const and let instead of var
* keep names descriptive e.g.
  ```diff 
  -const m = document.getElementById('menu')
  +const menu = document.getElementById('menu')
  ```
* use classes to group if your functionality needs multiple functions


## Thymeleaf
### DO
* create reusable templates for stuff that gets reused a lot (though keep in mind thymeleaf will resolve the template for each call, which could cause slowdown)
