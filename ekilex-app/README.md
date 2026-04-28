# EKILEX App front end development
First make sure you have the following installed on your computer
* [Node.js](https://nodejs.org/en/) max version 20 (grunt will probably fail to resolve sass otherwise)
* [grunt-cli] npm install -g grunt-cli

If you have node and npm installed, then
* using you command line tool (Terminal on OSX or CMD on Windows) navigate to the folder `ekilex-app` 
* enter the command `npm install` to command line

This should install all the necessary tools for front end development.
If the install was successful then run the command

`grunt`

Now you can edit all your `.scss` files and `.html` files and they will be automatically copied to the target folder.
**Note:** current setup does not handle adding new files. If for example you add a new `.html` file then you have to restart the `grunt` command. 
You do this by first entering `Ctr+C` to your command line and then calling `grunt` again. Now grunt-cli will see the new files

## Running developer server
* Start a tunnel between test database and your computer `ssh username@ekitest.tripledev.ee -L 5433:127.0.0.1:5432 -N -p {portHere}` - ask developer for ssh port and run it at the root folder of the project. If there is no output from the command then it means everything is working. Otherwise it will output errors.
* Install Maven application (at root folder) - `mvn clean install -D skipTests`
* Add application-dev.properties file to src/main/resources with this content and tweak as necessary:
```
spring.datasource.main.url=jdbc:postgresql://localhost:5433/ekilex
spring.datasource.arch.url=jdbc:postgresql://localhost:5433/archive
server.servlet.context-path=/
```  
* Start Ekilex server (at ekilex-app folder) - `mvn spring-boot:run -D spring-boot.run.profiles=dev`

## Styles
Styles are written in .scss files and later compiled into a single .css file, which should not be manually modified.
There are also quill.snow.css (WYSIWYG editor styles) and jquery-ui.min.css files
The system is used via desktops with probably quite large monitors, so mobile view is not a requirement here, but if you can, include it
### TODO
* Get rid of excess color shades, can be found in the _variables.scss file
* Replace all legacy icons with material symbols
### DO
* Use css variables for colors etc, do not use scss variables and especially avoid hardcoded colors (edge case okay like a specific box-shadow, though if designer uses a box-shadow that has a name, you can probably add it to variables)
* Use BEM naming convention when creating new pages / components
### Theming Bootstrap
We use **npm** to add Bootstrap to our project. And we use **.scss** to write styles.
Bootstrap is imported to our styles in the `src/main/resources/scss/styles.scss` file.
We use scss variables to override bootstrap default values. Variables are located in the` _variables.scss` partial.
Read more here [https://getbootstrap.com/docs/4.3/getting-started/theming/](https://getbootstrap.com/docs/4.3/getting-started/theming/)

## Js
Js logic is spread out across different files with the intention to group them by domain/functionality, but some files like *search etc could contain a lot of logic
Js files get bundled into one main.js file by grunt
Some js files are downloaded versions of 3rd party libs to avoid relying on a CDN
Code is written in both plain js and jQuery
You will need to use jQuery when interacting with Bootstrap, like modal events etc
Logic usually gets initiated by wpm.js by defining a jQuery plugin and then using `data-plugin={pluginName}` in the template

### TODO
* Switch to dialog element to reduce dependency on Bootstrap
* Swap out grunt, probably for something that supports sourcemaps

### eki-components
Common lib used by both ekilex-app and wordweb-app, currently only contains a toast web component
#### TODO
Figure out if it's worth keeping, if new real reusable components show up then there's a viable base there to create web components with

## Js guidelines
### Try to
* keep your logic generic where possible, e.g. if making a popup, do it in a way that it potentially be reused (this means relying on generic data attributes or other ways of identifying elements instead of specific id's etc)
* prioritize using plain js to avoid increasing jQuery code
* keep aria attributes and general accessibility in mind (though not a big focus as this is an internal system)
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
