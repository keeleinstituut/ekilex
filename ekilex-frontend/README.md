# Requirements
Nodejs 18+  
NPM (included with Nodejs install)  
Either an .env* file containing API_URL or that same variable already defined in your environment  
For example you can have a file called ```.env.local``` containing ```API_URL=http://localhost:5555```

## Dev setup
1. Start ekilex-app by following the instructions in ekilex-app/README.md
2. Open up a new terminal and move into the ekilex-frontend folder
3. Run npm i
4. Run npm run dev

## Running prod build (assumes ekilex-app is already running)
1. Move into the ekilex-frontend folder in your terminal/script
2. Either add an .env* file containing ```API_URL``` or add the variable into your environment, the variable should point to the url that ekilex-app is served from
3. Run npm ci
4. Run npm build
5. Run npm run start


## Technologies
This project uses Next.js and React along with Tailwind